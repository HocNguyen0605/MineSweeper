package com.minesweeper.controller;

import com.minesweeper.model.Board;
import com.minesweeper.model.GameState;
import com.minesweeper.model.GameTimer;
import com.minesweeper.model.ScoreRecord;
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
        newGame();
    }

    // ── Setup ─────────────────────────────────────────────────

    private void registerViewHandlers() {
        mainView.getBoardView().setOnLeftClick(this::onLeftClick);
        mainView.getBoardView().setOnRightClick(this::onRightClick);
        mainView.getBoardView().setOnChord(this::onChord);
        mainView.getHeaderView().setOnReset(this::reset);
        mainView.getHeaderView().setOnDifficultyChange(this::setDifficulty);
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
        mainView.getBoardView().updateCell(row, col, board.getCell(row, col));

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
        timer.pause();
        gameState = GameState.LOSE;
        board.revealAllMines();
        mainView.getBoardView().revealAllMines(explodedRow, explodedCol);
        mainView.setDisabled(true);
        mainView.getHeaderView().setResetEmoji("😵");
        mainView.showResult(false);
    }
}