package andrade.luis.librehmi;

import andrade.luis.librehmi.controllers.HMIScene;
import andrade.luis.librehmi.models.*;
import andrade.luis.librehmi.views.canvas.CanvasObject;
import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.users.HMIUser;
import andrade.luis.librehmi.util.DBConnection;
import andrade.luis.librehmi.views.*;
import andrade.luis.librehmi.views.windows.*;
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
import javafx.scene.Node;
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

import static andrade.luis.librehmi.util.Alerts.showAlert;
import static javafx.geometry.Pos.CENTER;

public class HMIApp extends Application {
    public static final String EDITAR_STR = "Editar";
    public static final String EJECUTAR_STR = "Ejecutar";
    public static final String USER_HOME = "user.home";
    public static final String OPERATOR_STR = "Operador";
    public static final String CIPHER_ALGORITHM = "PBEWithMD5AndTripleDES";
    public static final String WINDOW_STR = "WINDOW";
    public static final String ERROR_WHILE_SAVING_STR = "Existi?? un error al guardar el proyecto";
    public static final String CANCEL_STR = "Cancelar";
    private Stage mainStage;
    Logger logger
            = Logger.getLogger(
            HMIApp.class.getName());

    private static final String DEFAULT_PAGE_NAME = "P??gina 1";
    private static final String FILE_ERROR_TITLE = "El archivo de proyecto no es v??lido";
    private final ArrayList<HMIScene> pages = new ArrayList<>();
    private final ArrayList<Stage> createdWindows = new ArrayList<>();
    private final ArrayList<String> pagesTitles = new ArrayList<>();
    private final ArrayList<Alarm> projectAlarms = new ArrayList<>();
    private final ArrayList<Alarm> manageableAlarms = new ArrayList<>();
    private ArrayList<Tag> localTags = new ArrayList<>();
    private final ArrayList<MenuItem> editExecuteMenuItems = new ArrayList<>();
    private final ArrayList<Menu> fileMenus = new ArrayList<>();
    private final ArrayList<Menu> editMenus = new ArrayList<>();
    private final ArrayList<Menu> alarmsMenus = new ArrayList<>();
    private final ArrayList<Menu> configMenus = new ArrayList<>();
    private final ArrayList<Menu> windowMenus = new ArrayList<>();
    private final ArrayList<HBox> expandBoxes = new ArrayList<>();
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
    private static final String ALERT_SAVE_TITLE = "??Desea guardar los cambios del proyecto actual?";
    private static final String ALERT_SAVE_DESCRIPTION = "Los cambios se perder??n si elige No Guardar";
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
    private HMICanvas root;

    /**
     * Permite actualizar las banderas de modificaci??n de proyecto
     *
     * @param wasModified Bandera sobre si la aplicaci??n fue modificada
     */
    public void setWasModified(boolean wasModified) {
        this.wasModified = wasModified;
        this.updateTitleFlag.setValue(this.wasModified);
    }

    public HMIAppData getHmiAppData() {
        return hmiAppData;
    }

