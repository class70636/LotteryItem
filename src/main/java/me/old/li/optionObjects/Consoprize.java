package me.old.li.optionObjects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class Consoprize extends OptionObject implements ConfigurationSerializable {

	public Consoprize() {
		super();
	}

	@Override
	public Consoprize clone() {
		Consoprize conso = null;
		try {
			conso = (Consoprize) super.clone();
			conso.receiveItem = (receiveItem != null ? receiveItem.clone() : null);
			conso.receiveMoney = (receiveMoney != null ? receiveMoney.clone() : null);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return conso;
	}

	public Consoprize(Map<String, Object> map) {
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
