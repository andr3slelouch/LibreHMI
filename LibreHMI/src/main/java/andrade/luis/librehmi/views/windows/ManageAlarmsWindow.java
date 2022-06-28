package andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.HMIApp;
import andrade.luis.librehmi.models.Alarm;
import andrade.luis.librehmi.models.AlarmRow;
import andrade.luis.librehmi.views.canvas.CanvasAlarmDisplay;
import andrade.luis.librehmi.views.canvas.CanvasPoint;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.util.ArrayList;

import static andrade.luis.librehmi.util.Alerts.showAlert;
import static javafx.scene.control.Alert.AlertType.CONFIRMATION;

/**
 * Ventana de administración de alarmas
 */
public class ManageAlarmsWindow extends Stage {
    private final CanvasAlarmDisplay canvasAlarmDisplay;

    public ArrayList<Alarm> getAlarmsList() {
        return alarmsList;
    }

    public HMIApp getHmiApp() {
        return hmiApp;
    }

    private final HMIApp hmiApp;
    private final ArrayList<Alarm> alarmsList;

    /**
     * Constructor de la ventana de administración de alarmas
     * @param alarmsList Lista de alarmas a administrarse
     * @param hmiApp Clase principal de la aplicación
     */
    public ManageAlarmsWindow(ArrayList<Alarm> alarmsList, HMIApp hmiApp) {
        this.alarmsList = alarmsList;
        this.hmiApp = hmiApp;
        StackPane root = new StackPane();
        setTitle("Seleccione alarma a ser administrada");
        canvasAlarmDisplay= new CanvasAlarmDisplay(new CanvasPoint(0,0),false);
        canvasAlarmDisplay.setSelected(false);
        canvasAlarmDisplay.setTableItems(alarmsList);
        canvasAlarmDisplay.getAlarmsTable().setRowFactory(alarmsTableView -> new TableRow<>() {
            @Override
            protected void updateItem(AlarmRow item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                        ContextMenu contextMenu = new ContextMenu();
                        MenuItem newItem = createNewMenuIteM();
                        MenuItem editItem = createEditMenuItem(item);
                        MenuItem deleteItem = createDeleteMenuItem(item);
                        contextMenu.getItems().addAll(newItem,editItem,deleteItem);
                        setContextMenu(contextMenu);

                } else {
                    setItem(null);
                }
            }
        });

