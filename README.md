RegionMusic

Every region tells a story with music

Automatically play music when players enter WorldGuard regions!

RegionMusic is a lightweight Minecraft plugin that automatically plays music/sounds when players enter or leave WorldGuard regions. Perfect for creating immersive experiences with region-specific soundtracks!

✨ Features

Song Management GUI - Complete graphical interface for managing songs (add/edit/delete) (/regionmusic songs)
✏️ Edit Songs - Edit existing songs with new properties (/regionmusic editmusic)
️ Delete Songs - Safely delete unused songs with confirmation dialog
Multi-page GUI - Paginated song list for better navigation
Safety Checks - Prevent deletion of songs currently used in regions
Enhanced UI - Modern and intuitive user interface design
Automatic Music Playback - Plays music/sounds when players enter or leave WorldGuard regions
Multiple Tracks Support - Play music and ambience simultaneously for immersive experiences
Multiple Songs Support - Configure multiple songs per region that play sequentially or randomly
Auto Loop - Automatically loops back to the first song after the playlist ends
Play Mode - Choose playback mode: sequential (in order) or random (shuffle)
Toggle System - Players can toggle music on/off with /togglemusic (default: ON)
Custom Sounds - Supports both vanilla Minecraft sounds and custom sounds (ItemsAdder, etc.)
Custom Song Names - Customize display names for songs in musics.yml
Now Playing Notifications - Shows "Now playing: [song name]" when a song starts
Volume & Pitch Control - Adjust volume and pitch for each song individually
⚡ Performance Optimized - Lightweight and optimized for performance
No Overlap - Prevents music overlap from different regions
Customizable Messages - All messages can be customized via language files in lang/ folder
Multi-Language Support - Supports Vietnamese (vi), English (en), and Chinese (zh) with configurable language selection
WorldGuard Integration - Works with all WorldGuard regions
Granular Permissions - Separate permissions for each command
Persistent Toggle State - Toggle music preference is saved and persists across server restarts
️ GUI Interface - Visual interface to view region and song information (/regionmusic gui)
Add Music via Chat - Add new songs to musics.yml through chat interface (/regionmusic addmusic)
⚙️ Config Version & Debug - Version management and debug mode in config.yml
Custom Logger - Beautiful logger with RYNX prefix for better console visibility

Requirements

Minecraft Server: Paper/Spigot 1.21 or higher
Java: Java 21 or higher
WorldGuard: Version 7.1.0 or higher
WorldEdit: Required by WorldGuard

Installation

Download RegionMusic.jar from this page
Place the file in your server's plugins folder
Restart your server
Configure regions.yml and musics.yml in plugins/RegionMusic/
Use /regionmusic reload to load the configuration

⚙️ Configuration

regions.yml
Configure which regions play which music:

[CODE=yaml]
regions:
  spawn:
    regionname: spawn  # WorldGuard region name
    music:             # Single song or list of songs
      - spawn
      - custommusic
    playmode: sequential  # sequential (in order) or random (shuffle), default: sequential
    ambience: forest_ambience  # (Optional) Background ambience that plays continuously with music
  dungeon1:
    regionname: boss_area
    music: boss        # Or: [boss, boss2, boss3]
    playmode: random   # Random playback
    ambience:          # (Optional) Multiple ambience tracks can play simultaneously
      - dungeon_ambience
      - cave_echoes
[/CODE]

musics.yml
Define music properties:

[CODE=yaml]
musics:
  spawn:
    sound: MUSIC_DISC_CAT        # Vanilla sound name
    interval: 185                 # Duration in seconds
    name: "Spawn Theme"           # Display name (optional)
    volume: 1.0                   # Volume 0.0-1.0 (optional, default: 1.0)
    pitch: 1.0                    # Pitch 0.5-2.0 (optional, default: 1.0)
  custommusic:
    sound: records.cat            # Custom sound (ItemsAdder, etc.)
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
[/CODE]

Sound Format:
- Vanilla: MUSIC_DISC_CAT, minecraft:music_disc.cat
- Custom: records.cat, itemsadder:records.cat, namespace:sound_name

