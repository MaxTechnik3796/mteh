package cz.maxtechnik.mteh;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MtehEnderChestScreen extends AbstractContainerScreen<MtehEnderChestMenu> {

	// Vanilla textura pro 3-řadou truhlu
	private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");

	public MtehEnderChestScreen(MtehEnderChestMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 256;
		this.imageHeight = 168;

		// Posun nápisů o 36 px doprava, aby seděly nad vanilla texturou
		this.titleLabelX = 44;
		this.titleLabelY = 6;
		this.inventoryLabelX = 44;
		this.inventoryLabelY = this.imageHeight - 96 + 2; // y = 74
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		int x = (this.width - this.imageWidth) / 2;
		int y = (this.height - this.imageHeight) / 2;

		// 1. Tmavý podklad pod celou šířkou (aby boční sloty nevisely ve vzduchu)
		//guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xCC101010);

		// 2. Vanilla textura truhly vycentrovaná na středové sloty (+36 px zleva)
		int chestX = x + 36;

		// Horní část (3 řady Ender truhly: výška 71 px)
		guiGraphics.blit(CONTAINER_BACKGROUND, chestX, y, 0, 0, 176, 71);
		// Spodní část (inventář hráče + hotbar: výška 96 px)
		guiGraphics.blit(CONTAINER_BACKGROUND, chestX, y + 71, 0, 126, 176, 96);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);
	}
}