        root.getChildren().add(canvasAlarmDisplay);
        Scene scene = new Scene(root,1080,400);
        this.setScene(scene);
    }

    /**
     * Permite crear el menu de crear nueva alarma
     * @return MenuItem con el comportamiento de creación de una nueva alarma
     */
    private MenuItem createNewMenuIteM(){
        MenuItem newItem = new MenuItem();
        newItem.setText("Nueva");
        newItem.setOnAction(event -> {
            SetAlarmWindow setAlarmWindow = new SetAlarmWindow(hmiApp);
            createNewAlarm(setAlarmWindow);
        });
        return newItem;
    }

    /**
     * Permite crear el menu de editar una alarma
     * @param item Fila de alarma de la tabla a ser editada
     * @return MenuItem con el comportamiento de edición
     */
    private MenuItem createEditMenuItem(AlarmRow item){
        MenuItem editItem = new MenuItem();
        editItem.setText("Editar");
        editItem.setOnAction(event -> {
            int index = hmiApp.getIndexForAlarm(item.getName());
            if(index>=0){
                editAlarm(alarmsList.get(index));
            }
        });
        return editItem;
    }

    /**
     * Permite crear el menú de eliminación de alarma
     * @param item Fila de alarma de la tabla a ser eliminada
     * @return MenuItem con el comportamiento de eliminación
     */
    private MenuItem createDeleteMenuItem(AlarmRow item){
        MenuItem deleteItem = new MenuItem();
        deleteItem.setText("Eliminar");
        deleteItem.setOnAction(event -> {
            if(showAlert(CONFIRMATION,"Confirmar eliminación","Desea eliminar la alarma seleccionada \"" + item.getName() + "\"?","")){
                deleteAlarm(item.getName());
            }
        });
        return deleteItem;
    }

    /**
     * Permite eliminar una alarma del proyecto
     * @param name Nombre de la alarma a ser eliminada
     */
    private void deleteAlarm(String name){
        ManageAlarmsWindow.this.canvasAlarmDisplay.removeTableItem(name);
        int index = hmiApp.getIndexForAlarm(name);
        alarmsList.remove(index);
    }

    /**
     * Permite editar una alarma del proyecto
     * @param alarm Alarma a ser editada
     */
    private void editAlarm(Alarm alarm){
        SetAlarmWindow setAlarmWindow = new SetAlarmWindow(hmiApp);
        setAlarmWindow.setTitle("Editar Alarma");
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
                setAlarmWindow.getAlarmNameTF().getText(),
                setAlarmWindow.getAlarmCommentTF().getText()
        );
        updatedAlarm.setHighLimit(Double.parseDouble(setAlarmWindow.getHighLimitTF().getText()),setAlarmWindow.getHighLimitCheckBox().isSelected());
        updatedAlarm.setHiHiLimit(Double.parseDouble(setAlarmWindow.getHiHiLimitTF().getText()),setAlarmWindow.getHiHiLimitCheckBox().isSelected());
        updatedAlarm.setLowLimit(Double.parseDouble(setAlarmWindow.getLowLimitTF().getText()),setAlarmWindow.getLowLimitCheckBox().isSelected());
        updatedAlarm.setLoloLimit(Double.parseDouble(setAlarmWindow.getLoloLimitTF().getText()),setAlarmWindow.getLoloLimitCheckBox().isSelected());
        if(index >= 0){
            alarmsList.set(index,updatedAlarm);
            ManageAlarmsWindow.this.canvasAlarmDisplay.updateTableItem(alarm.getName(),updatedAlarm);
            this.hmiApp.setWasModified(true);
        }
    }

    /**
     * Permite crear una nueva alarma
     * @param setAlarmWindow Ventana de definición de alarmas
     */
    private void createNewAlarm(SetAlarmWindow setAlarmWindow){
        setAlarmWindow.showAndWait();
        if(hmiApp.getIndexForAlarm(setAlarmWindow.getAlarmNameTF().getText())==-1){
            if(setAlarmWindow.getLocalExpression().determineResultType().equals("Flotante") || setAlarmWindow.getLocalExpression().determineResultType().equals("Entero")){
                Alarm alarm = new Alarm(
                        setAlarmWindow.getLocalExpression(),
                        setAlarmWindow.getAlarmNameTF().getText(),
                        setAlarmWindow.getAlarmCommentTF().getText()
                );
                alarm.setHighLimit(Double.parseDouble(setAlarmWindow.getHighLimitTF().getText()),setAlarmWindow.getHighLimitCheckBox().isSelected());
                alarm.setHiHiLimit(Double.parseDouble(setAlarmWindow.getHiHiLimitTF().getText()),setAlarmWindow.getHiHiLimitCheckBox().isSelected());
                alarm.setLowLimit(Double.parseDouble(setAlarmWindow.getLowLimitTF().getText()),setAlarmWindow.getLowLimitCheckBox().isSelected());
                alarm.setLoloLimit(Double.parseDouble(setAlarmWindow.getLoloLimitTF().getText()),setAlarmWindow.getLoloLimitCheckBox().isSelected());
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
            showAlert(Alert.AlertType.ERROR,"El nombre de la nueva alarma ya existe","Ya existe una alarma con el nombre \"" +setAlarmWindow.getAlarmNameTF().getText()+ "\", cambia el nombre de la alarma","");
            createNewAlarm(setAlarmWindow);
        }
    }
}
