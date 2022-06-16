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
    requires java.sql;
    requires com.opencsv;
    requires jasypt;

    opens andrade.luis.hmiethernetip to javafx.fxml;
    exports andrade.luis.hmiethernetip;
    exports andrade.luis.hmiethernetip.views;
    exports andrade.luis.hmiethernetip.models;
    exports andrade.luis.hmiethernetip.controllers;
    opens andrade.luis.hmiethernetip.models to javafx.base, com.google.gson, javafx.fxml;
    exports andrade.luis.hmiethernetip.views.canvas;
    exports andrade.luis.hmiethernetip.models.users;
    opens andrade.luis.hmiethernetip.views.canvas to javafx.base, com.google.gson;
    exports andrade.luis.hmiethernetip.views.canvas.input;
    opens andrade.luis.hmiethernetip.views.canvas.input to javafx.base;
    opens andrade.luis.hmiethernetip.models.users to javafx.base, com.google.gson;
    opens andrade.luis.hmiethernetip.views to com.google.gson, javafx.base, javafx.fxml;
    exports andrade.luis.hmiethernetip.views.windows;
    opens andrade.luis.hmiethernetip.views.windows to com.google.gson, javafx.base, javafx.fxml;
}