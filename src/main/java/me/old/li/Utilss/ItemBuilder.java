package me.old.li.Utilss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemBuilder {

	private ItemStack item = null;

	public ItemBuilder() {
	}
	
	public ItemBuilder(ItemStack is) {
		item = is.clone();
	}

	public ItemBuilder(Material material, String name, String... lore) {
		item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(Arrays.asList(lore));
		item.setItemMeta(im);
	}

	public ItemBuilder(ItemStack is, List<String> lore) {
		item = is.clone();
		ItemMeta im = item.getItemMeta();
		List<String> newLore = new ArrayList<>();
		if (im.hasLore())
			newLore.addAll(im.getLore());
		newLore.addAll(lore);
		im.setLore(newLore);
		item.setItemMeta(im);
	}

	public ItemBuilder(Material material, String name, List<String> lore) {
		item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		im.setLore(lore);
		item.setItemMeta(im);
	}

	public void setItem(ItemStack is) {
		item = is.clone();
	}

	public void setMaterial(Material m) {
		if (item == null)
			item = new ItemStack(m);
		else
			item.setType(m);
	}

	public void setName(String name) {
		setNameMeta(name);
	}

	public void setLore(List<String> lore) {
		setLoreMeta(lore);
	}
	
	public void addLore(List<String> lore) {
		ItemMeta im = item.getItemMeta();
		List<String> newLore = new ArrayList<>();
		if (im.hasLore())
			newLore.addAll(im.getLore());
		newLore.addAll(lore);
		im.setLore(newLore);
		item.setItemMeta(im);
	}

	private void setNameMeta(String name) {
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(name);
		item.setItemMeta(im);
	}

	private void setLoreMeta(List<String> lore) {
		ItemMeta im = item.getItemMeta();
		im.setLore(lore);
		item.setItemMeta(im);
	}

	public ItemStack getItem() {
		return item.clone();
	}
}
