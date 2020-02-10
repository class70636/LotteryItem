package me.old.li.optionObjects;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.Utilss.Hooker;
import me.old.li.Utilss.ItemBuilder;
import me.old.li.Utilss.Utils;

public class OptionObject implements Cloneable {

	protected ItemStack receiveItem;
	protected Money receiveMoney;

	public OptionObject() {
		resetObjects();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof OptionObject))
			return false;
		OptionObject oo = (OptionObject) o;
		return this.toString().equals(oo.toString());
	}

	public Object getRecive() {
		if (this.receiveItem != null)
			return this.receiveItem.clone();
		if (this.receiveMoney != null)
			return this.receiveMoney;
		return null;
	}

	public void setReciveItem(ItemStack is) {
		this.receiveItem = Utils.isNullableItem(is) ? null : is.clone();
		this.receiveMoney = null;
	}

	public void setReciveMoney(Money money) {
		this.receiveMoney = money;
		this.receiveItem = null;
	}

	public void resetObjects() {
		this.receiveItem = null;
		this.receiveMoney = null;
	}

	public String getReciveName() {
		Object o = this.getRecive();
		if (o == null)
			return null;
		if (o instanceof ItemStack)
			return Config.TYPE_ITEM_NAME;
		else if (o instanceof Money)
			return Config.TYPE_MONEY_NAME;
		return null;
	}

	public String getDisplay() {
		if (this.receiveItem != null) {
			String name = Utils.getItemName(this.receiveItem);
			int amount = this.receiveItem.getAmount();
			return Config.ITEM_DISPLAY.replace("{item_name}", name).replace("{amount}", String.valueOf(amount));
		}
		if (this.receiveMoney != null) {
			String name = Config.MONEY_DISPLAY.replace("{item_name}", Config.TYPE_MONEY_NAME);
			double amount = this.receiveMoney.getValue();
			return name.replace("{amount}", String.valueOf(amount));
		}
		return null;
	}

	public String getTypeDisplay() {
		if (this.receiveItem != null) {
			String name = Utils.getItemName(this.receiveItem);
			return name;
		}
		if (this.receiveMoney != null) {
			return Config.TYPE_MONEY_NAME;
		}
		return null;
	}

	public ItemStack buildItem() {
		if (this.receiveItem != null) {
			return receiveItem.clone();
		}
		if (this.receiveMoney != null) {
			return new ItemBuilder(Material.GOLD_INGOT, this.getDisplay()).getItem();
		}
		return null;
	}

	public String getResultAmount() {
		if (this.receiveItem != null) {
			return String.valueOf(this.receiveItem.getAmount());
		}
		if (this.receiveMoney != null) {
			return String.valueOf(this.receiveMoney.getValue());
		}
		return null;
	}

	public void executeGive(Player p) {
		if (this.receiveItem != null) {
			Map<Integer, ItemStack> map = p.getInventory().addItem(this.receiveItem);
			if (map.size() > 0) {
				Utils.sendPluginMessage(p, Config.INVENTORY_FULL);
				for (ItemStack is : map.values())
					p.getWorld().dropItemNaturally(p.getLocation(), is);
			}
			return;
		}
		if (this.receiveMoney != null) {
			Hooker.getEconomy().depositPlayer(p, this.receiveMoney.getValue());
		}
	}

	public boolean hasContain(Player p) {
		if (this.receiveItem != null
				&& p.getInventory().containsAtLeast(this.receiveItem, this.receiveItem.getAmount()))
			return true;
		if (this.receiveMoney != null && (Hooker.getEconomy().getBalance(p) - this.receiveMoney.getValue() >= 0))
			return true;
		return false;
	}

	public boolean executeRemove(Player p) {
		if (this.receiveItem != null
				&& p.getInventory().containsAtLeast(this.receiveItem, this.receiveItem.getAmount())) {
			p.getInventory().removeItem(this.receiveItem);
			return true;
		}
		if (this.receiveMoney != null && (Hooker.getEconomy().getBalance(p) - this.receiveMoney.getValue() >= 0)) {
			Hooker.getEconomy().withdrawPlayer(p, this.receiveMoney.getValue());
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("item=");
		sb.append(receiveItem != null ? receiveItem.toString() : "null");
		sb.append(",money=");
		sb.append(receiveMoney != null ? receiveMoney.toString() : "null");

		return sb.toString();
	}

}
