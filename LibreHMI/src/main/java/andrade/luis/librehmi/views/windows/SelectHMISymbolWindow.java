package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.HMISymbolsTree;
import andrade.luis.librehmi.views.canvas.CanvasColor;
import andrade.luis.librehmi.views.canvas.CanvasImage;
import andrade.luis.librehmi.views.canvas.CanvasPoint;
import andrade.luis.librehmi.views.canvas.CanvasObject;
import andrade.luis.librehmi.util.DBConnection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ventana de selección de símbolos HMI
 */
public class SelectHMISymbolWindow extends Stage {

    private LocalDateTime max;

    public double getImageViewWidth() {
        return imageViewWidth;
    }

    public void setImageViewWidth(double imageViewWidth) {
        this.imageViewWidth = imageViewWidth;
    }

    public double getImageViewHeight() {
        return imageViewHeight;
    }

    public void setImageViewHeight(double imageViewHeight) {
        this.imageViewHeight = imageViewHeight;
    }

    private double imageViewWidth;
    private double imageViewHeight;
    Logger logger = Logger.getLogger(this.getClass().getName());

    private Image selectedImage;
    private String selectedImagePath;
    private boolean mirroringVertical = false;
    private boolean mirroringHorizontal = false;
    private boolean modifyingColor = false;
    private boolean preservingRatio = false;
    private double rotation = 0;

    private String symbolCategory = "";
    private CanvasColor color;
    private final ArrayList<CanvasImage> currentImages = new ArrayList<>();
    private final ArrayList<ScrollPane> categoriesPanes = new ArrayList<>();

