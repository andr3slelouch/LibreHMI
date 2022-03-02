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
    requires commons.compiler;
    requires janino;
    requires org.mybatis;

    opens andrade.luis.hmiethernetip to javafx.fxml;
    exports andrade.luis.hmiethernetip;
    exports andrade.luis.hmiethernetip.views;
    exports andrade.luis.hmiethernetip.models;
    exports andrade.luis.hmiethernetip.controllers;
    opens andrade.luis.hmiethernetip.models to javafx.base;
    opens andrade.luis.hmiethernetip.views to javafx.base, javafx.fxml;
    exports andrade.luis.hmiethernetip.models.canvas;
    exports andrade.luis.hmiethernetip.models.users;
    opens andrade.luis.hmiethernetip.models.canvas to javafx.base;
    exports andrade.luis.hmiethernetip.models.canvas.input;
    opens andrade.luis.hmiethernetip.models.canvas.input to javafx.base;
    opens andrade.luis.hmiethernetip.models.users to javafx.base;
}