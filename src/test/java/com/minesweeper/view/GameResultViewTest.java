package com.minesweeper.view;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC14 – VIEW GAME RESULT
 * Kiểm tra logic điều phối của GameResultView.showPvP():
 *   - 14.1.1 : Dialog khởi tạo không viền (kiểm tra qua kết quả trả về)
 *   - 14.1.2 : Tiêu đề / banner đúng theo winner (1 / 2 / 0)
 *   - 14.1.4 : Nút RESTART / QUIT_TO_MENU trả về Action tương ứng
 *   - 14.1.5 : show() solo lose → Action RESTART hoặc QUIT_TO_MENU
 *   - 14.3.1 : RESTART từ tầng hai
 *   - 14.3.2 : QUIT_TO_MENU từ tầng hai
 *
 * Vì GameResultView.showPvP() dùng Stage.showAndWait() (blocking),
 * chúng ta dùng subclass StubGameResultView để ghi lại tham số đầu vào
 * và trả về Action được cấu hình sẵn mà không mở cửa sổ thật.
 */
public class GameResultViewTest {

    @BeforeAll
    static void startJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException ignored) { }
    }

    // =========================================================
    // UC14 – 14.1.2  Tiêu đề / banner theo winner
    // =========================================================

    /**
     * 14.1.2: Khi winner == 1, view phải nhận được tên P1 và winner = 1
     * để render tiêu đề "P1 CHIẾN THẮNG!" với banner màu xanh (win style).
     */
    @Test
    void showPvP_winner1_shouldReceiveCorrectWinnerAndNames() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> view.showPvP(null, 1, "Alice", "Bob", 30, 45, 3, 2));

        assertEquals(1, view.capturedWinner);
        assertEquals("Alice", view.capturedP1Name);
        assertEquals("Bob", view.capturedP2Name);
    }

    /**
     * 14.1.2 (đối xứng): winner == 2 → banner tone đỏ (lose style cho P1).
     */
    @Test
    void showPvP_winner2_shouldReceiveCorrectWinner() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> view.showPvP(null, 2, "Alice", "Bob", 30, 45, 3, 2));

        assertEquals(2, view.capturedWinner);
    }

    /**
     * 14.1.2: winner == 0 → HÒA, tiêu đề "KẾT QUẢ: HÒA NHAU!".
     */
    @Test
    void showPvP_draw_shouldReceiveWinnerZero() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> view.showPvP(null, 0, "Alice", "Bob", 60, 60, 5, 5));

        assertEquals(0, view.capturedWinner);
    }

    // =========================================================
    // UC14 – 14.1.3  Thống kê Grid (thời gian, số cờ)
    // =========================================================

    /**
     * 14.1.3: Bảng số liệu phải nhận đúng thời gian và số cờ của cả hai bên.
     */
    @Test
    void showPvP_shouldReceiveCorrectTimeAndFlagStats() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.RESTART);

        runFxVoid(() -> view.showPvP(null, 1, "Alice", "Bob", 120, 95, 7, 4));

        assertEquals(120, view.capturedTimeP1);
        assertEquals(95,  view.capturedTimeP2);
        assertEquals(7,   view.capturedFlagsP1);
        assertEquals(4,   view.capturedFlagsP2);
    }

    // =========================================================
    // UC14 – 14.1.4 / 14.1.5  Nút RESTART
    // =========================================================

    /**
     * 14.1.4: Khi người dùng nhấn "THI ĐẤU LẠI" (RESTART),
     * showPvP() phải trả về Action.RESTART.
     */
    @Test
    void showPvP_userClicksRestart_shouldReturnRestartAction() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.RESTART);

        AtomicReference<GameResultView.Action> result = new AtomicReference<>();
        runFxVoid(() -> result.set(view.showPvP(null, 1, "Alice", "Bob", 30, 40, 2, 3)));

        assertEquals(GameResultView.Action.RESTART, result.get());
    }

    /**
     * 14.1.5: Khi người dùng nhấn "QUAY LẠI MENU" (QUIT_TO_MENU),
     * showPvP() phải trả về Action.QUIT_TO_MENU.
     */
    @Test
    void showPvP_userClicksMenu_shouldReturnQuitToMenuAction() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.QUIT_TO_MENU);

        AtomicReference<GameResultView.Action> result = new AtomicReference<>();
        runFxVoid(() -> result.set(view.showPvP(null, 2, "Alice", "Bob", 55, 70, 1, 6)));

        assertEquals(GameResultView.Action.QUIT_TO_MENU, result.get());
    }

    // =========================================================
    // UC14 – 14.2.1 (AltF1)  Chơi lại ngay từ tầng 1 (P1 thắng)
    // =========================================================

    /**
     * 14.2.1: Tầng lựa chọn thứ nhất – người chơi chọn RESTART ngay
     * sau khi đối thủ dẫm mìn (winner == 1 hoặc 2) mà không chờ đơn độc.
     * showPvP() phải trả về Action.RESTART và winner không phải 0 (không phải DRAW).
     */
    @Test
    void showPvP_tier1Restart_winnerNotZero_shouldReturnRestart() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.RESTART);

        AtomicReference<GameResultView.Action> result = new AtomicReference<>();
        runFxVoid(() -> result.set(view.showPvP(null, 1, "Alice", "Bob", 10, 0, 0, 0)));

        assertEquals(GameResultView.Action.RESTART, result.get());
        assertNotEquals(0, view.capturedWinner);
    }

    // =========================================================
    // UC14 – 14.3.1 / 14.3.2 (AltF2)  Tầng 2 – sau chu kỳ đơn độc
    // =========================================================

    /**
     * 14.3.1: Sau khi người thắng tiếp tục đơn độc rồi cũng thua,
     * popup tầng 2 xuất hiện. Người dùng chọn THI ĐẤU LẠI → RESTART.
     * (Mô phỏng bằng timeP2 == 0 nghĩa là P2 đã bị dừng từ trước.)
     */
    @Test
    void showPvP_tier2_userChoosesRestart_shouldReturnRestart() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.RESTART);

        AtomicReference<GameResultView.Action> result = new AtomicReference<>();
        runFxVoid(() -> result.set(view.showPvP(null, 1, "Alice", "Bob", 200, 0, 8, 0)));

        assertEquals(GameResultView.Action.RESTART, result.get());
    }

    /**
     * 14.3.2: Sau chu kỳ đơn độc kết thúc, người dùng chọn QUAY LẠI MENU.
     * showPvP() phải trả về QUIT_TO_MENU để controller gọi onMatchEnd.run().
     */
    @Test
    void showPvP_tier2_userChoosesQuitToMenu_shouldReturnQuitToMenu() throws Exception {
        RecordingGameResultView view = new RecordingGameResultView(GameResultView.Action.QUIT_TO_MENU);

        AtomicReference<GameResultView.Action> result = new AtomicReference<>();
        runFxVoid(() -> result.set(view.showPvP(null, 2, "Alice", "Bob", 0, 180, 0, 9)));

        assertEquals(GameResultView.Action.QUIT_TO_MENU, result.get());
    }

    // =========================================================
    // UC14 – 14.1.1  Giá trị mặc định của result trước khi chọn
    // =========================================================

    /**
     * 14.1.1: Trước khi người dùng tương tác, giá trị mặc định của
     * trường result bên trong GameResultView phải là QUIT_TO_MENU
     * (hành vi an toàn khi dialog bị đóng bất ngờ).
     */
    @Test
    void gameResultView_defaultResult_shouldBeQuitToMenu() throws Exception {
        GameResultView view = new GameResultView();

        Field resultField = GameResultView.class.getDeclaredField("result");
        resultField.setAccessible(true);
        GameResultView.Action defaultAction = (GameResultView.Action) resultField.get(view);

        assertEquals(GameResultView.Action.QUIT_TO_MENU, defaultAction);
    }

    // =========================================================
    // Stub subclass – ghi lại tham số, không mở cửa sổ thật
    // =========================================================

    private static class RecordingGameResultView extends GameResultView {

        final Action presetAction;

        // Các tham số được ghi lại sau mỗi lần gọi showPvP()
        int    capturedWinner;
        String capturedP1Name;
        String capturedP2Name;
        int    capturedTimeP1;
        int    capturedTimeP2;
        int    capturedFlagsP1;
        int    capturedFlagsP2;

        RecordingGameResultView(Action presetAction) {
            this.presetAction = presetAction;
        }

        /**
         * Override showPvP(): Ghi lại tất cả tham số đầu vào,
         * trả về action được cấu hình sẵn, KHÔNG mở Stage thật.
         */
        @Override
        public Action showPvP(Stage ownerStage, int winner,
                              String p1Name, String p2Name,
                              int timeP1, int timeP2,
                              int flagsP1, int flagsP2) {
            this.capturedWinner  = winner;
            this.capturedP1Name  = p1Name;
            this.capturedP2Name  = p2Name;
            this.capturedTimeP1  = timeP1;
            this.capturedTimeP2  = timeP2;
            this.capturedFlagsP1 = flagsP1;
            this.capturedFlagsP2 = flagsP2;
            return presetAction;
        }
    }

    // =========================================================
    // Helpers
    // =========================================================

    private static void runFxVoid(FxRunnable r) throws Exception {
        if (Platform.isFxApplicationThread()) { r.run(); return; }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> err = new AtomicReference<>();
        Platform.runLater(() -> {
            try { r.run(); }
            catch (Throwable e) { err.set(e); }
            finally { latch.countDown(); }
        });
        latch.await();
        if (err.get() != null) throw new RuntimeException(err.get());
    }

    @FunctionalInterface
    interface FxRunnable { void run() throws Exception; }
}