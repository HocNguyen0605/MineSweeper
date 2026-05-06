package com.minesweeper.model;

public enum CellState {
    /** Ô chưa được mở, chưa cắm cờ */
    HIDDEN,

    /** Ô đã được mở */
    REVEALED,

    /** Ô đã được cắm cờ */
    FLAGGED
}