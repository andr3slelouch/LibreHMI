package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.controllers.HMIScene;
import javafx.scene.control.CheckBox;

public class SceneItem {
    private CheckBox checkBox;
    private HMIScene scene;
    public HMIScene getScene() {
        return scene;
    }

    public void setScene(HMIScene scene) {
        this.scene = scene;
    }

    public SceneItem(HMIScene scene, CheckBox checkBox) {
        this.scene = scene;
        this.checkBox = checkBox;
    }
    public boolean isSelected() {
        return this.checkBox.isSelected();
    }

    public void setSelected(boolean selected) {
        this.checkBox.setSelected(selected);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }
}

