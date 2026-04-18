package com.university.scheduler.ui.controller;

import com.university.scheduler.business.ConflictDetectionService;
import com.university.scheduler.business.RoomSearchService;
import com.university.scheduler.business.StatisticsService;
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

public class ManagerDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(ManagerDashboardController.class);

    @FXML private TabPane mainTabPane;
    
    // Courses Tab
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseSubjectCol;
    @FXML private TableColumn<Course, String> courseDayCol;
    @FXML private TableColumn<Course, String> courseTimeCol;
    @FXML private TextField courseSubjectField;
    @FXML private ComboBox<String> courseTeacherCombo;
    @FXML private ComboBox<String> courseClassCombo;
    @FXML private ComboBox<String> courseDayCombo;
    @FXML private TextField courseStartTimeField;
    @FXML private Spinner<Integer> courseDurationSpinner;
    @FXML private ComboBox<String> courseRoomCombo;

    // Room Search Tab
    @FXML private Spinner<Integer> searchCapacitySpinner;
    @FXML private ComboBox<String> searchRoomTypeCombo;
    @FXML private DatePicker searchDatePicker;
    @FXML private TextField searchStartTimeField;
    @FXML private TextField searchEndTimeField;
    @FXML private TableView<Room> availableRoomsTable;
    @FXML private TableColumn<Room, String> availRoomNumberCol;
    @FXML private TableColumn<Room, Integer> availRoomCapacityCol;

    // Statistics Tab
    @FXML private Label avgOccupancyLabel;
    @FXML private Label criticalRoomsLabel;
    @FXML private Label underutilizedRoomsLabel;

    // Schedule Management Tab
    @FXML private TableView<Course> scheduleTable;
    @FXML private TableColumn<Course, String> scheduleSubjectCol;
    @FXML private TableColumn<Course, String> scheduleTeacherCol;
    @FXML private TableColumn<Course, String> scheduleClassCol;
    @FXML private TableColumn<Course, String> scheduleDayCol;
    @FXML private TableColumn<Course, String> scheduleTimeCol;
    @FXML private TableColumn<Course, String> scheduleRoomCol;
    @FXML private TableColumn<Course, Void> scheduleActionCol;
    @FXML private TextField scheduleSubjectField;
    @FXML private ComboBox<String> scheduleTeacherCombo;
    @FXML private ComboBox<String> scheduleClassCombo;
    @FXML private ComboBox<String> scheduleDayCombo;
    @FXML private TextField scheduleStartTimeField;
    @FXML private Spinner<Integer> scheduleDurationSpinner;
    @FXML private ComboBox<String> scheduleRoomCombo;

    private CourseDAO courseDAO;
    private UserDAO userDAO;
    private ClassDAO classDAO;
    private RoomDAO roomDAO;
    private ConflictDetectionService conflictService;
    private RoomSearchService roomSearchService;
    private StatisticsService statisticsService;

    @FXML
    public void initialize() {
        this.courseDAO = new CourseDAO();
        this.userDAO = new UserDAO();
        this.classDAO = new ClassDAO();
        this.roomDAO = new RoomDAO();
        this.conflictService = new ConflictDetectionService();
        this.roomSearchService = new RoomSearchService();
        this.statisticsService = new StatisticsService();

        initializeCourseTab();
        initializeSearchTab();
        initializeStatisticsTab();
        initializeScheduleTab();
        loadData();
    }

    private void initializeCourseTab() {
        courseSubjectCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubject()));
        courseDayCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDayOfWeek()));
        courseTimeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartTime() + " - " + cellData.getValue().getEndTime()
        ));

        courseDayCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        ));

        courseDurationSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 300, 60, 30)
        );
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

    private void initializeStatisticsTab() {
        // Initialize statistics display
    }

    private void initializeScheduleTab() {
        scheduleSubjectCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSubject()));
        scheduleTeacherCol.setCellValueFactory(cellData -> {
            User teacher = userDAO.getUserById(cellData.getValue().getTeacherId());
            String teacherName = (teacher != null) ? teacher.getName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(teacherName);
        });
        scheduleClassCol.setCellValueFactory(cellData -> {
            com.university.scheduler.model.AcademicClass clazz = classDAO.getClassById(cellData.getValue().getClassId());
            String className = (clazz != null) ? clazz.getName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(className);
        });
        scheduleDayCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDayOfWeek()));
        scheduleTimeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartTime() + " - " + cellData.getValue().getEndTime()
        ));
        scheduleRoomCol.setCellValueFactory(cellData -> {
            Room room = roomDAO.getRoomById(cellData.getValue().getRoomId());
            String roomNumber = (room != null) ? room.getNumber() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(roomNumber);
        });

        // Initialize delete button column
        scheduleActionCol.setCellFactory(param -> new javafx.scene.control.TableCell<Course, Void>() {
            private final Button deleteBtn = new Button("Supprimer");

            {
                deleteBtn.setStyle("-fx-padding: 5 10; -fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    if (course != null) {
                        handleDeleteSchedule(course);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        scheduleDayCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"
        ));

        scheduleDurationSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 300, 60, 30)
        );

        // Initialize ComboBox data for schedule tab
        loadScheduleFormData();
    }

    private void loadScheduleFormData() {
        // Load teachers
        List<User> teachers = userDAO.getUsersByRole("TEACHER");
        List<String> teacherNames = teachers.stream()
                .map(User::getName)
                .toList();
        scheduleTeacherCombo.setItems(javafx.collections.FXCollections.observableArrayList(teacherNames));

        // Load classes
        List<com.university.scheduler.model.AcademicClass> classes = classDAO.getAllClasses();
        List<String> classNames = classes.stream()
                .map(com.university.scheduler.model.AcademicClass::getName)
                .toList();
        scheduleClassCombo.setItems(javafx.collections.FXCollections.observableArrayList(classNames));

        // Load rooms
        List<Room> rooms = roomDAO.getAllRooms();
        List<String> roomNumbers = rooms.stream()
                .map(Room::getNumber)
                .toList();
        scheduleRoomCombo.setItems(javafx.collections.FXCollections.observableArrayList(roomNumbers));
    }

    private void loadData() {
        loadCourses();
        loadTeachers();
        loadClasses();
        loadRooms();
        loadStatistics();
        loadSchedule();
    }

    private void loadCourses() {
        List<Course> courses = courseDAO.getAllCourses();
        coursesTable.setItems(javafx.collections.FXCollections.observableArrayList(courses));
    }

    private void loadTeachers() {
        List<User> teachers = userDAO.getUsersByRole("TEACHER");
        List<String> teacherNames = teachers.stream()
                .map(User::getName)
                .toList();
        courseTeacherCombo.setItems(javafx.collections.FXCollections.observableArrayList(teacherNames));
    }

    private void loadClasses() {
        List<com.university.scheduler.model.AcademicClass> classes = classDAO.getAllClasses();
        List<String> classNames = classes.stream()
                .map(com.university.scheduler.model.AcademicClass::getName)
                .toList();
        courseClassCombo.setItems(javafx.collections.FXCollections.observableArrayList(classNames));
    }

    private void loadRooms() {
        List<Room> rooms = roomDAO.getAllRooms();
        List<String> roomNumbers = rooms.stream()
                .map(Room::getNumber)
                .toList();
        courseRoomCombo.setItems(javafx.collections.FXCollections.observableArrayList(roomNumbers));
    }

    private void loadStatistics() {
        String today = java.time.LocalDate.now().toString();
        List<StatisticsService.RoomStatistic> criticalRooms = statisticsService.getCriticalRooms(today);
        List<StatisticsService.RoomStatistic> underutilizedRooms = statisticsService.getUnderutilizedRooms(today);

        criticalRoomsLabel.setText(String.valueOf(criticalRooms.size()));
        underutilizedRoomsLabel.setText(String.valueOf(underutilizedRooms.size()));
    }

    @FXML
    public void handleAddCourse() {
        String subject = courseSubjectField.getText();
        String teacherName = courseTeacherCombo.getValue();
        String className = courseClassCombo.getValue();
        String day = courseDayCombo.getValue();
        String startTime = courseStartTimeField.getText();
        int duration = courseDurationSpinner.getValue();
        String roomNumber = courseRoomCombo.getValue();

        if (subject.isEmpty() || teacherName == null || className == null || day == null || 
            startTime.isEmpty() || roomNumber == null) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        // Get IDs from names
        User teacher = userDAO.getUsersByRole("TEACHER").stream()
                .filter(u -> u.getName().equals(teacherName))
                .findFirst()
                .orElse(null);
        com.university.scheduler.model.AcademicClass clazz = classDAO.getClassByName(className);
        Room room = roomDAO.getAllRooms().stream()
                .filter(r -> r.getNumber().equals(roomNumber))
                .findFirst()
                .orElse(null);

        if (teacher == null || clazz == null || room == null) {
            showAlert("Erreur", "Données invalides", Alert.AlertType.ERROR);
            return;
        }

        // Create end time
        String endTime = calculateEndTime(startTime, duration);
        
        Course course = new Course(subject, teacher.getId(), clazz.getId(), day, startTime, duration, room.getId());
        course.setEndTime(endTime);

        // Check for conflicts
        if (conflictService.hasTeacherConflict(course) || conflictService.hasClassConflict(course)) {
            showAlert("Erreur", "Conflit détecté avec un autre cours", Alert.AlertType.ERROR);
            return;
        }

        int courseId = courseDAO.createCourse(course);
        if (courseId > 0) {
            logger.info("Course created: {}", subject);
            clearCourseFields();
            loadCourses();
            showAlert("Succès", "Cours créé avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Erreur lors de la création du cours", Alert.AlertType.ERROR);
        }
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
    public void handleAddSchedule() {
        String subject = scheduleSubjectField.getText();
        String teacherName = scheduleTeacherCombo.getValue();
        String className = scheduleClassCombo.getValue();
        String day = scheduleDayCombo.getValue();
        String startTime = scheduleStartTimeField.getText();
        int duration = scheduleDurationSpinner.getValue();
        String roomNumber = scheduleRoomCombo.getValue();

        if (subject.isEmpty() || teacherName == null || className == null || day == null || 
            startTime.isEmpty() || roomNumber == null) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        // Get IDs from names
        User teacher = userDAO.getUsersByRole("TEACHER").stream()
                .filter(u -> u.getName().equals(teacherName))
                .findFirst()
                .orElse(null);
        com.university.scheduler.model.AcademicClass clazz = classDAO.getClassByName(className);
        Room room = roomDAO.getAllRooms().stream()
                .filter(r -> r.getNumber().equals(roomNumber))
                .findFirst()
                .orElse(null);

        if (teacher == null || clazz == null || room == null) {
            showAlert("Erreur", "Données invalides", Alert.AlertType.ERROR);
            return;
        }

        // Create end time
        String endTime = calculateEndTime(startTime, duration);
        
        Course course = new Course(subject, teacher.getId(), clazz.getId(), day, startTime, duration, room.getId());
        course.setEndTime(endTime);

        // Create the course without conflict checking (manager manages conflicts manually)
        int courseId = courseDAO.createCourse(course);
        if (courseId > 0) {
            logger.info("Course created: {} for {}", subject, teacherName);
            clearScheduleFields();
            loadSchedule();
            showAlert("Succès", "Cours ajouté à l'emploi du temps avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Erreur lors de l'ajout du cours", Alert.AlertType.ERROR);
        }
    }

    private void loadSchedule() {
        List<Course> courses = courseDAO.getAllCourses();
        scheduleTable.setItems(javafx.collections.FXCollections.observableArrayList(courses));
    }

    private void clearScheduleFields() {
        scheduleSubjectField.clear();
        scheduleTeacherCombo.setValue(null);
        scheduleClassCombo.setValue(null);
        scheduleDayCombo.setValue(null);
        scheduleStartTimeField.clear();
        scheduleDurationSpinner.getValueFactory().setValue(60);
        scheduleRoomCombo.setValue(null);
    }

    private void handleDeleteSchedule(Course course) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le cours '" + course.getSubject() + "' ?");
        
        java.util.Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = courseDAO.deleteCourse(course.getId());
            if (deleted) {
                logger.info("Course deleted: {}", course.getSubject());
                loadSchedule();
                showAlert("Succès", "Cours supprimé avec succès", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la suppression du cours", Alert.AlertType.ERROR);
            }
        }
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

    private String calculateEndTime(String startTime, int duration) {
        try {
            String[] parts = startTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);

            minutes += duration;
            hours += minutes / 60;
            minutes %= 60;

            return String.format("%02d:%02d", hours, minutes);
        } catch (Exception e) {
            return startTime;
        }
    }

    private void clearCourseFields() {
        courseSubjectField.clear();
        courseTeacherCombo.setValue(null);
        courseClassCombo.setValue(null);
        courseDayCombo.setValue(null);
        courseStartTimeField.clear();
        courseDurationSpinner.getValueFactory().setValue(60);
        courseRoomCombo.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
