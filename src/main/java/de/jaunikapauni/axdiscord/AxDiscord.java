package de.jaunikapauni.axdiscord;

import de.jaunikapauni.axdiscord.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public final class AxDiscord extends JavaPlugin {
    String webhookUrl;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        webhookUrl = getConfig().getString("discord.webhook");
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void send(String username, String message){
        Bukkit.getScheduler().runTask(this, () -> {
            try{
                URL url = new URL(webhookUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                String json = """
                        {
                        "username": "%s",
                        "content": "%s"
                }
                """.formatted(username, message);

                try(OutputStream os = conn.getOutputStream()){
                    os.write(json.getBytes());
                }
                conn.getResponseCode();
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
