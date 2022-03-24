package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.RecentUsedFilesCell;
import andrade.luis.hmiethernetip.models.RecentUsedFilesData;
import andrade.luis.hmiethernetip.util.DBConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WelcomeWindow extends Stage {
    HMIApp hmiApp;
    @FXML
    private ListView<String> recentUsedFilesListView;
    @FXML
    private Button newProjectButton;
    @FXML
    private Button loadProjectButton;
    @FXML
    private ImageView backgroundImageView;
    @FXML
    private VBox vBox;
    @FXML
    private VBox titleVBox;
    @FXML
    private VBox beginVBox;
    @FXML
    private VBox recentVBox;
    private Scene scene;
    private ArrayList<String> stringList = new ArrayList<>();
    ObservableList<String> observableList = FXCollections.observableArrayList();
    /**
     * HMI background source <a href="https://www.flaticon.com/free-icons/machines" title="machines icons">Machines icons created by Flat Icons - Flaticon</a>
     * */
    private Image backgroundImage;
    Logger logger = Logger.getLogger(this.getClass().getName());


    public WelcomeWindow(HMIApp hmiApp) throws IOException {
        this();
        this.hmiApp = hmiApp;
    }

    public WelcomeWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("welcomeStage.fxml"));
        fxmlLoader.setController(this);
        StackPane root = fxmlLoader.load();

        this.scene = new Scene(root);
        this.setScene(this.scene);
        this.setMaximized(true);
    }
    public void setListView()
    {

        String recentFilesFilePath = DBConnection.getWorkingDirectory()+ File.separator+"recentFiles.json";
        RecentUsedFilesData recentUsedFilesData = null;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(recentFilesFilePath))) {
            recentUsedFilesData = gson.fromJson(bufferedReader, RecentUsedFilesData.class);
            logger.log(Level.INFO,recentUsedFilesData.getRecentlyUsedFilePaths().get(0));
            stringList.addAll(recentUsedFilesData.getRecentlyUsedFilePaths());
        } catch (IOException e) {
            e.printStackTrace();
        }

        observableList.setAll(stringList);
        recentUsedFilesListView.setCellFactory(stringListView -> new RecentUsedFilesCell(WelcomeWindow.this.hmiApp));
        recentUsedFilesListView.setItems(observableList);
    }
    @FXML
    void initialize() throws FileNotFoundException {
        assert recentUsedFilesListView != null : "fx:id=\"recentUsedFilesListView\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert newProjectButton != null : "fx:id=\"newProjectButton\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert loadProjectButton != null : "fx:id=\"loadProjectButton\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert backgroundImageView != null : "fx:id=\"backgroundImageView\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert vBox != null : "fx:id=\"vBox\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert titleVBox != null : "fx:id=\"titleVBox\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert beginVBox != null : "fx:id=\"beginVBox\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert recentVBox != null : "fx:id=\"recentVBox\" was not injected: check your FXML file 'CustomList.fxml'.";

        vBox.setPrefWidth(500);

        setListView();
        newProjectButton.setOnAction(mouseEvent -> {
            if(this.hmiApp!=null){
                try {
                    this.hmiApp.setHMIStage(null,"Default");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        loadProjectButton.setOnAction(mouseEvent -> {
            if(this.hmiApp!=null){
                try {
                    this.hmiApp.setHMIStage("","Default");
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        String imagePath = getClass().getResource("machines.png").toExternalForm();
        backgroundImage = new Image(imagePath);
        backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setImage(backgroundImage);
        backgroundImageView.setFitWidth(150);
        backgroundImageView.setFitHeight(150);
        this.titleVBox.getChildren().set(0,backgroundImageView);
        this.vBox.setPadding(new Insets(50,0,0,0));
        this.vBox.setSpacing(15);
        this.beginVBox.setSpacing(5);
    }
}

