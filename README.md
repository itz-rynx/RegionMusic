# RegionMusic

**Every region tells a story with music**

Automatically play music when players enter WorldGuard regions!

RegionMusic is a lightweight Minecraft plugin that automatically plays music/sounds when players enter or leave WorldGuard regions. Perfect for creating immersive experiences with region-specific soundtracks!

---

## ✨ Features

- **Song Management GUI** – Complete graphical interface for managing songs (add/edit/delete) (`/regionmusic songs`)
- **Edit Songs** – Edit existing songs with new properties (`/regionmusic editmusic`)
- **Delete Songs** – Safely delete unused songs with confirmation dialog
- **Multi-page GUI** – Paginated song list for better navigation
- **Safety Checks** – Prevent deletion of songs currently used in regions
- **Enhanced UI** – Modern and intuitive user interface design
- **Automatic Music Playback** – Plays music/sounds when players enter or leave WorldGuard regions
- **Multiple Tracks Support** – Play music and ambience simultaneously for immersive experiences
- **Multiple Songs Support** – Configure multiple songs per region that play sequentially or randomly
- **Auto Loop** – Automatically loops back to the first song after the playlist ends
- **Play Mode** – Choose playback mode: sequential (in order) or random (shuffle)
- **Toggle System** – Players can toggle music on/off with `/togglemusic` (default: ON)
- **Custom Sounds** – Supports both vanilla Minecraft sounds and custom sounds (ItemsAdder, etc.)
- **Custom Song Names** – Customize display names for songs in `musics.yml`
- **Now Playing Notifications** – Customizable message with placeholders: `{song}`, `{duration}` (M:SS), `{duration_seconds}`
- **Hex & Multi-Format Colors** – In lang messages use `&#RRGGBB`, `&a`/`&f`, or `§a`/`§f` (all three supported)
- **Volume & Pitch Control** – Adjust volume and pitch for each song individually
- **Performance Optimized** – Lightweight and optimized for performance
- **No Overlap** – Prevents music overlap from different regions
- **Customizable Messages** – All messages in `lang/` folder (supports hex colors and placeholders)
- **Multi-Language Support** – Vietnamese (vi), English (en), Chinese (zh)
- **WorldGuard Integration** – Works with all WorldGuard regions
- **Granular Permissions** – Separate permissions for each command
- **Persistent Toggle State** – Toggle preference saved across server restarts
- **GUI Interface** – View region and song information (`/regionmusic gui`)
- **Add Music via Chat** – Add new songs through chat (`/regionmusic addmusic`)
- **Config Version & Debug** – Version management and debug mode in `config.yml`
- **Custom Logger** – Logger with RYNX prefix for console visibility

---

## 📋 Requirements

- **Minecraft Server**: Paper/Spigot 1.21+
- **Java**: 21+
- **WorldGuard**: 7.1.0+
- **WorldEdit**: Required by WorldGuard

---

## 📥 Installation

