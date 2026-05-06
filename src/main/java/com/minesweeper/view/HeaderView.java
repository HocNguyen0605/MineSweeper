package com.minesweeper.view;
import com.minesweeper.controller.Difficulty;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import java.util.function.Consumer;

public class HeaderView {

    private final HBox root;
    private final Label mineCountLabel;
    private final Label timerLabel;
    private final Button resetButton;
    private final MenuButton difficultyMenu;

    private Consumer<Difficulty> onDifficultyChange;

    public HeaderView() {
        mineCountLabel = new Label("000");
        mineCountLabel.getStyleClass().add("header-counter");

        timerLabel = new Label("000");
        timerLabel.getStyleClass().add("header-counter");

        resetButton = new Button("🙂");
        resetButton.getStyleClass().add("reset-button");

        difficultyMenu = new MenuButton("Easy");
        difficultyMenu.getStyleClass().add("difficulty-menu");
        initDifficultyMenu();

        // Spacer để căn giữa reset button
        Region spacerLeft  = new Region();
        Region spacerRight = new Region();
        HBox.setHgrow(spacerLeft,  Priority.ALWAYS);
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        root = new HBox(10);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("header");
        root.getChildren().addAll(
                mineCountLabel, spacerLeft,
                resetButton,
                spacerRight, timerLabel,
                difficultyMenu
        );
    }

    private void initDifficultyMenu() {
        for (Difficulty d : Difficulty.values()) {
            MenuItem item = new MenuItem(d.name().charAt(0) + d.name().substring(1).toLowerCase());
            item.setOnAction(e -> {
                difficultyMenu.setText(item.getText());
                if (onDifficultyChange != null) onDifficultyChange.accept(d);
            });
            difficultyMenu.getItems().add(item);
        }
    }

    // ── Binding ───────────────────────────────────────────────

    public void bindTimer(IntegerProperty secondsProp) {
        timerLabel.textProperty().bind(secondsProp.asString("%03d"));
    }

    public void bindMineCount(IntegerProperty remainingMinesProp) {
        mineCountLabel.textProperty().bind(remainingMinesProp.asString("%03d"));
    }

    // ── Event setters ─────────────────────────────────────────

    public void setOnReset(Runnable handler) {
        resetButton.setOnAction(e -> handler.run());
    }

    public void setOnDifficultyChange(Consumer<Difficulty> handler) {
        this.onDifficultyChange = handler;
    }

    // ── UI updates ────────────────────────────────────────────

    public void setResetEmoji(String emoji) {
        resetButton.setText(emoji);
    }

    public void showBestTime(int seconds) {
        String text = (seconds == Integer.MAX_VALUE) ? "Best: --" : "Best: " + seconds + "s";
        difficultyMenu.setTooltip(new Tooltip(text));
    }

    public HBox getRoot() { return root; }
}
