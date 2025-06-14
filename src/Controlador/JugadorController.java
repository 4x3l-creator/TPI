package Controlador;

import Modelo.Jugador;
import Modelo.Torneo;
import Modelo.ConexionBD; // Conexion con "ConexionBD"

import java.util.List;
import java.util.Optional;

public class JugadorController {
    private ConexionBD conexionBD; // Usa ConexionBD
    private Torneo torneo;

    public JugadorController(ConexionBD conexionBD) { // Recibe ConexionBD
        this.conexionBD = conexionBD;
        this.torneo = Torneo.getInstancia();
    }

    public void agregarNuevoJugador(String nombre, int edad) {
        if (nombre == null || nombre.trim().isEmpty() || edad <= 0) {
            System.out.println("Error: Nombre del jugador no puede estar vacío y la edad debe ser positiva.");
            return;
        }
        Jugador nuevoJugador = new Jugador(nombre, edad);
        conexionBD.insertarJugador(nuevoJugador); // Usa ConexionBD para insertar
        torneo.agregarJugador(nuevoJugador);
        System.out.println("Jugador '" + nombre + "' agregado exitosamente.");
    }

    public List<Jugador> obtenerTodosLosJugadores() {
        return torneo.getJugadores();
    }

    public Optional<Jugador> buscarJugadorPorId(int id) {
        return torneo.buscarJugadorPorId(id);
    }
}