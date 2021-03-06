package andrade.luis.librehmi.views.canvas;

import andrade.luis.librehmi.models.CanvasObjectData;
import andrade.luis.librehmi.views.DateHBox;
import andrade.luis.librehmi.models.Tag;
import andrade.luis.librehmi.models.TrendChartSerieData;
import andrade.luis.librehmi.views.windows.SetTrendChartPropertiesWindow;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import com.opencsv.CSVWriter;
import java.util.logging.Level;

/**
 * Clase que define el objeto CanvasTrendChart, que permitirá mostrar un gráfico de tendencias con base en los tags que
 * se asocien a la representación
 */
public class CanvasTrendChart extends CanvasObject {
    private DateTimeFormatter dtf;
    private Slider startRangeSlider;
    private Slider endRangeSlider;
    private static final String DISPLAY_FORMAT = "EEEE, HH:mm:ss";
    private static final String FULL_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private DateHBox startRangeHBox;
    private DateHBox endRangeHBox;
    private final ArrayList<XYChart.Series<String, Number>> lineChartSeries = new ArrayList<>();
    private final ArrayList<XYChart.Series<LocalDateTime, Number>> lineChartSeriesToExport = new ArrayList<>();
    /**
     * Zoom in: <a href="https://www.flaticon.com/free-icons/zoom-out" title="zoom out icons">Zoom out icons created by Freepik - Flaticon</a>
     */
    Image zoomInIcon = new Image(this.getClass().getResource("zoom-in.png").toExternalForm());
    /**
     * Zoom out: <a href="https://www.flaticon.com/free-icons/zoom-out" title="zoom out icons">Zoom out icons created by Freepik - Flaticon</a>
     */
    Image zoomOutIcon = new Image(this.getClass().getResource("zoom-out.png").toExternalForm());
    /**
     * Reload : <a href="https://www.flaticon.com/free-icons/refresh" title="refresh icons">Refresh icons created by Gregor Cresnar - Flaticon</a>
     */
    Image reloadIcon = new Image(this.getClass().getResource("reload.png").toExternalForm());
    /**
     * CSV : <a href="https://www.flaticon.com/free-icons/csv" title="csv icons">Csv icons created by Freepik - Flaticon</a>
     */
    Image csvIcon = new Image(this.getClass().getResource("csv.png").toExternalForm());
    private LineChart<String, Number> lineChart;
    private LocalDateTime lastUpdatedTime;
    private LocalDateTime sliderLocalDateTime;
    private boolean zoomedIn;

    public Timeline getTrendTimeline() {
        return trendTimeline;
    }

    private Timeline trendTimeline;
    private ArrayList<TrendChartSerieData> trendChartSerieDataArrayList;
    private final ArrayList<CheckBox> enabledExpressionCheckBoxes = new ArrayList<>();
    private final ArrayList<String> showingExpressions = new ArrayList<>();

    /**
     * Constructor que permite agregar un nuevo CanvasTrendChart al canvas
     * @param positionCanvasPoint Posición del objeto en el canvas
     * @param trendChartSerieDataArrayList ArrayList de series de datos a ser graficados
     * @param width Ancho de la representación
     * @param height Alto de la representación
     * @param rotation Valor para rotar la representación
     */
    public CanvasTrendChart(CanvasPoint positionCanvasPoint, ArrayList<TrendChartSerieData> trendChartSerieDataArrayList, double width, double height,double rotation) {
        super(positionCanvasPoint);
        setData(trendChartSerieDataArrayList,width,height,rotation);
    }

    /**
     * Constructor para pegar un CanvasTrendChart copiado o regenerarlo desde el archivo
     * @param canvasObjectData CanvasObjectData conteniendo la información del objeto a generar
     */
    public CanvasTrendChart(CanvasObjectData canvasObjectData){
        super(canvasObjectData);
        ArrayList<TrendChartSerieData> trendChartSerieDataArrayListLocal = new ArrayList<>(Arrays.asList(this.getCanvasObjectData().getTrendChartSerieDataArr()));
        setData(trendChartSerieDataArrayListLocal, getCanvasObjectData().getWidth(),getCanvasObjectData().getHeight(),getCanvasObjectData().getRotation());
    }

