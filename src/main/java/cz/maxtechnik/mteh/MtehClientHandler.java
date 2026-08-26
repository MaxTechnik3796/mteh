package cz.maxtechnik.mteh;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
public class MtehClientHandler{
	public static void openScreen(int containerId){
		Minecraft mc=Minecraft.getInstance();
		if(mc.player!=null){
			EnderHubMenu menu=new EnderHubMenu(containerId,mc.player.getInventory());
			mc.player.containerMenu=menu;
			mc.setScreen(new EnderHubScreen(menu,mc.player.getInventory(),Component.translatable("container.mteh.ender_hub")));
		}
	}
}