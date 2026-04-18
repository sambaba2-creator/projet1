package com.university.scheduler.ui.controller;

import com.university.scheduler.business.StatisticsService;
import com.university.scheduler.dao.*;
import com.university.scheduler.model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

import java.util.List;

public class AdminDashboardController {
    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardController.class);

    @FXML private TabPane mainTabPane;
    
    // Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameCol;
    @FXML private TableColumn<User, String> userEmailCol;
    @FXML private TableColumn<User, String> userRoleCol;
    @FXML private TextField newUserNameField;
    @FXML private TextField newUserEmailField;
    @FXML private PasswordField newUserPasswordField;
    @FXML private ComboBox<String> userRoleCombo;

    // Buildings Tab
    @FXML private TableView<Building> buildingsTable;
    @FXML private TableColumn<Building, String> buildingNameCol;
    @FXML private TableColumn<Building, String> buildingLocationCol;
    @FXML private TextField newBuildingNameField;
    @FXML private TextField newBuildingLocationField;
    @FXML private Spinner<Integer> newBuildingFloorsSpinner;

    // Rooms Tab
    @FXML private TableView<Room> roomsTable;
    @FXML private TableColumn<Room, String> roomNumberCol;
    @FXML private TableColumn<Room, Integer> roomCapacityCol;
    @FXML private TableColumn<Room, String> roomTypeCol;
    @FXML private TableColumn<Room, String> roomBuildingCol;
    @FXML private TextField newRoomNumberField;
    @FXML private Spinner<Integer> newRoomCapacitySpinner;
    @FXML private ComboBox<String> newRoomTypeCombo;
    @FXML private ComboBox<String> newRoomBuildingCombo;

    // Statistics Tab
    @FXML private Label totalUsersLabel;
    @FXML private Label totalRoomsLabel;
    @FXML private Label totalBuildingsLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private BarChart<String, Number> statsBarChart;
    @FXML private CategoryAxis statsCategoryAxis;
    @FXML private NumberAxis statsNumberAxis;

    private UserDAO userDAO;
    private BuildingDAO buildingDAO;
    private RoomDAO roomDAO;
    private CourseDAO courseDAO;
    private EquipmentDAO equipmentDAO;
    private StatisticsService statisticsService;

    @FXML
    public void initialize() {
        this.userDAO = new UserDAO();
        this.buildingDAO = new BuildingDAO();
        this.roomDAO = new RoomDAO();
        this.courseDAO = new CourseDAO();
        this.equipmentDAO = new EquipmentDAO();
        this.statisticsService = new StatisticsService();

        initializeUserTab();
        initializeBuildingTab();
        initializeRoomTab();
        initializeStatisticsTab();
        loadData();
    }

    private void initializeUserTab() {
        userNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        userEmailCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEmail()));
        userRoleCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));

        userRoleCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "ADMIN", "SCHEDULE_MANAGER", "TEACHER", "STUDENT"
        ));
    }

    private void initializeBuildingTab() {
        buildingNameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        buildingLocationCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getLocation()));

        newBuildingFloorsSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1)
        );
    }

    private void initializeStatisticsTab() {
        // Configure axes labels
        try {
            statsCategoryAxis.setLabel("Ressource");
            statsNumberAxis.setLabel("Nombre");
        } catch (Exception e) {
            // In case FXML injection not present during tests
        }
    }

    private void initializeRoomTab() {
        roomNumberCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNumber()));
        roomCapacityCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        roomTypeCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        
        // Room Building column - will show building name
        roomBuildingCol.setCellValueFactory(cellData -> {
            Building building = buildingDAO.getBuildingById(cellData.getValue().getBuildingId());
            String buildingName = (building != null) ? building.getName() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(buildingName);
        });

        newRoomCapacitySpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 20)
        );

        newRoomTypeCombo.setItems(javafx.collections.FXCollections.observableArrayList(
                "TD", "TP", "AMPHI", "SEMINAR", "CONFERENCE"
        ));
        
        // Populate buildings combo
        List<Building> buildings = buildingDAO.getAllBuildings();
        List<String> buildingNames = buildings.stream()
                .map(Building::getName)
                .toList();
        newRoomBuildingCombo.setItems(javafx.collections.FXCollections.observableArrayList(buildingNames));
    }

    private void loadData() {
        loadUsers();
        loadBuildings();
        loadRooms();
        loadStatistics();
    }

    private void loadUsers() {
        List<User> users = userDAO.getAllUsers();
        usersTable.setItems(javafx.collections.FXCollections.observableArrayList(users));
    }

    private void loadBuildings() {
        List<Building> buildings = buildingDAO.getAllBuildings();
        buildingsTable.setItems(javafx.collections.FXCollections.observableArrayList(buildings));
    }

    private void loadRooms() {
        List<Room> rooms = roomDAO.getAllRooms();
        roomsTable.setItems(javafx.collections.FXCollections.observableArrayList(rooms));
    }

    private void loadStatistics() {
        totalUsersLabel.setText(String.valueOf(userDAO.getAllUsers().size()));
        totalRoomsLabel.setText(String.valueOf(roomDAO.getAllRooms().size()));
        totalBuildingsLabel.setText(String.valueOf(buildingDAO.getAllBuildings().size()));
        totalCoursesLabel.setText(String.valueOf(courseDAO.getAllCourses().size()));
        updateStatisticsChart();
    }

    private void updateStatisticsChart() {
        if (statsBarChart == null) return;
        int users = userDAO.getAllUsers().size();
        int buildings = buildingDAO.getAllBuildings().size();
        int rooms = roomDAO.getAllRooms().size();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Comptes");
        series.getData().add(new XYChart.Data<>("Utilisateurs", users));
        series.getData().add(new XYChart.Data<>("Bâtiments", buildings));
        series.getData().add(new XYChart.Data<>("Salles", rooms));

        statsBarChart.getData().clear();
        statsBarChart.getData().add(series);
    }

    @FXML
    public void handleAddUser() {
        String name = newUserNameField.getText();
        String email = newUserEmailField.getText();
        String password = newUserPasswordField.getText();
        String role = userRoleCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        User user = new User(name, email, password, role);
        int userId = userDAO.createUser(user);
        boolean userCreated = userId > 0 || userDAO.getUserByEmail(email) != null;

        if (userCreated) {
            logger.info("User created: {}", name);
            clearUserFields();
            loadUsers();
            showAlert("Succès", "Utilisateur créé avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Erreur lors de la création de l'utilisateur", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAddBuilding() {
        String name = newBuildingNameField.getText();
        String location = newBuildingLocationField.getText();
        int floors = newBuildingFloorsSpinner.getValue();

        if (name.isEmpty() || location.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        Building building = new Building(name, location, floors);
        int buildingId = buildingDAO.createBuilding(building);

        if (buildingId > 0) {
            logger.info("Building created: {}", name);
            clearBuildingFields();
            loadBuildings();
            // Refresh room building combo
            List<Building> buildings = buildingDAO.getAllBuildings();
            List<String> buildingNames = buildings.stream()
                    .map(Building::getName)
                    .toList();
            newRoomBuildingCombo.setItems(javafx.collections.FXCollections.observableArrayList(buildingNames));
            showAlert("Succès", "Bâtiment créé avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Erreur lors de la création du bâtiment", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleAddRoom() {
        String number = newRoomNumberField.getText();
        Integer capacity = newRoomCapacitySpinner.getValue();
        String type = newRoomTypeCombo.getValue();
        String buildingName = newRoomBuildingCombo.getValue();

        if (number.isEmpty() || type == null || buildingName == null) {
            showAlert("Erreur", "Tous les champs sont requis", Alert.AlertType.ERROR);
            return;
        }

        // Get building ID from name
        Building building = buildingDAO.getAllBuildings().stream()
                .filter(b -> b.getName().equals(buildingName))
                .findFirst()
                .orElse(null);

        if (building == null) {
            showAlert("Erreur", "Bâtiment invalide", Alert.AlertType.ERROR);
            return;
        }

        Room room = new Room(number, capacity, type, building.getId());
        int roomId = roomDAO.createRoom(room);

        if (roomId > 0) {
            logger.info("Room created: {}", number);
            clearRoomFields();
            loadRooms();
            showAlert("Succès", "Salle créée avec succès", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Erreur", "Erreur lors de la création de la salle", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void handleDeleteUser() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Erreur", "Sélectionnez un utilisateur à supprimer", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (userDAO.deleteUser(selectedUser.getId())) {
                logger.info("User deleted: {}", selectedUser.getName());
                loadUsers();
                showAlert("Succès", "Utilisateur supprimé", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleDeleteBuilding() {
        Building selectedBuilding = buildingsTable.getSelectionModel().getSelectedItem();
        if (selectedBuilding == null) {
            showAlert("Erreur", "Sélectionnez un bâtiment à supprimer", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce bâtiment?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (buildingDAO.deleteBuilding(selectedBuilding.getId())) {
                logger.info("Building deleted: {}", selectedBuilding.getName());
                loadBuildings();
                // Refresh room building combo
                List<Building> buildings = buildingDAO.getAllBuildings();
                List<String> buildingNames = buildings.stream()
                        .map(Building::getName)
                        .toList();
                newRoomBuildingCombo.setItems(javafx.collections.FXCollections.observableArrayList(buildingNames));
                showAlert("Succès", "Bâtiment supprimé", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void handleDeleteRoom() {
        Room selectedRoom = roomsTable.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showAlert("Erreur", "Sélectionnez une salle à supprimer", Alert.AlertType.ERROR);
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette salle?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            if (roomDAO.deleteRoom(selectedRoom.getId())) {
                logger.info("Room deleted: {}", selectedRoom.getNumber());
                loadRooms();
                showAlert("Succès", "Salle supprimée", Alert.AlertType.INFORMATION);
            } else {
                showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
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

    private void clearUserFields() {
        newUserNameField.clear();
        newUserEmailField.clear();
        newUserPasswordField.clear();
        userRoleCombo.setValue(null);
    }

    private void clearBuildingFields() {
        newBuildingNameField.clear();
        newBuildingLocationField.clear();
        newBuildingFloorsSpinner.getValueFactory().setValue(1);
    }

    private void clearRoomFields() {
        newRoomNumberField.clear();
        newRoomCapacitySpinner.getValueFactory().setValue(20);
        newRoomTypeCombo.setValue(null);
        newRoomBuildingCombo.setValue(null);
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
