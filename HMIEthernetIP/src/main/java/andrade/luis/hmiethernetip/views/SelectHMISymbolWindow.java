package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.canvas.CanvasImage;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.GraphicalRepresentation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.*;


public class SelectHMISymbolWindow extends Stage {
    private final String resourcesDirectory = getClass().getResource("HMISymbols").toExternalForm().substring(5);
    private Image selectedImage;
    private String selectedImagePath;
    private boolean mirroringVertical;
    private boolean mirroringHorizontal;
    private double rotation;
    private ArrayList<CanvasImage> currentImages = new ArrayList<>();
    private ArrayList<ScrollPane> categoriesPanes = new ArrayList<>();
    Map<String, String> categoriesDirectory = Map.ofEntries(
            new AbstractMap.SimpleEntry<String, String>("1. Tuberias", "PipesValves"),
            new AbstractMap.SimpleEntry<String, String>("2. Motores y bombas", "MotorsPumps"),
            new AbstractMap.SimpleEntry<String, String>("3. Hornos, calderas, etc", "BoilerFurnace"),
            new AbstractMap.SimpleEntry<String, String>("4. Cintas transportadoras", "ConveyorBelts"),
            new AbstractMap.SimpleEntry<String, String>("5. Tanques", "Tanks"),
            new AbstractMap.SimpleEntry<String, String>("6. Otros", "Others")
    );
    ArrayList<String> categories = new ArrayList<>(categoriesDirectory.keySet());

    public SelectHMISymbolWindow() throws FileNotFoundException {
        Collections.sort(categories);
        StackPane superRoot = new StackPane();
        Group root = new Group();
        final Label label = new Label("Seleccione un símbolo HMi para añadir");
        label.setFont(new Font("Arial", 20));


        final Accordion accordion = new Accordion();

        ArrayList<TitledPane> tps = new ArrayList<>();
        for (String category : categories) {
            File categoryDirectoryPath = new File(resourcesDirectory+File.separator+categoriesDirectory.get(category));
            if(categoryDirectoryPath.listFiles() != null){
                HBox imagesHBox = new HBox(4);
                ArrayList<File> filesArrayList = new ArrayList<>(List.of(Objects.requireNonNull(categoryDirectoryPath.listFiles())));
                for(File imageFile: filesArrayList){
                    Image image = new Image(new FileInputStream(imageFile.getAbsolutePath()));
                    CanvasImage canvasImage = new CanvasImage(image,new CanvasPoint(0,0),false,imageFile.getAbsolutePath(),false,false,0,true);
                    canvasImage.clearContextMenu();
                    currentImages.add(canvasImage);
                    imagesHBox.getChildren().add(canvasImage);
                }
                imagesHBox.setOnMouseClicked(mouseEvent -> updateSelected());
                ScrollPane scrollPane = new ScrollPane(imagesHBox);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scrollPane.setMaxWidth(this.widthProperty().get());
                categoriesPanes.add(scrollPane);
                tps.add(new TitledPane(category, scrollPane));
            }
        }
        accordion.getPanes().addAll(tps);
        Button optionsButton = new Button("Opciones de Imagen");
        optionsButton.setOnAction(mouseEvent -> {
            SetImageOptionsWindow setImageOptionsWindow = new SetImageOptionsWindow();
            setImageOptionsWindow.showAndWait();
            mirroringVertical = setImageOptionsWindow.isMirroringVertical();
            mirroringHorizontal = setImageOptionsWindow.isMirroringHorizontal();
            rotation = Double.parseDouble(setImageOptionsWindow.getRotationValue());
        });
        Button okButton = new Button("OK");
        okButton.setOnAction(mouseEvent -> this.close());
        HBox okHBox = new HBox();
        okHBox.getChildren().addAll(optionsButton,okButton);
        okHBox.setAlignment(Pos.BOTTOM_RIGHT);
        VBox vbox = new VBox();
        root.getChildren().add(accordion);
        vbox.getChildren().addAll(label,root,okHBox);
        superRoot.getChildren().add(vbox);
        Scene scene = new Scene(superRoot,500,350);
        this.setScene(scene);
        this.widthProperty().addListener((obs, oldVal, newVal) -> updateScrollPanesWidth(newVal.doubleValue()));

    }

    public void updateScrollPanesWidth(double width){
        for(ScrollPane scrollPane : categoriesPanes){
            scrollPane.setMaxWidth(width);
        }
    }

    public void updateSelected(String imagePath){
        for(CanvasImage canvasImage: currentImages){
            if(canvasImage.getGraphicalRepresentationData().getData().equals(imagePath)){
                canvasImage.setSelected(true);
                updateSelected();
            }
        }
    }

    public void updateSelected(){
        LocalDateTime max = null;
        int index = -1;
        for (int i = 0; i < currentImages.size(); i++) {
            if (i == 0) {
                max = currentImages.get(i).getLastTimeSelected();
                index = i;
            } else {
                GraphicalRepresentation canvasImage = currentImages.get(i);
                if (canvasImage.getLastTimeSelected() != null && max != null) {
                    if (max.isBefore(canvasImage.getLastTimeSelected())) {
                        max = currentImages.get(i).getLastTimeSelected();
                        index = i;
                    }
                }
            }
        }
        if (index > -1 && max != null) {
            for (int i = 0; i < currentImages.size(); i++) {
                if (i != index) {
                    currentImages.get(i).setSelected(false);
                }
            }
        }
        this.selectedImage = currentImages.get(index).getImage();
        this.selectedImagePath = currentImages.get(index).getGraphicalRepresentationData().getData();
    }

    public Image getSelectedImage() {
        return selectedImage;
    }

    public String getSelectedImagePath() {
        return selectedImagePath;
    }
    public boolean isMirroringVertical() {
        return mirroringVertical;
    }

    public void setMirroringVertical(boolean mirroringVertical) {
        this.mirroringVertical = mirroringVertical;
    }

    public boolean isMirroringHorizontal() {
        return mirroringHorizontal;
    }

    public void setMirroringHorizontal(boolean mirroringHorizontal) {
        this.mirroringHorizontal = mirroringHorizontal;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
}
