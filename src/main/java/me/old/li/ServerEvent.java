package me.old.li;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import me.old.li.Utilss.LIApi;
import me.old.li.Utilss.LotteryExecuter;
import me.old.li.Utilss.SelectableLotteryExecuter;
import me.old.li.Utilss.Utils;
import me.old.li.files.Saves;
import me.old.li.ui.Page;

public class ServerEvent implements Listener {

	@EventHandler
	public void onLotteryItemInteract(PlayerInteractEvent e) {
		// Check is main hand
		if (e.getHand() != EquipmentSlot.HAND)
			return;
		// Check is right click item
		if (!e.getAction().toString().toLowerCase().startsWith("right"))
			return;
		Player p = e.getPlayer();
		ItemStack handItem = e.getItem();
		// Check item not null
		if (Utils.isNullableItem(handItem))
			return;
		handItem = handItem.clone();
		handItem.setAmount(1);
		// Check item is a lottery item
		if (!LIApi.isLotteryItem(handItem))
			return;
		e.setCancelled(true);
		// Check if key lottery item not exsits
		String liKey = Utils.getLiKey(handItem);
		if (!Saves.getConfig().contains(liKey)) {
			Utils.sendPluginMessage(p, Config.LOTTERYITEM_NOT_AVAILABLE);
			return;
		}
		LotteryItem li = (LotteryItem) Saves.getConfig().get(liKey);
		if (li.isSelectable()) {
			SelectableLotteryExecuter sle = new SelectableLotteryExecuter(p, li);
			sle.execute();
			return;
		}
		LotteryExecuter le = new LotteryExecuter(p, li);
		boolean b = le.execute();

		if (b && !p.hasPermission("lotteryitem.tester"))
			p.getInventory().removeItem(handItem);
	}

	@EventHandler
	public void onCommandPro(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();

		Page page = Main.getInstance().getInvManager().getPlayerSession(p);
		if (page != null && page.isInputing()) {
			Utils.sendPluginMessage(p, Config.MESSAGE_INPUT_BLOCKING);
			e.setCancelled(true);
		}
	}
}
