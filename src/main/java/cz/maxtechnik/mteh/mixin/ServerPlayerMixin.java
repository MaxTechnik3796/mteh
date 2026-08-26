package cz.maxtechnik.mteh.mixin;

import cz.maxtechnik.mteh.EnderHubMenu;
import cz.maxtechnik.mteh.MtehModPackets;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
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

	@Shadow private int containerCounter;
	@Shadow private void nextContainerCounter() {}
	@Shadow private void initMenu(AbstractContainerMenu menu) {}

	@Inject(
			method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at = @At("HEAD"),
			cancellable = true
	)
	private void mteh$handleOpenMenu(MenuProvider menuProvider, Consumer<AbstractContainerMenu> extraData, CallbackInfoReturnable<OptionalInt> cir) {
		if (menuProvider == null) {
			cir.setReturnValue(OptionalInt.empty());
			return;
		}

		ServerPlayer player = (ServerPlayer) (Object) this;

		if (player.containerMenu != player.inventoryMenu) {
			player.closeContainer();
		}

		this.nextContainerCounter();
		AbstractContainerMenu menu = menuProvider.createMenu(this.containerCounter, player.getInventory(), player);
		if (menu == null) {
			if (player.isSpectator()) {
				player.displayClientMessage(Component.translatable("container.spectatorCantOpen").withStyle(ChatFormatting.RED), true);
			}
			cir.setReturnValue(OptionalInt.empty());
			return;
		}

		// 1. Ender Chest pro modovaného hráče
		if (menu instanceof ChestMenu chestMenu && chestMenu.getContainer() instanceof PlayerEnderChestContainer
				&& player.connection != null && player.connection.hasChannel(MtehModPackets.OpenEnderChestPayload.TYPE)) {

			int containerId = this.containerCounter;
			EnderHubMenu customMenu = new EnderHubMenu(containerId, player.getInventory(), player.getEnderChestInventory());
			this.initMenu(customMenu);
			player.containerMenu = customMenu;

			// Odesíláme výhradně náš paket (žádný vanilla ClientboundOpenScreenPacket)
			PacketDistributor.sendToPlayer(player, new MtehModPackets.OpenEnderChestPayload(containerId));
			customMenu.sendAllDataToRemote();

			cir.setReturnValue(OptionalInt.of(containerId));
			return;
		}

		// 2. Všechny ostatní kontejnery (barely, normální truhly, pec, hráči bez módu)
		if (extraData != null) {
			extraData.accept(menu);
		}

		this.initMenu(menu);
		if (player.connection != null) {
			player.connection.send(new ClientboundOpenScreenPacket(menu.containerId, menu.getType(), menuProvider.getDisplayName()));
		}
		player.containerMenu = menu;

		cir.setReturnValue(OptionalInt.of(this.containerCounter));
	}
}