package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.views.SelectHMISymbolWindow;
import andrade.luis.hmiethernetip.views.SetImageOptionsWindow;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasImage extends CanvasObject {
    Logger logger
            = Logger.getLogger(
            getClass().getName());
    private ImageView imageView;
    /**
     * Image Not found attribution:
     * <a href="https://www.flaticon.com/free-icons/page-not-found" title="page-not-found icons">Page-not-found icons created by Pixel perfect - Flaticon</a>
     */
    private final Image imageNotFound = new Image(getClass().getResource("not-found.png").toExternalForm());
    private Image image;

    public CanvasImage(Image image, CanvasPoint center, boolean isOnCanvas, String imagePath, boolean isImageSymbol) {
        super(center);
        this.setData(image, imagePath, isOnCanvas, 100, 100, isImageSymbol);
    }

    public CanvasImage(CanvasObjectData canvasObjectData){
        super(canvasObjectData);

        try {
            this.image = new Image(new FileInputStream(canvasObjectData.getData()));
        } catch (FileNotFoundException e) {
            this.image = imageNotFound;
        }

        this.setData(this.image, this.getCanvasObjectData().getData(), true, this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), this.getCanvasObjectData().isImageSymbol());
        this.modifyImageViewSizeRotation(this.getCanvasObjectData().isMirroringHorizontal(),this.getCanvasObjectData().isMirroringVertical(),this.getCanvasObjectData().getRotation());
        if(this.getCanvasObjectData().isModifyingImage()){
            this.modifyImageViewColors(this.getCanvasObjectData().getPrimaryColor(),this.getCanvasObjectData().getContrast(),this.getCanvasObjectData().getBrightness(),this.getCanvasObjectData().getSaturation(),this.getCanvasObjectData().getHue());
        }
    }

    public void modifyImageViewColors(CanvasColor color, double contrast, double brightness, double saturation, double hue) {
        if (this.imageView != null) {
            Lighting lighting = new Lighting(new Light.Distant(45, 90, color.getColor()));
            ColorAdjust bright = new ColorAdjust();
            bright.setContrast(contrast);
            bright.setHue(-0.05);
            bright.setSaturation(saturation);
            bright.setBrightness(brightness);
            bright.setHue(hue);
            lighting.setContentInput(bright);
            lighting.setSurfaceScale(0.0);
            this.imageView.setEffect(lighting);
            this.getCanvasObjectData().setModifyingImage(true);
            this.getCanvasObjectData().setPrimaryColor(color);
            this.getCanvasObjectData().setContrast(contrast);
            this.getCanvasObjectData().setBrightness(brightness);
            this.getCanvasObjectData().setHue(hue);
        }
    }

    public void modifyImageViewSizeRotation(boolean isMirroringHorizontal, boolean isMirroringVertical, double rotation){
        if (this.imageView != null) {
            this.imageView.setRotate(rotation);
            this.getCanvasObjectData().setRotation(rotation);
            if (isMirroringHorizontal) {
                this.getCanvasObjectData().setMirroringHorizontal(true);
                this.imageView.setScaleX(-1);
            }
            if (isMirroringVertical) {
                this.getCanvasObjectData().setMirroringVertical(true);
                this.imageView.setScaleY(-1);
            }
        }
    }

    private void setData(Image image, String imagePath, boolean isOnCanvas, double width, double height, boolean isImageSymbol) {
        if(imagePath != null) {
            this.image = image;
        }else{
            this.image = imageNotFound;
        }
        this.imageView = new ImageView(this.image);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(true);
        this.getCanvasObjectData().setImageSymbol(isImageSymbol);
        this.setCenter(this.imageView);
        this.getCanvasObjectData().setType("Image");
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.getCanvasObjectData().setData(imagePath);
        if (!isOnCanvas) {
            this.setOnMouseDragged(mouseEvent -> {
            });
        }
        this.setSelected(false);
        this.setContextMenu();
        MenuItem editMI = new MenuItem("Editar");
        editMI.setId("#editMI");
        editMI.setOnAction(actionEvent -> {
            try {
                this.setCanvasImage();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        this.getRightClickMenu().getItems().set(4,editMI);
    }

    private void setCanvasImage() throws FileNotFoundException {
        if (this.getCanvasObjectData().isImageSymbol()) {
            setSymbolImageProcess();
        } else {
            setImageViewProcess();
        }
        this.getHmiApp().setWasModified(true);
    }

    private void setSymbolImageProcess() throws FileNotFoundException {
        SelectHMISymbolWindow selectHMISymbolWindow = new SelectHMISymbolWindow();
        if (this.getCanvasObjectData() != null) {
            selectHMISymbolWindow.setMirroringHorizontal(this.getCanvasObjectData().isMirroringHorizontal());
            selectHMISymbolWindow.setMirroringVertical(this.getCanvasObjectData().isMirroringHorizontal());
            selectHMISymbolWindow.setRotation(this.getCanvasObjectData().getRotation());
            selectHMISymbolWindow.setSymbolCategory(this.getCanvasObjectData().getSymbolCategory());
            selectHMISymbolWindow.setSelectedImagePath(this.getCanvasObjectData().getData());
            if(this.getCanvasObjectData().isModifyingImage()){
                selectHMISymbolWindow.setColor(this.getCanvasObjectData().getPrimaryColor());
                selectHMISymbolWindow.setContrast(this.getCanvasObjectData().getContrast());
                selectHMISymbolWindow.setBrightness(this.getCanvasObjectData().getBrightness());
                selectHMISymbolWindow.setSaturation(this.getCanvasObjectData().getSaturation());
                selectHMISymbolWindow.setHue(this.getCanvasObjectData().getHue());
                selectHMISymbolWindow.setModifyingColor(this.getCanvasObjectData().isModifyingImage());
            }
        }
        selectHMISymbolWindow.showAndWait();

        String selectedSymbolPath = selectHMISymbolWindow.getSelectedImagePath();
        Image selectedSymbol = new Image(new FileInputStream(selectedSymbolPath));
        boolean isMirroringVertical = selectHMISymbolWindow.isMirroringVertical();
        boolean isMirroringHorizontal = selectHMISymbolWindow.isMirroringHorizontal();
        boolean isPreservingRatio = selectHMISymbolWindow.isPreservingRatio();
        double rotation = selectHMISymbolWindow.getRotation();
        boolean isModifyingColor = selectHMISymbolWindow.isModifyingColor();
        double contrast = selectHMISymbolWindow.getContrast();
        double brightness = selectHMISymbolWindow.getBrightness();
        double saturation = selectHMISymbolWindow.getSaturation();
        double hue = selectHMISymbolWindow.getHue();
        CanvasColor color = selectHMISymbolWindow.getColor();
        this.getCanvasObjectData().setPreservingRatio(isPreservingRatio);
        setData(selectedSymbol, selectedSymbolPath, true, this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), true);
        modifyImageViewSizeRotation(isMirroringHorizontal,isMirroringVertical,rotation);
        if(isModifyingColor){
            modifyImageViewColors(color,contrast,brightness,saturation,hue);
        }
    }

    private void setImageViewProcess() throws FileNotFoundException {
        SetImageOptionsWindow imageOptionsWindow = new SetImageOptionsWindow();
        if(this.getCanvasObjectData().isModifyingImage()){
            imageOptionsWindow.setModifyingColor(true);
            imageOptionsWindow.getBrightnessTextField().setText(String.valueOf(this.getCanvasObjectData().getBrightness()));
            imageOptionsWindow.getContrastTextField().setText(String.valueOf(this.getCanvasObjectData().getContrast()));
            imageOptionsWindow.getHueTextField().setText(String.valueOf(this.getCanvasObjectData().getHue()));
            imageOptionsWindow.getSaturationTextField().setText(String.valueOf(this.getCanvasObjectData().getSaturation()));
            imageOptionsWindow.getColorPicker().setValue(this.getCanvasObjectData().getPrimaryColor().getColor());
        }
        imageOptionsWindow.getImagePathTextField().setText(this.getCanvasObjectData().getData());
        imageOptionsWindow.getMirrorHorizontalCheckBox().setSelected(this.getCanvasObjectData().isMirroringHorizontal());
        imageOptionsWindow.getMirrorVerticalCheckBox().setSelected(this.getCanvasObjectData().isMirroringVertical());
        imageOptionsWindow.getPreserveRatioCheckBox().setSelected(this.getCanvasObjectData().isPreservingRatio());
        imageOptionsWindow.getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        imageOptionsWindow.showAndWait();

        String selectedImagePath = imageOptionsWindow.getImagePathTextField().getText();
        Image selectedImage = new Image(new FileInputStream(selectedImagePath));
        boolean isMirroringVertical = imageOptionsWindow.isMirroringVertical();
        boolean isMirroringHorizontal = imageOptionsWindow.isMirroringHorizontal();
        boolean isPreservingRatio = imageOptionsWindow.isPreservingRatio();
        double rotation = Double.parseDouble(imageOptionsWindow.getRotationTextField().getText());
        boolean isModifyingColor = imageOptionsWindow.isModifyingColor();
        double contrast = Double.parseDouble(imageOptionsWindow.getContrastTextField().getText());
        double brightness = Double.parseDouble(imageOptionsWindow.getBrightnessTextField().getText());
        double saturation = Double.parseDouble(imageOptionsWindow.getSaturationTextField().getText());
        double hue = Double.parseDouble(imageOptionsWindow.getHueTextField().getText());
        CanvasColor color = new CanvasColor(imageOptionsWindow.getColorPicker().getValue());
        this.getCanvasObjectData().setPreservingRatio(isPreservingRatio);
        setData(selectedImage, selectedImagePath , true, this.getCanvasObjectData().getWidth(), this.getCanvasObjectData().getHeight(), false);
        modifyImageViewSizeRotation(isMirroringHorizontal,isMirroringVertical,rotation);
        if(isModifyingColor){
            modifyImageViewColors(color,contrast,brightness,saturation,hue);
        }
    }

    public Image getImage() {
        return this.image;
    }

    @Override
    public void resize() {
        super.resize();
        this.imageView.setFitWidth(this.getCanvasObjectData().getWidth());
        this.imageView.setFitHeight(this.getCanvasObjectData().getHeight());
        this.imageView.setPreserveRatio(this.getCanvasObjectData().isPreservingRatio());
    }
}
