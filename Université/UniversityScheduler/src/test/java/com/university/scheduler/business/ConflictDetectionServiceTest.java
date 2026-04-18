package com.university.scheduler.business;

import com.university.scheduler.dao.CourseDAO;
import com.university.scheduler.dao.ReservationDAO;
import com.university.scheduler.dao.RoomDAO;
import com.university.scheduler.dao.UserDAO;
import com.university.scheduler.model.Course;
import com.university.scheduler.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConflictDetectionServiceTest {
    private ConflictDetectionService service;

    @BeforeEach
    public void setUp() {
        // provide stub DAOs with controlled data
        service = new ConflictDetectionService(
                new ReservationDAO() {
                    @Override
                    public List<Reservation> getReservationsByRoomAndDate(int roomId, String date) {
                        return Collections.emptyList();
                    }
                },
                new CourseDAO() {
                    @Override
                    public List<Course> getCoursesByTeacher(int teacherId) {
                        // one existing course on MONDAY 11:00-13:00
                        Course existing = new Course(2, "C2", teacherId, 0, "MONDAY", "11:00", "13:00", 120, 0);
                        return Collections.singletonList(existing);
                    }

                    @Override
                    public List<Course> getCoursesByClass(int classId) {
                        // no courses by default
                        return Collections.emptyList();
                    }
                },
                new UserDAO(),
                new RoomDAO()
        );
    }

    @Test
    public void testHasRoomConflict_noConflict() {
        Reservation r = new Reservation(1, 1, "2023-01-01", "10:00", "12:00", "LECTURE", 0, 0, "", "BOOKED");
        List<ConflictDetectionService.ConflictDetails> conflicts = service.checkForConflicts(r);
        assertTrue(conflicts.isEmpty(), "There should be no conflicts when DAO returns empty list");
    }

    @Test
    public void testIsRoomAvailable_empty() {
        boolean available = service.isRoomAvailable(1, "2023-01-01", "08:00", "09:00");
        assertTrue(available);
    }

    @Test
    public void testHasTeacherConflict_noConflict() {
        Course course = new Course(1, "C1", 0, 999, "TUESDAY", "10:00", "12:00", 120, 0);
        // different teacher and different day -> no conflict
        assertFalse(service.hasTeacherConflict(course));
    }

    @Test
    public void testHasTeacherConflict_conflict() {
        Course course = new Course(1, "C1", 0, 123, "MONDAY", "10:30", "12:30", 120, 0);
        // same teacher 123 and overlapping time
        assertTrue(service.hasTeacherConflict(course));
    }

    @Test
    public void testHasClassConflict_noConflict() {
        Course course = new Course(1, "C1", 123, 0, "TUESDAY", "10:00", "12:00", 120, 0);
        assertFalse(service.hasClassConflict(course));
    }
}
