package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.views.SelectTagWindow;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class SelectTagWindowTestRow {
    private SelectTagWindow selectTagWindow;

    @Start
    private void start(Stage stage){
        selectTagWindow = new SelectTagWindow(false,"",true);
        stage = selectTagWindow;
        stage.setMaximized(true);
        stage.show();
    }

    @Test
    void clickOnOkWithoutSelectingAnything(FxRobot robot) {
        robot.clickOn(selectTagWindow.getFinishSelectionButton());
        Assertions.assertThat(selectTagWindow.getAlert().isShowing()).isTrue();
    }

    @Test
    void showAlertIfTableIsEmpty(FxRobot robot) {
        robot.clickOn();
        Assertions.assertThat(selectTagWindow.getAlert().isShowing()).isFalse();
    }

    @Test
    void getTagIfRowIsSelected(FxRobot robot) {
        selectTagWindow.getTable().setItems(selectTagWindow.getExistingTags(false,""));
        robot.clickOn(selectTagWindow.getTable().getLayoutX()+20,selectTagWindow.getTable().getLayoutY()+100);
        robot.clickOn(selectTagWindow.getFinishSelectionButton());
        Assertions.assertThat(selectTagWindow.getAlert().isShowing()).isFalse();
        Assertions.assertThat(selectTagWindow.getSelectedTag()).isNotNull();
    }

}