package com.university.scheduler.dao;

import com.university.scheduler.model.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {
    private static final Logger logger = LoggerFactory.getLogger(RoomDAO.class);
    private DatabaseManager dbManager;

    public RoomDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createRoom(Room room) {
        String sql = "INSERT INTO rooms (number, capacity, type, building_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, room.getNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getType());
            stmt.setInt(4, room.getBuildingId());
            stmt.executeUpdate();

            try (Statement lastIdStmt = dbManager.getConnection().createStatement();
                 ResultSet resultSet = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating room", e);
        }
        return -1;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                room.setEquipmentIds(getEquipmentsForRoom(id));
                return room;
            }
        } catch (SQLException e) {
            logger.error("Error getting room by id", e);
        }
        return null;
    }

    public List<Room> getRoomsByBuilding(int buildingId) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE building_id = ? ORDER BY number";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, buildingId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                room.setEquipmentIds(getEquipmentsForRoom(room.getId()));
                rooms.add(room);
            }
        } catch (SQLException e) {
            logger.error("Error getting rooms by building", e);
        }
        return rooms;
    }

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY number";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                room.setEquipmentIds(getEquipmentsForRoom(room.getId()));
                rooms.add(room);
            }
        } catch (SQLException e) {
            logger.error("Error getting all rooms", e);
        }
        return rooms;
    }

    public List<Room> getRoomsByCapacity(int minCapacity) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE capacity >= ? ORDER BY number";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, minCapacity);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                room.setEquipmentIds(getEquipmentsForRoom(room.getId()));
                rooms.add(room);
            }
        } catch (SQLException e) {
            logger.error("Error getting rooms by capacity", e);
        }
        return rooms;
    }

    public List<Room> getRoomsByType(String type) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE type = ? ORDER BY number";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Room room = mapResultSetToRoom(rs);
                room.setEquipmentIds(getEquipmentsForRoom(room.getId()));
                rooms.add(room);
            }
        } catch (SQLException e) {
            logger.error("Error getting rooms by type", e);
        }
        return rooms;
    }

    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET number = ?, capacity = ?, type = ?, building_id = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, room.getNumber());
            stmt.setInt(2, room.getCapacity());
            stmt.setString(3, room.getType());
            stmt.setInt(4, room.getBuildingId());
            stmt.setInt(5, room.getId());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating room", e);
        }
        return false;
    }

    public boolean deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting room", e);
        }
        return false;
    }

    public void addEquipmentToRoom(int roomId, int equipmentId) {
        String sql = "INSERT OR IGNORE INTO room_equipment (room_id, equipment_id) VALUES (?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, equipmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error adding equipment to room", e);
        }
    }

    public void removeEquipmentFromRoom(int roomId, int equipmentId) {
        String sql = "DELETE FROM room_equipment WHERE room_id = ? AND equipment_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setInt(2, equipmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error removing equipment from room", e);
        }
    }

    private List<Integer> getEquipmentsForRoom(int roomId) {
        List<Integer> equipments = new ArrayList<>();
        String sql = "SELECT equipment_id FROM room_equipment WHERE room_id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                equipments.add(rs.getInt("equipment_id"));
            }
        } catch (SQLException e) {
            logger.error("Error getting equipments for room", e);
        }
        return equipments;
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        return new Room(
                rs.getInt("id"),
                rs.getString("number"),
                rs.getInt("capacity"),
                rs.getString("type"),
                rs.getInt("building_id")
        );
    }
}