    /**
     * Permite definir los datos de la representación
     * @param trendChartSerieDataArrayList ArrayList de series de datos a ser graficados
     * @param width Ancho de la representación
     * @param height Alto de la representación
     * @param rotation Valor para rotar la representación
     */
    public void setData(ArrayList<TrendChartSerieData> trendChartSerieDataArrayList, double width, double height, double rotation){

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tiempo");


        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Datos");

        //Creating the line chart
        this.lineChart = new LineChart<>(xAxis, yAxis);
        this.getCanvasObjectData().setType("TrendChart");
        this.getCanvasObjectData().setDataType("Gráfico de Tendencias");
        this.getCanvasObjectData().setSuperType("TagOutputObject");

        this.trendChartSerieDataArrayList = trendChartSerieDataArrayList;
        TrendChartSerieData[] trendChartSerieDataArr = new TrendChartSerieData[8];
        for(int i=0;i<this.trendChartSerieDataArrayList.size();i++){
            trendChartSerieDataArr[i] = this.trendChartSerieDataArrayList.get(i);
        }
        this.getCanvasObjectData().setTrendChartSerieDataArr(trendChartSerieDataArr);

        this.lineChart.getData().clear();
        this.lineChartSeries.clear();
        //Prepare XYChart.Series objects by setting data
        initTrendChartSeries();
        lineChart.getData().addAll(lineChartSeries);
        updateLineChartColors();


        this.lastUpdatedTime = LocalDateTime.now();
        this.lastUpdatedTime = this.lastUpdatedTime.minusSeconds(86400);
        this.sliderLocalDateTime = LocalDateTime.now().minusSeconds(86400);
        Locale spanishLocale = new Locale("es", "ES");
        this.dtf = DateTimeFormatter.ofPattern(DISPLAY_FORMAT, spanishLocale);

        lineChart.setTitle("Datos en el Tiempo");
        lineChart.setCreateSymbols(false);
        lineChart.setLegendVisible(false);
        this.getCanvasObjectData().setSamplingTime(1);
        this.startRangeHBox = new DateHBox();
        this.startRangeSlider = new Slider(0, 90000, 0) {
            Text text;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (text == null) {
                    AtomicReference<LocalDateTime> localSliderDateTime = new AtomicReference<>(CanvasTrendChart.this.sliderLocalDateTime.plusSeconds((long) getValue()));
                    text = new Text(convertSliderValueToString(CanvasTrendChart.this.sliderLocalDateTime, getValue()));
                    startRangeHBox.setDateTime(localSliderDateTime.get());
                    valueProperty().addListener((obs, old, val) -> {
                        localSliderDateTime.set(CanvasTrendChart.this.sliderLocalDateTime.plusSeconds((long) getValue()));
                        text.setText(convertSliderValueToString(CanvasTrendChart.this.sliderLocalDateTime, getValue()));
                        startRangeHBox.setDateTime(localSliderDateTime.get());
                    });
                }
            }
        };
        this.getCanvasObjectData().setWidth(width);
        this.getCanvasObjectData().setHeight(height);
        this.setPrefWidth(width);
        this.setPrefHeight(height);
        this.getCanvasObjectData().setRotation(rotation);
        this.setRotate(rotation);

