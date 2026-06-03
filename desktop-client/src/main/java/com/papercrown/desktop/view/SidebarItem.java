package com.papercrown.desktop.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignH;
import org.kordamp.ikonli.materialdesign2.MaterialDesignP;
import org.kordamp.ikonli.materialdesign2.MaterialDesignT;
import org.kordamp.ikonli.materialdesign2.MaterialDesignV;

public class SidebarItem extends HBox {

    private static Ikon resolveIcon(String iconStr) {
        return switch (iconStr) {
            case "mdi2v:view-dashboard" -> MaterialDesignV.VIEW_DASHBOARD;
            case "mdi2p:play-circle" -> MaterialDesignP.PLAY_CIRCLE;
            case "mdi2h:history" -> MaterialDesignH.HISTORY;
            case "mdi2t:trophy" -> MaterialDesignT.TROPHY_OUTLINE;
            case "mdi2c:cog" -> MaterialDesignC.COG;
            default -> MaterialDesignH.HELP_CIRCLE;
        };
    }

    private final FontIcon icon;
    private final Label textLabel;
    private boolean active = false;

    public SidebarItem(String text, String iconStr) {
        setPadding(new Insets(10, 12, 10, 12));
        setSpacing(12);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("sidebar-item");

        icon = new FontIcon(resolveIcon(iconStr));
        icon.getStyleClass().add("sidebar-icon");
        icon.setIconSize(18);

        textLabel = new Label(text);
        textLabel.getStyleClass().add("sidebar-label");

        getChildren().addAll(icon, textLabel);

        setOnMouseClicked(this::handleClick);
    }

    private void handleClick(MouseEvent event) {
        if (onAction != null) {
            onAction.handle(event);
        }
    }

    private javafx.event.EventHandler<MouseEvent> onAction;

    public void setOnAction(javafx.event.EventHandler<MouseEvent> handler) {
        this.onAction = handler;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            getStyleClass().add("sidebar-item-active");
        } else {
            getStyleClass().remove("sidebar-item-active");
        }
    }

    public boolean isActive() {
        return active;
    }
}
