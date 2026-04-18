package com.university.scheduler.dao;

import com.university.scheduler.model.Equipment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentDAO.class);
    private DatabaseManager dbManager;

    public EquipmentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createEquipment(Equipment equipment) {
        String sql = "INSERT INTO equipments (name) VALUES (?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, equipment.getName());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error creating equipment", e);
        }
        return -1;
    }

    public Equipment getEquipmentById(int id) {
        String sql = "SELECT * FROM equipments WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEquipment(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting equipment by id", e);
        }
        return null;
    }

    public List<Equipment> getAllEquipments() {
        List<Equipment> equipments = new ArrayList<>();
        String sql = "SELECT * FROM equipments ORDER BY name";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                equipments.add(mapResultSetToEquipment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all equipments", e);
        }
        return equipments;
    }

    public Equipment getEquipmentByName(String name) {
        String sql = "SELECT * FROM equipments WHERE name = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToEquipment(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting equipment by name", e);
        }
        return null;
    }

    public boolean updateEquipment(Equipment equipment) {
        String sql = "UPDATE equipments SET name = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, equipment.getName());
            stmt.setInt(2, equipment.getId());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating equipment", e);
        }
        return false;
    }

    public boolean deleteEquipment(int id) {
        String sql = "DELETE FROM equipments WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting equipment", e);
        }
        return false;
    }

    private Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
        return new Equipment(
                rs.getInt("id"),
                rs.getString("name")
        );
    }
}
