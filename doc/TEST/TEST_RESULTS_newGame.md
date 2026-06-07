# TÀI LIỆU BÁO CÁO KẾT QUẢ KIỂM THỬ (TEST REPORT)
## Use Case: UC-05 – New Game

---

## 1. TỔNG QUAN KẾT QUẢ KIỂM THỬ (TEST SUMMARY)

Tài liệu này ghi nhận kết quả thực hiện kiểm thử dành cho chức năng **UC-05: New Game (Bắt Đầu Trò Chơi Mới)**. Quá trình kiểm thử bao gồm việc xác thực luồng khởi tạo bàn cờ theo các cấp độ khó khác nhau trong chế độ chơi đơn, cũng như khả năng cấu hình và bắt đầu trận đấu trong chế độ PvP (chia đôi màn hình).

| Chỉ số kiểm thử (Metric) | Nội dung / Giá trị | Trạng thái (Status) |
| :--- | :--- | :---: |
| **Tổng số Test Case thuộc UC-05** | **6** | |
| **Số lượng vượt qua (Passed)** | **6 / 6** | **100% ✅** |
| **Số lượng thất bại (Failed)** | **0** | **0% ❌** |
| **Ngày thực hiện (Execution Date)** | Ngày 07 tháng 06 năm 2026 | |
| **Môi trường Kiểm Thử** | Môi trường hệ thống giao diện (JavaFX) | |

---

## 2. DANH SÁCH BẢNG CHI TIẾT CÁC TEST CASES

### 2.1. Kiểm Thử Giao Diện Và Logic Khởi Tạo Bàn Cờ (`GameController` / `PvPGameController`)

Tập trung xác thực logic khởi tạo kích thước bàn cờ, số lượng mìn dựa trên Enum `Difficulty`, quá trình chuyển đổi giao diện, và việc thiết lập thông số cho chế độ PvP Cục bộ (hai người chơi trên cùng một máy).

| Test Case ID | Thành phần kiểm thử | Tên Test / Mục tiêu kịch bản | Điều kiện đầu vào (Input / Setup) | Kết quả kỳ vọng (Expected Output) | Kết quả |
| :---: | :--- | :--- | :--- | :--- | :---: |
| **TC_05_01** | GameController / Board | Khởi tạo ván chơi mới - Chế độ Dễ (Easy) | - Người chơi ở giao diện Menu chính.<br>- Nhấn chọn mức độ khó "Easy". | - Giao diện Menu đóng lại.<br>- Khởi tạo đối tượng Board với cấu hình: 9x9 ô, 10 mìn.<br>- Trạng thái game (GameState) được set là `IDLE`.<br>- Hiển thị GameView với bàn cờ 9x9 và bộ đếm thời gian/mìn. | ✅ PASS |
| **TC_05_02** | GameController / Board | Khởi tạo ván chơi mới - Chế độ Trung bình (Medium) | - Người chơi ở giao diện Menu chính.<br>- Nhấn chọn mức độ khó "Medium". | - Giao diện Menu đóng lại.<br>- Khởi tạo đối tượng Board với cấu hình: 16x16 ô, 40 mìn.<br>- Trạng thái game (GameState) được set là `IDLE`.<br>- Hiển thị GameView với bàn cờ 16x16 và bộ đếm thời gian/mìn. | ✅ PASS |
| **TC_05_03** | GameController / Board | Khởi tạo ván chơi mới - Chế độ Khó (Hard) | - Người chơi ở giao diện Menu chính.<br>- Nhấn chọn mức độ khó "Hard". | - Giao diện Menu đóng lại.<br>- Khởi tạo đối tượng Board với cấu hình: 16x30 ô, 99 mìn.<br>- Trạng thái game (GameState) được set là `IDLE`.<br>- Hiển thị GameView với bàn cờ 16x30 và bộ đếm thời gian/mìn. | ✅ PASS |
| **TC_05_04** | GameController / PvPBoardView | Mở giao diện cấu hình PvP | - Người chơi ở giao diện Menu chính.<br>- Nhấn chọn nút "Chơi PvP Cục Bộ". | - Hệ thống hiển thị Pop-up/màn hình phụ cấu hình trận đấu PvP.<br>- Có các tùy chọn: Cấp độ khó, Tên Người chơi 1, Tên Người chơi 2. | ✅ PASS |
| **TC_05_05** | PvPController / Board | Khởi tạo trận PvP - Tên mặc định | - Tại màn hình cấu hình PvP, để trống tên 2 người chơi.<br>- Chọn cấp độ "Dễ".<br>- Nhấn "Bắt đầu đấu". | - Đóng màn hình menu/cấu hình.<br>- Tên người chơi được gán mặc định là "Player 1" và "Player 2".<br>- Hiển thị giao diện chia đôi màn hình.<br>- Khởi tạo 2 bàn cờ độc lập (9x9, 10 mìn).<br>- Gán đúng phím điều khiển (P1: WASD+Space+F, P2: Mũi tên+Enter+P).<br>- Trạng thái game chuyển sang `PVP_SPLIT_START`.<br>- 2 đồng hồ đếm giờ bắt đầu chạy. | ✅ PASS |
| **TC_05_06** | PvPController / Board | Khởi tạo trận PvP - Tên tự định nghĩa | - Tại màn hình cấu hình PvP, nhập Tên 1: "Alice", Tên 2: "Bob".<br>- Chọn cấp độ "Trung bình".<br>- Nhấn "Bắt đầu đấu". | - Đóng màn hình menu/cấu hình.<br>- Tên người chơi hiển thị trên giao diện là "Alice" (trái) và "Bob" (phải).<br>- Hiển thị giao diện chia đôi màn hình.<br>- Khởi tạo 2 bàn cờ độc lập (16x16, 40 mìn).<br>- Gán đúng phím điều khiển như thiết kế.<br>- Trạng thái game chuyển sang `PVP_SPLIT_START`. | ✅ PASS |

---

## 3. THỐNG KÊ MỨC ĐỘ PHỦ THEO ĐẶC TẢ USE CASE (USE CASE COVERAGE)

Hệ thống mã kiểm thử đã bao phủ trọn vẹn tất cả các kịch bản quan trọng được mô tả trong tài liệu đặc tả chức năng:

* **Luồng cơ bản (Standard Flow: Khởi tạo chơi đơn):** Được kiểm thử toàn diện qua nhóm **TC_05_01, TC_05_02, TC_05_03**. Đảm bảo game tiếp nhận đúng thông số (số hàng, số cột, số lượng mìn) tương ứng với từng cấp độ được người chơi chọn.
* **Luồng thay thế 1 (Alternative Flow 1 - Xử lý chế độ PvP):** Được phủ bởi **TC_05_04, TC_05_05, TC_05_06**. Xác nhận hệ thống có khả năng chuyển đổi giao diện, chia đôi màn hình, khởi tạo bàn cờ kép với các Key Listeners độc lập cho phép 2 người chơi tương tác cùng lúc.

---
**Kết luận:** Hệ thống khởi tạo màn chơi mới (bao gồm cả chơi đơn và đối kháng cục bộ) thuộc **UC-05: New Game** hoạt động hoàn toàn chính xác theo yêu cầu thiết kế, sẵn sàng tích hợp với các Use Case về xử lý logic chơi game.
