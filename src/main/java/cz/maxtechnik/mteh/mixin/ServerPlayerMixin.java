package cz.maxtechnik.mteh.mixin;

import cz.maxtechnik.mteh.EnderHubMenu;
import cz.maxtechnik.mteh.MtehModPackets;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;
import java.util.function.Consumer;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

	// 1. Zabalíme MenuProvider na začátku metody (volá se pouze 1x pro jakýkoliv kontejner)
	@ModifyVariable(
			method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at = @At("HEAD"),
			argsOnly = true
	)
	private MenuProvider mteh$wrapMenuProvider(MenuProvider originalProvider) {
		if (originalProvider == null) return null;
		ServerPlayer player = (ServerPlayer) (Object) this;

		// Pokud hráč nemá náš mód, necháme původní provider beze změny
		if (player.connection == null || !player.connection.hasChannel(MtehModPackets.OpenEnderChestPayload.TYPE)) {
			return originalProvider;
		}

		return new MenuProvider() {
			@Override
			public @NotNull Component getDisplayName() {
				return originalProvider.getDisplayName();
			}

			@Nullable
			@Override
			public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
				AbstractContainerMenu menu = originalProvider.createMenu(containerId, playerInventory, player);

				// Pokud je to Ender Chestka, nahradíme ji naším EnderHubMenu
				if (menu instanceof ChestMenu chestMenu && chestMenu.getContainer() instanceof PlayerEnderChestContainer) {
					return new EnderHubMenu(containerId, playerInventory, player.getEnderChestInventory());
				}

				// Pro barely a obyčejné truhly vrátíme původní menu nedotčené
				return menu;
			}
		};
	}

	// 2. Po úspěšném otevření odešleme klientovi náš packet a data slotů
	@Inject(
			method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at = @At("RETURN")
	)
	private void mteh$sendCustomPacketOnOpen(MenuProvider menuProvider, Consumer<AbstractContainerMenu> extraData, CallbackInfoReturnable<OptionalInt> cir) {
		ServerPlayer player = (ServerPlayer) (Object) this;

		if (cir.getReturnValue().isPresent() && player.containerMenu instanceof EnderHubMenu customMenu) {
			int containerId = cir.getReturnValue().getAsInt();
			PacketDistributor.sendToPlayer(player, new MtehModPackets.OpenEnderChestPayload(containerId));
			customMenu.sendAllDataToRemote();
		}
	}
}