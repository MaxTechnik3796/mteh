package cz.maxtechnik.mteh;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
public class MtehEnderChestScreen extends AbstractContainerScreen<MtehEnderChestMenu>{
	// Výchozí širší rozměry pro umístění postranních panelů
	public MtehEnderChestScreen(MtehEnderChestMenu menu,Inventory playerInventory,Component title){
		super(menu,playerInventory,title);
		this.imageWidth=256;
		this.imageHeight=166;
	}
	@Override
	protected void renderBg(GuiGraphics guiGraphics,float partialTick,int mouseX,int mouseY){
		// Tmavé pozadí pro vizuální test slotů (vlastní texturu dosadíme později)
		int x=(this.width-this.imageWidth)/2;
		int y=(this.height-this.imageHeight)/2;
		guiGraphics.fill(x,y,x+this.imageWidth,y+this.imageHeight,0xCC101010);
	}
	@Override
	public void render(@NotNull GuiGraphics guiGraphics,int mouseX,int mouseY,float partialTick){
		super.render(guiGraphics,mouseX,mouseY,partialTick);
		this.renderTooltip(guiGraphics,mouseX,mouseY);
	}
}