package andrade.luis.librehmi.views;

import andrade.luis.librehmi.HMIApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Item para la lista de archivos de la ventana de bienvenida
 */
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

    /**
     * Constructor del item de la lista
     * @param hmiApp Objeto de aplicación
     */
    public RecentUsedFilesItem(HMIApp hmiApp){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("welcomeStageLVItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.log(Level.INFO,e.getMessage());
        }
        this.hmiApp = hmiApp;
    }

    /**
     * Permite definir la información del item
     * @param filepath Path del archivo reciente
     */
    public void setInfo(String filepath){
        filepathLabel.setText(filepath);
        String[] filenameArr = filepath.split(File.separator);
        String filename = filenameArr[filenameArr.length - 1];
        filenameLabel.setText(filename);
        Logger logger = Logger.getLogger(this.getClass().getName());
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
                logger.log(Level.INFO,e.getMessage());
            }
        });
        playButton.setGraphic(playImageView);
        playButton.setText("");
        playButton.setOnAction(mouseEvent -> {
            try {
                hmiApp.setHMIStage(filepath, "Ejecutar");
            } catch (IOException e) {
                logger.log(Level.INFO,e.getMessage());
            }
        });
    }

    public HBox getBox(){
        return hBox;
    }
}
