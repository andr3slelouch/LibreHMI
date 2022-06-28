package andrade.luis.librehmi.views;

import andrade.luis.librehmi.util.TextFormatters;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * HBox para definir los campos de fecha y hora
 */
public class DateHBox extends HBox {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final DatePicker datePicker;
    private LocalDateTime sliderDateTime;

    public void setDateSlider(Slider dateSlider) {
        this.dateSlider = dateSlider;
    }

    public void attachSlider(Slider slider, LocalDateTime sliderDateTime) {
        setDateSlider(slider);
        this.sliderDateTime = sliderDateTime;
    }



    private TextField hoursTextField;
    private TextField minutesTextField;
    private TextField secondsTextField;
    private Slider dateSlider;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    private LocalDateTime localDateTime;
    private boolean isLocalUpdate = true;

    /**
     * Constructor
     */
    public DateHBox() {
        datePicker = new DatePicker();
        datePicker.valueProperty().addListener((observableValue, oldDate, newDate) -> this.setSliderValue());
        initHoursTextField();
        Label hoursLabel = new Label(":");
        initMinutesTextField();
        Label minutesLabel = new Label(":");
        initSecondsTextField();
        this.getChildren().addAll(datePicker, hoursTextField, hoursLabel, minutesTextField, minutesLabel, secondsTextField);
    }

    /**
     * Permite definir la fecha y hora en los campos
     * @param localDateTime LocalDateTime con la hora requerida
     */
    public void setDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.localDateTime = localDateTime;
        isLocalUpdate = false;
        this.datePicker.setValue(LocalDate.parse(dtf.format(this.localDateTime), dtf));
        this.hoursTextField.setText(String.valueOf(localDateTime.getHour()));
        this.minutesTextField.setText(String.valueOf(localDateTime.getMinute()));
        this.secondsTextField.setText(String.valueOf(localDateTime.getSecond()));
        isLocalUpdate = true;
    }

    /**
     * Permite inicializar los campos de horas HBox
     */
    private void initHoursTextField(){
        hoursTextField = new TextField("");
        hoursTextField.setPrefWidth(50);
        hoursTextField.setPromptText("HH");
        hoursTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, TextFormatters.digitFilter));
        hoursTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = Double.parseDouble(newValue);
            if (value > 23) {
                hoursTextField.setText(String.valueOf((int) value%24));
            } else {
                this.setSliderValue();
            }
        });
        hoursTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.UP)) {
                setDateTime(DateHBox.this.localDateTime.plusHours(1));
                setSliderValue();
            }else if (keyEvent.getCode().equals(KeyCode.DOWN)) {
                setDateTime(DateHBox.this.localDateTime.minusHours(1));
                setSliderValue();
            }
        });
    }

    /**
     * Permite inicializar los campos de minutos del HBox
     */
    private void initMinutesTextField(){
        minutesTextField = new TextField("");
        minutesTextField.setPrefWidth(50);
        minutesTextField.setPromptText("mm");
        minutesTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, TextFormatters.digitFilter));
        minutesTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = Double.parseDouble(newValue);
            if (value > 59) {
                minutesTextField.setText(String.valueOf((int) value%60));
            } else {
                this.setSliderValue();
            }
        });
        minutesTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.UP)) {
                setDateTime(DateHBox.this.localDateTime.plusMinutes(1));
                setSliderValue();
            }else if (keyEvent.getCode().equals(KeyCode.DOWN)) {
                setDateTime(DateHBox.this.localDateTime.minusMinutes(1));
                setSliderValue();
            }
        });
    }

    /**
     * Permite inicializar los campos de minutos del HBox
     */
    private void initSecondsTextField(){
        secondsTextField = new TextField("");
        secondsTextField.setPrefWidth(50);
        secondsTextField.setPromptText("ss");
        secondsTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, TextFormatters.digitFilter));
        secondsTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = Double.parseDouble(newValue);
            if (value > 59) {
                secondsTextField.setText(String.valueOf((int) value%60));
            } else {
                this.setSliderValue();
            }
        });
        secondsTextField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(KeyCode.UP)) {
                setDateTime(DateHBox.this.localDateTime.plusSeconds(1));
                setSliderValue();
            }else if (keyEvent.getCode().equals(KeyCode.DOWN)) {
                setDateTime(DateHBox.this.localDateTime.minusSeconds(1));
                setSliderValue();
            }
        });
    }

    /**
     * Permite obtener el LocalDateTime a partir del string ingresado
     * @param dateFormatString Fecha y hora en string
     * @return LocalDateTime convertido a partir del string
     * @throws ParseException
     */
    public LocalDateTime getDateTimeFromString(String dateFormatString) throws ParseException {
        Locale spanishLocale = new Locale("es", "ES");
        DateFormat dateFormat = new SimpleDateFormat(FORMAT, spanishLocale);
        Date startD;
        startD = dateFormat.parse(dateFormatString);
        return Instant.ofEpochMilli(startD.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    /**
     * Permite definir el valor del slider
     */
    public void setSliderValue() {
        if (this.dateSlider != null && this.sliderDateTime != null && isLocalUpdate) {
            String dateFormatString = this.datePicker.getValue() + " " + hoursTextField.getText() + ":" + minutesTextField.getText() + ":" + secondsTextField.getText();
            try {
                this.localDateTime = getDateTimeFromString(dateFormatString);
                int sliderValue = convertDateFormatStringToSliderValue(this.sliderDateTime, this.localDateTime);
                this.dateSlider.setValue(sliderValue);
            } catch (ParseException e) {
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.log(Level.INFO,e.getMessage());
            }
        }
    }

    /**
     * Permite obtener un valor de slider a partir de un LocalDateTime
     * @param currentLocalDateTime LocalDateTime de fecha y hora actual
     * @param sliderDateTime LocalDateTime del slider
     * @return Valor del slider
     */
    public int convertDateFormatStringToSliderValue(LocalDateTime currentLocalDateTime, LocalDateTime sliderDateTime) {
        return (int) ChronoUnit.SECONDS.between(currentLocalDateTime, sliderDateTime);
    }
}
