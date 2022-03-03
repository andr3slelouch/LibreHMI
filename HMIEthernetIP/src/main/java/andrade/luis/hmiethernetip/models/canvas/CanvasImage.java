package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
import andrade.luis.hmiethernetip.views.SelectHMISymbolWindow;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasImage extends GraphicalRepresentation{
    Logger logger = Logger.getLogger(this.getClass().getName());
    private ImageView imageView;
    private Image image;

    public CanvasImage(Image image, CanvasPoint center, boolean isOnCanvas, String imagePath, boolean isMirroringVertical, boolean isMirroringHorizontal,double rotation, boolean isImageSymbol){
        super(center);
        this.setData(image,imagePath,isOnCanvas,100,100,isMirroringVertical,isMirroringHorizontal,rotation, isImageSymbol);
    }

    public CanvasImage(GraphicalRepresentationData graphicalRepresentationData) throws FileNotFoundException {
        super(graphicalRepresentationData);
        this.image = new Image(new FileInputStream(graphicalRepresentationData.getData()));
        this.setData(this.image,this.getGraphicalRepresentationData().getData(),true,this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight(),this.getGraphicalRepresentationData().isMirroringVertical(),this.getGraphicalRepresentationData().isMirroringHorizontal(),this.getGraphicalRepresentationData().getRotation(),this.getGraphicalRepresentationData().isImageSymbol());
    }

    private void setData(Image image,String imagePath ,boolean isOnCanvas, double width, double height,boolean isMirroringVertical, boolean isMirroringHorizontal, double rotation, boolean isImageSymbol) {
        this.image = image;
        this.imageView = new ImageView(this.image);
        this.imageView.setRotate(rotation);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(true);
        this.getGraphicalRepresentationData().setImageSymbol(isImageSymbol);
        if(isMirroringHorizontal){
            this.imageView.setScaleX(-1);
        }
        if(isMirroringVertical){
            this.imageView.setScaleY(-1);
        }
        this.setCenter(this.imageView);
        this.getGraphicalRepresentationData().setType("Image");
        this.getGraphicalRepresentationData().setWidth(width);
        this.getGraphicalRepresentationData().setHeight(height);
        this.getGraphicalRepresentationData().setData(imagePath);
        if(!isOnCanvas){
            this.setOnMouseDragged(mouseEvent -> {});
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
        if(this.getGraphicalRepresentationData().isImageSymbol()){
            setSymbolImageProcess();
        }else{
            setImageViewProcess();
        }
    }

    private void setSymbolImageProcess() throws FileNotFoundException {
        SelectHMISymbolWindow selectHMISymbolWindow = new SelectHMISymbolWindow();
        if(this.getGraphicalRepresentationData() != null){
            selectHMISymbolWindow.setMirroringHorizontal(this.getGraphicalRepresentationData().isMirroringHorizontal());
            selectHMISymbolWindow.setMirroringVertical(this.getGraphicalRepresentationData().isMirroringHorizontal());
            selectHMISymbolWindow.setRotation(this.getGraphicalRepresentationData().getRotation());
            selectHMISymbolWindow.updateSelected(this.getGraphicalRepresentationData().getData());
        }
        selectHMISymbolWindow.showAndWait();
        Image selectedSymbol = selectHMISymbolWindow.getSelectedImage();
        String selectedSymbolPath = selectHMISymbolWindow.getSelectedImagePath();
        boolean isMirroringVertical = selectHMISymbolWindow.isMirroringVertical();
        boolean isMirroringHorizontal = selectHMISymbolWindow.isMirroringHorizontal();
        double rotation = selectHMISymbolWindow.getRotation();
        setData(selectedSymbol,selectedSymbolPath,true,this.getGraphicalRepresentationData().getWidth(),this.getGraphicalRepresentationData().getHeight(),isMirroringVertical,isMirroringHorizontal,rotation,true);
    }

    private void setImageViewProcess(){

    }

    public Image getImage() {
        return this.image;
    }

    @Override
    public void resize(){
        super.resize();
        this.imageView.setFitWidth(this.getGraphicalRepresentationData().getWidth());
        this.imageView.setFitHeight(this.getGraphicalRepresentationData().getHeight());
        this.imageView.setPreserveRatio(true);
    }
}
