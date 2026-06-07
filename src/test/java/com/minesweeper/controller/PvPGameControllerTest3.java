package com.minesweeper.controller;

import com.minesweeper.model.Board;
import com.minesweeper.model.GameState;
import com.minesweeper.model.PvPRequestType;
import com.minesweeper.view.GameResultView;
import com.minesweeper.view.PvPSetupDialog;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC13 – CHECK GAME RESULT
 * Kiểm tra toàn bộ logic phân định kết quả trong PvPGameController:
 *   - Luồng cơ bản  : P1/P2 dẫm mìn khi còn 2 người → chuyển chế độ đơn độc
 *   - Luồng đơn độc : người còn lại dẫm mìn tiếp hoặc thắng
 *   - Alt-flow      : cả hai dẫm mìn cùng lúc (DRAW), một bên thắng bình thường
 */
public class PvPGameControllerTest3 {

    @BeforeAll
    static void startJavaFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
            latch.await();
        } catch (IllegalStateException ignored) { }
    }

    // =========================================================
    // UC13 – 13.1.1 → 13.1.4  (P1 dẫm mìn, chuyển đơn độc)
    // =========================================================

    /**
     * 13.1.1 + 13.1.2 + 13.1.3:
     * Khi P1 lật trúng mìn (activePlayersCount == 2),
     * hệ thống phải set stateP1 = LOSE, giảm activePlayersCount xuống 1,
     * dừng đồng hồ P1, và gán cờ đàm phán PAUSE (chờ P2 quyết định).
     */
    @Test
    void checkMatchResult_p1HitsMine_activeTwo_shouldSetP1LoseDecrement() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.checkMatchResult(true, false, 0, 0));

        // 13.1.1 – trạng thái thua cuộc gán cho P1
        assertEquals(GameState.LOSE, getState(ctrl, "stateP1"));
        // 13.1.2 – P2 vẫn đang chơi
        assertEquals(GameState.PLAYING, getState(ctrl, "stateP2"));
        // 13.1.2 – số người chơi chủ động giảm xuống 1
        assertEquals(1, getInt(ctrl, "activePlayersCount"));
    }

    /**
     * 13.1.3 – Khi P1 dẫm mìn, hệ thống phải gán pendingRequest = PAUSE
     * và waitingConfirmation = true để mở màn đàm phán với P2.
     */
    @Test
    void checkMatchResult_p1HitsMine_shouldSetPauseNegotiationFlags() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.checkMatchResult(true, false, 0, 0));

        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.PAUSE, getRequestType(ctrl, "pendingRequest"));
        // requestingPlayer phải là 1 (P1 là bên "yêu cầu" vì P1 đã thua)
        assertEquals(1, getInt(ctrl, "requestingPlayer"));
    }

    /**
     * 13.1.1 + 13.1.2 (đối xứng):
     * Khi P2 lật trúng mìn, stateP2 = LOSE, stateP1 giữ PLAYING.
     */
    @Test
    void checkMatchResult_p2HitsMine_activeTwo_shouldSetP2LoseDecrement() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.checkMatchResult(false, false, 0, 0));

        assertEquals(GameState.LOSE, getState(ctrl, "stateP2"));
        assertEquals(GameState.PLAYING, getState(ctrl, "stateP1"));
        assertEquals(1, getInt(ctrl, "activePlayersCount"));
    }

    /**
     * 13.1.3 (đối xứng):
     * Khi P2 dẫm mìn, requestingPlayer = 2.
     */
    @Test
    void checkMatchResult_p2HitsMine_shouldSetPauseNegotiationFlagsForP2() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.checkMatchResult(false, false, 0, 0));

        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.PAUSE, getRequestType(ctrl, "pendingRequest"));
        assertEquals(2, getInt(ctrl, "requestingPlayer"));
    }

    // =========================================================
    // UC13 – 13.1.6 → 13.1.7 (chế độ đơn độc – P1 dẫm mìn lần 2)
    // =========================================================

    /**
     * 13.1.6 + 13.1.7:
     * Khi activePlayersCount == 1 và P1 đang PLAYING rồi dẫm mìn,
     * hệ thống phải set stateP1 = LOSE và gán pendingRequest = RESET.
     */
    @Test
    void checkMatchResult_singleMode_p1HitsMine_shouldSetResetNegotiation() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        // Thiết lập chế độ đơn độc: P2 đã thua trước đó
        setField(ctrl, "activePlayersCount", 1);
        setField(ctrl, "stateP1", GameState.PLAYING);
        setField(ctrl, "stateP2", GameState.LOSE);

        runFxVoid(() -> ctrl.checkMatchResult(true, false, 0, 0));

        // 13.1.6 – xác nhận thua cuộc lần hai
        assertEquals(GameState.LOSE, getState(ctrl, "stateP1"));
        // 13.1.7 – gán cờ RESET
        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.RESET, getRequestType(ctrl, "pendingRequest"));
    }

    /**
     * 13.1.6 + 13.1.7 (đối xứng):
     * P2 đơn độc dẫm mìn lần 2 → RESET negotiation.
     */
    @Test
    void checkMatchResult_singleMode_p2HitsMine_shouldSetResetNegotiation() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        setField(ctrl, "activePlayersCount", 1);
        setField(ctrl, "stateP1", GameState.LOSE);
        setField(ctrl, "stateP2", GameState.PLAYING);

        runFxVoid(() -> ctrl.checkMatchResult(false, false, 0, 0));

        assertEquals(GameState.LOSE, getState(ctrl, "stateP2"));
        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.RESET, getRequestType(ctrl, "pendingRequest"));
    }

    // =========================================================
    // UC13 – Alt-flow: Cả hai dẫm mìn cùng lúc → DRAW (finishMatch(0))
    // =========================================================

    /**
     * 13.2.x (DRAW):
     * Khi P1 dẫm mìn và stateP2 cũng đã là LOSE,
     * hệ thống phải gọi finishMatch(0) → callback onMatchEnd chạy.
     */
    @Test
    void checkMatchResult_bothHitMine_shouldTriggerDraw() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        ctrl.setOnMatchEnd(() -> callbackCalled.set(true));

        // P2 đã thua trước rồi
        setField(ctrl, "stateP2", GameState.LOSE);

        runFxVoid(() -> ctrl.checkMatchResult(true, false, 0, 0));

        assertTrue(callbackCalled.get());
    }

    // =========================================================
    // UC13 – P1 / P2 thắng bình thường (checkWin)
    // =========================================================

    /**
     * Khi P1 lật ô an toàn và boardP1.checkWin() == true,
     * stateP1 = WIN, stateP2 = LOSE, onMatchEnd được gọi (QUIT flow).
     */
    @Test
    void checkMatchResult_p1WinsNormally_shouldCallFinishMatch() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        ctrl.setOnMatchEnd(() -> callbackCalled.set(true));

        // Reveal tất cả ô an toàn của boardP1 để checkWin() == true
        Board boardP1 = getBoard(ctrl, "boardP1");
        runFxVoid(() -> {
            int rows = boardP1.getRows();
            int cols = boardP1.getCols();
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (!boardP1.getCell(r, c).isMine())
                        boardP1.revealCell(r, c);
            ctrl.checkMatchResult(true, true, 0, 0);
        });

        // Với QUIT_TO_MENU action → callback onMatchEnd phải được gọi
        assertTrue(callbackCalled.get());
    }

    /**
     * Khi P2 thắng bình thường, stateP2 = WIN, stateP1 = LOSE.
     */
    @Test
    void checkMatchResult_p2WinsNormally_shouldCallFinishMatch() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        ctrl.setOnMatchEnd(() -> callbackCalled.set(true));

        Board boardP2 = getBoard(ctrl, "boardP2");
        runFxVoid(() -> {
            int rows = boardP2.getRows();
            int cols = boardP2.getCols();
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    if (!boardP2.getCell(r, c).isMine())
                        boardP2.revealCell(r, c);
            ctrl.checkMatchResult(false, true, 0, 0);
        });

        assertTrue(callbackCalled.get());
    }

    // =========================================================
    // showIntermediateOverlay() – 13.1.4
    // =========================================================

    /**
     * 13.1.4: Khi P1 thắng lượt đầu, overlay phải gán requestingPlayer = 2
     * (P2 là bên vừa thua → P2 đứng ở vị trí "requester" trong đàm phán).
     */
    @Test
    void showIntermediateOverlay_winnerP1_requestingPlayerShouldBeP2() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.showIntermediateOverlay(1));

        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.PAUSE, getRequestType(ctrl, "pendingRequest"));
        assertEquals(2, getInt(ctrl, "requestingPlayer"));
    }

    /**
     * 13.1.4 (đối xứng): P2 thắng lượt đầu → requestingPlayer = 1.
     */
    @Test
    void showIntermediateOverlay_winnerP2_requestingPlayerShouldBeP1() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.showIntermediateOverlay(2));

        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.PAUSE, getRequestType(ctrl, "pendingRequest"));
        assertEquals(1, getInt(ctrl, "requestingPlayer"));
    }

    // =========================================================
    // showSinglePlayerLoseOverlay() – 13.1.7
    // =========================================================

    /**
     * 13.1.7: Sau khi P1 thua đơn độc, overlay phải gán RESET và requestingPlayer = 1.
     */
    @Test
    void showSinglePlayerLoseOverlay_p1_shouldSetResetAndRequestingP1() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.showSinglePlayerLoseOverlay(1));

        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.RESET, getRequestType(ctrl, "pendingRequest"));
        assertEquals(1, getInt(ctrl, "requestingPlayer"));
    }

    /**
     * 13.1.7 (đối xứng): P2 thua đơn độc → RESET, requestingPlayer = 2.
     */
    @Test
    void showSinglePlayerLoseOverlay_p2_shouldSetResetAndRequestingP2() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        runFxVoid(() -> ctrl.showSinglePlayerLoseOverlay(2));

        assertTrue(getBool(ctrl, "waitingConfirmation"));
        assertEquals(PvPRequestType.RESET, getRequestType(ctrl, "pendingRequest"));
        assertEquals(2, getInt(ctrl, "requestingPlayer"));
    }

    // =========================================================
    // finishMatch() – 13.2.1 (Restart) / 13.3.2 (Quit)
    // =========================================================

    /**
     * 13.2.1 (AltF1): Người dùng chọn RESTART sau finishMatch() →
     * initMatch() phải được gọi: boardP1 mới, activePlayersCount = 2,
     * trạng thái về PVP_SPLIT_START, p1Started/p2Started = false.
     */
    @Test
    void finishMatch_restart_shouldReinitializeMatch() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.RESTART);

        Board oldBoard = getBoard(ctrl, "boardP1");

        setField(ctrl, "activePlayersCount", 1);
        setField(ctrl, "stateP1", GameState.WIN);
        setField(ctrl, "stateP2", GameState.LOSE);
        setField(ctrl, "p1Started", true);
        setField(ctrl, "p2Started", true);

        runFxVoid(() -> ctrl.finishMatch(1));

        Board newBoard = getBoard(ctrl, "boardP1");

        assertNotSame(oldBoard, newBoard, "boardP1 phải là thực thể mới sau initMatch()");
        assertEquals(2, getInt(ctrl, "activePlayersCount"));
        assertEquals(GameState.PVP_SPLIT_START, getState(ctrl, "stateP1"));
        assertEquals(GameState.PVP_SPLIT_START, getState(ctrl, "stateP2"));
        assertFalse(getBool(ctrl, "p1Started"));
        assertFalse(getBool(ctrl, "p2Started"));
    }

    /**
     * 13.3.2 (AltF2): Người dùng chọn QUIT_TO_MENU sau finishMatch() →
     * callback onMatchEnd phải được gọi.
     */
    @Test
    void finishMatch_quitToMenu_shouldCallOnMatchEndCallback() throws Exception {
        TestablePvPGameController ctrl = createController(GameResultView.Action.QUIT_TO_MENU);

        AtomicBoolean callbackCalled = new AtomicBoolean(false);
        ctrl.setOnMatchEnd(() -> callbackCalled.set(true));

        runFxVoid(() -> ctrl.finishMatch(2));

        assertTrue(callbackCalled.get(), "onMatchEnd callback phải được gọi khi QUIT_TO_MENU");
    }

    // =========================================================
    // Testable subclass
    // =========================================================

    private static class TestablePvPGameController extends PvPGameController {
        private final GameResultView.Action fakeAction;

        TestablePvPGameController(PvPSetupDialog.Config config, GameResultView.Action action) {
            super(config);
            this.fakeAction = action;
        }

        @Override
        protected GameResultView createGameResultView() {
            return new FakeGameResultView(fakeAction);
        }
    }

    private static class FakeGameResultView extends GameResultView {
        private final Action action;

        FakeGameResultView(Action action) { this.action = action; }

        @Override
        public Action showPvP(Stage ownerStage, int winner,
                              String p1Name, String p2Name,
                              int timeP1, int timeP2,
                              int flagsP1, int flagsP2) {
            return action;
        }
    }

    // =========================================================
    // Helpers
    // =========================================================

    private static TestablePvPGameController createController(GameResultView.Action action) throws Exception {
        return runFx(() -> {
            Constructor<PvPSetupDialog.Config> ctor =
                    PvPSetupDialog.Config.class.getDeclaredConstructor(
                            Difficulty.class, String.class, String.class);
            ctor.setAccessible(true);
            PvPSetupDialog.Config config = ctor.newInstance(Difficulty.EASY, "Player 1", "Player 2");
            TestablePvPGameController ctrl = new TestablePvPGameController(config, action);
            new Scene(ctrl.getPvPBoardView().getRoot());
            return ctrl;
        });
    }

    private static Field field(String name) throws Exception {
        Field f = PvPGameController.class.getDeclaredField(name);
        f.setAccessible(true);
        return f;
    }

    private static void setField(Object t, String name, Object val) throws Exception {
        field(name).set(t, val);
    }

    private static int getInt(Object t, String name) throws Exception {
        return field(name).getInt(t);
    }

    private static boolean getBool(Object t, String name) throws Exception {
        return field(name).getBoolean(t);
    }

    private static GameState getState(Object t, String name) throws Exception {
        return (GameState) field(name).get(t);
    }

    private static PvPRequestType getRequestType(Object t, String name) throws Exception {
        return (PvPRequestType) field(name).get(t);
    }

    private static Board getBoard(Object t, String name) throws Exception {
        return (Board) field(name).get(t);
    }

    private static <T> T runFx(FxCallable<T> c) throws Exception {
        if (Platform.isFxApplicationThread()) return c.call();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<T> res = new AtomicReference<>();
        AtomicReference<Throwable> err = new AtomicReference<>();
        Platform.runLater(() -> {
            try { res.set(c.call()); }
            catch (Throwable e) { err.set(e); }
            finally { latch.countDown(); }
        });
        latch.await();
        if (err.get() != null) throw new RuntimeException(err.get());
        return res.get();
    }

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

    @FunctionalInterface interface FxCallable<T> { T call() throws Exception; }
    @FunctionalInterface interface FxRunnable { void run() throws Exception; }
}