# Changelog / Nhật ký thay đổi

All notable changes to RegionMusic will be documented in this file.

Tất cả các thay đổi đáng chú ý của RegionMusic sẽ được ghi lại trong file này.

---

## [1.7] - 2024

### Added / Thêm mới
- ✨ **Volume Control**: Thêm khả năng tùy chỉnh volume (âm lượng) cho từng bài nhạc trong `musics.yml`
- ✨ **Pitch Control**: Thêm khả năng tùy chỉnh pitch (cao độ) cho từng bài nhạc trong `musics.yml`

### Changed / Thay đổi
- 📝 **Config**: Thêm field `volume` (0.0 - 1.0, mặc định: 1.0) và `pitch` (0.5 - 2.0, mặc định: 1.0) vào `musics.yml`
- 🔧 **Sound Playback**: Cập nhật `playSound()` để sử dụng volume và pitch từ config thay vì hardcode

### Technical / Kỹ thuật
- 🔨 **Code**: Thêm Map `musicVolumeMap` và `musicPitchMap` trong `RegionConfigManager` để lưu trữ volume và pitch
- 🔨 **Code**: Thêm method `getVolumeForMusic()` và `getPitchForMusic()` trong `RegionConfigManager`
- 🔨 **Code**: Cập nhật `playSound()` để nhận volume và pitch làm tham số
- 🔨 **Code**: Thêm validation để giới hạn volume trong khoảng 0.0 - 1.0 và pitch trong khoảng 0.5 - 2.0

---

## [1.6] - 2024

### Changed / Thay đổi
- 🔒 **Permissions**: Tách permission riêng cho từng lệnh thay vì chỉ có `regionmusic.admin`
  - `regionmusic.reload` - Cho phép sử dụng `/regionmusic reload`
  - `regionmusic.playmusic` - Cho phép sử dụng `/regionmusic playmusic`
  - `regionmusic.stopmusic` - Cho phép sử dụng `/regionmusic stopmusic`
  - `regionmusic.togglemusic` - Cho phép sử dụng `/regionmusic togglemusic`
  - `regionmusic.nextsong` - Cho phép sử dụng `/regionmusic nextsong`
  - `regionmusic.about` - Cho phép sử dụng `/regionmusic about`
  - `regionmusic.admin` - Permission cha bao gồm tất cả các permission trên (backward compatible)

### Technical / Kỹ thuật
- 🔨 **Code**: Cập nhật `RegionMusicCommand` để kiểm tra permission riêng cho từng lệnh
- 🔨 **Code**: Cập nhật `plugin.yml` để định nghĩa các permission mới với parent permission

---

## [1.5] - 2024

### Added / Thêm mới
- ✨ **Custom Song Names**: Thêm khả năng tùy chỉnh tên bài hát trong `musics.yml` với field `name`
- ✨ **Now Playing Notification**: Hiển thị thông báo "Đang phát bài: [tên bài]" khi bắt đầu phát một bài nhạc
- ✨ **Skip Notification**: Hiển thị thông báo tên bài hát khi dùng lệnh `/regionmusic nextsong` (skip)

### Changed / Thay đổi
- 📝 **Config**: Thêm field `name` (tùy chọn) vào `musics.yml` để tùy chỉnh tên hiển thị của bài nhạc
- 📝 **Messages**: Thêm message `now-playing` vào `lang.yml` để tùy chỉnh thông báo đang phát

### Technical / Kỹ thuật
- 🔨 **Code**: Thêm method `getDisplayNameForMusic()` trong `RegionConfigManager` để lấy tên hiển thị của bài nhạc
- 🔨 **Code**: Thêm Map `musicDisplayNameMap` để lưu trữ tên hiển thị tùy chỉnh
- 🔨 **Code**: Cập nhật `playNextSong()` để hiển thị thông báo khi phát bài nhạc mới

---

## [1.4] - 2024

### Fixed / Sửa lỗi
- 🐛 **Fixed**: Sửa lỗi nhạc không tự động chuyển bài - bài 1 phát xong sẽ tự động chuyển sang bài 2
- 🐛 **Fixed**: Sửa lỗi lệnh `/regionmusic nextsong` (skip) không hoạt động đúng cách
- 🐛 **Fixed**: Sửa lỗi spam nhạc khi ra vào region nhanh - cải thiện logic kiểm tra region
- 🐛 **Fixed**: Sửa lỗi spam nhạc khi vào region, ra khỏi region, rồi vào lại cùng region - thêm cooldown 2 giây để tránh phát lại

### Technical / Kỹ thuật
- 🔨 **Code**: Cải thiện logic tự động chuyển bài trong `playNextSong` - đảm bảo task tự động chuyển bài hoạt động đúng
- 🔨 **Code**: Sửa logic skip để không bị conflict với task tự động chuyển bài
- 🔨 **Code**: Cải thiện logic kiểm tra region trong `RegionListener` để tránh spam khi ra vào nhanh
- 🔨 **Code**: Thêm kiểm tra region trong `playNextSong` để đảm bảo player vẫn ở trong region trước khi phát bài tiếp theo
- 🔨 **Code**: Thêm method `isMusicPlaying()` và `isMusicPlayingForRegion()` trong `MusicManager` để kiểm tra trạng thái phát nhạc
- 🔨 **Code**: Thêm tracking thời gian ra khỏi region trong `RegionListener` - nếu vào lại cùng region trong vòng 2 giây sẽ không phát lại (tránh spam)

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

