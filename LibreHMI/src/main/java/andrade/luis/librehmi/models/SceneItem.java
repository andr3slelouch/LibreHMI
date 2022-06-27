package andrade.luis.librehmi.models;

import javafx.scene.control.CheckBox;

/**
 * Clase que contiene los datos de una página de la ventana de selección de páginas
 */
public class SceneItem {
    private final CheckBox checkBox;
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

}

