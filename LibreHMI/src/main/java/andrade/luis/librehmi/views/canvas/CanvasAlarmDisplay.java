package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.Alarm;
import andrade.luis.librehmi.models.AlarmRow;
import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.models.users.HMIUser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Clase que define el objeto CanvasAlarmDisplay, que permitirá mostrar alarmas dentro de una tabla
 */
public class CanvasAlarmDisplay extends CanvasObject {
    private static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private static final String ACKNOWLEDGED_STATE = "Reconocida";
    private static final String ACKNOWLEDGED_ALARM_STYLE = "acknowledgedAlarm";
    private static final String UNACKNOWLEDGED_ALARM_STYLE = "unacknowledgedAlarm";
    private static final String DEACTIVATED_ALARM_STYLE = "deactivatedAlarm";
    private TableView<AlarmRow> alarmsTable;
    private final ArrayList<Alarm> activatedAlarms = new ArrayList<>();

    /**
     * Constructor para pegar un CanvasAlarmDisplay copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasAlarmDisplay(CanvasObjectData canvasObjectData) {
        super(canvasObjectData);
        setData(canvasObjectData.getPosition().getX(), canvasObjectData.getPosition().getY(), canvasObjectData.getWidth(), canvasObjectData.getHeight(), true);
    }

    @Override
    public HMIUser getUser() {
        return user;
    }

    @Override
    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    /**
     * Constructor que permite agregar un nuevo CanvasAlarmDisplay al canvas
     * @param center CanvasPoint con la posición del objeto
     * @param isOnCanvas Bandera para definir si el objeto está dentro del canvas
     */
    public CanvasAlarmDisplay(CanvasPoint center, boolean isOnCanvas) {
        super(center);
        setData(center.getX(), center.getY(), 950, 400, isOnCanvas);
    }

    /**
     * Método que permite definir las propiedades del objeto
     * @param x Posición del objeto en X
     * @param y Posición dle objeto en Y
     * @param width Ancho del objeto
     * @param height Alto del objeto
     * @param isOnCanvas Bandera que define si el objeto está en el canvas
     */
    private void setData(double x, double y, double width, double height, boolean isOnCanvas) {
        this.getCanvasObjectData().setType("AlarmDisplay");
        this.getCanvasObjectData().setDataType("Resumen de Alarmas");
        this.getCanvasObjectData().setSuperType("AlarmManagementObject");
        this.getCanvasObjectData().setPosition(new CanvasPoint(x, y));
        this.alarmsTable = new TableView<>();

        TableColumn<AlarmRow, String> rowNumberColumn = new TableColumn<>("#");
        rowNumberColumn.setPrefWidth(40);
        rowNumberColumn.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        TableColumn<AlarmRow, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<AlarmRow, String> expressionColumn = new TableColumn<>("Alarma");
        expressionColumn.setCellValueFactory(new PropertyValueFactory<>("expression"));
        TableColumn<AlarmRow, String> datetimeColumn = new TableColumn<>("Fecha y Hora");
        datetimeColumn.setPrefWidth(120);
        datetimeColumn.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        TableColumn<AlarmRow, String> typeColumn = new TableColumn<>("Tipo");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<AlarmRow, String> hiHiValueColumn = new TableColumn<>("Límite Alto Alto");
        hiHiValueColumn.setPrefWidth(120);
        hiHiValueColumn.setCellValueFactory(new PropertyValueFactory<>("hiHiValue"));
        TableColumn<AlarmRow, String> maxValueColumn = new TableColumn<>("Límite Alto");
        maxValueColumn.setCellValueFactory(new PropertyValueFactory<>("maxValue"));
        TableColumn<AlarmRow, String> minValueColumn = new TableColumn<>("Límite Bajo");
        minValueColumn.setCellValueFactory(new PropertyValueFactory<>("minValue"));
        TableColumn<AlarmRow, String> loLoValueColumn = new TableColumn<>("Límite Bajo Bajo");
        loLoValueColumn.setPrefWidth(120);
        loLoValueColumn.setCellValueFactory(new PropertyValueFactory<>("loLoValue"));
        TableColumn<AlarmRow, String> valueColumn = new TableColumn<>("Valor");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        TableColumn<AlarmRow, String> statusColumn = new TableColumn<>("Estado");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        TableColumn<AlarmRow, String> acknowledgementColumn = new TableColumn<>("Reconocimiento");
        acknowledgementColumn.setPrefWidth(120);
        acknowledgementColumn.setCellValueFactory(new PropertyValueFactory<>("acknowledgement"));

        ObservableList<AlarmRow> data = FXCollections.observableArrayList();

        this.alarmsTable.setItems(data);

        this.alarmsTable.getColumns().addAll(
                rowNumberColumn,
                nameColumn,
                expressionColumn,
                datetimeColumn,
                typeColumn,
                hiHiValueColumn,
                maxValueColumn,
                minValueColumn,
                loLoValueColumn,
                valueColumn,
                statusColumn,
                acknowledgementColumn
        );

        setRowFactory();
        this.alarmsTable.setPlaceholder(new Label("No existen Alarmas iniciadas"));
        this.alarmsTable.setPrefWidth(width);
        this.alarmsTable.setPrefHeight(height);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        if (!isOnCanvas) {
            this.setOnMouseDragged(mouseEvent -> {
            });
            this.setOnMousePressed(mouseEvent -> {
            });
            this.setOnMouseReleased(mouseEvent -> {
            });
        }

        TableView.TableViewSelectionModel<AlarmRow> selectionModel = this.alarmsTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCenter(this.alarmsTable);
    }

