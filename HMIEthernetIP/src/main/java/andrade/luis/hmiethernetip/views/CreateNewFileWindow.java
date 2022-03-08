package andrade.luis.hmiethernetip.views;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;

public class CreateNewFileWindow extends Stage {
    private final StackPane root;

    public TextField getFilenameTextField() {
        return filenameTextField;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    private final TextField filenameTextField;
    private String filename;

    public CreateNewFileWindow(){
        root = new StackPane();
        Label label = new Label("Guardar Proyecto");
        Label filenameLabel = new Label("Nombre del archivo:");
        filenameTextField = new TextField("");
        Button fileDirectoryButton = new Button("Destino");
        Button saveButton = new Button("Guardar");
        fileDirectoryButton.setOnAction(mouseEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null) filename = selectedDirectory.getAbsolutePath();
        });
        Button cancelButton = new Button("Cancelar");
        cancelButton.setOnAction(mouseEvent -> this.close());
        HBox filenameHBox = new HBox();
        filenameHBox.getChildren().addAll(filenameLabel,filenameTextField,fileDirectoryButton);
        HBox buttonsHBox = new HBox();
        buttonsHBox.getChildren().addAll(cancelButton,saveButton);
        VBox vbox = new VBox();
        vbox.getChildren().addAll(label,filenameHBox,buttonsHBox);
        root.getChildren().add(vbox);

        this.setScene(new Scene(root,400,300));
    }
}
