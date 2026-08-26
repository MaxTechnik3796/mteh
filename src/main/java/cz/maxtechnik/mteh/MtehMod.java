package cz.maxtechnik.mteh;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@Mod(MtehMod.MODID)
public class MtehMod {
	public static final String MODID = "mteh";
	public static final Logger LOGGER = LogUtils.getLogger();

	public MtehMod(IEventBus bus) {
		bus.addListener(this::registerPayloads);
	}

	private void registerPayloads(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1").optional();
		registrar.playToClient(
				MtehModPackets.OpenEnderChestPayload.TYPE,
				MtehModPackets.OpenEnderChestPayload.STREAM_CODEC,
				MtehModPackets.OpenEnderChestPayload::handleClient
		);
	}
}