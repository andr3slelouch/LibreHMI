package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.util.DBConnection;
import andrade.luis.hmiethernetip.views.*;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HMIApp extends Application {
    private Stage mainStage;
    Logger logger
            = Logger.getLogger(
            HMIApp.class.getName());

    public ArrayList<HMIScene> getPages() {
        return pages;
    }

    private final ArrayList<HMIScene> pages = new ArrayList<>();
    private ArrayList<HMIScene> selectedPages = new ArrayList<>();
    private ArrayList<Stage> createdWindows = new ArrayList<>();
    private ArrayList<String> pagesTitles = new ArrayList<>();
    private static final String HMI_TITLE = "HMI: ";
    private HMIUser user;

    @Override
    public void start(Stage stage) {

        mainStage = stage;
        generateDatabase();
        HMIScene scene = generatePage("Página 1", "", Color.WHITESMOKE);
        addScene(scene);
        mainStage.setTitle(HMI_TITLE + scene.getTitle());
        mainStage.setScene(scene);
        mainStage.show();
    }

    private HMIScene generatePage(String sceneTitle, String sceneCommentary, Color backgroundColor) {
        var canvas = new Canvas(300, 300);
        HMICanvas root = new HMICanvas();
        root.getChildren().add(canvas);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        ScrollPane stackPane = new ScrollPane();
        stackPane.setContent(root);
        HMIScene scene = new HMIScene(stackPane, root, sceneTitle, sceneCommentary, bounds.getWidth(), bounds.getHeight(), backgroundColor);
        Button rectangleBtn = new Button("Rectangle");
        rectangleBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Rectangle");
        });
        Button systemDateTimeLabelBtn = new Button("DateTime Label");
        systemDateTimeLabelBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("SystemDateTimeLabel");
        });
        Button textBtn = new Button("Text");
        textBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Text");
        });
        Button buttonBtn = new Button("Button");
        buttonBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Button");
        });
        Button sliderBtn = new Button("Slider");
        sliderBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Slider");
        });
        Button textFieldBtn = new Button("TextField");
        textFieldBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("TextField");
        });
        Button manageUsersBtn = new Button("Manage Users");
        Button registerUserBtn = new Button("Register");
        Button logIntUserBtn = new Button("Log In");
        Button saveBtn = new Button("Guardar Propiedades");
        Button showSelectedWindowsBtn = new Button("Show");
        Button playBtn = new Button("Play");
        Button stopBtn = new Button("Stop");
        Button defaultBtn = new Button("Default");
        HBox hbox = new HBox(rectangleBtn, systemDateTimeLabelBtn, textBtn, buttonBtn);
        HBox secondHBox = new HBox(sliderBtn, textFieldBtn, manageUsersBtn, registerUserBtn);
        HBox thirdHBox = new HBox(logIntUserBtn, saveBtn, showSelectedWindowsBtn);
        HBox fourthHBox = new HBox(playBtn, stopBtn, defaultBtn);

        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of(scene.getTitle()));
        ListView<String> listViewReference = new ListView<>();
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);

        VBox vbox = new VBox(hbox, secondHBox, thirdHBox, fourthHBox, scene.getListViewReference());
        root.getChildren().add(vbox);

        manageUsersBtn.setOnMouseClicked(mouseEvent -> {
            ManageUsersWindow manageUsersWindow = new ManageUsersWindow(this.user);
            manageUsersWindow.show();
        });
        registerUserBtn.setOnMouseClicked(mouseEvent -> {
            SignUpWindow signUpWindow = new SignUpWindow(null);
            signUpWindow.show();
        });
        logIntUserBtn.setOnAction(mouseEvent -> {
            LogInWindow logInWindow = new LogInWindow();
            logInWindow.showAndWait();
            user = logInWindow.getLoggedUser();
        });
        saveBtn.setOnAction(mouseEvent -> {
            SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
            saveDatabaseCredentialsWindow.show();
        });
        showSelectedWindowsBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Image");

        });
        playBtn.setOnAction(mouseEvent -> enableInputRepresentations("Play"));
        stopBtn.setOnAction(mouseEvent -> enableInputRepresentations("Stop"));
        defaultBtn.setOnAction(mouseEvent -> enableInputRepresentations("Default"));

        scene.setHmiApp(this);

        return scene;
    }

    public void enableInputRepresentations(String value) {
        for (HMIScene page : this.pages) {
            for (int i = 0; i < page.getCanvas().getShapeArrayList().size(); i++) {
                page.getCanvas().getShapeArrayList().get(i).setEnable(value);
            }
        }
    }

    public void generateStagesForPages(ArrayList<HMIScene> selectedPages) {
        boolean mainStageWasUpdated = false;
        if (!selectedPages.isEmpty()) {
            for (HMIScene selectedPage : selectedPages) {
                int index = getIndexForStageWithScene(selectedPage.getTitle());
                if (index == -1) {
                    if (!mainStageWasUpdated) {
                        changeSelectedScene(selectedPage.getTitle());
                        mainStageWasUpdated = true;
                    } else {
                        generateStage(selectedPage);
                    }
                } else {
                    if(createdWindows.get(index).isShowing()){
                        createdWindows.get(index).requestFocus();
                    }else{
                        createdWindows.get(index).show();
                    }
                }
            }
        }
    }

    private void generateStage(HMIScene scene){
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(HMI_TITLE + scene.getTitle());
        stage.show();
        createdWindows.add(stage);
    }

    private int getIndexForStageWithScene(String title) {
        int index = -1;
        for (int i = 0; i < createdWindows.size(); i++) {
            if (((HMIScene) createdWindows.get(i).getScene()).getTitle().equals(title)) {
                index = i;
            }
        }
        return index;
    }

    public void changeSelectedScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        mainStage.setScene(pages.get(index));
        mainStage.setTitle(HMI_TITLE + pages.get(index).getTitle());
        pages.get(index).getListViewReference().getSelectionModel().select(index);
    }

    private void updateScenesInListView(ArrayList<String> itemsForComboBox) {
        for (HMIScene page : pages) {
            page.setItems(itemsForComboBox);
        }
    }

    public void duplicateScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        try {
            HMIScene duplicateScene = pages.get(index).clone();
            if (duplicateScene != null) {
                SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(duplicateScene.getTitle() + " copy", duplicateScene.getSceneCommentary(), duplicateScene.getBackground());
                setWindowPropertiesWindow.showAndWait();
                duplicateScene.update(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
                addScene(duplicateScene);
            } else {
                logger.log(Level.INFO, "duplication is null");
            }


        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        logger.log(Level.INFO, "Finished duplicatedScene method");

    }

    public void deleteScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        if (confirmDelete(sceneTitle)) {
            pages.remove(index);
            pagesTitles.remove(index);
            updateScenesInListView(pagesTitles);
            mainStage.setScene(pages.get(0));
        }
    }

    public void updateScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        HMIScene scene = (index != -1 && index < pages.size()) ? pages.get(index) : null;

        if (scene != null) {
            SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(scene.getTitle(), scene.getSceneCommentary(), scene.getBackground());
            setWindowPropertiesWindow.setPagesTitles(pagesTitles);
            setWindowPropertiesWindow.showAndWait();
            if(!setWindowPropertiesWindow.isCancelled()){
                scene.update(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
                pages.set(index, scene);
                for (HMIScene page : pages) {
                    pagesTitles.set(index, page.getTitle());
                    page.updateItem(index, scene.getTitle());
                }
            }
        }
    }


    public void addNewScene() {
        SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow();
        setWindowPropertiesWindow.setPagesTitles(pagesTitles);
        setWindowPropertiesWindow.showAndWait();
        if (!setWindowPropertiesWindow.isCancelled()) {
            HMIScene newScene = generatePage(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
            addScene(newScene);
        }
    }

    private void addScene(HMIScene newScene) {
        this.pages.add(newScene);
        this.pagesTitles.add(newScene.getTitle());
        updateScenesInListView(pagesTitles);

        mainStage.setScene(newScene);
        mainStage.setTitle(HMI_TITLE + newScene.getTitle());
    }

    private int findSceneIndex(String sceneTitle) {
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getTitle().equals(sceneTitle)) {
                return i;
            }
        }
        return -1;
    }

    private boolean confirmDelete(String sceneTitle) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("Desea eliminar la página seleccionada \"" + sceneTitle + "\"?");

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cancelButton, okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        }
        return false;
    }

    private void generateDatabase() {
        try {
            if (!DBConnection.checkIfTablesFromSchemaBDDriverAreReady()) {
                DBConnection dbConnection = new DBConnection();
                dbConnection.generateSchemaBDDriverEIP();
            }
            if (!DBConnection.tableExistsInSchema("Users", "HMIUsers")) {
                DBConnection.generateSchemaHMIUsers();
            }
        } catch (SQLException sqlException) {
            showAlertForGeneratingSchemas(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", sqlException.getMessage(),false);
            sqlException.printStackTrace();
        } catch (IOException e){
            if(!showAlertForGeneratingSchemas(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", e.getMessage()+"; pulse OK para mostrar la ventana de ingreso de credenciales para conectarse a la base de datos",true)){
                generateDatabase();
            }
            e.printStackTrace();
        }

    }

    public boolean showAlertForGeneratingSchemas(Alert.AlertType type, String title, String message, boolean showCredentialsWindow) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            if(showCredentialsWindow){
                SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
                saveDatabaseCredentialsWindow.showAndWait();
                return saveDatabaseCredentialsWindow.isCancelled();
            }
        }
        return true;
    }

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }
}
