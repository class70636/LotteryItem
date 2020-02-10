package me.old.li.Utilss;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBTItem;
import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.optionObjects.Gift;

public class LotteryItemBuilder {

	private LotteryItem li;
	private ItemStack temp;

	public LotteryItemBuilder(LotteryItem li) {
		this.li = li;
		this.temp = li.getTypeItem();
	}

	public ItemStack buildManagerShowItem() {
		setNameMeta();
		setManagerShowItemLoreMeta();
		return temp.clone();
	}

	public ItemStack buildLotteryItem() {
		setNameMeta();
		setLotteryItemLoreMeta();
		NBTItem nbti = new NBTItem(temp);
		nbti.setString("liKey", li.getItemId());
		return nbti.getItem();
	}

	private void setNameMeta() {
		if (li.getLotteryItemName() == null)
			return;
		ItemMeta im = temp.getItemMeta();
		im.setDisplayName(li.getLotteryItemName());
		temp.setItemMeta(im);
	}

	private void setLotteryItemLoreMeta() {
		List<String> newLore = new ArrayList<>();
		if (li.getCustomLore() != null) {
			newLore.addAll(li.getCustomLore());
		} else {
			if (temp.hasItemMeta() && temp.getItemMeta().hasLore())
				newLore.addAll(temp.getItemMeta().getLore());
		}
		if (li.isAddGiftsLore())
			newLore.addAll(getGiftsListLore());

		ItemMeta im = temp.getItemMeta();
		im.setLore(newLore);
		temp.setItemMeta(im);
	}

	private List<String> getGiftsListLore() {

		String cd_display = getCdDisplay();
		String deadline_display = getDeadlineDisplay();
		String key_display = getKeyDisplay();

		String cdArg = "{cd_display}";
		String dlArg = "{deadline_display}";
		String keyArg = "{key_display}";
		String giftsArg = "{gifts}";

		boolean hasDeadline = deadline_display != null;
		boolean hasCd = li.getCoolDown() > 0;
		boolean hasKey = key_display != null;

		List<String> lore = new ArrayList<>();
		for (String str : Config.GIFTS_LORE) {
			String tempString = str;
			// replace cd
			if (tempString.contains(cdArg)) {
				if (!hasCd) {
					if (isSingleLine(tempString, cdArg))
						continue;
					tempString = tempString.replace(cdArg, "");
				} else
					tempString = tempString.replace(cdArg, cd_display);
			}

			// replace deadline
			if (tempString.contains(dlArg)) {
				if (!hasDeadline) {
					if (isSingleLine(tempString, dlArg))
						continue;
					tempString = tempString.replace(dlArg, "");
				} else
					tempString = tempString.replace(dlArg, deadline_display);
			}

			// replace key
			if (tempString.contains(keyArg)) {
				if (!hasKey) {
					if (isSingleLine(tempString, keyArg))
						continue;
					tempString = tempString.replace(keyArg, "");
				} else
					tempString = tempString.replace(keyArg, key_display);
			}

			// replace gifts
			if (tempString.contains(giftsArg)) {
				for (String s : getGiftsDisplay())
					lore.add(tempString.replace(giftsArg, s));
				continue;
			}
			lore.add(tempString);
		}
		return lore;
	}

	private boolean isSingleLine(String line, String argument) {
		String tempLine = line.replaceAll(" ", "");
		return tempLine.length() == argument.length();
	}

	private String getCdDisplay() {
		return Config.CD_DISPLAY.replace("{seconds}", String.valueOf(li.getCoolDown()));
	}

