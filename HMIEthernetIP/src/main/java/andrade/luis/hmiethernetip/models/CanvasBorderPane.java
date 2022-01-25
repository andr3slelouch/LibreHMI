package andrade.luis.hmiethernetip.models;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

import java.time.LocalDateTime;

public class CanvasBorderPane extends BorderPane {
    private CanvasInterface canvas;
    private GraphicalRepresentation graphicalRepresentation;
    private PseudoClass border;
    private SimpleBooleanProperty borderActive;
    private LocalDateTime lastTimeSelected;
    private ContextMenu rightClickMenu;
    private MenuItem copyMenuItem;
    private MenuItem cutMenuItem;
    private MenuItem deleteMenuItem;
}
