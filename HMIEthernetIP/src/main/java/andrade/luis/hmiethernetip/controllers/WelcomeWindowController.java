package andrade.luis.hmiethernetip.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

import java.util.Set;

public class WelcomeWindowController {
    @FXML
    private StackPane recentUsedFilesListView;
    private Set<String> stringSet;
    ObservableList observableList = FXCollections.observableArrayList();

    public WelcomeWindowController() {
        FXMLLoader fxmlLoader = new FXMLLoader();
    }
}
