# Hướng dẫn Deploy lên AWS EC2 qua GitHub Actions

Project của bạn hiện đã có một luồng CI/CD khá đầy đủ trong file `cicd.yml`. Để deploy lên AWS EC2, bạn chỉ cần thực hiện các bước chuẩn bị trên server và cấu hình lại một chút các biến môi trường.

## 1. Chuẩn bị AWS EC2
1.  **Tạo Instance**: Khuyên dùng Ubuntu 22.04 LTS.
2.  **Cấu hình Security Group**:
    -   Mở **Port 22 (SSH)** để truy cập server.
    -   Mở **Port 8080 (HTTP)** để truy cập vào ứng dụng backend.
3.  **Cài đặt Docker trên EC2**:
    Chạy các lệnh sau trên terminal của EC2:
    ```bash
    sudo apt-get update
    sudo apt-get install -y docker.io
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker $USER
    # Sau đó logout và login lại để quyền docker có hiệu lực
    ```

## 2. Cấu hình GitHub Secrets
Bạn cần vào **Settings -> Secrets and variables -> Actions** trên GitHub repo của bạn và thêm các biến sau:
- `DOCKER_USERNAME`: Username Docker Hub của bạn.
- `DOCKER_PASSWORD`: Password hoặc Access Token của Docker Hub.
- `SERVER_IP`: Địa chỉ IP Public của EC2.
- `SERVER_USER`: Thường là `ubuntu` (nếu dùng image Ubuntu).
- `SSH_PRIVATE_KEY`: Nội dung file `.pem` (Private Key) bạn dùng để kết nối EC2.

## 3. Cập nhật mã nguồn (Khuyến nghị)
Để linh hoạt hơn trong việc kết nối Database (ví dụ dùng AWS RDS), bạn nên cập nhật `application.properties` để nhận các thông số từ biến môi trường:

### `src/main/resources/application.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/productdb}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:root}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:Nam@0246}
```

### Cập nhật lệnh chạy trong `cicd.yml`:
Trong bước `Deploy to Server via SSH`, bạn có thể truyền các biến môi trường vào container:
```yaml
      - name: Deploy to Server via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_IP }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            docker pull ${{ secrets.DOCKER_USERNAME }}/product-cicd:latest
            docker stop my-app || true
            docker rm my-app || true
            docker run -d --name my-app \
              -p 8080:8080 \
              -e SPRING_DATASOURCE_URL=${{ secrets.DB_URL }} \
              -e SPRING_DATASOURCE_USERNAME=${{ secrets.DB_USER }} \
              -e SPRING_DATASOURCE_PASSWORD=${{ secrets.DB_PASSWORD }} \
              ${{ secrets.DOCKER_USERNAME }}/product-cicd:latest
```

## 4. Quản lý Database
- **Cách 1**: Cài MySQL trực tiếp trên EC2 (Đơn giản nhất cho demo).
- **Cách 2**: Sử dụng **AWS RDS** (Khuyên dùng cho thực tế). Khi đó bạn chỉ cần thay đổi biến `DB_URL` trong GitHub Secrets trỏ tới endpoint của RDS.
