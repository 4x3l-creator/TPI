package Controlador;

import Modelo.Torneo;
import Modelo.ConexionBD; // Importamos directamente ConexionBD
import Modelo.Jugador; // Necesario para cargar partidos
import Modelo.Partido; // Necesario para cargar partidos

public class MainController {
    private ConexionBD conexionBD;
    private Torneo torneo;

    private JugadorController jugadorController;
    private PartidoController partidoController;

    public MainController() {
        this.conexionBD = new ConexionBD();
        this.torneo = Torneo.getInstancia();

        // Pasa la instancia de ConexionBD a los controladores específicos
        this.jugadorController = new JugadorController(this.conexionBD);
        this.partidoController = new PartidoController(this.conexionBD);
    }

    public void inicializarAplicacion() {
        System.out.println("Inicializando la aplicación de Campeonato de Ping Pong...");
        cargarDatosIniciales();
        System.out.println("Aplicación inicializada y datos cargados.");
    }

    private void cargarDatosIniciales() {
        // Cargar jugadores y partidos usando ConexionBD directamente
        torneo.setJugadores(conexionBD.cargarJugadores());
        torneo.setPartidos(conexionBD.cargarPartidos(torneo.getJugadores()));
    }

    public void borrarTodosLosDatosDelSistema() {
        conexionBD.borrarTodosLosDatos(); // Borra de la BD
        torneo.borrarTodosLosDatos();     // Borra de la memoria del modelo
        System.out.println("Todos los datos del sistema (BD y memoria) han sido borrados.");
    }

    public JugadorController getJugadorController() {
        return jugadorController;
    }

    public PartidoController getPartidoController() {
        return partidoController;
    }

    public Torneo getTorneo() {
        return torneo;
    }
}