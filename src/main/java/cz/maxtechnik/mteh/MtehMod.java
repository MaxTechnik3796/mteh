package cz.maxtechnik.mteh;

import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
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
	private static final Set<UUID> MODDED_PLAYERS=Collections.synchronizedSet(new HashSet<>());
	public MtehMod(IEventBus bus){
		bus.addListener(this::commonSetup);
		bus.addListener(this::registerPayloads);
		NeoForge.EVENT_BUS.addListener(this::onPlayerLogout);
	}
	private void registerPayloads(RegisterPayloadHandlersEvent event){
		PayloadRegistrar registrar=event.registrar("1").optional();
		registrar.playToServer(
				MtehModPackets.ClientHelloPayload.TYPE,
				MtehModPackets.ClientHelloPayload.STREAM_CODEC,
				MtehModPackets.ClientHelloPayload::handleServer
		);
		registrar.playToClient(
				MtehModPackets.OpenEnderChestPayload.TYPE,
				MtehModPackets.OpenEnderChestPayload.STREAM_CODEC,
				MtehModPackets.OpenEnderChestPayload::handleClient
		);
	}
	public static void addModdedPlayer(UUID uuid){
		MODDED_PLAYERS.add(uuid);
	}
	public static void removeModdedPlayer(UUID uuid){
		MODDED_PLAYERS.remove(uuid);
	}
	public static boolean hasMod(UUID uuid){
		return MODDED_PLAYERS.contains(uuid);
	}
	private void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
		removeModdedPlayer(event.getEntity().getUUID());
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
	}
	@EventBusSubscriber(modid=MODID, bus=EventBusSubscriber.Bus.GAME, value=Dist.CLIENT)
	public static class ClientGameEvents{
		@SubscribeEvent
		public static void onPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event){
			PacketDistributor.sendToServer(new MtehModPackets.ClientHelloPayload());
		}
	}
}