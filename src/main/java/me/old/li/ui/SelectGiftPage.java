package me.old.li.ui;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.Utilss.ItemBuilder;
import me.old.li.Utilss.LICds;
import me.old.li.Utilss.SelectableLotteryExecuter;
import me.old.li.optionObjects.Gift;

public class SelectGiftPage extends Page {

	private SelectableLotteryExecuter sle;

	public SelectGiftPage(String title, int size, LotteryItem li, SelectableLotteryExecuter sle) {
		super(title, size);
		// TODO Auto-generated constructor stub
		this.lotteryItem = li;
		this.sle = sle;
	}

	@Override
	public void init() {
		List<Gift> gifts = lotteryItem.getGifts();
		for (int i = 0; i < gifts.size(); i++) {
			new Button(i) {

				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
					Gift g = gifts.get(this.getSlot()).draw();

					ItemStack hand = p.getInventory().getItemInMainHand().clone();
					hand.setAmount(1);
					if (!p.hasPermission("lotteryitem.tester"))
						p.getInventory().removeItem(hand);

					// 加入冷卻以及刪除物品
					if (sle.getLottery().hasCoolDown()) {
						LICds liCds = Main.liCds.get(p);
						liCds.addLotteryItem(sle.getLottery().getItemId(), sle.getLottery().getCoolDown());
					}
					if (sle.getLottery().hasKey()) {
						sle.getLottery().getKey().executeRemove(p);
						sle.showConsumeKey();
					}
					// 顯示消耗物品
					sle.showUseDisplay();
					// 顯示獲得物品
					sle.showGiftDisplay(g);
					// 播放聲音
					if (sle.getLottery().hasSoundSet())
						sle.playSound();
					// 給物品
					g.executeGive(p);

					p.closeInventory();
					// 清除不良物品
					if (!gifts.get(this.getSlot()).isSingleAmount()) {
						if (p.getInventory().contains(display))
							p.getInventory().remove(display);
						p.updateInventory();
					}
				}

				@Override
				protected void setDisplayItem() {
					ItemBuilder ib = new ItemBuilder(gifts.get(this.getSlot()).buildItem());
					if (!gifts.get(this.getSlot()).isSingleAmount())
						ib.setName(gifts.get(this.getSlot()).getDisplayName());
					ItemStack dis = ib.getItem();
					this.display = dis;
				}

			};
		}

		this.buttons.forEach((k, v) -> v.refresh());
	}

}