    /**
     * Permite definir los datos de la aplicaci??n
     *
     * @param hmiAppData Datos de la aplicaci??n
     */
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
        if (this.hmiAppData.getHmiLocalTags() != null) {
            localTags.addAll(this.hmiAppData.getHmiLocalTags());
        }
    }

    /**
     * Permite limpiar los elementos del canvas
     */
    public void clearProject() {
        this.pages.clear();
        this.pagesTitles.clear();
        this.wasModified = false;
        this.currentProjectFilePath = null;
    }

    /**
     * Permite crear un nuevo proyecto
     */
    public void createNewProject() {
        if (wasModified) {
            if (showSaveDialog(false, "")) {
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

    /**
     * M??todo para inicializar la aplicaci??n
     *
     * @param stage Stage donde se agregaran los elementos de la aplicaci??n
     */
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
            log(e.getMessage());
        }

        updateTitleFlag.addListener((observableValue, oldBoolean, newBoolean) -> {
            if (Boolean.TRUE.equals(newBoolean)) {
                mainStage.setTitle(mainStage.getTitle() + "*");
            } else if (Boolean.FALSE.equals(newBoolean) && mainStage.getTitle().endsWith("*")) {
                mainStage.setTitle(mainStage.getTitle().substring(0, mainStage.getTitle().length() - 1));
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Permite cerrar la aplicaci??n
     *
     * @param windowEvent Objeto de evento de ventana que permite cerrar la aplicaci??n
     */
    private void closeProcess(WindowEvent windowEvent) {
        if (wasModified) {
            if (showSaveDialog(false, "")) {
                log("CANCELED");
            } else {
                windowEvent.consume();
            }
        }
    }

    /**
     * Permite definir el modo de la aplicaci??n
     *
     * @param filenamePath Path del archivo del proyecto
     * @param mode         Modo de la aplicaci??n
     * @throws IOException
     */
    public void setHMIStage(String filenamePath, String mode) throws IOException {
        if (mode.equals(EDITAR_STR)) {
            loginUser(mode);
            this.modeLabel = EJECUTAR_STR;
            this.mode = EDITAR_STR;
        } else {
            this.modeLabel = EDITAR_STR;
            this.mode = EJECUTAR_STR;
            user = new HMIUser("", "", "", "", OPERATOR_STR, "");
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
     * Este m??todo permite la generaci??n de p??ginas que contienen los botones y donde se pueden agregar objetos a partir
     * de dichos botones, cuando se crea una nueva p??gina se utiliza este m??todo y se guarda en un ArrayList.
     *
     * @param sceneTitle      T??tulo de la p??gina, mostrado en el t??tulo de la ventana.
     * @param sceneCommentary Comentario de la p??gina puede ser utilizada como una peque??a documentaci??n.
     * @param backgroundColor Color de fondo de la p??gina
     * @return La p??gina(del Tipo HMIScene, heredera de una Scene de JavaFx)
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
        Label designElementsLabel = new Label("Elementos de Dise??o");
        designElementsLabel.setFont(new Font("Arial", 15));
        VBox designToolsVBox = generateDesignVBox(scene, root);

        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of(scene.getTitle()));
        ListView<String> listViewReference = new ListView<>();
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);

        Label pagesLabel = new Label("P??ginas");
        pagesLabel.setFont(new Font("Arial", 15));

        VBox lateralMenuVBox = new VBox(designElementsLabel, designToolsVBox, pagesLabel, scene.getListViewReference());
        lateralMenuVBox.setPadding(new Insets(50, 0, 0, 0));
        lateralMenuVBox.setSpacing(10);
        lateralMenuVBox.setPrefWidth(150);
        Button expandButton = new Button(">");
        expandButton.setId("#expandButton");
        HBox expandHBox = new HBox(lateralMenuVBox, expandButton);
        expandHBox.setAlignment(CENTER);
        expandHBox.setPrefWidth(Region.USE_COMPUTED_SIZE);
        expandHBox.setMaxWidth(Region.USE_PREF_SIZE);

        expandHBox.setTranslateX(-lateralMenuVBox.getPrefWidth());
        StackPane.setAlignment(expandHBox, Pos.CENTER_LEFT);
        // animation for moving the slider
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(expandHBox.translateXProperty(), -lateralMenuVBox.getPrefWidth())),
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
        expandBoxes.add(expandHBox);

        MenuBar menuBar = generateMenuBar(scene, root);
        menuBar.setId("#menuBar");

        root.getChildren().add(menuBar);
        root.getChildren().add(expandHBox);
        scene.setHmiApp(this);

        return scene;
    }

    /**
     * Permite generar el men?? superior de la aplicaci??n
     *
     * @param scene P??gina donde se agregar?? el men??
     * @param root  Canvas donde se agregar?? el men??
     * @return MenuBar con todos los elementos requeridos por el men??
     */
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
            if (user.getRole().equals("Administrador")) {
                ManageUsersWindow manageUsersWindow = new ManageUsersWindow();
                manageUsersWindow.showAndWait();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error de privilegios", "Para administrar los usuarios, ud debe tener un rol de \"Administrador\"", "");
            }

        });
        userMI.getItems().addAll(changeUserMI, manageUsersMI);
        Menu tagsMI = new Menu("Tags Locales");
        tagsMI.setId("#tagsMI");
        MenuItem createLocalTagMI = new MenuItem("Crear Tag Local");
        createLocalTagMI.setOnAction(mouseEvent -> {
            try {
                ManageLocalTagWindow manageLocalTagWindow = new ManageLocalTagWindow(null);
                manageLocalTagWindow.showAndWait();
                if (manageLocalTagWindow.getTag() != null) {
                    localTags.add(manageLocalTagWindow.getTag());
                }
            } catch (SQLException | IOException e) {
                log(e.getMessage());
            }

        });
        MenuItem manageLocalTagsMI = new MenuItem("Administrar Tag Locales");
        manageLocalTagsMI.setOnAction(mouseEvent -> {
            SelectTagWindow tagWindow = new SelectTagWindow(false, "LocalTags", false, localTags);
            tagWindow.showAndWait();
            updateLocalTags();
        });
        tagsMI.getItems().addAll(createLocalTagMI, manageLocalTagsMI);
        MenuItem propertiesMI = new MenuItem("Conexi??n de Base de Datos");
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
            SelectPagesWindow selectPagesWindow = new SelectPagesWindow(getPagesTitles(), showPages);
            selectPagesWindow.showAndWait();
            generateStagesForPages(selectPagesWindow.getSelectedItems());
        });
        MenuItem importWindowsMI = new MenuItem("Importar Ventanas");
        importWindowsMI.setAccelerator(KeyCombination.keyCombination("Ctrl+I"));
        importWindowsMI.setOnAction(mouseEvent -> {
            try {
                loadSceneData();
            } catch (IOException e) {
                if (e.getMessage() != null)
                    showAlert(Alert.AlertType.ERROR, "Error al cargar el archivo", "Existi?? un error al cargar el archivo", e.getMessage());
            }
        });
        importWindowsMI.setId("#import");
        menuWindows.getItems().add(selectWindowsMI);
        menuWindows.getItems().add(importWindowsMI);
        windowMenus.add(menuWindows);

        Menu menuHelp = new Menu("Ayuda");

        MenuItem userManualMI = new MenuItem("Manual de Usuario");
        userManualMI.setAccelerator(KeyCombination.keyCombination("Ctrl+M"));
        userManualMI.setOnAction(mouseEvent -> {
        });

        menuHelp.getItems().add(userManualMI);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuAlarm, menuConfiguration, menuWindows, menuHelp);

        return menuBar;
    }

    /**
     * Permite iniciar el proceso de apertura de un archivo
     */
    private void openProcess() {
        try {
            if (wasModified) {
                if (showSaveDialog(false, "")) {
                    loadHMIData();
                }
            } else {
                loadHMIData();
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error al Cargar Proyecto", "Existi?? un error al cargar el proyecto", e.getMessage());

        }
    }

    /**
     * Permite generar el men?? de opciones de archivo para el men?? superior
     *
     * @return Men?? con las opciones de archivo
     */
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
        openRecentProjectsMI.setId("#recents");
        ArrayList<String> menuItems = getRecentItems();
        if (!menuItems.isEmpty()) {
            for (int i = 0; i < menuItems.size(); i++) {
                MenuItem recentMenuItem = new MenuItem(menuItems.get(i));
                int finalI = i;
                recentMenuItem.setOnAction(mouseEvent -> loadRecentFileProcess(menuItems.get(finalI)));
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
                showAlert(Alert.AlertType.ERROR, "Error al Guardar", ERROR_WHILE_SAVING_STR, e.getMessage());
                log(e.getMessage());
            }
        });
        MenuItem saveAsMI = new MenuItem("Guardar como");
        saveAsMI.setId("#saveAsMI");
        saveAsMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Shift+G"));
        saveAsMI.setOnAction(mouseEvent -> {
            try {
                this.saveAsHMIData(false);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar Como", ERROR_WHILE_SAVING_STR, e.getMessage());
                log(e.getMessage());
            }
        });
        MenuItem saveAsEncryptedMI = new MenuItem("Guardar como Archivo Protegido");
        saveAsEncryptedMI.setId("#saveAsEncryptedMI");
        saveAsEncryptedMI.setAccelerator(KeyCombination.keyCombination("Ctrl+Alt+G"));
        saveAsEncryptedMI.setOnAction(mouseEvent -> {
            try {
                this.saveAsHMIData(true);
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar Como", ERROR_WHILE_SAVING_STR, e.getMessage());
                log(e.getMessage());
            }
        });

        SeparatorMenuItem runEditSeparatorMI = new SeparatorMenuItem();

        MenuItem runEditMI = new MenuItem(modeLabel);

        editExecuteMenuItems.add(runEditMI);
        runEditMI.setOnAction(actionEvent -> changeMode());


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

    private void log(String e) {
        logger.log(Level.INFO, e);
    }

    /**
     * Permite cambiar el modo de la aplicaci??n
     */
    private void changeMode() {
        if (modeLabel.equals(EDITAR_STR)) {
            loginUser(modeLabel);
            if (!this.user.getRole().equals(OPERATOR_STR)) {
                this.modeLabel = EJECUTAR_STR;
                this.mode = EDITAR_STR;
            }
        } else {
            this.modeLabel = EDITAR_STR;
            this.mode = EJECUTAR_STR;
            enableInputRepresentations(this.mode);
        }
        updateMenuBarsMode(this.modeLabel);
        updateLateralMenuMode();
    }

    /**
     * Permite actualizar el modo del men?? lateral
     */
    private void updateLateralMenuMode() {
        for (HBox hBox : expandBoxes) {
            for (Node content : hBox.getChildren()) {
                if (content.getId() != null && content.getId().equals("#expandButton")) {
                    Button tmpButton = (Button) content;
                    if (tmpButton.getText().equals("<")) {
                        tmpButton.fire();
                    }
                }
            }
            hBox.setDisable(this.mode.equals(EJECUTAR_STR));
        }
    }

    /**
     * Permite actualizar el modo del men?? superior
     *
     * @param mode Modo de aplicaci??n
     */
    private void updateMenuBarsMode(String mode) {
        for (MenuItem menuItem : editExecuteMenuItems) {
            menuItem.setText(mode);
        }

        updateMenuFileMode();

        for (Menu menu : editMenus) {
            menu.setDisable(this.mode.equals(EJECUTAR_STR));
        }

        for (Menu menu : alarmsMenus) {
            menu.setDisable(this.mode.equals(EJECUTAR_STR));
        }

        for (Menu menu : configMenus) {
            menu.setDisable(this.mode.equals(EJECUTAR_STR));
        }

        updateMenuWindowsMode();
    }

    /**
     * Permite actualizar el men?? de ventanas
     */
    private void updateMenuWindowsMode() {
        for (Menu menu : windowMenus) {
            for (MenuItem item : menu.getItems()) {
                if (item.getId() != null && item.getId().equals("#import")) {
                    item.setDisable(this.mode.equals(EJECUTAR_STR));
                }
            }
        }
    }

    /**
     * Permite actualizar el men?? de archivos
     */
    private void updateMenuFileMode() {
        for (Menu menu : fileMenus) {
            for (MenuItem subMenu : menu.getItems()) {
                if (subMenu.getId() != null && (subMenu.getId().equals("#saveMI") || subMenu.getId().equals("#saveAsMI") || subMenu.getId().equals("#saveAsEncryptedMI") || subMenu.getId().equals("#recents"))) {
                    subMenu.setDisable(this.mode.equals(EJECUTAR_STR));
                }
            }
        }
    }

    /**
     * Permite generar el men?? de alarmas
     *
     * @return Men?? para agregarse al men?? superior
     */
    private Menu generateMenuAlarm() {
        // --- Menu View
        Menu menuAlarm = new Menu("Alarmas");
        menuAlarm.setId("#alarmMI");
        MenuItem alarmMI = new MenuItem("Crear Alarma");
        alarmMI.setAccelerator(KeyCombination.keyCombination("Ctrl+A"));
        alarmMI.setOnAction(mouseEvent -> {
            SetAlarmWindow setAlarmWindow = new SetAlarmWindow(this);
            setAlarmWindow.setCreating(true);
            setAlarmWindow.showAndWait();
            if (setAlarmWindow.isDone()) {
                if (setAlarmWindow.getLocalExpression().determineResultType().equals("Flotante") || setAlarmWindow.getLocalExpression().determineResultType().equals("Entero")) {
                    Alarm alarm = new Alarm(
                            setAlarmWindow.getLocalExpression(),
                            setAlarmWindow.getAlarmNameTF().getText(),
                            setAlarmWindow.getAlarmCommentTF().getText()
                    );
                    alarm.setHighLimit(Double.parseDouble(setAlarmWindow.getHighLimitTF().getText()), setAlarmWindow.getHighLimitCheckBox().isSelected());
                    alarm.setHiHiLimit(Double.parseDouble(setAlarmWindow.getHiHiLimitTF().getText()), setAlarmWindow.getHiHiLimitCheckBox().isSelected());
                    alarm.setLowLimit(Double.parseDouble(setAlarmWindow.getLowLimitTF().getText()), setAlarmWindow.getLowLimitCheckBox().isSelected());
                    alarm.setLoloLimit(Double.parseDouble(setAlarmWindow.getLoloLimitTF().getText()), setAlarmWindow.getLoloLimitCheckBox().isSelected());
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

    /**
     * VBox con las opciones para agregar representaciones gr??ficas al canvas
     *
     * @param scene P??gina donde se agregar?? el VBox
     * @param root  Canvas donde se agregar?? el VBox
     * @return VBox con las opciones requeridas
     */
    private VBox generateDesignVBox(HMIScene scene, HMICanvas root) {
        Button rectangleBtn = new Button("");
        rectangleBtn.setTooltip(new Tooltip("Rect??ngulo"));
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
        buttonBtn.setTooltip(new Tooltip("Bot??n"));
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
        symbolBtn.setTooltip(new Tooltip("S??mbolo HMI"));
        symbolBtn.setGraphic(new ImageView(symbolIcon));
        Button imageBtn = new Button("");
        imageBtn.setTooltip(new Tooltip("Imagen"));
        imageBtn.setGraphic(new ImageView(imageIcon));
        Button pushbuttonBtn = new Button("");
        pushbuttonBtn.setTooltip(new Tooltip("Bot??n Pulsador"));
        pushbuttonBtn.setGraphic(new ImageView(pushbuttonIcon));
        Button lineBtn = new Button("");
        lineBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("Line");
        });
        lineBtn.setTooltip(new Tooltip("L??nea"));
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
        trendGraphBtn.setTooltip(new Tooltip("Gr??fico de Tendencias"));
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

    /**
     * Permite iniciar el proceso de inicio de sesi??n
     *
     * @param mode Modo de la aplicaci??n
     */
    public void loginUser(String mode) {
        LogInWindow logInWindow = new LogInWindow();
        logInWindow.showAndWait();
        if (logInWindow.getLoggedUser() != null) {
            this.user = logInWindow.getLoggedUser();
            if (this.user.getRole().equals(OPERATOR_STR) && mode.equals(EDITAR_STR)) {
                showAlert(Alert.AlertType.ERROR, "Error de Privilegios", "Error un usuario con el Rol \"Operador\" no puede editar el proyecto", "");
            } else {
                enableInputRepresentations(mode);
            }
        }
    }

    /**
     * Permite iniciar el hilo de autobloqueo de representaciones gr??ficas
     */
    public void setAutoBlockObjectsTimeline() {
        this.autoBlockTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            log("Executing autoBlockTimeline");
                            user = new HMIUser("", "", "", "", OPERATOR_STR, "");
                            enableInputRepresentations(mode);
                        }), new KeyFrame(Duration.seconds(1)));
        this.autoBlockTimeline.setDelay(Duration.seconds(60));
    }

    /**
     * Permite definir el hilo de actualizaci??n de tags locales
     */
    public void setRefreshLocalTagsTimeline() {
        Timeline refreshLocalTagsTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> refreshLocalTags()), new KeyFrame(Duration.seconds(1)));
        refreshLocalTagsTimeline.setCycleCount(Animation.INDEFINITE);
        refreshLocalTagsTimeline.play();
    }

    /**
     * Permite actualizar los tags locales empezando por las entradas
     */
    public void refreshLocalTags() {
        updateLocalTagsFromInputs();
        updateLocalTags();
    }

    /**
     * Permite actualizar los tags
     */
    public void updateLocalTags() {
        updateTagsInObjects();
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

    /**
     * Permite actualizar los tags de los objetos
     */
    public void updateTagsInObjects() {
        for (int i = 0; i < root.getShapeArrayList().size(); i++) {
            for (Tag localTag : localTags) {
                root.getShapeArrayList().get(i).updateTag(localTag);
            }
        }
    }

    /**
     * Permite actualizar los tags de entradas
     */
    public void updateLocalTagsFromInputs() {
        ArrayList<CanvasObject> canvasObjects = root.getShapeArrayList();
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < canvasObjects.size(); i++) {
            CanvasObject canvasObject = canvasObjects.get(i);
            if (max == null && canvasObject.getCanvasObjectData().getSuperType().equals("TagInputObject")) {
                max = canvasObject.getLastTimeSelected();
                index = i;
            } else if (canvasObject.getLastTimeSelected() != null && max != null && max.isBefore(canvasObject.getLastTimeSelected())) {
                max = canvasObjects.get(i).getLastTimeSelected();
                index = i;
            }
        }
        for (int i = 0; i < localTags.size() && index > -1; i++) {
            Tag tagToUpdate = canvasObjects.get(index).getCanvasObjectData().getTag();
            if (localTags.get(i).compareToTag(tagToUpdate)) {
                localTags.set(i, tagToUpdate);
            }
        }

    }

    /**
     * Permite a??adir una alarma a la aplicaci??n
     *
     * @param alarm Alarma para a??adir a la aplicaci??n
     */
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

    /**
     * Permite obtener los archivos de proyecto recientemente utilizados
     *
     * @return ArrayList de strings con los paths de archivos de proyecto recientes
     */
    private ArrayList<String> getRecentItems() {
        String recentFilesFilePath = DBConnection.getWorkingDirectory() + File.separator + "recentFiles.json";
        RecentUsedFilesData recentUsedFilesData;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(recentFilesFilePath))) {
            recentUsedFilesData = gson.fromJson(bufferedReader, RecentUsedFilesData.class);
            return new ArrayList<>(recentUsedFilesData.getRecentlyUsedFilePaths());
        } catch (IOException e) {
            log(e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Permite generar las alarmas de tipo double del proyecto
     *
     * @param manageableAlarm Alarma administrable del proyecto
     */
    private void generateDoubleProjectAlarms(Alarm manageableAlarm) {
        if (manageableAlarm.isHighAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            alarm.setHighLimit(manageableAlarm.getHighLimit(), manageableAlarm.isHighAlarmEnabled());
            alarm.setHiHiLimit(manageableAlarm.getHiHiLimit(), false);
            alarm.setLowLimit(manageableAlarm.getLowLimit(), false);
            alarm.setLoloLimit(manageableAlarm.getLoloLimit(), false);
            projectAlarms.add(alarm);
        }
        if (manageableAlarm.isHiHiAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            alarm.setHighLimit(manageableAlarm.getHighLimit(), false);
            alarm.setHiHiLimit(manageableAlarm.getHiHiLimit(), manageableAlarm.isHiHiAlarmEnabled());
            alarm.setLowLimit(manageableAlarm.getLowLimit(), false);
            alarm.setLoloLimit(manageableAlarm.getLoloLimit(), false);
            projectAlarms.add(alarm);
        }
        if (manageableAlarm.isLoloAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            alarm.setHighLimit(manageableAlarm.getHighLimit(), false);
            alarm.setHiHiLimit(manageableAlarm.getHiHiLimit(), false);
            alarm.setLowLimit(manageableAlarm.getLowLimit(), false);
            alarm.setLoloLimit(manageableAlarm.getLoloLimit(), manageableAlarm.isLoloAlarmEnabled());
            projectAlarms.add(alarm);
        }
        if (manageableAlarm.isLowAlarmEnabled()) {
            Alarm alarm = new Alarm(
                    manageableAlarm.getExpression(),
                    manageableAlarm.getName(),
                    manageableAlarm.getComment()
            );
            alarm.setHighLimit(manageableAlarm.getHighLimit(), false);
            alarm.setHiHiLimit(manageableAlarm.getHiHiLimit(), false);
            alarm.setLowLimit(manageableAlarm.getLowLimit(), manageableAlarm.isLowAlarmEnabled());
            alarm.setLoloLimit(manageableAlarm.getLoloLimit(), false);
            projectAlarms.add(alarm);
        }
    }

    /**
     * Permite iniciar el proceso de guardar de la aplicaci??n
     *
     * @throws IOException
     */
    private void saveHMIDataProcess() throws IOException {
        if (currentProjectFilePath != null) {
            this.saveHMIData(currentProjectFilePath);
        } else {
            this.saveAsHMIData(false);
        }
    }

    /**
     * Permite iniciar el proceso de carga del proyecto
     *
     * @throws IOException
     */
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

    /**
     * Permite iniciar el proceso carga de proyecto desde un path de archivo de proyecto
     *
     * @param filenamePath Path del archivo de proyecto
     * @throws IOException
     */
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
        loadHMIDataProcess(localHmiAppData, filenamePath);
    }

    /**
     * Permite iniciar el proceso de carga de proyecto
     *
     * @param localHmiAppData Datos de aplicaci??n a cargarse
     * @param filenamePath    Path del archivo a cargarse
     */
    private void loadHMIDataProcess(HMIAppData localHmiAppData, String filenamePath) {
        if (localHmiAppData != null) {
            if (localHmiAppData.getType() == null) {
                if (showAlert(Alert.AlertType.WARNING, FILE_ERROR_TITLE, "??Intentar cargarlo de todas formas?", "")) {
                    loadHMIData(localHmiAppData, filenamePath);
                }
            } else if (localHmiAppData.getType().equals(HMI_PROJECT)) {
                loadHMIData(localHmiAppData, filenamePath);
            } else {
                if (showAlert(Alert.AlertType.WARNING, FILE_ERROR_TITLE, "??Intentar cargarlo de todas formas?", "")) {
                    loadHMIData(localHmiAppData, filenamePath);
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, FILE_ERROR_TITLE, "No se pudo obtener ning??n dato de proyecto desde el archivo", "");
        }
    }

    /**
     * Permite realizar la carga de un archivo reciente
     * @param recentFileNamePath Path del archivo a cargarse
     */
    private void loadRecentFileProcess(String recentFileNamePath){
        try {
            if (wasModified) {
                if (showSaveDialog(false, "")) {
                    this.loadHMIData(recentFileNamePath);
                }
            } else {
                this.loadHMIData(recentFileNamePath);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error al cargar archivo reciente", "Existi?? un error al cargar un archivo reciente", e.getMessage());
        }
    }

    /**
     * Permite obtener los datos de aplicaci??n encriptados en un archivo
     *
     * @param gson         Objeto para leer JSON
     * @param filenamePath Path del archivo de proyecto encriptado
     * @return Datos de aplicaci??n para cargarse
     * @throws IOException
     */
    private HMIAppData getEncryptedHmiAppData(Gson gson, String filenamePath) throws IOException {
        HMIAppData localHmiAppData;
        String rawJson = decipherRawJson(filenamePath);
        localHmiAppData = gson.fromJson(rawJson, HMIAppData.class);
        return localHmiAppData;
    }

    /**
     * Permite decifrar un JSON cifrado a partir de mostrar la ventana de ingreso de contrase??a
     *
     * @param filenamePath Path del archivo de proyecto a cargarse
     * @return JSON dentro de un string
     * @throws IOException
     */
    private String decipherRawJson(String filenamePath) throws IOException {
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
                encryptor.setAlgorithm(CIPHER_ALGORITHM);
                try {
                    return encryptor.decrypt(rawEncryptedDataSB.toString());
                } catch (EncryptionOperationNotPossibleException e) {
                    wasEncryptedFileInputPasswordCanceled = !showAlert(Alert.AlertType.ERROR, "Contrase??a incorrecta", "La contrase??a ingresada no es correcta, reintente", "");
                }
            } finally {
                wasEncryptedFileInputPasswordCanceled = false;
            }
        } else {
            wasEncryptedFileInputPasswordCanceled = true;
        }
        return "";
    }

    /**
     * Permite cargar un archivo de proyecto
     *
     * @param localHmiAppData Objeto de datos de aplicaci??n
     * @param filenamePath    Path del archivo a cargarse
     */
    private void loadHMIData(HMIAppData localHmiAppData, String filenamePath) {
        this.setHmiAppData(localHmiAppData);
        this.currentProjectFilePath = filenamePath;
        String[] filenameArr = filenamePath.split(File.separator);
        currentFilename = filenameArr[filenameArr.length - 1];
        this.setWasModified(false);
        updateTitleWithFilename();
        addRecentUsedFilePath(filenamePath);
    }

    /**
     * Permite a??adir un archivo de proyecto al registro de archivos recientes
     *
     * @param filenamePath Path del archivo de proyecto
     */
    private void addRecentUsedFilePath(String filenamePath) {
        String recentFilesFilePath = DBConnection.getWorkingDirectory() + File.separator + "recentFiles.json";
        RecentUsedFilesData recentUsedFilesData = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(recentFilesFilePath))) {
            recentUsedFilesData = gson.fromJson(bufferedReader, RecentUsedFilesData.class);
        } catch (IOException e) {
            log(e.getMessage());
        }

        RecentUsedFilesCache recentUsedFilesCache;
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
            log(e.getMessage());
        }
    }

    /**
     * Permite guardar un archivo de proyecto nuevo
     *
     * @param encrypted Bandera que define si se exportar?? un archivo de proyecto encriptado
     * @throws IOException
     */
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

    /**
     * Permite preparar los datos de la aplicaci??n
     */
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
     * Este m??todo inicia el proceso para guardar el proyecto dentro de un archivo json.
     *
     * @throws IOException Si hay alg??n problema en la lectura o escritura.
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
     * Este m??todo inicia el proceso para guardar el proyecto dentro de un archivo json.
     *
     * @throws IOException Si hay alg??n problema en la lectura o escritura.
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
                try (Writer writer = Files.newBufferedWriter(Path.of(filenamePath))) {
                    String rawJson = gson.toJson(this.hmiAppData);
                    StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                    encryptor.setPassword(setFilePasswordWindow.getPassword());
                    encryptor.setAlgorithm(CIPHER_ALGORITHM);
                    String encryptedJSON = encryptor.encrypt(rawJson);
                    writer.write(encryptedJSON);
                    this.currentProjectFilePath = filenamePath;
                    this.setWasModified(false);
                    addRecentUsedFilePath(filenamePath);
                    updateTitleWithFilename();
                }
            }
        }
    }

    /**
     * Este m??todo permite cambiar de estados a los objetos de entrada de datos,
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
        updateLateralMenuMode();
        if (mode.equals(EJECUTAR_STR) && this.user.getRole().equals("Administrador")) {
            setAutoBlockObjectsTimeline();
            this.autoBlockTimeline.play();
            setRefreshLocalTagsTimeline();
        }
    }

    /**
     * Este m??todo permite la creaci??n de ventanas para las Escenas si son mayores que uno,
     * si es solo uno cambia la Escena de la Ventana mostrada a la seleccionada
     * Adem??s de crear ventanas(Stage) adicionales tambi??n las muestra
     *
     * @param selectedPages ArrayList de los t??tulos de las P??ginas Seleccionadas
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
     * Este m??todo permite la creaci??n de una nueva ventana a partir de una HMIScene previamente existente.
     *
     * @param scene La p??gina creada del tipo HMIScene
     */
    private void generateStage(HMIScene scene) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(scene.getTitle() + " - " + HMI_TITLE);
        stage.show();
        createdWindows.add(stage);
    }

    /**
     * Buscar el ??ndice de una determinada Ventana basada en su t??tulo.
     *
     * @param title T??tulo de la p??gina
     * @return El ??ndice de la p??gina en el ArrayList
     */
    private int getIndexForStageWithScene(String title) {
        int index = -1;
        for (int i = 0; i < createdWindows.size(); i++) {
            if (((HMIScene) createdWindows.get(i).getScene()).getId().equals(title)) {
                index = i;
            }
        }
        return index;
    }

    /**
     * Si se solicita el cambio de una sola p??gina esta no generar?? una ventana nueva sino, que cambiar?? la escena actual
     * de la aplicaci??n a la seleccionada basada en su t??tulo
     *
     * @param sceneTitle T??tulo de la p??gina a seleccionarse
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
     * Cuando se crea una nueva p??gina todas las dem??s requieren actualizar la lista de p??ginas disponibles.
     *
     * @param itemsForComboBox ArrayList que contiene todos los t??tulos de las p??ginas.
     */
    private void updateScenesInListView(ArrayList<String> itemsForComboBox) {
        for (HMIScene page : pages) {
            page.setItems(itemsForComboBox);
        }
    }

    /**
     * Elimina la p??gina seleccionada basada en el t??tulo, la elimina del arraylist de p??ginas y de los t??tulos de
     * p??ginas disponibles en las p??ginas restantes.
     *
     * @param sceneTitle T??tulo de la Escena a eliminarse.
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
     * Actualiza las propiedades de una escena seleccionada basada en su t??tulo
     *
     * @param sceneTitle T??tulo de la escena a actualizar atributos.
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
                this.pages.set(index, scene);
                for (HMIScene page : pages) {
                    pagesTitles.set(index, page.getTitle());
                    page.updateItem(index, scene.getTitle());
                }
                setWasModified(true);
            }
        }
    }

    /**
     * Permite iniciar el proceso para a??adir una nueva p??gina.
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
     * Permite generar un nombre no duplicado cuando se realiza una importaci??n desde el archivo
     * @param name Nombre de la p??gina
     * @param copyNumber N??mero para agregar al nombre de la p??gina
     * @return Nombre de la p??gina que no estar?? duplicado
     */
    private String generateImportedPageName(String name, int copyNumber) {
        if (this.pagesTitles.contains(name) && name.matches(".*[(]\\d*[)]")) {
            copyNumber++;
            return generateImportedPageName(name.replaceAll("[(]\\d*[)]", "(" + copyNumber + ")"), copyNumber);
        } else if (!name.matches(".*[(]\\d*[)]")) {
            copyNumber++;
            name = name + "(" + copyNumber + ")";
            if (this.pagesTitles.contains(name)){
                return generateImportedPageName(name, copyNumber);
            }else{
                return name;
            }
        } else{
            return name;
        }
    }

    /**
     * Este m??todo a??ade una nueva p??gina creada previamente por el proceso addNewScene.
     *
     * @param newScene HMIScene creada a trav??s del proceso addNewScene
     */
    private void addScene(HMIScene newScene) {
        if (this.pagesTitles.contains(newScene.getTitle())) {
            newScene.setTitle(generateImportedPageName(newScene.getTitle(), 0));
            newScene.getHmiSceneData().setTitle(newScene.getTitle());
        }
        this.pages.add(newScene);
        this.selectedPage = newScene.getTitle();
        this.pagesTitles.add(newScene.getTitle());
        updateScenesInListView(pagesTitles);
        mainStage.setScene(newScene);
        mainStage.setTitle(newScene.getTitle() + " - " + HMI_TITLE);
        updateTitleWithFilename();
    }

    /**
     * Permite a??adir una p??gina al proyecto
     *
     * @param hmiSceneData Datos de la p??gina
     */
    private void addScene(HMISceneData hmiSceneData) {
        HMIScene hmiScene = generatePage(hmiSceneData.getTitle(), hmiSceneData.getSceneCommentary(), hmiSceneData.getBackground().getColor());
        addScene(hmiScene);
        hmiScene.setHmiSceneData(hmiSceneData);
    }

    /**
     * Permite exportar los datos de la p??gina seleccionada a un archivo
     *
     * @param sceneName Nombre de la p??gina a exportar
     * @param encrypted Bandera para definir si se exportar?? el archivo de forma encriptada
     */
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
                    new FileChooser.ExtensionFilter(WINDOW_STR, "*.window", "*.WINDOW")
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
                    showSaveDialog(true, e.getMessage());
                }
            } else {
                try {
                    exportSceneData(scene.getHmiSceneData(), file.getAbsolutePath());
                } catch (IOException e) {
                    showSaveDialog(true, e.getMessage());
                }
            }
        }
    }

    /**
     * Permite exportar los datos de una p??gina
     *
     * @param hmiSceneData Datos de la p??gina
     * @param filePathName Path del archivo dodne se exportar??
     * @throws IOException
     */
    public void exportSceneData(HMISceneData hmiSceneData, String filePathName) throws IOException {
        Gson gson = new Gson();
        if (filePathName != null) {
            Writer writer = Files.newBufferedWriter(Path.of(filePathName));
            gson.toJson(hmiSceneData, writer);
            writer.close();
        }
    }

    /**
     * Permite cargar los datos de una escena a una p??gina
     *
     * @throws IOException Si existen problemas para leer los datos de la p??gina
     */
    private void loadSceneData() throws IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccione un archivo de ventana de proyecto LibreHMI");
        fileChooser.setInitialDirectory(
                new File(System.getProperty(USER_HOME))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(WINDOW_STR, "*.window", "*.WINDOW"),
                new FileChooser.ExtensionFilter("SECURE WINDOW", "*.swindow", "*.SWINDOW"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        loadSceneData(file.getAbsolutePath());
    }

    /**
     * Permite cargar los datos de una escena de una p??gina
     *
     * @param filenamePath Path del archivo de p??gina
     * @throws IOException Si existen problemas para leer los datos de la p??gina
     */
    private void loadSceneData(String filenamePath) throws IOException {
        String[] filenamePathArr = filenamePath.split("\\.");
        String type = filenamePathArr[filenamePathArr.length - 1];
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        HMISceneData localHmiAppData = null;
        if (type.equals("window") || type.equals(WINDOW_STR)) {
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
            showAlert(Alert.AlertType.ERROR, FILE_ERROR_TITLE, "No se pudo obtener ning??n dato de proyecto desde el archivo", "");
        }
    }

    /**
     * Permite obtener los datos de una p??gina cifrada
     *
     * @param gson         Objeto de lectura de JSON
     * @param filenamePath Path de archivo de p??gina
     * @return Datos de p??gina
     * @throws IOException
     */
    private HMISceneData getEncryptedHmiSceneData(Gson gson, String filenamePath) throws IOException {
        HMISceneData localHmiSceneData;
        String rawJson = decipherRawJson(filenamePath);
        localHmiSceneData = gson.fromJson(rawJson, HMISceneData.class);
        return localHmiSceneData;
    }

    /**
     * Permite exportar los datos de una p??gina de forma encriptada
     *
     * @param hmiSceneData Datos de p??gina a exportarse
     * @param filePathName Path del archivo a exportarse
     * @throws IOException
     */
    public void exportSceneEncryptedData(HMISceneData hmiSceneData, String filePathName) throws IOException {
        SetFilePasswordWindow setFilePasswordWindow = new SetFilePasswordWindow(true);
        setFilePasswordWindow.showAndWait();
        if (!setFilePasswordWindow.isCanceled()) {
            Gson gson = new Gson();
            if (filePathName != null) {
                try (Writer writer = Files.newBufferedWriter(Path.of(filePathName))) {
                    String rawJson = gson.toJson(hmiSceneData);
                    StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
                    encryptor.setPassword(setFilePasswordWindow.getPassword());
                    encryptor.setAlgorithm(CIPHER_ALGORITHM);
                    String encryptedJSON = encryptor.encrypt(rawJson);
                    writer.write(encryptedJSON);
                }
            }
        }

    }

    /**
     * Permite actualizar el t??tulo de la aplicaci??n con el nombre de archivo de proyecto
     */
    public void updateTitleWithFilename() {
        if (this.currentFilename != null && !mainStage.getTitle().contains(this.currentFilename)) {
            mainStage.setTitle(this.currentFilename + " - " + mainStage.getTitle());
        }
    }

    /**
     * Permite obtener el ??ndice de una alarma requerida
     *
     * @param name Nombre de la alarma
     * @return ??ndice de la alarma
     */
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
     * Busca el ??ndice de la Escena basada en su t??tulo
     *
     * @param sceneTitle T??tulo de la escena buscada
     * @return El ??ndice de la escena en el arrayList
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
     * Muestra una Alerta de confirmaci??n de eliminaci??n de la una p??gina
     *
     * @param sceneTitle T??tulo de la Escena a eliminarse
     * @return Booleano: - True si se confirma eliminaci??n
     * - False si se cancela
     */
    private boolean confirmDelete(String sceneTitle) {
        return showAlert(Alert.AlertType.CONFIRMATION, "Confirmar eliminaci??n", "Desea eliminar la p??gina seleccionada \"" + sceneTitle + "\"?", "");
    }

    /**
     * Permite generar los esquemas de base de datos si estas no existen
     */
    private void generateDatabase() {
        try {
            if (!DBConnection.checkIfTablesFromSchemaBDDriverAreReady()) {
                DBConnection dbConnection = new DBConnection();
                dbConnection.generateSchemaBDDriverEIP();
            }
            if (!DBConnection.tableExistsInSchema("Users", "HMIUsers")) {
                DBConnection.generateSchemaHMIUsers();
                showAlert(Alert.AlertType.INFORMATION, "Se ha creado la base de datos de usuarios", "Se creo la base de datos de usuarios con el usuario \"admin\" con contrase??a \"12345\",\npor su seguridad actualice la contrase??a desde la ventana de administraci??n de usuarios", "");
            }
        } catch (SQLException sqlException) {
            showAlert(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", "Verifique que tiene acceso a MySQL a trav??s de la ventana de credenciales que se mostrar?? a continuaci??n", ERROR_STR + sqlException.getMessage());
            log(sqlException.getMessage());
            SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
            saveDatabaseCredentialsWindow.showAndWait();
            generateDatabase();
        } catch (IOException e) {
            if (!showAlertWithCredentials()) {
                generateDatabase();
            }
            log(e.getMessage());
        }
    }

    /**
     * Permite mostrar el di??logo de guardar cambios
     *
     * @param errorMode Bandera para indicar que se trata de un error
     * @param message   Mensaje a mostrarse en la ventana
     * @return false si el usuario da clic en Cancelar
     */
    public boolean showSaveDialog(boolean errorMode, String message) {
        Alert alert;
        if (errorMode) {
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al guardar el archivo");
            alert.setHeaderText("Existi?? un error al tratar de guardar el archivo");
            alert.setContentText(message);
        } else {
            alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(ALERT_SAVE_TITLE);
            alert.setHeaderText(ALERT_SAVE_DESCRIPTION);
        }

        ButtonType saveButton = new ButtonType("Guardar", ButtonBar.ButtonData.YES);
        ButtonType dontSaveButton = new ButtonType("No Guardar", ButtonBar.ButtonData.NO);
        ButtonType cancelButton = new ButtonType(CANCEL_STR, ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(dontSaveButton, cancelButton, saveButton);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {
            alert.close();
            try {
                saveHMIDataProcess();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error al Guardar", ERROR_WHILE_SAVING_STR, e.getMessage());
                log(e.getMessage());
            }
            return true;
        } else if (result.isPresent() && result.get() == dontSaveButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        }
        return true;
    }

    /**
     * Permite mostrar una ventana de di??logo para mostrar la ventana de definici??n de propiedades luego de dar clic en
     * OK
     *
     * @return true si el usuario da clic en OK
     */
    public boolean showAlertWithCredentials() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error al conectarse a la base de datos");
        alert.setHeaderText("El archivo de propiedades no se encontr?? pulse OK para mostrar la ventana de ingreso de credenciales para conectarse a la base de datos");
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType(CANCEL_STR, ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cancelButton, okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
            saveDatabaseCredentialsWindow.showAndWait();
            return saveDatabaseCredentialsWindow.isCancelled();
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
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

    public ArrayList<Tag> getLocalTags() {
        return localTags;
    }

    public void setLocalTags(ArrayList<Tag> localTags) {
        this.localTags = localTags;
    }
}