        this.startRangeSlider.setOrientation(Orientation.HORIZONTAL);
        this.startRangeHBox.getChildren().add(startRangeSlider);
        this.startRangeHBox.attachSlider(this.startRangeSlider, this.sliderLocalDateTime);
        this.startRangeHBox.setAlignment(Pos.CENTER);
        this.endRangeHBox = new DateHBox();
        this.endRangeSlider = new Slider(0, 90000, 90000) {
            Text text;

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                if (text == null) {
                    AtomicReference<LocalDateTime> localSliderDateTime = new AtomicReference<>(CanvasTrendChart.this.sliderLocalDateTime.plusSeconds((long) getValue()));
                    text = new Text(convertSliderValueToString(CanvasTrendChart.this.sliderLocalDateTime, getValue()));
                    endRangeHBox.setDateTime(localSliderDateTime.get());
                    valueProperty().addListener((obs, old, val) -> {
                        localSliderDateTime.set(CanvasTrendChart.this.sliderLocalDateTime.plusSeconds((long) getValue()));
                        text.setText(convertSliderValueToString(CanvasTrendChart.this.sliderLocalDateTime, getValue()));
                        endRangeHBox.setDateTime(localSliderDateTime.get());
                    });
                }
            }
        };
        this.endRangeSlider.setOrientation(Orientation.HORIZONTAL);
        this.endRangeHBox.getChildren().add(endRangeSlider);
        this.endRangeHBox.attachSlider(this.endRangeSlider, this.sliderLocalDateTime);
        this.endRangeHBox.setAlignment(Pos.CENTER);

        Button zoomInButton = new Button("Acercar");
        zoomInButton.setGraphic(new ImageView(zoomInIcon));
        Button zoomOutButton = new Button("Alejar");
        zoomOutButton.setGraphic(new ImageView(zoomOutIcon));
        zoomOutButton.setDisable(true);
        Button reloadButton = new Button("Actualizar");
        reloadButton.setGraphic(new ImageView(reloadIcon));
        reloadButton.setOnAction(actionEvent -> {
            this.zoomedIn = true;
            zoomOutOperation();
        });
        zoomInButton.setOnAction(actionEvent -> {
            reloadButton.setDisable(true);
            zoomOutButton.setDisable(false);
            zoomInOperation();
        });
        zoomOutButton.setOnAction(actionEvent -> {
            reloadButton.setDisable(false);
            zoomOutOperation();
            zoomOutButton.setDisable(true);
        });
        Button exportButton = new Button("Exportar");
        exportButton.setGraphic(new ImageView(csvIcon));
        exportButton.setOnAction(actionEvent -> exportDataToCSV());
        HBox buttonsHBox = new HBox();
        buttonsHBox.setAlignment(Pos.CENTER);
        buttonsHBox.getChildren().addAll(zoomInButton, zoomOutButton, reloadButton,exportButton);
        HBox enabledExpressionsHBox = new HBox();
        enabledExpressionsHBox.setAlignment(Pos.CENTER);
        enabledExpressionCheckBoxes.clear();
        for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
            if(trendChartSerieData!=null){
                CheckBox expressionCheckBox = new CheckBox(trendChartSerieData.getSerieDataName());
                expressionCheckBox.setGraphic(new Circle(0, 0, 5, trendChartSerieData.getColor().getColor()));
                expressionCheckBox.setSelected(true);
                expressionCheckBox.selectedProperty().addListener((observableValue, oldBoolean, newBoolean) -> updateSeries());
                this.showingExpressions.add(trendChartSerieData.getSerieDataName());
                enabledExpressionCheckBoxes.add(expressionCheckBox);
            }
        }
        enabledExpressionsHBox.getChildren().addAll(enabledExpressionCheckBoxes);
        enabledExpressionsHBox.setSpacing(5);
        VBox downContent = new VBox(enabledExpressionsHBox, startRangeHBox, endRangeHBox, buttonsHBox);
        downContent.setAlignment(Pos.CENTER);
        downContent.setSpacing(5);

        this.trendChartSerieDataArrayList = trendChartSerieDataArrayList;
        TrendChartSerieData[] data = new TrendChartSerieData[8];
        for (int i = 0; i < trendChartSerieDataArrayList.size(); i++) {
            data[i] = trendChartSerieDataArrayList.get(i);
        }
        this.getCanvasObjectData().setTrendChartSerieDataArr(data);
        this.setCenter(lineChart);
        this.setBottom(downContent);
    }

    /**
     * Permite inicializar las series del gráfico de tendencias
     */
    private void initTrendChartSeries(){
        for (TrendChartSerieData data : trendChartSerieDataArrayList) {
            if(data!=null){
                XYChart.Series<String, Number> serie = new XYChart.Series<>();
                if(data.getSerieDataName()!=null){
                    serie.setName(data.getSerieDataName());
                }
                this.lineChartSeries.add(serie);
            }
        }
        for (TrendChartSerieData data : trendChartSerieDataArrayList) {
            if(data!=null){
                XYChart.Series<LocalDateTime, Number> serie = new XYChart.Series<>();
                if(data.getSerieDataName()!=null){
                    serie.setName(data.getSerieDataName());
                }
                this.lineChartSeriesToExport.add(serie);
            }
        }
    }

    /**
     * Permite exportar los datos recopilados dentro de las series haca un CSV
     */
    private void exportDataToCSV() {
        List<String[]> dataToExport = new ArrayList<>();
        String[] header = new String[lineChartSeriesToExport.size()+1];
        dataToExport.add(header);
        header[0] = "Fecha y Hora";
        Locale spanishLocale = new Locale("es", "ES");
        DateTimeFormatter localDtf = DateTimeFormatter.ofPattern(FULL_FORMAT, spanishLocale);
        for(int i=0;i<lineChartSeriesToExport.size();i++){
            header[i+1] = lineChartSeriesToExport.get(i).getName();
        }
        int maximumRows = lineChartSeriesToExport.get(0).getData().size();
        for(int i=0;i<maximumRows;i++){
            String[] row = new String[lineChartSeriesToExport.size()+1];
            for(int j=0;j<lineChartSeriesToExport.size();j++){
                if(j==0){
                    row[0] = localDtf.format(lineChartSeriesToExport.get(j).getData().get(i).getXValue());
                    row[1] = String.valueOf(lineChartSeriesToExport.get(j).getData().get(i).getYValue());
                }else{
                    row[j+1] = String.valueOf(lineChartSeriesToExport.get(j).getData().get(i).getYValue());
                }
            }
            dataToExport.add(row);
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV", "*.csv", "*.CSV")
        );
        fileChooser.setTitle("Exportar Datos");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try (CSVWriter writer = new CSVWriter(new FileWriter(file.getAbsolutePath()))) {
                writer.writeAll(dataToExport);
            } catch (IOException e) {
                logger.log(Level.INFO,e.getMessage());
            }
        }

    }

    /**
     * Permite mostrar la ventana de propiedades del objeto
     */
    @Override
    public void setProperties(){
        SetTrendChartPropertiesWindow setTrendChartPropertiesWindow = new SetTrendChartPropertiesWindow(750,475,getHmiApp().getLocalTags());
        setTrendChartPropertiesWindow.getRotationHBox().getRotationTextField().setText(String.valueOf(this.getCanvasObjectData().getRotation()));
        setTrendChartPropertiesWindow.getSizeVBox().getWidthField().setText(String.valueOf(this.getCanvasObjectData().getWidth()));
        setTrendChartPropertiesWindow.getSizeVBox().getHeightField().setText(String.valueOf(this.getCanvasObjectData().getHeight()));
        setTrendChartPropertiesWindow.getSamplingTimeTF().setText(String.valueOf(this.getCanvasObjectData().getSamplingTime()));
        for(int i=0;i<trendChartSerieDataArrayList.size();i++){
            if(trendChartSerieDataArrayList.get(i)!=null){
                setTrendChartPropertiesWindow.getExpressionHBoxes().get(i).setExpression(trendChartSerieDataArrayList.get(i).getExpression());
                setTrendChartPropertiesWindow.getExpressionHBoxes().get(i).getExpressionNameTF().setText(trendChartSerieDataArrayList.get(i).getSerieDataName());
                setTrendChartPropertiesWindow.getExpressionHBoxes().get(i).getExpressionTF().setText(trendChartSerieDataArrayList.get(i).getExpression().getExpressionToEvaluate());
                setTrendChartPropertiesWindow.getExpressionHBoxes().get(i).getExpressionColorPicker().setValue(trendChartSerieDataArrayList.get(i).getColor().getColor());
                setTrendChartPropertiesWindow.getExpressionHBoxes().get(i).getEnableChartExpression().setSelected(true);
            }
        }
        setTrendChartPropertiesWindow.showAndWait();
        if(!setTrendChartPropertiesWindow.isCanceled()){
            this.setData(
                    setTrendChartPropertiesWindow.getTrendChartSerieDataArrayList(),
                    Double.parseDouble(setTrendChartPropertiesWindow.getSizeVBox().getWidthField().getText()),
                    Double.parseDouble(setTrendChartPropertiesWindow.getSizeVBox().getHeightField().getText()),
                    Double.parseDouble(setTrendChartPropertiesWindow.getRotationHBox().getRotationTextField().getText())
            );
            this.getCanvasObjectData().setSamplingTime(Double.parseDouble(setTrendChartPropertiesWindow.getSamplingTimeTF().getText()));
            if(this.trendTimeline.getStatus().equals(Animation.Status.RUNNING)){
                this.trendTimeline.stop();
            }
            this.setTrendTimeline();
            this.trendTimeline.play();
        }
    }

    /**
     * Permite realizar la operación de acercamiento
     */
    public void zoomInOperation() {
        this.zoomedIn = true;
        double sliderRange = getSliderRangeValue(startRangeHBox.getLocalDateTime(), endRangeHBox.getLocalDateTime(), this.getCanvasObjectData().getSamplingTime());
        updateSliders(startRangeHBox.getLocalDateTime(), sliderRange, true);
    }

    /**
     * Permite realizar la operación de alejamiento
     */
    public void zoomOutOperation() {
        if (this.zoomedIn) {
            this.zoomedIn = false;
            updateSliders(LocalDateTime.now().minusSeconds(86400), 90000, false);
        }
    }

    /**
     * Permite actualizar los colores de las líneas de tendencias del gráfico
     */
    public void updateLineChartColors() {
        for (int i = 0; i < lineChart.getData().size(); i++) {
            XYChart.Series<String, Number> serie = lineChart.getData().get(i);
            String colorString = "";
            for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                if (trendChartSerieData != null && serie.getName().equals(trendChartSerieData.getSerieDataName())) {
                    colorString = trendChartSerieData.getColor().toHexString();
                }
            }
            serie.getNode().setStyle("-fx-stroke: " + colorString + ";");
        }
    }

    /**
     * Permite actualizar las series asociadas al gráfico
     */
    public void updateSeries() {
        ArrayList<XYChart.Series<String, Number>> seriesToDelete = new ArrayList<>();
        ArrayList<XYChart.Series<String, Number>> seriesToAdd = new ArrayList<>();
        this.showingExpressions.clear();
        for (CheckBox enabledExpressionCheckBox : enabledExpressionCheckBoxes) {
            if (!enabledExpressionCheckBox.isSelected()) {
                this.showingExpressions.remove(enabledExpressionCheckBox.getText());
                int index = getIndexForSerieInArrayList(new ArrayList<>(this.lineChart.getData()), enabledExpressionCheckBox.getText());
                if (index > -1) seriesToDelete.add(this.lineChart.getData().get(index));
            } else {
                this.showingExpressions.add(enabledExpressionCheckBox.getText());
                int index = getIndexForSerieInArrayList(this.lineChartSeries, enabledExpressionCheckBox.getText());
                if (index > -1) seriesToAdd.add(this.lineChartSeries.get(index));
            }
        }
        this.lineChart.getData().removeAll(seriesToDelete);
        for (XYChart.Series<String, Number> stringNumberSeries : seriesToAdd) {
            if (!this.lineChart.getData().contains(stringNumberSeries)) {
                this.lineChart.getData().add(stringNumberSeries);
            }
            updateLineChartColors();
        }
        if (this.zoomedIn) {
            zoomInOperation();
        }
    }

    /**
     * Permite obtener el índice de una serie dentro del arrayList a partir de su nombre
     * @param arrayList ArrayList de series de datos mostrados en el gráfico
     * @param name Nombre de la serie de datos a buscarse
     * @return Índice retornado de la serie especificada
     */
    public int getIndexForSerieInArrayList(ArrayList<XYChart.Series<String, Number>> arrayList, String name) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Permite actualizar los valores dentro de los sliders en las operaciones de Zoom In y Zoom Out
     * @param sliderDateTime LocalDateTime del nuevo tiempo a ser definido
     * @param sliderRange Cantidad para ser mostrada dentro del slider
     * @param isFilterEnabled Bandera definiendo si se ha aplicado el filtro de zoom
     */
    public void updateSliders(LocalDateTime sliderDateTime, double sliderRange, boolean isFilterEnabled) {
        this.sliderLocalDateTime = sliderDateTime;
        this.startRangeSlider.setMin(0.0);
        this.startRangeSlider.setMax(sliderRange);
        this.startRangeSlider.setValue(0.0);
        this.endRangeSlider.setMin(0.0);
        this.endRangeSlider.setMax(sliderRange);
        this.endRangeSlider.setValue(sliderRange);
        if (isFilterEnabled) {
            LocalDateTime startD = sliderLocalDateTime.plusSeconds(((Double)startRangeSlider.getValue()).longValue());
            LocalDateTime endD = sliderLocalDateTime.plusSeconds(((Double)endRangeSlider.getValue()).longValue());
            this.lineChart.getData().clear();
            for (XYChart.Series<LocalDateTime, Number> lineChartSerie : this.lineChartSeriesToExport) {
                XYChart.Series<String, Number> filteredSerie = filterSerie(startD, endD, lineChartSerie);
                if (showingExpressions.contains(filteredSerie.getName())) {
                    this.lineChart.getData().add(filteredSerie);
                }
            }
        } else {
            this.startRangeHBox.setDateTime(sliderDateTime);
            this.endRangeHBox.setDateTime(sliderDateTime.plusSeconds(90000));
            this.lineChart.getData().clear();
            for (XYChart.Series<String, Number> lineChartSerie : this.lineChartSeries) {
                if (showingExpressions.contains(lineChartSerie.getName()) && !this.lineChart.getData().contains(lineChartSerie)) {
                    this.lineChart.getData().add(lineChartSerie);
                }
            }
        }
        updateLineChartColors();
    }

    /**
     * Permite filtrar una serie del gráfico de tendencias
     * @param start LocalDateTime de inicio del filtro
     * @param end LocalDateTime de fin de filtro
     * @param serie Serie a la cual se aplicará el filtro
     * @return Serie filtrada
     */
    public XYChart.Series<String, Number> filterSerie(LocalDateTime start, LocalDateTime end, XYChart.Series<LocalDateTime, Number> serie) {
        XYChart.Series<String, Number> filteredSerie = new XYChart.Series<>();
        for (int i = 0; i < serie.getData().size(); i++) {
            LocalDateTime serieDateTime = serie.getData().get(i).getXValue();
            if (serieDateTime.isAfter(start) && serieDateTime.isBefore(end)) {
                filteredSerie.getData().add(new LineChart.Data<>(dtf.format(serieDateTime), serie.getData().get(i).getYValue()));
            }
        }
        filteredSerie.setName(serie.getName());
        return filteredSerie;
    }

    /**
     * Permite convertir un valor de fecha y hora desde el valor del slider a un String de fecha y hora
     * @param currentLocalDateTime LocalDateTime actual del gráfico de tendencias
     * @param value Valor desde el Slider
     * @return Valor de fecha y hora en string
     */
    public String convertSliderValueToString(LocalDateTime currentLocalDateTime, Double value) {
        LocalDateTime sliderDateTime = currentLocalDateTime.plusSeconds(value.longValue());
        return dtf.format(sliderDateTime);
    }

    /**
     * Permite obtener el rango para el slider con base a un LocalDateTime de inicio y de fin
     * @param start LocalDateTime de inicio
     * @param end LocalDateTime de fin
     * @param samplingTime Tiempo de sampleo
     * @return Rango para el slider
     */
    public double getSliderRangeValue(LocalDateTime start, LocalDateTime end, double samplingTime) {
        double rangeSeconds = (int) ChronoUnit.SECONDS.between(start, end);
        return rangeSeconds / samplingTime;
    }

    /**
     * Permite definir el hilo para graficar las series en el gráfico de tendencias
     */
    public void setTrendTimeline() {
        this.trendTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            LocalDateTime now = LocalDateTime.now();
                            this.lastUpdatedTime = now;
                            for (int i=0;i<lineChartSeries.size();i++){
                                for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                                    updateTrendChartSerieData(trendChartSerieData, i, now);
                                }
                            }
                        }), new KeyFrame(Duration.seconds(this.getCanvasObjectData().getSamplingTime())));
        this.trendTimeline.setCycleCount(Animation.INDEFINITE);
        this.trendTimeline.play();
    }

    /**
     * Permite actualizar una serie del gráfico de tendencias
     * @param trendChartSerieData Serie para actualizar sus valores
     * @param index Índice de la serie a actualizarse
     * @param now LocalDateTime del momento de la actualización
     */
    private void updateTrendChartSerieData(TrendChartSerieData trendChartSerieData, int index, LocalDateTime now){
        if (trendChartSerieData != null && lineChartSeries.get(index).getName().equals(trendChartSerieData.getSerieDataName())) {
            try {
                if(lineChartSeries.get(index).getData().size()>31){
                    lineChartSeries.get(index).getData().remove(0);
                }
                lineChartSeries.get(index).getData().add(new LineChart.Data<>(dtf.format(now), (double) trendChartSerieData.getExpression().evaluate()));
                lineChartSeriesToExport.get(index).getData().add(new LineChart.Data<>(now, (double) trendChartSerieData.getExpression().evaluate()));
            } catch (CompileException | InvocationTargetException | SQLException |
                     IOException e) {
                logger.log(Level.INFO, e.getMessage());
            }
        }
    }
    @Override
    public void updateTag(Tag tag){
        super.updateTag(tag);
        if(trendTimeline != null){
            for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                ArrayList<Tag> parameters = trendChartSerieData.getExpression().getParameters();
                for (int j = 0; j < parameters.size(); j++) {
                    if (parameters.get(j).compareToTag(tag)) {
                        int floatPrecision = parameters.get(j).getFloatPrecision();
                        tag.setFloatPrecision(floatPrecision);
                        parameters.set(j, tag);
                    }
                }
                trendChartSerieData.getExpression().setParameters(parameters);
            }
        }
    }
}
