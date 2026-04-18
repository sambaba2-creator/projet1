package com.university.scheduler.ui.controller;

import com.university.scheduler.business.RoomSearchService;
import com.university.scheduler.dao.*;
import com.university.scheduler.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class TeacherDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(TeacherDashboardController.class);

    @FXML private TabPane mainTabPane;
    
    // Schedule Tab
    @FXML private TableView<Course> scheduleTable;
    @FXML private TableColumn<Course, String> scheduleSubjectCol;
    @FXML private TableColumn<Course, String> scheduleDayCol;
    @FXML private TableColumn<Course, String> scheduleTimeCol;
    @FXML private TableColumn<Course, String> scheduleRoomCol;

    // Room Search Tab
    @FXML private Spinner<Integer> searchCapacitySpinner;
    @FXML private ComboBox<String> searchRoomTypeCombo;
    @FXML private DatePicker searchDatePicker;
    @FXML private TextField searchStartTimeField;
    @FXML private TextField searchEndTimeField;
    @FXML private TableView<Room> availableRoomsTable;
    @FXML private TableColumn<Room, String> availRoomNumberCol;
    @FXML private TableColumn<Room, Integer> availRoomCapacityCol;
    @FXML private TextArea reasonTextArea;

    // Report Tab
    @FXML private TextArea reportTextArea;
    @FXML private Label totalCoursesLabel;
    @FXML private Label totalHoursLabel;

    private CourseDAO courseDAO;
    private RoomDAO roomDAO;
    private ReservationDAO reservationDAO;
    private UserDAO userDAO;
    private RoomSearchService roomSearchService;
    private int currentTeacherId;

    @FXML
    public void initialize() {
        this.courseDAO = new CourseDAO();
        this.roomDAO = new RoomDAO();
        this.reservationDAO = new ReservationDAO();
        this.userDAO = new UserDAO();
        this.roomSearchService = new RoomSearchService();

        // Get current teacher ID from session (in real app, pass from login)
        this.currentTeacherId = 2; // TODO: Get from session

        initializeScheduleTab();
        initializeSearchTab();
        initializeReportTab();
        loadData();
    }

    private void initializeScheduleTab() {
        scheduleSubjectCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubject()));
        scheduleDayCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDayOfWeek()));
        scheduleTimeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartTime() + " - " + cellData.getValue().getEndTime()
        ));
        scheduleRoomCol.setCellValueFactory(cellData -> {
            Room room = roomDAO.getRoomById(cellData.getValue().getRoomId());
            String roomName = (room != null) ? room.getNumber() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(roomName);
        });
    }

    private void initializeSearchTab() {
        searchCapacitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 20)
        );

        searchRoomTypeCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "TD", "TP", "AMPHI", "SEMINAR", "CONFERENCE"
        ));

        availRoomNumberCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumber()));
        availRoomCapacityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
    }

    private void initializeReportTab() {
        reportTextArea.setEditable(false);
    }

    private void loadData() {
        loadSchedule();
        loadStatistics();
    }

    private void loadSchedule() {
        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacherId);
        scheduleTable.setItems(javafx.collections.FXCollections.observableArrayList(courses));
    }

    private void loadStatistics() {
        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacherId);
        double totalHours = 0;
        for (Course c : courses) {
            totalHours += c.getDuration() / 60.0;
        }

        totalCoursesLabel.setText(String.valueOf(courses.size()));
        totalHoursLabel.setText(String.format("%.1f", totalHours));
    }

    @FXML
    public void handleSearchRooms() {
        int minCapacity = searchCapacitySpinner.getValue();
        String roomType = searchRoomTypeCombo.getValue();
        LocalDate date = searchDatePicker.getValue();
        String startTime = searchStartTimeField.getText();
        String endTime = searchEndTimeField.getText();

        RoomSearchService.RoomSearchCriteria criteria = new RoomSearchService.RoomSearchCriteria();
        criteria.setMinCapacity(minCapacity);
        criteria.setRoomType(roomType);
        if (date != null) {
            criteria.setDate(date.toString());
        }
        criteria.setStartTime(startTime);
        criteria.setEndTime(endTime);

        List<Room> availableRooms = roomSearchService.searchAvailableRooms(criteria);
        availableRoomsTable.setItems(javafx.collections.FXCollections.observableArrayList(availableRooms));
    }

    @FXML
    public void handleReserveRoom() {
        Room selectedRoom = availableRoomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Erreur", "Veuillez sélectionner une salle", Alert.AlertType.ERROR);
            return;
        }

        LocalDate date = searchDatePicker.getValue();
        String startTime = searchStartTimeField.getText();
        String endTime = searchEndTimeField.getText();
        String reason = reasonTextArea.getText();

        if (date == null || startTime.isEmpty() || endTime.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        Reservation reservation = new Reservation(selectedRoom.getId(), date.toString(), startTime, endTime, "EVENT", currentTeacherId);
        reservation.setReason(reason);

        int resId = reservationDAO.createReservation(reservation);
        if (resId > 0) {
            logger.info("Reservation created for room {}", selectedRoom.getNumber());
            showAlert("Succès", "Salle réservée avec succès", Alert.AlertType.INFORMATION);
            handleSearchRooms(); // Refresh search results
        } else {
            showAlert("Erreur", "Erreur lors de la réservation", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleGenerateReport() {
        List<Course> courses = courseDAO.getCoursesByTeacher(currentTeacherId);
        StringBuilder report = new StringBuilder();

        report.append("RAPPORT D'EMPLOI DU TEMPS\n\n");
        report.append("Nombre de cours: ").append(courses.size()).append("\n");

        double totalHours = 0;
        for (Course c : courses) {
            totalHours += c.getDuration() / 60.0;
        }
        report.append("Heures totales: ").append(String.format("%.1f", totalHours)).append("\n\n");

        report.append("DÉTAIL DES COURS:\n");
        report.append("─".repeat(80)).append("\n");

        for (Course c : courses) {
            report.append("Matière: ").append(c.getSubject()).append("\n");
            report.append("Jour: ").append(c.getDayOfWeek()).append("\n");
            report.append("Heure: ").append(c.getStartTime()).append(" - ").append(c.getEndTime()).append("\n");
            report.append("Salle: ").append(c.getRoomId()).append("\n");
            report.append("─".repeat(80)).append("\n");
        }

        reportTextArea.setText(report.toString());
    }

    @FXML
    public void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("University Scheduler - Connexion");
            loginStage.show();
        } catch (IOException e) {
            logger.error("Impossible de charger l'écran de connexion", e);
            showAlert("Erreur", "Impossible de charger l'écran de connexion", Alert.AlertType.ERROR);
            return;
        }

        Stage stage = (Stage) mainTabPane.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
