package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.models.users.HMIPassword;
import andrade.luis.hmiethernetip.util.DBConnection;
import andrade.luis.hmiethernetip.views.HMICanvas;
import andrade.luis.hmiethernetip.views.LogInWindow;
import andrade.luis.hmiethernetip.views.SetWindowPropertiesWindow;
import andrade.luis.hmiethernetip.views.SignUpWindow;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
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
    private static final String HMI_TITLE = "HMI: ";

    @Override
    public void start(Stage stage) {

        mainStage = stage;
        try{
            generateDatabase();
        }catch(SQLException sqlException){
            showAlertForGeneratingSchemas(Alert.AlertType.ERROR,"Error al conectarse a la base de datos",sqlException.getMessage());
            sqlException.printStackTrace();
        }
        HMIScene scene = generatePage("Página 1", "", Color.WHITESMOKE);
        /*HMIPassword password = new HMIPassword();
        String saltStr = "qATX04VQ7XoYwVzCMdzA0Q==";
        byte[] salt = Base64.getDecoder().decode(saltStr);
        byte[] hash = HMIPassword.computeSaltedHash("kakaroto".toCharArray(),salt);
        System.out.println(Base64.getEncoder().encodeToString(salt));
        String encondeHash = Base64.getEncoder().encodeToString(hash);
        System.out.println("New Hash:"+encondeHash+"Hash length:"+encondeHash.length());
        String computeSaltedHash = "jio1PQFD1Y2qcZGguQU5B4m4ZPhq7+VGB6drl53l9m8YNVUqsEBJvoFzSdI2juxj17vnhGTwb0tHtgLbKociiQ==";
        System.out.println(HMIPassword.verifyPassword("kakaroto",saltStr,computeSaltedHash));

        String saltedString = HMIPassword.createRandomSaltString();
        String hashString = HMIPassword.computeSaltedHashString("12345",saltedString);
        System.out.println("New Salt:"+saltedString);
        System.out.println("New Hash:"+hashString+"Hash length:"+hashString.length());
        System.out.println(HMIPassword.verifyPassword("12345","1bk/Dj19jpWgdCeyx63V9w==","ivK9LYDfcFki9zTJm3kksQUqUTYazkvki6+ZLks6wQIyyNM3xbEnWIE6YesnjkHM+1GqpbMK8ul6CPAE71vFgQ=="));*/


        pages.add(scene);
        mainStage.setTitle(HMI_TITLE + scene.getSceneTitle());
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
        Button newPageBtn = new Button("Add new Page");
        Button registerUserBtn = new Button("Register");
        Button logIntUserBtn = new Button("Log In");
        HBox hbox = new HBox(rectangleBtn, systemDateTimeLabelBtn, textBtn, buttonBtn,sliderBtn,textFieldBtn,newPageBtn,registerUserBtn,logIntUserBtn);

        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of(scene.getSceneTitle()));
        ListView<String> listViewReference = new ListView<>();
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);

        VBox vbox = new VBox(hbox, scene.getListViewReference());
        root.getChildren().add(vbox);

        newPageBtn.setOnMouseClicked(mouseEvent -> addNewScene());
        registerUserBtn.setOnMouseClicked(mouseEvent -> {
            SignUpWindow signUpWindow = new SignUpWindow();
            signUpWindow.show();
        });
        logIntUserBtn.setOnAction(mouseEvent -> {
            LogInWindow logInWindow = new LogInWindow();
            logInWindow.show();
        });

        scene.setHmiApp(this);

        return scene;
    }

    public void changeSelectedScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        mainStage.setScene(pages.get(index));
        mainStage.setTitle(HMI_TITLE + pages.get(index).getSceneTitle());
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
                SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(duplicateScene.getSceneTitle() + " copy", duplicateScene.getSceneCommentary(), duplicateScene.getBackground());
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
            ArrayList<String> pagesTitles = new ArrayList<>();
            for (HMIScene hmiScene : this.pages) {
                pagesTitles.add(hmiScene.getSceneTitle());
            }
            updateScenesInListView(pagesTitles);
            mainStage.setScene(pages.get(0));
        }
    }

    public void updateScene(String sceneTitle) {
        int index = findSceneIndex(sceneTitle);
        HMIScene scene = (index != -1 && index < pages.size()) ? pages.get(index) : null;

        if (scene != null) {
            SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(scene.getSceneTitle(), scene.getSceneCommentary(), scene.getBackground());
            setWindowPropertiesWindow.showAndWait();
            scene.update(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
            pages.set(index, scene);
            for (HMIScene page : pages) {
                page.updateItem(index, scene.getSceneTitle());
            }
        }
    }


    public void addNewScene() {
        SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow();
        setWindowPropertiesWindow.showAndWait();
        if (!setWindowPropertiesWindow.isCancelled()) {
            HMIScene newScene = generatePage(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
            addScene(newScene);
        }
    }

    private void addScene(HMIScene newScene) {
        logger.log(Level.INFO, "In addScene");
        this.pages.add(newScene);
        ArrayList<String> pagesTitles = new ArrayList<>();
        for (HMIScene hmiScene : this.pages) {
            pagesTitles.add(hmiScene.getSceneTitle());
        }
        updateScenesInListView(pagesTitles);

        mainStage.setScene(newScene);
        mainStage.setTitle(HMI_TITLE + newScene.getSceneTitle());

        logger.log(Level.INFO, "Finishing addScene");
    }

    private int findSceneIndex(String sceneTitle) {
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getSceneTitle().equals(sceneTitle)) {
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

    private void generateDatabase() throws SQLException {
        if(!DBConnection.checkIfTablesFromSchemaBDDriverAreReady()){
            DBConnection dbConnection = new DBConnection();
            dbConnection.generateSchemaBDDriverEIP();
        }
        if(!DBConnection.tableExistsInSchema("Users","HMIUsers")){
            DBConnection.generateSchemaHMIUsers();
        }
    }

    public void showAlertForGeneratingSchemas(Alert.AlertType type, String title,String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType okButton = new ButtonType("OK",ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == okButton)
        {
            alert.close();
        }
    }
}
