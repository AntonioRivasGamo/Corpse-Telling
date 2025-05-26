package org.corpse.telling.death;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.mongodb.MongoSecurityException;
import com.mongodb.MongoSocketException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class CommandsCorpse implements CommandExecutor{
	
	private final File ipMongo;

    public CommandsCorpse(JavaPlugin plugin) {
        this.ipMongo = new File(plugin.getDataFolder(), "MongoIp");
        
        try {
            if (!ipMongo.exists()) {
                ipMongo.createNewFile();
            }

            if (ipMongo.length() == 0) {
                try (FileWriter writer = new FileWriter(ipMongo)) {
                    writer.write("mongodb://<user>:<password>@<host>:27017/<database>");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("setMongo")) {
			if(args.length == 5) {
				try {
					setMongo(args);
					sender.sendMessage("Mongo database changed");
					return true;
				} catch (UnknownHostException e) {
					sender.sendMessage("ERROR: Url is wrong");
					return true;
				} catch (IOException e) {
					sender.sendMessage("ERROR: " + e.getMessage());
					return true;
				}catch (MongoSecurityException e) {
					sender.sendMessage("ERROR: User or password is wrong");
					return true;
				} catch (Exception e) {
					sender.sendMessage("ERROR: " + e.getClass());
					return true;
				}
				
			}
				
		}
		if(command.getName().equalsIgnoreCase("getMongo")) {
			try {
				String ip = getMongo();
				sender.sendMessage(ip);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				sender.sendMessage("ERROR");
				return true;
			}
		}
		return false;
	}

	private void setMongo(String[] args) throws IOException, UnknownHostException {
	    String ip = "mongodb://" + args[0] + ":" + args[1] + "@" + args[2] + ":27017/";

	    try {
	        InetAddress address = InetAddress.getByName(args[2]);
	        address.getHostAddress();
	    } catch (UnknownHostException e) {
	        throw new UnknownHostException("Invalid host: " + args[2]);
	    }

	    try (MongoClient mongoClient = MongoClients.create(ip)) {
	        boolean dbExists = false;
	        for (String dbName : mongoClient.listDatabaseNames()) {
	            if (dbName.equals(args[3])) {
	                dbExists = true;
	                break;
	            }
	        }

	        if (!dbExists) {
	            throw new IOException("Database does not exist");
	        }

	        boolean collectionExists = false;
	        var database = mongoClient.getDatabase(args[3]);
	        for (String collectionName : database.listCollectionNames()) {
	            if (collectionName.equals(args[4])) {
	                collectionExists = true;
	                break;
	            }
	        }

	        if (!collectionExists) {
	            throw new IOException("Collection does not exist");
	        }

	        try (FileWriter writer = new FileWriter(ipMongo)) {
	            writer.write(ip + " " + args[3] + " " + args[4]);
	        }
	    } catch (MongoSocketException e) {
	        throw new MongoSocketException("Error connecting to " + args[2] + ": " + e.getMessage(), null, e.getCause());
	    }
	}

	private String getMongo() throws IOException {
		return Files.readString(ipMongo.toPath());
	}

}