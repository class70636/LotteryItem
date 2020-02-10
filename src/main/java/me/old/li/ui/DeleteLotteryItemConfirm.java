package me.old.li.ui;

import org.bukkit.event.inventory.InventoryCloseEvent;

import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.files.Saves;

public class DeleteLotteryItemConfirm extends DeleteConfirmPage {

	private LotteryItem li;

	public DeleteLotteryItemConfirm(String title, int size, LotteryItem li) {
		super(title, size);
		this.li = li;
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		if (confirm) {
			if (Saves.getConfig().contains(li.getItemId())) {
				Saves.getConfig().set(li.getItemId(), null);
				Saves.saveConfig();
			}
			Main.getInstance().getServerLotteryItems().getLotteryItems().remove(li);
		}
//		Page pre = this.previousPage;
//		pre.open(p);
		this.openPrevioudPage(p);
	}

}
