package de.jaunikapauni.axdiscord.listener;

import de.jaunikapauni.axdiscord.AxDiscord;
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
    public void onChatMessage(PlayerChatEvent e){
        Player p = e.getPlayer();
        String msg = PlainTextComponentSerializer.plainText().serialize(Component.text(e.getMessage()));
        reference.send(p.getName(), msg);
    }
}
