package org.corpse.telling.death;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MainQuija extends JavaPlugin implements Listener{
	
	private Thread apiThread;
	
	@Override
	public void onEnable() {

	    iniciarApi();

	    getServer().getPluginManager().registerEvents(this, this);
	    
	    if (!getDataFolder().exists()) getDataFolder().mkdirs();	    
	    
	    CommandsCorpse commands = new CommandsCorpse(this);
	    getCommand("setMongo").setExecutor(commands);
	    getCommand("getMongo").setExecutor(commands);
	}
	
	public void iniciarApi() {
		apiThread = new Thread(() -> {
	        ApiServidor.iniciar();
	    });
	    apiThread.setDaemon(true);
	    apiThread.start();
	}

	@Override
	public void onDisable() {
	    ApiServidor.detener();
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
	    Player player = event.getEntity();
	    Location deathLocation = player.getLocation();
	    String deathCause = event.getDeathMessage();
	    String time = LocalDateTime.now().toString();

	    Document dLocation = new Document()
	        .append("X", deathLocation.getBlockX())
	        .append("Y", deathLocation.getBlockY())
	        .append("Z", deathLocation.getBlockZ());

	    List<Document> inventoryList = new ArrayList<>();
	    for (ItemStack item : player.getInventory().getContents()) {
	        if (item != null && item.getType() != Material.AIR) {
	            Document dItem = new Document();
	            String displayName = item.hasItemMeta() && item.getItemMeta().hasDisplayName()
	                ? item.getItemMeta().getDisplayName()
	                : item.getType().toString();
	            dItem.append("Item", displayName);
	            dItem.append("Amount", item.getAmount());

	            if (item.getItemMeta() != null && item.getItemMeta().hasEnchants()) {
	                List<Document> dEnchantsList = new ArrayList<>();
	                for (Entry<Enchantment, Integer> ench : item.getEnchantments().entrySet()) {
	                    Document dEnchant = new Document()
	                        .append("Name", ench.getKey().getTranslationKey())
	                        .append("Level", ench.getValue());
	                    dEnchantsList.add(dEnchant);
	                }
	                dItem.append("Enchantments", dEnchantsList);
	            }

	            if (item.getType().getMaxDurability() > 1) {
	                Damageable damageable = (Damageable) item.getItemMeta();
	                Document dDurability = new Document()
	                    .append("Max", item.getType().getMaxDurability())
	                    .append("Current", item.getType().getMaxDurability() - damageable.getDamage());
	                dItem.append("Durability", dDurability);
	            }
	            inventoryList.add(dItem);
	        }
	    }

	    Document dFull = new Document()
	        .append("Time", time)
	        .append("Player", player.getName())
	        .append("Death", deathCause)
	        .append("Location", dLocation)
	        .append("Inventory", inventoryList);

	    File ipMongo = new File(this.getDataFolder(), "MongoIp");
	    try {
	        if (!ipMongo.exists()) {
	            ipMongo.createNewFile();
	        }
	        if (ipMongo.length() == 0) {
	            try (FileWriter writer = new FileWriter(ipMongo)) {
	                writer.write("mongodb://<user>:<password>@<host>:27017");
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        return;
	    }

	    try {
	        String[] parts = Files.readString(ipMongo.toPath()).split(" ");
	        try (MongoClient mongoClient = MongoClients.create(parts[0])) {
	            MongoDatabase database = mongoClient.getDatabase(parts[1]);
	            MongoCollection<Document> collection = database.getCollection(parts[2]);
	            collection.insertOne(dFull);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
