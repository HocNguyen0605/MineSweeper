package com.minesweeper.model;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Duration;

/**
 * Đồng hồ đếm giây cho ván chơi Minesweeper.
 * <p>
 * Dùng JavaFX {@link Timeline} để đếm theo từng giây.
 * {@code elapsedSeconds} là {@link IntegerProperty} để HeaderView
 * có thể bind trực tiếp mà không cần Controller cập nhật thủ công
 * — Observer pattern.
 * </p>
 * <p>
 * Timer chỉ bắt đầu khi người chơi mở ô đầu tiên — FR-06, UR-12.
 * </p>
 */
public class GameTimer {

    // ── Fields ────────────────────────────────────────────────

    /**
     * Số giây đã trôi qua kể từ khi bắt đầu.
     * IntegerProperty để View bind — Observer pattern.
     */
    private final IntegerProperty elapsedSeconds = new SimpleIntegerProperty(0);

    /**
     * JavaFX Timeline chạy định kỳ mỗi 1 giây.
     */
    private Timeline timeline;

    // ── Constructor ───────────────────────────────────────────

    /**
     * Khởi tạo GameTimer.
     * Timeline được cấu hình nhưng chưa chạy cho đến khi gọi {@link #start()}.
     */
    public GameTimer() {
        // TODO: khởi tạo timeline với KeyFrame 1 giây,
        //       mỗi frame tăng elapsedSeconds lên 1
        //       timeline.setCycleCount(Timeline.INDEFINITE)
    }

    // ── Control ───────────────────────────────────────────────

    /**
     * Bắt đầu (hoặc tiếp tục) đếm giờ.
     * Gọi khi người chơi mở ô đầu tiên — FR-06, UR-12.
     */
    public void start() {
        // TODO: timeline.play()
    }

    /**
     * Tạm dừng đồng hồ, giữ nguyên số giây hiện tại.
     * FR-03
     */
    public void pause() {
        // TODO: timeline.pause()
    }

    /**
     * Tiếp tục đếm từ số giây đang giữ.
     * FR-04
     */
    public void resume() {
        // TODO: timeline.play()
    }

    /**
     * Dừng và đặt lại về 0.
     * Gọi khi bắt đầu ván mới — FR-01, FR-02.
     */
    public void reset() {
        // TODO: timeline.stop(), elapsedSeconds.set(0)
    }

    // ── Getters ───────────────────────────────────────────────

    /**
     * Trả về IntegerProperty để HeaderView bind — Observer pattern.
     * FR-06
     *
     * @return elapsedSeconds property
     */
    public IntegerProperty elapsedSecondsProperty() {
        return elapsedSeconds;
    }

    /** @return số giây hiện tại */
    public int getElapsedSeconds() {
        return elapsedSeconds.get();
    }
}