package Controlador;

import Modelo.Jugador;
import Modelo.Partido;
import Modelo.Torneo;
import Modelo.ConexionBD; // Importamos directamente ConexionBD

import java.util.List;
import java.util.Optional;

public class PartidoController {
    private ConexionBD conexionBD; // Ahora directamente usa ConexionBD
    private Torneo torneo;

    public PartidoController(ConexionBD conexionBD) { // Recibe ConexionBD
        this.conexionBD = conexionBD;
        this.torneo = Torneo.getInstancia();
    }

    public void crearNuevoPartido(int jugador1Id, int jugador2Id) {
        Optional<Jugador> optJugador1 = torneo.buscarJugadorPorId(jugador1Id);
        Optional<Jugador> optJugador2 = torneo.buscarJugadorPorId(jugador2Id);

        if (optJugador1.isEmpty() || optJugador2.isEmpty()) {
            System.out.println("Error: Uno o ambos jugadores no existen.");
            return;
        }

        Jugador j1 = optJugador1.get();
        Jugador j2 = optJugador2.get();

        if (j1.equals(j2)) {
            System.out.println("Error: Un jugador no puede jugar contra sí mismo.");
            return;
        }

        Partido nuevoPartido = new Partido(j1, j2);
        conexionBD.insertarPartido(nuevoPartido); // Usa ConexionBD para insertar
        torneo.agregarPartido(nuevoPartido);
        System.out.println("Partido creado entre " + j1.getNombre() + " y " + j2.getNombre() + ".");
    }

    public void registrarResultadoPartido(int partidoId, int ganadorId, String marcador) {
        Optional<Partido> optPartido = torneo.buscarPartidoPorId(partidoId);
        Optional<Jugador> optGanador = torneo.buscarJugadorPorId(ganadorId);

        if (optPartido.isEmpty()) {
            System.out.println("Error: Partido con ID " + partidoId + " no encontrado.");
            return;
        }
        if (optGanador.isEmpty()) {
            System.out.println("Error: Jugador ganador con ID " + ganadorId + " no encontrado.");
            return;
        }

        Partido partido = optPartido.get();
        Jugador ganador = optGanador.get();

        if (!partido.getJugador1().equals(ganador) && !partido.getJugador2().equals(ganador)) {
            System.out.println("Error: El ganador debe ser uno de los jugadores del partido.");
            return;
        }

        partido.setResultado(ganador, marcador);
        conexionBD.actualizarResultadoPartido(partido); // Usa ConexionBD para actualizar
        System.out.println("Resultado registrado para partido ID " + partidoId + ".");
    }

    public List<Partido> obtenerTodosLosPartidos() {
        return torneo.getPartidos();
    }

    public List<Partido> obtenerPartidosPendientes() {
        return torneo.getPartidos().stream()
                .filter(p -> !p.estaJugado())
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Partido> obtenerPartidosJugados() {
        return torneo.getPartidos().stream()
                .filter(Partido::estaJugado)
                .collect(java.util.stream.Collectors.toList());
    }
}