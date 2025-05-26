package org.corpse.telling.death;

import static spark.Spark.*;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.World;

import com.google.gson.Gson;

public class ApiServidor {
	
	private static final Gson gson = new Gson();

    public static void iniciar() {
        port(4567);

        get("/ping", (req, res) -> {
            res.type("text/plain");
            return "API encontrada";
        });
        
        get("/ping", (req, res) -> {
            res.type("text/plain");
            return "API funcionando correctamente";
        });
        
        get(("/players"), (req, res) -> {
        	List<String> playerList = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        	res.type("application/json");
            return gson.toJson(playerList);
        });

        post("/command", (req, res) -> {
        	World world = Bukkit.getWorlds().get(0);
            String command = req.body();
            Bukkit.getLogger().info(command);
            switch (command.toLowerCase()) {
            case "day":
                Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                	world.setTime(1000);
            	});
                Bukkit.getLogger().info("Tiempo cambiado a día.");
                break;
            case "night":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
            		world.setTime(13000);
            	});
                Bukkit.getLogger().info("Tiempo cambiado a noche.");
                break;
            case "noon":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    world.setTime(6000);
            	});
                Bukkit.getLogger().info("Tiempo cambiado a mediodía.");
                break;
            case "midnight":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    world.setTime(13000);
            	});
                Bukkit.getLogger().info("Tiempo cambiado a medianoche.");
                break;
            case "clear":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
            		world.setStorm(false);
                    world.setThundering(false);
            	});
                Bukkit.getLogger().info("Clima cambiado a despejado.");
                break;
            case "rain":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
            		world.setStorm(true);
                    world.setThundering(false);
            	});
                Bukkit.getLogger().info("Clima cambiado a lluvia.");
                break;
            case "storm":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
            		world.setStorm(true);
                    world.setThundering(true);
            	});
                Bukkit.getLogger().info("Clima cambiado a tormenta.");
                break;
            case "easy":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    world.setDifficulty(org.bukkit.Difficulty.EASY);
            	});
                Bukkit.getLogger().info("Dificultad cambiada a fácil.");
                break;
            case "normal":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    world.setDifficulty(org.bukkit.Difficulty.NORMAL);
            	});
                Bukkit.getLogger().info("Dificultad cambiada a normal.");
                break;
            case "hard":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    world.setDifficulty(org.bukkit.Difficulty.HARD);
            	});
                Bukkit.getLogger().info("Dificultad cambiada a difícil.");
                break;
            case "peaceful":
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    world.setDifficulty(org.bukkit.Difficulty.PEACEFUL);
            	});
                Bukkit.getLogger().info("Dificultad cambiada a pacífica.");
                break;
            default:
            	Bukkit.getScheduler().runTask(JavaPlugin.getProvidingPlugin(MainQuija.class), () -> {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
            	});
                Bukkit.getLogger().info("Comando ejecutado: " + command);
                break;
        }
            return "Executed";
        });
    }

    public static void detener() {
        stop();
    }
}
