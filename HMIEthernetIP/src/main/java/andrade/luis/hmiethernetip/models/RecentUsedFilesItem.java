package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.HMIApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;


public class RecentUsedFilesItem {
    @FXML
    private HBox hBox;
    @FXML
    private Label filenameLabel;
    @FXML
    private Label filepathLabel;
    @FXML
    private Button playButton;
    @FXML
    private Button editButton;
    private final HMIApp hmiApp;

    public RecentUsedFilesItem(HMIApp hmiApp){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("welcomeStageLVItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.hmiApp = hmiApp;
    }

    public void setInfo(String filepath){
        filepathLabel.setText(filepath);
        String[] filenameArr = filepath.split(File.separator);
        String filename = filenameArr[filenameArr.length - 1];
        filenameLabel.setText(filename);
        String editPath = getClass().getResource("edit.png").toExternalForm();
        String playPath = getClass().getResource("play-button.png").toExternalForm();
        /**
         * Edit button source <a href="https://www.flaticon.com/free-icons/edit" title="edit icons">Edit icons created by Pixel perfect - Flaticon</a>
         */
        Image editImage = new Image(editPath);
        /**
         * Play button source <a href="https://www.flaticon.com/free-icons/play-button" title="play button icons">Play button icons created by Maxim Basinski Premium - Flaticon</a>
         */
        Image playImage = new Image(playPath);
        ImageView editImageView = new ImageView(editImage);
        ImageView playImageView = new ImageView(playImage);

        editButton.setGraphic(editImageView);
        editButton.setText("");
        editButton.setOnAction(mouseEvent -> {
            try {
                hmiApp.setHMIStage(filepath, "Editar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        playButton.setGraphic(playImageView);
        playButton.setText("");
        playButton.setOnAction(mouseEvent -> {
            try {
                hmiApp.setHMIStage(filepath, "Ejecutar");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public HBox getBox(){
        return hBox;
    }
}
