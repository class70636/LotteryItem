package me.old.li.optionObjects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class Key extends OptionObject implements ConfigurationSerializable, Cloneable {

	public Key() {
		super();
	}

	@Override
	public Key clone() {
		Key key = null;
		try {
			key = (Key) super.clone();
			key.receiveItem = (receiveItem != null ? receiveItem.clone() : null);
			key.receiveMoney = (receiveMoney != null ? receiveMoney.clone() : null);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return key;
	}

	public Key(Map<String, Object> map) {
		this();
		Object o = map.get("receive");
		if (o == null)
			return;
		if (o instanceof ItemStack)
			this.receiveItem = (ItemStack) o;
		if (o instanceof Money)
			this.receiveMoney = (Money) o;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("receive", this.getRecive());
		return map;
	}

}
