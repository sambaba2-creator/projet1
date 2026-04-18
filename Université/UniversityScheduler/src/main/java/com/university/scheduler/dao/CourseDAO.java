package com.university.scheduler.dao;

import com.university.scheduler.model.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private static final Logger logger = LoggerFactory.getLogger(CourseDAO.class);
    private DatabaseManager dbManager;

    public CourseDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public int createCourse(Course course) {
        String sql = "INSERT INTO courses (subject, teacher_id, class_id, day_of_week, start_time, end_time, duration, room_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, course.getSubject());
            stmt.setInt(2, course.getTeacherId());
            stmt.setInt(3, course.getClassId());
            stmt.setString(4, course.getDayOfWeek());
            stmt.setString(5, course.getStartTime());
            stmt.setString(6, course.getEndTime());
            stmt.setInt(7, course.getDuration());
            stmt.setInt(8, course.getRoomId());
            stmt.executeUpdate();

            try (Statement lastIdStmt = dbManager.getConnection().createStatement();
                 ResultSet resultSet = lastIdStmt.executeQuery("SELECT last_insert_rowid()")) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating course", e);
        }
        return -1;
    }

    public Course getCourseById(int id) {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCourse(rs);
            }
        } catch (SQLException e) {
            logger.error("Error getting course by id", e);
        }
        return null;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY day_of_week, start_time";
        try (Statement stmt = dbManager.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting all courses", e);
        }
        return courses;
    }

    public List<Course> getCoursesByTeacher(int teacherId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE teacher_id = ? ORDER BY day_of_week, start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting courses by teacher", e);
        }
        return courses;
    }

    public List<Course> getCoursesByClass(int classId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE class_id = ? ORDER BY day_of_week, start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting courses by class", e);
        }
        return courses;
    }

    public List<Course> getCoursesByRoom(int roomId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE room_id = ? ORDER BY day_of_week, start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting courses by room", e);
        }
        return courses;
    }

    public List<Course> getCoursesByDay(String dayOfWeek) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE day_of_week = ? ORDER BY start_time";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, dayOfWeek);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            logger.error("Error getting courses by day", e);
        }
        return courses;
    }

    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET subject = ?, teacher_id = ?, class_id = ?, day_of_week = ?, " +
                     "start_time = ?, end_time = ?, duration = ?, room_id = ? WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setString(1, course.getSubject());
            stmt.setInt(2, course.getTeacherId());
            stmt.setInt(3, course.getClassId());
            stmt.setString(4, course.getDayOfWeek());
            stmt.setString(5, course.getStartTime());
            stmt.setString(6, course.getEndTime());
            stmt.setInt(7, course.getDuration());
            stmt.setInt(8, course.getRoomId());
            stmt.setInt(9, course.getId());
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error updating course", e);
        }
        return false;
    }

    public boolean deleteCourse(int id) {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement stmt = dbManager.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            logger.error("Error deleting course", e);
        }
        return false;
    }

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        return new Course(
                rs.getInt("id"),
                rs.getString("subject"),
                rs.getInt("teacher_id"),
                rs.getInt("class_id"),
                rs.getString("day_of_week"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getInt("duration"),
                rs.getInt("room_id")
        );
    }
}
