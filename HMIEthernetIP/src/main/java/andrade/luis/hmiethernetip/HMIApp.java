package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.models.*;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.util.DBConnection;
import andrade.luis.hmiethernetip.views.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;


import static javafx.geometry.Pos.CENTER;

public class HMIApp extends Application {
    public static final String EDITAR_STR = "Editar";
    public static final String EJECUTAR_STR = "Ejecutar";
    public static final String USER_HOME = "user.home";
    private Stage mainStage;
    Logger logger
            = Logger.getLogger(
            HMIApp.class.getName());

    private static final String DEFAULT_PAGE_NAME = "Página 1";
    private static final String FILE_ERROR_TITLE = "El archivo de proyecto no es válido";
    private final ArrayList<HMIScene> pages = new ArrayList<>();
    private final ArrayList<Stage> createdWindows = new ArrayList<>();
    private ArrayList<String> pagesTitles = new ArrayList<>();
    private ArrayList<Alarm> projectAlarms = new ArrayList<>();
    private ArrayList<Alarm> manageableAlarms = new ArrayList<>();
    private ArrayList<Tag> localTags = new ArrayList<>();
    private final ArrayList<MenuItem> editExecuteMenuItems = new ArrayList<>();
    private final ArrayList<Menu> fileMenus = new ArrayList<>();
    private final ArrayList<Menu> editMenus = new ArrayList<>();
    private final ArrayList<Menu> alarmsMenus = new ArrayList<>();
    private final ArrayList<Menu> configMenus = new ArrayList<>();
    private final ArrayList<Menu> windowsMenus = new ArrayList<>();
    private final ArrayList<Menu> helpMenus = new ArrayList<>();
    private String currentProjectFilePath = null;
    private String currentFilename = null;
    private boolean wasModified = false;
    private boolean wasEncryptedFileInputPasswordCanceled = true;
    private final BooleanProperty updateTitleFlag = new SimpleBooleanProperty(false);
    private HMIAppData hmiAppData = new HMIAppData();
    private static final String HMI_TITLE = "LibreHMI";
    private static final String HMI_PROJECT = "LibreHMIProject";
    private static final String ERROR_STR = "Error:";
    private static final String IMAGES_DIR_STR = "images";
    private static final String ALERT_SAVE_TITLE = "¿Desea guardar los cambios del proyecto actual?";
    private static final String ALERT_SAVE_DESCRIPTION = "Los cambios se perderán si elige No Guardar";
    private static final int CAPACITY = 10;
    private HMIUser user;
    /**
     * Calendar Icon source : <a href="https://www.flaticon.com/free-icons/calendar" title="calendar icons">Calendar icons created by Freepik - Flaticon</a>
     */
    private final Image calendarIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "calendar.png").toExternalForm());
    /**
     * Text icon source: <a href="https://www.flaticon.com/free-icons/text" title="text icons">Text icons created by Freepik - Flaticon</a>
     */
    private final Image textIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "text.png").toExternalForm());
    /**
     * Input icon source: <a href="https://www.flaticon.com/free-icons/type" title="type icons">Type icons created by Freepik - Flaticon</a>
     */
    private final Image inputIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "type.png").toExternalForm());
    /**
     * Line icon source: <a href="https://www.flaticon.com/free-icons/line" title="line icons">Line icons created by Freepik - Flaticon</a>
     */
    private final Image lineIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "diagonal-line.png").toExternalForm());
    /**
     * Rectangle icon source: <a href="https://www.flaticon.com/free-icons/geometry" title="geometry icons">Geometry icons created by Freepik - Flaticon</a>
     */
    private final Image rectangleIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "rectangle-shape.png").toExternalForm());
    /**
     * Slider icon source: <a href="https://www.flaticon.com/free-icons/slider" title="slider icons">Slider icons created by Andrejs Kirma - Flaticon</a>
     */
    private final Image sliderIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "slider.png").toExternalForm());
    /**
     * Button icon source: <a href="https://www.flaticon.com/free-icons/accept" title="accept icons">Accept icons created by Freepik - Flaticon</a>
     */
    private final Image buttonIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "ok-button.png").toExternalForm());
    /**
     * Alarm icon source: <a href="https://www.flaticon.com/free-icons/alarm" title="alarm icons">Alarm icons created by Freepik - Flaticon</a>
     */
    private final Image alarmDisplayIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "alarm-display.png").toExternalForm());
    /**
     * Symbol icon source: <a href="https://www.flaticon.com/free-icons/graphic-design" title="graphic design icons">Graphic design icons created by monkik - Flaticon</a>
     */
    private final Image symbolIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "symbol-icon.png").toExternalForm());
    /**
     * Pushbutton icon source: <a href="https://www.flaticon.com/free-icons/push-button" title="push button icons">Push button icons created by Pixel perfect - Flaticon</a>
     */
    private final Image pushbuttonIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "pushbutton.png").toExternalForm());
    /**
     * Image icon source: <a href="https://www.flaticon.com/free-icons/photo" title="photo icons">Photo icons created by feen - Flaticon</a>
     */
    private final Image imageIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "image.png").toExternalForm());
    /**
     * Ellipse icon source: <a href="https://www.flaticon.com/free-icons/oval" title="oval icons">Oval icons created by Freepik - Flaticon</a>
     */
    private final Image ellipseIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "ellipse.png").toExternalForm());
    /**
     * Trend icon source: <a href="https://www.flaticon.com/free-icons/trend" title="trend icons">Trend icons created by adriansyah - Flaticon</a>
     */
    private final Image trendIcon = new Image(getClass().getResource(IMAGES_DIR_STR + File.separator + "trending.png").toExternalForm());
    private String modeLabel = "";
    private String mode = "";
    private Timeline autoBlockTimeline;
    private String selectedPage;
    private Timeline refreshLocalTagsTimeline;
    private HMICanvas root;

    public String getCurrentProjectFilePath() {
        return currentProjectFilePath;
    }

    public void setCurrentProjectFilePath(String currentProjectFilePath) {
        this.currentProjectFilePath = currentProjectFilePath;
    }

    public boolean isWasModified() {
        return wasModified;
    }

    public void setWasModified(boolean wasModified) {
        this.wasModified = wasModified;
        this.updateTitleFlag.setValue(this.wasModified);
    }

    public HMIAppData getHmiAppData() {
        return hmiAppData;
    }

    public ArrayList<HMIScene> getPages() {
        return pages;
    }

    public void setHmiAppData(HMIAppData hmiAppData) {
        this.hmiAppData = hmiAppData;
        clearProject();
        for (int i = 0; i < this.hmiAppData.getHmiAppPages().size(); i++) {
            HMISceneData hmiSceneData = this.hmiAppData.getHmiAppPages().get(i);
            addScene(hmiSceneData);
        }
        manageableAlarms.clear();
        projectAlarms.clear();
        localTags.clear();
        if (this.hmiAppData.getHmiAlarms() != null) {
            for (int i = 0; i < this.hmiAppData.getHmiAlarms().size(); i++) {
                addAlarm(this.hmiAppData.getHmiAlarms().get(i));
            }
        }
        if(this.hmiAppData.getHmiLocalTags() != null){
            localTags.addAll(this.hmiAppData.getHmiLocalTags());
        }
    }

    public void clearProject() {
        this.pages.clear();
        this.pagesTitles.clear();
        this.wasModified = false;
        this.currentProjectFilePath = null;
    }

    public void createNewProject() {
        logger.log(Level.INFO, "Closing Project...");
        if (wasModified) {
            if (showAlert(Alert.AlertType.CONFIRMATION, ALERT_SAVE_TITLE, ALERT_SAVE_DESCRIPTION, false, true)) {
                this.clearProject();
                HMIScene scene = generatePage(DEFAULT_PAGE_NAME, "", Color.WHITESMOKE);
                addScene(scene);
            }
        } else {
            this.clearProject();
            HMIScene scene = generatePage(DEFAULT_PAGE_NAME, "", Color.WHITESMOKE);
            addScene(scene);
        }

    }

    public ArrayList<String> getPagesTitles() {
        return pagesTitles;
    }

    public void setPagesTitles(ArrayList<String> pagesTitles) {
        this.pagesTitles = pagesTitles;
    }

    @Override
    public void start(Stage stage) {

        mainStage = stage;
        mainStage.setOnCloseRequest(this::closeProcess);
        generateDatabase();
        try {
            WelcomeWindow welcomeWindow = new WelcomeWindow(this);
            Scene tempScene = welcomeWindow.getScene();
            mainStage.setScene(tempScene);
            mainStage.setMaximized(true);
            mainStage.setTitle(HMI_TITLE);
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateTitleFlag.addListener((observableValue, oldBoolean, newBoolean) -> {
            if (Boolean.TRUE.equals(newBoolean)) {
                mainStage.setTitle(mainStage.getTitle() + "*");
            } else if (Boolean.FALSE.equals(newBoolean) && mainStage.getTitle().endsWith("*")) {
                mainStage.setTitle(mainStage.getTitle().substring(0, mainStage.getTitle().length() - 1));
            }
        });
    }

    private void closeProcess(WindowEvent windowEvent) {
        logger.log(Level.INFO, "Closing...");
        if (wasModified) {
            if (showAlert(Alert.AlertType.CONFIRMATION, ALERT_SAVE_TITLE, ALERT_SAVE_DESCRIPTION, false, true)) {
                logger.log(Level.INFO, "CANCELED");
            } else {
                windowEvent.consume();
            }
        } else {
            logger.log(Level.INFO, "CLOSED...");
        }
    }

    public void setHMIStage(String filenamePath, String mode) throws IOException {
        if (mode.equals(EDITAR_STR)) {
            loginUser(mode);
            this.modeLabel = EJECUTAR_STR;
            this.mode = EDITAR_STR;
        } else {
            this.modeLabel = EDITAR_STR;
            this.mode = EJECUTAR_STR;
            user = new HMIUser("", "", "", "", "Operador", "");
        }
        if (user != null) {
            if (filenamePath == null) {
                createNewProject();
            } else if (filenamePath.equals("")) {
                loadHMIData();
            } else {
                loadHMIData(filenamePath);
            }
            enableInputRepresentations(mode);
        }
    }

    /**
     * Este método permite la generación de páginas que contienen los botones y donde se pueden agregar objetos a partir
     * de dichos botones, cuando se crea una nueva página se utiliza este método y se guarda en un ArrayList.
     *
     * @param sceneTitle      Título de la página, mostrado en el título de la ventana.
     * @param sceneCommentary Comentario de la página puede ser utilizada como una pequeña documentación.
     * @param backgroundColor Color de fondo de la página
     * @return La página(del Tipo HMIScene, heredera de una Scene de JavaFx)
     */
    private HMIScene generatePage(String sceneTitle, String sceneCommentary, Color backgroundColor) {
        var canvas = new Canvas(300, 300);
        this.root = new HMICanvas();
        root.getChildren().add(canvas);
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);
        HMIScene scene = new HMIScene(scrollPane, root, sceneTitle, sceneCommentary, bounds.getWidth(), bounds.getHeight(), backgroundColor);
        Label designElementsLabel = new Label("Elementos de Diseño");
        designElementsLabel.setFont(new Font("Arial", 15));
        VBox designToolsVBox = generateDesignVBox(scene, root);


        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of(scene.getTitle()));
        ListView<String> listViewReference = new ListView<>();
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);

        Label pagesLabel = new Label("Páginas");
        pagesLabel.setFont(new Font("Arial", 15));

        VBox vbox = new VBox(designElementsLabel, designToolsVBox, pagesLabel, scene.getListViewReference());
        vbox.setPadding(new Insets(50, 0, 0, 0));
        vbox.setSpacing(10);
        vbox.setPrefWidth(150);
        Button expandButton = new Button(">");
        HBox expandHBox = new HBox(vbox, expandButton);
        expandHBox.setAlignment(CENTER);
        expandHBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        expandHBox.setMaxWidth(Region.USE_PREF_SIZE);

        expandHBox.setTranslateX(-vbox.getPrefWidth());
        StackPane.setAlignment(expandHBox, Pos.CENTER_LEFT);
        // animation for moving the slider
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(expandHBox.translateXProperty(), -vbox.getPrefWidth())),
                new KeyFrame(Duration.millis(500), new KeyValue(expandHBox.translateXProperty(), 0d))
        );

        expandButton.setOnAction(evt -> {
            // adjust the direction of play and start playing, if not already done
            String text = expandButton.getText();
            boolean playing = timeline.getStatus() == Animation.Status.RUNNING;
            if (">".equals(text)) {
                timeline.setRate(1);
                if (!playing) {
                    timeline.playFromStart();
                }
                expandButton.setText("<");
            } else {
                timeline.setRate(-1);
                if (!playing) {
                    timeline.playFrom("end");
                }
                expandButton.setText(">");
            }
        });

        MenuBar menuBar = generateMenuBar(scene, root);
        menuBar.setId("#menuBar");
        //menuBars.add(menuBar);

        root.getChildren().add(menuBar);
        root.getChildren().add(expandHBox);
        scene.setHmiApp(this);

        return scene;
    }

    private MenuBar generateMenuBar(HMIScene scene, HMICanvas root) {
        MenuBar menuBar = new MenuBar();
        menuBar.setPrefWidth(mainStage.getWidth());

        //Menu file
        Menu menuFile = generateMenuFile();
        fileMenus.add(menuFile);

        // --- Menu Edit
        Menu menuEdit = new Menu(EDITAR_STR);
        editMenus.add(menuEdit);
        menuEdit.setId("#editMI");
        MenuItem copyMI = new MenuItem("Copiar");
        copyMI.setAccelerator(KeyCombination.keyCombination("Ctrl+C"));
        copyMI.setOnAction(mouseEvent -> scene.copy());

        MenuItem cutMI = new MenuItem("Cortar");
        cutMI.setAccelerator(KeyCombination.keyCombination("Ctrl+X"));
        cutMI.setOnAction(mouseEvent -> scene.cut());

        MenuItem pasteMI = new MenuItem("Pegar");
        pasteMI.setAccelerator(KeyCombination.keyCombination("Ctrl+V"));
        pasteMI.setOnAction(mouseEvent -> root.paste());

        menuEdit.getItems().addAll(copyMI, cutMI, pasteMI);

        Menu menuAlarm = generateMenuAlarm();
        alarmsMenus.add(menuAlarm);

        // --- Menu Configuration
        Menu menuConfiguration = new Menu("Configurar");
        configMenus.add(menuConfiguration);
        menuConfiguration.setId("#configurationMI");
        Menu userMI = new Menu("Usuarios");
        userMI.setId("#usersMI");
        MenuItem changeUserMI = new MenuItem("Cambiar de Usuario");
        changeUserMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+U"));
        changeUserMI.setOnAction(mouseEvent -> loginUser(this.mode));
        MenuItem manageUsersMI = new MenuItem("Administrar Usuarios");
        manageUsersMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Alt+U"));
        manageUsersMI.setOnAction(mouseEvent -> {
            ManageUsersWindow manageUsersWindow = new ManageUsersWindow(this.user);
            manageUsersWindow.showAndWait();
        });
        userMI.getItems().addAll(changeUserMI, manageUsersMI);
        Menu tagsMI = new Menu("Tags Locales");
        tagsMI.setId("#tagsMI");
        MenuItem createLocalTagMI = new MenuItem("Crear Tag Local");
        //createLocalTagMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+U"));
        createLocalTagMI.setOnAction(mouseEvent -> {
            ManageLocalTagWindow manageLocalTagWindow = new ManageLocalTagWindow(null);
            manageLocalTagWindow.showAndWait();
            if (manageLocalTagWindow.getTag() != null) {
                localTags.add(manageLocalTagWindow.getTag());
            }

        });
        MenuItem manageLocalTagsMI = new MenuItem("Administrar Tag Locales");
        //manageUsersMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Alt+U"));
        manageLocalTagsMI.setOnAction(mouseEvent -> {
            SelectTagWindow tagWindow = new SelectTagWindow(false, "LocalTags", false, localTags);
            tagWindow.showAndWait();
            updateLocalTags(true);
        });
        tagsMI.getItems().addAll(createLocalTagMI, manageLocalTagsMI);
        MenuItem propertiesMI = new MenuItem("Conexión de Base de Datos");
        propertiesMI.setAccelerator(KeyCombination.keyCombination("Ctrl+P"));
        propertiesMI.setOnAction(mouseEvent -> {
            SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
            saveDatabaseCredentialsWindow.show();
        });

        MenuItem blockingTimeoutMI = new MenuItem("Tiempo de Bloqueo");
        blockingTimeoutMI.setAccelerator(KeyCombination.keyCombination("Ctrl+T"));
        blockingTimeoutMI.setOnAction(mouseEvent -> {
            SetBlockingTimeoutWindow setBlockingTimeoutWindow = new SetBlockingTimeoutWindow(this.getHmiAppData().getBlockingTimeout());
            setBlockingTimeoutWindow.showAndWait();
            this.hmiAppData.setBlockingTimeout(setBlockingTimeoutWindow.getTimeout());
            this.setWasModified(true);
        });
        menuConfiguration.getItems().addAll(userMI, tagsMI, blockingTimeoutMI, propertiesMI);

        Menu menuWindows = new Menu("Ventanas");
        MenuItem selectWindowsMI = new MenuItem("Mostrar Ventanas");
        selectWindowsMI.setAccelerator(KeyCombination.keyCombination("Ctrl+W"));
        selectWindowsMI.setOnAction(mouseEvent -> {
            ArrayList<String> showPages = new ArrayList<>();
            showPages.add(selectedPage);
            SelectWindowsWindow selectWindowsWindow = new SelectWindowsWindow(getPagesTitles(), showPages);
            selectWindowsWindow.showAndWait();
            generateStagesForPages(selectWindowsWindow.getSelectedItems());
        });
        MenuItem importWindowsMI = new MenuItem("Importar Ventanas");
        importWindowsMI.setAccelerator(KeyCombination.keyCombination("Ctrl+I"));
        importWindowsMI.setOnAction(mouseEvent -> {
            try {
                loadSceneData();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error al cargar el archivo", "Existió un error al cargar el archivo: " + e.getMessage(), false, false);
            }
        });
        menuWindows.getItems().add(selectWindowsMI);
        menuWindows.getItems().add(importWindowsMI);

        windowsMenus.add(menuWindows);

        Menu menuHelp = new Menu("Ayuda");
        helpMenus.add(menuHelp);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuAlarm, menuConfiguration, menuWindows, menuHelp);

        return menuBar;
    }

    private void openProcess() {
        try {
            logger.log(Level.INFO, "Closing Project...");
            if (wasModified) {
                if (showAlert(Alert.AlertType.CONFIRMATION, ALERT_SAVE_TITLE, ALERT_SAVE_DESCRIPTION, false, true)) {
                    loadHMIData();
                }
            } else {
                loadHMIData();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error al Cargar Proyecto", ERROR_STR + e.getMessage(), false, false);
            e.printStackTrace();
        }
    }

    private Menu generateMenuFile() {
        // --- Menu File
        Menu menuFile = new Menu("Archivo");
        menuFile.setId("#fileMI");
        MenuItem newProjectMI = new MenuItem("Nuevo");
        newProjectMI.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        newProjectMI.setOnAction(mouseEvent -> this.createNewProject());
        MenuItem openProjectMI = new MenuItem("Abrir");
        openProjectMI.setAccelerator(KeyCombination.keyCombination("Ctrl+O"));
        openProjectMI.setOnAction(mouseEvent -> openProcess());
        Menu openRecentProjectsMI = new Menu("Recientes");

        ArrayList<String> menuItems = getRecentItems();
        if (!menuItems.isEmpty()) {
            for (int i = 0; i < menuItems.size(); i++) {
                MenuItem recentMenuItem = new MenuItem(menuItems.get(i));
                int finalI = i;
                recentMenuItem.setOnAction(mouseEvent -> {
                    try {
                        this.loadHMIData(menuItems.get(finalI));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                openRecentProjectsMI.getItems().add(recentMenuItem);
            }
        }

        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();

        MenuItem saveMI = new MenuItem("Guardar");
        saveMI.setId("#saveMI");
        saveMI.setAccelerator(KeyCombination.keyCombination("Ctrl+G"));
        saveMI.setOnAction(mouseEvent -> {
            try {
                this.saveHMIDataProcess();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar", ERROR_STR + e.getMessage(), false, false);
                e.printStackTrace();
            }
        });
        MenuItem saveAsMI = new MenuItem("Guardar como");
        saveAsMI.setId("#saveAsMI");
        saveAsMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+G"));
        saveAsMI.setOnAction(mouseEvent -> {
            try {
                this.saveAsHMIData(false);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar Como", ERROR_STR + e.getMessage(), false, false);
                e.printStackTrace();
            }
        });
        MenuItem saveAsEncryptedMI = new MenuItem("Guardar como Archivo Protegido");
        saveAsEncryptedMI.setId("#saveAsEncryptedMI");
        saveAsEncryptedMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Alt+G"));
        saveAsEncryptedMI.setOnAction(mouseEvent -> {
            try {
                this.saveAsHMIData(true);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar Como", ERROR_STR + e.getMessage(), false, false);
                e.printStackTrace();
            }
        });

        SeparatorMenuItem runEditSeparatorMI = new SeparatorMenuItem();

        MenuItem runEditMI = new MenuItem(modeLabel);

        editExecuteMenuItems.add(runEditMI);
        runEditMI.setOnAction(actionEvent -> {
            if (modeLabel.equals(EDITAR_STR)) {
                loginUser(modeLabel);
                this.modeLabel = EJECUTAR_STR;
                this.mode = EDITAR_STR;
            } else {
                this.modeLabel = EDITAR_STR;
                this.mode = EJECUTAR_STR;
                enableInputRepresentations(this.mode);
            }
            updateMenuBarsMode(this.modeLabel);
        });


        MenuItem exitMI = new MenuItem("Salir");
        exitMI.setAccelerator(KeyCombination.keyCombination("Alt+F4"));
        exitMI.setOnAction(mouseEvent -> closeProcess(new WindowEvent(
                mainStage,
                WindowEvent.WINDOW_CLOSE_REQUEST
        )));

        SeparatorMenuItem exitSeparatorMenuItem = new SeparatorMenuItem();

        menuFile.getItems().addAll(newProjectMI, openProjectMI, openRecentProjectsMI, separatorMenuItem, saveMI, saveAsMI, saveAsEncryptedMI, runEditSeparatorMI, runEditMI, exitSeparatorMenuItem, exitMI);

        return menuFile;
    }

    private void updateMenuBarsMode(String mode) {
        for (MenuItem menuItem : editExecuteMenuItems) {
            menuItem.setText(mode);
        }

        for (Menu menu : fileMenus) {
            for (MenuItem subMenu : menu.getItems()) {
                if (subMenu.getId() != null) {
                    if (subMenu.getId().equals("#saveMI") || subMenu.getId().equals("#saveAsMI")) {
                        subMenu.setDisable(this.mode.equals(EJECUTAR_STR));
                    }
                }
            }
        }

        for (Menu menu : editMenus) {
            menu.setDisable(this.mode.equals(EJECUTAR_STR));
        }

        for (Menu menu : alarmsMenus) {
            menu.setDisable(this.mode.equals(EJECUTAR_STR));
        }

        for (Menu menu : configMenus) {
            menu.setDisable(this.mode.equals(EJECUTAR_STR));
        }

    }

    private Menu generateMenuAlarm() {
        // --- Menu View
        Menu menuAlarm = new Menu("Alarmas");
        menuAlarm.setId("#alarmMI");
        MenuItem alarmMI = new MenuItem("Crear Alarma");
        alarmMI.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        alarmMI.setOnAction(mouseEvent -> {
            SetAlarmWindow setAlarmWindow = new SetAlarmWindow(this);
            setAlarmWindow.showAndWait();
            if (setAlarmWindow.isDone()) {
                if (setAlarmWindow.getLocalExpression().determineResultType().equals("Flotante") || setAlarmWindow.getLocalExpression().determineResultType().equals("Entero")) {
                    Alarm alarm = new Alarm(
                            setAlarmWindow.getLocalExpression(),
                            Double.parseDouble(setAlarmWindow.getHighLimitTF().getText()),
                            Double.parseDouble(setAlarmWindow.getHiHiLimitTF().getText()),
                            Double.parseDouble(setAlarmWindow.getLowLimitTF().getText()),
                            Double.parseDouble(setAlarmWindow.getLoloLimitTF().getText()),
                            setAlarmWindow.getHighLimitCheckBox().isSelected(),
                            setAlarmWindow.getHiHiLimitCheckBox().isSelected(),
                            setAlarmWindow.getLowLimitCheckBox().isSelected(),
                            setAlarmWindow.getLoloLimitCheckBox().isSelected(),
                            setAlarmWindow.getAlarmNameTF().getText(),
                            setAlarmWindow.getAlarmCommentTF().getText()
                    );
                    addAlarm(alarm);
                } else if (setAlarmWindow.getLocalExpression().determineResultType().equals("Bool")) {
                    Alarm alarm = new Alarm(
                            setAlarmWindow.getLocalExpression(),
                            setAlarmWindow.getTrueRadioButton().isSelected(),
                            true,
                            setAlarmWindow.getAlarmNameTF().getText(),
                            setAlarmWindow.getAlarmCommentTF().getText()
                    );
                    addAlarm(alarm);
                }
            }
        });

        MenuItem manageAlarmsMI = new MenuItem("Administrar Alarmas");
        manageAlarmsMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Alt+A"));
        manageAlarmsMI.setOnAction(mouseEvent -> {
            ManageAlarmsWindow manageAlarmsWindow = new ManageAlarmsWindow((ArrayList<Alarm>) manageableAlarms.clone(), this);
            manageAlarmsWindow.showAndWait();
            manageableAlarms.clear();
            projectAlarms.clear();
            for (int i = 0; i < manageAlarmsWindow.getAlarmsList().size(); i++) {
                addAlarm(manageAlarmsWindow.getAlarmsList().get(i));
            }
        });

        menuAlarm.getItems().addAll(alarmMI, manageAlarmsMI);
        return menuAlarm;
    }

    private VBox generateDesignVBox(HMIScene scene, HMICanvas root) {
        Button rectangleBtn = new Button("");
        rectangleBtn.setTooltip(new Tooltip("Rectángulo"));
        rectangleBtn.setGraphic(new ImageView(rectangleIcon));
        rectangleBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Rectangle");
        });
        Button systemDateTimeLabelBtn = new Button("");
        systemDateTimeLabelBtn.setTooltip(new Tooltip("Etiqueta de Fecha y Hora"));
        systemDateTimeLabelBtn.setGraphic(new ImageView(calendarIcon));
        systemDateTimeLabelBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("SystemDateTime");
        });
        Button textBtn = new Button("");
        textBtn.setTooltip(new Tooltip("Texto"));
        textBtn.setGraphic(new ImageView(textIcon));
        textBtn.setOnMouseClicked(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Text");
        });
        Button buttonBtn = new Button("");
        buttonBtn.setTooltip(new Tooltip("Botón"));
        buttonBtn.setGraphic(new ImageView(buttonIcon));
        buttonBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Button");
        });
        Button sliderBtn = new Button("");
        sliderBtn.setTooltip(new Tooltip("Slider"));
        sliderBtn.setGraphic(new ImageView(sliderIcon));
        sliderBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Slider");
        });
        Button textFieldBtn = new Button("");
        textFieldBtn.setTooltip(new Tooltip("Entrada de Texto"));
        textFieldBtn.setGraphic(new ImageView(inputIcon));
        textFieldBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("TextField");
        });
        Button alarmDisplayBtn = new Button("");
        alarmDisplayBtn.setTooltip(new Tooltip("Resumen de Alarmas"));
        alarmDisplayBtn.setGraphic(new ImageView(alarmDisplayIcon));
        alarmDisplayBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("AlarmDisplay");
        });
        Button symbolBtn = new Button("");
        symbolBtn.setTooltip(new Tooltip("Símbolo HMI"));
        symbolBtn.setGraphic(new ImageView(symbolIcon));
        Button imageBtn = new Button("");
        imageBtn.setTooltip(new Tooltip("Imagen"));
        imageBtn.setGraphic(new ImageView(imageIcon));
        Button pushbuttonBtn = new Button("");
        pushbuttonBtn.setTooltip(new Tooltip("Botón Pulsador"));
        pushbuttonBtn.setGraphic(new ImageView(pushbuttonIcon));
        Button lineBtn = new Button("");
        lineBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Line");
        });
        lineBtn.setTooltip(new Tooltip("Línea"));
        lineBtn.setGraphic(new ImageView(lineIcon));
        Button ellipseBtn = new Button("");
        ellipseBtn.setTooltip(new Tooltip("Elipse"));
        ellipseBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Ellipse");
        });
        ellipseBtn.setGraphic(new ImageView(ellipseIcon));
        HBox firstHBox = new HBox(lineBtn, ellipseBtn, rectangleBtn);
        firstHBox.setAlignment(CENTER);
        HBox secondHBox = new HBox(systemDateTimeLabelBtn, textBtn, textFieldBtn);
        secondHBox.setAlignment(CENTER);
        HBox thirdHBox = new HBox(imageBtn, symbolBtn, pushbuttonBtn);
        thirdHBox.setAlignment(CENTER);
        HBox fourthHBox = new HBox(buttonBtn, alarmDisplayBtn, sliderBtn);
        fourthHBox.setAlignment(CENTER);
        symbolBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Symbol");
        });
        imageBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Image");
        });
        pushbuttonBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Pushbutton");
        });
        Button trendGraphBtn = new Button("");
        trendGraphBtn.setTooltip(new Tooltip("Gráfico de Tendencias"));
        trendGraphBtn.setGraphic(new ImageView(trendIcon));
        trendGraphBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("TrendChart");
        });


        HBox fifthHBox = new HBox(trendGraphBtn);
        fifthHBox.setAlignment(CENTER);
        VBox designToolsVBox = new VBox(firstHBox, secondHBox, thirdHBox, fourthHBox, fifthHBox);
        designToolsVBox.setAlignment(CENTER);
        return designToolsVBox;
    }

    public void loginUser(String mode) {
        LogInWindow logInWindow = new LogInWindow();
        logInWindow.showAndWait();
        if (logInWindow.getLoggedUser() != null) {
            this.user = logInWindow.getLoggedUser();
            if (this.user.getRole().equals("Operador") && this.mode.equals(EDITAR_STR)) {
                showAlert(Alert.AlertType.ERROR, "Error de Privilegios", "Error un usuario con el Rol \"Operador\" no puede editar el proyecto", false, false);
            } else {
                enableInputRepresentations(mode);
            }
        }
    }

    public void setAutoBlockObjectsTimeline() {
        this.autoBlockTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            logger.log(Level.INFO, "Executing autoBlockTimeline");
                            user = new HMIUser("", "", "", "", "Operador", "");
                            enableInputRepresentations(mode);
                        }), new KeyFrame(Duration.seconds(1)));
        this.autoBlockTimeline.setDelay(Duration.seconds(60));
    }

    public void setRefreshLocalTagsTimeline() {
        this.refreshLocalTagsTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> refreshLocalTags()), new KeyFrame(Duration.seconds(1)));
        this.refreshLocalTagsTimeline.setCycleCount(Animation.INDEFINITE);
        this.refreshLocalTagsTimeline.play();
    }

    public void refreshLocalTags() {
        updateLocalTagsFromInputs();
        updateLocalTags(false);
    }

    public void updateLocalTags(boolean forceUpdate) {
        for (int i = 0; i < root.getShapeArrayList().size(); i++) {
            for (Tag localTag : localTags) {
                root.getShapeArrayList().get(i).updateTag(localTag,forceUpdate);
            }
        }
        for (int i = 0; i < projectAlarms.size(); i++) {
            for (Tag localTag : localTags) {
                for (Alarm alarm : projectAlarms) {
                    ArrayList<Tag> parameters = alarm.getExpression().getParameters();
                    for (int j = 0; j < parameters.size(); j++) {
                        if (parameters.get(j).compareToTag(localTag)) {
                            parameters.set(j, localTag);
                        }
                    }
                    alarm.getExpression().setParameters(parameters);
                }
            }
        }
    }

    public void updateLocalTagsFromInputs() {
        ArrayList<CanvasObject> canvasObjects = root.getShapeArrayList();
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < canvasObjects.size(); i++) {
            CanvasObject canvasObject = canvasObjects.get(i);
            if (max == null && canvasObject.getCanvasObjectData().getSuperType().equals("TagInputObject")) {
                max = canvasObject.getLastTimeSelected();
                index = i;
            } else if (canvasObject.getLastTimeSelected() != null && max != null) {
                if (max.isBefore(canvasObject.getLastTimeSelected())) {
                    max = canvasObjects.get(i).getLastTimeSelected();
                    index = i;
                }
            }
        }
        for (int i = 0; i < localTags.size() && index > -1; i++) {
            Tag tagToUpdate = canvasObjects.get(index).getCanvasObjectData().getTag();
            if (localTags.get(i).compareToTag(tagToUpdate)) {
                localTags.set(i, tagToUpdate);
            }
        }

    }

    private void addAlarm(Alarm alarm) {
        if (alarm.getExpression().determineResultType().equals("Flotante") || alarm.getExpression().determineResultType().equals("Entero")) {
            manageableAlarms.add(alarm);
            generateDoubleProjectAlarms(alarm);
            this.setWasModified(true);
        } else if (alarm.getExpression().determineResultType().equals("Bool")) {
            manageableAlarms.add(alarm);
            projectAlarms.add(alarm);
            this.setWasModified(true);
        }
    }

    private ArrayList<String> getRecentItems() {
        String recentFilesFilePath = DBConnection.getWorkingDirectory() + File.separator + "recentFiles.json";
        RecentUsedFilesData recentUsedFilesData = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(recentFilesFilePath))) {
            recentUsedFilesData = gson.fromJson(bufferedReader, RecentUsedFilesData.class);
            return new ArrayList<>(recentUsedFilesData.getRecentlyUsedFilePaths());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    private void generateDoubleProjectAlarms(Alarm manageableAlarm) {
        if (manageableAlarm.isHighAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getHighLimit(),
                    manageableAlarm.getHiHiLimit(),
                    manageableAlarm.getLowLimit(),
                    manageableAlarm.getLoloLimit(),
                    manageableAlarm.isHighAlarmEnabled(),
                    false,
                    false,
                    false,
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            projectAlarms.add(alarm);
        }
        if (manageableAlarm.isHiHiAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getHighLimit(),
                    manageableAlarm.getHiHiLimit(),
                    manageableAlarm.getLowLimit(),
                    manageableAlarm.getLoloLimit(),
                    false,
                    manageableAlarm.isHiHiAlarmEnabled(),
                    false,
                    false,
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()

            );
            projectAlarms.add(alarm);
        }
        if (manageableAlarm.isLoloAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getHighLimit(),
                    manageableAlarm.getHiHiLimit(),
                    manageableAlarm.getLowLimit(),
                    manageableAlarm.getLoloLimit(),
                    false,
                    false,
                    false,
                    manageableAlarm.isLoloAlarmEnabled(),
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            projectAlarms.add(alarm);
        }
        if (manageableAlarm.isLowAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getHighLimit(),
                    manageableAlarm.getHiHiLimit(),
                    manageableAlarm.getLowLimit(),
                    manageableAlarm.getLoloLimit(),
                    false,
                    false,
                    manageableAlarm.isLowAlarmEnabled(),
                    false,
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            projectAlarms.add(alarm);
        }
    }

    private void saveHMIDataProcess() throws IOException {
        if (currentProjectFilePath != null) {
            this.saveHMIData(currentProjectFilePath);
        } else {
            this.saveAsHMIData(false);
        }
    }

    private void loadHMIData() throws IOException {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccione un archivo de proyecto LibreHMI");
        fileChooser.setInitialDirectory(
                new File(System.getProperty(USER_HOME))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json", "*.JSON"),
                new FileChooser.ExtensionFilter("LHMI", "*.lhmi", "*.LHMI"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        loadHMIData(file.getAbsolutePath());

    }

    public void loadHMIData(String filenamePath) throws IOException {
        String[] filenamePathArr = filenamePath.split("\\.");
        String type = filenamePathArr[filenamePathArr.length - 1];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        HMIAppData localHmiAppData = null;
        if (type.equals("json") || type.equals("JSON")) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filenamePath));
            localHmiAppData = gson.fromJson(bufferedReader, HMIAppData.class);
        } else if (type.equals("lhmi") || type.equals("LHMI")) {
            do {
                localHmiAppData = getEncryptedHmiAppData(gson, filenamePath);
            } while (!wasEncryptedFileInputPasswordCanceled && localHmiAppData == null);
        }


        if (localHmiAppData != null) {
            if (localHmiAppData.getType() == null) {
                if (showAlert(Alert.AlertType.WARNING, FILE_ERROR_TITLE, "¿Intentar cargarlo de todas formas?", false, false)) {
                    loadHMIData(localHmiAppData, filenamePath);
                }
            } else if (localHmiAppData.getType().equals(HMI_PROJECT)) {
                loadHMIData(localHmiAppData, filenamePath);
            } else {
                if (showAlert(Alert.AlertType.WARNING, FILE_ERROR_TITLE, "¿Intentar cargarlo de todas formas?", false, false)) {
                    loadHMIData(localHmiAppData, filenamePath);
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, FILE_ERROR_TITLE, "No se pudo obtener ningún dato de proyecto desde el archivo", false, false);
        }
    }

    private HMIAppData getEncryptedHmiAppData(Gson gson, String filenamePath) throws IOException {
        HMIAppData localHmiAppData = null;
        SetFilePasswordWindow setFilePasswordWindow = new SetFilePasswordWindow(false);
        setFilePasswordWindow.showAndWait();
        if (!setFilePasswordWindow.isCanceled()) {
            StringBuilder rawEncryptedDataSB = new StringBuilder();
            String line;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filenamePath))) {
                while ((line = bufferedReader.readLine()) != null) {
                    rawEncryptedDataSB.append(line);
                }
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(setFilePasswordWindow.getPassword());
                encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
                try {
                    String rawJson = encryptor.decrypt(rawEncryptedDataSB.toString());
                    localHmiAppData = gson.fromJson(rawJson, HMIAppData.class);
                    wasEncryptedFileInputPasswordCanceled = false;
                } catch (EncryptionOperationNotPossibleException e) {
                    wasEncryptedFileInputPasswordCanceled = !showAlert(Alert.AlertType.ERROR, "Contraseña incorrecta", "La contraseña ingresada no es correcta, reintente", false, false);
                }
            } finally {
                wasEncryptedFileInputPasswordCanceled = false;
            }
        } else {
            wasEncryptedFileInputPasswordCanceled = true;
        }
        return localHmiAppData;
    }

    private void loadHMIData(HMIAppData localHmiAppData, String filenamePath) {
        this.setHmiAppData(localHmiAppData);
        this.currentProjectFilePath = filenamePath;
        String[] filenameArr = filenamePath.split(File.separator);
        currentFilename = filenameArr[filenameArr.length - 1];
        this.setWasModified(false);
        updateTitleWithFilename();
        addRecentUsedFilePath(filenamePath);
    }

    private void addRecentUsedFilePath(String filenamePath) {
        String recentFilesFilePath = DBConnection.getWorkingDirectory() + File.separator + "recentFiles.json";
        RecentUsedFilesData recentUsedFilesData = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(recentFilesFilePath))) {
            recentUsedFilesData = gson.fromJson(bufferedReader, RecentUsedFilesData.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        RecentUsedFilesCache recentUsedFilesCache = null;
        if (recentUsedFilesData == null) {
            recentUsedFilesData = new RecentUsedFilesData();
            recentUsedFilesData.setCapacity(CAPACITY);
            recentUsedFilesCache = new RecentUsedFilesCache(CAPACITY);
        } else {
            recentUsedFilesCache = new RecentUsedFilesCache(recentUsedFilesData.getCapacity());
            recentUsedFilesCache.importArrayList(recentUsedFilesData.getRecentlyUsedFilePaths());
        }

        recentUsedFilesCache.refer(filenamePath);
        recentUsedFilesData.setCapacity(recentUsedFilesCache.getCapacity());
        recentUsedFilesData.setRecentlyUsedFilePaths(recentUsedFilesCache.exportArrayList());
        try (Writer writer = Files.newBufferedWriter(Path.of(recentFilesFilePath))) {
            gson.toJson(recentUsedFilesData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAsHMIData(boolean encrypted) throws IOException {
        FileChooser fileChooser = new FileChooser();
        if (encrypted) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("LHMI", "*.lhmi", "*.LHMI")
            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON", "*.json", "*.JSON")
            );
        }
        fileChooser.setTitle("Guardar Proyecto");
        fileChooser.setInitialDirectory(
                new File(System.getProperty(USER_HOME))
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (encrypted) {
                saveEncryptedHMIData(file.getAbsolutePath());
            } else {
                saveHMIData(file.getAbsolutePath());
            }
        }
    }

    private void prepareHMIData() {
        ArrayList<HMISceneData> hmiSceneDataArrayList = new ArrayList<>();
        for (HMIScene hmiScene : pages) {
            ArrayList<CanvasObjectData> shapeArrayList = new ArrayList<>();
            for (CanvasObject canvasObject : hmiScene.getCanvas().getCurrentCanvasObjects()) {
                shapeArrayList.add(canvasObject.getCanvasObjectData());
            }
            hmiScene.getHmiSceneData().setShapeArrayList(shapeArrayList);
            hmiSceneDataArrayList.add(hmiScene.getHmiSceneData());
        }
        this.hmiAppData.setHmiAppPages(hmiSceneDataArrayList);
        this.hmiAppData.setHmiAlarms(manageableAlarms);
        this.hmiAppData.setHmiLocalTags(localTags);
        this.hmiAppData.setType(HMI_PROJECT);
    }

    /**
     * Este método inicia el proceso para guardar el proyecto dentro de un archivo json.
     *
     * @throws IOException Si hay algún problema en la lectura o escritura.
     */
    private void saveHMIData(String filenamePath) throws IOException {
        prepareHMIData();
        Gson gson = new Gson();
        if (filenamePath != null) {
            String[] filenameArr = filenamePath.split(File.separator);
            this.currentFilename = filenameArr[filenameArr.length - 1];
            Writer writer = Files.newBufferedWriter(Path.of(filenamePath));
            gson.toJson(this.hmiAppData, writer);
            writer.close();
            this.currentProjectFilePath = filenamePath;
            this.setWasModified(false);
            addRecentUsedFilePath(filenamePath);
            updateTitleWithFilename();
        }
    }

    /**
     * Este método inicia el proceso para guardar el proyecto dentro de un archivo json.
     *
     * @throws IOException Si hay algún problema en la lectura o escritura.
     */
    private void saveEncryptedHMIData(String filenamePath) throws IOException {
        SetFilePasswordWindow setFilePasswordWindow = new SetFilePasswordWindow(true);
        setFilePasswordWindow.showAndWait();
        if (!setFilePasswordWindow.isCanceled()) {
            prepareHMIData();
            Gson gson = new Gson();
            if (filenamePath != null) {
                String[] filenameArr = filenamePath.split(File.separator);
                this.currentFilename = filenameArr[filenameArr.length - 1];
                Writer writer = Files.newBufferedWriter(Path.of(filenamePath));
                String rawJson = gson.toJson(this.hmiAppData);
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(setFilePasswordWindow.getPassword());
                encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
                String encryptedJSON = encryptor.encrypt(rawJson);
                writer.write(encryptedJSON);
                writer.close();
                this.currentProjectFilePath = filenamePath;
                this.setWasModified(false);
                addRecentUsedFilePath(filenamePath);
                updateTitleWithFilename();
            }
        }
    }

    /**
     * Este método permite cambiar de estados a los objetos de entrada de datos,
     * como un Button, Pushbutton, TextField, o Slider
     *
     * @param value Valor del estado, puede ser "Play","Stop","Default"
     */
    public void enableInputRepresentations(String value) {
        for (HMIScene page : this.pages) {
            for (int i = 0; i < page.getCanvas().getShapeArrayList().size(); i++) {
                page.getCanvas().getShapeArrayList().get(i).setUser(this.user);
                page.getCanvas().getShapeArrayList().get(i).setEnable(value);
            }
        }
        updateMenuBarsMode(this.modeLabel);
        if (mode.equals(EJECUTAR_STR) && this.user.getRole().equals("Administrador")) {
            setAutoBlockObjectsTimeline();
            this.autoBlockTimeline.play();
            setRefreshLocalTagsTimeline();
        }
    }

    /**
     * Este método permite la creación de ventanas para las Escenas si son mayores que uno,
     * si es solo uno cambia la Escena de la Ventana mostrada a la seleccionada
     * Además de crear ventanas(Stage) adicionales también las muestra
     *
     * @param selectedPages ArrayList de los títulos de las Páginas Seleccionadas
     */
    public void generateStagesForPages(ArrayList<String> selectedPages) {
        boolean mainStageWasUpdated = false;
        for (String selectedPageToGenerate : selectedPages) {
            int index = getIndexForStageWithScene(selectedPageToGenerate);
            if (index == -1) {
                if (!mainStageWasUpdated) {
                    changeSelectedScene(selectedPageToGenerate);
                    mainStageWasUpdated = true;
                } else {
                    int hmiSceneIndex = getIndexForScene(selectedPageToGenerate);
                    generateStage(pages.get(hmiSceneIndex));
                }
            } else {
                if (createdWindows.get(index).isShowing()) {
                    createdWindows.get(index).requestFocus();
                } else {
                    createdWindows.get(index).show();
                }
            }
        }

    }

    /**
     * Este método permite la creación de una nueva ventana a partir de una HMIScene previamente existente.
     *
     * @param scene La página creada del tipo HMIScene
     */
    private void generateStage(HMIScene scene) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(scene.getTitle() + " - " + HMI_TITLE);
        stage.show();
        createdWindows.add(stage);
    }

    /**
     * Buscar el índice de una determinada Ventana basada en su título.
     *
     * @param title Título de la página
     * @return El índice de la página en el ArrayList
     */
    private int getIndexForStageWithScene(String title) {
        int index = -1;
        logger.log(Level.INFO, "Title" + title);
        for (int i = 0; i < createdWindows.size(); i++) {
            logger.log(Level.INFO, "Current" + ((HMIScene) createdWindows.get(i).getScene()).getId());
            logger.log(Level.INFO, "Current" + ((HMIScene) createdWindows.get(i).getScene()).getTitle());
            if (((HMIScene) createdWindows.get(i).getScene()).getId().equals(title)) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Si se solicita el cambio de una sola página esta no generará una ventana nueva sino que cambiará la escena actual
     * de la aplicación a la seleccionada basada en su título
     *
     * @param sceneTitle Título de la página a seleccionarse
     */
    public void changeSelectedScene(String sceneTitle) {
        int index = getIndexForScene(sceneTitle);
        this.selectedPage = sceneTitle;
        mainStage.setScene(pages.get(index));
        String compliment = wasModified ? "*" : "";
        mainStage.setTitle(pages.get(index).getTitle() + " - " + HMI_TITLE + compliment);
        updateTitleWithFilename();
        pages.get(index).getListViewReference().getSelectionModel().select(index);
    }

    /**
     * Cuando se crea una nueva página todas las demás requieren actualizar la lista de páginas disponibles.
     *
     * @param itemsForComboBox ArrayList que contiene todos los títulos de las páginas.
     */
    private void updateScenesInListView(ArrayList<String> itemsForComboBox) {
        for (HMIScene page : pages) {
            page.setItems(itemsForComboBox);
        }
    }

    public void duplicateScene(String sceneTitle) {
        int index = getIndexForScene(sceneTitle);
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

    /**
     * Elimina la página seleccionada basada en el título, la elimina del arraylist de páginas y de los títulos de
     * páginas disponibles en las páginas restantes.
     *
     * @param sceneTitle Título de la Escena a eliminarse.
     */
    public void deleteScene(String sceneTitle) {
        int index = getIndexForScene(sceneTitle);
        if (confirmDelete(sceneTitle)) {
            pages.remove(index);
            pagesTitles.remove(index);
            updateScenesInListView(pagesTitles);
            mainStage.setScene(pages.get(0));
        }
    }

    /**
     * Actualiza las propiedades de una escena seleccionada basada en su título
     *
     * @param sceneTitle Título de la escena a actualizar atributos.
     */
    public void updateScene(String sceneTitle) {
        int index = getIndexForScene(sceneTitle);
        HMIScene scene = (index != -1 && index < pages.size()) ? pages.get(index) : null;

        if (scene != null) {
            SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow(scene.getTitle(), scene.getSceneCommentary(), scene.getBackground());
            setWindowPropertiesWindow.setPagesTitles(pagesTitles);
            setWindowPropertiesWindow.showAndWait();
            if (!setWindowPropertiesWindow.isCancelled()) {
                scene.update(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());
                pages.set(index, scene);
                for (HMIScene page : pages) {
                    pagesTitles.set(index, page.getTitle());
                    page.updateItem(index, scene.getTitle());
                }
            }
        }
    }

    /**
     * Permite iniciar el proceso para añadir una nueva página.
     */
    public void addNewScene() {
        SetWindowPropertiesWindow setWindowPropertiesWindow = new SetWindowPropertiesWindow();
        setWindowPropertiesWindow.setPagesTitles(pagesTitles);
        setWindowPropertiesWindow.showAndWait();
        if (!setWindowPropertiesWindow.isCancelled()) {
            HMIScene newScene = generatePage(setWindowPropertiesWindow.getNameField().getText(), setWindowPropertiesWindow.getCommentField().getText(), setWindowPropertiesWindow.getWindowColorPicker().getValue());

            addScene(newScene);
            setWasModified(true);
        }
    }

    /**
     * Este método añade una nueva página creada previamente por el proceso addNewScene.
     *
     * @param newScene HMIScene creada a través del proceso addNewScene
     */
    private void addScene(HMIScene newScene) {
        this.pages.add(newScene);
        this.selectedPage = newScene.getTitle();
        this.pagesTitles.add(newScene.getTitle());
        updateScenesInListView(pagesTitles);
        mainStage.setScene(newScene);
        mainStage.setTitle(newScene.getTitle() + " - " + HMI_TITLE);
        updateTitleWithFilename();
    }

    private void addScene(HMISceneData hmiSceneData) {
        HMIScene hmiScene = generatePage(hmiSceneData.getTitle(), hmiSceneData.getSceneCommentary(), hmiSceneData.getBackground().getColor());
        addScene(hmiScene);
        hmiScene.setHmiSceneData(hmiSceneData);
    }

    public void exportSceneData(String sceneName, boolean encrypted) {
        int index = getIndexForScene(sceneName);
        HMIScene scene = (index != -1 && index < pages.size()) ? pages.get(index) : null;
        FileChooser fileChooser = new FileChooser();
        if (encrypted) {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("SECURE WINDOW", "*.swindow", "*.SWINDOW")
            );
        } else {
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("WINDOW", "*.window", "*.WINDOW")
            );
        }
        fileChooser.setTitle("Exportar Ventana");
        fileChooser.setInitialDirectory(
                new File(System.getProperty(USER_HOME))
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null && scene != null) {
            if (encrypted) {
                try {
                    exportSceneEncryptedData(scene.getHmiSceneData(), file.getAbsolutePath());
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error al guardar el archivo: " + e.getMessage(), "Error al escribir el archivo", false, true);
                }
            } else {
                try {
                    exportSceneData(scene.getHmiSceneData(), file.getAbsolutePath());
                } catch (IOException e) {
                    showAlert(Alert.AlertType.ERROR, "Error al guardar el archivo: " + e.getMessage(), "Error al escribir el archivo", false, true);
                }
            }
        }
    }

    public void exportSceneData(HMISceneData hmiSceneData, String filePathName) throws IOException {
        Gson gson = new Gson();
        if (filePathName != null) {
            Writer writer = Files.newBufferedWriter(Path.of(filePathName));
            gson.toJson(hmiSceneData, writer);
            writer.close();
        }
    }

    private void loadSceneData() throws IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccione un archivo de proyecto LibreHMI");
        fileChooser.setInitialDirectory(
                new File(System.getProperty(USER_HOME))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("WINDOW", "*.window", "*.WINDOW"),
                new FileChooser.ExtensionFilter("SECURE WINDOW", "*.swindow", "*.SWINDOW"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        loadSceneData(file.getAbsolutePath());
    }

    private void loadSceneData(String filenamePath) throws IOException {
        String[] filenamePathArr = filenamePath.split("\\.");
        String type = filenamePathArr[filenamePathArr.length - 1];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        HMISceneData localHmiAppData = null;
        if (type.equals("window") || type.equals("WINDOW")) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filenamePath));
            localHmiAppData = gson.fromJson(bufferedReader, HMISceneData.class);
        } else if (type.equals("swindow") || type.equals("SWINDOW")) {
            do {
                localHmiAppData = getEncryptedHmiSceneData(gson, filenamePath);
            } while (!wasEncryptedFileInputPasswordCanceled && localHmiAppData == null);
        }


        if (localHmiAppData != null) {
            addScene(localHmiAppData);
        } else {
            showAlert(Alert.AlertType.ERROR, FILE_ERROR_TITLE, "No se pudo obtener ningún dato de proyecto desde el archivo", false, false);
        }
    }

    private HMISceneData getEncryptedHmiSceneData(Gson gson, String filenamePath) throws IOException {
        HMISceneData localHmiSceneData = null;
        SetFilePasswordWindow setFilePasswordWindow = new SetFilePasswordWindow(false);
        setFilePasswordWindow.showAndWait();
        if (!setFilePasswordWindow.isCanceled()) {
            StringBuilder rawEncryptedDataSB = new StringBuilder();
            String line;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filenamePath))) {
                while ((line = bufferedReader.readLine()) != null) {
                    rawEncryptedDataSB.append(line);
                }
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(setFilePasswordWindow.getPassword());
                encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
                try {
                    String rawJson = encryptor.decrypt(rawEncryptedDataSB.toString());
                    localHmiSceneData = gson.fromJson(rawJson, HMISceneData.class);
                    wasEncryptedFileInputPasswordCanceled = false;
                } catch (EncryptionOperationNotPossibleException e) {
                    wasEncryptedFileInputPasswordCanceled = !showAlert(Alert.AlertType.ERROR, "Contraseña incorrecta", "La contraseña ingresada no es correcta, reintente", false, false);
                }
            } finally {
                wasEncryptedFileInputPasswordCanceled = false;
            }
        } else {
            wasEncryptedFileInputPasswordCanceled = true;
        }
        return localHmiSceneData;
    }

    public void exportSceneEncryptedData(HMISceneData hmiSceneData, String filePathName) throws IOException {
        SetFilePasswordWindow setFilePasswordWindow = new SetFilePasswordWindow(true);
        setFilePasswordWindow.showAndWait();
        if (!setFilePasswordWindow.isCanceled()) {
            Gson gson = new Gson();
            if (filePathName != null) {
                Writer writer = Files.newBufferedWriter(Path.of(filePathName));
                String rawJson = gson.toJson(hmiSceneData);
                StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                encryptor.setPassword(setFilePasswordWindow.getPassword());
                encryptor.setAlgorithm("PBEWithMD5AndTripleDES");
                String encryptedJSON = encryptor.encrypt(rawJson);
                writer.write(encryptedJSON);
                writer.close();
            }
        }

    }

    public void updateTitleWithFilename() {
        if (this.currentFilename != null) {
            if (!mainStage.getTitle().contains(this.currentFilename)) {
                mainStage.setTitle(this.currentFilename + " - " + mainStage.getTitle());
            }
        }
    }

    public int getIndexForAlarm(String name) {
        int res = -1;
        for (int i = 0; i < manageableAlarms.size(); i++) {
            if (name.equals(manageableAlarms.get(i).getName())) {
                res = i;
            }
        }
        return res;
    }

    /**
     * Busca el índice de la Escena basada en su título
     *
     * @param sceneTitle Título de la escena buscada
     * @return El índice de la escena en el arrayList
     */
    public int getIndexForScene(String sceneTitle) {
        for (int i = 0; i < pages.size(); i++) {
            if (pages.get(i).getTitle().equals(sceneTitle)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Muestra una Alerta de confirmación de eliminación de la una página
     *
     * @param sceneTitle Título de la Escena a eliminarse
     * @return Booleano: - True si se confirma eliminación
     * - False si se cancela
     */
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
            showAlert(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", sqlException.getMessage(), false, false);
            sqlException.printStackTrace();
        } catch (IOException e) {
            if (!showAlert(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", e.getMessage() + "; pulse OK para mostrar la ventana de ingreso de credenciales para conectarse a la base de datos", true, false)) {
                generateDatabase();
            }
            e.printStackTrace();
        }

    }

    public boolean showAlert(Alert.AlertType type, String title, String message, boolean showCredentialsWindow, boolean isSaveDialog) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType saveButton = new ButtonType("Guardar", ButtonBar.ButtonData.YES);
        ButtonType dontSaveButton = new ButtonType("No Guardar", ButtonBar.ButtonData.NO);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        if (isSaveDialog) {
            alert.getButtonTypes().setAll(dontSaveButton, cancelButton, saveButton);
        } else {
            alert.getButtonTypes().setAll(cancelButton, okButton);
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            if (showCredentialsWindow) {
                SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
                saveDatabaseCredentialsWindow.showAndWait();
                return saveDatabaseCredentialsWindow.isCancelled();
            }
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        } else if (result.isPresent() && result.get() == saveButton) {
            alert.close();
            try {
                saveHMIDataProcess();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar", ERROR_STR + e.getMessage(), false, false);
                e.printStackTrace();
            }
            return true;
        } else if (result.isPresent() && result.get() == dontSaveButton) {
            alert.close();
            return true;
        }
        return true;
    }

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    public ArrayList<Alarm> getProjectAlarms() {
        return projectAlarms;
    }

    public void setProjectAlarms(ArrayList<Alarm> projectAlarms) {
        this.projectAlarms = projectAlarms;
    }

    public ArrayList<Alarm> getManageableAlarms() {
        return manageableAlarms;
    }

    public void setManageableAlarms(ArrayList<Alarm> manageableAlarms) {
        this.manageableAlarms = manageableAlarms;
    }

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }
}
