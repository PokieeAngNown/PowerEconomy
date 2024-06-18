package cn.postudio.poeconomy;

import cn.postudio.poeconomy.command.EconomyCommand;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlaceholderSupport extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "PoEconomy";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(",", PoEconomy.getPlugin().getDescription().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        List<String> economyTypeList = EconomyManager.getEconomyTypeList();
        for (String s : economyTypeList) {
            if (params.equalsIgnoreCase(s)) {
                return EconomyManager.getEconomy(player.getPlayer(), s) + " " + params;
            }
        }
        Player p = EconomyCommand.target;
        if (params.equalsIgnoreCase(p.getName())){
            return p.getName();
        }
        return null;
    }
}
