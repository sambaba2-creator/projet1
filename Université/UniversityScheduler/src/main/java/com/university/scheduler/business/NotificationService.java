package com.university.scheduler.business;

import com.university.scheduler.dao.*;
import com.university.scheduler.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public NotificationService() {
    }

    /**
     * Send notification to user about room reservation conflict
     */
    public void notifyConflict(User user, String message) {
        logger.info("Sending conflict notification to {}: {}", user.getEmail(), message);
        // In a real application, this would send an email
        String fullMessage = "Conflit détecté: " + message;
        sendEmailNotification(user.getEmail(), "Conflit de réservation", fullMessage);
    }

    /**
     * Send notification about room change
     */
    public void notifyRoomChange(User user, String oldRoom, String newRoom) {
        String message = "Votre réservation a été changée de la salle " + oldRoom + " à la salle " + newRoom;
        logger.info("Sending room change notification to {}: {}", user.getEmail(), message);
        sendEmailNotification(user.getEmail(), "Changement de salle", message);
    }

    /**
     * Send reservation confirmation
     */
    public void notifyReservationConfirmed(User user, Room room, String date, String time) {
        String message = "Votre réservation de la salle " + room.getNumber() + " pour le " + date + " à " + time + " est confirmée.";
        logger.info("Sending confirmation to {}: {}", user.getEmail(), message);
        sendEmailNotification(user.getEmail(), "Confirmation de réservation", message);
    }

    /**
     * Send course schedule notification
     */
    public void notifyCourseScheduled(User teacher, Course course, Room room) {
        String message = "Le cours " + course.getSubject() + " a été planifié dans la salle " + room.getNumber() +
                         " le " + course.getDayOfWeek() + " de " + course.getStartTime() + " à " + course.getEndTime();
        logger.info("Sending course schedule notification to {}: {}", teacher.getEmail(), message);
        sendEmailNotification(teacher.getEmail(), "Cours programmé", message);
    }

    /**
     * Send reservation reminder
     */
    public void sendReservationReminder(User user, Room room, String date, String time) {
        String message = "Rappel: Vous avez une réservation de salle " + room.getNumber() + " le " + date + " à " + time;
        logger.info("Sending reminder to {}: {}", user.getEmail(), message);
        sendEmailNotification(user.getEmail(), "Rappel de réservation", message);
    }

    /**
     * Send end of reservation notification
     */
    public void notifyReservationEnd(User user, Room room) {
        String message = "Votre réservation de la salle " + room.getNumber() + " se termine.";
        logger.info("Sending end notification to {}: {}", user.getEmail(), message);
        sendEmailNotification(user.getEmail(), "Fin de réservation", message);
    }

    /**
     * Send technical problem report
     */
    public void notifyTechnicalProblem(User admin, Room room, String problem) {
        String message = "Problème technique signalé dans la salle " + room.getNumber() + ": " + problem;
        logger.info("Sending problem notification to {}: {}", admin.getEmail(), message);
        sendEmailNotification(admin.getEmail(), "Problème technique signalé", message);
    }

    /**
     * Send email notification (mock implementation)
     */
    private void sendEmailNotification(String email, String subject, String message) {
        // In a real application, this would:
        // 1. Connect to SMTP server
        // 2. Send actual email
        // For now, we just log it
        logger.info("EMAIL NOTIFICATION");
        logger.info("To: {}", email);
        logger.info("Subject: {}", subject);
        logger.info("Message: {}", message);
    }

    /**
     * Bulk notify teachers about schedule changes
     */
    public void notifyTeachersOfScheduleChange(List<Integer> teacherIds, String message) {
        UserDAO userDAO = new UserDAO();
        for (Integer teacherId : teacherIds) {
            User teacher = userDAO.getUserById(teacherId);
            if (teacher != null) {
                notifyConflict(teacher, message);
            }
        }
    }

    /**
     * Notify admin of multiple conflicts
     */
    public void notifyAdminOfConflicts(List<String> conflicts) {
        UserDAO userDAO = new UserDAO();
        List<User> admins = userDAO.getUsersByRole("ADMIN");

        StringBuilder message = new StringBuilder("Conflits détectés:\n");
        for (String conflict : conflicts) {
            message.append("- ").append(conflict).append("\n");
        }

        for (User admin : admins) {
            sendEmailNotification(admin.getEmail(), "Conflits d'emploi du temps", message.toString());
        }
    }
}
