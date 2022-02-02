module andrade.luis.hmiethernetip {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.logging;
    requires java.desktop;
    requires com.google.gson;

    opens andrade.luis.hmiethernetip to javafx.fxml;
    exports andrade.luis.hmiethernetip;
    exports andrade.luis.hmiethernetip.views;
    exports andrade.luis.hmiethernetip.models;
    exports andrade.luis.hmiethernetip.example to javafx.graphics;
    opens andrade.luis.hmiethernetip.models to javafx.base;
    opens andrade.luis.hmiethernetip.views to javafx.fxml;
}