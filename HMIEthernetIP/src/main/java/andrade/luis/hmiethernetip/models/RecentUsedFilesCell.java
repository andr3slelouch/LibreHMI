package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.views.WelcomeWindow;
import javafx.scene.control.ListCell;

import java.io.IOException;
import java.util.logging.Level;

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
