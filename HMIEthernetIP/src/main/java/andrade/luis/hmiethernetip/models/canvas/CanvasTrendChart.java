package andrade.luis.hmiethernetip.models.canvas;

import andrade.luis.hmiethernetip.models.DateHBox;
import andrade.luis.hmiethernetip.models.Expression;
import andrade.luis.hmiethernetip.models.TrendChartSerieData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.codehaus.commons.compiler.CompileException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class CanvasTrendChart extends CanvasObject {
    private final DateTimeFormatter dtf;
    private final Slider startRangeSlider;
    private final Slider endRangeSlider;
    private static final String DISPLAY_FORMAT = "EEEE, HH:mm:ss";
    private static final String FULL_FORMAT = "yyyy/MM/dd HH:mm:ss";
    private final DateHBox startRangeHBox;
    private final DateHBox endRangeHBox;
    private boolean isFilterEnabled = false;
    private ArrayList<XYChart.Series<String, Number>> lineChartSeries = new ArrayList<>();
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
    private LineChart<String, Number> lineChart;
    private LocalDateTime lastUpdatedTime;
    private LocalDateTime sliderLocalDateTime;
    private boolean zoomedIn;

    public Timeline getTrendTimeline() {
        return trendTimeline;
    }

    public void setTrendTimeline(Timeline trendTimeline) {
        this.trendTimeline = trendTimeline;
    }

    private Timeline trendTimeline;
    private ArrayList<TrendChartSerieData> trendChartSerieDataArrayList;

    public CanvasTrendChart(CanvasPoint positionCanvasPoint, ArrayList<TrendChartSerieData> trendChartSerieDataArrayList) {
        super(positionCanvasPoint);
        //Defining the x axis
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tiempo");

        //Defining the y axis
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Datos");

        //Creating the line chart
        this.lineChart = new LineChart<>(xAxis, yAxis);

        //Prepare XYChart.Series objects by setting data
        for (TrendChartSerieData data : trendChartSerieDataArrayList) {
            XYChart.Series<String, Number> serie = new XYChart.Series<>();
            serie.setName(data.getSerieDataName());
            this.lineChartSeries.add(serie);
        }
        lineChart.getData().addAll(lineChartSeries);
        //Setting the data to Line chart
        for (int i = 0; i < lineChart.getData().size(); i++) {
            XYChart.Series<String, Number> serie = lineChart.getData().get(i);
            String colorString = "";
            for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                if (serie.getName().equals(trendChartSerieData.getSerieDataName())) {
                    colorString = trendChartSerieData.getColor().toHexString();
                }
            }
            serie.getNode().setStyle("-fx-stroke: " + colorString + ";");
        }
        for (Node node : lineChart.lookupAll("Label.chart-legend-item")) {
            String colorString = "";
            Label label = (Label) node;
            for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                if (label.getText().equals(trendChartSerieData.getSerieDataName())) {
                    colorString = trendChartSerieData.getColor().toHexString();
                }
            }
            label.getGraphic().setStyle("-fx-background-color: " + colorString + ", white");
        }


        this.lastUpdatedTime = LocalDateTime.now();
        this.lastUpdatedTime = this.lastUpdatedTime.minusSeconds(86400);
        this.sliderLocalDateTime = LocalDateTime.now().minusSeconds(86400);
        Locale spanishLocale = new Locale("es", "ES");
        this.dtf = DateTimeFormatter.ofPattern(DISPLAY_FORMAT, spanishLocale);

        lineChart.setTitle("Datos en el Tiempo");
        lineChart.setCreateSymbols(false);
        //lineChart.setLegendVisible(false);
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
        this.startRangeSlider.setOrientation(Orientation.HORIZONTAL);
        this.startRangeHBox.getChildren().add(startRangeSlider);
        this.startRangeHBox.attachSlider(this.startRangeSlider, this.sliderLocalDateTime);
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
        HBox buttonsHBox = new HBox();
        buttonsHBox.setAlignment(Pos.CENTER);
        buttonsHBox.getChildren().addAll(zoomInButton, zoomOutButton, reloadButton);
        VBox downContent = new VBox(startRangeHBox, endRangeHBox, buttonsHBox);
        downContent.setAlignment(Pos.CENTER);

        this.trendChartSerieDataArrayList = trendChartSerieDataArrayList;
        TrendChartSerieData[] data = new TrendChartSerieData[8];
        for (int i = 0; i < trendChartSerieDataArrayList.size(); i++) {
            data[i] = trendChartSerieDataArrayList.get(i);
        }
        this.getCanvasObjectData().setTrendChartSerieDataArr(data);

        this.setCenter(lineChart);
        this.setBottom(downContent);
    }

    public void zoomInOperation() {
        this.zoomedIn = true;
        double sliderRange = getSliderRangeValue(startRangeHBox.getLocalDateTime(), endRangeHBox.getLocalDateTime(), this.getCanvasObjectData().getSamplingTime());
        updateSliders(startRangeHBox.getLocalDateTime(), sliderRange, true, null);
    }

    public void zoomOutOperation() {
        if (this.zoomedIn) {
            this.zoomedIn = false;
            updateSliders(LocalDateTime.now().minusSeconds(86400), 90000, false, null);
        }
    }

    public void updateSliders(LocalDateTime sliderDateTime, double sliderRange, boolean isFilterEnabled, ArrayList<TrendChartSerieData> selectedData) {
        this.sliderLocalDateTime = sliderDateTime;
        this.startRangeSlider.setMin(0.0);
        this.startRangeSlider.setMax(sliderRange);
        this.startRangeSlider.setValue(0.0);
        this.endRangeSlider.setMin(0.0);
        this.endRangeSlider.setMax(sliderRange);
        this.endRangeSlider.setValue(sliderRange);
        this.isFilterEnabled = isFilterEnabled;
        if (this.isFilterEnabled) {
            LocalDateTime startD = convertStringToLocalDateTime(convertSliderValueToString(sliderLocalDateTime, startRangeSlider.getValue()));
            LocalDateTime endD = convertStringToLocalDateTime(convertSliderValueToString(sliderLocalDateTime, endRangeSlider.getValue()));
            this.lineChart.getData().clear();
            for (XYChart.Series<String, Number> lineChartSerie : this.lineChartSeries) {
                this.lineChart.getData().add(filterSerie(startD, endD, lineChartSerie));
            }
        } else {
            this.startRangeHBox.setDateTime(sliderDateTime);
            this.endRangeHBox.setDateTime(sliderDateTime.plusSeconds(90000));
            if (!new HashSet<>(this.lineChart.getData()).containsAll(this.lineChartSeries)) {
                this.lineChart.getData().clear();
                for (XYChart.Series<String, Number> lineChartSerie : this.lineChartSeries) {
                    this.lineChart.getData().add(lineChartSerie);
                }
            }

        }
        for (int i = 0; i < lineChart.getData().size(); i++) {
            XYChart.Series<String, Number> serie = lineChart.getData().get(i);
            String colorString = "";
            for (int j = 0; j < trendChartSerieDataArrayList.size(); j++) {
                if (serie.getName().equals(trendChartSerieDataArrayList.get(j).getSerieDataName())) {
                    colorString = trendChartSerieDataArrayList.get(j).getColor().toHexString();
                }
            }
            serie.getNode().setStyle("-fx-stroke: " + colorString + ";");
        }
        for (Node node : lineChart.lookupAll("Label.chart-legend-item")) {
            String colorString = "";
            Label label = (Label) node;
            for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                if (label.getText().equals(trendChartSerieData.getSerieDataName())) {
                    colorString = trendChartSerieData.getColor().toHexString();
                }
            }
            label.getGraphic().setStyle("-fx-background-color: " + colorString + ", white");
        }
    }

    public XYChart.Series<String, Number> filterSerie(LocalDateTime start, LocalDateTime end, XYChart.Series<String, Number> serie) {
        XYChart.Series<String, Number> filteredSerie = new XYChart.Series<>();
        for (int i = 0; i < serie.getData().size(); i++) {
            LocalDateTime serieDateTime = convertStringToLocalDateTime(serie.getData().get(i).getXValue());
            if (serieDateTime.isAfter(start) && serieDateTime.isBefore(end)) {
                filteredSerie.getData().add(serie.getData().get(i));
            }
        }
        filteredSerie.setName(serie.getName());
        return filteredSerie;
    }

    public LocalDateTime convertStringToLocalDateTime(String dateFormatString) {
        Locale spanishLocale = new Locale("es", "ES");
        DateFormat dateFormat = new SimpleDateFormat(DISPLAY_FORMAT, spanishLocale);
        Date startD = null;
        try {
            startD = dateFormat.parse(dateFormatString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startD == null) {
            return null;
        } else {
            return Instant.ofEpochMilli(startD.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    public String convertSliderValueToString(LocalDateTime currentLocalDateTime, Double object) {
        LocalDateTime sliderDateTime = currentLocalDateTime.plusSeconds(object.longValue());
        return dtf.format(sliderDateTime);
    }

    public int convertDateFormatStringToSliderValue(LocalDateTime currentLocalDateTime, LocalDateTime sliderDateTime) {
        return (int) ChronoUnit.SECONDS.between(currentLocalDateTime, sliderDateTime);
    }

    public double getSliderRangeValue(LocalDateTime start, LocalDateTime end, double samplingTime) {
        double rangeSeconds = (int) ChronoUnit.SECONDS.between(start, end);
        return rangeSeconds / samplingTime;
    }

    public void setTrendTimeline() {
        this.trendTimeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            LocalDateTime now = LocalDateTime.now();
                            this.lastUpdatedTime = now;
                            for (XYChart.Series<String, Number> lineChartSerie : lineChartSeries) {
                                for (TrendChartSerieData trendChartSerieData : trendChartSerieDataArrayList) {
                                    if (lineChartSerie.getName().equals(trendChartSerieData.getSerieDataName())) {
                                        try {
                                            lineChartSerie.getData().add(new LineChart.Data<>(dtf.format(now), (double) trendChartSerieData.getExpression().evaluate()));
                                        } catch (CompileException | InvocationTargetException | SQLException |
                                                 IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                            }
                        }), new KeyFrame(Duration.seconds(this.getCanvasObjectData().getSamplingTime())));
        this.trendTimeline.setCycleCount(Animation.INDEFINITE);
        this.trendTimeline.play();
    }
}
