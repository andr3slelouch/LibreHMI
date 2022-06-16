package andrade.luis.hmiethernetip.views;

import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

public class DateHBox extends HBox {
    Logger logger = Logger.getLogger(getClass().getName());

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void setDatePicker(DatePicker datePicker) {
        this.datePicker = datePicker;
    }

    private DatePicker datePicker;
    private LocalDateTime sliderDateTime;

    public TextField getHoursTextField() {
        return hoursTextField;
    }

    public void setHoursTextField(TextField hoursTextField) {
        this.hoursTextField = hoursTextField;
    }

    public TextField getMinutesTextField() {
        return minutesTextField;
    }

    public void setMinutesTextField(TextField minutesTextField) {
        this.minutesTextField = minutesTextField;
    }

    public TextField getSecondsTextField() {
        return secondsTextField;
    }

    public void setSecondsTextField(TextField secondsTextField) {
        this.secondsTextField = secondsTextField;
    }

    public Slider getDateSlider() {
        return dateSlider;
    }

    public void setDateSlider(Slider dateSlider) {
        this.dateSlider = dateSlider;
    }

    public void attachSlider(Slider slider, LocalDateTime sliderDateTime) {
        setDateSlider(slider);
        this.sliderDateTime = sliderDateTime;
    }

    private final UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    private TextField hoursTextField;
    private TextField minutesTextField;
    private TextField secondsTextField;
    private Slider dateSlider;

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    private LocalDateTime localDateTime;
    private boolean isLocalUpdate = true;

    public DateHBox() {
        String[] days = {
                "lunes",
                "martes",
                "miércoles",
                "jueves",
                "viernes",
                "sábado",
                "domingo",
        };
        datePicker = new DatePicker();
        datePicker.valueProperty().addListener((observableValue, oldDate, newDate) -> this.setSliderValue());
        hoursTextField = new TextField("");
        hoursTextField.setPrefWidth(50);
        hoursTextField.setPromptText("HH");
        hoursTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        hoursTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = Double.parseDouble(newValue);
            if (value > 23) {
                hoursTextField.setText(String.valueOf((int) value%24));
            } else {
                this.setSliderValue();
            }
        });
        hoursTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.UP)) {
                    setDateTime(DateHBox.this.localDateTime.plusHours(1));
                    setSliderValue();
                }else if (keyEvent.getCode().equals(KeyCode.DOWN)) {
                    setDateTime(DateHBox.this.localDateTime.minusHours(1));
                    setSliderValue();
                }
            }
        });
        Label hoursLabel = new Label(":");
        minutesTextField = new TextField("");
        minutesTextField.setPrefWidth(50);
        minutesTextField.setPromptText("mm");
        minutesTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
        minutesTextField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            double value = Double.parseDouble(newValue);
            if (value > 59) {
                minutesTextField.setText(String.valueOf((int) value%60));
            } else {
                this.setSliderValue();
            }
        });
        minutesTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                if (keyEvent.getCode().equals(KeyCode.UP)) {
                    setDateTime(DateHBox.this.localDateTime.plusMinutes(1));
                    setSliderValue();
                }else if (keyEvent.getCode().equals(KeyCode.DOWN)) {
                    setDateTime(DateHBox.this.localDateTime.minusMinutes(1));
                    setSliderValue();
                }
            }
        });

        Label minutesLabel = new Label(":");
        secondsTextField = new TextField("");
        secondsTextField.setPrefWidth(50);
        secondsTextField.setPromptText("ss");
        secondsTextField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 0, integerFilter));
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
        this.getChildren().addAll(datePicker, hoursTextField, hoursLabel, minutesTextField, minutesLabel, secondsTextField);
    }

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

    public LocalDateTime getDateTimeFromString(String dateFormatString) throws ParseException {
        Locale spanishLocale = new Locale("es", "ES");
        DateFormat dateFormat = new SimpleDateFormat(FORMAT, spanishLocale);
        Date startD = null;
        startD = dateFormat.parse(dateFormatString);
        return Instant.ofEpochMilli(startD.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();

    }

    public void setSliderValue() {
        if (this.dateSlider != null && this.sliderDateTime != null && isLocalUpdate) {
            String dateFormatString = this.datePicker.getValue() + " " + hoursTextField.getText() + ":" + minutesTextField.getText() + ":" + secondsTextField.getText();
            try {
                this.localDateTime = getDateTimeFromString(dateFormatString);
                int sliderValue = convertDateFormatStringToSliderValue(this.sliderDateTime, this.localDateTime);
                this.dateSlider.setValue(sliderValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public int convertDateFormatStringToSliderValue(LocalDateTime currentLocalDateTime, LocalDateTime sliderDateTime) {
        int sliderValue = (int) ChronoUnit.SECONDS.between(currentLocalDateTime, sliderDateTime);
        return sliderValue;
    }
}
