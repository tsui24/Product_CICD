# Hướng dẫn thiết lập Database MySQL trên AWS

Để sử dụng MySQL cho ứng dụng của bạn trên AWS, có 2 cách phổ biến:

## Cách 1: Sử dụng AWS RDS (Khuyên dùng)
Đây là dịch vụ cơ sở dữ liệu được quản lý bởi AWS, giúp bạn không cần lo lắng về cài đặt, backup hay bảo trì.

### Các bước tạo RDS Instance:
1.  Tìm kiếm **"RDS"** trên AWS Console và chọn **Create database**.
2.  **Choose a database creation method**: Chọn **Standard create**.
3.  **Engine options**: Chọn **MySQL**.
4.  **Templates**: Chọn **Free Tier** (để không bị mất phí).
5.  **Settings**:
    *   **DB instance identifier**: `product-db-instance`.
    *   **Master username**: `admin` (hoặc tên bạn muốn).
    *   **Master password**: Nhập mật khẩu của bạn (Lưu lại để dùng sau).
6.  **Connectivity**:
    *   **Public access**: Chọn **Yes** (Để ứng dụng từ EC2 hoặc máy bạn có thể kết nối được, nhưng cần cấu hình Security Group cẩn thận).
    *   **VPC security group**: Chọn **Create new** và đặt tên là `rds-sg`.
7.  **Additional configuration**:
    *   **Initial database name**: Nhập `productdb` (Trùng với cấu hình trong code).
8.  Nhấn **Create database**.

### Cấu hình Security Group cho RDS:
Sau khi tạo xong, bạn cần cho phép EC2 truy cập vào RDS:
1.  Vào mục **Connectivity & security** của database vừa tạo.
2.  Nhấn vào Security Group (`rds-sg`).
3.  Thêm **Inbound rule**: Type `MYSQL/Aurora` (Port 3306), Source là Security Group của EC2 (`spring-boot-sg`).

---

## Cách 2: Chạy MySQL bằng Docker trên EC2 (Đơn giản cho Testing)
Nếu bạn không muốn dùng RDS, bạn có thể chạy MySQL ngay trên chính con EC2 đó bằng Docker.

### Lệnh chạy MySQL trên EC2:
```bash
docker run -d \
  --name mysql-db \
  -p 3306:3306 \
  -e MYSQL_DATABASE=productdb \
  -e MYSQL_ROOT_PASSWORD=your_password \
  --restart always \
  mysql:8.0
```

### Cập nhật GitHub Secrets:
Sau khi có Database (RDS hoặc Docker), bạn hãy cập nhật các biến sau vào **GitHub Secrets**:
- `DB_URL`: `jdbc:mysql://<ENDPOINT_RDS>:3306/productdb` (hoặc `jdbc:mysql://localhost:3306/productdb` nếu chạy Docker trên cùng EC2).
- `DB_USER`: `admin` (hoặc root).
- `DB_PASSWORD`: Mật khẩu bạn đã thiết lập.

---

### Lưu ý về `cicd.yml`:
Nếu bạn chọn chạy MySQL trên cùng EC2 bằng Docker và muốn app kết nối vào, trong lệnh `docker run` của file `cicd.yml`, bạn nên thêm `--network="host"` để app có thể nhận diện `localhost` là chính cái server EC2 đó:

```yaml
docker run -d --name my-app \
  --network="host" \
  -e SPRING_DATASOURCE_URL=${{ secrets.DB_URL }} \
  ...
```
