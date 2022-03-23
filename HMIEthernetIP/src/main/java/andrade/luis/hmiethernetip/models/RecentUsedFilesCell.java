package andrade.luis.hmiethernetip.models;

import javafx.scene.control.ListCell;

public class RecentUsedFilesCell extends ListCell<String>
{
    @Override
    public void updateItem(String string, boolean empty)
    {
        super.updateItem(string,empty);
        if(string != null)
        {
            setItem(string);
        }
    }
}
