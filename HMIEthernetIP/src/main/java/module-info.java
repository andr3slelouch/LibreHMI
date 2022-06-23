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

    opens andrade.luis.libreHMI to javafx.fxml;
    exports andrade.luis.libreHMI;
    exports andrade.luis.libreHMI.views;
    exports andrade.luis.libreHMI.models;
    exports andrade.luis.libreHMI.controllers;
    opens andrade.luis.libreHMI.models to javafx.base, com.google.gson, javafx.fxml;
    exports andrade.luis.libreHMI.views.canvas;
    exports andrade.luis.libreHMI.models.users;
    opens andrade.luis.libreHMI.views.canvas to javafx.base, com.google.gson;
    exports andrade.luis.libreHMI.views.canvas.input;
    opens andrade.luis.libreHMI.views.canvas.input to javafx.base;
    opens andrade.luis.libreHMI.models.users to javafx.base, com.google.gson;
    opens andrade.luis.libreHMI.views to com.google.gson, javafx.base, javafx.fxml;
    exports andrade.luis.libreHMI.views.windows;
    opens andrade.luis.libreHMI.views.windows to com.google.gson, javafx.base, javafx.fxml;
}