package com.minesweeper.controller;

import com.minesweeper.model.Board;

/**
 * Tạo đối tượng {@link Board} theo cấp độ khó cho trước.
 * <p>
 * <b>Factory Method pattern:</b> Tách logic khởi tạo Board ra khỏi
 * {@link GameController}. Khi cần thêm chế độ Custom (tuỳ chỉnh kích
 * thước), chỉ cần thêm vào class này mà không động vào Controller.
 * </p>
 *
 * <pre>
 * // Dùng trong GameController:
 * Board board = BoardFactory.createBoard(Difficulty.MEDIUM);
 * </pre>
 */
public class BoardFactory {

    /**
     * Constructor private — class này chỉ có static method,
     * không cần khởi tạo instance.
     */
    private BoardFactory() {}

    /**
     * Tạo một {@link Board} mới với kích thước và số mìn
     * tương ứng với {@link Difficulty} được chỉ định.
     * Factory Method pattern — FR-05.
     *
     * @param difficulty cấp độ khó (EASY / MEDIUM / HARD)
     * @return Board mới chưa đặt mìn (mìn đặt sau click đầu tiên — FR-12)
     */
    public static Board createBoard(Difficulty difficulty) {
        return new Board(difficulty.getRows(), difficulty.getCols(), difficulty.getMines());
    }
}
