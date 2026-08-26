package cz.maxtechnik.mteh.mixin;

import cz.maxtechnik.mteh.EnderHubMenu;
import cz.maxtechnik.mteh.MtehMod;
import cz.maxtechnik.mteh.MtehModPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.function.Consumer;
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin{
	@Shadow
	private int containerCounter;
	@Shadow
	private void nextContainerCounter(){
	}
	@Shadow
	private void initMenu(AbstractContainerMenu menu){
	}
	@Inject(
			method="openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at=@At("HEAD"),
			cancellable=true
	)
	private void mteh$replaceEnderChestOpen(MenuProvider menuProvider,Consumer<AbstractContainerMenu> extraData,CallbackInfoReturnable<OptionalInt> cir){
		if(menuProvider==null) return;
		ServerPlayer player=(ServerPlayer)(Object)this;
		if(!MtehMod.hasMod(player.getUUID())) return;
		AbstractContainerMenu menu=menuProvider.createMenu(this.containerCounter+1,player.getInventory(),player);
		if(menu instanceof ChestMenu chestMenu&&chestMenu.getContainer() instanceof PlayerEnderChestContainer){
			if(player.containerMenu!=player.inventoryMenu) player.closeContainer();
			this.nextContainerCounter();
			int containerId=this.containerCounter;
			EnderHubMenu customMenu=new EnderHubMenu(containerId,player.getInventory(),player.getEnderChestInventory());
			this.initMenu(customMenu);
			player.containerMenu=customMenu;
			PacketDistributor.sendToPlayer(player,new MtehModPackets.OpenEnderChestPayload(containerId));
			customMenu.sendAllDataToRemote();
			cir.setReturnValue(OptionalInt.of(containerId));
		}
	}
}