package com.minesweeper.controller;

/**
 * Định nghĩa các cấp độ khó của game Minesweeper.
 * <p>
 * Mỗi cấp độ giữ sẵn thông số rows, cols, mines — FR-05.
 * {@link BoardFactory} dùng enum này để tạo Board đúng kích thước.
 * </p>
 *
 * <pre>
 * Board board = BoardFactory.createBoard(Difficulty.EASY);
 * </pre>
 */
public enum Difficulty {

    /** 9×9, 10 mìn */
    EASY(9, 9, 10),

    /** 16×16, 40 mìn */
    MEDIUM(16, 16, 40),

    /** 30×16, 99 mìn */
    HARD(30, 16, 99);

    // ── Fields ────────────────────────────────────────────────

    private final int rows;
    private final int cols;
    private final int mines;

    // ── Constructor ───────────────────────────────────────────

    Difficulty(int rows, int cols, int mines) {
        this.rows  = rows;
        this.cols  = cols;
        this.mines = mines;
    }

    // ── Getters ───────────────────────────────────────────────

    /** @return số hàng của bàn cờ ở cấp độ này */
    public int getRows()  { return rows; }

    /** @return số cột của bàn cờ ở cấp độ này */
    public int getCols()  { return cols; }

    /** @return số mìn của bàn cờ ở cấp độ này */
    public int getMines() { return mines; }
}