package me.old.li;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerLotteryItems {

	private final List<LotteryItem> liList;

	public ServerLotteryItems() {
		this.liList = new ArrayList<>();
	}

	public LotteryItem getLotteryItem(String index) {
		for (LotteryItem ll : liList) {
			if (ll.getItemId().equals(index))
				return ll;
		}
		return null;
	}

	public List<LotteryItem> getLotteryItems() {
		return liList;
	}
	
	public void sort() {
		Collections.sort(liList);
	}

}
