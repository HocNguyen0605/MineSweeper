package com.minesweeper.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

public class MainView {

    private final VBox root;
    private final HeaderView headerView;
    private final BoardView  boardView;
    private final Scene scene;

    public MainView(HeaderView headerView, BoardView boardView) {
        this.headerView = headerView;
        this.boardView  = boardView;

        root = new VBox(8);
        root.setPadding(new Insets(12));
        root.getStyleClass().add("main-root");
        root.getChildren().addAll(headerView.getRoot(), boardView.getGrid());

        scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/css/style.css").toExternalForm()
        );
    }

    public void showResult(boolean win) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(win ? "Bạn thắng! 🎉" : "Bạn thua! 💥");
        alert.setHeaderText(null);
        alert.setContentText(win ? "Chúc mừng! Bạn đã tìm được tất cả mìn!" : "Bạn đã mở trúng mìn. Chúc may mắn lần sau!");
        alert.showAndWait();
    }

    public void setDisabled(boolean disabled) {
        boardView.setDisabled(disabled);
    }

    public Scene      getScene()      { return scene; }
    public HeaderView getHeaderView() { return headerView; }
    public BoardView  getBoardView()  { return boardView; }
}