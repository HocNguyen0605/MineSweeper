package com.minesweeper.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        // 10x10 board with 10 mines
        board = new Board(10, 10, 10);
    }

    // UC-1: Place Flag
    @Test
    void testPlaceFlag() {
        // Given a hidden cell at (0,0)
        Cell cell = board.getCell(0, 0);
        assertFalse(cell.isFlagged(), "Cell should not be flagged initially.");
        assertEquals(0, board.getFlagCount(), "Initial flag count should be 0.");

        // When toggling flag on the cell
        board.toggleFlag(0, 0);

        // Then the cell should be flagged and flag count should increase
        assertTrue(cell.isFlagged(), "Cell should be flagged after toggling.");
        assertEquals(1, board.getFlagCount(), "Flag count should be 1.");
    }

    // UC-2: Remove Flag
    @Test
    void testRemoveFlag() {
        // Given a flagged cell at (0,0)
        board.toggleFlag(0, 0);
        Cell cell = board.getCell(0, 0);
        assertTrue(cell.isFlagged(), "Cell should be flagged initially.");
        assertEquals(1, board.getFlagCount(), "Initial flag count should be 1.");

        // When toggling flag on the cell again
        board.toggleFlag(0, 0);

        // Then the cell should not be flagged and flag count should decrease
        assertFalse(cell.isFlagged(), "Cell should not be flagged after toggling again.");
        assertEquals(0, board.getFlagCount(), "Flag count should be 0.");
    }

    // UC-3: Chording
    @Test
    void testChording_Safe() {
        // Given a specific board setup for chording test
        board = new Board(5, 5, 2);
        // Place mines manually for predictable test
        board.getCell(0, 0).setMine();
        board.getCell(0, 2).setMine();
        // Calculate adjacent mines for all cells
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (!board.getCell(r, c).isMine()) {
                    int count = 0;
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int nr = r + dr;
                            int nc = c + dc;
                            if (nr >= 0 && nr < 5 && nc >= 0 && nc < 5 && board.getCell(nr, nc).isMine()) {
                                count++;
                            }
                        }
                    }
                    board.getCell(r, c).setAdjacentMines(count);
                }
            }
        }

        // Reveal cell (1,1) which has 2 adjacent mines
        board.getCell(1, 1).reveal();
        assertEquals(2, board.getCell(1, 1).getAdjacentMines());

        // Flag the two mines
        board.toggleFlag(0, 0);
        board.toggleFlag(0, 2);

        // When chording on cell (1,1)
        boolean isSafe = board.chord(1, 1);

        // Then the chording should be safe and reveal neighbors
        assertTrue(isSafe, "Chording should be safe.");
        assertTrue(board.getCell(0, 1).isRevealed(), "Neighbor (0,1) should be revealed.");
        assertTrue(board.getCell(1, 0).isRevealed(), "Neighbor (1,0) should be revealed.");
        assertTrue(board.getCell(1, 2).isRevealed(), "Neighbor (1,2) should be revealed.");
        assertTrue(board.getCell(2, 0).isRevealed(), "Neighbor (2,0) should be revealed.");
        assertTrue(board.getCell(2, 1).isRevealed(), "Neighbor (2,1) should be revealed.");
        assertTrue(board.getCell(2, 2).isRevealed(), "Neighbor (2,2) should be revealed.");

        // The flagged cells and the chording cell itself should not change state (except for revealed)
        assertTrue(board.getCell(0, 0).isFlagged(), "Mine at (0,0) should remain flagged.");
        assertTrue(board.getCell(0, 2).isFlagged(), "Mine at (0,2) should remain flagged.");
        assertTrue(board.getCell(1, 1).isRevealed(), "Chording cell (1,1) should remain revealed.");
    }

    @Test
    void testChording_Explosion() {
        // Given a specific board setup
        board = new Board(5, 5, 2);
        board.getCell(0, 0).setMine(); // Correctly flagged mine
        board.getCell(2, 2).setMine(); // Incorrectly unflagged mine that will cause explosion
        board.getCell(0, 2).setMine(); // This will be incorrectly flagged

        // Manually calculate adjacent mines for cell (1,1)
        board.getCell(1, 1).setAdjacentMines(3); // It's surrounded by 3 mines

        // Reveal the cell (1,1)
        board.getCell(1, 1).reveal();

        // Flag two cells, one correct, one incorrect
        board.toggleFlag(0, 0); // Correct flag
        board.toggleFlag(0, 2); // Incorrect flag

        // When chording on cell (1,1)
        boolean isSafe = board.chord(1, 1);

        // Then the chording should hit a mine and return false
        assertFalse(isSafe, "Chording should hit a mine and return false.");
    }

    // UC-4: View Total Mines
    @Test
    void testViewTotalMines() {
        // Given a board with a specific number of mines
        int totalMines = 15;
        board = new Board(10, 10, totalMines);

        // When getting the total number of mines
        int reportedMines = board.getTotalMines();

        // Then the reported number should match the actual number
        assertEquals(totalMines, reportedMines, "The number of mines should be correctly reported.");
    }
}

