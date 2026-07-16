package de.jaunikapauni.axdiscord;

import de.jaunikapauni.axdiscord.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class AxDiscord extends JavaPlugin {
    String webhookUrl;
    String server;
    Queue<String> queue = new ConcurrentLinkedQueue<>();

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
        startWebhookWorker();
        this.sendAsync("Server", "Enabled!");
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
        this.sendSync("Server", "Disabled!");
    }

    public void sendAsync(String username, String message){
        String format = server + " " + username + " " + message;
        String json = """
                {
                "username": "Minecraft",
                "content": "%s"
                }
                """.formatted(escapeJson(format));
        queue.add(json);
    }

    public String escapeJson(String text){
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public void sendSync(String username, String message){
        String format = server + " " + username + " " + message;
        String json = """
                {
                "username": "Minecraft",
                "content": "%s"
                }
                """.formatted(escapeJson(format));
        send(json);
    }

    public void send(String json){
        try{
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            try(OutputStream os = conn.getOutputStream()){
                os.write(json.getBytes());
            }
            try{
                conn.getResponseCode();
            } finally {
                conn.disconnect();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    void startWebhookWorker(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            String json;
            while ((json = queue.poll()) != null){
                send(json);
            }
        }, 0L, 20L);
    }
}
