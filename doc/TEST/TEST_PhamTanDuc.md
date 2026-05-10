# Kế hoạch kiểm thử (Test Plan) - v2

**Phiên bản:** 2.0 | **Ngày:** 10/05/2026

Tài liệu này mô tả các kịch bản kiểm thử được thực hiện, bao gồm cả các test tự động (JUnit) và các test thủ công/quan
sát.

---

Thành viên kiểm thử: Phạm Tấn Đức
Thành viên bị kiểm thử: Trần Lê Công Hiêếu
Use case phụ trách: UC-1, UC-2, UC-3, UC-4

## I. KIỂM THỬ CHỨC NĂNG (FUNCTIONAL)

### A. Test Tự Động (Automated Tests - JUnit)

Các test case này được viết bằng JUnit và nằm trong thư mục `src/test/java`. Chúng kiểm tra logic cốt lõi của ứng dụng
một cách tự động.

| STT | UC        | Thuộc tính / Kịch bản cần kiểm tra                                                | Kết quả mong đợi (Tiêu chuẩn)                                                 | Phương thức Test                   | Trạng thái | Ghi chú                                                              |
|:----|:----------|:----------------------------------------------------------------------------------|:------------------------------------------------------------------------------|:-----------------------------------|:-----------|:---------------------------------------------------------------------|
| 1   | **UC-01** | Đặt cờ trên một ô chưa mở.                                                        | Ô chuyển sang trạng thái `FLAGGED`, bộ đếm cờ tăng lên 1.                     | `BoardTest.testPlaceFlag`          | Đạt        | -                                                                    |
| 2   | **UC-02** | Gỡ cờ khỏi một ô đã được cắm cờ.                                                  | Ô trở về trạng thái `HIDDEN`, bộ đếm cờ giảm đi 1.                            | `BoardTest.testRemoveFlag`         | Đạt        | -                                                                    |
| 3   | **UC-03** | Chording (an toàn) trên một ô số khi các mìn xung quanh đã được cắm cờ chính xác. | Các ô ẩn xung quanh được mở ra.                                               | `BoardTest.testChording_Safe`      | Đạt        | -                                                                    |
| 4   | **UC-03** | Chording (gây nổ) trên một ô số khi cắm cờ sai.                                   | Trò chơi kết thúc (thua), phương thức `chord` trả về `false`.                 | `BoardTest.testChording_Explosion` | Lỗi        | `AssertionFailedError`: Mong đợi `false` nhưng nhận về `true`.       |
| 5   | **UC-04** | Xem tổng số mìn.                                                                  | Phương thức `getTotalMines()` trả về đúng số mìn đã được cấu hình cho bàn cờ. | `BoardTest.testViewTotalMines`     | Đạt        | -                                                                    |

### B. Test Thủ Công (Manual Tests)

| STT | UC           | Thuộc tính / Kịch bản cần kiểm tra                 | Kết quả mong đợi (Tiêu chuẩn)                                                                       | Trạng thái (Đạt / Lỗi) |
|:----|:-------------|:---------------------------------------------------|:----------------------------------------------------------------------------------------------------|:----------------------:|
| 1   | **UC-01/02** | Quan sát bộ đếm mìn trên giao diện.                | Con số trên bộ đếm giảm khi cắm cờ và tăng khi hủy cờ.                                              |           ✅            |
| 2   | **UC-03**    | Double-click (hoặc click chuột giữa) vào một ô số. | Nếu các ô xung quanh đã được cắm cờ đủ, các ô ẩn còn lại sẽ được mở. Nếu không, không có gì xảy ra. |           ✅            |
| 3   | **UC-04**    | Quan sát bộ đếm mìn khi bắt đầu game.              | Bộ đếm hiển thị đúng tổng số mìn của màn chơi (ví dụ: 10 cho Beginner).                             |           ✅            |

---
