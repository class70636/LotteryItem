package me.old.li.ui;

import org.bukkit.event.inventory.InventoryCloseEvent;

import me.old.li.LotteryItem;
import me.old.li.optionObjects.Gift;

public class DeleteGiftConfirm extends DeleteConfirmPage {

	private Gift gift;

	public DeleteGiftConfirm(String title, int size, Gift gift) {
		super(title, size);
		this.gift = gift;
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		if (confirm) {
			LotteryItem li = this.previousPage.getLotteryItem();
			li.getGifts().remove(gift);
			li.sortGifts();
		}
		this.openPrevioudPage(p);
	}

}
