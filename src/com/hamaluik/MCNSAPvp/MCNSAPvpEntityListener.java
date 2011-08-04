package com.hamaluik.MCNSAPvp;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageByProjectileEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

public class MCNSAPvpEntityListener extends EntityListener {
	private MCNSAPvp plugin;
	
	// grab the main plug in so we can use it later
	public MCNSAPvpEntityListener(MCNSAPvp instance) {
		plugin = instance;
	}
	
	@Override
	public void onEntityDamage(EntityDamageEvent event) { 
		// make sure a player got damaged
		if(event.getEntity() instanceof Player) {
			// get the player's name
			String name = ((Player)event.getEntity()).getName();
			
			// see if another entity damaged them
			if(event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent mobevent = (EntityDamageByEntityEvent)event;
				if(mobevent.getDamager() instanceof Player) {
					// a player hit them
					if(!plugin.canPlayerPvp((Player)mobevent.getDamager())) {
						((Player)mobevent.getDamager()).sendMessage(plugin.processColours("&cYou MAY NOT pvp others!"));
						plugin.log.info("[MCNSAPvp] pvp-banned player " + ((Player)mobevent.getDamager()).getName() + " attempted pvp on player " + name);
						event.setCancelled(true);
					}
				}
			}
			// see if it was a projectile
			else if(event instanceof EntityDamageByProjectileEvent) {
				EntityDamageByProjectileEvent mobevent = (EntityDamageByProjectileEvent)event;
				if(mobevent.getDamager() instanceof Player) {
					// a player shot a bow at them
					if(!plugin.canPlayerPvp((Player)mobevent.getDamager())) {
						((Player)mobevent.getDamager()).sendMessage(plugin.processColours("&cYou MAY NOT pvp others!"));
						plugin.log.info("[MCNSAPvp] pvp-banned player " + ((Player)mobevent.getDamager()).getName() + " attempted pvp on player " + name);
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
