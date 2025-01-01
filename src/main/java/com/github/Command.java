package com.github;

import com.github.NlFramework.utils.CL;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        if (command.getName().equalsIgnoreCase("ifreload")) {
            NaleumItemFavorites.instance.reloadConfig();
            sender.sendMessage(CL.G + "Successfully reloaded Plugin");
            return true;
        }
        return false;
    }
}