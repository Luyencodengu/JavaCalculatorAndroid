# Java Calculator Android v2

Phiên bản nâng cấp giao diện và hiệu ứng.

## Điểm mới
- Thiết kế hiện đại hơn
- Nút bo tròn đẹp hơn
- Màn hình hiển thị nổi bật hơn
- Hiệu ứng nhấn nút
- Khi bấm "=":
  - biểu thức thu nhỏ và chuyển lên trên
  - kết quả trượt lên vùng hiển thị chính

## Mở project
1. Giải nén file ZIP.
2. Mở Android Studio.
3. Chọn **Open**.
4. Chọn thư mục `JavaCalculatorAndroid_v2`.
5. Đợi Gradle Sync hoàn tất.
6. Chạy trên máy ảo hoặc điện thoại thật.

## File chính
- `app/src/main/java/com/example/javacalculator/MainActivity.java`
- `app/src/main/res/layout/activity_main.xml`
- `app/src/main/res/drawable/*`

## v2.1 FIX
- Removed AndroidX AppCompat dependency.
- Uses standard Android Activity.
- Fixes `Duplicate class kotlin.*` errors seen on some Gradle environments.
- Keeps the redesigned UI and animations.

## v2.2 FIX
- Fixed Android resource linking after removing AppCompat.
- Theme attributes now use framework names:
  - `android:colorPrimary`
  - `android:colorPrimaryDark`
  - `android:colorAccent`
