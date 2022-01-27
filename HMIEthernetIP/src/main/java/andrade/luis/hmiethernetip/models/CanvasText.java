package andrade.luis.hmiethernetip.models;

import andrade.luis.hmiethernetip.util.DBConnection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CanvasText extends CanvasLabel{
    private Connection con;
    private Statement statement;
    private ResultSet resultSet;
    private String text;

    public CanvasText(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.getGraphicalRepresentationData().setType("Text");
    }

    public CanvasText(String content, CanvasPoint center) {
        super(content, center);
        this.getGraphicalRepresentationData().setType("Text");
    }

    public void setTimeline(){
        Timeline t1 = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            try {
                                con = DBConnection.createConnection();
                                statement = con.createStatement();
                                resultSet = statement.executeQuery("select valor from FLOTANTE where nombreTag='temperatura'");
                                while (resultSet.next()){
                                    text = resultSet.getString("valor");
                                    this.getLabel().setText(text);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        t1.setCycleCount(t1.INDEFINITE);
        t1.play();
    }
}
