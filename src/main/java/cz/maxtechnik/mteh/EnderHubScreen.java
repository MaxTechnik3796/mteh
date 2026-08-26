package cz.maxtechnik.mteh;

import net.minecraft.ChatFormatting;
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
		this.imageWidth=277;
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
		guiGraphics.blit(ENDER_HUB_BACKGROUND,chestX-34,y+20,0,0,34,112,128,128);
		//Crafting:
		guiGraphics.blit(ENDER_HUB_BACKGROUND,chestX+176,y+20,58,0,70,99,128,128);
		//Ender Chest:
		guiGraphics.blit(CONTAINER_BACKGROUND,chestX,y,0,0,176,71);
		//inv:
		guiGraphics.blit(CONTAINER_BACKGROUND,chestX,y+71,0,126,176,96);
		if(!this.menu.hasCraftingTable()){
			for(int r=0;r<3;++r){
				for(int c=0;c<3;++c){
					if(r==2||c==2){
						int slotX=x+221+c*18;
						int slotY=y+29+r*18;
						guiGraphics.fill(slotX,slotY,slotX+16,slotY+16,0xAAFFFFFF);
					}
				}
			}
		}
	}
	@Override
	public void render(@NotNull GuiGraphics guiGraphics,int mouseX,int mouseY,float partialTick){
		super.render(guiGraphics,mouseX,mouseY,partialTick);
		this.renderTooltip(guiGraphics,mouseX,mouseY);
		if(!this.menu.hasCraftingTable()){
			int x=(this.width-this.imageWidth)/2;
			int y=(this.height-this.imageHeight)/2;
			for(int r=0;r<3;++r){
				for(int c=0;c<3;++c){
					if(r==2||c==2){
						int slotX=x+221+c*18;
						int slotY=y+29+r*18;
						if(mouseX>=slotX&&mouseX<slotX+16&&mouseY>=slotY&&mouseY<slotY+16){
							guiGraphics.renderTooltip(this.font,Component.translatable("tooltip.mteh.missing_crafting_table").withStyle(ChatFormatting.RED),mouseX,mouseY);
							return;
						}
					}
				}
			}
		}
	}
}