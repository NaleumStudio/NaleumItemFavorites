package com.github;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.github.NlFramework.utils.CL;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class NaleumItemFavorites extends JavaPlugin implements Listener {
    private final Map<UUID, Boolean> Cooldown = new HashMap<>();
    public static NaleumItemFavorites instance;

    @Override
    public void onEnable() {
        CL.Send("&eNaleumItemFavorites &7Enabled", this);
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        instance = this;
        getCommand("ifreload").setExecutor(new Command());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack Item = event.getCurrentItem();
        if (Item == null || Item.getType() == Material.AIR) { return; }
        if (!(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT)) { return; }

        String Rmsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("favorites.register.message", "&eItem added to favorites."));
        String URmsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("favorites.unregister.message", "&cItem removed from favorites."));
        String txt = ChatColor.translateAlternateColorCodes('&', getConfig().getString("favorites.text", "&eAdded to favorites"));

        String Rsound = getConfig().getString("favorites.register.sound", "minecraft:block.note_block.bell");
        String URsound = getConfig().getString("favorites.unregister.sound", "minecraft:block.note_block.bass");

        int time = getConfig().getInt("favorites.cooldown", 1);

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (Cooldown.getOrDefault(player.getUniqueId(), false)) { return; }

        ItemMeta meta = Item.getItemMeta();
        List<String> lore = Objects.requireNonNull(meta).getLore();
        if (lore == null) { lore = new ArrayList<>(); }
        if (!lore.isEmpty() && lore.get(0).equals(txt)) {
            if (!(URmsg.isEmpty())) {
                player.sendMessage(CL.W + URmsg);
            }
            lore.remove(0);
            player.playSound(player.getLocation(), URsound, 1.0f, 1.0f);
        } else {
            if (!(Rmsg.isEmpty())) {
                player.sendMessage(CL.W + Rmsg);
            }
            lore.add(0, CL.W + txt);
            player.playSound(player.getLocation(), Rsound, 1.0f, 1.0f);
        }
        meta.setLore(lore);
        Item.setItemMeta(meta);

        Cooldown.put(player.getUniqueId(), true);

        new BukkitRunnable() {
            @Override
            public void run() {
                Cooldown.put(player.getUniqueId(), false);
            }
        }.runTaskLater(this, time * 20L);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        ItemStack item = event.getItemDrop().getItemStack();

        if (item == null || !item.hasItemMeta()) { return; }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) { return; }

        String txt = ChatColor.translateAlternateColorCodes('&', getConfig().getString("favorites.text", "&eAdded to favorites"));

        if (Objects.requireNonNull(meta.getLore()).get(0).equals(txt)) {
            event.setCancelled(true);
        }
    }
}
