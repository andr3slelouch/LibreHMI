package andrade.luis.hmiethernetip.util.example;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private final StringProperty name = new SimpleStringProperty();
    private final BooleanProperty selected = new SimpleBooleanProperty();

    public Item(String name) {
        setName(name);
        setSelected(false);
    }

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public final BooleanProperty selectedProperty() {
        return this.selected;
    }

    public final boolean isSelected() {
        return this.selectedProperty().get();
    }

    public final void setSelected(final boolean selected) {
        this.selectedProperty().set(selected);
    }

}

