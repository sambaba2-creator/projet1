package com.university.scheduler.dao;

import com.university.scheduler.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReservationDAO.class);
    private DatabaseManager dbManager;

    public ReservationDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (room_id, date, start_time, end_time, type, course_id, user_id, reason, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, reservation.getRoomId());
            stmt.setString(2, reservation.getDate());
            stmt.setString(3, reservation.getStartTime());
            stmt.setString(4, reservation.getEndTime());
            stmt.setString(5, reservation.getType());
            if (reservation.getCourseId() > 0) {
                stmt.setInt(6, reservation.getCourseId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, reservation.getUserId());
            stmt.setString(8, reservation.getReason());
            stmt.setString(9, reservation.getStatus());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error creating reservation", e);
        }
        return -1;
    }

    public Reservation getReservationById(int id) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToReservation(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting reservation by id", e);
        }
        return null;
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY date, start_time";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all reservations", e);
        }
        return reservations;
    }

    public List<Reservation> getReservationsByRoom(int roomId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE room_id = ? ORDER BY date, start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting reservations by room", e);
        }
        return reservations;
    }

    public List<Reservation> getReservationsByDate(String date) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE date = ? ORDER BY start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting reservations by date", e);
        }
        return reservations;
    }

    public List<Reservation> getReservationsByRoomAndDate(int roomId, String date) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE room_id = ? AND date = ? ORDER BY start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting reservations by room and date", e);
        }
        return reservations;
    }

    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY date, start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting reservations by user", e);
        }
        return reservations;
    }

    public List<Reservation> getReservationsByStatus(String status) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY date, start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting reservations by status", e);
        }
        return reservations;
    }

    public boolean updateReservation(Reservation reservation) {
        String sql = "UPDATE reservations SET room_id = ?, date = ?, start_time = ?, end_time = ?, " +
                     "type = ?, course_id = ?, user_id = ?, reason = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, reservation.getRoomId());
            stmt.setString(2, reservation.getDate());
            stmt.setString(3, reservation.getStartTime());
            stmt.setString(4, reservation.getEndTime());
            stmt.setString(5, reservation.getType());
            if (reservation.getCourseId() > 0) {
                stmt.setInt(6, reservation.getCourseId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            stmt.setInt(7, reservation.getUserId());
            stmt.setString(8, reservation.getReason());
            stmt.setString(9, reservation.getStatus());
            stmt.setInt(10, reservation.getId());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating reservation", e);
        }
        return false;
    }

    public boolean deleteReservation(int id) {
        String sql = "DELETE FROM reservations WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting reservation", e);
        }
        return false;
    }

    public boolean cancelReservation(int id) {
        String sql = "UPDATE reservations SET status = 'CANCELLED' WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error cancelling reservation", e);
        }
        return false;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        return new Reservation(
                rs.getInt("id"),
                rs.getInt("room_id"),
                rs.getString("date"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getString("type"),
                rs.getInt("course_id"),
                rs.getInt("user_id"),
                rs.getString("reason"),
                rs.getString("status")
        );
    }
}
