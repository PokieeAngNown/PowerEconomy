package cn.postudio.poeconomy;

import cn.postudio.pocore.PlayerDataManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class EconomyManager {
    private static final List<String> EconomyTypeList = PoEconomy.getPlugin().getConfig().getStringList("EconomyType");
    public static List<String> getEconomyTypeList(){
        return EconomyTypeList;
    }

    private static FileConfiguration getPlayerData(@NotNull Player player){
        return PlayerDataManager.getPlayerData(player);
    }

    public static void initializePlayerEconomy(Player player){
        try {
            PlayerDataManager.writePlayerData(player, "Economy", getEconomyTypeList());
            for (String s : getEconomyTypeList()) {
                PlayerDataManager.writePlayerData(player, "Economy." + s, 0d);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void handlePlayerEconomy(Player player){
        // 获取玩家数据中只含 "Economy" 字眼的条目
        Set<String> economySet = getPlayerData(player).getKeys(true);
        List<String> dataList = new ArrayList<>(economySet);

        // 移除不含 "Economy" 字眼的条目
        dataList.removeIf(s -> !s.contains("Economy"));

        // 只保留含 "Economy" 的条目
        List<String> playerEconomyList = new ArrayList<>();
        for (String s : dataList) {
            String[] strings = s.split("\\.");
            if (strings.length > 1){
                playerEconomyList.add(strings[1]);
            }
        }
        // 检查多余或缺少的字符串
        Set<String> modelSet = new HashSet<>(getEconomyTypeList());
        Set<String> playerSet = new HashSet<>(playerEconomyList);

        // 找到缺少的字符串
        Set<String> missingInPlayer = new HashSet<>(modelSet);
        missingInPlayer.removeAll(playerSet);

        // 找到多余的字符串
        Set<String> extraInPlayer = new HashSet<>(playerSet);
        extraInPlayer.removeAll(modelSet);

        //处理缺少和多余的字符串
        if (!missingInPlayer.isEmpty()){
            for (String s : missingInPlayer){
                try {
                    PlayerDataManager.writePlayerData(player, "Economy." + s, 0d);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (!extraInPlayer.isEmpty()){
            for (String s : extraInPlayer){
                try {
                    PlayerDataManager.writePlayerData(player, "Economy." + s, null);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public static synchronized @NotNull Double getEconomy(Player player, String economyType){
        return PlayerDataManager.getPlayerData(player).getDouble("Economy." + economyType);
    }
    public static synchronized void senderPayTarget(Player sender, Player target, String economyType, @NotNull Double value) {
        double senderEconomy = getEconomy(sender, economyType);
        double targetEconomy = getEconomy(target, economyType);
        try {
            PlayerDataManager.writePlayerData(sender, "Economy." + economyType, senderEconomy - value);
            PlayerDataManager.writePlayerData(target, "Economy." + economyType, targetEconomy + value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public static synchronized void OPPayTarget(Player target, String economyType, @NotNull Double value){
        double targetEconomy = getEconomy(target, economyType);
        try {
            PlayerDataManager.writePlayerData(target, "Economy." + economyType, targetEconomy + value);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static synchronized void OPTakeTarget(Player target, String economyType, @NotNull Double value){
        double targetEconomy = getEconomy(target, economyType);
        try {
            PlayerDataManager.writePlayerData(target, "Economy." + economyType, targetEconomy - value);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
