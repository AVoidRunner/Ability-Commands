package me.Mastervrunner.AbilityCmd;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
//import org.jetbrains.annotations.NotNull;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.waterbending.util.WaterReturn;

//import net.minecraft.server.v1_16_R1.ItemStack;

/*
 * Implements Listener so that the server knows this is checking for events.
 */
public class AbilityCmdListener implements Listener {

	/*
	 * The event method.
	 * This specific event is looking for "PlayerAnimationEvent" which is triggered any time
	 * the server sees that the player has left-clicked. This is also triggered by other
	 * things but we are using it for the left-click function.
	 */
	
	 /*extends JavaPlugin*/
	
	private HashMap<String, String> playerAbils = new HashMap<String, String>();
	
	private String KhaosCombination = "SwiftKick | Jab | RapidPunch";
	
	private HashMap<String, String> comboCommands = new HashMap<String, String>();
	
	private boolean firstSwing = true;
	
	ArrayList<String> abilCMDS = new ArrayList<String>();
	
	//ArrayList<String> rightClickCooldown = new ArrayList<String>();
	private HashMap<String, Long> rightClickCooldown = new HashMap<String, Long>();
	
	private long MSTime;
	
	int itemAmount = 1;
	String item;
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(firstSwing) {
			getConfigs();
			firstSwing = false;
		}
		
		
		/*
		 * Variables to define.
		 * Here we need to create a player with ProjectKorra.
		 * We do this by grabbing whoever triggered the event, and then getting their bending details.
		 */
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		/*
		 * If this event has been cancelled or a player that triggered the event does not exist,
		 * then return.
		 */
		//if (event.isCancelled() || bPlayer == null) {
		//if (event.isCancelled() || bPlayer == null) {
		if (bPlayer == null) {
			return;
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("")) {
			return;
		} else if(!bPlayer.isToggled()) {
			return;
		
		}
		
		String playerUUID = player.getUniqueId().toString();
		
		String previousCombo = getCombo(playerUUID);
		
		String Combo;
		
		MSTime = System.currentTimeMillis();
		
		if(previousCombo == null) {
			if(event.getAction() == Action.LEFT_CLICK_AIR) {
				playerAbils.put(player.getUniqueId().toString(), "LEFT_CLICK: " + bPlayer.getBoundAbilityName());
			} else if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				playerAbils.put(player.getUniqueId().toString(), "LEFT_CLICK_" + event.getClickedBlock().getType().toString() + ": " + bPlayer.getBoundAbilityName());
			} else if(event.getAction() == Action.RIGHT_CLICK_AIR) {
				playerAbils.put(player.getUniqueId().toString(), "RIGHT_CLICK: " + bPlayer.getBoundAbilityName());
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				playerAbils.put(player.getUniqueId().toString(),"RIGHT_CLICK_" + event.getClickedBlock().getType().toString() + ": " + bPlayer.getBoundAbilityName());
			}
			
			rightClickCooldown.put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
			
