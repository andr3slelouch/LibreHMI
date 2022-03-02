package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.GraphicalRepresentationData;
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

    public CanvasImage(Image image, CanvasPoint center, boolean isOnCanvas, String imagePath){
        super(center);
        this.setData(image,imagePath,isOnCanvas,100,100);
    }

    public CanvasImage(GraphicalRepresentationData graphicalRepresentationData) throws FileNotFoundException {
        super(graphicalRepresentationData);
        this.image = new Image(new FileInputStream(graphicalRepresentationData.getData()));
        this.setData(this.image,this.getGraphicalRepresentationData().getData(),true,this.getGraphicalRepresentationData().getWidth(), this.getGraphicalRepresentationData().getHeight());
    }

    private void setData(Image image,String imagePath ,boolean isOnCanvas, double width, double height) {
        this.image = image;
        this.imageView = new ImageView(this.image);
        this.imageView.setFitWidth(width);
        this.imageView.setFitHeight(height);
        this.imageView.setPreserveRatio(true);
        this.setCenter(this.imageView);
        this.getGraphicalRepresentationData().setType("Image");
        this.getGraphicalRepresentationData().setWidth(width);
        this.getGraphicalRepresentationData().setHeight(height);
        this.getGraphicalRepresentationData().setData(imagePath);
        if(!isOnCanvas){
            this.setOnMouseDragged(mouseEvent -> {});
        }
        this.setSelected(false);
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
