package me.old.li.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.old.li.Config;
import me.old.li.InputType;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.Utilss.ItemBuilder;
import me.old.li.Utilss.LIApi;
import me.old.li.Utilss.LotteryItemBuilder;
import me.old.li.Utilss.Utils;
import me.old.li.Utilss.XMaterial;
import me.old.li.files.Saves;

public class ManagerPage extends Page {

	private Main instance;
	private Page page;
	private int currentPage;
	private List<LotteryItem> liList;

	public ManagerPage(String title, int size, Main instance) {
		super(title, size);
		this.instance = instance;
		this.page = this;
		this.currentPage = 0;
	}

	private void initLiList() {
		liList = new ArrayList<>();
		for (String key : Saves.getConfig().getKeys(false)) {
			LotteryItem li = (LotteryItem) Saves.getConfig().get(key);
			liList.add(li.clone());
		}
		liList.addAll(instance.getServerLotteryItems().getLotteryItems());

		Collections.sort(liList, new Comparator<LotteryItem>() {
			@Override
			public int compare(LotteryItem l1, LotteryItem l2) {
				return l1.getItemId().compareTo(l2.getItemId());
			}
		});
	}

	@Override
	public void init() {
//		this.totalPage = (instance.getServerLotteryItems().getLotteryItems().size() + 45) / 45;
		this.initLiList();

		setWhiteEmpty();
		setBlackEmpty();
		setGiftButtons();

		this.buttons.forEach((k, v) -> v.refresh());
	}

	private void setWhiteEmpty() {
		for (int i = 0; i < this.inv.getSize() - 9; i++) {
			new Button(i) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial(), " ").getItem();
				}
			};
		}
	}

	private void setBlackEmpty() {
		for (int i = this.inv.getSize() - 9; i < this.inv.getSize(); i++) {
			new Button(i) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(),
							Config.BUTTON_NOTUSE_NAME).getItem();
				}
			};
		}
	}

	private void setGiftButtons() {
		for (int i = 0; i < 45; i++) {
			int realIndex = currentPage * 45 + i;
			if (realIndex >= liList.size()) {
				new Button(i) {

					@Override
					protected void execute(Player p, InventoryClickEvent e) {
						e.setCancelled(true);
						new InputHandler(p, page, InputType.TEXT) {

							@Override
							boolean setValue(String id) {
								if (LIApi.isExists(id)) {
									Utils.sendPluginMessage(p, Config.MESSAGE_LOTTERYITEM_EXISTS);
									return false;
								}
								// instance.getServerLotteryItems().getLotteryItems().add(new LotteryItem(id));
								return true;
							}

						}.enable();
					}

					@Override
					protected void setDisplayItem() {
						this.display = new ItemBuilder(XMaterial.CHEST_MINECART.parseMaterial(),
								Config.BUTTON_CREATE_LI_NAME, Config.BUTTON_CREATE_LI_LORE).getItem();
					}

				};
				break;
			}

//			instance.getServerLotteryItems().sort();
//			LotteryItem li = instance.getServerLotteryItems().getLotteryItems().get(realIndex);
			LotteryItem li = liList.get(realIndex);

			new Button(i) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
					if (e.isRightClick() && e.isShiftClick()) {
						DeleteLotteryItemConfirm dcp = new DeleteLotteryItemConfirm(Config.GUI_DELETE_GIFT_TITLE, 3,
								li);
						page.openNextPage(dcp, p);
					} else if (e.isLeftClick()) {
						if (!li.isFinished())
							return;
						p.getInventory().addItem(new LotteryItemBuilder(li).buildLotteryItem());
					} else if (e.isRightClick()) {
						EditLIPage editPage = new EditLIPage(Config.GUI_EDIT_TITLE, 6, li);
						page.openNextPage(editPage, p);
					}
				}

				@Override
				protected void setDisplayItem() {
					this.display = new LotteryItemBuilder(li).buildManagerShowItem();
				}
			};
		}
	}

}
