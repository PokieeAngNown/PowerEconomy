package cn.postudio.poeconomy;

import cn.postudio.poeconomy.command.EconomyCommand;
import cn.postudio.poeconomy.listener.EconomyListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public final class PoEconomy extends JavaPlugin {
    private final Player author = Bukkit.getPlayer(UUID.fromString("8b900ed4-36e1-4de4-99b2-ac10409177d6"));
    public static Plugin getPlugin(){
        return Bukkit.getPluginManager().getPlugin("PoEconomy");
    }

    public @NotNull PluginLogger getPluginLogger(){
        return (PluginLogger) getPlugin().getLogger();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        initializePlugin();
        checkDepend();
        regListener();
        regCommand();
        regTab();
        getPluginLogger().info("Plugin has been enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getPluginLogger().info("Plugin has been disabled");
    }

    private void checkDepend(){
        if (Bukkit.getPluginManager().getPlugin("PoCore") != null){
            getPluginLogger().info("The PoCore has check in!");
        }else{
            getServer().getPluginManager().disablePlugin(this);
            getPluginLogger().warning("The harddepend PoCore does not enable");
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderSupport().register();
            getPluginLogger().info("The PlaceholderAPI has check in!");
        }
    }
    private void regListener(){
        getServer().getPluginManager().registerEvents(new EconomyListener(), this);
    }
    private void regCommand(){
        Objects.requireNonNull(this.getCommand("economy")).setExecutor(new EconomyCommand());
    }
    private void regTab(){
        Objects.requireNonNull(this.getCommand("economy")).setTabCompleter(new EconomyCommand());
    }

    private void initializePlugin(){
        if (!new File(getPlugin().getDataFolder(), "language.yml").exists()){
            saveResource("language.yml", false);
        }
        if (!new File(getPlugin().getDataFolder(), "config.yml").exists()){
            saveResource("config.yml", false);
        }
    }
}