	private String getDeadlineDisplay() {
		if (li.getPeriodOfUse() == null)
			return null;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(li.getPeriodOfUse());
			SimpleDateFormat sdf = new SimpleDateFormat(Config.DATE_FORMAT);
			return Config.DEADLINE_DISPLAY.replace("{date}", sdf.format(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getKeyDisplay() {
		if (li.getKey().getDisplay() == null)
			return null;
		return Config.KEY_DISPLAY.replace("{key}", li.getKey().getDisplay());
	}

	private List<String> getGiftsDisplay() {
		List<Gift> gifts = new ArrayList<>(li.getGifts());
		Collections.sort(gifts, new Comparator<Gift>() {
			@Override
			public int compare(Gift g1, Gift g2) {
				int x = (int) (((g2.getChance() - g1.getChance()) * 100000) / 100);
				int y = (int) (((g1.getChance() - g2.getChance()) * 100000) / 100);
				int z = g1.getItemName().compareTo(g2.getItemName());
				double d1 = Double.valueOf(g1.getAmount().split("-")[0]);
				double d2 = Double.valueOf(g2.getAmount().split("-")[0]);
				int w = (int) (((d1 - d2) * 100000) / 100);

				if (w == 0) {
					int l1 = g1.getAmount().split("-").length;
					int l2 = g2.getAmount().split("-").length;
					if (l1 != l2)
						w = l1 - l2;
					else {
						d1 = l1 > 1 ? Double.valueOf(g1.getAmount().split("-")[1]) : d1;
						d2 = l2 > 1 ? Double.valueOf(g2.getAmount().split("-")[1]) : d2;
						w = (int) (((d1 - d2) * 100000) / 100);
					}
				}

				switch (Config.GIFTS_ORDER) {
				case 1:
					return x;
				case 2:
					return y;
				case 3:
					return z;
				case 4:
					if (x == 0) {
						if (z == 0)
							return w;
						return z;
					}
					return x;
				case 5:
					if (y == 0) {
						if (z == 0)
							return w;
						return z;
					}
					return y;
				default:
					return z;
				}
			}
		});
		List<String> lore = new ArrayList<>();
		for (Gift g : gifts)
			if (li.isSelectable())
				lore.add(g.getSelectableDisplay());
			else
				lore.add(g.getItemDisplay());
		return lore;
	}

	private void setManagerShowItemLoreMeta() {
		List<String> newLore = new ArrayList<>();
		if (li.getCustomLore() != null) {
			newLore.addAll(li.getCustomLore());
		} else {
			if (temp.hasItemMeta() && temp.getItemMeta().hasLore())
				newLore.addAll(temp.getItemMeta().getLore());
		}
		List<String> lore = new ArrayList<>();
		for (String str : Config.BUTTON_MANAGER_LORE) {
			if (str.contains("{gifts}")) {
				if (li.getGifts().size() == 0) {
					lore.add(str.replace("{gifts}", Utils.getBooleanSymbol(false)));
					continue;
				}
				for (Gift g : li.getGifts()) {
					String gift_display = Config.GIFTS_MANAGER_DISPLAY
							.replace("{item_display}", g.getDisplayName() != null ? g.getDisplayName() : "無")
							.replace("{amount}", g.getAmount()).replace("{chance}", g.getChance() + "%")
							.replace("{broadcast}", Utils.getBooleanSymbol(g.getBroadcast()));
					lore.add(str.replace("{gifts}", gift_display));
				}
				continue;
			}
			String temp = str;
			temp = temp.replace("{id}", li.getItemId());
			temp = temp.replace("{conso_item}",
					(li.hasConsoPrize() ? li.getConsoPrize().getDisplay() : Utils.getBooleanSymbol(false)));
			temp = temp.replace("{key_item}", li.hasKey() ? li.getKey().getDisplay() : Utils.getBooleanSymbol(false));
			temp = temp.replace("{seconds}", String.valueOf(li.getCoolDown()));
			temp = temp.replace("{date}", li.hasPeriodOfUse() ? li.getPeriodOfUse() : Utils.getBooleanSymbol(false));
			temp = temp.replace("{sound}", li.hasSoundSet() ? li.getSoundSet() : Utils.getBooleanSymbol(false));
			temp = temp.replace("{addLore_symbol}", Utils.getBooleanSymbol(li.isAddGiftsLore()));
			temp = temp.replace("{singleExtract_symbol}", Utils.getBooleanSymbol(li.isSingleExtract()));
			temp = temp.replace("{randomExtract_symbol}", Utils.getBooleanSymbol(li.isRandomExtract()));
			temp = temp.replace("{selectable_symbol}", Utils.getBooleanSymbol(li.isSelectable()));

			lore.add(temp);
		}
		if (!li.isFinished())
			lore.addAll(Arrays.asList("", Config.NOT_FINISHED_YET_DISPLAY));
		newLore.addAll(lore);

		ItemMeta im = temp.getItemMeta();
		im.setLore(newLore);
		temp.setItemMeta(im);

	}

}
