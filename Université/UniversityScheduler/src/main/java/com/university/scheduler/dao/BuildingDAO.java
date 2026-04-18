package com.university.scheduler.dao;

import com.university.scheduler.model.Building;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuildingDAO {
    private static final Logger logger = LoggerFactory.getLogger(BuildingDAO.class);
    private DatabaseManager dbManager;

    public BuildingDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createBuilding(Building building) {
        String sql = "INSERT INTO buildings (name, location, floors) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, building.getName());
            stmt.setString(2, building.getLocation());
            stmt.setInt(3, building.getFloors());
            stmt.executeUpdate();

            try (Statement lastIdStmt = dbManager.getConnection().createStatement();
                 ResultSet resultSet = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating building", e);
        }
        return -1;
    }

    public Building getBuildingById(int id) {
        String sql = "SELECT * FROM buildings WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBuilding(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting building by id", e);
        }
        return null;
    }

    public List<Building> getAllBuildings() {
        List<Building> buildings = new ArrayList<>();
        String sql = "SELECT * FROM buildings ORDER BY name";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                buildings.add(mapResultSetToBuilding(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all buildings", e);
        }
        return buildings;
    }

    public Building getBuildingByName(String name) {
        String sql = "SELECT * FROM buildings WHERE name = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToBuilding(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting building by name", e);
        }
        return null;
    }

    public boolean updateBuilding(Building building) {
        String sql = "UPDATE buildings SET name = ?, location = ?, floors = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, building.getName());
            stmt.setString(2, building.getLocation());
            stmt.setInt(3, building.getFloors());
            stmt.setInt(4, building.getId());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating building", e);
        }
        return false;
    }

    public boolean deleteBuilding(int id) {
        String sql = "DELETE FROM buildings WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting building", e);
        }
        return false;
    }

    private Building mapResultSetToBuilding(ResultSet rs) throws SQLException {
        return new Building(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getInt("floors")
        );
    }
}
