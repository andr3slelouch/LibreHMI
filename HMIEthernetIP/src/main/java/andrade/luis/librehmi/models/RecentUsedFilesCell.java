package andrade.luis.librehmi.models;

import andrade.luis.librehmi.HMIApp;
import andrade.luis.librehmi.views.RecentUsedFilesItem;
import javafx.scene.control.ListCell;

public class RecentUsedFilesCell extends ListCell<String> {
    HMIApp hmiApp;
    @Override
    public void updateItem(String item, boolean empty)
    {
        super.updateItem(item, empty);
        if(item!=null && !empty){
            RecentUsedFilesItem recentUsedFilesItem = new RecentUsedFilesItem(this.hmiApp);
            recentUsedFilesItem.setInfo(item);
            setGraphic(recentUsedFilesItem.getBox());
        }
    }
    public RecentUsedFilesCell(HMIApp hmiApp){
        super();
        this.hmiApp = hmiApp;
    }
}
