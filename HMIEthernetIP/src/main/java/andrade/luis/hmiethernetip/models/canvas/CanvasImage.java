package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import andrade.luis.hmiethernetip.views.SelectHMISymbolWindow;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

public class CanvasImage extends GraphicalRepresentation {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private ImageView imageView;
    private Image image;

    public CanvasImage(Image image, CanvasPoint center, boolean isOnCanvas, String imagePath, boolean isImageSymbol) {
        super(center);
        this.setData(image, imagePath, isOnCanvas, 100, 100, isImageSymbol);
    }

    public CanvasImage(GraphicalRepresentationData graphicalRepresentationData) throws FileNotFoundException {
        super(graphicalRepresentationData);
        this.image = new Image(new FileInputStream(graphicalRepresentationData.getData()));
        this.setData(this.image, this.getGraphicalRepresentationData().getData(), true, this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight(), this.getGraphicalRepresentationData().isImageSymbol());
        this.modifyImageViewSizeRotation(this.getGraphicalRepresentationData().isMirroringHorizontal(),this.getGraphicalRepresentationData().isMirroringVertical(),this.getGraphicalRepresentationData().getRotation());
        if(this.getGraphicalRepresentationData().isModifyingImage()){
            this.modifyImageViewColors(this.getGraphicalRepresentationData().getPrimaryColor(),this.getGraphicalRepresentationData().getContrast(),this.getGraphicalRepresentationData().getBrightness(),this.getGraphicalRepresentationData().getSaturation(),this.getGraphicalRepresentationData().getHue());
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
            this.getGraphicalRepresentationData().setModifyingImage(true);
            this.getGraphicalRepresentationData().setPrimaryColor(color);
            this.getGraphicalRepresentationData().setContrast(contrast);
            this.getGraphicalRepresentationData().setBrightness(brightness);
            this.getGraphicalRepresentationData().setHue(hue);
        }
    }

    public void modifyImageViewSizeRotation(boolean isMirroringHorizontal, boolean isMirroringVertical, double rotation){
        if (this.imageView != null) {
            this.imageView.setRotate(rotation);
            this.getGraphicalRepresentationData().setRotation(rotation);
            if (isMirroringHorizontal) {
                this.getGraphicalRepresentationData().setMirroringHorizontal(true);
                this.imageView.setScaleX(-1);
            }
            if (isMirroringVertical) {
                this.getGraphicalRepresentationData().setMirroringVertical(true);
                this.imageView.setScaleY(-1);
            }
        }
    }

    private void setData(Image image, String imagePath, boolean isOnCanvas, double width, double height, boolean isImageSymbol) {
        this.image = image;
        this.imageView = new ImageView(this.image);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(true);
        this.getGraphicalRepresentationData().setImageSymbol(isImageSymbol);
        this.setCenter(this.imageView);
        this.getGraphicalRepresentationData().setType("Image");
        this.getGraphicalRepresentationData().setWidth(width);
        this.getGraphicalRepresentationData().setHeight(height);
        this.getGraphicalRepresentationData().setData(imagePath);
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
        this.getRightClickMenu().getItems().add(editMI);
    }

    private void setCanvasImage() throws FileNotFoundException {
        if (this.getGraphicalRepresentationData().isImageSymbol()) {
            setSymbolImageProcess();
        } else {
            setImageViewProcess();
        }
    }

    private void setSymbolImageProcess() throws FileNotFoundException {
        SelectHMISymbolWindow selectHMISymbolWindow = new SelectHMISymbolWindow();
        if (this.getGraphicalRepresentationData() != null) {
            selectHMISymbolWindow.setMirroringHorizontal(this.getGraphicalRepresentationData().isMirroringHorizontal());
            selectHMISymbolWindow.setMirroringVertical(this.getGraphicalRepresentationData().isMirroringHorizontal());
            selectHMISymbolWindow.setRotation(this.getGraphicalRepresentationData().getRotation());
            selectHMISymbolWindow.updateSelected(this.getGraphicalRepresentationData().getData());
            if(this.getGraphicalRepresentationData().isModifyingImage()){
                selectHMISymbolWindow.setColor(this.getGraphicalRepresentationData().getPrimaryColor());
                selectHMISymbolWindow.setContrast(this.getGraphicalRepresentationData().getContrast());
                selectHMISymbolWindow.setBrightness(this.getGraphicalRepresentationData().getBrightness());
                selectHMISymbolWindow.setSaturation(this.getGraphicalRepresentationData().getSaturation());
                selectHMISymbolWindow.setHue(this.getGraphicalRepresentationData().getHue());
                selectHMISymbolWindow.setModifyingColor(this.getGraphicalRepresentationData().isModifyingImage());
            }
        }
        selectHMISymbolWindow.showAndWait();
        Image selectedSymbol = selectHMISymbolWindow.getSelectedImage();
        String selectedSymbolPath = selectHMISymbolWindow.getSelectedImagePath();
        boolean isMirroringVertical = selectHMISymbolWindow.isMirroringVertical();
        boolean isMirroringHorizontal = selectHMISymbolWindow.isMirroringHorizontal();
        double rotation = selectHMISymbolWindow.getRotation();
        boolean isModifyingColor = selectHMISymbolWindow.isModifyingColor();
        double contrast = selectHMISymbolWindow.getContrast();
        double brightness = selectHMISymbolWindow.getBrightness();
        double saturation = selectHMISymbolWindow.getSaturation();
        double hue = selectHMISymbolWindow.getHue();
        CanvasColor color = selectHMISymbolWindow.getColor();
        setData(selectedSymbol, selectedSymbolPath, true, this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight(), true);
        modifyImageViewSizeRotation(isMirroringHorizontal,isMirroringVertical,rotation);
        if(isModifyingColor){
            modifyImageViewColors(color,contrast,brightness,saturation,hue);
        }
    }

    private void setImageViewProcess() {

    }

    public Image getImage() {
        return this.image;
    }

    @Override
    public void resize() {
        super.resize();
        this.imageView.setFitWidth(this.getGraphicalRepresentationData().getWidth());
        this.imageView.setFitHeight(this.getGraphicalRepresentationData().getHeight());
        this.imageView.setPreserveRatio(true);
    }
}
