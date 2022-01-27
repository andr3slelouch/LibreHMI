package andrade.luis.hmiethernetip.models;

import javafx.scene.control.TableCell;

public class CanvasCell<T> extends TableCell<T, String> {
    private final String value;

    public CanvasCell(){
        value = new String();
    }

    @Override
    protected void updateItem(String value,boolean empty){
        if(empty || value == null){
            setText(null);
        }else{
            setText(value);
        }
    }
}