1. Download `RegionMusic.jar` from [Releases](https://github.com/itz-rynx/RegionMusic/releases) or your preferred source.
2. Place the jar in your server’s `plugins` folder.
3. Restart the server.
4. Configure `regions.yml` and `musics.yml` in `plugins/RegionMusic/`.
5. Use `/regionmusic reload` to load the configuration.

---

## ⚙️ Configuration

### regions.yml

Configure which regions play which music:

```yaml
regions:
  spawn:
    regionname: spawn
    music:
      - spawn
      - custommusic
    playmode: sequential   # sequential or random
    ambience: forest_ambience   # optional
  dungeon1:
    regionname: boss_area
    music: boss
    playmode: random
    ambience:
      - dungeon_ambience
      - cave_echoes
```

### musics.yml

Define music properties:

```yaml
musics:
  spawn:
    sound: MUSIC_DISC_CAT
    interval: 185          # duration in seconds
    name: "Spawn Theme"
    volume: 1.0
    pitch: 1.0
  custommusic:
    sound: itemsadder:records.custom_music
    interval: 185
    name: "Custom Music"
    volume: 1.0
    pitch: 1.0
  boss:
    sound: MUSIC_DISC_PIGSTEP
    interval: 148
    name: "Boss Battle"
    volume: 1.0
    pitch: 1.0
```

**Sound format:** Vanilla: `MUSIC_DISC_CAT`, `minecraft:music_disc.cat` · Custom: `itemsadder:records.custom`, `namespace:sound_name`

### config.yml

```yaml
version: '2.1'    # auto-updated, do not edit
debug: false
language: vi      # vi, en, zh
```

### Lang files & colors (lang/vi.yml, en.yml, zh.yml)

Messages support three color formats:

| Format    | Example   | Description        |
|----------|-----------|--------------------|
| `&#RRGGBB` | `&#FFFFFF` | Hex color (6 digits) |
| `&X`     | `&a`, `&f` | Ampersand + 1 char  |
| `§X`     | `§a`, `§f` | Section sign (as-is) |

**Now-playing placeholders:**

| Placeholder         | Meaning              | Example      |
|---------------------|----------------------|-------------|
| `{song}`            | Display name         | Spawn Theme |
| `{duration}`        | Length as M:SS       | 3:05        |
| `{duration_seconds}`| Length in seconds    | 185         |

Example in `lang/vi.yml`:

```yaml
messages:
  now-playing: "&aĐang phát: &f{song} &7({duration})"
```

---

## 💻 Commands

| Command | Description | Permission |
|--------|-------------|------------|
| `/regionmusic reload` | Reload configuration | regionmusic.reload |
| `/regionmusic playmusic` | Play music for current region | regionmusic.playmusic |
| `/regionmusic stopmusic` | Stop current music | regionmusic.stopmusic |
| `/regionmusic togglemusic` | Toggle music on/off | regionmusic.togglemusic |
| `/regionmusic nextsong` | Skip to next song | regionmusic.nextsong |
| `/regionmusic about` | Plugin info | regionmusic.about |
| `/regionmusic gui` | View regions and songs | regionmusic.admin |
| `/regionmusic songs` | Song management GUI | regionmusic.admin |
| `/regionmusic addmusic <name>` | Add song via chat | regionmusic.admin |
| `/regionmusic editmusic <name> <params>` | Edit song | regionmusic.admin |
| `/rm` | Alias for `/regionmusic` | same as regionmusic |
| `/togglemusic` | Toggle music (all players) | none |
| `/cancel` | Cancel add-music process | none |

---

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| regionmusic.admin | All /regionmusic commands | OP |
| regionmusic.reload | /regionmusic reload | OP |
| regionmusic.playmusic | /regionmusic playmusic | OP |
| regionmusic.stopmusic | /regionmusic stopmusic | OP |
| regionmusic.togglemusic | /regionmusic togglemusic | OP |
| regionmusic.nextsong | /regionmusic nextsong | OP |
| regionmusic.about | /regionmusic about | OP |

`/togglemusic` is available to all players without permission.

---

## 📖 Usage examples

**Single song per region:** Set `music: spawn` and define `spawn` in `musics.yml`.

**Playlist:** Use a list under `music:` and set `playmode: sequential` or `random`.

**Music + ambience:** Set `ambience: forest_ambience` (or a list) and define the sounds in `musics.yml`.

---

## 📝 Notes

- Music is **enabled by default** for all players.
- Toggle state is **saved** and persists across restarts.
- Music stops when players leave the region.
- Ambience can play at the same time as music.
- Use **placeholders** `{song}`, `{duration}`, `{duration_seconds}` in the now-playing message in `lang/*.yml`.
- Use **hex** `&#RRGGBB`, **&a/&f**, or **§a/§f** in lang messages (quote values with `#` in YAML).
- Use `/regionmusic reload` after editing config or lang files.

---

## 🐛 Support

- [GitHub Issues](https://github.com/itz-rynx/RegionMusic/issues)
- Ensure you use the latest version and that WorldGuard is installed and configured.

---

## 📄 License

This project is licensed under the MIT License.

---

## 👤 Author

**rynx**  
- GitHub: [@rynx](https://github.com/itz-rynx)  
- Discord: [@qbao1702](https://discord.com/users/695998203065008178)
