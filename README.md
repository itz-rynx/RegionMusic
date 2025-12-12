# RegionMusic

[![Version](https://img.shields.io/badge/version-1.7-blue.svg)](https://github.com/rynx/RegionMusic)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21+-green.svg)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Discord](https://dcbadge.limes.pink/api/shield/695998203065008178)](https://discord.com/users/695998203065008178)
[![Author](https://img.shields.io/badge/author-rynx-purple.svg)](https://github.com/itz-rynx)

A lightweight Minecraft plugin that automatically plays music/sounds when players enter or leave WorldGuard regions. Supports multiple songs per region with sequential playback and automatic looping.

---

## 📋 Table of Contents / Mục lục

- [English](#english)
  - [Features](#features)
  - [Requirements](#requirements)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Commands](#commands)
  - [Permissions](#permissions)
  - [Usage Examples](#usage-examples)
- [Tiếng Việt](#tiếng-việt)
  - [Tính năng](#tính-năng)
  - [Yêu cầu](#yêu-cầu)
  - [Cài đặt](#cài-đặt)
  - [Cấu hình](#cấu-hình)
  - [Lệnh](#lệnh)
  - [Quyền](#quyền)
  - [Ví dụ sử dụng](#ví-dụ-sử-dụng)

---

# English

## Features

- 🎵 **Automatic Music Playback**: Plays music/sounds when players enter or leave WorldGuard regions
- 🔁 **Multiple Songs Support**: Configure multiple songs per region that play sequentially
- 🔂 **Auto Loop**: Automatically loops back to the first song after the playlist ends
- 🎮 **Toggle System**: Players can toggle music on/off with `/togglemusic` (default: ON)
- 🔊 **Custom Sounds**: Supports both vanilla Minecraft sounds and custom sounds (ItemsAdder, etc.)
- ⚡ **Performance Optimized**: Lightweight and optimized for performance
- 🚫 **No Overlap**: Prevents music overlap from different regions
- 📝 **Customizable Messages**: All messages can be customized via `lang.yml`
- 🌍 **WorldGuard Integration**: Works with all WorldGuard regions
- 🎼 **Custom Song Names**: Customize display names for songs in `musics.yml`
- 📢 **Now Playing Notifications**: Shows "Now playing: [song name]" when a song starts
- 🔊 **Volume & Pitch Control**: Adjust volume and pitch for each song individually

## Requirements

- **Minecraft Server**: Paper/Spigot 1.21.5 or higher
- **Java**: Java 21 or higher
- **WorldGuard**: Version 7.1.0 or higher
- **WorldEdit**: Required by WorldGuard

## Installation

1. Download the latest `RegionMusic.jar` from the releases page
2. Place the file in your server's `plugins` folder
3. Restart your server
4. Configure `regions.yml` and `musics.yml` in `plugins/RegionMusic/`
5. Use `/regionmusic reload` to load the configuration

## Configuration

### regions.yml

Configure which regions play which music:

```yaml
regions:
  spawn:
    regionname: spawn  # WorldGuard region name
    music:             # Single song or list of songs
      - spawn
      - custommusic
  dungeon1:
    regionname: boss_area
    music: boss        # Or: [boss, boss2, boss3]
```

### musics.yml

Define music properties:

```yaml
musics:
  spawn:
    sound: MUSIC_DISC_CAT        # Vanilla sound name
    interval: 185                 # Duration in seconds
    name: "Spawn Theme"           # Display name (optional, defaults to key name)
    volume: 1.0                   # Volume (0.0 - 1.0, default: 1.0)
    pitch: 1.0                    # Pitch (0.5 - 2.0, default: 1.0)
  custommusic:
    sound: records.cat            # Custom sound (ItemsAdder, etc.)
    interval: 185
    name: "Custom Music"          # Display name (optional)
    volume: 1.0                   # Volume (optional)
    pitch: 1.0                    # Pitch (optional)
  boss:
    sound: MUSIC_DISC_PIGSTEP
    interval: 148
    name: "Boss Battle"          # Display name (optional)
    volume: 1.0                  # Volume (optional)
    pitch: 1.0                   # Pitch (optional)
```

**Sound Format:**
- Vanilla: `MUSIC_DISC_CAT`, `minecraft:music_disc.cat`
- Custom: `records.cat`, `itemsadder:records.cat`, `namespace:sound_name`

**Display Name:**
- Field `name` is optional - if not provided, the key name will be used as display name
- Display name is shown in "Now playing" notifications

**Volume & Pitch:**
- Field `volume` is optional (default: 1.0) - controls sound volume (0.0 = silent, 1.0 = full volume)
- Field `pitch` is optional (default: 1.0) - controls sound pitch (0.5 = half speed, 1.0 = normal, 2.0 = double speed)

### lang.yml

Customize all plugin messages:

```yaml
messages:
  no-permission: "&cYou don't have permission to use this command!"
  player-only: "&cThis command is for players only!"
  toggle-off: "&cMusic turned off!"
  toggle-on: "&aMusic turned on!"
  now-playing: "&aNow playing: &f{song}"  # Song name notification
  # ... and more
```

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/regionmusic reload` | Reload configuration files | `regionmusic.reload` or `regionmusic.admin` |
| `/regionmusic playmusic` | Manually play music for current region | `regionmusic.playmusic` or `regionmusic.admin` |
| `/regionmusic stopmusic` | Stop currently playing music | `regionmusic.stopmusic` or `regionmusic.admin` |
| `/regionmusic togglemusic` | Toggle music on/off | `regionmusic.togglemusic` or `regionmusic.admin` |
| `/regionmusic nextsong` | Skip to next song in playlist | `regionmusic.nextsong` or `regionmusic.admin` |
| `/regionmusic about` | Show plugin information | `regionmusic.about` or `regionmusic.admin` |
| `/rm` | Alias for `/regionmusic` | Same as `/regionmusic` |
| `/togglemusic` | Toggle music on/off (for all players) | None |

## Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `regionmusic.admin` | Access to all `/regionmusic` commands (parent permission) | OP |
| `regionmusic.reload` | Access to `/regionmusic reload` command | OP |
| `regionmusic.playmusic` | Access to `/regionmusic playmusic` command | OP |
| `regionmusic.stopmusic` | Access to `/regionmusic stopmusic` command | OP |
| `regionmusic.togglemusic` | Access to `/regionmusic togglemusic` command | OP |
| `regionmusic.nextsong` | Access to `/regionmusic nextsong` command | OP |
| `regionmusic.about` | Access to `/regionmusic about` command | OP |

**Note**: 
- `/togglemusic` command is available to all players without permission.
- `regionmusic.admin` is a parent permission that includes all sub-permissions. You can grant individual permissions for fine-grained control.

## Usage Examples

### Example 1: Single Song per Region

```yaml
# regions.yml
regions:
  spawn:
    regionname: spawn
    music: spawn

# musics.yml
musics:
  spawn:
    sound: MUSIC_DISC_CAT
    interval: 185
    name: "Spawn Theme"  # Optional
    volume: 1.0          # Optional
    pitch: 1.0            # Optional
```

### Example 2: Multiple Songs (Playlist)

```yaml
# regions.yml
regions:
  dungeon:
    regionname: dungeon_area
    music:
      - dungeon_theme_1
      - dungeon_theme_2
      - boss_music

# musics.yml
musics:
  dungeon_theme_1:
    sound: MUSIC_DISC_CHIRP
    interval: 185
    name: "Dungeon Theme 1"  # Optional
    volume: 1.0               # Optional
    pitch: 1.0                # Optional
  dungeon_theme_2:
    sound: MUSIC_DISC_FAR
    interval: 197
    name: "Dungeon Theme 2"  # Optional
    volume: 1.0               # Optional
    pitch: 1.0                # Optional
  boss_music:
    sound: MUSIC_DISC_PIGSTEP
    interval: 148
    name: "Boss Battle"  # Optional
    volume: 1.0          # Optional
    pitch: 1.0           # Optional
```

### Example 3: Custom Sounds (ItemsAdder)

```yaml
# musics.yml
musics:
  custom:
    sound: itemsadder:records.custom_music
    interval: 200
    name: "Custom Music"  # Optional
    volume: 0.8           # Optional
    pitch: 1.0            # Optional
```

---

# Tiếng Việt

## Tính năng

- 🎵 **Phát nhạc tự động**: Phát nhạc/âm thanh khi người chơi vào hoặc ra khỏi khu vực WorldGuard
- 🔁 **Hỗ trợ nhiều nhạc**: Cấu hình nhiều bài nhạc trong một region, phát tuần tự
- 🔂 **Tự động lặp lại**: Tự động quay lại bài đầu sau khi phát hết danh sách
- 🎮 **Hệ thống bật/tắt**: Người chơi có thể bật/tắt nhạc bằng `/togglemusic` (mặc định: BẬT)
- 🔊 **Âm thanh tùy chỉnh**: Hỗ trợ cả âm thanh vanilla Minecraft và âm thanh tùy chỉnh (ItemsAdder, v.v.)
- ⚡ **Tối ưu hiệu năng**: Nhẹ và được tối ưu hóa cho hiệu năng
- 🚫 **Không chồng chéo**: Ngăn chặn nhạc chồng chéo từ các vùng khác nhau
- 📝 **Tùy chỉnh thông báo**: Tất cả thông báo có thể tùy chỉnh qua `lang.yml`
- 🌍 **Tích hợp WorldGuard**: Hoạt động với tất cả các khu vực WorldGuard
- 🎼 **Tùy chỉnh tên bài hát**: Tùy chỉnh tên hiển thị của bài nhạc trong `musics.yml`
- 📢 **Thông báo đang phát**: Hiển thị "Đang phát bài: [tên bài]" khi bắt đầu phát nhạc

## Yêu cầu

- **Minecraft Server**: Paper/Spigot 1.21.5 trở lên
- **Java**: Java 21 trở lên
- **WorldGuard**: Phiên bản 7.1.0 trở lên
- **WorldEdit**: Yêu cầu bởi WorldGuard

## Cài đặt

1. Tải file `RegionMusic.jar` mới nhất từ trang releases
2. Đặt file vào thư mục `plugins` của server
3. Khởi động lại server
4. Cấu hình `regions.yml` và `musics.yml` trong `plugins/RegionMusic/`
5. Sử dụng `/regionmusic reload` để tải lại cấu hình

## Cấu hình

### regions.yml

Cấu hình region nào phát nhạc nào:

```yaml
regions:
  spawn:
    regionname: spawn  # Tên khu vực WorldGuard
    music:             # Một nhạc hoặc danh sách nhạc
      - spawn
      - custommusic
  dungeon1:
    regionname: boss_area
    music: boss        # Hoặc: [boss, boss2, boss3]
```

### musics.yml

Định nghĩa thuộc tính của nhạc:

```yaml
musics:
  spawn:
    sound: MUSIC_DISC_CAT        # Tên âm thanh vanilla
    interval: 185                 # Thời lượng tính bằng giây
    name: "Spawn Theme"           # Tên hiển thị (tùy chọn, mặc định dùng tên key)
    volume: 1.0                   # Âm lượng (0.0 - 1.0, mặc định: 1.0)
    pitch: 1.0                    # Cao độ (0.5 - 2.0, mặc định: 1.0)
  custommusic:
    sound: records.cat            # Âm thanh tùy chỉnh (ItemsAdder, v.v.)
    interval: 185
    name: "Custom Music"          # Tên hiển thị (tùy chọn)
    volume: 1.0                   # Âm lượng (tùy chọn)
    pitch: 1.0                    # Cao độ (tùy chọn)
  boss:
    sound: MUSIC_DISC_PIGSTEP
    interval: 148
    name: "Boss Battle"           # Tên hiển thị (tùy chọn)
    volume: 1.0                   # Âm lượng (tùy chọn)
    pitch: 1.0                    # Cao độ (tùy chọn)
```

**Định dạng Sound:**
- Vanilla: `MUSIC_DISC_CAT`, `minecraft:music_disc.cat`
- Tùy chỉnh: `records.cat`, `itemsadder:records.cat`, `namespace:sound_name`

**Tên hiển thị:**
- Field `name` là tùy chọn - nếu không có, sẽ dùng tên key làm tên hiển thị
- Tên hiển thị được dùng trong thông báo "Đang phát bài"

**Âm lượng và Cao độ:**
- Field `volume` là tùy chọn (mặc định: 1.0) - điều khiển âm lượng (0.0 = im lặng, 1.0 = đầy đủ)
- Field `pitch` là tùy chọn (mặc định: 1.0) - điều khiển cao độ (0.5 = nửa tốc độ, 1.0 = bình thường, 2.0 = gấp đôi tốc độ)

### lang.yml

Tùy chỉnh tất cả thông báo của plugin:

```yaml
messages:
  no-permission: "&cBạn không có quyền sử dụng lệnh này!"
  player-only: "&cLệnh này chỉ dành cho người chơi!"
  toggle-off: "&cĐã tắt nhạc!"
  toggle-on: "&aĐã bật nhạc!"
  now-playing: "&aĐang phát bài: &f{song}"  # Thông báo tên bài hát
  # ... và nhiều hơn nữa
```

## Lệnh

| Lệnh | Mô tả | Quyền |
|------|-------|-------|
| `/regionmusic reload` | Tải lại các file cấu hình | `regionmusic.reload` hoặc `regionmusic.admin` |
| `/regionmusic playmusic` | Phát nhạc thủ công cho region hiện tại | `regionmusic.playmusic` hoặc `regionmusic.admin` |
| `/regionmusic stopmusic` | Dừng nhạc đang phát | `regionmusic.stopmusic` hoặc `regionmusic.admin` |
| `/regionmusic togglemusic` | Bật/tắt nhạc | `regionmusic.togglemusic` hoặc `regionmusic.admin` |
| `/regionmusic nextsong` | Chuyển sang bài nhạc tiếp theo | `regionmusic.nextsong` hoặc `regionmusic.admin` |
| `/regionmusic about` | Hiển thị thông tin plugin | `regionmusic.about` hoặc `regionmusic.admin` |
| `/rm` | Alias cho `/regionmusic` | Giống như `/regionmusic` |
| `/togglemusic` | Bật/tắt nhạc (cho tất cả người chơi) | Không cần |

## Quyền

| Quyền | Mô tả | Mặc định |
|-------|-------|----------|
| `regionmusic.admin` | Truy cập tất cả lệnh `/regionmusic` (permission cha) | OP |
| `regionmusic.reload` | Truy cập lệnh `/regionmusic reload` | OP |
| `regionmusic.playmusic` | Truy cập lệnh `/regionmusic playmusic` | OP |
| `regionmusic.stopmusic` | Truy cập lệnh `/regionmusic stopmusic` | OP |
| `regionmusic.togglemusic` | Truy cập lệnh `/regionmusic togglemusic` | OP |
| `regionmusic.nextsong` | Truy cập lệnh `/regionmusic nextsong` | OP |
| `regionmusic.about` | Truy cập lệnh `/regionmusic about` | OP |

**Lưu ý**: 
- Lệnh `/togglemusic` có sẵn cho tất cả người chơi mà không cần quyền.
- `regionmusic.admin` là permission cha bao gồm tất cả các permission con. Bạn có thể cấp từng permission riêng để kiểm soát chi tiết hơn.

## Ví dụ sử dụng

### Ví dụ 1: Một nhạc mỗi region

```yaml
# regions.yml
regions:
  spawn:
    regionname: spawn
    music: spawn

# musics.yml
musics:
  spawn:
    sound: MUSIC_DISC_CAT
    interval: 185
    name: "Spawn Theme"  # Tùy chọn
```

### Ví dụ 2: Nhiều nhạc (Playlist)

```yaml
# regions.yml
regions:
  dungeon:
    regionname: dungeon_area
    music:
      - dungeon_theme_1
      - dungeon_theme_2
      - boss_music

# musics.yml
musics:
  dungeon_theme_1:
    sound: MUSIC_DISC_CHIRP
    interval: 185
    name: "Dungeon Theme 1"  # Tùy chọn
  dungeon_theme_2:
    sound: MUSIC_DISC_FAR
    interval: 197
    name: "Dungeon Theme 2"  # Tùy chọn
  boss_music:
    sound: MUSIC_DISC_PIGSTEP
    interval: 148
    name: "Boss Battle"  # Tùy chọn
```

### Ví dụ 3: Âm thanh tùy chỉnh (ItemsAdder)

```yaml
# musics.yml
musics:
  custom:
    sound: itemsadder:records.custom_music
    interval: 200
    name: "Custom Music"  # Tùy chọn
```

---

## 📝 Notes / Lưu ý

- Music is **enabled by default** for all players
- Players can toggle music on/off with `/togglemusic`
- Music automatically stops when players leave the region
- Multiple songs play sequentially, then loop back to the first song
- Use `/regionmusic reload` after editing config files
- "Now playing" notifications are shown when a song starts or when skipping
- Custom song names can be set in `musics.yml` with the `name` field
- Volume and pitch can be adjusted per song for fine-tuned audio experience

- Nhạc **mặc định được bật** cho tất cả người chơi
- Người chơi có thể bật/tắt nhạc bằng `/togglemusic`
- Nhạc tự động dừng khi người chơi rời khỏi region
- Nhiều bài nhạc phát tuần tự, sau đó quay lại bài đầu
- Sử dụng `/regionmusic reload` sau khi chỉnh sửa file cấu hình
- Thông báo "Đang phát bài" được hiển thị khi bắt đầu phát hoặc khi skip
- Có thể tùy chỉnh tên bài hát trong `musics.yml` với field `name`

---

## 🐛 Issues / Vấn đề

If you encounter any issues, please report them on the [GitHub Issues](https://github.com/itz-rynx/RegionMusic/issues) page.

Nếu bạn gặp bất kỳ vấn đề nào, vui lòng báo cáo trên trang [GitHub Issues](https://github.com/itz-rynx/RegionMusic/issues).

---

## 📄 License / Giấy phép

This project is licensed under the MIT License.

Dự án này được cấp phép theo MIT License.

---

## 👤 Author / Tác giả

**rynx**

- GitHub: [@rynx](https://github.com/itz-rynx)

---

