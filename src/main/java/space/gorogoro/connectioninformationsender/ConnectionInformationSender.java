package space.gorogoro.connectioninformationsender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ConnectionInformationSender extends JavaPlugin implements Listener{
  
  @Override
  public void onEnable(){
    try{
      getServer().getPluginManager().registerEvents(this, this);
      getLogger().info("The Plugin Has Been Enabled!");
      
      // 設定ファイルが無ければ作成
      File configFile = new File(this.getDataFolder() + "/config.yml");
      if(!configFile.exists()){
        this.saveDefaultConfig();
      }
      
    } catch (Exception e){
      getLogger().warning(e.toString());
    }
  }
  
  @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
  public void on(PlayerJoinEvent event) {
      try{
      Player p = event.getPlayer();
      FileConfiguration conf = getConfig();
      String strUrl = conf.getString("api-url");
      URL url = new URL(strUrl);
      HttpURLConnection connection = null;
      connection = (HttpURLConnection) url.openConnection();
      connection.setDoOutput(true);
      connection.setRequestMethod("POST");
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8));
      String strPost = "ip=" + p.getAddress().getAddress().toString().replace("/", "") + "&tag=" + p.getName();
      writer.write(strPost);
      writer.flush();
      if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
          getLogger().warning("Api '" + strUrl + "' is not valid.");
      }
      connection.disconnect();
    } catch (Exception e){
        getLogger().warning("Api request error.msg=" + e.getMessage());
    }
  }
  
  @Override
  public void onDisable(){
    getLogger().info("The Plugin Has Been Disabled!");
  }
}
