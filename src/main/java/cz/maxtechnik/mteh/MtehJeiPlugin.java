package cz.maxtechnik.mteh;

import cz.maxtechnik.mteh.gui.EnderHubMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
@SuppressWarnings("unused")
@JeiPlugin
public class MtehJeiPlugin implements IModPlugin{
	public static final ResourceLocation PLUGIN_UID=ResourceLocation.fromNamespaceAndPath(MtehMod.MODID,"jei_plugin");
	@Override
	public @NotNull ResourceLocation getPluginUid(){
		return PLUGIN_UID;
	}
	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration){
		registration.addRecipeTransferHandler(EnderHubMenu.class,null,RecipeTypes.CRAFTING,69,9,0,63);
	}
}