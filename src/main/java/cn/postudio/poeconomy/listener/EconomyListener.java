package cn.postudio.poeconomy.listener;

import cn.postudio.pocore.PlayerDataManager;
import cn.postudio.poeconomy.EconomyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class EconomyListener implements Listener {

    @EventHandler
    public void onPlayerFirstJoinServer(PlayerJoinEvent e){
        Player player = e.getPlayer();
        boolean b = PlayerDataManager.getPlayerData(player).isSet("Economy");
        if (!b){
            EconomyManager.initializePlayerEconomy(player);
        }else{
            EconomyManager.handlePlayerEconomy(player);
        }
    }
}
