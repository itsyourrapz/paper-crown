package com.papercrown.desktop.component;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class Toast extends HBox {

    public enum Type {
        INFO, ERROR, ACHIEVEMENT
    }

    public Toast(String message, Type type) {
        setPadding(new Insets(12, 20, 12, 20));
        setSpacing(10);
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(javafx.scene.layout.Region.USE_PREF_SIZE);
        getStyleClass().add("toast");

        FontIcon icon = switch (type) {
            case INFO -> new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
            case ERROR -> new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
            case ACHIEVEMENT -> new FontIcon(FontAwesomeSolid.TROPHY);
        };
        icon.setIconSize(18);
        icon.getStyleClass().add("toast-icon");

        switch (type) {
            case INFO -> getStyleClass().add("toast-info");
            case ERROR -> getStyleClass().add("toast-error");
            case ACHIEVEMENT -> getStyleClass().add("toast-achievement");
        }

        Label msgLabel = new Label(message);
        msgLabel.getStyleClass().add("toast-message");

        getChildren().addAll(icon, msgLabel);
    }

    public static void show(StackPane parent, String message, Type type) {
        show(parent, message, type, 4000);
    }

    public static void show(StackPane parent, String message, Type type, int displayMs) {
        Toast toast = new Toast(message, type);
        StackPane.setAlignment(toast, Pos.TOP_RIGHT);
        StackPane.setMargin(toast, new Insets(16, 16, 0, 0));

        toast.setTranslateX(400);
        toast.setOpacity(0);
        parent.getChildren().add(toast);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(300), toast);
        slideIn.setToX(0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
        fadeIn.setToValue(1);
        ParallelTransition enter = new ParallelTransition(slideIn, fadeIn);
        enter.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.millis(displayMs));
            pause.setOnFinished(ev -> {
                TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), toast);
                slideOut.setToX(400);
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
                fadeOut.setToValue(0);
                ParallelTransition exit = new ParallelTransition(slideOut, fadeOut);
                exit.setOnFinished(ex -> parent.getChildren().remove(toast));
                exit.play();
            });
            pause.play();
        });
        enter.play();
    }
}
