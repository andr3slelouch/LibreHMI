package andrade.luis.hmiethernetip.views;

import andrade.luis.hmiethernetip.HMIApp;
import andrade.luis.hmiethernetip.models.Alarm;
import andrade.luis.hmiethernetip.models.AlarmRow;
import andrade.luis.hmiethernetip.models.canvas.CanvasAlarmDisplay;
import andrade.luis.hmiethernetip.models.canvas.CanvasPoint;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

public class ManageAlarmsWindow extends Stage {
    Logger logger = Logger.getLogger(this.getClass().getName());
    private final CanvasAlarmDisplay canvasAlarmDisplay;

    public ArrayList<Alarm> getAlarmsList() {
        return alarmsList;
    }

    public void setAlarmsList(ArrayList<Alarm> alarmsList) {
        this.alarmsList = alarmsList;
    }

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    public void setHmiApp(HMIApp hmiApp) {
        this.hmiApp = hmiApp;
    }

    private HMIApp hmiApp;
    private ArrayList<Alarm> alarmsList = new ArrayList<>();

    public ManageAlarmsWindow(ArrayList<Alarm> alarmsList, HMIApp hmiApp) {
        this.alarmsList = alarmsList;
        this.hmiApp = hmiApp;
        StackPane root = new StackPane();
        final Label label = new Label("Seleccione la alarma a administrar");
        canvasAlarmDisplay= new CanvasAlarmDisplay(new CanvasPoint(0,0),false);
        canvasAlarmDisplay.setSelected(false);
        canvasAlarmDisplay.setTableItems(alarmsList);
        canvasAlarmDisplay.getAlarmsTable().setRowFactory(alarmsTableView -> new TableRow<>() {
            @Override
            protected void updateItem(AlarmRow item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                        ContextMenu contextMenu = new ContextMenu();

                        MenuItem newItem = new MenuItem();
                        newItem.setText("Nueva");
                        newItem.setOnAction(event -> {
                            SetAlarmWindow setAlarmWindow = new SetAlarmWindow(hmiApp);
                            createNewAlarm(setAlarmWindow);
                        });
                        MenuItem editItem = new MenuItem();
                        editItem.setText("Editar");
                        editItem.setOnAction(event -> {
                            int index = hmiApp.getIndexForAlarm(item.getName());
                            if(index>=0){
                                editAlarm(alarmsList.get(index));
                            }
                        });
                        MenuItem deleteItem = new MenuItem();
                        deleteItem.setText("Eliminar");
                        deleteItem.setOnAction(event -> {
                            if(showAlert(CONFIRMATION,"Confirmar eliminación","Desea eliminar la alarma seleccionada \"" + item.getName() + "\"?")){
                                deleteAlarm(item.getName());
                            }
                        });
                        contextMenu.getItems().addAll(newItem,editItem,deleteItem);
                        setContextMenu(contextMenu);

                } else {
                    setItem(null);
                }
            }
        });
        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, canvasAlarmDisplay);

        root.getChildren().add(vbox);
        Scene scene = new Scene(root,980,500);
        this.setScene(scene);
    }

    private void deleteAlarm(String name){
        ManageAlarmsWindow.this.canvasAlarmDisplay.removeTableItem(name);
        int index = hmiApp.getIndexForAlarm(name);
        alarmsList.remove(index);
    }

    private void editAlarm(Alarm alarm){
        SetAlarmWindow setAlarmWindow = new SetAlarmWindow(hmiApp);
        setAlarmWindow.getAlarmNameTF().setText(alarm.getName());
        setAlarmWindow.getAlarmCommentTF().setText(alarm.getComment());
        setAlarmWindow.setLocalExpression(alarm.getExpression());
        setAlarmWindow.setAddedTags(alarm.getExpression().getParameters());
        setAlarmWindow.getTextField().setText(alarm.getExpression().getExpressionToEvaluate());
        setAlarmWindow.getHighLimitCheckBox().setSelected(alarm.isHighAlarmEnabled());
        setAlarmWindow.getHiHiLimitCheckBox().setSelected(alarm.isHiHiAlarmEnabled());
        setAlarmWindow.getLowLimitCheckBox().setSelected(alarm.isLowAlarmEnabled());
        setAlarmWindow.getLoloLimitCheckBox().setSelected(alarm.isLoloAlarmEnabled());
        setAlarmWindow.getTrueRadioButton().setSelected(alarm.isCondition());
        setAlarmWindow.getFalseRadioButton().setSelected(!alarm.isCondition());
        setAlarmWindow.getHiHiLimitTF().setText(String.valueOf(alarm.getHiHiLimit()));
        setAlarmWindow.getHighLimitTF().setText(String.valueOf(alarm.getHighLimit()));
        setAlarmWindow.getLowLimitTF().setText(String.valueOf(alarm.getLowLimit()));
        setAlarmWindow.getLoloLimitTF().setText(String.valueOf(alarm.getLoloLimit()));
        setAlarmWindow.showAndWait();
        int index = hmiApp.getIndexForAlarm(alarm.getName());
        Alarm updatedAlarm = new Alarm(
                setAlarmWindow.getLocalExpression(),
                Double.parseDouble(setAlarmWindow.getHighLimitTF().getText()),
                Double.parseDouble(setAlarmWindow.getHiHiLimitTF().getText()),
                Double.parseDouble(setAlarmWindow.getLowLimitTF().getText()),
                Double.parseDouble(setAlarmWindow.getLoloLimitTF().getText()),
                setAlarmWindow.getHighLimitCheckBox().isSelected(),
                setAlarmWindow.getHiHiLimitCheckBox().isSelected(),
                setAlarmWindow.getLowLimitCheckBox().isSelected(),
                setAlarmWindow.getLoloLimitCheckBox().isSelected(),
                setAlarmWindow.getAlarmNameTF().getText(),
                setAlarmWindow.getAlarmCommentTF().getText()
        );
        if(index >= 0){
            alarmsList.set(index,updatedAlarm);
            ManageAlarmsWindow.this.canvasAlarmDisplay.updateTableItem(alarm.getName(),updatedAlarm);
            this.hmiApp.setWasModified(true);
        }
    }

    private void createNewAlarm(SetAlarmWindow setAlarmWindow){
        setAlarmWindow.showAndWait();
        if(hmiApp.getIndexForAlarm(setAlarmWindow.getAlarmNameTF().getText())==-1){
            if(setAlarmWindow.getLocalExpression().determineResultType().equals("Flotante") || setAlarmWindow.getLocalExpression().determineResultType().equals("Entero")){
                Alarm alarm = new Alarm(
                        setAlarmWindow.getLocalExpression(),
                        Double.parseDouble(setAlarmWindow.getHighLimitTF().getText()),
                        Double.parseDouble(setAlarmWindow.getHiHiLimitTF().getText()),
                        Double.parseDouble(setAlarmWindow.getLowLimitTF().getText()),
                        Double.parseDouble(setAlarmWindow.getLoloLimitTF().getText()),
                        setAlarmWindow.getHighLimitCheckBox().isSelected(),
                        setAlarmWindow.getHiHiLimitCheckBox().isSelected(),
                        setAlarmWindow.getLowLimitCheckBox().isSelected(),
                        setAlarmWindow.getLoloLimitCheckBox().isSelected(),
                        setAlarmWindow.getAlarmNameTF().getText(),
                        setAlarmWindow.getAlarmCommentTF().getText()
                );
                ManageAlarmsWindow.this.alarmsList.add(alarm);
                this.canvasAlarmDisplay.addTableItem(alarm);
                this.hmiApp.setWasModified(true);
            }else if(setAlarmWindow.getLocalExpression().determineResultType().equals("Bool")){
                Alarm alarm = new Alarm(
                        setAlarmWindow.getLocalExpression(),
                        setAlarmWindow.getHighLimitCheckBox().isSelected(),
                        setAlarmWindow.getTrueRadioButton().isSelected(),
                        setAlarmWindow.getAlarmNameTF().getText(),
                        setAlarmWindow.getAlarmCommentTF().getText()
                );
                ManageAlarmsWindow.this.alarmsList.add(alarm);
                this.canvasAlarmDisplay.addTableItem(alarm);
                this.hmiApp.setWasModified(true);
            }
        }else{
            showAlert(Alert.AlertType.ERROR,"El nombre de la nueva alarma ya existe","Ya existe una alarma con el nombre \"" +setAlarmWindow.getAlarmNameTF().getText()+ "\", cambia el nombre de la alarma");
            createNewAlarm(setAlarmWindow);
        }
    }

    private boolean showAlert(Alert.AlertType alertType, String title, String description) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(description);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(cancelButton,okButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == okButton) {
            alert.close();
            return true;
        } else if (result.isPresent() && result.get() == cancelButton) {
            alert.close();
            return false;
        }
        return false;
    }
}
