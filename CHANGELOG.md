# Changelog

All notable changes to RegionMusic will be documented in this file.

---

## [1.9]

### Added
- ✨ **Config Version & Debug**: Added version management and debug mode in `config.yml`
  - Version automatically updates with plugin version
  - Debug mode to enable/disable detailed logging in console
- ✨ **Custom Logger**: Added custom logger with beautiful **RYNX** prefix
  - All logs display with `[RYNX]` prefix in yellow/red/yellow colors
  - Debug logs have `[DEBUG]` prefix when debug mode is enabled
- ✨ **GUI**: Added graphical interface to view region and song information
  - Command: `/regionmusic gui` or `/rm gui`
  - Shows current region and all configured regions
  - Displays detailed information: number of songs, play mode, song list
- ✨ **Add Music via Chat**: Added ability to add new songs to `musics.yml` through chat
  - Command: `/regionmusic addmusic <song_name>`
  - Enter information in format: `sound|interval|name|volume|pitch`
  - Cancel with `/cancel` command
- ✨ **Cancel Command**: Added `/cancel` command to cancel adding music process

### Changed
- 📝 **Config**: Added `version` and `debug` fields to `config.yml`
- 🔧 **Logger**: Replaced default logger with CustomLogger with RYNX prefix
- 📝 **Plugin.yml**: Added `cancel` command and updated usage of `regionmusic` command

### Technical
- 🔨 **Code**: Created `CustomLogger` class to manage logs with beautiful format
- 🔨 **Code**: Created `ConfigManager` class to manage `config.yml` with version and debug
- 🔨 **Code**: Created `RegionMusicGUI` class to display GUI
- 🔨 **Code**: Created `ChatListener` class to handle chat when adding songs
- 🔨 **Code**: Created `GUIListener` class to handle click events in GUI
- 🔨 **Code**: Added `getAllRegions()` and `addMusic()` methods in `RegionConfigManager`
- 🔨 **Code**: Updated `RegionMusicCommand` to support `gui` and `addmusic` commands
- 🔨 **Code**: Updated `RegionMusicTabCompleter` to add tab completion for new commands

---

## [1.8]

### Added
- ✨ **Play Mode**: Added ability to choose playback mode: sequential (in order) or random (shuffle)
  - Sequential: Plays songs in order from first to last, then loops back
  - Random: Plays songs randomly, no duplicate with currently playing song
- ✨ **Multi-Language Support**: Added multi-language support with configurable language selection
  - Supports Vietnamese (vi), English (en), and Chinese (zh)
  - Language files stored in `lang/` folder
- ✨ **Persistent Toggle State**: Toggle music preference is saved and persists across server restarts

### Changed
- 📝 **Config**: Added `playmode` field (sequential or random, default: sequential) to `regions.yml`
- 🔧 **Playback**: Updated song switching logic to support both sequential and random modes
- 📝 **Config**: Added `config.yml` file for language configuration
- 📝 **Messages**: Moved language files to `lang/` folder structure

### Technical
- 🔨 **Code**: Added Map `regionPlayModeMap` in `RegionConfigManager` to store playmode for each region
- 🔨 **Code**: Added `getPlayModeForRegion()` and `isRandomMode()` methods in `RegionConfigManager`
- 🔨 **Code**: Added `getNextSongIndex()` method in `MusicManager` to calculate next song based on playmode
- 🔨 **Code**: Added Random instance to support random mode
- 🔨 **Code**: Created `MessageManager` to load messages from language files
- 🔨 **Code**: Added `MusicToggleManager` to persist toggle state to `toggles.yml`

---

## [1.7]

### Added
- ✨ **Volume Control**: Added ability to customize volume for each song in `musics.yml`
- ✨ **Pitch Control**: Added ability to customize pitch for each song in `musics.yml`

### Changed
- 📝 **Config**: Added `volume` field (0.0 - 1.0, default: 1.0) and `pitch` field (0.5 - 2.0, default: 1.0) to `musics.yml`
- 🔧 **Sound Playback**: Updated `playSound()` to use volume and pitch from config instead of hardcoded values

### Technical
- 🔨 **Code**: Added Map `musicVolumeMap` and `musicPitchMap` in `RegionConfigManager` to store volume and pitch
- 🔨 **Code**: Added `getVolumeForMusic()` and `getPitchForMusic()` methods in `RegionConfigManager`
- 🔨 **Code**: Updated `playSound()` to accept volume and pitch as parameters
- 🔨 **Code**: Added validation to limit volume in range 0.0 - 1.0 and pitch in range 0.5 - 2.0

---

## [1.6]

### Changed
- 🔒 **Permissions**: Separated permissions for each command instead of only `regionmusic.admin`
  - `regionmusic.reload` - Allows use of `/regionmusic reload`
  - `regionmusic.playmusic` - Allows use of `/regionmusic playmusic`
  - `regionmusic.stopmusic` - Allows use of `/regionmusic stopmusic`
  - `regionmusic.togglemusic` - Allows use of `/regionmusic togglemusic`
  - `regionmusic.nextsong` - Allows use of `/regionmusic nextsong`
  - `regionmusic.about` - Allows use of `/regionmusic about`
  - `regionmusic.admin` - Parent permission including all above permissions (backward compatible)

