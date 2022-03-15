package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.AlarmRow;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class CanvasDisplayAlarm extends CanvasObject{
    private TableView<Object> alarmsTable;

    public CanvasDisplayAlarm(CanvasPoint center) {
        super(center);
        setData(center.getX(),center.getY(),400,400);
    }

    private void setData(double x, double y, double width, double height) {
        this.alarmsTable = new TableView<>();

        TableColumn<AlarmRow, String> rowNumberColumn = new TableColumn<>("#");
        rowNumberColumn.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        TableColumn<AlarmRow, String> datetimeColumn = new TableColumn<>("Fecha y Hora");
        datetimeColumn.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        TableColumn<AlarmRow, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<AlarmRow, String> hiHiValueColumn = new TableColumn<>("Límite Alto Alto");
        hiHiValueColumn.setCellValueFactory(new PropertyValueFactory<>("hiHiValue"));
        TableColumn<AlarmRow, String> maxValueColumn = new TableColumn<>("Límite Alto");
        maxValueColumn.setCellValueFactory(new PropertyValueFactory<>("maxValue"));
        TableColumn<AlarmRow, String> minValueColumn = new TableColumn<>("Límite Bajo");
        minValueColumn.setCellValueFactory(new PropertyValueFactory<>("minValue"));
        TableColumn<AlarmRow, String> loLoValueColumn = new TableColumn<>("Límite Bajo Bajo");
        loLoValueColumn.setCellValueFactory(new PropertyValueFactory<>("loLoValue"));
        TableColumn<AlarmRow, String> valueColumn = new TableColumn<>("Valor");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        TableColumn<AlarmRow, String> statusColumn = new TableColumn<>("Estado");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<AlarmRow, String> acknowledgementColumn = new TableColumn<>("Reconocimiento");
        acknowledgementColumn.setCellValueFactory(new PropertyValueFactory<>("acknowledgement"));
    }
}
