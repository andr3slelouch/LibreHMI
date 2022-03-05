package andrade.luis.hmiethernetip.models;

import javafx.scene.control.CheckBox;

public class SceneItem {
    private CheckBox checkBox;
    private String scene;
    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public SceneItem(String scene, CheckBox checkBox) {
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

