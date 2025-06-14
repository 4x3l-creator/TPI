package Vista;

import Controlador.MainController;
import Controlador.JugadorController;
import Controlador.PartidoController;
import Modelo.Jugador;
import Modelo.Partido;
import Modelo.Torneo;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ConsolaPrincipal {
    private static Scanner scanner = new Scanner(System.in);
    private MainController mainController;
    private JugadorController jugadorController;
    private PartidoController partidoController;

    public ConsolaPrincipal() {
        this.mainController = new MainController();
        this.jugadorController = mainController.getJugadorController();
        this.partidoController = mainController.getPartidoController();
    }

    public void iniciar() {
        mainController.inicializarAplicacion();
        mostrarMenuPrincipal();
    }

    private void mostrarMenuPrincipal() {
        int opcion;
        do {
            System.out.println("\n--- CAMPEONATO DE PING PONG ---");
            System.out.println("1. Gestión de Jugadores");
            System.out.println("2. Gestión de Partidos");
            System.out.println("3. Ver Estado del Torneo");
            System.out.println("4. Borrar todos los datos (De base de datos)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    menuGestionJugadores();
                    break;
                case 2:
                    menuGestionPartidos();
                    break;
                case 3:
                    mostrarEstadoTorneo();
                    break;
                case 4:
                    confirmarBorrarDatos();
                    break;
                case 0:
                    System.out.println("Saliendo del programa. ¡Hasta pronto!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void menuGestionJugadores() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE JUGADORES ---");
            System.out.println("1. Añadir Jugador");
            System.out.println("2. Listar Jugadores");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    añadirJugador();
                    break;
                case 2:
                    listarJugadores();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal.");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void añadirJugador() {
        System.out.print("Ingrese nombre del jugador: ");
        String nombre = scanner.nextLine();
        System.out.print("Ingrese edad del jugador: ");
        int edad = leerEntero();
        jugadorController.agregarNuevoJugador(nombre, edad);
    }

    private void listarJugadores() {
        List<Jugador> jugadores = jugadorController.obtenerTodosLosJugadores();
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores registrados.");
            return;
        }
        System.out.println("\n--- LISTA DE JUGADORES ---");
        jugadores.forEach(System.out::println);
    }

    private void menuGestionPartidos() {
        int opcion;
        do {
            System.out.println("\n--- GESTIÓN DE PARTIDOS ---");
            System.out.println("1. Crear Nuevo Partido");
            System.out.println("2. Registrar Resultado de Partido");
            System.out.println("3. Listar Partidos Pendientes");
            System.out.println("4. Listar Partidos Jugados");
            System.out.println("5. Listar Todos los Partidos");
            System.out.println("0. Volver al Menú Principal");
            System.out.print("Seleccione una opción: ");
            opcion = leerEntero();

            switch (opcion) {
                case 1:
                    crearPartido();
                    break;
                case 2:
                    registrarResultadoPartido();
                    break;
                case 3:
                    listarPartidosPendientes();
                    break;
                case 4:
                    listarPartidosJugados();
                    break;
                case 5:
                    listarTodosLosPartidos();
                    break;
                case 0:
                    System.out.println("Volviendo al menú principal.");
                    break;
                default:
                    System.out.println("Opción no válida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }

    private void crearPartido() {
        listarJugadores();
        if (jugadorController.obtenerTodosLosJugadores().size() < 2) {
            System.out.println("Se necesitan al menos 2 jugadores para crear un partido.");
            return;
        }
        System.out.print("Ingrese ID del Jugador 1: ");
        int idJugador1 = leerEntero();
        System.out.print("Ingrese ID del Jugador 2: ");
        int idJugador2 = leerEntero();

        partidoController.crearNuevoPartido(idJugador1, idJugador2);
    }

    private void registrarResultadoPartido() {
        listarPartidosPendientes();
        List<Partido> pendientes = partidoController.obtenerPartidosPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("No hay partidos pendientes para registrar resultados.");
            return;
        }

        System.out.print("Ingrese ID del Partido a registrar: ");
        int idPartido = leerEntero();

        Optional<Partido> optPartido = mainController.getTorneo().buscarPartidoPorId(idPartido);
        if (optPartido.isEmpty() || optPartido.get().estaJugado()) {
            System.out.println("Partido no encontrado o ya jugado.");
            return;
        }

        Partido partido = optPartido.get();
        System.out.println("Registrando resultado para: " + partido);
        System.out.print("Ingrese ID del Jugador ganador (" + partido.getJugador1().getId() + ". " + partido.getJugador1().getNombre() + " o " + partido.getJugador2().getId() + ". " + partido.getJugador2().getNombre() + "): ");
        int idGanador = leerEntero();
        System.out.print("Ingrese el marcador (ej. '3-1', '7-5 6-3'): ");
        String marcador = scanner.nextLine();

        partidoController.registrarResultadoPartido(idPartido, idGanador, marcador);
    }

    private void listarPartidosPendientes() {
        List<Partido> pendientes = partidoController.obtenerPartidosPendientes();
        if (pendientes.isEmpty()) {
            System.out.println("No hay partidos pendientes.");
            return;
        }
        System.out.println("\n--- PARTIDOS PENDIENTES ---");
        pendientes.forEach(System.out::println);
    }

    private void listarPartidosJugados() {
        List<Partido> jugados = partidoController.obtenerPartidosJugados();
        if (jugados.isEmpty()) {
            System.out.println("No hay partidos jugados.");
            return;
        }
        System.out.println("\n--- PARTIDOS JUGADOS ---");
        jugados.forEach(System.out::println);
    }

    private void listarTodosLosPartidos() {
        List<Partido> todos = partidoController.obtenerTodosLosPartidos();
        if (todos.isEmpty()) {
            System.out.println("No hay partidos registrados.");
            return;
        }
        System.out.println("\n--- TODOS LOS PARTIDOS ---");
        todos.forEach(System.out::println);
    }

    private void mostrarEstadoTorneo() {
        System.out.println(mainController.getTorneo().toString());
    }

    private void confirmarBorrarDatos() {
        System.out.println("\n¡ADVERTENCIA! Esta acción borrará TODOS los datos de la base de datos y de la memoria.");
        System.out.print("¿Está seguro que desea continuar? (escriba 'SI' para confirmar): ");
        String confirmacion = scanner.nextLine();
        if (confirmacion.equalsIgnoreCase("SI")) {
            mainController.borrarTodosLosDatosDelSistema();
        } else {
            System.out.println("Operación de borrado cancelada.");
        }
    }

    private int leerEntero() {
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Por favor, ingrese un número entero.");
            scanner.next();
        }
        int valor = scanner.nextInt();
        scanner.nextLine();
        return valor;
    }

    public static void main(String[] args) {
        ConsolaPrincipal app = new ConsolaPrincipal();
        app.iniciar();
    }
}