			Bukkit.getScheduler().runTaskLater(ProjectKorra.plugin, new Runnable() {
			    @Override
			    public void run() {
			      playerAbils.remove(playerUUID);
			    }
			}, 5*20);
		} else{
			
			Long playerMSTime = rightClickCooldown.get(event.getPlayer().getUniqueId().toString());
			
			if(event.getAction() == Action.LEFT_CLICK_AIR) {
				playerAbils.put(player.getUniqueId().toString(), previousCombo + " | " + "LEFT_CLICK: " + bPlayer.getBoundAbilityName());
			} else if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
				playerAbils.put(player.getUniqueId().toString(), previousCombo + " | " + "LEFT_CLICK_" + event.getClickedBlock().getType().toString() + ": " + bPlayer.getBoundAbilityName());
			} else if(event.getAction() == Action.RIGHT_CLICK_AIR && MSTime > (playerMSTime + 250)) {
				rightClickCooldown.put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
				playerAbils.put(player.getUniqueId().toString(), previousCombo + " | " + "RIGHT_CLICK: " + bPlayer.getBoundAbilityName());
			} else if(event.getAction() == Action.RIGHT_CLICK_BLOCK && MSTime > (playerMSTime + 250)) {
				rightClickCooldown.put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
				playerAbils.put(player.getUniqueId().toString(), previousCombo + " | " + "RIGHT_CLICK_" + event.getClickedBlock().getType().toString() + ": " + bPlayer.getBoundAbilityName());
			}
		}
		
		Combo = getCombo(playerUUID);
		
		String foundCombo = checkCombo(playerUUID, Combo, player);
		
	}
	
	@EventHandler
	public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {

		if(firstSwing) {
			getConfigs();
			firstSwing = false;
		}

		Player player = event.getPlayer();
		BendingPlayer bPlayer = BendingPlayer.getBendingPlayer(player);

		/*
		 * If this event has been cancelled or a player that triggered the event does not exist,
		 * then return.
		 */
		if (event.isCancelled() || bPlayer == null) {
			return;
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase(null)) {
			return;
		} else if (bPlayer.getBoundAbilityName().equalsIgnoreCase("")) {
			return;
		} else if(!bPlayer.isToggled()) {
			return;
		
		}
		
		String playerUUID = player.getUniqueId().toString();
		
		String previousCombo = getCombo(playerUUID);
		
		String Combo;
		
		if(previousCombo == null) {
			
			if(!player.isSneaking()) {
				playerAbils.put(player.getUniqueId().toString(), "SHIFT_DOWN: " + bPlayer.getBoundAbilityName());
			} else {
				playerAbils.put(player.getUniqueId().toString(), "SHIFT_UP: " + bPlayer.getBoundAbilityName());
			}
			Bukkit.getScheduler().runTaskLater(ProjectKorra.plugin, new Runnable() {
			    @Override
			    public void run() {
			      playerAbils.remove(playerUUID);
			    }
			}, 5*20);
		} else{
			if(!player.isSneaking()) {
				playerAbils.put(player.getUniqueId().toString(), previousCombo + " | SHIFT_DOWN: " + bPlayer.getBoundAbilityName());
			} else {
				playerAbils.put(player.getUniqueId().toString(), previousCombo + " | SHIFT_UP: " + bPlayer.getBoundAbilityName());
			}
		}
		
		Combo = getCombo(playerUUID);
		
		String foundCombo = checkCombo(playerUUID, Combo, player);
		
	}
	
	public String getCombo(String playerUUID) {
		return playerAbils.get(playerUUID);
	}
	
	public String checkCombo(String playerUUID, String Combo, Player player) {
		
		//Lets say this:
		//Needs to use: LeftClick: AirBlast and have dirt in inventory
		//Config
		//comboCommands2: 'Needs[Dirt: 1, Return: 3] heal @p - LEFT_CLICK: AirBlast'
		
		for(String combo: abilCMDS) {
			if(combo.contains("Needs[")) {
				String realNeed = combo.substring(combo.indexOf("[") + 1);
				realNeed = realNeed.substring(0, realNeed.indexOf("]"));
				
				//realNeed = Dirt, Return: 3
				String fullText = "Needs[" + realNeed + "] ";
				combo = combo.replace(fullText, "");
				
				if(realNeed.contains(", ")) {
					//Realneed: Dirt, Return: 3
					String[] itemAndTime = realNeed.split(", ");
					//ItemAndTime[1]: Return: 3
					// Split[1]: 3
					String time = itemAndTime[1].split("Return: ")[1];
					int intTime = Integer.valueOf(time);
					
					//Realneed: Dirt, Return: 3
					//itemAndTime [Dirt], [Return: 3]
					//itemAndTime[0]: Dirt
					item = itemAndTime[0];
					itemAmount = 1;
					if(item.contains(": ")) {
						//Item: Dirt: 2
						itemAmount = Integer.valueOf(item.split(": ")[1]);
						item = item.replace(": " + itemAmount, "");
					}
					
					//String s = /* your material string */;
					Material m = Material.matchMaterial(item);
					ItemStack stack = new ItemStack(m, itemAmount);
					
					if(item.equals("WaterBottle")) {
						WaterReturn.emptyWaterBottle(player);
						Bukkit.getScheduler().runTaskLater(ProjectKorra.plugin, new Runnable() {
						    @Override
						    public void run() {
						    	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
						    	String command = "minecraft:give " + player.getName() + " minecraft:potion{Potion:\"minecraft:water\"}" + itemAmount;
						    	Bukkit.dispatchCommand(console, command);
						    }
						}, intTime);
					} else if(player.getInventory().containsAtLeast(stack, 1)) {
						player.getInventory().remove(stack);
						Bukkit.getScheduler().runTaskLater(ProjectKorra.plugin, new Runnable() {
						    @Override
						    public void run() {
						    	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
						    	String command = "minecraft:give " + player.getName() + item + " " + itemAmount;
						    	Bukkit.dispatchCommand(console, command);
						    }
						}, intTime);
					}
				}
				
				
			}
			String[] combos = combo.split(" - ");
			String cmds = combos[0];
			String realCombo = combos[1];
	
			if(Combo.equals(realCombo)) {
				
				String[] allCmds = cmds.split(", ");
				
				for(String cmd: allCmds) {
						player.performCommand(cmd);
				}
				
				return "Found combo: " + Combo;
			}
		}
			
		
		return "Did not find combo: " + Combo;
			
	}
	
	public void getConfigs(){
		boolean x = true;
		for(int i = 1; i <= 100; i++) {
		
			String abilCMDSString;
			abilCMDSString = ConfigManager.getConfig().getString("ExtraAbilities.Mastervrunner.AbCmd.comboCommands"+i);
			if(abilCMDSString == null) {
				break;
			} else {
				abilCMDS.add(abilCMDSString);
			}
		}
		
		
		
	}
}