### Technical
- 🔨 **Code**: Updated `RegionMusicCommand` to check individual permissions for each command
- 🔨 **Code**: Updated `plugin.yml` to define new permissions with parent permission

---

## [1.5]

### Added
- ✨ **Custom Song Names**: Added ability to customize song names in `musics.yml` with `name` field
- ✨ **Now Playing Notification**: Shows "Now playing: [song name]" notification when starting to play a song
- ✨ **Skip Notification**: Shows song name notification when using `/regionmusic nextsong` (skip) command

### Changed
- 📝 **Config**: Added `name` field (optional) to `musics.yml` to customize display name of songs
- 📝 **Messages**: Added `now-playing` message to `lang.yml` to customize now playing notification

### Technical
- 🔨 **Code**: Added `getDisplayNameForMusic()` method in `RegionConfigManager` to get display name of songs
- 🔨 **Code**: Added Map `musicDisplayNameMap` to store custom display names
- 🔨 **Code**: Updated `playNextSong()` to display notification when playing new song

---

## [1.4]

### Fixed
- 🐛 **Fixed**: Fixed music not automatically switching - song 1 will automatically switch to song 2 after finishing
- 🐛 **Fixed**: Fixed `/regionmusic nextsong` (skip) command not working correctly
- 🐛 **Fixed**: Fixed music spam when quickly entering/exiting regions - improved region checking logic
- 🐛 **Fixed**: Fixed music spam when entering region, leaving region, then re-entering same region - added 2 second cooldown to prevent replay

### Technical
- 🔨 **Code**: Improved automatic song switching logic in `playNextSong` - ensured automatic song switching task works correctly
- 🔨 **Code**: Fixed skip logic to not conflict with automatic song switching task
- 🔨 **Code**: Improved region checking logic in `RegionListener` to prevent spam when quickly entering/exiting
- 🔨 **Code**: Added region check in `playNextSong` to ensure player is still in region before playing next song
- 🔨 **Code**: Added `isMusicPlaying()` and `isMusicPlayingForRegion()` methods in `MusicManager` to check music playing status
- 🔨 **Code**: Added exit time tracking in `RegionListener` - if re-entering same region within 2 seconds will not replay (prevents spam)

---

## [1.3]

### Added
- ✨ **New Alias**: Added `rm` alias for `/regionmusic` command
  - Players can use `/rm` instead of `/regionmusic` for convenience
  - Examples: `/rm reload`, `/rm about`, `/rm playmusic`

---

## [1.2]

### Added
- ✨ **New Command**: `/regionmusic nextsong` - Skip to next song in current region
  - Allows players to skip currently playing song and switch to next song
  - Automatically loops back to first song if at last song
  - Only works when in a region with music

### Changed
- 📝 **File Rename**: Renamed `msg.yml` file to `lang.yml` for easier management
- 🔧 **Default Toggle**: Togglemusic defaults to **ON** for all new players
- ⚡ **Performance**: Improved performance and optimized code

### Fixed
- 🐛 **Fixed**: Fixed music spam/double when entering region with multiple songs
- 🐛 **Fixed**: Fixed multiple songs playing simultaneously instead of sequentially
- 🐛 **Fixed**: Improved task checking logic to avoid race conditions

### Technical
- 🔨 **Code**: Improved MusicManager to ensure only one task is running
- 🔨 **Code**: Added check for currently playing region to avoid unnecessary replay
- 🔨 **Code**: Increased delay to ensure old task is completely cancelled

---

## [1.1]

### Added
- 🎵 **Multiple Songs**: Support for multiple songs in 1 region with sequential playback
- 🔂 **Auto Loop**: Automatically loops back to first song after playlist ends
- 📝 **Message System**: Added message management system via `msg.yml` file

### Changed
- 🔄 **Playback**: Changed from playing one song to supporting playlists
- 📋 **Config**: Updated `regions.yml` structure to support song lists

---

## [1.0]

### Added
- 🎵 **Initial Release**: First version of RegionMusic
- 🌍 **WorldGuard Integration**: Integrated with WorldGuard to play music when entering/leaving regions
- 🔊 **Sound Support**: Supports both vanilla and custom sounds (ItemsAdder, etc.)
- 🎮 **Commands**: 
  - `/regionmusic reload` - Reload configuration
  - `/regionmusic playmusic` - Manually play music
  - `/regionmusic stopmusic` - Stop music
  - `/regionmusic togglemusic` - Toggle music on/off
  - `/regionmusic about` - Plugin information
  - `/togglemusic` - Toggle music on/off (for all players)
- 📝 **Configuration**: 
  - `regions.yml` - Configure regions and music
  - `musics.yml` - Configure music properties
- ⚡ **Performance**: Optimized performance and prevented spam
- 🚫 **No Overlap**: Prevents music overlap from different regions

---

## Format

Format based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## Legend

- ✨ **Added**: New features
- 🔄 **Changed**: Changes in existing functionality
- 🐛 **Fixed**: Bug fixes
- 🔨 **Technical**: Technical changes
- 📝 **Documentation**: Documentation updates
- ⚡ **Performance**: Performance improvements
- 🔒 **Security**: Security updates
