package cz.maxtechnik.mteh;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
public class EnderHubScreen extends AbstractContainerScreen<EnderHubMenu>{
	private static final ResourceLocation CONTAINER_BACKGROUND=ResourceLocation.withDefaultNamespace("textures/gui/container/generic_54.png");
	private static final ResourceLocation ENDER_HUB_BACKGROUND=ResourceLocation.fromNamespaceAndPath(MtehMod.MODID,"textures/gui/container/ender_hub.png");
	public EnderHubScreen(EnderHubMenu menu,Inventory playerInventory,Component title){
		super(menu,playerInventory,title);
		this.imageWidth=256;
		this.imageHeight=168;
		this.titleLabelX=44;
		this.titleLabelY=6;
		this.inventoryLabelX=44;
		this.inventoryLabelY=this.imageHeight-94;
	}
	@Override
	protected void renderBg(GuiGraphics guiGraphics,float partialTick,int mouseX,int mouseY){
		int x=(this.width-this.imageWidth)/2;
		int y=(this.height-this.imageHeight)/2;
		int chestX=x+36;
		//Armor&Offhand:
		guiGraphics.blit(ENDER_HUB_BACKGROUND,chestX-34,y+20,0,0,36,112,128,128);
		//Crafting:
		guiGraphics.blit(ENDER_HUB_BACKGROUND,chestX+173,y+20,73,0,55,81,128,128);
		//Ender:
		guiGraphics.blit(CONTAINER_BACKGROUND,chestX,y,0,0,176,71);
		//inv:
		guiGraphics.blit(CONTAINER_BACKGROUND,chestX,y+71,0,126,176,96);
	}
	@Override
	public void render(@NotNull GuiGraphics guiGraphics,int mouseX,int mouseY,float partialTick){
		super.render(guiGraphics,mouseX,mouseY,partialTick);
		this.renderTooltip(guiGraphics,mouseX,mouseY);
	}
}