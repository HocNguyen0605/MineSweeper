package com.minesweeper.view;

import com.minesweeper.controller.Difficulty; // Giả sử bạn có Enum này
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import java.util.function.Consumer;

public class MenuView {
    private final VBox root;
    private Consumer<Difficulty> onDifficultySelected;

    public MenuView() {
        root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 20;");

        // Tạo các nút chọn level
        Button btnEasy = createButton("Dễ (Easy)", Difficulty.EASY);
        Button btnMedium = createButton("Trung bình (Medium)", Difficulty.MEDIUM);
        Button btnHard = createButton("Khó (Hard)", Difficulty.HARD);

        root.getChildren().addAll(btnEasy, btnMedium, btnHard);
    }

    private Button createButton(String text, Difficulty difficulty) {
        Button btn = new Button(text);
        btn.setPrefWidth(200);
        btn.setOnAction(e -> {
            if (onDifficultySelected != null) {
                onDifficultySelected.accept(difficulty);
            }
        });
        return btn;
    }

    // Setter để Controller đăng ký hành động
    public void setOnDifficultySelected(Consumer<Difficulty> handler) {
        this.onDifficultySelected = handler;
    }

    public VBox getRoot() { return root; }
}