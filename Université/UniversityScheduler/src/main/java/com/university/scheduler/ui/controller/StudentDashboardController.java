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

public class StudentDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(StudentDashboardController.class);

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

    private CourseDAO courseDAO;
    private RoomDAO roomDAO;
    private ClassDAO classDAO;
    private RoomSearchService roomSearchService;
    private int currentStudentId;
    private int currentClassId;

    @FXML
    public void initialize() {
        this.courseDAO = new CourseDAO();
        this.roomDAO = new RoomDAO();
        this.classDAO = new ClassDAO();
        this.roomSearchService = new RoomSearchService();

        // Get current student's class ID (in real app, pass from login)
        this.currentStudentId = 6; // TODO: Get from session
        this.currentClassId = 1; // TODO: Get from session

        initializeScheduleTab();
        initializeSearchTab();
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

    private void loadData() {
        loadSchedule();
    }

    private void loadSchedule() {
        List<Course> courses = courseDAO.getCoursesByClass(currentClassId);
        scheduleTable.setItems(javafx.collections.FXCollections.observableArrayList(courses));
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
