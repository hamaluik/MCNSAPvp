package com.hamaluik.MCNSAPvp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MCNSAPvp extends JavaPlugin {
	// the basics
	Logger log = Logger.getLogger("Minecraft");
	public PermissionHandler permissionHandler;
	
	// listeners
	private MCNSAPvpEntityListener entityListener = new MCNSAPvpEntityListener(this);
	
	// data
	private List<String> denyGroups = new ArrayList<String>();
	
	// startup routine..
	public void onEnable() {
		setupPermissions();
		loadConfiguration();
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Lowest, this);
		
		log.info("[MCNSAPvp] enabled");
	}

	// shutdown routine
	public void onDisable() {
		log.info("[MCNSAPvp] disabled");
	}
	
	// load the permissions plugin..
	private void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		
		if(this.permissionHandler == null) {
			if(permissionsPlugin != null) {
				this.permissionHandler = ((Permissions)permissionsPlugin).getHandler();
				log.info("[MCNSAPvp] permissions successfully loaded");
			} else {
				log.info("[MCNSAPvp] permission system not detected, defaulting to OP");
			}
		}
	}
	
	// just an interface function for checking permissions
	// if permissions are down, default to OP status.
	public boolean hasPermission(Player player, String permission) {
		if(permissionHandler == null) {
			return player.isOp();
		}
		else {
			return (permissionHandler.has(player, permission));
		}
	}
	
	// just an interface function for getting user's groups
	// if permissions are down, default to none.
	public String[] getPlayerGroups(Player player) {
		if(permissionHandler == null) {
			return null;
		}
		else {
			return permissionHandler.getGroups(player.getWorld().getName(), player.getName());
		}
	}
	
	public boolean canPlayerPvp(Player player) {
		String[] groups = getPlayerGroups(player);
		if(groups != null && groups.length > 0) {
			// should be their highest rank
			String group = groups[0];
			if(denyGroups.contains(group)) {
				return false;
			}
		}
		return true;
	}
	
	private void checkConfiguration() {
		// first, check to see if the file exists
		File configFile = new File(getDataFolder() + "/config.yml");
		if(!configFile.exists()) {
			// file doesn't exist yet :/
			log.info("[MCNSAPvp] config file not found, will attempt to create a default!");
			new File(getDataFolder().toString()).mkdir();
			try {
				// create the file
				configFile.createNewFile();
				// and attempt to write the defaults to it
				FileWriter out = new FileWriter(getDataFolder() + "/config.yml");
				out.write("---\n");
				out.write("deny-groups:\n");
				out.write("    - 'Peon'\n");
				out.close();
			} catch(IOException ex) {
				// something went wrong :/
				log.info("[MCNSAPvp] error: config file does not exist and could not be created");
			}
		}
	}

	private void loadConfiguration() {
		// make sure the config exists
		// and if it doesn't, make it!
		this.checkConfiguration();
		
		// get the configuration..
		Configuration config = getConfiguration();
		denyGroups = config.getStringList("deny-groups", denyGroups);
		for(int i = 0; i < denyGroups.size(); i++) {
			log.info("[MCNSAPvp] Denied pvp rights for group: " + denyGroups.get(i));
		}
	}
	
	// allow for colour tags to be used in strings..
	public String processColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
	}
	
	// strip colour tags from strings..
	public String stripColours(String str) {
		return str.replaceAll("(&([a-f0-9]))", "");
	}
}
