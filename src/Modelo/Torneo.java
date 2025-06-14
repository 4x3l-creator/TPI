package Modelo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Torneo {
    private List<Jugador> jugadores;
    private List<Partido> partidos;
    private static Torneo instancia;

    private Torneo() {
        this.jugadores = new ArrayList<>();
        this.partidos = new ArrayList<>();
    }

    public static Torneo getInstancia() {
        if (instancia == null) {
            instancia = new Torneo();
        }
        return instancia;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public void setJugadores(List<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public void setPartidos(List<Partido> partidos) {
        this.partidos = partidos;
    }

    public void agregarJugador(Jugador jugador) {
        this.jugadores.add(jugador);
    }

    public void agregarPartido(Partido partido) {
        this.partidos.add(partido);
    }

    public Optional<Jugador> buscarJugadorPorId(int id) {
        return jugadores.stream()
                .filter(j -> j.getId() == id)
                .findFirst();
    }

    public Optional<Partido> buscarPartidoPorId(int id) {
        return partidos.stream()
                .filter(p -> p.getId() == id)
                .findFirst();
    }

    public void borrarTodosLosDatos() {
        this.jugadores.clear();
        this.partidos.clear();
        Jugador.setContador(0);
        Partido.setContador(0);
        System.out.println("Datos en memoria del Torneo borrados.");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- ESTADO ACTUAL DEL TORNEO ---\n");
        sb.append("Jugadores Registrados:\n");
        if (jugadores.isEmpty()) {
            sb.append("  (Ninguno)\n");
        } else {
            jugadores.forEach(j -> sb.append("  - ").append(j).append("\n"));
        }
        sb.append("Partidos Programados/Jugados:\n");
        if (partidos.isEmpty()) {
            sb.append("  (Ninguno)\n");
        } else {
            partidos.forEach(p -> sb.append("  - ").append(p).append("\n"));
        }
        return sb.toString();
    }
}