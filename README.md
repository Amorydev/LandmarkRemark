# Tigerspike - Landmark Remark Android App
## Giới thiệu:
Là ứng dụng đơn giản được xây dựng dựa trên yêu cầu của dự án Tigerspike. Người dùng có thể ghim/đánh dấu vị trí hiện tại của mình lên bản đồ (thêm tiêu đề, mô tả cho vị trí). Các mốc vị trí đánh dấu được đồng bộ hóa trên Firebase.
## Thiết kế
+ Ứng dụng được viết bằng ngôn ngữ Kotlin, không sử dụng bất kì thư viện nào của bên thứ ba. Với Backend-as-a-service (Baas), tôi sử dụng[ Google Firebase](https://firebase.google.com) (vì được sử dụng miễn phí).
+ Mô hình ứng dụng (Architecture pattern): Ứng dùng sử dụng mô hình MVVM (Model - View - ViewModel).
+ Các file chính:
```
1. MainActivity.kt, Sign.kt, Register.kt là các activity chính của ứng dụng.
2. MainViewModel.kt, SignViewModel.kt, RegisterViewModel.kt là các ViewModel.
3. LandMark.kt là Model của ứng dụng,
4. CustomShowMark.kt là adapter để custom lại view hiển thị chi tiết các vị trí đã đánh dấu
(bao gồm tiêu đề, mô tả và chủ chia sẻ)
```
## Ý tưởng
+ Tích hợp Firebase Authentication để thực hiện chức năng đăng kí đăng nhập.
+ Sử dụng Firebase FireStore để lưu trữ lại các giá trị của vị trí như là danh sách vị trí đã đánh dấu. Ở đây chúng ta lấy theo kinh độ và vĩ độ. Tạo 1 database bao gồm các userID, bên trong userId chứa các locationId gồm các thông tin của vị trí như là tiêu đề, mô tả, kinh độ, vĩ độ.
## Công nghệ sử dụng
+ Kotlin
+ MVVM Architecture Pattern
+ Kotlin Coroutines
+ LiveData
+ ViewBinding
+ Firebase

## Kiểm thử
+ Phương pháp cài đặt:
```
- Download mã nguồn về máy.
- Mở mã nguồn trên Android Studio.
- Bật chức năng gỡ lỗi USB và khởi chạy ứng dụng.
- Khi khởi chạy app, ứng dụng yêu cầu quyền truy cập. Vui lòng cấp truy cập trước khi sử dụng.
```
+ Tính năng chính:
```
- Ứng dụng sử dụng Firebase Authentication để thực hiện đăng nhập/ đăng kí/ đăng xuất.
- Người dùng có thể xem vị trí hiện tại của mình 1 cách chính xác. Và xem vị trí mà người khác
đã chia sẻ khi dùng ứng dụng (bao gồm tiêu đề, mô tả và tên người chia sẻ)
- Người dùng có thể đánh dấu lại vị trí của mình lên bản đồ và xem đánh dấu của những người khác.
- Người dùng có thể tìm kiếm các vị trí mình đã đánh dấu dựa trên tiêu đề đã sử dụng.
- Thực hiện zoom in hoặc zoom out trên bản đồ.
- Bản đồ có 4 kiểu để lựa chọn: địa hình, đường phố, vệ tinh, hỗn hợp.
```
+ Hạn chế ứng dụng:
```
- Ứng dụng không khả dụng ở các quốc gia bị dịch vụ Google chặn hoặc hạn chế,
- Ứng dụng phụ thuộc vào quyền truy cập vị trí để sử dụng.
- Vì thời gian có hạn nên tôi chưa thể thực hiện thêm chức năng xóa hoặc chỉnh sửa đánh dấu. 
Nhưng việc này khá là đơn giản.
```
## Thời gian thực hiện từng thành phần
+ Tạo bản đồ và đồng bộ hóa Firebase - 2hr
+ Tạo chức năng đăng kí đăng nhập - 2h
+ Cấp quyền và lấy ra vị trí hiện tại - 2.5hr
+ Thêm đánh dấu vị trí và hiển thị tất cả đánh dấu - 3hr
+ Tìm kiếm đánh dấu của mình theo tiêu đề - 1.5hr
+ Kiểm tra lỗi, tối ưu code - 2hr