    /**
     * Permite definir el constructor de filas de la tabla
     */
    public void setRowFactory() {
        this.alarmsTable.setRowFactory(alarmsTableView -> new TableRow<>() {
            @Override
            protected void updateItem(AlarmRow item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    if (item.getStatus().equals("Activada")) {
                        if (item.getAcknowledgement().equals("No Reconocida")) {
                            getStyleClass().remove(ACKNOWLEDGED_ALARM_STYLE);
                            getStyleClass().remove(DEACTIVATED_ALARM_STYLE);
                            getStyleClass().add(UNACKNOWLEDGED_ALARM_STYLE);

                            ContextMenu contextMenu = new ContextMenu();

                            MenuItem newItem = new MenuItem();
                            newItem.setText("Reconocer");
                            newItem.setOnAction(event -> addAcknowledgedAlarm(item));
                            contextMenu.getItems().addAll(newItem);
                            setContextMenu(contextMenu);

                        } else if (item.getAcknowledgement().equals(ACKNOWLEDGED_STATE)) {
                            getStyleClass().remove(UNACKNOWLEDGED_ALARM_STYLE);
                            getStyleClass().remove(DEACTIVATED_ALARM_STYLE);
                            getStyleClass().add(ACKNOWLEDGED_ALARM_STYLE);
                        }
                    } else {
                        getStyleClass().remove(ACKNOWLEDGED_ALARM_STYLE);
                        getStyleClass().remove(UNACKNOWLEDGED_ALARM_STYLE);
                        getStyleClass().add(DEACTIVATED_ALARM_STYLE);
                    }
                } else {
                    setItem(null);
                }
            }
        });
    }

    /**
     * Permite cambiar el estado de una alarma de "No Reconocido" a "Reconocido"
     * @param item Fila de alarma
     */
    private void addAcknowledgedAlarm(AlarmRow item){
        AlarmRow alarmRow = new AlarmRow(
                String.valueOf(this.alarmsTable.getItems().size() + 1),
                item.getName(),
                item.getExpression(),
                DateTimeFormatter.ofPattern(DATETIME_FORMAT).format(LocalDateTime.now()),
                item.getType(),
                item.getStatus(),
                ACKNOWLEDGED_STATE
        );
        alarmRow.setValues(item.getMaxValue(),
                item.getHiHiValue(),
                item.getMinValue(),
                item.getLoLoValue(),
                item.getValue());
        this.alarmsTable.getItems().add(alarmRow);
        for (int i = 0; i < CanvasAlarmDisplay.this.getHmiApp().getProjectAlarms().size(); i++) {
            if (item.getName().equals(CanvasAlarmDisplay.this.getHmiApp().getProjectAlarms().get(i).getName())) {
                CanvasAlarmDisplay.this.getHmiApp().getProjectAlarms().get(i).setAcknowledgement(ACKNOWLEDGED_STATE);
            }
        }
    }

    /**
     * Permite definir el hilo de verificación de alarmas
     */
    public void setUpdateTableTimeline() {
        Timeline updateTableTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            for (int i = 0; i < this.getHmiApp().getProjectAlarms().size(); i++) {
                                addAlarmFromTimeline(this.getHmiApp().getProjectAlarms().get(i));
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        updateTableTimeline.setCycleCount(Animation.INDEFINITE);
        updateTableTimeline.play();
    }

    /**
     * Permite añadir una nueva alarma a la tabla
     * @param alarm Objeto de alarma para agregarse a la tabla
     */
    private void addAlarmFromTimeline(Alarm alarm) {
        try {
            boolean alarmStatus = alarm.checkAlarm();
            int alarmIndex = -1;
            for (int j = 0; j < activatedAlarms.size(); j++) {
                if (activatedAlarms.get(j).getName().equals(alarm.getName()) && activatedAlarms.get(j).getType().equals(alarm.getType())) {
                    alarmIndex = j;
                }
            }
            if (alarmStatus && alarmIndex == -1) {
                this.activatedAlarms.add(alarm);
                addTableItem(alarm);
            } else if (!alarmStatus && alarmIndex != -1) {
                this.activatedAlarms.remove(alarmIndex);
                addTableItem(alarm);
            }
        } catch (SQLException | CompileException | IOException | InvocationTargetException e) {
            logger.log(Level.INFO,e.getMessage());
        }
    }

    /**
     * Permite definir las propiedades del objeto en cuanto al ancho y alto
     */
    @Override
    public void setProperties() {
        super.setProperties();
        this.alarmsTable.setPrefWidth(this.getCanvasObjectData().getWidth());
        this.alarmsTable.setPrefHeight(this.getCanvasObjectData().getHeight());
    }

    @Override
    public void setEnable(String mode) {
        switch (mode) {
            case "Ejecutar":
                if(!this.getUser().getRole().equals("Administrador")){
                    this.enableListeners(false);
                    this.setOnMousePressed(this.onDoubleClick);
                    this.alarmsTable.setDisable(true);
                }else{
                    this.enableListeners(true);
                    this.alarmsTable.setDisable(false);
                }
                break;
            case "Stop":
                super.setEnable("Stop");
                this.alarmsTable.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.enableListeners(true);
                this.alarmsTable.setDisable(true);
                break;
        }
    }


    public TableView<AlarmRow> getAlarmsTable() {
        return alarmsTable;
    }

    /**
     * Permite actualizar una fila de alarma de la tabla
     * @param oldName Nombre de la alarma a ser modificada
     * @param tableItem Objeto de alarma a modificarse
     */
    public void updateTableItem(String oldName, Alarm tableItem) {
        int index = getAlarmIndex(oldName);
        AlarmRow updatedAlarm = new AlarmRow(
                String.valueOf(index + 1),
                tableItem.getName(),
                tableItem.getExpression().getExpressionToEvaluate(),
                tableItem.getAlarmExecutionDateTime() != null ? tableItem.getAlarmExecutionDateTime() : "-",
                tableItem.getType(),
                tableItem.getStatus(),
                tableItem.getAcknowledgement()
        );
        updatedAlarm.setValues(tableItem.getHighLimit() != null ? String.valueOf(tableItem.getHighLimit()) : "-",
                tableItem.getHiHiLimit() != null ? String.valueOf(tableItem.getHiHiLimit()) : "-",
                tableItem.getLowLimit() != null ? String.valueOf(tableItem.getLowLimit()) : "-",
                tableItem.getLoloLimit() != null ? String.valueOf(tableItem.getLoloLimit()) : "-",
                tableItem.getValue());
        this.alarmsTable.getItems().set(index, updatedAlarm);
    }

    /**
     * Permite eliminar una fila de alarma de la tabla
     * @param name Nombre de la alarma a eliminarse
     */
    public void removeTableItem(String name) {
        int index = getAlarmIndex(name);
        if (index >= 0) {
            this.alarmsTable.getItems().remove(index);
        }
    }

    /**
     * Permite añadir una nueva fila basada en un objeto de alarma
     * @param tableItem Objeto de alarma agregarse a la tabla
     */
    public void addTableItem(Alarm tableItem) {
        AlarmRow alarmRow = new AlarmRow(
                String.valueOf(this.alarmsTable.getItems().size() + 1),
                tableItem.getName(),
                tableItem.getExpression().getExpressionToEvaluate(),
                tableItem.getAlarmExecutionDateTime() != null ? tableItem.getAlarmExecutionDateTime() : "-",
                tableItem.getType(),
                tableItem.getStatus(),
                tableItem.getAcknowledgement()
        );
        alarmRow.setValues(tableItem.getHighLimit() != null ? String.valueOf(tableItem.getHighLimit()) : "-",
                tableItem.getHiHiLimit() != null ? String.valueOf(tableItem.getHiHiLimit()) : "-",
                tableItem.getLowLimit() != null ? String.valueOf(tableItem.getLowLimit()) : "-",
                tableItem.getLoloLimit() != null ? String.valueOf(tableItem.getLoloLimit()) : "-",
                tableItem.getValue());
        this.alarmsTable.getItems().add(alarmRow);
    }

    /**
     * Permite definir las filas de la tabla basándose en una lista de objetos de alarma
     * @param tableItems Lista de objetos de alarma
     */
    public void setTableItems(ArrayList<Alarm> tableItems) {
        for (Alarm tableItem : tableItems) {
            addTableItem(tableItem);
        }
    }

    /**
     * Permite obtener el índice de una alarma en la tabla
     * @param name Nombre de alarma a buscarse dentro de la tabla
     * @return Índice de alarma encontrada
     */
    private int getAlarmIndex(String name) {
        int res = -1;
        for (int i = 0; i < alarmsTable.getItems().size(); i++) {
            if (name.equals(alarmsTable.getItems().get(i).getName())) {
                res = i;
            }
        }
        return res;
    }
}
