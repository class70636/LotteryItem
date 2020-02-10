package me.old.li;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import me.old.li.Utilss.LotteryItemBuilder;
import me.old.li.optionObjects.Consoprize;
import me.old.li.optionObjects.Gift;
import me.old.li.optionObjects.Key;

public class LotteryItem implements ConfigurationSerializable, Cloneable, Comparable<LotteryItem> {

	private String itemId;

	private ItemStack typeItem;

	private String name;
	private List<String> lore;

	private Consoprize consoPrize;
	private Key key;

	private int coolDown;
	private String periodOfUse;
	private String soundSet;

	private boolean addGiftsLore;
	private boolean singleExtract;
	private boolean randomExtract;
	private boolean selectable;

	private List<Gift> gifts;

	public LotteryItem(String id) {
		this.itemId = id;
		this.typeItem = new ItemStack(Material.CHEST);
		this.name = null;
		this.lore = null;
		this.consoPrize = new Consoprize();
		this.key = new Key();
		this.coolDown = 0;
		this.periodOfUse = null;
		this.soundSet = null;
		this.addGiftsLore = true;
		this.singleExtract = this.randomExtract = this.selectable = false;
		this.gifts = new ArrayList<>();
	}

	@Override
	public LotteryItem clone() {
		LotteryItem li = null;
		try {
			li = (LotteryItem) super.clone();
			li.lore = (this.lore == null ? null : new ArrayList<>(this.lore));
			li.consoPrize = this.consoPrize.clone();
			li.key = this.key.clone();
			li.gifts = new ArrayList<>(this.gifts);
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return li;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("a=" + itemId);
		sb.append(",b=" + typeItem.toString());
		sb.append(",c=" + getLotteryItemName());
		sb.append(",d=" + (this.getCustomLore() != null ? lore.toString() : "null"));
		sb.append(",e=" + consoPrize.toString());
		sb.append(",f=" + key.toString());
		sb.append(",g=" + coolDown);
		sb.append(",h=" + (this.hasPeriodOfUse() ? periodOfUse : "null"));
		sb.append(",i=" + addGiftsLore);
		sb.append(",j=" + singleExtract);
		sb.append(",k=" + randomExtract);
		sb.append(",l=" + gifts.toString());
		sb.append(",m=" + (this.hasSoundSet() ? soundSet : "null"));
		sb.append(",n=" + selectable);
//		System.out.println(gifts.toString());
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		return this.toString().equals(((LotteryItem) o).toString());
	}

	@SuppressWarnings("unchecked")
	public LotteryItem(Map<String, Object> map) {
		this((String) map.get("itemId"));
		this.typeItem = (ItemStack) map.get("typeItem");
		this.name = (String) map.get("name");
		this.lore = (List<String>) map.get("lore");
		this.consoPrize = (Consoprize) map.get("consoPrize");
		this.key = (Key) map.get("key");
		this.coolDown = (int) map.get("coolDown");
		this.periodOfUse = (String) map.get("periodOfUse");
		this.soundSet = (String) map.get("soundSet");
		this.addGiftsLore = (boolean) map.get("addGiftsLore");
		this.singleExtract = (boolean) map.get("singleExtract");
		this.randomExtract = (boolean) map.get("randomExtract");
		this.selectable = map.get("selectable") == null ? false : (boolean) map.get("selectable");
		this.gifts = (List<Gift>) map.get("gifts");
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		map.put("itemId", this.itemId);
		map.put("typeItem", this.typeItem);
		map.put("name", this.name);
		map.put("lore", this.lore);
		map.put("consoPrize", this.consoPrize);
		map.put("key", this.key);
		map.put("coolDown", this.coolDown);
		map.put("periodOfUse", this.periodOfUse);
		map.put("soundSet", this.soundSet);
		map.put("addGiftsLore", this.addGiftsLore);
		map.put("singleExtract", this.singleExtract);
		map.put("randomExtract", this.randomExtract);
		map.put("selectable", this.selectable);
		map.put("gifts", this.gifts);
		return map;
	}

	public ItemStack getTypeItem() {
		return typeItem.clone();
	}

	public void setTypeItem(ItemStack is) {
		typeItem = is.clone();
		typeItem.setAmount(1);
	}

	public boolean hasLiName() {
		return this.getLotteryItemName() != null;
	}

	public boolean hasLiLore() {
		if (addGiftsLore)
			return true;
		if ((typeItem.hasItemMeta() && typeItem.getItemMeta().hasLore()))
			return true;
		if (this.lore != null && this.lore.size() > 0)
			return true;
		return false;
	}

	public String getCustomName() {
		return name;
	}

	public String getLotteryItemName() {
		if (name != null)
			return name;
		if (typeItem.hasItemMeta() && typeItem.getItemMeta().hasDisplayName())
			return typeItem.getItemMeta().getDisplayName();
		return null;
	}

	public void setName(String str) {
		name = str;
	}

	public List<String> getCustomLore() {
		return this.lore;
	}

	public void setLore(List<String> lore) {
		if (lore == null) {
			this.lore = lore;
			return;
		}
		if (this.lore == null)
			this.lore = new ArrayList<>();
		this.lore.clear();
		this.lore.addAll(lore);
	}

	public String getItemId() {
		return itemId;
	}

	public Consoprize getConsoPrize() {
		return this.consoPrize;
	}

	public void setConsoPrize(Consoprize cp) {
		this.consoPrize = cp;
	}

	public boolean hasConsoPrize() {
		return this.getConsoPrize().getRecive() != null;
	}

	public Key getKey() {
		return this.key;
	}

	public void setKey(Key k) {
		this.key = k;
	}

	public boolean hasKey() {
		return this.getKey().getRecive() != null;
	}

	public int getCoolDown() {
		return this.coolDown;
	}

	public void setCoolDown(int c) {
		this.coolDown = c;
	}

	public boolean hasCoolDown() {
		return this.coolDown > 0;
	}

	public String getPeriodOfUse() {
		return this.periodOfUse;
	}

	public void setPeriodOfUse(String str) {
		this.periodOfUse = str;
	}

	public boolean hasPeriodOfUse() {
		return this.periodOfUse != null;
	}

	public String getSoundSet() {
		return this.soundSet;
	}

	public void setSoundSet(String str) {
		this.soundSet = str;
	}

	public boolean hasSoundSet() {
		return this.soundSet != null;
	}

	public boolean isAddGiftsLore() {
		return this.addGiftsLore;
	}

	public void setAddGiftsLore(boolean b) {
		this.addGiftsLore = b;
	}

	public boolean isSingleExtract() {
		return this.singleExtract;
	}

	public void setSingleExtract(boolean b) {
		this.singleExtract = b;
	}

	public boolean isRandomExtract() {
		return this.randomExtract;
	}

	public void setRandomExtract(boolean b) {
		this.randomExtract = b;
	}

	public boolean isSelectable() {
		return this.selectable;
	}

	public void setSelectable(boolean b) {
		this.selectable = b;
	}

	public List<Gift> getGifts() {
		return this.gifts;
	}

	public void sortGifts() {
		Collections.sort(this.gifts);
	}

	public ItemStack buildItem() {
		return new LotteryItemBuilder(this).buildLotteryItem();
	}

	public boolean isFinished() {
		if (this.hasLiName() && this.hasLiLore() && this.gifts.size() > 0)
			return true;
		return false;
	}

	@Override
	public int compareTo(LotteryItem li) {
		return this.getItemId().compareTo(li.getItemId());
	}

}
