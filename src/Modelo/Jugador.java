package Modelo;

public class Jugador {
    private static int contador = 0;
    private int id;
    private String nombre;
    private int edad;

    public Jugador(String nombre, int edad) {
        this.id = ++contador;
        this.nombre = nombre;
        this.edad = edad;
    }

    public Jugador(int id, String nombre, int edad) {
        this.id = id;
        this.nombre = nombre;
        this.edad = edad;
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

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    @Override
    public String toString() {
        return id + ". " + nombre + " (" + edad + " años)";
    }
}