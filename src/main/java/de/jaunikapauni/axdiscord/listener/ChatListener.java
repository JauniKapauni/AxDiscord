package de.jaunikapauni.axdiscord.listener;

import de.jaunikapauni.axdiscord.AxDiscord;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;

public class ChatListener implements Listener {
    AxDiscord reference;
    public ChatListener(AxDiscord reference){
        this.reference = reference;
    }
    @EventHandler
    public void onChatMessage(AsyncChatEvent e){
        Player p = e.getPlayer();
        String msg = PlainTextComponentSerializer.plainText().serialize(e.message());
        reference.send(p.getName(), msg);
    }
}
