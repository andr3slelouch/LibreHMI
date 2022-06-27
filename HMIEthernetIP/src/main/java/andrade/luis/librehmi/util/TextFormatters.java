package andrade.luis.librehmi.util;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class TextFormatters {
    private TextFormatters(){
        throw new IllegalStateException("TextFormatters");
    }
    public static final UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^([+-])?\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public static final UnaryOperator<TextFormatter.Change> digitFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public static final UnaryOperator<TextFormatter.Change> numberFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("^([+-])?\\d+\\.\\d+$")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        return change;
    };

    public static final UnaryOperator<TextFormatter.Change> booleanFilter = change -> {
        String newText = change.getControlNewText();
        if (!newText.matches("(\\b0\\b)|(\\b1\\b)|(\\b00\\b)|(\\b01\\b)|")) {
            change.setText("");
            change.setRange(change.getRangeStart(), change.getRangeStart());
        }
        if (!change.getText().isEmpty()) {
            change.setText(String.valueOf(Integer.valueOf(change.getText())));
        }
        return change;
    };
}
