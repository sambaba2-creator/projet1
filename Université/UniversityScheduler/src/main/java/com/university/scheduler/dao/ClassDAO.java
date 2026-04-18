package com.university.scheduler.dao;

import com.university.scheduler.model.AcademicClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassDAO {
    private static final Logger logger = LoggerFactory.getLogger(ClassDAO.class);
    private DatabaseManager dbManager;

    public ClassDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createClass(AcademicClass clazz) {
        String sql = "INSERT INTO classes (name, capacity, level, specialization) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, clazz.getName());
            stmt.setInt(2, clazz.getCapacity());
            stmt.setString(3, clazz.getLevel());
            stmt.setString(4, clazz.getSpecialization());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error creating class", e);
        }
        return -1;
    }

    public AcademicClass getClassById(int id) {
        String sql = "SELECT * FROM classes WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAcademicClass(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting class by id", e);
        }
        return null;
    }

    public List<AcademicClass> getAllClasses() {
        List<AcademicClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes ORDER BY name";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                classes.add(mapResultSetToAcademicClass(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all classes", e);
        }
        return classes;
    }

    public AcademicClass getClassByName(String name) {
        String sql = "SELECT * FROM classes WHERE name = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToAcademicClass(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting class by name", e);
        }
        return null;
    }

    public List<AcademicClass> getClassesByLevel(String level) {
        List<AcademicClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE level = ? ORDER BY name";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, level);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classes.add(mapResultSetToAcademicClass(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting classes by level", e);
        }
        return classes;
    }

    public List<AcademicClass> getClassesBySpecialization(String specialization) {
        List<AcademicClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE specialization = ? ORDER BY name";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, specialization);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                classes.add(mapResultSetToAcademicClass(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting classes by specialization", e);
        }
        return classes;
    }

    public boolean updateClass(AcademicClass clazz) {
        String sql = "UPDATE classes SET name = ?, capacity = ?, level = ?, specialization = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, clazz.getName());
            stmt.setInt(2, clazz.getCapacity());
            stmt.setString(3, clazz.getLevel());
            stmt.setString(4, clazz.getSpecialization());
            stmt.setInt(5, clazz.getId());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating class", e);
        }
        return false;
    }

    public boolean deleteClass(int id) {
        String sql = "DELETE FROM classes WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting class", e);
        }
        return false;
    }

    private AcademicClass mapResultSetToAcademicClass(ResultSet rs) throws SQLException {
        return new AcademicClass(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getInt("capacity"),
                rs.getString("level"),
                rs.getString("specialization")
        );
    }
}