    private final ArrayList<TitledPane> categoriesTitlePanes = new ArrayList<>();
    private static final String PIPES_VALVES = "PipesValves";
    private static final String MOTOR_PUMPS = "MotorsPumps";
    private static final String BOILER_FURNACE = "BoilerFurnace";
    private static final String CONVEYOR_BELTS = "ConveyorBelts";
    private static final String TANKS = "Tanks";
    private static final String OTHERS = "Others";
    Map<String, ArrayList<String>> directoryFileNames = new HashMap<>();
    Map<String, String> categoriesDirectory = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("1. Tuberías", PIPES_VALVES),
            new AbstractMap.SimpleEntry<>("2. Motores y bombas", MOTOR_PUMPS),
            new AbstractMap.SimpleEntry<>("3. Hornos, calderas, etc", BOILER_FURNACE),
            new AbstractMap.SimpleEntry<>("4. Cintas transportadoras", CONVEYOR_BELTS),
            new AbstractMap.SimpleEntry<>("5. Tanques", TANKS),
            new AbstractMap.SimpleEntry<>("6. Otros", OTHERS)
    );
    HashMap<String, Boolean> categoriesDirectoriesFlags = new HashMap<>();
    ArrayList<String> categories = new ArrayList<>(categoriesDirectory.keySet());

    public CanvasObjectData getCanvasObjectData() {
        return canvasObjectData;
    }

    public void setCanvasObjectData(CanvasObjectData canvasObjectData) {
        this.canvasObjectData = canvasObjectData;
    }

    private CanvasObjectData canvasObjectData = new CanvasObjectData();

    /**
     * Constructor de la ventana
     * @param imageViewWidth Ancho de la imagen
     * @param imageViewHeight Alto de la imagen
     */
    public SelectHMISymbolWindow(double imageViewWidth, double imageViewHeight) {
        this.imageViewWidth = imageViewWidth;
        this.imageViewHeight = imageViewHeight;
        categoriesDirectoriesFlags.put(PIPES_VALVES, false);
        categoriesDirectoriesFlags.put(MOTOR_PUMPS, false);
        categoriesDirectoriesFlags.put(BOILER_FURNACE, false);
        categoriesDirectoriesFlags.put(CONVEYOR_BELTS, false);
        categoriesDirectoriesFlags.put(TANKS, false);
        categoriesDirectoriesFlags.put(OTHERS, false);

        Collections.sort(categories);
        StackPane superRoot = new StackPane();
        Group root = new Group();
        setTitle("Seleccione un símbolo HMI para añadir");

        final Accordion accordion = new Accordion();

        for (String category : categories) {
            TitledPane titledPane = new TitledPane(category, null);
            titledPane.setPrefWidth(500);
            categoriesTitlePanes.add(titledPane);
        }
        accordion.getPanes().addAll(categoriesTitlePanes);
        accordion.expandedPaneProperty().addListener((observableValue, oldTitledPane, newTitledPane) -> {
            if (newTitledPane != null) {
                log(newTitledPane.getText());
                try {
                    if (newTitledPane.getContent() == null) {
                        ScrollPane scrollPane = generateSymbolsScrollPaneByCategory(newTitledPane.getText());
                        newTitledPane.setContent(scrollPane);
                        updateSelected(selectedImagePath);
                        categoriesPanes.add(scrollPane);
                    }
                } catch (IOException e) {
                    log(e.getMessage());
                }
            }
        });
        Button optionsButton = new Button("Opciones de Imagen");
        optionsButton.setOnAction(mouseEvent -> {
            double width = SelectHMISymbolWindow.this.imageViewWidth;
            double height = SelectHMISymbolWindow.this.imageViewHeight;
            SetImageOptionsWindow setImageOptionsWindow = new SetImageOptionsWindow(width, height);
            setImageOptionsWindow.getImagePathButton().setDisable(true);
            if (modifyingColor) {
                setImageOptionsWindow.setModifyingColor(true);
                setImageOptionsWindow.getModColorRB().setSelected(true);
                setImageOptionsWindow.getBrightnessTextField().setText(String.valueOf(this.getCanvasObjectData().getBrightness()));
                setImageOptionsWindow.getContrastTextField().setText(String.valueOf(this.getCanvasObjectData().getContrast()));
                setImageOptionsWindow.getHueTextField().setText(String.valueOf(this.getCanvasObjectData().getHue()));
                setImageOptionsWindow.getSaturationTextField().setText(String.valueOf(this.getCanvasObjectData().getSaturation()));
                setImageOptionsWindow.getColorPicker().setValue(color.getColor());
            } else {
                setImageOptionsWindow.getOriginalColorRB().setSelected(true);
            }
            setImageOptionsWindow.getPreserveRatioCheckBox().setSelected(preservingRatio);
            setImageOptionsWindow.getMirrorHorizontalCheckBox().setSelected(mirroringHorizontal);
            setImageOptionsWindow.getMirrorVerticalCheckBox().setSelected(mirroringVertical);
            setImageOptionsWindow.getRotationTextField().setText(String.valueOf(rotation));
            setImageOptionsWindow.showAndWait();
            preservingRatio = setImageOptionsWindow.isPreservingRatio();
            mirroringVertical = setImageOptionsWindow.isMirroringVertical();
            mirroringHorizontal = setImageOptionsWindow.isMirroringHorizontal();
            rotation = Double.parseDouble(setImageOptionsWindow.getRotationTextField().getText());
            SelectHMISymbolWindow.this.imageViewWidth = setImageOptionsWindow.getSizeVBox().getWidthFromField();
            SelectHMISymbolWindow.this.imageViewHeight = setImageOptionsWindow.getSizeVBox().getHeightFromField();
            if (setImageOptionsWindow.isModifyingColor()) {
                modifyingColor = setImageOptionsWindow.isModifyingColor();
                color = new CanvasColor(setImageOptionsWindow.getColorPicker().getValue());
                this.getCanvasObjectData().setContrast(Double.parseDouble(setImageOptionsWindow.getContrastTextField().getText()));
                this.getCanvasObjectData().setBrightness(Double.parseDouble(setImageOptionsWindow.getBrightnessTextField().getText()));
                this.getCanvasObjectData().setSaturation(Double.parseDouble(setImageOptionsWindow.getSaturationTextField().getText()));
                this.getCanvasObjectData().setHue(Double.parseDouble(setImageOptionsWindow.getHueTextField().getText()));
            }
        });
        Button okButton = new Button("OK");
        okButton.setOnAction(mouseEvent -> this.close());
        HBox okHBox = new HBox();
        okHBox.getChildren().addAll(optionsButton, okButton);
        okHBox.setAlignment(Pos.BOTTOM_RIGHT);
        VBox vbox = new VBox();
        root.getChildren().add(accordion);
        vbox.getChildren().addAll(root, okHBox);
        superRoot.getChildren().add(vbox);
        Scene scene = new Scene(superRoot, 500, 350);
        this.setScene(scene);
        this.widthProperty().addListener((obs, oldVal, newVal) -> updateScrollPanesWidth(newVal.doubleValue()));

    }

    private void log(String e) {
        logger.log(Level.INFO, e);
    }

    /**
     * Permite obtener los paths de las figuras predeterminadas
     * @param category Categoría de la cual se requieren las figuras predeterminadas
     * @return ArrayList de Strings de paths de las figuras predeterminadas
     */
    private ArrayList<String> getCategoryFilesPaths(String category){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("HMISymbolsTree.json")));
        HMISymbolsTree tree = gson.fromJson(bufferedReader, HMISymbolsTree.class);
        return tree.getCategoryFilesList(category);

    }

    /**
     * Permite obtener los objetos InputStream de las imágenes del directorio especificado
     * @param categoryFilesPaths Path del directorio
     * @return ArrayList de InputStream de las figuras predeterminadas dentro del directorio
     */
    private ArrayList<InputStream> getCategoryFilesArrayList(ArrayList<String> categoryFilesPaths) {
        ArrayList<InputStream> categoryFilesArrayList = new ArrayList<>();
        for (String path : categoryFilesPaths) {
            categoryFilesArrayList.add(getClass().getResourceAsStream(path));
        }
        return categoryFilesArrayList;
    }

    /**
     * Permite generar un ScrollPane que contiene las distintas imágenes predeterminadas
     * @param category Categoría a ser especificada
     * @return ScrollPane conteniendo las imágenes predeterminadas de la categoría
     * @throws IOException
     */
    private ScrollPane generateSymbolsScrollPaneByCategory(String category) throws IOException {
        File importedCategoryDirectoryPath = new File(DBConnection.getWorkingDirectory() + File.separator + categoriesDirectory.get(category));
        HBox imagesHBox = new HBox(4);
        ArrayList<String> filePaths = getCategoryFilesPaths(categoriesDirectory.get(category));
        ArrayList<InputStream> filesArrayList = getCategoryFilesArrayList(filePaths);
        ArrayList<String> imagesFilenames = new ArrayList<>();
        for (int i = 0; i < filesArrayList.size(); i++) {
            InputStream imageFile = filesArrayList.get(i);
            imagesFilenames.add(filePaths.get(i));
            Image image = new Image(imageFile);
            CanvasImage canvasImage = new CanvasImage(image, new CanvasPoint(0, 0), false, filePaths.get(i), true);
            canvasImage.getCanvasObjectData().setPreservingRatio(true);
            canvasImage.getImageView().setPreserveRatio(true);
            canvasImage.clearContextMenu();
            currentImages.add(canvasImage);
            imagesHBox.getChildren().add(canvasImage);
        }
        log(categoriesDirectory.get(category));
        log(DBConnection.readPropertiesFile().getProperty(categoriesDirectory.get(category)));
        if (Boolean.parseBoolean(DBConnection.readPropertiesFile().getProperty(categoriesDirectory.get(category), String.valueOf(false)))) {
            ArrayList<File> importedFilesArrayList = new ArrayList<>(List.of(Objects.requireNonNull(importedCategoryDirectoryPath.listFiles())));
            for (File imageFile : importedFilesArrayList) {
                imagesFilenames.add(imageFile.getAbsolutePath());
                Image image = new Image(new FileInputStream(imageFile.getAbsolutePath()));
                CanvasImage canvasImage = new CanvasImage(image, new CanvasPoint(0, 0), false, imageFile.getAbsolutePath(), true);
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
                log(e.getMessage());
            }
        });
        directoryFileNames.put(category, imagesFilenames);
        imagesHBox.setOnMouseClicked(mouseEvent -> updateSelected());
        ScrollPane scrollPane = new ScrollPane(imagesHBox);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setMaxWidth(this.widthProperty().get());
        return scrollPane;
    }

    /**
     * Permite importar una imagen a un directorio específico de trabajo que la aplicación consultará para mostrar
     * dentro de las imágenes predeterminadas
     * @param category Categoría de la imagen importada
     * @throws IOException
     */
    public void importAction(String category) throws IOException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Seleccione una imagen");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagen", "*.bmp", "*.gif", "*.jpg", "*.jpeg", "*.png", "*.BMP", "*.GIF", "*.JPG", "*.JPEG", "*.PNG"),
                new FileChooser.ExtensionFilter("BMP", "*.bmp", "*.BMP"),
                new FileChooser.ExtensionFilter("GIF", "*.gif", "*.GIF"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.JPG"),
                new FileChooser.ExtensionFilter("JPEG", "*.jpeg", "*.JPEG"),
                new FileChooser.ExtensionFilter("PNG", "*.png", "*.PNG")
        );
        File file = fileChooser.showOpenDialog(this);
        if (file != null) {
            String categoryDirectory = DBConnection.getWorkingDirectory() + File.separator + category;
            File directory = new File(categoryDirectory);
            if (!directory.exists()) {
                directory.mkdir();
            }
            String outputPath = categoryDirectory + File.separator + file.getName();
            try (FileInputStream is = new FileInputStream(file.getAbsolutePath())) {
                copyFile(is, outputPath);
            } catch (IOException e) {
                throw new IOException(e);
            }
            updateCategoryDirectoryInProperties(category);
            setResultValues(new Image(new FileInputStream(outputPath)), outputPath);
            this.close();
        }

    }

    /**
     * Permite copiar un archivo desde el FileInputStream hacia un directorio especificado
     * @param is Archivo que se copiará
     * @param outputPath Directorio de destino
     * @throws IOException
     */
    private void copyFile(FileInputStream is, String outputPath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            int c;

            while ((c = is.read()) != -1) {
                out.write(c);
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Permite actualizar las banderas del archivo de propiedades
     * @param category Categoría a actualizar
     * @throws IOException
     */
    private void updateCategoryDirectoryInProperties(String category) throws IOException {
        Properties properties = DBConnection.readPropertiesFile();
        String hostname = "";
        String port = "";
        String username = "";
        String password = "";
        if (properties != null) {
            hostname = properties.getProperty("hostname", "");
            port = properties.getProperty("port", "");
            username = properties.getProperty("username", "");
            password = properties.getProperty("password", "");
            categoriesDirectoriesFlags.put(BOILER_FURNACE, Boolean.parseBoolean(properties.getProperty(BOILER_FURNACE)));
            categoriesDirectoriesFlags.put(CONVEYOR_BELTS, Boolean.parseBoolean(properties.getProperty(CONVEYOR_BELTS)));
            categoriesDirectoriesFlags.put(MOTOR_PUMPS, Boolean.parseBoolean(properties.getProperty(MOTOR_PUMPS)));
            categoriesDirectoriesFlags.put(OTHERS, Boolean.parseBoolean(properties.getProperty(OTHERS)));
            categoriesDirectoriesFlags.put(PIPES_VALVES, Boolean.parseBoolean(properties.getProperty(PIPES_VALVES)));
            categoriesDirectoriesFlags.put(TANKS, Boolean.parseBoolean(properties.getProperty(TANKS)));
        }
        categoriesDirectoriesFlags.put(category, true);
        Properties newProperties = new Properties();
        newProperties = DBConnection.prepareCategoriesProperties(categoriesDirectoriesFlags.get(BOILER_FURNACE), categoriesDirectoriesFlags.get(CONVEYOR_BELTS), categoriesDirectoriesFlags.get(MOTOR_PUMPS), categoriesDirectoriesFlags.get(OTHERS), categoriesDirectoriesFlags.get(PIPES_VALVES), categoriesDirectoriesFlags.get(TANKS),newProperties);
        DBConnection.writePropertiesFile(username, password, hostname, port, newProperties);
    }

    /**
     * Permite actualizar el ancho de scrollPane
     * @param width Ancho para actualizar
     */
    public void updateScrollPanesWidth(double width) {
        for (ScrollPane scrollPane : categoriesPanes) {
            scrollPane.setMaxWidth(width);
        }
        for (TitledPane categoriesTitlePane : categoriesTitlePanes) {
            categoriesTitlePane.setPrefWidth(width);
        }
    }

    /**
     * Permite actualizar una imagen seleccionada
     * @param imagePath Path de la imagen a actualizar
     */
    public void updateSelected(String imagePath) {
        for (CanvasImage canvasImage : currentImages) {
            if (canvasImage.getCanvasObjectData().getData().equals(imagePath)) {
                canvasImage.setSelected(true);
                updateSelected();
            }
        }
    }

    /**
     * Permite actualizar la imagen seleccionada
     */
    public void updateSelected() {
        max = null;
        int index = getLastSelectedImageIndex();
        if (index > -1 && max != null) {
            for (int i = 0; i < currentImages.size(); i++) {
                if (i != index) {
                    currentImages.get(i).setSelected(false);
                }
            }
        }
        setResultValues(currentImages.get(index).getImage(), currentImages.get(index).getCanvasObjectData().getData());
    }

    /**
     * Permite obtener el índice de la última imagen seleccionada
     * @return Índice de la imagen seleccionada
     */
    private int getLastSelectedImageIndex() {
        int index = -1;
        for (int i = 0; i < currentImages.size(); i++) {
            if (i == 0) {
                max = currentImages.get(i).getLastTimeSelected();
                index = i;
            } else {
                CanvasObject canvasImage = currentImages.get(i);
                if (canvasImage.getLastTimeSelected() != null && max != null && max.isBefore(canvasImage.getLastTimeSelected())) {
                    max = currentImages.get(i).getLastTimeSelected();
                    index = i;
                }
            }
        }
        return index;
    }

    /**
     * Permite definir los atributos de retorno
     * @param image Imagen a ser definida como retorno
     * @param imagePath Path de la imagen a ser definida como retorno
     */
    public void setResultValues(Image image, String imagePath) {
        this.selectedImage = image;
        this.selectedImagePath = imagePath;
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
