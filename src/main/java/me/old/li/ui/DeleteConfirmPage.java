package me.old.li.ui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import me.old.li.Config;
import me.old.li.Utilss.ItemBuilder;

public class DeleteConfirmPage extends Page {

	protected boolean confirm;

	public DeleteConfirmPage(String title, int size) {
		super(title, size);
		this.confirm = false;
	}

	@Override
	public void init() {
		this.buttons.clear();
		setLineButtons();
		setConfirmButton();

		this.buttons.forEach((k, v) -> v.refresh());
	}

	private void setConfirmButton() {
		new Button((int) (Math.random() * this.inv.getSize())) {
			@Override
			protected void execute(Player p, InventoryClickEvent e) {
				e.setCancelled(true);
				confirm = true;
				p.closeInventory();
			}

			@Override
			protected void setDisplayItem() {
				this.display = new ItemBuilder(Material.LIME_WOOL, Config.BUTTON_CONFIRM_NAME,
						Config.BUTTON_CONFIRM_LORE).getItem();
			}
		};
	}

	public void setLineButtons() {
		for (int i = 0; i < this.inv.getSize(); i++) {
			new Button(i) {
				@Override
				protected void execute(Player p, InventoryClickEvent e) {
					e.setCancelled(true);
				}

				@Override
				protected void setDisplayItem() {
					this.display = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE, " ").getItem();
				}
			};
		}
	}

}
