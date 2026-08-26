package cz.maxtechnik.mteh.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import cz.maxtechnik.mteh.EnderHubMenu;
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
public abstract class ServerPlayerMixin {
	@Shadow
	private int containerCounter;

	@Shadow
	private void initMenu(AbstractContainerMenu menu) {
	}

	@Inject(
			method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;initMenu(Lnet/minecraft/world/inventory/AbstractContainerMenu;)V"),
			cancellable = true
	)
	private void mteh$replaceEnderChestOpen(
			MenuProvider menuProvider,
			Consumer<AbstractContainerMenu> extraData,
			CallbackInfoReturnable<OptionalInt> cir,
			@Local AbstractContainerMenu abstractcontainermenu
	) {
		if (menuProvider == null || abstractcontainermenu == null) return;
		ServerPlayer player = (ServerPlayer) (Object) this;
		if (player.connection == null || !player.connection.hasChannel(MtehModPackets.OpenEnderChestPayload.TYPE)) {
			return;
		}

		if (abstractcontainermenu instanceof ChestMenu chestMenu && chestMenu.getContainer() instanceof PlayerEnderChestContainer) {
			int containerId = this.containerCounter;
			EnderHubMenu customMenu = new EnderHubMenu(containerId, player.getInventory(), player.getEnderChestInventory());
			this.initMenu(customMenu);
			if (extraData != null) {
				extraData.accept(customMenu);
			}
			player.containerMenu = customMenu;
			PacketDistributor.sendToPlayer(player, new MtehModPackets.OpenEnderChestPayload(containerId));
			customMenu.sendAllDataToRemote();
			cir.setReturnValue(OptionalInt.of(containerId));
		}
	}
}