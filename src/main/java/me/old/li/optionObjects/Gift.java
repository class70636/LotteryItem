package me.old.li.optionObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Utilss.Hooker;
import me.old.li.Utilss.LIApi;
import me.old.li.Utilss.Utils;
import me.old.li.files.Saves;

public class Gift extends OptionObject implements ConfigurationSerializable, Comparable<Gift> {

	private String amount;
	private double chance;
	private boolean broadcast;

	public Gift() {
		super();
		this.amount = null;
		this.chance = 0;
		this.broadcast = false;
	}

	@Override
	public Gift clone() {
		Gift conso = null;
		try {
			conso = (Gift) super.clone();
			conso.receiveItem = (receiveItem != null ? receiveItem.clone() : null);
			conso.receiveMoney = (receiveMoney != null ? receiveMoney.clone() : null);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return conso;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("item=" + (receiveItem != null ? receiveItem.toString() : "null"));
		sb.append(",money=" + (receiveMoney != null ? receiveMoney.toString() : "null"));
		sb.append(",amount=" + amount);
		sb.append(",chance=" + chance);
		sb.append(",broadcast=" + broadcast);
		return sb.toString();
	}

	public Gift(Map<String, Object> map) {
		this();
		Object o = map.get("receive");
		if (o == null)
			return;
		if (o instanceof ItemStack)
			this.receiveItem = (ItemStack) o;
		if (o instanceof Money)
			this.receiveMoney = (Money) o;
		this.amount = (String) map.get("amount");
		this.chance = (double) map.get("chance");
		this.broadcast = (boolean) map.get("broadcast");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("receive", this.getRecive());
		map.put("amount", this.amount);
		map.put("chance", this.chance);
		map.put("broadcast", this.broadcast);
		return map;
	}

	@Override
	public void setReciveItem(ItemStack is) {
		this.receiveItem = Utils.isNullableItem(is) ? null : is.clone();
		if (!Utils.isNullableItem(this.receiveItem))
			this.receiveItem.setAmount(1);
		this.receiveMoney = null;
	}

	public void setAmount(String str) {
		amount = str;
	}

	public String getAmount() {
		return amount;
	}

	public double getChance() {
		return this.chance;
	}

	public void setChance(double d) {
		this.chance = d;
	}

	public boolean getBroadcast() {
		return this.broadcast;
	}

	public void setBroadcast(boolean b) {
		this.broadcast = b;
	}

	public String getItemDisplay() {
		String item_display = getDisplayName();

		String chance_display = Config.CHANCE_DISPLAY.replace("{chance}", this.chance + "");
		item_display = item_display.replace("{chance_display}", chance_display);

		String dis = (this.chance == 100 ? Config.GIFT_FIXED_DISPLAY : Config.GIFT_RANDOM_DISPLAY);
		return dis.replace("{detail}", item_display);
	}

	public String getSelectableDisplay() {
		String item_display = getDisplayName();
		return Config.GIFT_SELECTABLE_DISPLAY.replace("{detail}", item_display);
	}

//	public String getShowName() {
//		String item_display = "";
//		if (this.getRecive() instanceof ItemStack) {
//			item_display = Utils.getItemName((ItemStack) this.getRecive());
//		} else if (this.getRecive() instanceof Money) {
//			item_display = Config.TYPE_MONEY_NAME;
//		}
//		return item_display;
//	}

	public String getItemName() {
		String item_display = "";
		if (this.getRecive() instanceof ItemStack) {
			String itemName = Utils.getItemName((ItemStack) this.getRecive());
			item_display = Config.ITEM_DISPLAY.replace("{item_name}", itemName);
		} else if (this.getRecive() instanceof Money) {
			item_display = Config.MONEY_DISPLAY.replace("{item_name}", Config.TYPE_MONEY_NAME);
		}
		return item_display;
	}

	public String getDisplayName() {
		if (this.getRecive() instanceof ItemStack) {
			String itemName = Utils.getItemName((ItemStack) this.getRecive());
			return Config.ITEM_DISPLAY.replace("{item_name}", itemName).replace("{amount}", this.getAmount());
		} else if (this.getRecive() instanceof Money) {
			return Config.MONEY_DISPLAY.replace("{amount}", this.getAmount()).replace("{item_name}",
					Config.TYPE_MONEY_NAME);
		}
		return null;
	}

	@Override
	public String getDisplay() {
		if (this.receiveItem != null) {
			String name = Utils.getItemName(this.receiveItem);
			return Config.ITEM_DISPLAY.replace("{item_name}", name).replace("{amount}", amount);
		}
		if (this.receiveMoney != null) {
			String name = Config.MONEY_DISPLAY.replace("{item_name}", Config.TYPE_MONEY_NAME);
			return name.replace("{amount}", amount);
		}
		return null;
	}

	public Gift draw() {
		Gift g = this.clone();
		if (g.amount.split("-").length == 2) {
			double d1 = Double.valueOf(g.amount.split("-")[0]);
			double d2 = Double.valueOf(g.amount.split("-")[1]);

			g.amount = String.valueOf((int) (Math.random() * (d2 - d1 + 1) + d1));

		}
		return g;
	}

	public void draw(List<OptionObject> source) {
		if (Math.random() * 10000000 + 1 <= chance * 100000) {
			Gift g = this.clone();
			if (g.amount.split("-").length == 2) {
				double d1 = Double.valueOf(g.amount.split("-")[0]);
				double d2 = Double.valueOf(g.amount.split("-")[1]);

				g.amount = String.valueOf((int) (Math.random() * (d2 - d1 + 1) + d1));

			}
			source.add(g);
		}
	}

	@Override
	public String getResultAmount() {
		return this.amount;
	}

	@Override
	public void executeGive(Player p) {
		if (this.receiveItem != null) {
			if (LIApi.isLotteryItem(this.receiveItem)) {
				String key = Utils.getLiKey(this.receiveItem);
				this.receiveItem = ((LotteryItem) Saves.getConfig().get(key)).buildItem();
			}
			this.receiveItem.setAmount(Integer.valueOf(amount));
			Map<Integer, ItemStack> map = p.getInventory().addItem(this.receiveItem);
			if (map.size() > 0) {
				Utils.sendPluginMessage(p, Config.INVENTORY_FULL);
				for (ItemStack is : map.values()) {
					p.getWorld().dropItemNaturally(p.getLocation(), is);
				}
			}
			return;
		}
		if (this.receiveMoney != null) {
			Hooker.getEconomy().depositPlayer(p, Double.valueOf(amount));
		}
	}

	public boolean isFinished() {
		if (this.getRecive() == null || this.amount == null || this.chance == 0)
			return false;
		return true;
	}

	public boolean isSingleAmount() {
		if (this.amount.contains("-"))
			return false;
		if (Integer.valueOf(this.amount) > 1)
			return false;
		return true;
	}

	@Override
	public int compareTo(Gift g2) {
		int x = (int) (((g2.getChance() - this.getChance()) * 100000) / 100);
//		int y = (int) (((this.getChance() - g2.getChance()) * 100000) / 100);
		int z = this.getItemName().compareTo(g2.getItemName());
		double d1 = Double.valueOf(this.getAmount().split("-")[0]);
		double d2 = Double.valueOf(g2.getAmount().split("-")[0]);
		int w = (int) (((d1 - d2) * 100000) / 100);

		if (w == 0) {
			int l1 = this.getAmount().split("-").length;
			int l2 = g2.getAmount().split("-").length;
			if (l1 != l2)
				w = l1 - l2;
			else {
				d1 = l1 > 1 ? Double.valueOf(this.getAmount().split("-")[1]) : d1;
				d2 = l2 > 1 ? Double.valueOf(g2.getAmount().split("-")[1]) : d2;
				w = (int) (((d1 - d2) * 100000) / 100);
			}
		}

		if (x == 0) {
			if (z == 0)
				return w;
			return z;
		}
		return x;
	}

}
