package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/campeonatopingpong";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public ConexionBD() {
        crearTablasSiNoExisten();
    }

    public void crearTablasSiNoExisten() {
        String sqlJugadores = "CREATE TABLE IF NOT EXISTS jugadores ("
                + "id INT PRIMARY KEY, "
                + "nombre VARCHAR(255) NOT NULL, "
                + "edad INT"
                + ");";

        String sqlPartidos = "CREATE TABLE IF NOT EXISTS partidos ("
                + "id INT PRIMARY KEY, "
                + "jugador1_id INT NOT NULL, "
                + "jugador2_id INT NOT NULL, "
                + "ganador_id INT NULL, "
                + "marcador VARCHAR(50) NULL, "
                + "FOREIGN KEY (jugador1_id) REFERENCES jugadores(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (jugador2_id) REFERENCES jugadores(id) ON DELETE CASCADE, "
                + "FOREIGN KEY (ganador_id) REFERENCES jugadores(id) ON DELETE SET NULL"
                + ");";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sqlJugadores);
            stmt.execute(sqlPartidos);
            System.out.println("Tablas verificadas/creadas en la base de datos.");
        } catch (SQLException e) {
            System.err.println("Error al crear las tablas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertarJugador(Jugador jugador) {
        String sql = "INSERT INTO jugadores(id, nombre, edad) VALUES(?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jugador.getId());
            pstmt.setString(2, jugador.getNombre());
            pstmt.setInt(3, jugador.getEdad());
            pstmt.executeUpdate();
            System.out.println("Jugador '" + jugador.getNombre() + "' insertado en la BD.");
        } catch (SQLException e) {
            System.err.println("Error al insertar jugador '" + jugador.getNombre() + "': " + e.getMessage());
        }
    }

    public List<Jugador> cargarJugadores() {
        List<Jugador> jugadores = new ArrayList<>();
        String sql = "SELECT id, nombre, edad FROM jugadores ORDER BY id";
        int maxId = 0;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                int edad = rs.getInt("edad");
                jugadores.add(new Jugador(id, nombre, edad));
                if (id > maxId) {
                    maxId = id;
                }
            }
            Jugador.setContador(maxId);
            System.out.println(jugadores.size() + " jugadores cargados desde la base de datos.");
        } catch (SQLException e) {
            System.err.println("Error al cargar jugadores desde la BD: " + e.getMessage());
        }
        return jugadores;
    }

    public void insertarPartido(Partido partido) {
        String sql = "INSERT INTO partidos(id, jugador1_id, jugador2_id, ganador_id, marcador) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, partido.getId());
            pstmt.setInt(2, partido.getJugador1().getId());
            pstmt.setInt(3, partido.getJugador2().getId());
            if (partido.getGanador() != null) {
                pstmt.setInt(4, partido.getGanador().getId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, partido.getMarcador());
            pstmt.executeUpdate();
            System.out.println("Partido ID " + partido.getId() + " insertado en la BD.");
        } catch (SQLException e) {
            System.err.println("Error al insertar partido ID " + partido.getId() + ": " + e.getMessage());
        }
    }

    public void actualizarResultadoPartido(Partido partido) {
        String sql = "UPDATE partidos SET ganador_id = ?, marcador = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (partido.getGanador() != null) {
                pstmt.setInt(1, partido.getGanador().getId());
            } else {
                pstmt.setNull(1, Types.INTEGER);
            }
            pstmt.setString(2, partido.getMarcador());
            pstmt.setInt(3, partido.getId());
            pstmt.executeUpdate();
            System.out.println("Resultado para partido ID " + partido.getId() + " actualizado en la BD.");
        } catch (SQLException e) {
            System.err.println("Error al actualizar resultado del partido ID " + partido.getId() + ": " + e.getMessage());
        }
    }

    public List<Partido> cargarPartidos(List<Jugador> jugadoresExistentes) {
        List<Partido> partidos = new ArrayList<>();
        String sql = "SELECT id, jugador1_id, jugador2_id, ganador_id, marcador FROM partidos ORDER BY id";
        int maxId = 0;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int jugador1Id = rs.getInt("jugador1_id");
                int jugador2Id = rs.getInt("jugador2_id");
                Integer ganadorId = rs.getObject("ganador_id", Integer.class);
                String marcador = rs.getString("marcador");

                Jugador j1 = jugadoresExistentes.stream().filter(j -> j.getId() == jugador1Id).findFirst().orElse(null);
                Jugador j2 = jugadoresExistentes.stream().filter(j -> j.getId() == jugador2Id).findFirst().orElse(null);
                Jugador ganador = (ganadorId != null) ? jugadoresExistentes.stream().filter(j -> j.getId() == ganadorId).findFirst().orElse(null) : null;

                if (j1 != null && j2 != null) {
                    partidos.add(new Partido(id, j1, j2, ganador, marcador));
                    if (id > maxId) {
                        maxId = id;
                    }
                } else {
                    System.err.println("Error: Jugadores de partido ID " + id + " no encontrados. Saltando partido.");
                }
            }
            Partido.setContador(maxId);
            System.out.println(partidos.size() + " partidos cargados desde la base de datos.");
        } catch (SQLException e) {
            System.err.println("Error al cargar partidos desde la BD: " + e.getMessage());
        }
        return partidos;
    }

    public void borrarTodosLosDatos() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.executeUpdate("DELETE FROM partidos;");
            stmt.executeUpdate("DELETE FROM jugadores;");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");

            System.out.println("Todos los datos (jugadores y partidos) han sido borrados de la base de datos.");
        } catch (SQLException e) {
            System.err.println("Error al borrar todos los datos de la BD: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
