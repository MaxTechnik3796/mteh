package cz.maxtechnik.mteh.mixin;

import cz.maxtechnik.mteh.EnderHubMenu;
import cz.maxtechnik.mteh.MtehModPackets;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.neoforged.neoforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

	@ModifyVariable(
			method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at = @At(
					value = "INVOKE_ASSIGN",
					target = "Lnet/minecraft/world/MenuProvider;createMenu(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/entity/player/Player;)Lnet/minecraft/world/inventory/AbstractContainerMenu;"
			)
	)
	private AbstractContainerMenu mteh$replaceEnderChestMenu(AbstractContainerMenu menu) {
		ServerPlayer player = (ServerPlayer) (Object) this;

		// Kontrola, zda otevřené menu patří Ender truhle a klient má náš mód
		if (menu instanceof ChestMenu chestMenu && chestMenu.getContainer() instanceof PlayerEnderChestContainer) {
			if (player.connection != null && player.connection.hasChannel(MtehModPackets.OpenEnderChestPayload.TYPE)) {

				// Vytvoříme vlastní menu se stejným containerId
				EnderHubMenu customMenu = new EnderHubMenu(chestMenu.containerId, player.getInventory(), player.getEnderChestInventory());

				// Pošleme klientovi náš paket
				PacketDistributor.sendToPlayer(player, new MtehModPackets.OpenEnderChestPayload(chestMenu.containerId));

				return customMenu;
			}
		}

		// Všechny barely, shulker boxy i vanilla hráči projdou beze změny
		return menu;
	}
}