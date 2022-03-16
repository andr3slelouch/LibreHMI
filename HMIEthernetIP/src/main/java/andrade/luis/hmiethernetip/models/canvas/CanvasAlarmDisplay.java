package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.Alarm;
import andrade.luis.hmiethernetip.models.AlarmRow;
import andrade.luis.hmiethernetip.models.users.HMIUser;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;

public class CanvasAlarmDisplay extends CanvasObject {
    private static final String ACKNOWLEDGED_STATE = "Reconocida";
    private static final String ACKNOWLEDGED_ALARM_STYLE = "acknowledgedAlarm";
    private static final String UNACKNOWLEDGED_ALARM_STYLE = "unacknowledgedAlarm";
    private static final String DEACTIVATED_ALARM_STYLE = "deactivatedAlarm";
    private TableView<AlarmRow> alarmsTable;
    private TableView.TableViewSelectionModel<AlarmRow> selectionModel;
    private Timeline updateTableTimeline;
    private ArrayList<Alarm> activatedAlarms = new ArrayList<>();

    public HMIUser getUser() {
        return user;
    }

    public void setUser(HMIUser user) {
        this.user = user;
    }

    private HMIUser user;

    public CanvasAlarmDisplay(CanvasPoint center) {
        super(center);
        setData(center.getX(), center.getY(), 400, 400);
    }

