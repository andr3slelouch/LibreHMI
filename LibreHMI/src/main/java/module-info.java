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

    opens andrade.luis.librehmi to javafx.fxml;
    exports andrade.luis.librehmi;
    exports andrade.luis.librehmi.views;
    exports andrade.luis.librehmi.models;
    exports andrade.luis.librehmi.controllers;
    opens andrade.luis.librehmi.models to javafx.base, com.google.gson, javafx.fxml;
    exports andrade.luis.librehmi.views.canvas;
    exports andrade.luis.librehmi.models.users;
    opens andrade.luis.librehmi.views.canvas to javafx.base, com.google.gson;
    exports andrade.luis.librehmi.views.canvas.input;
    opens andrade.luis.librehmi.views.canvas.input to javafx.base;
    opens andrade.luis.librehmi.models.users to javafx.base, com.google.gson;
    opens andrade.luis.librehmi.views to com.google.gson, javafx.base, javafx.fxml;
    exports andrade.luis.librehmi.views.windows;
    opens andrade.luis.librehmi.views.windows to com.google.gson, javafx.base, javafx.fxml;
    exports andrade.luis.librehmi.util;
}