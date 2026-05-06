package com.minesweeper.controller;

import com.minesweeper.model.*;
import com.minesweeper.view.MainView;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GameController {

    private Board board;
    private final GameTimer timer;
    private final ScoreRecord record;
    private GameState gameState;
    private Difficulty difficulty;
    private final MainView mainView;

    public GameController(MainView mainView) {
        this.mainView = mainView;
        this.timer = new GameTimer();
        this.record = ScoreRecord.getInstance();
        this.difficulty = Difficulty.EASY;
        this.gameState = GameState.IDLE;

        registerViewHandlers();
        registerMenuHandlers();
    }

    // ── Setup ─────────────────────────────────────────────────

    private void registerViewHandlers() {
        mainView.getBoardView().setOnLeftClick(this::onLeftClick);
        mainView.getBoardView().setOnRightClick(this::onRightClick);
        mainView.getBoardView().setOnChord(this::onChord);
        mainView.getHeaderView().setOnReset(this::reset);
        mainView.getHeaderView().setOnDifficultyChange(this::setDifficulty);
    }
    // Trong GameController constructor
    private void registerMenuHandlers() {
        mainView.getMenuView().setOnDifficultySelected(difficulty -> {
            this.difficulty = difficulty;
            mainView.showGame(); // Hiện bàn cờ
            newGame();           // Gọi logic tạo board của bạn
        });
    }
    private void bindProperties() {
        // Bind timer
        mainView.getHeaderView().bindTimer(timer.elapsedSecondsProperty());

        // Remaining mines = totalMines - flagCount
        IntegerProperty remaining = new SimpleIntegerProperty();
        remaining.bind(Bindings.subtract(board.getTotalMines(), board.flagCountProperty()));
        mainView.getHeaderView().bindMineCount(remaining);
    }

    // ── Game Management ───────────────────────────────────────

    public void newGame() {
        System.out.println("Đang tạo game với độ khó: " + difficulty);
        System.out.println("Board size: " + board.getRows() + "x" + board.getCols());
        board = BoardFactory.createBoard(difficulty);   // Factory pattern
        gameState = GameState.IDLE;
        timer.reset();

        mainView.getBoardView().build(board.getRows(), board.getCols());
        bindProperties();

        mainView.getHeaderView().setResetEmoji("🙂");
        mainView.getHeaderView().showBestTime(record.getBestTime(difficulty));
        mainView.setDisabled(false);
    }

    public void reset() {
        newGame();
    }

    public void pause() {
        if (gameState != GameState.PLAYING) return;
        timer.pause();
        gameState = GameState.PAUSED;
        mainView.setDisabled(true);
    }

    public void resume() {
        if (gameState != GameState.PAUSED) return;
        timer.resume();
        gameState = GameState.PLAYING;
        mainView.setDisabled(false);
    }

    public void setDifficulty(Difficulty d) {
        this.difficulty = d;
        newGame();
    }

    // ── Board Interaction ─────────────────────────────────────

    public void onLeftClick(int row, int col) {
        if (gameState == GameState.PAUSED
                || gameState == GameState.WIN
                || gameState == GameState.LOSE) return;

        if (gameState == GameState.IDLE) {
            timer.start();
            gameState = GameState.PLAYING;
        }

        boolean safe = board.revealCell(row, col);
        // Cập nhật tất cả ô vừa được reveal (bao gồm flood-fill)
        for (int[] pos : board.getLastRevealedPositions()) {
            mainView.getBoardView().updateCell(pos[0], pos[1], board.getCell(pos[0], pos[1]));
        }

        if (!safe) handleLose(row, col);
        else if (board.checkWin()) handleWin();
    }

    public void onRightClick(int row, int col) {
        if (gameState != GameState.PLAYING) return;
        board.toggleFlag(row, col);
        mainView.getBoardView().updateCell(row, col, board.getCell(row, col));
    }

    public void onChord(int row, int col) {
        if (gameState != GameState.PLAYING) return;
        boolean safe = board.chord(row, col);
        // TODO: cập nhật lại các ô xung quanh trên View
        if (!safe) handleLose(row, col);
        else if (board.checkWin()) handleWin();
    }

    public void onKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.F2) reset();
    }

    // ── Game Status ───────────────────────────────────────────

    private void handleWin() {
        timer.pause();
        gameState = GameState.WIN;
        mainView.setDisabled(true);
        mainView.getHeaderView().setResetEmoji("😎");
        record.update(difficulty, timer.getElapsedSeconds());
        mainView.getHeaderView().showBestTime(record.getBestTime(difficulty));
        mainView.showResult(true);
    }

    private void handleLose(int explodedRow, int explodedCol) {
        board.revealAllMines();
        timer.pause();
        gameState = GameState.LOSE;
        mainView.getBoardView().revealAllMines(explodedRow, explodedCol);
        mainView.setDisabled(true);
        mainView.getHeaderView().setResetEmoji("😵");
        mainView.showResult(false);
    }
}