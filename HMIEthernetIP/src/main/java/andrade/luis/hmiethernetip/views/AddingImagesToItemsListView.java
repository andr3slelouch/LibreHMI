package andrade.luis.hmiethernetip.views;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class AddingImagesToItemsListView extends Application {
    /*loading images with their paths*/
    private final Image cabinetImage  = new Image("Cab.png");
    private final Image docIconImage  = new Image("documenticon.png");
    private final Image homeCabImage  = new Image("HomCab.png");
    private final Image searchIconImage = new Image("searchicon.png");
    /*image array to load all images at a time*/
    private Image[] imagesArray = {cabinetImage, docIconImage, homeCabImage, searchIconImage};
    @Override
    public void start(Stage displayScreen) throws Exception {
        /* create list object */
        ListView<String> listViewReference = new ListView<String>();
        /* adding items to the list view */
        ObservableList<String> elements = FXCollections.observableArrayList("Fist Image", "Second Image", "Third Image",
                "Fourth Image");
        listViewReference.setItems(elements);
        /*setting each image to corresponding array index*/
        listViewReference.setCellFactory(param -> new ListCell<String>() {
            /*view the image class to display the image*/
            private ImageView displayImage = new ImageView();
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    if (name.equals("Fist Image"))
                        displayImage.setImage(imagesArray[0]); /*setting array image to First Image*/
                    else if (name.equals("Second Image"))
                        displayImage.setImage(imagesArray[1]);/*setting array image to Second Image*/
                    else if (name.equals("Third Image"))
                        displayImage.setImage(imagesArray[2]);/*setting array image to Third Image*/
                    else if (name.equals("Fourth Image"))
                        displayImage.setImage(imagesArray[3]);/*setting array image to Fourth Image*/
                    setText(name);
                    setGraphic(displayImage);
                }
            }
        });
        /* creating vertical box to add item objects */
        VBox vBox = new VBox(listViewReference);
        /* creating scene */
        Scene scene = new Scene(vBox, 220, 270);
        /* adding scene to stage */
        displayScreen.setScene(scene);
        /* display scene for showing output */
        displayScreen.show();
    }
    public static void main(String[] args) {
        /* launch method calls internally start() method */
        Application.launch(args);
    }
}


