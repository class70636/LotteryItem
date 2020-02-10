package me.old.li.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.InputType;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.Utilss.ItemBuilder;
import me.old.li.Utilss.LIApi;
import me.old.li.Utilss.Utils;
import me.old.li.Utilss.XMaterial;
import me.old.li.files.Saves;
import me.old.li.optionObjects.Consoprize;
import me.old.li.optionObjects.Gift;
import me.old.li.optionObjects.Key;
import me.old.li.optionObjects.Money;
import net.md_5.bungee.api.ChatColor;

public class EditLIPage extends Page {

	private EditLIPage page;
	private Selects consoPrize, key;
//	protected boolean realClose;

	public EditLIPage(String title, int size, LotteryItem li) {
		super(title, size);
		this.page = this;
		this.lotteryItem = li;
	}

	private void setSelects() {
		Object o = this.getLotteryItem().getConsoPrize().getRecive();
		if (o == null)
			this.consoPrize = Selects.ITEM;
		else {
			if (o instanceof ItemStack)
				this.consoPrize = Selects.ITEM;
			else if (o instanceof Money)
				this.consoPrize = Selects.MONEY;
		}

		o = this.getLotteryItem().getKey().getRecive();
		if (o == null)
			this.key = Selects.ITEM;
		else {
			if (o instanceof ItemStack)
				this.key = Selects.ITEM;
			else if (o instanceof Money)
				this.key = Selects.MONEY;
		}
	}

	@Override
	public void init() {
		this.buttons.clear();
		this.inv.clear();

		setSelects();

		this.setLineButtons();
		this.setKeepButtons();

		this.setShowIdButton();
		this.setSetNameButton();
		this.setSetLoreButton();
		this.setSetTypeButton();
		this.getSelectConsoPrizeButton();
		this.getSelectKeyButton();
		this.setCdButton();
		this.setPeriodButton();
		this.setSoundButton();

		this.setAddGiftsLore();
		this.setSingleExtractButton();
		this.setRandomExtractButton();
		this.setSelectableButton();

		this.setGiftButtons();

		this.buttons.forEach((k, v) -> v.refresh());
	}

