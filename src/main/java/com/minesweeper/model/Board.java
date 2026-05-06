package com.minesweeper.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Board {

    private Cell[][] cells;
    private int rows;
    private int cols;
    private int totalMines;
    private final IntegerProperty flagCount = new SimpleIntegerProperty(0);
    private boolean firstClick;

    /**
     * Tạo Board với kích thước và số mìn cho trước.
     * Chưa đặt mìn — đợi đến click đầu tiên.
     *
     * @param rows       số hàng
     * @param cols       số cột
     * @param totalMines tổng số mìn
     */
    public Board(int rows, int cols, int totalMines) {
        this.rows = rows;
        this.cols = cols;
        this.totalMines = totalMines;
        this.firstClick = true;
        // TODO: khởi tạo mảng cells, tạo Cell tại mỗi vị trí
    }

    // ── Mine Placement ────────────────────────────────────────

    /**
     * Đặt mìn ngẫu nhiên lên bàn cờ, đảm bảo ô (safeRow, safeCol)
     * và 8 ô xung quanh nó không bao giờ là mìn.
     * Gọi lần đầu tiên khi người chơi click ô đầu tiên — FR-12.
     *
     * @param safeRow hàng của ô đầu tiên được click
     * @param safeCol cột của ô đầu tiên được click
     */
    public void placeMines(int safeRow, int safeCol) {
        // TODO: random đặt totalMines mìn, tránh vùng an toàn quanh (safeRow, safeCol)
        // TODO: sau khi đặt mìn, gọi calculateAdjacentMines()
    }

    /**
     * Tính và gán số mìn xung quanh cho từng ô không phải mìn.
     * Gọi một lần sau {@link #placeMines(int, int)} — FR-13.
     */
    private void calculateAdjacentMines() {
        // TODO: duyệt toàn bộ cells, với mỗi ô không phải mìn → đếm mìn trong 8 ô xung quanh
    }

    // ── Board Interaction ─────────────────────────────────────

    /**
     * Xử lý thao tác mở ô tại (row, col).
     * <ul>
     *   <li>Nếu là click đầu tiên → gọi {@link #placeMines(int, int)} trước — FR-12</li>
     *   <li>Không làm gì nếu ô đã REVEALED hoặc FLAGGED — UR-04, FR-10</li>
     *   <li>Nếu là mìn → trả về false (GameController xử lý thua) — FR-17</li>
     *   <li>Nếu ô trống → gọi {@link #floodFill(int, int)} — FR-14</li>
     * </ul>
     *
     * @param row hàng
     * @param col cột
     * @return false nếu người chơi vừa mở trúng mìn, true nếu an toàn
     */
    public boolean revealCell(int row, int col) {
        // TODO: implement
        return true;
    }

    /**
     * Đệ quy mở tất cả ô trống liền kề cho đến khi gặp ô có số.
     * FR-14
     *
     * @param row hàng
     * @param col cột
     */
    private void floodFill(int row, int col) {
        // TODO: BFS hoặc DFS mở các ô HIDDEN liền kề chưa phải mìn
    }

    /**
     * Xử lý Chording: double-click vào ô số đã có đủ cờ xung quanh
     * → tự động mở các ô HIDDEN còn lại xung quanh.
     * FR-11
     *
     * @param row hàng
     * @param col cột
     * @return false nếu chording gây nổ mìn, true nếu an toàn
     */
    public boolean chord(int row, int col) {
        // TODO: kiểm tra số cờ xung quanh == adjacentMines của ô đó
        //       nếu đúng → mở toàn bộ ô HIDDEN xung quanh
        return true;
    }

    /**
     * Chuyển đổi cờ cho ô tại (row, col).
     * Cập nhật flagCount — FR-09, FR-15, FR-16.
     *
     * @param row hàng
     * @param col cột
     */
    public void toggleFlag(int row, int col) {
        // TODO: gọi cell.toggleFlag(), cập nhật flagCount (+1 hoặc -1)
    }

    // ── Game Status ───────────────────────────────────────────

    /**
     * Kiểm tra điều kiện thắng.
     * Thắng khi tất cả ô không phải mìn đều đã được mở — FR-18.
     *
     * @return true nếu người chơi đã thắng
     */
    public boolean checkWin() {
        // TODO: đếm số ô chưa mở, so với totalMines
        return false;
    }

    /**
     * Lật ngửa toàn bộ mìn trên bàn cờ khi người chơi thua.
     * FR-17
     */
    public void revealAllMines() {
        // TODO: duyệt cells, với mỗi ô isMine == true → reveal()
    }

    // ── Helpers ───────────────────────────────────────────────

    /**
     * Kiểm tra (row, col) có nằm trong phạm vi bàn cờ không.
     *
     * @param row hàng cần kiểm tra
     * @param col cột cần kiểm tra
     * @return true nếu hợp lệ
     */
    private boolean inBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    // ── Getters ───────────────────────────────────────────────

    /**
     * Lấy ô tại vị trí (row, col).
     *
     * @param row hàng
     * @param col cột
     * @return đối tượng {@link Cell}
     */
    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    /** @return số hàng của bàn cờ */
    public int getRows() {
        return rows;
    }

    /** @return số cột của bàn cờ */
    public int getCols() {
        return cols;
    }

    /** @return tổng số mìn */
    public int getTotalMines() {
        return totalMines;
    }

    /**
     * Trả về IntegerProperty của số cờ đã cắm.
     * HeaderView bind vào property này để tự cập nhật — Observer pattern, FR-15.
     *
     * @return flagCount property
     */
    public IntegerProperty flagCountProperty() {
        return flagCount;
    }

    /** @return giá trị số cờ hiện tại */
    public int getFlagCount() {
        return flagCount.get();
    }
}
