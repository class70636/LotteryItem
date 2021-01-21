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
import me.old.li.Utilss.ItemBuilder;
import me.old.li.Utilss.Utils;
import me.old.li.optionObjects.Gift;
import me.old.li.optionObjects.Money;

public class EditGiftPage extends Page {

	private Page page;
	private Gift gift;
	private Selects type;

	public EditGiftPage(String title, int size, Gift gift) {
		super(title, size);
		this.page = this;
		this.gift = gift;
	}

	private void setSelects() {
		Object o = this.gift.getRecive();
		if (o == null)
			this.type = Selects.ITEM;
		else {
			if (o instanceof ItemStack)
				this.type = Selects.ITEM;
			else if (o instanceof Money)
				this.type = Selects.MONEY;
		}
	}

	@Override
	public void init() {
		setSelects();

		this.buttons.clear();
		setKeepButtons();

		setSelectsTypeButton();
		setSetAmountButton();
		setSetChanceButton();
		setSetBroadcastButton();
		setSoundButton();

		this.buttons.forEach((k, v) -> v.refresh());
	}

	private Button setSelectsTypeButton() {
		switch (type) {
		case ITEM:
			return getSelectType_item_Button();
		case MONEY:
			return getSelectType_money_Button();
		}
		return getSelectType_item_Button();
	}

