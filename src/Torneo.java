import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Torneo {
    private List<Jugador> jugadores;
    private List<Partido> partidos;
    private List<Partido> rondaActual;
    private List<Jugador> ultimosGanadoresRonda;
    private ConexionBD conexionBD;

    public Torneo() {
        conexionBD = new ConexionBD();
        jugadores = new ArrayList<>();
        partidos = new ArrayList<>();
        rondaActual = new ArrayList<>();
        ultimosGanadoresRonda = new ArrayList<>();
        cargarDatosIniciales();
    }

    public List<Partido> getRondaActual() {
        return rondaActual;
    }

    private void cargarDatosIniciales() {
        List<Jugador> jugadoresCargados = conexionBD.cargarJugadores();
        this.jugadores.addAll(jugadoresCargados);

        List<Partido> partidosCargados = conexionBD.cargarPartidos(this.jugadores);
        this.partidos.addAll(partidosCargados);

        if (!this.partidos.isEmpty()) {
            List<Partido> partidosPendientes = this.partidos.stream()
                    .filter(p -> !p.estaJugado())
                    .collect(Collectors.toList());

            if (!partidosPendientes.isEmpty()) {
                this.rondaActual = partidosPendientes;
                System.out.println("Torneo reanudado con " + rondaActual.size() + " partidos pendientes en la ronda actual.");
            } else {
                List<Partido> ultimosPartidosJugados = this.partidos.stream()
                        .filter(Partido::estaJugado)
                        .collect(Collectors.toList());

                if (!ultimosPartidosJugados.isEmpty()) {
                    this.ultimosGanadoresRonda = ultimosPartidosJugados.stream()
                            .map(Partido::getGanador)
                            .distinct()
                            .filter(j -> j != null && !j.getNombre().equals("BYE"))
                            .collect(Collectors.toList());
                }

                if (this.ultimosGanadoresRonda.size() == 1) {
                    System.out.println("El torneo ya tiene un ganador registrado: " + this.ultimosGanadoresRonda.get(0).getNombre());
                } else if (this.ultimosGanadoresRonda.size() > 1 && this.ultimosGanadoresRonda.size() % 2 == 0) {
                    System.out.println("Todos los partidos cargados estan jugados. Hay " + this.ultimosGanadoresRonda.size() + " ganadores de la ultima ronda. Genere el fixture para una nueva ronda si es necesario.");
                } else if (this.ultimosGanadoresRonda.size() > 1 && this.ultimosGanadoresRonda.size() % 2 != 0) {
                    System.out.println("El torneo parece haber terminado una ronda, pero hay un numero impar de ganadores. Genere el fixture si es necesario.");
                } else {
                    System.out.println("Todos los partidos cargados estan jugados. Si el torneo no ha finalizado, genere el fixture para una nueva ronda.");
                }
            }
        } else {
            System.out.println("No hay datos de torneos previos cargados. Comience registrando jugadores.");
        }
    }

    public void agregarJugador(Jugador jugador) {
        boolean existe = jugadores.stream().anyMatch(j -> j.getNombre().equalsIgnoreCase(jugador.getNombre()));
        if (existe) {
            System.out.println("Error: Ya existe un jugador con ese nombre.");
            return;
        }

        jugadores.add(jugador);
        conexionBD.insertarJugador(jugador);
    }

    public void mostrarJugadores() {
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores registrados.");
        } else {
            System.out.println("Lista de jugadores:");
            for (Jugador j : jugadores) {
                System.out.println(j);
            }
        }
    }

    public void generarFixture() {
        if (jugadores.size() < 2) {
            System.out.println("Debe haber al menos 2 jugadores para generar el fixture.");
            return;
        }

        if (!rondaActual.isEmpty() && !rondaActual.stream().allMatch(Partido::estaJugado)) {
            System.out.println("La ronda actual aun no ha terminado. Complete los resultados antes de generar un nuevo fixture.");
            mostrarPartidos();
            return;
        }

        List<Jugador> jugadoresDisponiblesParaRonda = new ArrayList<>();
        if (partidos.isEmpty()) {
            jugadoresDisponiblesParaRonda.addAll(jugadores);
        } else {
            if (!ultimosGanadoresRonda.isEmpty()) {
                jugadoresDisponiblesParaRonda.addAll(ultimosGanadoresRonda);
            } else {
                System.out.println("No se pueden generar nuevas rondas. Parece que no hay ganadores de la ronda anterior disponibles.");
                return;
            }
        }

        if (jugadoresDisponiblesParaRonda.size() < 2) {
            System.out.println("Se necesitan al menos 2 jugadores para generar la siguiente ronda. Jugadores disponibles: " + jugadoresDisponiblesParaRonda.size());
            if(jugadoresDisponiblesParaRonda.size() == 1) {
                mostrarGanador();
            }
            return;
        }

        rondaActual.clear();
        ultimosGanadoresRonda.clear();

        List<Jugador> rondaTemp = new ArrayList<>(jugadoresDisponiblesParaRonda);

        if (rondaTemp.size() % 2 != 0) {
            Jugador byePlayer = new Jugador("BYE", 0);
            rondaTemp.add(byePlayer);
            System.out.println("Se añadio un jugador 'BYE' para emparejar la ronda. (" + jugadoresDisponiblesParaRonda.size() + " jugadores originales)");
        }

        for (int i = 0; i < rondaTemp.size(); i += 2) {
            Jugador j1 = rondaTemp.get(i);
            Jugador j2 = rondaTemp.get(i + 1);

            Partido p = new Partido(j1, j2);
            partidos.add(p);
            rondaActual.add(p);

            if (j1.getNombre().equals("BYE")) {
                p.setResultado(j2, "BYE");
                conexionBD.actualizarResultadoPartido(p);
                System.out.println("Partido ID " + p.getId() + ": " + j2.getNombre() + " automaticamente gana por BYE.");
            } else if (j2.getNombre().equals("BYE")) {
                p.setResultado(j1, "BYE");
                conexionBD.actualizarResultadoPartido(p);
                System.out.println("Partido ID " + p.getId() + ": " + j1.getNombre() + " automaticamente gana por BYE.");
            } else {
                conexionBD.insertarPartido(p);
            }
        }

        System.out.println("Fixture de nueva ronda generado. Total de partidos en esta ronda: " + rondaActual.size());
        mostrarPartidos();
    }

    public void mostrarPartidos() {
        if (rondaActual.isEmpty()) {
            System.out.println("No hay partidos generados para la ronda actual. Por favor, genere el fixture.");
        } else {
            System.out.println("Partidos de la ronda actual:");
            for (Partido p : rondaActual) {
                System.out.println(p);
            }
        }
    }

    public boolean existePartido(int id) {
        return partidos.stream().anyMatch(p -> p.getId() == id);
    }

    public Jugador obtenerJugadorPorId(int id) {
        return jugadores.stream().filter(j -> j.getId() == id).findFirst().orElse(null);
    }

    public void cargarResultado(int idPartido, int idGanador, String marcador) {
        Partido partido = rondaActual.stream().filter(p -> p.getId() == idPartido).findFirst().orElse(null);
        Jugador ganador = obtenerJugadorPorId(idGanador);

        if (partido == null) {
            System.out.println("Error: Partido ID " + idPartido + " no encontrado en la ronda actual.");
            return;
        }
        if (ganador == null) {
            System.out.println("Error: Jugador ganador con ID " + idGanador + " no encontrado.");
            return;
        }
        if (!partido.getJugador1().equals(ganador) && !partido.getJugador2().equals(ganador)) {
            System.out.println("Error: El jugador ganador debe ser uno de los participantes del partido.");
            System.out.println("Los jugadores del partido " + partido.getId() + " son: " + partido.getJugador1().getNombre() + " (ID " + partido.getJugador1().getId() + ") y " + partido.getJugador2().getNombre() + " (ID " + partido.getJugador2().getId() + ")");
            return;
        }

        if (partido.estaJugado()) {
            System.out.println("El partido ID " + idPartido + " ya tiene un resultado registrado.");
            System.out.println(partido);
            return;
        }

        partido.setResultado(ganador, marcador);
        // La corrección está aquí: usar 'partido' en lugar de 'p'
        conexionBD.actualizarResultadoPartido(partido);
        System.out.println("Resultado del partido ID " + idPartido + " cargado correctamente.");

        avanzarRonda();
    }

    private void avanzarRonda() {
        boolean rondaCompleta = rondaActual.stream().allMatch(Partido::estaJugado);

        if (!rondaCompleta) {
            System.out.println("La ronda actual aun no esta completa. Faltan partidos por jugar.");
            return;
        }

        System.out.println("Todos los partidos de la ronda actual han sido jugados!");

        List<Jugador> ganadores = rondaActual.stream()
                .filter(Partido::estaJugado)
                .map(Partido::getGanador)
                .collect(Collectors.toList());

        ganadores.removeIf(j -> j.getNombre().equals("BYE"));

        this.ultimosGanadoresRonda = new ArrayList<>(ganadores);

        if (ganadores.size() == 1) {
            System.out.println("--- Felicidades! Tenemos un ganador del torneo! ---");
            System.out.println("El campeon es: " + ganadores.get(0).getNombre());
            rondaActual.clear();
            this.ultimosGanadoresRonda.clear();
        } else if (ganadores.size() > 1) {
            System.out.println("Preparando la siguiente ronda con " + ganadores.size() + " jugadores ganadores!");
            System.out.println("Por favor, utilice la opcion 'Generar Fixture' para crear los partidos de la nueva ronda.");
            rondaActual.clear();
        } else {
            System.out.println("Algo inesperado ocurrio: No se encontraron ganadores para avanzar a la siguiente ronda.");
            this.ultimosGanadoresRonda.clear();
        }
    }

    public void mostrarGanador() {
        if (ultimosGanadoresRonda.size() == 1) {
            System.out.println("El ganador del torneo es: " + ultimosGanadoresRonda.get(0).getNombre());
            return;
        }

        if (rondaActual.isEmpty() && !partidos.isEmpty()) {
            List<Partido> partidosJugados = partidos.stream()
                    .filter(Partido::estaJugado)
                    .collect(Collectors.toList());

            if (!partidosJugados.isEmpty()) {
                List<Jugador> ganadoresDeTodosLosPartidos = partidosJugados.stream()
                        .map(Partido::getGanador)
                        .distinct()
                        .filter(j -> j != null && !j.getNombre().equals("BYE"))
                        .collect(Collectors.toList());

                if (ganadoresDeTodosLosPartidos.size() == 1) {
                    System.out.println("El ganador del torneo es: " + ganadoresDeTodosLosPartidos.get(0).getNombre());
                } else {
                    System.out.println("El torneo esta en curso o no se ha determinado un ganador final. (Multiples ganadores de rondas previas o torneo incompleto)");
                }
            } else {
                System.out.println("Aun no se han jugado partidos. No hay ganador.");
            }
        } else if (!rondaActual.isEmpty()) {
            System.out.println("El torneo esta en curso. No hay ganador aun.");
        } else {
            System.out.println("No hay partidos registrados ni torneo en curso.");
        }
    }

    public void reiniciarTorneo() {
        conexionBD.borrarTodosLosDatos();

        jugadores.clear();
        partidos.clear();
        rondaActual.clear();
        ultimosGanadoresRonda.clear();

        Jugador.setContador(0);
        Partido.setContador(0);

        System.out.println("El torneo ha sido completamente reiniciado. Puedes empezar de nuevo!");
    }
}