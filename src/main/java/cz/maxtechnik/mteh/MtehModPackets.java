package cz.maxtechnik.mteh;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class MtehModPackets {
	public record OpenEnderChestPayload(int containerId) implements CustomPacketPayload {
		public static final Type<OpenEnderChestPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(MtehMod.MODID, "open_ender_chest"));
		public static final StreamCodec<FriendlyByteBuf, OpenEnderChestPayload> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT,
				OpenEnderChestPayload::containerId,
				OpenEnderChestPayload::new
		);

		@Override
		public @NotNull Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}

		public static void handleClient(OpenEnderChestPayload payload, IPayloadContext context) {
			context.enqueueWork(() -> MtehClientHandler.openScreen(payload.containerId()));
		}
	}
}