	private Button getSelectType_item_Button() {
		Button b = new Button(0) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.isLeftClick()) {
					ItemStack cursor = e.getCursor();
					if (Utils.isNullableItem(cursor))
						return;

					gift.setReciveItem(cursor);
					gift.setAmount(String.valueOf(cursor.getAmount()));
					Button bb = setSetAmountButton();
					bb.refresh();
				} else if (e.isRightClick()) {
					gift.setReciveMoney(new Money(0));
					type = type.next();
					int index = b.getSlot();
					buttons.put(index, setSelectsTypeButton());
					buttons.get(index).refresh();

					gift.setAmount(null);
					Button bb = setSetAmountButton();
					bb.refresh();
				} else if (e.getClick() == ClickType.MIDDLE) {
					gift.resetObjects();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSelctTypeItem();
			}
		};
		return b;
	}

	private Button getSelectType_money_Button() {
		Button b = new Button(0) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.isRightClick()) {
					gift.resetObjects();
					type = type.next();
					int index = b.getSlot();
					buttons.put(index, setSelectsTypeButton());
					buttons.get(index).refresh();

					gift.setAmount(null);
					Button bb = setSetAmountButton();
					bb.refresh();
				} else if (e.getClick() == ClickType.MIDDLE) {
					gift.resetObjects();
				}
				b.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSelctTypeItem();
			}
		};
		this.buttons.put(b.getSlot(), b);
		return b;
	}

	private ItemStack getSelctTypeItem() {
		Object recive = this.gift.getRecive();
		ItemBuilder ib = new ItemBuilder();
		if (recive == null) {
			ib.setMaterial(this.type.getMaterial());
			ib.setName(Config.BUTTON_SET_GIFT_TYPE_NAME);
			ib.setLore(this.type.getGiftLore());
		} else {
			if (recive instanceof ItemStack) {
				ib = new ItemBuilder((ItemStack) recive, Config.BUTTON_SET_GIFT_TYPE_LORE);
			} else if (recive instanceof Money) {
				ib.setMaterial(Material.GOLD_INGOT);
				ib.setName(Config.BUTTON_SET_GIFT_TYPE_NAME2);
				ib.setLore(Config.BUTTON_SET_GIFT_TYPE_LORE2);
			}
		}
		return ib.getItem();
	}

	private Button setSetAmountButton() {
		Button b = new Button(1) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				new InputHandler(p, page, type.getInputType()) {

					@Override
					boolean setValue(String msg) {
						String str = msg;
						if (msg.contains("-")) {
							String[] temp = msg.split("-");
							double d1 = Double.valueOf(temp[0]), d2 = Double.valueOf(temp[1]);
							if (d1 > d2) {
								Utils.sendPluginMessage(p, Config.MESSAGE_INPUT_WRONG);
								return false;
							} else if (d1 == d2) {
								str = String.valueOf(d1);
							}
						}
						if (msg.equalsIgnoreCase("0")) {
							Utils.sendPluginMessage(p, Config.MESSAGE_INPUT_WRONG);
							return false;
						}
						gift.setAmount(str);
						b.refresh();
						return true;
					}

				}.enable();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetAmountItem();
			}
		};
		this.buttons.put(b.getSlot(), b);
		return b;
	}

	private ItemStack getSetAmountItem() {
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_SET_GIFT_AMOUNT_LORE) {
			if (str.contains("{amount}")) {
				if (gift.getAmount() != null) {
					int n = str.indexOf("{amount}");
					lore.add(str.replace(str.substring(n, str.length()), gift.getAmount()));
				} else
					lore.add(str.replace("{amount}:", "§r"));
				continue;
			}
			lore.add(str);
		}
		return new ItemBuilder(Material.REDSTONE_TORCH, Config.BUTTON_SET_GIFT_AMOUNT_NAME, lore).getItem();
	}

	private void setSetChanceButton() {
		new Button(2) {
			Button b = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.isRightClick()) {
					gift.setChance(100);
					b.refresh();
					return;
				}
				new InputHandler(p, page, InputType.DOUBLE_NOT_INCLUDE_ZERO) {
					@Override
					boolean setValue(String msg) {
						double d = Double.valueOf(msg);
						if (d > 100) {
							Utils.sendPluginMessage(p, Config.MESSAGE_INPUT_WRONG);
							return false;
						}
						gift.setChance(d);

						b.refresh();
						return true;
					}
				}.enable();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetChanceItem();
			}
		};
	}

	private ItemStack getSetChanceItem() {
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_SET_GIFT_CHANCE_LORE) {
			if (str.contains("{chance}")) {
				if (gift.getChance() != 0) {
					int n = str.indexOf("{chance}");
					lore.add(str.replace(str.substring(n, str.length()), gift.getChance() + "％"));
				} else
					lore.add(str.replace("{chance}:", "§r"));
				continue;
			}
			lore.add(str);
		}
		return new ItemBuilder(Material.FEATHER, Config.BUTTON_SET_GIFT_CHANCE_NAME, lore).getItem();
	}

	private Button setSetBroadcastButton() {
		Button b = new Button(3) {
			Button bb = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				boolean b = gift.getBroadcast();
				gift.setBroadcast(!b);

				bb.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSetBroadcastItem();
			}
		};
		this.buttons.put(b.getSlot(), b);
		return b;
	}

	private ItemStack getSetBroadcastItem() {
		ItemBuilder ib = new ItemBuilder();
		ib.setMaterial(gift.getBroadcast() ? Material.LIME_WOOL : Material.RED_WOOL);
		ib.setName(Config.BUTTON_SET_GIFT_BROADCAST_NAME);
		ib.setLore(Config.BUTTON_SET_GIFT_BROADCAST_LORE);
		return ib.getItem();
	}

	private Button setSoundButton() {
		Button b = new Button(4) {
			Button bb = this;

			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				if (e.getClick() == ClickType.MIDDLE) {
					gift.setSound(null);
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
							gift.setSound(msg);
							bb.refresh();
							return true;
						}
					}.enable();
				}
				bb.refresh();
			}

			@Override
			protected void setDisplayItem() {
				this.display = getSoundButtonItem();
			}

		};
		return b;
	}

	private ItemStack getSoundButtonItem() {
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_SET_GIFT_SOUND_LORE) {
			if (str.contains("{sound}")) {
				if (gift.getSound() != null) {
					int n = str.indexOf("{sound}");
					lore.add(str.replace(str.substring(n, str.length()), gift.getSound()));
				} else
					lore.add(str.replace("{sound}:", "§r"));
				continue;
			}
			lore.add(str);
		}
		return new ItemBuilder(Material.JUKEBOX, Config.BUTTON_SET_GIFT_SOUND_NAME, lore).getItem();
	}

	private void setKeepButtons() {
		for (int i = 0; i < this.inv.getSize(); i++) {
			new Button(i) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE, Config.BUTTON_NOTUSE_NAME)
							.getItem();
				}
			};
		}
	}

	@Override
	public void onClose(InventoryCloseEvent e) {
		if (!gift.isFinished()) {
			this.previousPage.getLotteryItem().getGifts().remove(gift);
		}
		this.previousPage.getLotteryItem().sortGifts();
		this.openPrevioudPage(p);
	}

}
