package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.RecentUsedFilesData;
import andrade.luis.hmiethernetip.util.DBConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;

public class WelcomeWindow extends Stage {
    HMIApp hmiApp;
    @FXML
    private ListView<String> recentUsedFilesListView;
    @FXML
    private Button newProjectButton;
    @FXML
    private Button loadProjectButton;
    private ArrayList<String> stringList = new ArrayList<>();
    ObservableList<String> observableList = FXCollections.observableArrayList();

    public WelcomeWindow(HMIApp hmiApp) throws IOException {
        this();
        this.hmiApp = hmiApp;
    }

    public WelcomeWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("welcomeStage.fxml"));
        fxmlLoader.setController(this);
        StackPane root = fxmlLoader.load();

        this.setScene(new Scene(root));
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
            stringList.addAll(recentUsedFilesData.getRecentlyUsedFilePaths());
        } catch (IOException e) {
            e.printStackTrace();
        }

        observableList.setAll(stringList);
        recentUsedFilesListView.setItems(observableList);
        recentUsedFilesListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                if(!empty){
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            try {
                                WelcomeWindow.this.hmiApp.setHMIStage(item);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }
    @FXML
    void initialize() {
        assert recentUsedFilesListView != null : "fx:id=\"recentUsedFilesListView\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert newProjectButton != null : "fx:id=\"newProjectButton\" was not injected: check your FXML file 'CustomList.fxml'.";
        assert loadProjectButton != null : "fx:id=\"loadProjectButton\" was not injected: check your FXML file 'CustomList.fxml'.";

        setListView();
        newProjectButton.setOnAction(mouseEvent -> {
            if(this.hmiApp!=null){
                try {
                    this.hmiApp.setHMIStage(null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        loadProjectButton.setOnAction(mouseEvent -> {
            if(this.hmiApp!=null){
                try {
                    this.hmiApp.setHMIStage("");
                }  catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

