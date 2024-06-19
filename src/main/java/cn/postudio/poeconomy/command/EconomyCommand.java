package cn.postudio.poeconomy.command;

import cn.postudio.pocore.PluginMessageManager;
import cn.postudio.poeconomy.EconomyManager;
import cn.postudio.poeconomy.PoEconomy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EconomyCommand implements CommandExecutor, TabCompleter {

    /*
        /economy [target] [economyType] get
        /economy [target] [economyType] <pay/take> [value]
     */
    Player sender;
    public static Player target;
    String economyType;
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (commandSender instanceof Player){
            sender = (Player) commandSender;
        }
        if (args.length < 3){
            PluginMessageManager.sendMessageToPlayer(PoEconomy.getPlugin(), "InvalidCommand", sender);
            return true;
        }
        target = Bukkit.getPlayerExact(args[0]);
        economyType = args[1];
        if (!EconomyManager.getEconomyTypeList().contains(economyType)){
            PluginMessageManager.sendMessageToPlayer(PoEconomy.getPlugin(), "Economy.TypeNotFound", sender);
            return true;
        }
        double value;
        switch (args[2]){
            case "get" :
                if (args.length != 3){
                    PluginMessageManager.sendMessageToPlayer(PoEconomy.getPlugin(), "InvalidCommand", sender);
                    break;
                }
                sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                        PoEconomy.getPlugin()).getString("Economy.Get"))
                        .replace("%[Target]", target.getName())
                        .replace("%[Economy]", EconomyManager.getEconomy(target, economyType) + economyType)
                );
                break;
            case "pay":
                if (args.length != 4 ){
                    PluginMessageManager.sendMessageToPlayer(PoEconomy.getPlugin(), "InvalidCommand", sender);
                    break;
                }
                value = Double.parseDouble(args[3]);
                if (sender.isOp()){
                    EconomyManager.OPPayTarget(target, economyType, value);
                    sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                    PoEconomy.getPlugin()).getString("Economy.Pay"))
                            .replace("%[Target]", target.getName())
                            .replace("%[Economy]", value + economyType)
                    );
                    sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                    PoEconomy.getPlugin()).getString("Economy.Get"))
                            .replace("%[Target]", target.getName())
                            .replace("%[Economy]", EconomyManager.getEconomy(target, economyType) + economyType)
                    );
                }else{
                    if (EconomyManager.isPayable(economyType)){
                        if (target  == sender){
                            sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                    PoEconomy.getPlugin()).getString("Economy.SelfPay")
                            ));
                            return true;
                        }
                        if (EconomyManager.getEconomy(sender, economyType) < 0){
                            sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                    PoEconomy.getPlugin()).getString("Economy.NoSuchEconomy")
                            ));
                            return true;
                        }
                        EconomyManager.senderPayTarget(sender, target, economyType, value);
                        sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                        PoEconomy.getPlugin()).getString("Economy.Pay"))
                                .replace("%[Target]", target.getName())
                                .replace("%[Economy]", value + economyType)
                        );
                    }else{
                        sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                PoEconomy.getPlugin()).getString("Economy.Unpayable"))
                        );
                        return true;
                    }

                }
                break;
            case "take":
                if (args.length != 4){
                    PluginMessageManager.sendMessageToPlayer(PoEconomy.getPlugin(), "InvalidCommand", sender);
                    break;
                }
                value = Double.parseDouble(args[3]);
                if (sender.isOp()){
                    EconomyManager.OPTakeTarget(target, economyType, value);
                }
                sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                        PoEconomy.getPlugin()).getString("Economy.Take"))
                        .replace("%[Target]", target.getName())
                        .replace("%[Economy]", value + economyType)
                );
                sender.sendMessage(Objects.requireNonNull(PluginMessageManager.getLang(
                                PoEconomy.getPlugin()).getString("Economy.Get"))
                        .replace("%[Target]", target.getName())
                        .replace("%[Economy]", EconomyManager.getEconomy(target, economyType) + economyType)
                );
                break;
            default:
                PluginMessageManager.sendMessageToPlayer(PoEconomy.getPlugin(), "InvalidCommand", sender);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> list = new ArrayList<>();
        List<Player> players = new ArrayList<>(PoEconomy.getPlugin().getServer().getOnlinePlayers());
        if (args.length == 1){
            for (Player player : players) {
                list.add(player.getName());
            }
            return list;
        }
        if (args.length == 2){
            if (commandSender.isOp()){
                return EconomyManager.getEconomyTypeList();
            }else{
                return EconomyManager.getPayableEconomyTypeList();
            }
        }
        if (args.length == 3){
            if (commandSender.isOp()){
                return Arrays.asList("get", "pay", "take");
            }else{
                return Collections.singletonList("pay");
            }
        }
        if (args.length >= 4){
            return list;
        }
        return null;
    }
}
