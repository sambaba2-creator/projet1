package com.university.scheduler.business;

import com.university.scheduler.dao.RoomDAO;
import com.university.scheduler.dao.ReservationDAO;
import com.university.scheduler.dao.CourseDAO;
import com.university.scheduler.dao.EquipmentDAO;
import com.university.scheduler.model.Equipment;
import com.university.scheduler.model.Reservation;
import com.university.scheduler.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoomSearchServiceTest {
    private RoomSearchService service;

    @BeforeEach
    public void setUp() {
        // stub DAOs
        service = new RoomSearchService(
                new RoomDAO() {
                    @Override
                    public List<Room> getAllRooms() {
                        // create two rooms
                        Room r1 = new Room(1, "R1", 20, "Lecture", 0);
                        r1.setEquipmentIds(List.of(1, 2));
                        Room r2 = new Room(2, "R2", 10, "Lab", 0);
                        r2.setEquipmentIds(List.of(2));
                        return List.of(r1, r2);
                    }
                },
                new ReservationDAO() {
                    @Override
                    public List<Reservation> getReservationsByRoomAndDate(int roomId, String date) {
                        if (roomId == 1 && "2023-01-01".equals(date)) {
                            // room 1 is booked from 9 to 11
                            return List.of(new Reservation(1, roomId, date, "09:00", "11:00", "", 0, 0, "", "BOOKED"));
                        }
                        return List.of();
                    }
                },
                new CourseDAO(),
                new EquipmentDAO() {
                    @Override
                    public Equipment getEquipmentById(int id) {
                        return new Equipment(id, "Equip" + id);
                    }
                }
        );
    }

    @Test
    public void testSearchAvailableRooms_capacityAndType() {
        RoomSearchService.RoomSearchCriteria crit = new RoomSearchService.RoomSearchCriteria();
        crit.setMinCapacity(15);
        crit.setRoomType("Lecture");

        List<Room> result = service.searchAvailableRooms(crit);
        // room1 should be returned if date/time not set (available by default but capacity 20)
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }

    @Test
    public void testSearchAvailableRooms_timeConflict() {
        RoomSearchService.RoomSearchCriteria crit = new RoomSearchService.RoomSearchCriteria();
        crit.setDate("2023-01-01");
        crit.setStartTime("10:00");
        crit.setEndTime("12:00");

        List<Room> result = service.searchAvailableRooms(crit);
        // room1 is booked in that slot, r2 only has capacity 10 < default min 1? default min 1 so r2 qualifies but same day/time has no reservation
        assertEquals(1, result.size());
        assertEquals(2, result.get(0).getId());
    }

    @Test
    public void testFindBestRoom_exactCapacity() {
        RoomSearchService.RoomSearchCriteria crit = new RoomSearchService.RoomSearchCriteria();
        crit.setMinCapacity(10);
        crit.setRequiredEquipments(List.of(2));

        Room best = service.findBestRoom(crit);
        assertNotNull(best);
        assertEquals(2, best.getId());
    }
}
