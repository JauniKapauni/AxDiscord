package de.jaunikapauni.axdiscord;

import de.jaunikapauni.axdiscord.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class AxDiscord extends JavaPlugin {
    String webhookUrl;
    String server;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        webhookUrl = getConfig().getString("discord.webhook");
        if(webhookUrl == null || webhookUrl.isEmpty()){
            getLogger().warning("Discord webhook is missing!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        server = getConfig().getString("server");
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        this.send("Server", "Enabled!");
        getLogger().info("");
        getLogger().info("----------------------------------------");
        getLogger().info("Name: " + getName());
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info(String.join("Authors: " + ", ", getDescription().getAuthors()));
        getLogger().info("----------------------------------------");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        this.send("Server", "Disabled!");
    }

    public void send(String username, String message){
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try{
                String format = server + " " + username + " " + message;
                URL url = new URL(webhookUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                String json = """
                        {
                        "username": "Minecraft",
                        "content": "%s"
                }
                """.formatted(escapeJson(format));

                try(OutputStream os = conn.getOutputStream()){
                    os.write(json.getBytes());
                }
                conn.getResponseCode();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public String escapeJson(String text){
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
