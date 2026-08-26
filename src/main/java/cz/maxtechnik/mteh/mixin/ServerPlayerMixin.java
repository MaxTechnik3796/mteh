package cz.maxtechnik.mteh.mixin;

import cz.maxtechnik.mteh.MtehEnderChestMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

	@ModifyVariable(
			method = "openMenu(Lnet/minecraft/world/MenuProvider;Ljava/util/function/Consumer;)Ljava/util/OptionalInt;",
			at = @At("HEAD"),
			argsOnly = true
	)
	private MenuProvider mteh$wrapEnderChestProvider(MenuProvider originalProvider) {
		if (originalProvider == null) {
			return null;
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

				// Pokud původní menu otevírá inventář Ender truhly, dosadíme naše MtehEnderChestMenu
				if (menu instanceof ChestMenu chestMenu && chestMenu.getContainer() instanceof PlayerEnderChestContainer) {
					return new MtehEnderChestMenu(containerId, playerInventory, player.getEnderChestInventory());
				}

				return menu;
			}
		};
	}
}