public class Partido {
    static int contador = 0;
    private int id;
    private Jugador jugador1;
    private Jugador jugador2;
    private Jugador ganador;
    private String marcador;

    public Partido(Jugador jugador1, Jugador jugador2) {
        this.id = ++contador;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.ganador = null;
        this.marcador = null;
    }

    public Partido(int id, Jugador jugador1, Jugador jugador2, Jugador ganador, String marcador) {
        this.id = id;
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.ganador = ganador;
        this.marcador = marcador;
        if (id > contador) {
            contador = id;
        }
    }

    public static void setContador(int lastId) {
        if (lastId > contador) {
            contador = lastId;
        }
    }

    public int getId() {
        return id;
    }

    public Jugador getJugador1() {
        return jugador1;
    }

    public Jugador getJugador2() {
        return jugador2;
    }

    public Jugador getGanador() {
        return ganador;
    }

    public String getMarcador() {
        return marcador;
    }

    public boolean estaJugado() {
        return ganador != null;
    }

    public void setResultado(Jugador ganador, String marcador) {
        this.ganador = ganador;
        this.marcador = marcador;
    }

    @Override
    public String toString() {
        return "Partido ID " + id + ": " + jugador1.getNombre() + " vs " + jugador2.getNombre() +
                (ganador != null ? " | Ganador: " + ganador.getNombre() + " | Marcador: " + marcador : " | En espera");
    }
}