package cz.maxtechnik.mteh;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.function.Supplier;
@SuppressWarnings("removal")
@Mod(MtehMod.MODID)
public class MtehMod{
	public static final String MODID="mteh";
	public static final Logger LOGGER=LogUtils.getLogger();
	public static final DeferredRegister<MenuType<?>> MENUS=DeferredRegister.create(Registries.MENU,MODID);
	public static final Supplier<MenuType<MtehEnderChestMenu>> ENDER_CHEST_MENU=MENUS.register("ender_chest_menu",()->new MenuType<>(MtehEnderChestMenu::new,FeatureFlags.VANILLA_SET));
	public MtehMod(IEventBus bus){
		bus.addListener(this::commonSetup);
		MENUS.register(bus);
	}
	private void commonSetup(final FMLCommonSetupEvent event){
		LOGGER.info("MT-EnderHub: Common Setup");
	}
	@EventBusSubscriber(modid=MODID, bus=EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
	public static class ClientModEvents{
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event){
			LOGGER.info("MT-EnderHub: Client Setup");
		}
		@SubscribeEvent
		public static void registerScreens(RegisterMenuScreensEvent event){
			event.register(ENDER_CHEST_MENU.get(),MtehEnderChestScreen::new);
		}
	}
}
