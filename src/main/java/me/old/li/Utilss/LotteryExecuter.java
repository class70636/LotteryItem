package me.old.li.Utilss;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.old.li.Config;
import me.old.li.LotteryItem;
import me.old.li.Main;
import me.old.li.optionObjects.Gift;
import me.old.li.optionObjects.OptionObject;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Item;

public class LotteryExecuter {

	protected Player p;
	protected LotteryItem li;
	protected List<OptionObject> results;

	public LotteryExecuter(Player p, LotteryItem li) {
		this.p = p;
		this.li = li.clone();
		this.results = new ArrayList<>();
	}

	public boolean execute() {
		if (checkFirst())
			return false;
		// 加入冷卻以及刪除物品
		if (li.hasCoolDown()) {
			LICds liCds = Main.liCds.get(p);
			liCds.addLotteryItem(li.getItemId(), li.getCoolDown());
		}
		// 扣除開啟需求物品
		if (li.hasKey()) {
			li.getKey().executeRemove(p);
			showConsumeKey();
		}
		// 開始抽獎
		draw();
		// 顯示消耗物品
		showUseDisplay();
		// 顯示獲得物品
		showGetDisplay();
		return true;
	}

	protected boolean checkFirst() {
		// 超過使用期限
		if (li.hasPeriodOfUse()) {
			try {
				Date current = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date liDeadline = sdf.parse(li.getPeriodOfUse());

				if (!current.before(liDeadline)) {
					Utils.sendPluginMessage(p, Config.LOTTERYITEM_AFTER_DEADLINE);
					return true;
				}
			} catch (Throwable t) {
				Bukkit.getLogger().log(Level.WARNING, "無法轉換日期", t);
			}
		}
		// 檢查冷卻時間
		if (li.hasCoolDown() && !coolDownYet()) {
			showCdDisplay();
			return true;
		}
		// 檢查開啟需求
		if (li.hasKey() && !li.getKey().hasContain(p)) {
			Utils.sendPluginMessage(p, Config.LOTTERYITEM_NO_KEY);
			return true;
		}
		return false;
	}

	protected boolean coolDownYet() {
		// Check if not cooldown yet
		LICds liCds = Main.liCds.get(p);
		if (liCds == null) {
			Main.liCds.put(p, new LICds());
			return true;
		}
		if (liCds.hasLotteryItem(li.getItemId())) {
			return false;
		}
		return true;
	}

	protected void showCdDisplay() {
		// Check if not cooldown yet
		LICds liCds = Main.liCds.get(p);
		if (liCds.hasLotteryItem(li.getItemId())) {
			Utils.sendPluginMessage(p,
					Config.LOTTERYITEM_NOT_COOLDOWN_YET.replace("{seconds}", liCds.getCountDown(li.getItemId()) + ""));
			return;
		}
	}

	public void showConsumeKey() {
		String consume_key = Config.LOTTERYITEM_CONSUME_KEY;
		if (consume_key == null)
			return;

		String keyName = li.getKey().getTypeDisplay();
		String amount = li.getKey().getResultAmount();
		consume_key = consume_key.replace("{amount}", amount);

		if (!Config.SHOW_ITEM) {
			consume_key = consume_key.replace("{key}", keyName);
			Utils.sendPluginMessage(p, consume_key);
			return;
		}

		String trans = trans(consume_key, "{key}");
		String[] split = trans.split("\\{spe\\}");

		ItemStack key = li.getKey().buildItem();
		String keyJson = Utils.transItemToJson(key);

		ComponentBuilder cb = new ComponentBuilder(Config.SETTINGS_PLUGIN_PREFIX + " ");
		for (String str : split) {
			if (str.equals("{key}")) {
//				BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(keyJson) };
//				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

//				Text t = new Text(new BaseComponent[] { new TextComponent(keyJson) });
//				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, t);

				Item it = new Item("minecraft:" + key.getType().toString().toLowerCase(), key.getAmount(),
						ItemTag.ofNbt(keyJson));
				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, it);

				TextComponent component = new TextComponent(keyName);
				component.setHoverEvent(event);

				cb.append(component, FormatRetention.ALL);
				continue;
			}
			cb.append(new TextComponent(str), FormatRetention.NONE);
		}

