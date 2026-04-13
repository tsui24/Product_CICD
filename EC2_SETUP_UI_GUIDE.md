# Hướng dẫn chi tiết tạo AWS EC2 Instance qua Giao diện Console

Dưới đây là các bước chi tiết để bạn tạo một máy ảo (EC2) trên AWS để chạy ứng dụng Spring Boot của mình.

## Bước 1: Truy cập dịch vụ EC2
1.  Đăng nhập vào [AWS Management Console](https://aws.amazon.com/console/).
2.  Tại ô tìm kiếm ở trên cùng, gõ **"EC2"** và chọn kết quả đầu tiên.
3.  Ở menu bên trái, chọn **Instances**.
4.  Nhấn nút màu cam **Launch instances** ở góc trên bên phải.

## Bước 2: Thiết lập thông tin cơ bản (Name and tags)
*   **Name**: Nhập tên cho server của bạn (Ví dụ: `Product-Backend-Server`).

## Bước 3: Chọn Hệ điều hành (Application and OS Images - AMI)
*   Tại mục **Quick Start**, chọn **Ubuntu**.
*   **Amazon Machine Image (AMI)**: Chọn bản **Ubuntu Server 22.04 LTS (HVM)** (được đánh dấu là *Free tier eligible* để tránh mất phí).
*   **Architecture**: Giữ nguyên `64-bit (x86)`.

## Bước 4: Chọn cấu hình phần cứng (Instance type)
*   **Instance type**: Chọn **t2.micro** (hoặc **t3.micro** tùy vùng). Đây là cấu hình nằm trong gói miễn phí (Free Tier), đủ để chạy ứng dụng nhỏ.

## Bước 5: Tạo khóa bảo mật (Key pair - login)
*   Nếu bạn chưa có Key pair:
    1.  Nhấn **Create new key pair**.
    2.  **Key pair name**: Gõ tên bất kỳ (Ví dụ: `my-ec2-key`).
    3.  **Key pair type**: `RSA`.
    4.  **Private key file format**: Chọn `.pem` (phù hợp cho OpenSSH/GitHub Actions).
    5.  Nhấn **Create key pair** và **LƯU FILE NÀY CẨN THẬN**. Bạn sẽ cần nội dung file này để bỏ vào GitHub Secrets (`SSH_PRIVATE_KEY`).

## Bước 6: Cấu hình Mạng (Network settings)
*   Nhấn **Edit** ở góc phải mục Network settings.
*   **Auto-assign public IP**: Đảm bảo là `Enable`.
*   **Firewall (Security Groups)**: 
    1.  Chọn **Create security group**.
    2.  **Security group name**: `spring-boot-sg`.
    3.  **Inbound Security Groups Rules**:
        *   **Rule 1 (SSH)**: Type `ssh`, Port `22`, Source `Anywhere` hoặc `My IP`. (Dùng để GitHub Actions remote vào).
        *   Nhấn **Add security group rule**.
        *   **Rule 2 (HTTP)**: Type `Custom TCP`, Port Range `8080`, Source `Anywhere (0.0.0.0/0)`. (Để mọi người truy cập vào app của bạn).

## Bước 7: Cấu hình lưu trữ (Configure storage)
*   Mặc định là **8 GiB gp3**. Bạn có thể tăng lên `20 GiB` hoặc `30 GiB` (mức tối đa miễn phí của gói Free Tier) để thoải mái lưu Docker images.

## Bước 8: Hoàn tất (Summary)
1.  Xem lại bảng tóm tắt ở bên phải.
2.  Nhấn nút **Launch instance**.
3.  Đợi khoảng 1-2 phút cho đến khi cột **Instance state** chuyển sang màu xanh **Running**.

---

### Sau khi hoàn tất:
*   Bạn lấy **Public IPv4 address** trong bảng Instances để điền vào Secret `SERVER_IP` trên GitHub.
*   Bạn mở file `.pem` đã tải ở bước 5 bằng Notepad, copy toàn bộ nội dung và điền vào Secret `SSH_PRIVATE_KEY` trên GitHub.