	private Button setShowIdButton() {

		Button b = new Button(0) {
			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
			}

			@Override
			protected void setDisplayItem() {
				List<String> lore = new ArrayList<>();
				for (String str : Config.BUTTON_SHOW_ID_LORE)
					lore.add(str.replace("{id}", getLotteryItem().getItemId()));
				ItemStack icon = new ItemBuilder(XMaterial.WRITABLE_BOOK.parseMaterial(), Config.BUTTON_SHOW_ID_NAME,
						lore).getItem();
				this.display = icon;
			}
		};
		return b;
	}

	private Button setSetTypeButton() {
		Button b = new Button(1) {

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				ItemStack cursor = e.getCursor();
				if (Utils.isNullableItem(cursor))
					return;

				page.getLotteryItem().setTypeItem(cursor);
				this.refresh();
			}

			@Override
			protected void setDisplayItem() {
				ItemStack type = getLotteryItem().getTypeItem();
				this.display = new ItemBuilder(type, Config.BUTTON_GET_TYPE_LORE).getItem();
			}
		};
		return b;
	}

	private Button setSetNameButton() {
		Button b = new Button(2) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.getClick() == ClickType.MIDDLE) {
					page.getLotteryItem().setName(null);
					this.refresh();
				} else {
					new InputHandler(p, page, InputType.TEXT) {

						@Override
						boolean setValue(String msg) {
							page.getLotteryItem().setName(ChatColor.translateAlternateColorCodes('&', msg));
							b.refresh();
							return true;
						}

					}.enable();
				}
			}

			@Override
			protected void setDisplayItem() {
				List<String> lore = new ArrayList<>();
				for (String str : Config.BUTTON_SET_ITEM_NAME_LORE) {
					if (str.contains("{name}")) {
						if (page.getLotteryItem().getCustomName() != null) {
							int n = str.indexOf("{name}");
							lore.add(
									str.replace(str.substring(n, str.length()), page.getLotteryItem().getCustomName()));
						} else
							lore.add(str.replace("{name}:", "§r"));
						continue;
					}
					lore.add(str);
				}
				this.display = new ItemBuilder(Material.NAME_TAG, Config.BUTTON_SET_ITEM_NAME_NAME, lore).getItem();
			}

		};
		return b;
	}

	private void setSetLoreButton() {
		new Button(3) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.getClick() == ClickType.MIDDLE) {
					page.getLotteryItem().setLore(null);
					b.refresh();
				} else {
					new InputHandler(p, page, InputType.TEXT) {

						@Override
						boolean setValue(String msg) {
							String strr = msg.replace("\\n", "\n");
							String[] temp = strr.split("\n");
							List<String> newLore = new ArrayList<>();
							for (String str : temp)
								newLore.add(ChatColor.translateAlternateColorCodes('&', str));
							page.getLotteryItem().setLore(newLore);

							b.refresh();
							return true;
						}

					}.enable();
				}
			}

			@Override
			protected void setDisplayItem() {
				List<String> lore = new ArrayList<>();
				for (String str : Config.BUTTON_SET_ITEM_LORE_LORE) {
					if (str.contains("{lore}")) {
						if (page.getLotteryItem().getCustomLore() != null) {
							int n = str.indexOf("{lore}");
							lore.add(str.replace(str.substring(n, str.length()), ""));
							lore.addAll(page.getLotteryItem().getCustomLore());
						} else
							lore.add(str.replace("{lore}:", "§r"));
						continue;
					}
					lore.add(str);
				}
				this.display = new ItemBuilder(Material.PAPER, Config.BUTTON_SET_ITEM_LORE_NAME, lore).getItem();
			}

		};
	}

	private Button getSetConsoPrize_item_Button() {
		Button b = new Button(4) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				Consoprize cp = page.getLotteryItem().getConsoPrize();

				if (e.isLeftClick()) {
					ItemStack cursor = e.getCursor();
					if (Utils.isNullableItem(cursor))
						return;

					cp.setReciveItem(cursor);
				} else if (e.isRightClick()) {
					cp.resetObjects();
					consoPrize = consoPrize.next();
					int index = b.getSlot();
					page.buttons.put(index, getSelectConsoPrizeButton());
					page.buttons.get(index).refresh();
				} else if (e.getClick() == ClickType.MIDDLE) {
					cp.resetObjects();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetConsoPrizeItem();
			}

		};
		return b;
	}

	private Button getSetConsoPrize_money_Button() {
		Button b = new Button(4) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				Consoprize cp = page.getLotteryItem().getConsoPrize();

				if (e.isLeftClick()) {
					new InputHandler(p, page, InputType.DOUBLE_INCLUDE_ZERO) {

						@Override
						boolean setValue(String msg) {
							double amount = Double.valueOf(msg);
							cp.setReciveMoney(amount == 0 ? null : new Money(amount));

							b.refresh();
							return true;
						}

					}.enable();
				} else if (e.isRightClick()) {
					cp.resetObjects();
					consoPrize = consoPrize.next();
					int index = b.getSlot();
					page.buttons.put(index, getSelectConsoPrizeButton());
					page.buttons.get(index).refresh();
				} else if (e.getClick() == ClickType.MIDDLE) {
					cp.resetObjects();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetConsoPrizeItem();
			}

		};
		return b;
	}

	private ItemStack getSetConsoPrizeItem() {
		Object recive = page.getLotteryItem().getConsoPrize().getRecive();
		ItemBuilder ib = new ItemBuilder();
		if (recive == null) {
			ib.setMaterial(this.consoPrize.getMaterial());
			ib.setName(Config.BUTTON_SET_CONSOPRIZE_NAME);
			ib.setLore(this.consoPrize.getConsoPrizeLore());
		} else {
			if (recive instanceof ItemStack) {
				ib = new ItemBuilder((ItemStack) recive, Config.BUTTON_SET_CONSOPRIZE_LORE);
			} else if (recive instanceof Money) {
				ib.setMaterial(this.consoPrize.getMaterial());
				ib.setName(Config.BUTTON_SET_CONSOPRIZE_NAME2.replace("{money}", ((Money) recive).getValue() + ""));
				ib.setLore(Config.BUTTON_SET_CONSOPRIZE_LORE2);
			}
		}
		return ib.getItem();
	}

	private Button getSetKey_item_Button() {
		Button b = new Button(5) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				Key key = page.getLotteryItem().getKey();

				if (e.isLeftClick()) {
					ItemStack cursor = e.getCursor();
					if (Utils.isNullableItem(cursor))
						return;

					key.setReciveItem(cursor);
				} else if (e.isRightClick()) {
					key.resetObjects();
					page.key = page.key.next();
					int index = b.getSlot();
					page.buttons.put(index, getSelectKeyButton());
					page.buttons.get(index).refresh();
				} else if (e.getClick() == ClickType.MIDDLE) {
					key.resetObjects();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetKeyItem();
			}

		};
		return b;
	}

	private Button getSetKey_money_Button() {
		Button b = new Button(5) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				Key key = page.getLotteryItem().getKey();

				if (e.isLeftClick()) {
					new InputHandler(p, page, InputType.DOUBLE_INCLUDE_ZERO) {

						@Override
						boolean setValue(String msg) {
							double amount = Double.valueOf(msg);
							key.setReciveMoney(amount == 0 ? null : new Money(amount));

							b.refresh();
							return true;
						}

					}.enable();
				} else if (e.isRightClick()) {
					key.resetObjects();
					page.key = page.key.next();
					int index = b.getSlot();
					page.buttons.put(index, getSelectKeyButton());
					page.buttons.get(index).refresh();
				} else if (e.getClick() == ClickType.MIDDLE) {
					key.resetObjects();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetKeyItem();
			}

		};
		return b;
	}

	private ItemStack getSetKeyItem() {
		Object recive = page.getLotteryItem().getKey().getRecive();
		ItemBuilder ib = new ItemBuilder();
		if (recive == null) {
			ib.setMaterial(this.key.getMaterial());
			ib.setName(Config.BUTTON_SET_KEY_NAME);
			ib.setLore(this.key.getKeyLore());
		} else {
			if (recive instanceof ItemStack) {
				ib = new ItemBuilder((ItemStack) recive, Config.BUTTON_SET_KEY_LORE);
			} else if (recive instanceof Money) {
				ib.setMaterial(Material.GOLD_INGOT);
				ib.setName(Config.BUTTON_SET_KEY_NAME2.replace("{money}", ((Money) recive).getValue() + ""));
				ib.setLore(Config.BUTTON_SET_KEY_LORE2);
			}
		}
		return ib.getItem();
	}

	private void setCdButton() {
		new Button(6) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				new InputHandler(p, page, InputType.INTEGER_INCLUDE_ZERO) {
					@Override
					boolean setValue(String msg) {
						page.getLotteryItem().setCoolDown(Integer.valueOf(msg));

						b.refresh();
						return true;
					}
				}.enable();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetCdItem();
			}

		};
	}

	private ItemStack getSetCdItem() {
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_SET_CD_LORE)
			lore.add(str.replace("{cd}", page.getLotteryItem().getCoolDown() + ""));

		return new ItemBuilder(XMaterial.CLOCK.parseMaterial(), Config.BUTTON_SET_CD_NAME, lore).getItem();
	}

	private void setPeriodButton() {
		new Button(7) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.getClick() == ClickType.MIDDLE) {
					page.getLotteryItem().setPeriodOfUse(null);
				} else {
					new InputHandler(p, page, InputType.DATE) {
						@Override
						boolean setValue(String msg) {
							page.getLotteryItem().setPeriodOfUse(msg);
							b.refresh();
							return true;
						}
					}.enable();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetPeriodItem();
			}
		};
	}

	private ItemStack getSetPeriodItem() {
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_SET_PERIOD_OF_USE_LORE) {
			if (str.contains("{date}")) {
				if (page.getLotteryItem().getPeriodOfUse() != null) {
					int n = str.indexOf("{date}");
					lore.add(str.replace(str.substring(n, str.length()), page.getLotteryItem().getPeriodOfUse()));
				} else
					lore.add(str.replace("{date}:", "§r"));
				continue;
			}
			lore.add(str);
		}
		return new ItemBuilder(Material.COMPASS, Config.BUTTON_SET_PERIOD_OF_USE_NAME, lore).getItem();
	}

	private void setSoundButton() {
		new Button(8) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.getClick() == ClickType.MIDDLE) {
					page.getLotteryItem().setSoundSet(null);
				} else {
					new InputHandler(p, page, InputType.SOUND) {
						@Override
						boolean setValue(String msg) {
							String str = msg.toUpperCase();
							String sound = str.split(",")[0];
							try {
								Sound.valueOf(sound);
							} catch (IllegalArgumentException e) {
								Utils.sendPluginMessage(p, Config.MESSAGE_NO_SOUND_NAME);
								return false;
							}
							page.getLotteryItem().setSoundSet(msg);
							b.refresh();
							return true;
						}
					}.enable();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetSoundItem();
			}
		};
	}

	private ItemStack getSetSoundItem() {
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_SET_SOUND_LORE) {
			if (str.contains("{sound}")) {
				if (page.getLotteryItem().getSoundSet() != null) {
					int n = str.indexOf("{sound}");
					lore.add(str.replace(str.substring(n, str.length()), page.getLotteryItem().getSoundSet()));
				} else
					lore.add(str.replace("{sound}:", "§r"));
				continue;
			}
			lore.add(str);
		}
		return new ItemBuilder(Material.JUKEBOX, Config.BUTTON_SET_SOUND_NAME, lore).getItem();
	}

	private void setAddGiftsLore() {
		new Button(9) {
			Button bb = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				boolean b = page.getLotteryItem().isAddGiftsLore();
				page.getLotteryItem().setAddGiftsLore(!b);

				bb.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetAddGiftsItem();
			}
		};
	}

	private ItemStack getSetAddGiftsItem() {
		ItemBuilder ib = new ItemBuilder();
		ib.setMaterial(page.getLotteryItem().isAddGiftsLore() ? XMaterial.LIME_WOOL.parseMaterial()
				: XMaterial.RED_WOOL.parseMaterial());
//		ib.setMaterial(page.getLotteryItem().isAddGiftsLore() ? Material.LIME_WOOL : Material.RED_WOOL);
		ib.setName(Config.BUTTON_SET_ADD_GIFTS_LORE_NAME);
		ib.setLore(Config.BUTTON_SET_ADD_GIFTS_LORE_LORE);
		return ib.getItem();
	}

	private void setSingleExtractButton() {
		int slot = 10;
		new Button(slot) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (page.getLotteryItem().isSelectable())
					return;

				boolean isSingleExtract = page.getLotteryItem().isSingleExtract();
				page.getLotteryItem().setSingleExtract(!isSingleExtract);

				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSingleExtractItem();
			}
		};
	}

	private ItemStack getSingleExtractItem() {
		ItemBuilder ib = new ItemBuilder();
		ib.setMaterial(page.getLotteryItem().isSingleExtract() ? XMaterial.LIME_WOOL.parseMaterial()
				: XMaterial.RED_WOOL.parseMaterial());
		ib.setName(Config.BUTTON_SET_SINGLE_EXTRACT_NAME);
		ib.setLore(Config.BUTTON_SET_SINGLE_EXTRACT_LORE);
		return ib.getItem();
	}

	private void setRandomExtractButton() {
		int slot = 11;
		new Button(slot) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (page.getLotteryItem().isSelectable())
					return;
				boolean isRandomExtract = page.getLotteryItem().isRandomExtract();
				page.getLotteryItem().setRandomExtract(!isRandomExtract);

				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getRandomExtractItem();
			}
		};
	}

	private ItemStack getRandomExtractItem() {
		ItemBuilder ib = new ItemBuilder();
		ib.setMaterial(page.getLotteryItem().isRandomExtract() ? XMaterial.LIME_WOOL.parseMaterial()
				: XMaterial.RED_WOOL.parseMaterial());
		ib.setName(Config.BUTTON_SET_RANDOM_EXTRACT_NAME);
		ib.setLore(Config.BUTTON_SET_RANDOM_EXTRACT_LORE);
		return ib.getItem();
	}

	private void setSelectableButton() {
		new Button(12) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				boolean selectable = page.getLotteryItem().isSelectable();
				page.getLotteryItem().setSelectable(!selectable);

				if (!selectable) {
					page.getLotteryItem().setRandomExtract(false);
					page.getLotteryItem().setSingleExtract(false);
					page.refreshPage();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSelectableItem();
			}
		};
	}

	private ItemStack getSelectableItem() {
		ItemBuilder ib = new ItemBuilder();
		ib.setMaterial(page.getLotteryItem().isSelectable() ? XMaterial.LIME_WOOL.parseMaterial()
				: XMaterial.RED_WOOL.parseMaterial());
		ib.setName(Config.BUTTON_SET_SELECTABLE_NAME);
		ib.setLore(Config.BUTTON_SET_SELECTABLE_LORE);
		return ib.getItem();
	}

	private void setKeepButtons() {
		int lines = 2;
		for (int start = 0; start < lines * 9; start++) {
			new Button(start) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(XMaterial.WHITE_STAINED_GLASS_PANE.parseMaterial(),
							Config.BUTTON_NOTUSE_NAME).getItem();
				}
			};
		}
	}

	private void setLineButtons() {
		int lines = 3;
		for (int start = 0; start < lines * 9; start++) {
			new Button(start) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial(), " ").getItem();
				}
			};
		}
	}

	public void setGiftButtons() {
		this.setEmptyButtons();
		int start, firstIndex = 27;
		for (start = 27; start < firstIndex + this.getLotteryItem().getGifts().size(); start++) {
			new Button(start) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
					Gift g = getLotteryItem().getGifts().get(this.getSlot() - 27);
					if (e.isShiftClick() && e.isRightClick()) {
						DeleteGiftConfirm dgc = new DeleteGiftConfirm(Config.GUI_DELETE_GIFT_TITLE, 3, g);
						page.openNextPage(dgc, p);
						return;
					}
					EditGiftPage egp = new EditGiftPage(Config.GUI_EDIT_GIFT_TITLE, 1, g);
					page.openNextPage(egp, p);
				}

				@Override
				protected void setDisplayItem() {
					this.display = getFinisedItem(this.getSlot() - 27);
				}
			};
		}
		if (start < this.inv.getSize()) {

			new Button(start) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
					page.getLotteryItem().getGifts().add(new Gift());
					Gift g = getLotteryItem().getGifts().get(this.getSlot() - 27);
					EditGiftPage egp = new EditGiftPage(Config.GUI_EDIT_GIFT_TITLE, 1, g);
					page.openNextPage(egp, p);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(Material.BOOK, Config.BUTTON_CREATE_NEW_GIFT_NAME).getItem();
				}
			};
		}
	}

	private ItemStack getFinisedItem(int index) {
		ItemBuilder ib = new ItemBuilder();
		Gift g = this.getLotteryItem().getGifts().get(index);
		if (g.getRecive() instanceof ItemStack) {
			ib.setItem((ItemStack) g.getRecive());
		} else if (g.getRecive() instanceof Money) {
			ib.setMaterial(Material.GOLD_INGOT);
			ib.setName(" ");
		}
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_EDIT_GIFT_LORE) {
			lore.add(str.replace("{type}", g.getReciveName()).replace("{amount}", g.getAmount())
					.replace("{chance}", g.getChance() + "")
					.replace("{broadcast}", Utils.getBooleanSymbol(g.getBroadcast())));
		}
		ib.addLore(lore);

		return ib.getItem();
	}

	private void setEmptyButtons() {
		for (int i = 27; i < this.inv.getSize(); i++) {
			new Button(i) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = null;
				}
			};
		}
	}

	private Button getSelectConsoPrizeButton() {
		switch (consoPrize) {
		case ITEM:
			return this.getSetConsoPrize_item_Button();
		case MONEY:
			return this.getSetConsoPrize_money_Button();
		}
		return this.getSetConsoPrize_item_Button();
	}

	private Button getSelectKeyButton() {
		switch (key) {
		case ITEM:
			return this.getSetKey_item_Button();
		case MONEY:
			return this.getSetKey_money_Button();
		}
		return this.getSetKey_item_Button();
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();

		if (realClose) {
			// 若抽獎物尚未完成不做任何事
			if (!this.getLotteryItem().isFinished()) {
				Utils.sendPluginMessage(p,
						Config.MESSAGE_NOT_FINISHED_YET.replace("\\n", "\n")
								.replace("{reasons}", Utils.getReasons(getLotteryItem()))
								.replace("{id}", this.getLotteryItem().getItemId()));
			}
			// 若抽獎物完成
			else {
				Saves.reloadConfig();
				String id = this.getLotteryItem().getItemId();
				// 檢查抽獎物是否在Saves
				if (LIApi.isConfigExists(id)) {
					LotteryItem saved = (LotteryItem) Saves.getConfig().get(id);
					// 檢查是否改變
					if (!saved.equals(this.getLotteryItem())) {
						String msg = Config.MESSAGE_EDIT_FINISHED.replace("{id}", id);
						Utils.sendPluginMessage(p, msg);
						Saves.getConfig().set(id, this.getLotteryItem().clone());
						Saves.saveConfig();
						// 替換物品
						Utils.replacePlayerLotteryItem(this.getLotteryItem());
					}
				} else {
					String msg = Config.MESSAGE_EDIT_FINISHED.replace("{id}", id);
					Utils.sendPluginMessage(p, msg);
					// 存檔
					Saves.getConfig().set(id, this.getLotteryItem().clone());
					Saves.saveConfig();

					Main.getInstance().getServerLotteryItems().getLotteryItems().remove(this.getLotteryItem());
				}
			}
		}

		Main.getInstance().getInvManager().setPlayerSession(this.p, null);

		if (!this.goNextPage && this.previousPage != null) {
			this.openPrevioudPage(p);
		}
	}

}