    private void setData(double x, double y, double width, double height) {
        this.alarmsTable = new TableView<>();

        TableColumn<AlarmRow, String> rowNumberColumn = new TableColumn<>("#");
        rowNumberColumn.setCellValueFactory(new PropertyValueFactory<>("rowNumber"));
        TableColumn<AlarmRow, String> nameColumn = new TableColumn<>("Nombre");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<AlarmRow, String> expressionColumn = new TableColumn<>("Alarma");
        expressionColumn.setCellValueFactory(new PropertyValueFactory<>("expression"));
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

        this.alarmsTable.setRowFactory(alarmsTableView -> new TableRow<>() {
            @Override
            protected void updateItem(AlarmRow item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    logger.log(Level.INFO, "Acknowledgement: "+item.getAcknowledgement()+"\nSTATUS: "+item.getStatus());
                    if(item.getStatus().equals("Activada")){
                        if (item.getAcknowledgement().equals("No Reconocida")) {
                            getStyleClass().remove(ACKNOWLEDGED_ALARM_STYLE);
                            getStyleClass().remove(DEACTIVATED_ALARM_STYLE);
                            getStyleClass().add(UNACKNOWLEDGED_ALARM_STYLE);
                        } else if(item.getAcknowledgement().equals(ACKNOWLEDGED_STATE)) {
                            getStyleClass().remove(UNACKNOWLEDGED_ALARM_STYLE);
                            getStyleClass().remove(DEACTIVATED_ALARM_STYLE);
                            getStyleClass().add(ACKNOWLEDGED_ALARM_STYLE);
                        }
                    }else{
                        getStyleClass().remove(ACKNOWLEDGED_ALARM_STYLE);
                        getStyleClass().remove(UNACKNOWLEDGED_ALARM_STYLE);
                        getStyleClass().add(DEACTIVATED_ALARM_STYLE);
                    }


                    ContextMenu contextMenu = new ContextMenu();

                    MenuItem newItem = new MenuItem();
                    newItem.setText("Reconocer");
                    newItem.setOnAction(event -> {
                        this.getTableView().getItems().add(new AlarmRow(
                                String.valueOf(this.getTableView().getItems().size() + 1),
                                item.getName(),
                                item.getExpression(),
                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()),
                                item.getType(),
                                item.getMaxValue(),
                                item.getHiHiValue(),
                                item.getMinValue(),
                                item.getLoLoValue(),
                                item.getValue(),
                                item.getStatus(),
                                ACKNOWLEDGED_STATE
                        ));
                        for(int i = 0; i < CanvasAlarmDisplay.this.getHmiApp().getProjectAlarms().size(); i++ ){
                            if(item.getName().equals(CanvasAlarmDisplay.this.getHmiApp().getProjectAlarms().get(i).getName())){
                                CanvasAlarmDisplay.this.getHmiApp().getProjectAlarms().get(i).setAcknowledgement(ACKNOWLEDGED_STATE);
                                logger.log(Level.INFO,"CHANGED STATE TO ACKNOWLEDGED");
                            }
                        }
                    });
                    contextMenu.getItems().addAll(newItem);
                    setContextMenu(contextMenu);
                } else {
                    setItem(null);
                }
            }
        });
        this.alarmsTable.setPlaceholder(new Label("No existen Alarmas iniciadas"));
        this.alarmsTable.setPrefWidth(width);
        this.alarmsTable.setPrefHeight(height);
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);

        selectionModel = this.alarmsTable.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);

        this.setCenter(this.alarmsTable);
    }

    public void setUpdateTableTimeline() {
        this.updateTableTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            for (int i = 0; i < this.getHmiApp().getProjectAlarms().size(); i++) {
                                try {
                                    Alarm alarm = this.getHmiApp().getProjectAlarms().get(i);
                                    logger.log(Level.INFO,alarm.getAcknowledgement());
                                    boolean alarmStatus = alarm.checkAlarm();
                                    int alarmIndex = -1;
                                    for (int j = 0; j < activatedAlarms.size();j++){
                                        if(activatedAlarms.get(j).getName().equals(alarm.getName())){
                                            alarmIndex = j;
                                        }
                                    }
                                    if (alarmStatus && alarmIndex==-1) {
                                        this.activatedAlarms.add(alarm);
                                        AlarmRow alarmRow = new AlarmRow(
                                                String.valueOf(this.alarmsTable.getItems().size() + 1),
                                                alarm.getName(),
                                                alarm.getExpression().getExpressionToEvaluate(),
                                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()),
                                                alarm.getType(),
                                                alarm.isHighAlarmEnabled() ? String.valueOf(alarm.getHighLimit()) : "-",
                                                alarm.isHiHiAlarmEnabled() ? String.valueOf(alarm.getHiHiLimit()) : "-",
                                                alarm.isLowAlarmEnabled() ? String.valueOf(alarm.getLowLimit()) : "-",
                                                alarm.isLoloAlarmEnabled() ? String.valueOf(alarm.getLoloLimit()) : "-",
                                                alarm.getValue(),
                                                alarm.getStatus(),
                                                alarm.getAcknowledgement()
                                        );
                                        this.alarmsTable.getItems().add(alarmRow);
                                    }else if(!alarmStatus && alarmIndex!=-1){
                                        this.activatedAlarms.remove(alarmIndex);
                                        AlarmRow alarmRow = new AlarmRow(
                                                String.valueOf(this.alarmsTable.getItems().size() + 1),
                                                alarm.getName(),
                                                alarm.getExpression().getExpressionToEvaluate(),
                                                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()),
                                                alarm.getType(),
                                                alarm.isHighAlarmEnabled() ? String.valueOf(alarm.getHighLimit()) : "-",
                                                alarm.isHiHiAlarmEnabled() ? String.valueOf(alarm.getHiHiLimit()) : "-",
                                                alarm.isLowAlarmEnabled() ? String.valueOf(alarm.getLowLimit()) : "-",
                                                alarm.isLoloAlarmEnabled() ? String.valueOf(alarm.getLoloLimit()) : "-",
                                                alarm.getValue(),
                                                alarm.getStatus(),
                                                alarm.getAcknowledgement()
                                        );
                                        this.alarmsTable.getItems().add(alarmRow);
                                    }
                                } catch (SQLException | CompileException | IOException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            }

                        }), new KeyFrame(Duration.seconds(1)));
        this.updateTableTimeline.setCycleCount(Animation.INDEFINITE);
        this.updateTableTimeline.play();
    }

    @Override
    public void resize() {
        super.resize();
        this.alarmsTable.setPrefWidth(this.getCanvasObjectData().getWidth());
        this.alarmsTable.setPrefHeight(this.getCanvasObjectData().getHeight());
    }

    @Override
    public void setEnable(String enabled) {
        if (user.getRole().equals("Operador")) {
            enabled = "Stop";
        }
        switch (enabled) {
            case "Play":
                super.setEnable("Play");
                this.alarmsTable.setDisable(false);
                break;
            case "Stop":
                super.setEnable("Stop");
                this.alarmsTable.setDisable(true);
                break;
            default:
                super.setEnable("True");
                this.alarmsTable.setDisable(true);
                break;
        }
    }
}
