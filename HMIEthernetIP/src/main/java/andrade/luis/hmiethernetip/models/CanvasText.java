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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CanvasText extends CanvasLabel {
    private Connection con;
    private Statement statement;
    private ResultSet resultSet;
    private String text;
    private String query;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
        if(tag != null){
            this.query = prepareQuery(tag);
        }
    }

    private Tag tag;
    private Map<String, String> queries = Map.of("Entero", "select valor from ENTERO where nombreTag=", "Flotante", "select valor from FLOTANTE where nombreTag=", "Bool", "select valor from BOOLEAN where nombreTag=");

    public CanvasText(GraphicalRepresentationData graphicalRepresentationData) {
        super(graphicalRepresentationData);
        this.getGraphicalRepresentationData().setType("Text");
    }

    public CanvasText(String content, CanvasPoint center) {
        super(content, center);
        this.getGraphicalRepresentationData().setType("Text");
    }

    private String prepareQuery(Tag tag) {
        return queries.get(tag.getTagType()) + "'" + tag.getTagName() + "'";
    }

    public void setTimeline() {
        Timeline t1 = new Timeline(
                new KeyFrame(
                        Duration.seconds(0),
                        (ActionEvent actionEvent) -> {
                            try {
                                con = DBConnection.createConnection();
                                statement = con.createStatement();
                                if(query != null){
                                    resultSet = statement.executeQuery(query);
                                    while (resultSet.next()) {
                                        text = resultSet.getString("valor");
                                        this.getLabel().setText(text);
                                    }
                                }
                                con.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }), new KeyFrame(Duration.seconds(1)));
        t1.setCycleCount(t1.INDEFINITE);
        t1.play();
    }
}
