package cz.maxtechnik.mteh;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
@SuppressWarnings("removal")
@Mod(MtehMod.MODID)
public class MtehMod{
	public static final String MODID="mteh";
	public static final Logger LOGGER=LogUtils.getLogger();
	public static final Set<UUID> PENDING_ENDER_HUB=Collections.synchronizedSet(new HashSet<>());
	public MtehMod(IEventBus bus){
		bus.addListener(this::commonSetup);
		bus.addListener(this::registerPayloads);
	}
	private void registerPayloads(RegisterPayloadHandlersEvent event){
		PayloadRegistrar registrar=event.registrar("1").optional();
		registrar.playToClient(
				MtehModPackets.OpenEnderChestPayload.TYPE,
				MtehModPackets.OpenEnderChestPayload.STREAM_CODEC,
				MtehModPackets.OpenEnderChestPayload::handleClient
		);
	}
	private void commonSetup(final FMLCommonSetupEvent event){
		LOGGER.info("MT-EnderHub: Common Setup");
	}
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event){
		LOGGER.info("MT-EnderHub: Server Starting");
	}
	@EventBusSubscriber(modid=MODID, bus=EventBusSubscriber.Bus.MOD, value=Dist.CLIENT)
	public static class ClientModEvents{
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event){
			LOGGER.info("MT-EnderHub: Client Setup");
		}
	}
}