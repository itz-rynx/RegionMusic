package org.rynx.regionMusic.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.rynx.regionMusic.RegionMusic;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class MessageManager {
    
    private final RegionMusic plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;
    private String currentLanguage = "vi";
    
    public MessageManager(RegionMusic plugin) {
        this.plugin = plugin;
        loadMessages();
    }
    
    public void loadMessages() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        // Load config.yml để lấy ngôn ngữ
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            // Tạo config.yml từ resource nếu chưa có
            plugin.saveResource("config.yml", false);
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        currentLanguage = config.getString("language", "vi");
        
        // Validate language (chỉ chấp nhận vi, en, zh)
        if (!currentLanguage.equals("vi") && !currentLanguage.equals("en") && !currentLanguage.equals("zh")) {
            plugin.getLogger().warning("Ngôn ngữ không hợp lệ: " + currentLanguage + ". Sử dụng mặc định: vi");
            currentLanguage = "vi";
        }
        
        // Tạo folder lang nếu chưa có
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }
        
        // Load file ngôn ngữ tương ứng
        messagesFile = new File(langFolder, currentLanguage + ".yml");
        
        if (!messagesFile.exists()) {
            // Copy từ resource nếu chưa có
            try {
                InputStream resourceStream = plugin.getResource("lang/" + currentLanguage + ".yml");
                if (resourceStream != null) {
                    Files.copy(resourceStream, messagesFile.toPath());
                } else {
                    // Fallback về vi.yml nếu file không tồn tại
                    plugin.getLogger().warning("Không tìm thấy file ngôn ngữ: " + currentLanguage + ".yml. Sử dụng vi.yml");
                    currentLanguage = "vi";
                    resourceStream = plugin.getResource("lang/vi.yml");
                    if (resourceStream != null) {
                        messagesFile = new File(langFolder, "vi.yml");
                        Files.copy(resourceStream, messagesFile.toPath());
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Lỗi khi copy file ngôn ngữ: " + e.getMessage());
                // Fallback về vi.yml
                currentLanguage = "vi";
                messagesFile = new File(langFolder, "vi.yml");
            }
        }
        
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        // Đảm bảo tất cả các file ngôn ngữ đều được copy vào data folder
        ensureLanguageFilesExist(langFolder);
    }
    
    private void ensureLanguageFilesExist(File langFolder) {
        String[] languages = {"vi", "en", "zh"};
        for (String lang : languages) {
            File langFile = new File(langFolder, lang + ".yml");
            if (!langFile.exists()) {
                try {
                    InputStream resourceStream = plugin.getResource("lang/" + lang + ".yml");
                    if (resourceStream != null) {
                        Files.copy(resourceStream, langFile.toPath());
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Không thể copy file ngôn ngữ " + lang + ".yml: " + e.getMessage());
                }
            }
        }
    }
    
    public void reload() {
        loadMessages();
    }
    
    public String getMessage(String key) {
        String message = messagesConfig.getString("messages." + key);
        if (message == null) {
            return "§cMessage not found: " + key;
        }
        return colorize(message);
    }
    
    /**
     * Chuyển mã màu trong chuỗi. Hỗ trợ cả 3 dạng:
     * <ul>
     *   <li>&amp;#RRGGBB → hex (VD: &#FFFFFF)</li>
     *   <li>&amp;X → §X (VD: &amp;a, &amp;f)</li>
     *   <li>§X → giữ nguyên (VD: §f, §a)</li>
     * </ul>
     */
    public static String colorize(String text) {
        if (text == null || text.isEmpty()) return text;
        // &#RRGGBB → §x§R§R§G§G§B§B (Minecraft 1.16+ hex)
        StringBuilder out = new StringBuilder(text.length() * 2);
        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '&' && i + 7 <= text.length() && text.charAt(i + 1) == '#') {
                String hex = text.substring(i + 2, i + 8);
                if (hex.matches("[0-9A-Fa-f]{6}")) {
                    out.append("§x");
                    for (int j = 0; j < 6; j++) {
                        out.append("§").append(hex.charAt(j));
                    }
                    i += 8;
                    continue;
                }
            }
            if (text.charAt(i) == '&' && i + 1 < text.length()) {
                out.append('§').append(text.charAt(i + 1));
                i += 2;
                continue;
            }
            // § hoặc ký tự thường: giữ nguyên
            out.append(text.charAt(i));
            i++;
        }
        return out.toString();
    }
    
    public String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                message = message.replace(replacements[i], replacements[i + 1]);
            }
        }
        return colorize(message);
    }
    
    public String getCurrentLanguage() {
        return currentLanguage;
    }
}

