package tests.andrade.luis.librehmi.views.windows;

import andrade.luis.librehmi.views.windows.LogInWindow;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

@ExtendWith(ApplicationExtension.class)
class LogInWindowFxTest {
    private LogInWindow root;

    @Start
    private void start(Stage stage) {
        root = new LogInWindow();
        stage = root;

        stage.show();
    }

    @Test
    void logInEmpty(FxRobot robot){
        robot.clickOn(root.usernameEmailField);
        robot.write("admin");
        robot.clickOn(root.passwordField);
        robot.write("12345");
        robot.clickOn(root.signInButton);
        Assertions.assertThat(root.getLoggedUser()).isNotNull();
    }
}
