package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.models.canvas.CanvasColor;
import andrade.luis.hmiethernetip.models.canvas.CanvasImage;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import andrade.luis.hmiethernetip.models.canvas.CanvasObject;
import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SelectHMISymbolWindow extends Stage {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private final String resourcesDirectory = getClass().getResource("HMISymbols").toExternalForm().substring(5);

    public double getHue() {
        return hue;
    }

    public void setHue(double hue) {
        this.hue = hue;
    }

    private double hue;
    private Image selectedImage;
    private String selectedImagePath;
    private boolean mirroringVertical=false;
    private boolean mirroringHorizontal=false;
    private boolean modifyingColor=false;
    private boolean preservingRatio = false;
    private double rotation=0;
    private double contrast=0;
    private double brightness=0;
    private double saturation=0;
    private String symbolCategory="";
    private CanvasColor color;
    private final ArrayList<CanvasImage> currentImages = new ArrayList<>();
    private ArrayList<ScrollPane> categoriesPanes = new ArrayList<>();
    private ArrayList<HBox> categoriesHBoxes = new ArrayList<>();
    private final ArrayList<TitledPane> categoriesTitlePanes = new ArrayList<>();
    Map<String, ArrayList<String>> directoryFileNames = new HashMap<>();
    Map<String, String> categoriesDirectory = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("1. Tuberías", "PipesValves"),
            new AbstractMap.SimpleEntry<>("2. Motores y bombas", "MotorsPumps"),
            new AbstractMap.SimpleEntry<>("3. Hornos, calderas, etc", "BoilerFurnace"),
            new AbstractMap.SimpleEntry<>("4. Cintas transportadoras", "ConveyorBelts"),
            new AbstractMap.SimpleEntry<>("5. Tanques", "Tanks"),
            new AbstractMap.SimpleEntry<>("6. Otros", "Others")
    );
    Map<String, Boolean> categoriesDirectoriesFlags = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("pipesValves",false),
            new AbstractMap.SimpleEntry<>("motorsPumps",false),
            new AbstractMap.SimpleEntry<>("boilerFurnace",false),
            new AbstractMap.SimpleEntry<>("conveyorBelts",false),
            new AbstractMap.SimpleEntry<>("tanks",false),
            new AbstractMap.SimpleEntry<>("others",false)
    );
    ArrayList<String> categories = new ArrayList<>(categoriesDirectory.keySet());

    public SelectHMISymbolWindow() throws FileNotFoundException {
        Collections.sort(categories);
        StackPane superRoot = new StackPane();
        Group root = new Group();
        final Label label = new Label("Seleccione un símbolo HMI para añadir");
        label.setFont(new Font("Arial", 20));

        final Accordion accordion = new Accordion();

        for (String category : categories) {
            TitledPane titledPane = new TitledPane(category, null);
            titledPane.setPrefWidth(500);
            categoriesTitlePanes.add(titledPane);
        }
        accordion.getPanes().addAll(categoriesTitlePanes);
        accordion.expandedPaneProperty().addListener((observableValue, oldTitledPane, newTitledPane) -> {
            if(newTitledPane != null){
                logger.log(Level.INFO, newTitledPane.getText());
                try {
                    if(newTitledPane.getContent()==null){
                        ScrollPane scrollPane = generateSymbolsScrollPaneByCategory(newTitledPane.getText());
                        newTitledPane.setContent(scrollPane);
                        updateSelected(selectedImagePath);
                        categoriesPanes.add(scrollPane);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button optionsButton = new Button("Opciones de Imagen");
        optionsButton.setOnAction(mouseEvent -> {
            SetImageOptionsWindow setImageOptionsWindow = new SetImageOptionsWindow();
            if(modifyingColor){
                setImageOptionsWindow.setModifyingColor(true);
                setImageOptionsWindow.getBrightnessTextField().setText(String.valueOf(brightness));
                setImageOptionsWindow.getContrastTextField().setText(String.valueOf(contrast));
                setImageOptionsWindow.getHueTextField().setText(String.valueOf(hue));
                setImageOptionsWindow.getSaturationTextField().setText(String.valueOf(saturation));
                setImageOptionsWindow.getColorPicker().setValue(color.getColor());
            }
            setImageOptionsWindow.showAndWait();
            preservingRatio = setImageOptionsWindow.isPreservingRatio();
            mirroringVertical = setImageOptionsWindow.isMirroringVertical();
            mirroringHorizontal = setImageOptionsWindow.isMirroringHorizontal();
            rotation = Double.parseDouble(setImageOptionsWindow.getRotationValue());
            if(setImageOptionsWindow.isModifyingColor()){
                modifyingColor = setImageOptionsWindow.isModifyingColor();
                color = new CanvasColor(setImageOptionsWindow.getColorPicker().getValue());
                contrast = Double.parseDouble(setImageOptionsWindow.getContrastTextField().getText());
                brightness = Double.parseDouble(setImageOptionsWindow.getBrightnessTextField().getText());
                saturation = Double.parseDouble(setImageOptionsWindow.getSaturationTextField().getText());
                hue = Double.parseDouble(setImageOptionsWindow.getHueTextField().getText());
            }
            setImageOptionsWindow.setMirroringHorizontal(isMirroringHorizontal());
            setImageOptionsWindow.setMirroringVertical(isMirroringVertical());
            setImageOptionsWindow.getRotationTextField().setText(String.valueOf(rotation));

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

    private ScrollPane generateSymbolsScrollPaneByCategory(String category) throws IOException {
        File localCategoryDirectoryPath = new File(resourcesDirectory+File.separator+categoriesDirectory.get(category));
        File importedCategoryDirectoryPath = new File(DBConnection.getWorkingDirectory()+File.separator+categoriesDirectory.get(category));
        if(localCategoryDirectoryPath.listFiles() != null){
            HBox imagesHBox = new HBox(4);
            ArrayList<File> filesArrayList = new ArrayList<>(List.of(Objects.requireNonNull(localCategoryDirectoryPath.listFiles())));
            ArrayList<String> imagesFilenames = new ArrayList<>();
            for(File imageFile: filesArrayList){
                imagesFilenames.add(imageFile.getAbsolutePath());
                Image image = new Image(new FileInputStream(imageFile.getAbsolutePath()));
                CanvasImage canvasImage = new CanvasImage(image,new CanvasPoint(0,0),false,imageFile.getAbsolutePath(),true);
                canvasImage.clearContextMenu();
                currentImages.add(canvasImage);
                imagesHBox.getChildren().add(canvasImage);
            }
            if(Boolean.parseBoolean(DBConnection.readPropertiesFile().getProperty(categoriesDirectory.get(category),String.valueOf(false)))){
                ArrayList<File> importedFilesArrayList = new ArrayList<>(List.of(Objects.requireNonNull(importedCategoryDirectoryPath.listFiles())));
                for(File imageFile : importedFilesArrayList){
                    imagesFilenames.add(imageFile.getAbsolutePath());
                    Image image = new Image(new FileInputStream(imageFile.getAbsolutePath()));
                    CanvasImage canvasImage = new CanvasImage(image,new CanvasPoint(0,0),false,imageFile.getAbsolutePath(),true);
                    canvasImage.clearContextMenu();
                    currentImages.add(canvasImage);
                    imagesHBox.getChildren().add(canvasImage);
                }
            }
            Button importButton = new Button("Importar");
            importButton.setPrefWidth(100);
            importButton.setPrefHeight(100);
            imagesHBox.getChildren().add(importButton);
            importButton.setOnAction(mouseEvent -> {
                try {
                    importAction(categoriesDirectory.get(category));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            directoryFileNames.put(category,imagesFilenames);
            imagesHBox.setOnMouseClicked(mouseEvent -> updateSelected());
            ScrollPane scrollPane = new ScrollPane(imagesHBox);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            scrollPane.setMaxWidth(this.widthProperty().get());
            return scrollPane;
        }
        return null;
    }

    public void importAction(String category) throws IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccione una imagen");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagen", "*.bmp","*.gif","*.jpg","*.jpeg","*.png","*.BMP","*.GIF","*.JPG","*.JPEG","*.PNG"),
                new FileChooser.ExtensionFilter("BMP","*.bmp","*.BMP"),
                new FileChooser.ExtensionFilter("GIF", "*.gif","*.GIF"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg","*.JPG"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg","*.JPEG"),
                new FileChooser.ExtensionFilter("PNG", "*.png","*.PNG")
        );
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            String categoryDirectory = DBConnection.getWorkingDirectory()+File.separator+category;
            File directory = new File(categoryDirectory);
            if (!directory.exists()) {
                directory.mkdir();
            }
            FileInputStream is = new FileInputStream(file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(categoryDirectory+File.separator+file.getName());
            int c;

            while ((c = is.read()) != -1) {
                out.write(c);
            }
            is.close();
            out.close();
        }

    }

    private void updateCategoryDirectoryInProperties(String category) throws IOException {
        Properties properties = DBConnection.readPropertiesFile();
        String hostname="", port="",username="",password="";
        if(properties!= null){
            hostname = properties.getProperty("hostname","");
            port = properties.getProperty("port");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            categoriesDirectoriesFlags.put("boilerFurnace",Boolean.parseBoolean(properties.getProperty("boilerFurnace")));
            categoriesDirectoriesFlags.put("conveyorBelts",Boolean.parseBoolean(properties.getProperty("conveyorBelts")));
            categoriesDirectoriesFlags.put("motorPumps",Boolean.parseBoolean(properties.getProperty("motorPumps")));
            categoriesDirectoriesFlags.put("others",Boolean.parseBoolean(properties.getProperty("others")));
            categoriesDirectoriesFlags.put("pipesValues",Boolean.parseBoolean(properties.getProperty("pipesValues")));
            categoriesDirectoriesFlags.put("tanks",Boolean.parseBoolean(properties.getProperty("tanks")));
        }
        categoriesDirectoriesFlags.put(category,true);
        DBConnection.writePropertiesFile(username,password,hostname,port,categoriesDirectoriesFlags.get("boilerFurnace"),categoriesDirectoriesFlags.get("conveyorBelts"),categoriesDirectoriesFlags.get("motorPumps"),categoriesDirectoriesFlags.get("others"),categoriesDirectoriesFlags.get("pipesValues"),categoriesDirectoriesFlags.get("tanks"));
    }

    public void updateScrollPanesWidth(double width){
        for(ScrollPane scrollPane : categoriesPanes){
            scrollPane.setMaxWidth(width);
        }
        for(TitledPane categoriesTitlePane : categoriesTitlePanes){
            categoriesTitlePane.setPrefWidth(width);
        }
    }

    public void updateSelected(String imagePath) throws FileNotFoundException {
        for(CanvasImage canvasImage: currentImages){
            if(canvasImage.getCanvasObjectData().getData().equals(imagePath)){
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
                CanvasObject canvasImage = currentImages.get(i);
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
        this.selectedImagePath = currentImages.get(index).getCanvasObjectData().getData();
        for (TitledPane categoriesTitlePane : categoriesTitlePanes) {
            if (categoriesTitlePane.isExpanded()) {
                this.symbolCategory = categoriesTitlePane.getText();
            }
        }
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

    public boolean isModifyingColor() {
        return modifyingColor;
    }

    public void setModifyingColor(boolean modifyingColor) {
        this.modifyingColor = modifyingColor;
    }

    public double getContrast() {
        return contrast;
    }

    public void setContrast(double contrast) {
        this.contrast = contrast;
    }

    public double getBrightness() {
        return brightness;
    }

    public void setBrightness(double brightness) {
        this.brightness = brightness;
    }

    public double getSaturation() {
        return saturation;
    }

    public void setSaturation(double saturation) {
        this.saturation = saturation;
    }

    public CanvasColor getColor() {
        return color;
    }

    public void setColor(CanvasColor color) {
        this.color = color;
    }

    public boolean isPreservingRatio() {
        return preservingRatio;
    }

    public void setPreservingRatio(boolean preservingRatio) {
        this.preservingRatio = preservingRatio;
    }

    public String getSymbolCategory() {
        return symbolCategory;
    }

    public void setSymbolCategory(String symbolCategory) {
        this.symbolCategory = symbolCategory;
    }
    public void setSelectedImagePath(String selectedImagePath) {
        this.selectedImagePath = selectedImagePath;
    }
}