		this.p.spigot().sendMessage(cb.create());
	}

	protected void draw() {
		// 是否為單一抽取
		if (li.isSingleExtract()) {
			List<Gift> newGifts = new ArrayList<>(li.getGifts());
			// 機率由小至大排序
			Utils.sortGifts(newGifts, 2);
			for (int i = 0; i <= newGifts.size(); i++) {
				if (results.size() > 0)
					break;
				if (i == newGifts.size()) {
					// 檢查是否有安慰獎
					if (li.hasConsoPrize())
						results.add(li.getConsoPrize().clone());
					else
						results.add(newGifts.get(i - 1));
					break;
				}
				newGifts.get(i).draw(results);
			}
			return;
		}
		// 正常抽
		for (Gift g : li.getGifts())
			g.draw(results);
		// 安慰獎
		if (results.size() == 0 && li.hasConsoPrize())
			results.add(li.getConsoPrize());

	}

	public void showGetDisplay() {
//		List<OptionObject> resultGifts = new ArrayList<>();
//		List<OptionObject> randomModeGifts = new ArrayList<>();
//		// 開始抽獎
//		for (Gift g : li.getGifts()) {
//			if (g.getChance() == 100)
//				g.draw(randomModeGifts);
//			g.draw(resultGifts);
//		}
//		// 沒有抽中任何獎品
//		if (resultGifts.size() == 0) {
//			// 檢查是否有安慰獎
//			if (li.hasConsoPrize()) {
//				resultGifts.add(li.getConsoPrize().clone());
//			}
//		}
		// 顯示沒抽到任何物品
		if (results.size() == 0) {
			Utils.sendPluginMessage(p, Config.LOTTERYITEM_GET_SHIT);
			return;
		}
//		// 隨機選取模式
//		if (li.isRandomExtract()) {
//			for (OptionObject oo : resultGifts) {
//				if (oo instanceof Gift && ((Gift) oo).getChance() == 100)
//					randomModeGifts.add(oo);
//			}
//			resultGifts.removeAll(randomModeGifts);
//			Gift g = (Gift) randomModeGifts.get((int) (Math.random() * randomModeGifts.size()));
//			showGiftDisplay(g);
//			g.executeGive(p);
//			if (g.getBroadcast())
//				broadCastGift(g);
//		}
//		// 顯示所有獎品名字及發送獎勵
		for (OptionObject o : results) {
			showGiftDisplay(o);
			o.executeGive(p);
			if (o instanceof Gift) {
				Gift gg = (Gift) o;
				if (gg.getBroadcast())
					broadCastGift(gg);
//				if (gg.hasSoundSet())
//					playSound(gg.getSound());
//				else if (li.hasSoundSet())
//					playSound(li.getSoundSet());
			}
		}

		// 播放聲音(獎品音效優先於全域音效)
		for (int i = 0; i <= results.size(); i++) {
			if (i == results.size()) {
				if (li.hasSoundSet())
					playSound(li.getSoundSet());
				break;
			}
			if (results.get(i) instanceof Gift && ((Gift) results.get(i)).hasSoundSet()) {
				Gift gg = (Gift) results.get(i);
				playSound(gg.getSound());

				break;
			}
		}
//		if (li.hasSoundSet())
//			playSound();
	}

	public void playSound(String soundStr) {
		String soundSet[] = soundStr.split(",");
		String soundName = soundSet[0].toUpperCase();

		Sound sound = Sound.valueOf(soundName);
		float volume = Float.parseFloat(soundSet[1]);
		float pitch = Float.parseFloat(soundSet[2]);

		p.playSound(p.getLocation(), sound, volume, pitch);
	}

	private void broadCastGift(Gift o) {
		String bro = Config.LOTTERYITEM_BROADCAST;
		bro = bro.replace("{player}", p.getName()).replace("{amount}", o.getResultAmount());

		String lotteryName = li.getLotteryItemName();
		String giftName = o.getTypeDisplay();
		if (!Config.SHOW_ITEM) {
			bro = bro.replace("{li_name}", lotteryName).replace("{gift_name}", giftName);
			for (Player player : Bukkit.getOnlinePlayers())
				Utils.sendPluginMessage(player, bro);
//			Utils.sendPluginMessage(p, bro);
			return;
		}

		String trans = trans(bro, "{li_name}", "{gift_name}");
		String[] split = trans.split("\\{spe\\}");

		ItemStack lottery_item = li.buildItem();
		String lottery_item_json = Utils.transItemToJson(lottery_item);

		ItemStack gift_item = o.buildItem();
		String gift_item_json = Utils.transItemToJson(gift_item);

		ComponentBuilder cb = new ComponentBuilder(Config.SETTINGS_PLUGIN_PREFIX + " ");
		for (String str : split) {
			// 替換抽獎物名字
			if (str.equals("{li_name}")) {
//				BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(lottery_item_json) };
//				Text t = new Text(new BaseComponent[] { new TextComponent(lottery_item_json) });
//				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, t);

				Item it = new Item("minecraft:" + lottery_item.getType().toString().toLowerCase(),
						lottery_item.getAmount(), ItemTag.ofNbt(lottery_item_json));
				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, it);

				TextComponent component = new TextComponent(lotteryName);
				component.setHoverEvent(event);

				cb.append(component, FormatRetention.ALL);
				continue;
			}
			// 替換獎品名字
			if (str.equals("{gift_name}")) {
//				BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(gift_item_json) };
//				Text t = new Text(new BaseComponent[] { new TextComponent(gift_item_json) });
//				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, t);

				Item it = new Item("minecraft:" + gift_item.getType().toString().toLowerCase(), gift_item.getAmount(),
						ItemTag.ofNbt(gift_item_json));
				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, it);

				TextComponent component = new TextComponent(giftName);
				component.setHoverEvent(event);

				cb.append(component, FormatRetention.ALL);
				continue;
			}
			cb.append(new TextComponent(str), FormatRetention.NONE);
		}
		for (Player player : Bukkit.getOnlinePlayers())
			player.spigot().sendMessage(cb.create());
	}

	public void showGiftDisplay(OptionObject o) {
		String gift_get = Config.LOTTERYITEM_GIFT_GET;
		if (gift_get == null)
			return;

		String giftName = o.getTypeDisplay();
		String amount = o.getResultAmount();
		gift_get = gift_get.replace("{amount}", amount);

		if (!Config.SHOW_ITEM) {
			gift_get = gift_get.replace("{gift_name}", giftName);
			Utils.sendPluginMessage(p, gift_get);
			return;
		}

		String trans = trans(gift_get, "{gift_name}");
		String[] split = trans.split("\\{spe\\}");

		ItemStack item = o.buildItem();
		String itemJson = Utils.transItemToJson(item);

		ComponentBuilder cb = new ComponentBuilder(Config.SETTINGS_PLUGIN_PREFIX + " ");

		for (String str : split) {
			if (str.equals("{gift_name}")) {
//				BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
//				Text t = new Text(new BaseComponent[] { new TextComponent(itemJson) });
//				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, t);

				Item it = new Item("minecraft:" + item.getType().toString().toLowerCase(), item.getAmount(),
						ItemTag.ofNbt(itemJson));
				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, it);

				TextComponent component = new TextComponent(giftName);
				component.setHoverEvent(event);

				cb.append(component, FormatRetention.ALL);
				continue;
			}
			cb.append(new TextComponent(str), FormatRetention.NONE);
		}

		this.p.spigot().sendMessage(cb.create());
	}

	public void showUseDisplay() {
		String li_use = Config.LOTTERYITEM_USE;
		if (li_use == null)
			return;

		String lotteryName = li.getLotteryItemName();

		if (!Config.SHOW_ITEM) {
			li_use = li_use.replace("{li_name}", lotteryName);
			Utils.sendPluginMessage(p, li_use);
			return;
		}

		String trans = trans(li_use, "{li_name}");

		String[] split = trans.split("\\{spe\\}");

		ItemStack item = li.buildItem();
		String itemJson = Utils.transItemToJson(item);

		ComponentBuilder cb = new ComponentBuilder(Config.SETTINGS_PLUGIN_PREFIX + " ");

		for (String str : split) {
			if (str.equals("{li_name}")) {
//				BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };
//				Text t = new Text(new BaseComponent[] { new TextComponent(itemJson) });
//				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

				Item it = new Item("minecraft:" + item.getType().toString().toLowerCase(), item.getAmount(),
						ItemTag.ofNbt(itemJson));
				HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, it);

				TextComponent component = new TextComponent(lotteryName);
				component.setHoverEvent(event);

				cb.append(component, FormatRetention.ALL);
				continue;
			}
			cb.append(new TextComponent(str), FormatRetention.NONE);
		}

		this.p.spigot().sendMessage(cb.create());
	}

	private String trans(String str, String... args) {
		String temp = str;
		for (String s : args) {
			temp = temp.replace(s, "{spe}" + s + "{spe}");
		}
		return temp;
	}

	public LotteryItem getLottery() {
		return this.li;
	}

}