config.yml
Configure plugin settings:

[CODE=yaml]
# Plugin Version (DO NOT EDIT - Auto-updated)
version: '1.9'

# Debug Mode
# Enable debug logging to console (true/false)
debug: false

# Language
# Available languages: vi (Vietnamese), en (English), zh (Chinese)
language: vi
[/CODE]

Config Options:
- version: Automatically updated with plugin version (do not edit manually)
- debug: Enable/disable debug mode for detailed console logging (default: false)
- language: Select plugin language - vi (Vietnamese), en (English), or zh (Chinese)

Language Files:
- lang/vi.yml - Tiếng Việt (Vietnamese) - Default
- lang/en.yml - English
- lang/zh.yml - 中文 (Chinese)

All messages can be customized by editing language files in lang/ folder.

Commands

[TABLE]
[TR]
[TH]Command[/TH]
[TH]Description[/TH]
[TH]Permission[/TH]
[/TR]
[TR]
[TD]/regionmusic reload[/TD]
[TD]Reload configuration files[/TD]
[TD]regionmusic.reload[/TD]
[/TR]
[TR]
[TD]/regionmusic playmusic[/TD]
[TD]Manually play music for current region[/TD]
[TD]regionmusic.playmusic[/TD]
[/TR]
[TR]
[TD]/regionmusic stopmusic[/TD]
[TD]Stop currently playing music[/TD]
[TD]regionmusic.stopmusic[/TD]
[/TR]
[TR]
[TD]/regionmusic togglemusic[/TD]
[TD]Toggle music on/off[/TD]
[TD]regionmusic.togglemusic[/TD]
[/TR]
[TR]
[TD]/regionmusic nextsong[/TD]
[TD]Skip to next song in playlist[/TD]
[TD]regionmusic.nextsong[/TD]
[/TR]
[TR]
[TD]/regionmusic about[/TD]
[TD]Show plugin information[/TD]
[TD]regionmusic.about[/TD]
[/TR]
[TR]
[TD]/regionmusic gui[/TD]
[TD]Open GUI to view regions and songs[/TD]
[TD]regionmusic.admin[/TD]
[/TR]
[TR]
[TD]/regionmusic songs[/TD]
[TD]Open song management GUI (add/edit/delete)[/TD]
[TD]regionmusic.admin[/TD]
[/TR]
[TR]
[TD]/regionmusic addmusic <name>[/TD]
[TD]Add new song via chat interface[/TD]
[TD]regionmusic.admin[/TD]
[/TR]
[TR]
[TD]/regionmusic editmusic <name> <params>[/TD]
[TD]Edit existing song properties[/TD]
[TD]regionmusic.admin[/TD]
[/TR]
[TR]
[TD]/rm[/TD]
[TD]Alias for /regionmusic[/TD]
[TD]Same as /regionmusic[/TD]
[/TR]
[TR]
[TD]/togglemusic[/TD]
[TD]Toggle music on/off (for all players)[/TD]
[TD]None[/TD]
[/TR]
[TR]
[TD]/cancel[/TD]
[TD]Cancel adding music process[/TD]
[TD]None[/TD]
[/TR]
[/TABLE]

Permissions

[TABLE]
[TR]
[TH]Permission[/TH]
[TH]Description[/TH]
[TH]Default[/TH]
[/TR]
[TR]
[TD]regionmusic.admin[/TD]
[TD]Access to all /regionmusic commands (parent permission)[/TD]
[TD]OP[/TD]
[/TR]
[TR]
[TD]regionmusic.reload[/TD]
[TD]Access to /regionmusic reload command[/TD]
[TD]OP[/TD]
[/TR]
[TR]
[TD]regionmusic.playmusic[/TD]
[TD]Access to /regionmusic playmusic command[/TD]
[TD]OP[/TD]
[/TR]
[TR]
[TD]regionmusic.stopmusic[/TD]
[TD]Access to /regionmusic stopmusic command[/TD]
[TD]OP[/TD]
[/TR]
[TR]
[TD]regionmusic.togglemusic[/TD]
[TD]Access to /regionmusic togglemusic command[/TD]
[TD]OP[/TD]
[/TR]
[TR]
[TD]regionmusic.nextsong[/TD]
[TD]Access to /regionmusic nextsong command[/TD]
[TD]OP[/TD]
[/TR]
[TR]
[TD]regionmusic.about[/TD]
[TD]Access to /regionmusic about command[/TD]
[TD]OP[/TD]
[/TR]
[/TABLE]

