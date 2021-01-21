package me.old.li.Utilss;

import org.bukkit.entity.Player;

import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.ui.SelectGiftPage;

public class SelectableLotteryExecuter extends LotteryExecuter {

	public SelectableLotteryExecuter(Player p, LotteryItem li) {
		super(p, li);
	}

	@Override
	public boolean execute() {
		if (checkFirst())
			return false;
		int size = ((li.getGifts().size() - 1) / 9) + 1;
		SelectGiftPage sgp = new SelectGiftPage(Config.GUI_SELECTABLE_GIFT_TITLE, size, li, this);
		sgp.open(p);
		return true;
	}

}
