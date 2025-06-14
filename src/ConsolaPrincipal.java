import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ConsolaPrincipal {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Torneo torneo = new Torneo();
        Pattern marcadorPattern = Pattern.compile("\\d+-\\d+");

        int opcionMenuInicial;
        do {
            System.out.println("\n==============================");
            System.out.println("SISTEMA TORNEO DE PING PONG");
            System.out.println("==============================");
            System.out.println("1. Gestion del Torneo (Registro y Fixture)");
            System.out.println("2. Verificar datos del torneo");
            System.out.println("0. Salir del sistema");
            System.out.print("Elija una opcion: ");

            opcionMenuInicial = leerEntero(scanner);
            scanner.nextLine();

            switch (opcionMenuInicial) {
                case 1 -> {
                    int opcionGestion;
                    do {
                        System.out.println("\nMENU DE GESTION DEL TORNEO");
                        System.out.println("1. Registrar nuevo jugador");
                        System.out.println("2. Generar Fixture / Siguiente Ronda");
                        System.out.println("3. Cargar resultado de un partido");
                        System.out.println("4. Reiniciar Torneo (BORRAR TODO)");
                        System.out.println("0. Volver al menu principal");
                        System.out.print("Elija una opcion: ");

                        opcionGestion = leerEntero(scanner);
                        scanner.nextLine();

                        switch (opcionGestion) {
                            case 1 -> {
                                boolean seguir;
                                do {
                                    String nombre;
                                    do {
                                        System.out.print("Ingrese el nombre del jugador: ");
                                        nombre = scanner.nextLine().trim();
                                        if (nombre.isEmpty()) {
                                            System.out.println("El nombre no puede estar vacio o solo contener espacios.");
                                        }
                                    } while (nombre.isEmpty());

                                    int edad = -1;
                                    while (edad < 0) {
                                        System.out.print("Ingrese la edad del jugador: ");
                                        try {
                                            edad = Integer.parseInt(scanner.nextLine());
                                            if (edad < 0)
                                                System.out.println("La edad debe ser positiva.");
                                        } catch (NumberFormatException e) {
                                            System.out.println("Ingrese un numero valido.");
                                        }
                                    }
                                    torneo.agregarJugador(new Jugador(nombre, edad));
                                    System.out.println("Jugador agregado correctamente.");

                                    System.out.print("Desea cargar otro jugador? (s/n): ");
                                    seguir = scanner.nextLine().equalsIgnoreCase("s");
                                } while (seguir);
                            }
                            case 2 -> {
                                torneo.generarFixture();
                            }
                            case 3 -> {
                                boolean seguir = false;
                                do {
                                    torneo.mostrarPartidos();
                                    if (torneo.getRondaActual().isEmpty()) {
                                        System.out.println("No hay partidos en la ronda actual para cargar resultados. Genere el fixture primero.");
                                        break;
                                    }

                                    System.out.print("Ingrese ID del partido para cargar resultado: ");
                                    int idPartido = leerEntero(scanner);
                                    scanner.nextLine();

                                    if (!torneo.existePartido(idPartido)) {
                                        System.out.println("Partido no encontrado.");
                                        System.out.print("Desea intentar con otro partido? (s/n): ");
                                        if (!scanner.nextLine().equalsIgnoreCase("s")) {
                                            break;
                                        }
                                        continue;
                                    }

                                    int idGanador;
                                    Jugador jugadorGanador;
                                    do {
                                        System.out.print("Ingrese ID del jugador ganador: ");
                                        idGanador = leerEntero(scanner);
                                        scanner.nextLine();
                                        jugadorGanador = torneo.obtenerJugadorPorId(idGanador);
                                        if (jugadorGanador == null) {
                                            System.out.println("Jugador ganador no encontrado. Intente de nuevo.");
                                        }
                                    } while (jugadorGanador == null);


                                    String marcador;
                                    do {
                                        System.out.print("Ingrese el marcador (ej. 3-2): ");
                                        marcador = scanner.nextLine();
                                        if (!marcadorPattern.matcher(marcador).matches()) {
                                            System.out.println("Formato invalido. Use el formato correcto como '3-2'.");
                                        }
                                    } while (!marcadorPattern.matcher(marcador).matches());

                                    torneo.cargarResultado(idPartido, idGanador, marcador);
                                    System.out.println("Resultado cargado.");

                                    System.out.print("Desea cargar otro resultado? (s/n): ");
                                    seguir = scanner.nextLine().equalsIgnoreCase("s");
                                } while (seguir);
                            }
                            case 4 -> {
                                System.out.println("ADVERTENCIA! Esta accion borrara TODOS los jugadores y partidos de la base de datos.");
                                System.out.print("Esta seguro que desea reiniciar el torneo y borrar todos los datos? (escriba 'SI' para confirmar): ");
                                String confirmacion = scanner.nextLine().trim();
                                if (confirmacion.equalsIgnoreCase("SI")) {
                                    torneo.reiniciarTorneo();
                                    opcionGestion = 0;
                                } else {
                                    System.out.println("Operacion de reinicio cancelada.");
                                }
                            }
                            case 0 -> System.out.println("Volviendo al menu principal...");
                            default -> System.out.println("Opcion invalida. Intente nuevamente.");
                        }
                    } while (opcionGestion != 0);
                }

                case 2 -> {
                    int opcionConsulta;
                    do {
                        System.out.println("\nMENU DE CONSULTAS");
                        System.out.println("1. Ver lista de jugadores");
                        System.out.println("2. Ver partidos de la ronda actual");
                        System.out.println("3. Ver ganador del torneo");
                        System.out.println("0. Volver al menu principal");
                        System.out.print("Elija una opcion: ");

                        opcionConsulta = leerEntero(scanner);
                        scanner.nextLine();

                        switch (opcionConsulta) {
                            case 1 -> torneo.mostrarJugadores();
                            case 2 -> torneo.mostrarPartidos();
                            case 3 -> torneo.mostrarGanador();
                            case 0 -> System.out.println("Volviendo al menu principal...");
                            default -> System.out.println("Opcion invalida. Intente nuevamente.");
                        }

                        if (opcionConsulta != 0) {
                            System.out.print("Desea hacer otra consulta? (s/n): ");
                            if (!scanner.nextLine().equalsIgnoreCase("s")) {
                                opcionConsulta = 0;
                                System.out.println("Volviendo al menu principal...");
                            }
                        }

                    } while (opcionConsulta != 0);
                }

                case 0 -> System.out.println("Gracias por usar el sistema! Hasta luego.");
                default -> System.out.println("Opcion invalida. Intente nuevamente.");
            }
        } while (opcionMenuInicial != 0);
        scanner.close();
    }

    private static int leerEntero(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.println("Ingrese un numero valido.");
            scanner.next();
            System.out.print("Elija una opcion: ");
        }
        return scanner.nextInt();
    }
}