package me.old.li.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.InputType;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.Utilss.XMaterial;

public class Page {

	protected Player p;

	protected String title;
	protected int size;
	protected Map<Integer, Button> buttons;

	protected boolean goNextPage;
	protected Page previousPage;

	protected InputTask it;
	protected boolean inputing;

	protected Inventory inv;

	protected LotteryItem lotteryItem;

	protected boolean realClose;

	public Page(String title, int size) {
		this.previousPage = null;
		this.goNextPage = false;
		this.realClose = true;
		this.inputing = false;
		this.buttons = new HashMap<>();

		inv = Bukkit.createInventory(null, this.size = size * 9, this.title = title);
	}

	protected void init() {
		this.buttons.forEach((k, v) -> {
			if (v.getDisplayItem() != null)
				this.inv.setItem(k, v.getDisplayItem());
		});
	}

	public void open() {
		// this.init();
		p.openInventory(inv);
	}

	public void open(Player p) {
		this.p = p;
		Main.getInstance().getInvManager().setPlayerSession(p, this);
		this.init();
		Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.open());
	}

	public void openWithoudInit() {
		Main.getInstance().getInvManager().setPlayerSession(p, this);
		Bukkit.getScheduler().runTask(Main.getInstance(), () -> this.open());
	}

	public void openNextPage(Page page, Player p) {
		this.goNextPage = true;
		this.realClose = false;
		page.previousPage = this;
		p.closeInventory();
		page.open(p);
	}

	public void openPrevioudPage(Player p) {
		this.previousPage.goNextPage = false;
		this.previousPage.realClose = true;
		this.previousPage.open(p);
	}

	public void setLotteryItem(LotteryItem li) {
		this.lotteryItem = li;
	}

	public LotteryItem getLotteryItem() {
		return lotteryItem;
	}

	public boolean isInputing() {
		return this.inputing;
	}

	public void refreshPage() {
		this.buttons.values().forEach(btn -> btn.refresh());
	}

	public void onClose(InventoryCloseEvent e) {
		Main.getInstance().getInvManager().setPlayerSession(this.p, null);
	}

	abstract class Button {

		private int slot;
		protected ItemStack display;

		public Button(int slot) {
			this.slot = slot;
			buttons.put(slot, this);
		}

		public Button(int slot, boolean inputButton) {
			this.slot = slot;
		}

		public ItemStack getDisplayItem() {
			return display.clone();
		}

		public int getSlot() {
			return slot;
		}

		public void refresh() {
			this.setDisplayItem();
			inv.setItem(slot, display);
		}

		public void inputButton() {
			buttons.put(this.slot, this);
		}

		protected abstract void execute(Player p, InventoryClickEvent e);

		protected abstract void setDisplayItem();
	}

	enum Selects {
		ITEM() {

			@Override
			Material getMaterial() {
				return XMaterial.CHEST_MINECART.parseMaterial();
			}

			@Override
			List<String> getConsoPrizeLore() {
				return Config.BUTTON_SET_CONSOPRIZE_LORE;
			}

			@Override
			List<String> getKeyLore() {
				return Config.BUTTON_SET_KEY_LORE;
			}

			@Override
			List<String> getGiftLore() {
				return Config.BUTTON_SET_GIFT_TYPE_LORE;
			}

			@Override
			InputType getInputType() {
				return InputType.INTEGER_AMOUNT;
			}

		},
		MONEY {

			@Override
			Material getMaterial() {
				return Material.GOLD_INGOT;
			}

			@Override
			List<String> getConsoPrizeLore() {
				return Config.BUTTON_SET_CONSOPRIZE_LORE2;
			}

			@Override
			List<String> getKeyLore() {
				return Config.BUTTON_SET_KEY_LORE2;
			}

			@Override
			List<String> getGiftLore() {
				return Config.BUTTON_SET_GIFT_TYPE_LORE2;
			}

			@Override
			InputType getInputType() {
				return InputType.DOUBLE_AMOUNT;
			}

		};

		private static Selects[] vals = values();

		public Selects next() {
			return vals[(this.ordinal() + 1) % vals.length];
		}

		abstract Material getMaterial();

		abstract List<String> getConsoPrizeLore();

		abstract List<String> getKeyLore();

		abstract List<String> getGiftLore();

		abstract InputType getInputType();

	}

}
