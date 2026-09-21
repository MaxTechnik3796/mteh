package cz.maxtechnik.mteh;

import net.neoforged.neoforge.common.ModConfigSpec;
public class MtehServerConfig{
	private static final ModConfigSpec.Builder BUILDER=new ModConfigSpec.Builder();
	public static final ModConfigSpec SPEC;
	public static final ModConfigSpec.BooleanValue DEBUG;
	public static final ModConfigSpec.BooleanValue REQUIRE_CRAFTING_TABLE;
	public static final ModConfigSpec.ConfigValue<String> CRAFTING_TABLE_TAG;
	static {
		DEBUG=BUILDER.define("debug",false);
		REQUIRE_CRAFTING_TABLE=BUILDER.comment("Require a crafting table in Inventory/Ender Chest for the 3x3 grid.").define("requireCraftingTable",true);
		CRAFTING_TABLE_TAG=BUILDER.comment("Item tag used to identify crafting tables.","Make sure the item tag exists!").define("crafting_table_tag","c:player_workstations/crafting_tables");
		SPEC=BUILDER.build();
	}
}
