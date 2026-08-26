package cz.maxtechnik.mteh;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class EnderHubScreen extends AbstractContainerScreen<EnderHubMenu> {
	private static final ResourceLocation CONTAINER_BACKGROUND = ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
	private static final ResourceLocation ENDER_HUB_BACKGROUND = ResourceLocation.fromNamespaceAndPath(MtehMod.MODID, "textures/gui/container/ender_hub.png");

	public EnderHubScreen(EnderHubMenu menu, Inventory playerInventory, Component title) {
		super(menu, playerInventory, title);
		this.imageWidth = 277;
		this.imageHeight = 168;
		this.titleLabelX = 44;
		this.titleLabelY = 6;
		this.inventoryLabelX = 44;
		this.inventoryLabelY = this.imageHeight - 94;
	}

	@Override
	protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
		int x = this.leftPos;
		int y = this.topPos;
		int chestX = x + 36;

		// Armor & Offhand panel
		guiGraphics.blit(ENDER_HUB_BACKGROUND, chestX - 34, y + 20, 0, 0, 34, 112, 128, 128);

		// Crafting panel
		guiGraphics.blit(ENDER_HUB_BACKGROUND, chestX + 176, y + 20, 58, 0, 70, 99, 128, 128);

		// Ender Chest slots area
		guiGraphics.blit(CONTAINER_BACKGROUND, chestX, y, 0, 0, 176, 71);

		// Player Inventory & Hotbar slots area
		guiGraphics.blit(CONTAINER_BACKGROUND, chestX, y + 71, 0, 126, 176, 96);

		// Visual feedback for locked 3x3 outer slots (crafting table required)
		if (!this.menu.hasCraftingTable()) {
			for (int r = 0; r < 3; ++r) {
				for (int c = 0; c < 3; ++c) {
					if (r == 2 || c == 2) {
						int slotX = x + 221 + c * 18;
						int slotY = y + 29 + r * 18;
						// Solid dark overlay
						guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xC0181818);
						// Diagonal cross lines (X pattern)
						for (int d = 0; d < 16; ++d) {
							guiGraphics.fill(slotX + d, slotY + d, slotX + d + 1, slotY + d + 1, 0x66FF4444);
							guiGraphics.fill(slotX + 15 - d, slotY + d, slotX + 16 - d, slotY + d + 1, 0x66FF4444);
						}
					}
				}
			}
		}
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		this.renderTooltip(guiGraphics, mouseX, mouseY);

		if (!this.menu.hasCraftingTable()) {
			for (int r = 0; r < 3; ++r) {
				for (int c = 0; c < 3; ++c) {
					if (r == 2 || c == 2) {
						int relX = 221 + c * 18;
						int relY = 29 + r * 18;
						if (this.isHovering(relX, relY, 16, 16, mouseX, mouseY)) {
							guiGraphics.renderTooltip(
									this.font,
									Component.translatable("tooltip.mteh.missing_crafting_table").withStyle(ChatFormatting.RED),
									mouseX,
									mouseY
							);
							return;
						}
					}
				}
			}
		}
	}
}