package cz.maxtechnik.mteh.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import cz.maxtechnik.mteh.MtehMod;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@SuppressWarnings("ConstantValue")
@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class ServerCommonPacketListenerImplMixin{
	@Inject(method="send(Lnet/minecraft/network/protocol/Packet;)V", at=@At("HEAD"), cancellable=true)
	private void mteh$cancelVanillaOpenScreen(Packet<?> packet,CallbackInfo ci){
		if(packet instanceof ClientboundOpenScreenPacket&&(Object)this instanceof ServerGamePacketListenerImpl gameListener) if(gameListener.player!=null&&MtehMod.PENDING_ENDER_HUB.remove(gameListener.player.getUUID())) ci.cancel();
	}
	@WrapOperation(
			method="send(Lnet/minecraft/network/protocol/Packet;)V",
			at=@At(
					value="INVOKE",
					target="Lnet/minecraft/server/network/ServerCommonPacketListenerImpl;send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V"
			)
	)
	private void mteh$catchUnsupportedVanillaPackets(ServerCommonPacketListenerImpl instance,Packet<?> packet,PacketSendListener listener,Operation<Void> original){
		try{
			original.call(instance,packet,listener);
		}catch(UnsupportedOperationException ignored){
		}
	}
}