Note: /togglemusic command is available to all players without permission.

Usage Examples

Example 1: Single Song per Region

[CODE=yaml]
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
    name: "Spawn Theme"
    volume: 1.0
    pitch: 1.0
[/CODE]

Example 2: Multiple Songs (Playlist)

[CODE=yaml]
# regions.yml
regions:
  dungeon:
    regionname: dungeon_area
    music:
      - dungeon_theme_1
      - dungeon_theme_2
      - boss_music
    playmode: sequential  # or random

# musics.yml
musics:
  dungeon_theme_1:
    sound: MUSIC_DISC_CHIRP
    interval: 185
    name: "Dungeon Theme 1"
  dungeon_theme_2:
    sound: MUSIC_DISC_FAR
    interval: 197
    name: "Dungeon Theme 2"
  boss_music:
    sound: MUSIC_DISC_PIGSTEP
    interval: 148
    name: "Boss Battle"
[/CODE]

Example 3: Custom Sounds (ItemsAdder)

[CODE=yaml]
# musics.yml
musics:
  custom:
    sound: itemsadder:records.custom_music
    interval: 200
    name: "Custom Music"
    volume: 0.8
    pitch: 1.0
[/CODE]

Example 4: Music + Ambience (Multiple Tracks Simultaneously)

[CODE=yaml]
# regions.yml
regions:
  forest:
    regionname: forest_area
    music:
      - forest_theme_1
      - forest_theme_2
    playmode: sequential
    ambience: forest_ambience  # Plays continuously with music

# musics.yml
musics:
  forest_theme_1:
    sound: MUSIC_DISC_CAT
    interval: 185
    name: "Forest Theme 1"
    volume: 1.0
    pitch: 1.0
  forest_theme_2:
    sound: MUSIC_DISC_CHIRP
    interval: 197
    name: "Forest Theme 2"
    volume: 1.0
    pitch: 1.0
  forest_ambience:  # Ambience track (plays continuously, loops automatically)
    sound: AMBIENT_CAVE
    interval: 60  # Loop every 60 seconds
    name: "Forest Ambience"
    volume: 0.5  # Lower volume for background ambience
    pitch: 1.0
[/CODE]

Notes

Music is enabled by default for all players
Players can toggle music on/off with /togglemusic - toggle state is saved and persists across server restarts
Music automatically stops when players leave the region
Ambience tracks can play simultaneously with music for immersive experiences
Multiple songs can play sequentially (default) or randomly based on playmode setting
Ambience tracks loop continuously while music plays in the background
Use /regionmusic reload after editing config files
"Now playing" notifications are shown when a song starts or when skipping
Custom song names can be set in musics.yml with the name field
Volume and pitch can be adjusted per song for fine-tuned audio experience
Play mode (sequential or random) can be configured per region in regions.yml
Language can be changed in config.yml - supports Vietnamese (vi), English (en), and Chinese (zh)
All messages can be customized by editing language files in lang/ folder
Use /regionmusic gui to view all regions and songs visually
Use /regionmusic songs to manage songs with full GUI (add/edit/delete)
Add new songs via /regionmusic addmusic <name> and chat interface
Edit existing songs via /regionmusic editmusic <name> <params>
Debug mode can be enabled in config.yml for detailed console logging
Logger displays with beautiful RYNX prefix for better visibility

Support

If you encounter any issues or have questions, please:
Check the GitHub Issues page
Make sure you're using the latest version
Check that WorldGuard is properly installed and configured

License

This project is licensed under the MIT License.

Author

rynx
- GitHub: @rynx
• Discord: @q.bao1702

---