package andrade.luis.hmiethernetip;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import andrade.luis.hmiethernetip.models.Alarm;
import andrade.luis.hmiethernetip.models.HMIAppData;
import andrade.luis.hmiethernetip.models.HMISceneData;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.models.canvas.CanvasObjectData;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import andrade.luis.hmiethernetip.util.DBConnection;
import andrade.luis.hmiethernetip.views.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    private static final String DEFAULT_PAGE_NAME = "Página 1";
    private final ArrayList<HMIScene> pages = new ArrayList<>();
    private final ArrayList<Stage> createdWindows = new ArrayList<>();
    private String currentProjectFilePath = null;
    private boolean wasModified = false;
    private final BooleanProperty updateTitleFlag = new SimpleBooleanProperty(false);
    private HMIAppData hmiAppData = new HMIAppData();
    private ArrayList<String> pagesTitles = new ArrayList<>();
    private ArrayList<Alarm> projectAlarms = new ArrayList<>();
    private ArrayList<Alarm> manageableAlarms = new ArrayList<>();
    private static final String HMI_TITLE = "HMI";
    private static final String ALERT_SAVE_TITLE = "¿Desea guardar los cambios del proyecto actual?";
    private static final String ALERT_SAVE_DESCRIPTION = "Los cambios se perderán si elige No Guardar";
    private HMIUser user;

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
            HMIScene hmiScene = generatePage(hmiSceneData.getTitle(), hmiSceneData.getSceneCommentary(), hmiSceneData.getBackground().getColor());
            addScene(hmiScene);
            hmiScene.setHmiSceneData(hmiSceneData);
        }
        manageableAlarms.clear();
        projectAlarms.clear();
        for(int i=0;i<this.hmiAppData.getHmiAlarms().size();i++){
            addAlarm(this.hmiAppData.getHmiAlarms().get(i));
        }
    }

    public void clearProject() {
        this.pages.clear();
        this.pagesTitles.clear();
        this.wasModified=false;
        this.currentProjectFilePath=null;
    }

    public void createNewProject() {
        logger.log(Level.INFO,"Closing Project...");
        if(wasModified){
            if(showAlert(Alert.AlertType.CONFIRMATION,ALERT_SAVE_TITLE,ALERT_SAVE_DESCRIPTION,false, true)){
                this.clearProject();
                HMIScene scene = generatePage(DEFAULT_PAGE_NAME, "", Color.WHITESMOKE);
                addScene(scene);
            }
        }else{
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
        mainStage.setOnCloseRequest(windowEvent -> {
            logger.log(Level.INFO,"Closing...");
            if(wasModified){
                if(showAlert(Alert.AlertType.CONFIRMATION,ALERT_SAVE_TITLE,ALERT_SAVE_DESCRIPTION,false, true)){
                    logger.log(Level.INFO,"CANCELED");
                }else{
                    windowEvent.consume();
                }
            }else{
                logger.log(Level.INFO,"CLOSED...");
            }
        });
        generateDatabase();
        HMIScene scene = generatePage(DEFAULT_PAGE_NAME, "", Color.WHITESMOKE);
        addScene(scene);
        mainStage.setTitle(scene.getTitle() +" - "+ HMI_TITLE);
        mainStage.setScene(scene);
        mainStage.show();

        updateTitleFlag.addListener((observableValue, oldBoolean, newBoolean) -> {
            if(Boolean.TRUE.equals(newBoolean)){
                mainStage.setTitle(mainStage.getTitle()+"*");
            }else if(Boolean.FALSE.equals(newBoolean) && mainStage.getTitle().endsWith("*")){
                mainStage.setTitle(mainStage.getTitle().substring(0,mainStage.getTitle().length()-1));
            }
        });
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
            root.setType("SystemDateTime");
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
        Button alarmDisplayBtn = new Button("Alarm Display");
        alarmDisplayBtn.setOnAction(mouseEvent -> {
            scene.getCanvas().setAddOnClickEnabled(true);
            root.setType("AlarmDisplay");
        });
        Button manageUsersBtn = new Button("Manage Users");
        Button registerUserBtn = new Button("Register");
        Button logIntUserBtn = new Button("Log In");
        Button propertiesBtn = new Button("Guardar Propiedades");
        Button symbolBtn = new Button("Symbol");
        Button imageBtn = new Button("Image");
        Button pushbuttonBtn = new Button("Pushbutton");
        Button playBtn = new Button("Play");
        Button stopBtn = new Button("Stop");
        Button defaultBtn = new Button("Default");
        Button saveBtn = new Button("Guardar");
        Button saveAsBtn = new Button("Guardar Como");
        Button loadBtn = new Button("Cargar");
        Button newBtn = new Button("Nuevo");
        Button alarmBtn = new Button("Alarma");
        Button manageAlarmBtn = new Button("Administrar Alarmas");
        HBox hbox = new HBox(rectangleBtn, systemDateTimeLabelBtn, textBtn, buttonBtn,alarmDisplayBtn);
        HBox secondHBox = new HBox(sliderBtn, textFieldBtn, manageUsersBtn, registerUserBtn);
        HBox thirdHBox = new HBox(saveBtn,saveAsBtn ,loadBtn, newBtn);
        HBox fourthHBox = new HBox(logIntUserBtn, propertiesBtn, imageBtn, symbolBtn, pushbuttonBtn);
        HBox fifthHBox = new HBox(playBtn, stopBtn, defaultBtn,alarmBtn,manageAlarmBtn);

        ArrayList<String> itemsForComboBox = new ArrayList<>(List.of(scene.getTitle()));
        ListView<String> listViewReference = new ListView<>();
        scene.setListViewReference(listViewReference);
        scene.setItems(itemsForComboBox);

        VBox vbox = new VBox(hbox, secondHBox, thirdHBox, fourthHBox, fifthHBox, scene.getListViewReference());
        vbox.setSpacing(10);
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
        propertiesBtn.setOnAction(mouseEvent -> {
            SaveDatabaseCredentialsWindow saveDatabaseCredentialsWindow = new SaveDatabaseCredentialsWindow();
            saveDatabaseCredentialsWindow.show();
        });
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
        playBtn.setOnAction(mouseEvent -> enableInputRepresentations("Play"));
        stopBtn.setOnAction(mouseEvent -> enableInputRepresentations("Stop"));
        defaultBtn.setOnAction(mouseEvent -> enableInputRepresentations("Default"));
        saveBtn.setOnAction(mouseEvent -> {
            try {
                this.saveHMIDataProcess();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR,"Error al Guardar","Error:"+e.getMessage(),false,false);
                e.printStackTrace();
            }
        });
        saveAsBtn.setOnAction(mouseEvent -> {
            try {
                this.saveAsHMIData();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR,"Error al Guardar Como","Error:"+e.getMessage(),false,false);
                e.printStackTrace();
            }
        });
        loadBtn.setOnAction(mouseEvent -> {
            try {
                logger.log(Level.INFO,"Closing Project...");
                if(wasModified){
                    if(showAlert(Alert.AlertType.CONFIRMATION,ALERT_SAVE_TITLE,ALERT_SAVE_DESCRIPTION,false, true)){
                        loadHMIData();
                    }
                }else{
                    loadHMIData();
                }
            } catch (FileNotFoundException e) {
                showAlert(Alert.AlertType.ERROR,"Error al Cargar Proyecto","Error:"+e.getMessage(),false,false);
                e.printStackTrace();
            }
        });
        newBtn.setOnAction(mouseEvent -> this.createNewProject());
        alarmBtn.setOnAction(mouseEvent -> {
           SetAlarmWindow setAlarmWindow = new SetAlarmWindow();
           setAlarmWindow.showAndWait();
           if(setAlarmWindow.getLocalExpression().determineResultType().equals("Flotante") || setAlarmWindow.getLocalExpression().determineResultType().equals("Entero")){
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
           }else if(setAlarmWindow.getLocalExpression().determineResultType().equals("Bool")){
               Alarm alarm = new Alarm(
                       setAlarmWindow.getLocalExpression(),
                       setAlarmWindow.getTrueRadioButton().isSelected(),
                       true,
                       setAlarmWindow.getAlarmNameTF().getText(),
                       setAlarmWindow.getAlarmCommentTF().getText()
               );
               addAlarm(alarm);
           }
        });
        manageAlarmBtn.setOnAction(mouseEvent ->{
           ManageAlarmsWindow manageAlarmsWindow = new ManageAlarmsWindow((ArrayList<Alarm>) manageableAlarms.clone());
           manageAlarmsWindow.showAndWait();
           manageableAlarms.clear();
           projectAlarms.clear();
           for(int i=0;i<manageAlarmsWindow.getAlarmsList().size();i++){
               addAlarm(manageAlarmsWindow.getAlarmsList().get(i));
           }
        });
        scene.setHmiApp(this);

        return scene;
    }

    private void addAlarm(Alarm alarm){
        if(alarm.getExpression().determineResultType().equals("Flotante") || alarm.getExpression().determineResultType().equals("Entero")){
            manageableAlarms.add(alarm);
            generateDoubleProjectAlarms(alarm);
        } else if(alarm.getExpression().determineResultType().equals("Bool")){
            manageableAlarms.add(alarm);
            projectAlarms.add(alarm);
        }
    }

    private void generateDoubleProjectAlarms(Alarm manageableAlarm) {
        if(manageableAlarm.isHighAlarmEnabled()){
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
        if(manageableAlarm.isHiHiAlarmEnabled()){
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
        if(manageableAlarm.isLoloAlarmEnabled()){
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
        if(manageableAlarm.isLowAlarmEnabled()){
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
        if(currentProjectFilePath!=null){
            this.saveHMIData(currentProjectFilePath);
        }else{
            this.saveAsHMIData();
        }
    }

    private void loadHMIData() throws FileNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccione un archivo de configuración");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json", "*.JSON"),
                new FileChooser.ExtensionFilter("Todos los archivos", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsolutePath()));

        HMIAppData localHmiAppData = gson.fromJson(bufferedReader, HMIAppData.class);
        if(localHmiAppData != null){
            this.setHmiAppData(localHmiAppData);
            this.currentProjectFilePath = file.getAbsolutePath();
            this.setWasModified(false);
            if(!mainStage.getTitle().contains(file.getName())){
                mainStage.setTitle(file.getName()+" - "+mainStage.getTitle());
            }
        }
    }

    private void saveAsHMIData() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON", "*.json", "*.JSON")
        );
        fileChooser.setTitle("Guardar Proyecto");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );

        File file = fileChooser.showSaveDialog(null);
        if(file!=null){
            saveHMIData(file.getAbsolutePath());
        }
    }

    /**
     * Este método inicia el proceso para guardar el proyecto dentro de un archivo json.
     *
     * @throws IOException Si hay algun problema en la lectura o escritura.
     */
    private void saveHMIData(String filenamePath) throws IOException {
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
        Gson gson = new Gson();
        if(filenamePath != null){
            String[] filenameArr = filenamePath.split(File.separator);
            String filename = filenameArr[filenameArr.length - 1];
            Writer writer = Files.newBufferedWriter(Path.of(filenamePath));
            gson.toJson(this.hmiAppData, writer);
            writer.close();
            this.currentProjectFilePath = filenamePath;
            this.setWasModified(false);
            if(!mainStage.getTitle().contains(filename)){
                mainStage.setTitle(filename+" - "+mainStage.getTitle());
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
                page.getCanvas().getShapeArrayList().get(i).setEnable(value);
            }
        }
    }

    /**
     * Este método permite la creación de ventanas para las Escenas si son mayores que uno,
     * si es solo uno solamente cambia la Escena de la Ventana mostrada a la seleccionada
     * Además de crear ventanas(Stage) adicionales también las muestra
     *
     * @param selectedPages ArrayList de los títulos de las Páginas Seleccionadas
     */
    public void generateStagesForPages(ArrayList<String> selectedPages) {
        boolean mainStageWasUpdated = false;
        if (!selectedPages.isEmpty()) {
            for (String selectedPage : selectedPages) {
                int index = getIndexForStageWithScene(selectedPage);
                if (index == -1) {
                    if (!mainStageWasUpdated) {
                        changeSelectedScene(selectedPage);
                        mainStageWasUpdated = true;
                    } else {
                        generateStage(pages.get(index));
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
    }

    /**
     * Este método permite la creación de una nueva ventana a partir de una HMIScene previamente existente.
     *
     * @param scene La página creada del tipo HMIScene
     */
    private void generateStage(HMIScene scene) {
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle(scene.getTitle()+" - "+HMI_TITLE);
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
        for (int i = 0; i < createdWindows.size(); i++) {
            if (((HMIScene) createdWindows.get(i).getScene()).getTitle().equals(title)) {
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
        mainStage.setScene(pages.get(index));
        String compliment = wasModified ? "*" : "";
        mainStage.setTitle(pages.get(index).getTitle()+ " - " +HMI_TITLE +compliment);
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
        this.pagesTitles.add(newScene.getTitle());
        updateScenesInListView(pagesTitles);

        mainStage.setScene(newScene);
        mainStage.setTitle(newScene.getTitle()+" - "+HMI_TITLE);
    }

    /**
     * Busca el índice de la Escena basada en su título
     *
     * @param sceneTitle Título de la escena buscada
     * @return El indice de la escena en el arrayList
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
            showAlert(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", sqlException.getMessage(), false,false);
            sqlException.printStackTrace();
        } catch (IOException e) {
            if (!showAlert(Alert.AlertType.ERROR, "Error al conectarse a la base de datos", e.getMessage() + "; pulse OK para mostrar la ventana de ingreso de credenciales para conectarse a la base de datos", true,false)) {
                generateDatabase();
            }
            e.printStackTrace();
        }

    }

    public boolean showAlert(Alert.AlertType type, String title, String message, boolean showCredentialsWindow, boolean isSaveDialog) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(message);

        ButtonType saveButton = new ButtonType("Guardar",ButtonBar.ButtonData.YES);
        ButtonType dontSaveButton = new ButtonType("No Guardar",ButtonBar.ButtonData.NO);
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        if(isSaveDialog){
            alert.getButtonTypes().setAll(dontSaveButton,cancelButton, saveButton);
        }else {
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
        }else if(result.isPresent() && result.get() == cancelButton){
            alert.close();
            return false;
        } else if(result.isPresent() && result.get() == saveButton){
            alert.close();
            try {
                saveHMIDataProcess();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR,"Error al Guardar","Error:"+e.getMessage(),false,false);
                e.printStackTrace();
            }
            return true;
        } else if(result.isPresent() && result.get() == dontSaveButton){
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
}
