package andrade.luis.hmiethernetip;

import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

@SuppressWarnings("restriction")
public class DesktopPane extends Pane {
    public DesktopPane(){
        super();
        setId("#desktop");
        Rectangle rect = new Rectangle(50,50,50,50);
        rect.setId(".rect");
        Button button = new Button("click me!");
        button.setId(".button");
        getChildren().add(button);
        getChildren().add(rect);
        System.out.println(getId());
        for(int i=0;i<this.getChildren().size();i++){
            System.out.println(this.getChildren().get(i).getClass()+"---"+this.getChildren().get(i).getId());
        }

    }
}