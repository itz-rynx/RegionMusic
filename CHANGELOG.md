# Changelog / Nhật ký thay đổi

All notable changes to RegionMusic will be documented in this file.

Tất cả các thay đổi đáng chú ý của RegionMusic sẽ được ghi lại trong file này.

---

## [1.3] - 2024

### Added / Thêm mới
- ✨ **New Alias**: Thêm alias `rm` cho command `/regionmusic`
  - Người chơi có thể sử dụng `/rm` thay vì `/regionmusic` để tiện lợi hơn
  - Ví dụ: `/rm reload`, `/rm about`, `/rm playmusic`

---

## [1.2] - 2024

### Added / Thêm mới
- ✨ **New Command**: `/regionmusic nextsong` - Chuyển sang bài nhạc tiếp theo trong khu vực hiện tại
  - Cho phép người chơi skip bài nhạc đang phát và chuyển sang bài tiếp theo
  - Tự động loop về bài đầu nếu đang ở bài cuối
  - Chỉ hoạt động khi đang ở trong region có nhạc

### Changed / Thay đổi
- 📝 **File Rename**: Đổi tên file `msg.yml` thành `lang.yml` để dễ quản lý hơn
- 🔧 **Default Toggle**: Togglemusic mặc định là **BẬT** cho tất cả người chơi mới
- ⚡ **Performance**: Cải thiện hiệu năng và tối ưu hóa code

### Fixed / Sửa lỗi
- 🐛 **Fixed**: Sửa lỗi nhạc bị spam/double khi vào region có nhiều nhạc
- 🐛 **Fixed**: Sửa lỗi nhiều bài nhạc phát cùng lúc thay vì tuần tự
- 🐛 **Fixed**: Cải thiện logic kiểm tra task để tránh race condition

### Technical / Kỹ thuật
- 🔨 **Code**: Cải thiện MusicManager để đảm bảo chỉ một task đang chạy
- 🔨 **Code**: Thêm kiểm tra region đang phát để tránh phát lại không cần thiết
- 🔨 **Code**: Tăng delay để đảm bảo task cũ được hủy hoàn toàn

---

## [1.1] - 2024

### Added / Thêm mới
- 🎵 **Multiple Songs**: Hỗ trợ nhiều nhạc trong 1 region với phát tuần tự
- 🔂 **Auto Loop**: Tự động quay lại bài đầu sau khi phát hết playlist
- 📝 **Message System**: Thêm hệ thống quản lý messages qua file `msg.yml`

### Changed / Thay đổi
- 🔄 **Playback**: Thay đổi từ phát một nhạc sang hỗ trợ playlist
- 📋 **Config**: Cập nhật cấu trúc `regions.yml` để hỗ trợ list nhạc

---

## [1.0] - 2024

### Added / Thêm mới
- 🎵 **Initial Release**: Phiên bản đầu tiên của RegionMusic
- 🌍 **WorldGuard Integration**: Tích hợp với WorldGuard để phát nhạc khi vào/ra region
- 🔊 **Sound Support**: Hỗ trợ cả vanilla và custom sounds (ItemsAdder, etc.)
- 🎮 **Commands**: 
  - `/regionmusic reload` - Tải lại cấu hình
  - `/regionmusic playmusic` - Phát nhạc thủ công
  - `/regionmusic stopmusic` - Dừng nhạc
  - `/regionmusic togglemusic` - Bật/tắt nhạc
  - `/regionmusic about` - Thông tin plugin
  - `/togglemusic` - Bật/tắt nhạc (cho tất cả người chơi)
- 📝 **Configuration**: 
  - `regions.yml` - Cấu hình region và nhạc
  - `musics.yml` - Cấu hình thuộc tính nhạc
- ⚡ **Performance**: Tối ưu hóa hiệu năng và ngăn chặn spam
- 🚫 **No Overlap**: Ngăn chặn nhạc chồng chéo từ các vùng khác nhau

---

## Format / Định dạng

Format dựa trên [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
và dự án này tuân thủ [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## Legend / Chú thích

- ✨ **Added**: Tính năng mới
- 🔄 **Changed**: Thay đổi trong chức năng hiện có
- 🐛 **Fixed**: Sửa lỗi
- 🔨 **Technical**: Thay đổi kỹ thuật
- 📝 **Documentation**: Cập nhật tài liệu
- ⚡ **Performance**: Cải thiện hiệu năng
- 🔒 **Security**: Cập nhật bảo mật

