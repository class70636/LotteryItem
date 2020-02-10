package me.old.li.optionObjects;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class Money implements ConfigurationSerializable, Cloneable {

	private double m;

	public Money(double n) {
		m = n;
	}

	public double getValue() {
		return m;
	}

	public Money(Map<String, Object> map) {
		this.m = (double) map.get("money");
	}

	@Override
	public Money clone() {
		try {
			return (Money) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("money", m);
		return map;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Money))
			return false;
		Money money = (Money) o;
		return this.m == money.getValue();
	}

	@Override
	public String toString() {
		return "money" + String.valueOf(m);
	}

//	public Money cloneMe() {
//		return new Money(m);
//	}

}
