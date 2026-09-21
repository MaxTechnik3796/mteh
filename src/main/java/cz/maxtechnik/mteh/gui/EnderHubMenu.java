package cz.maxtechnik.mteh.gui;

import com.mojang.datafixers.util.Pair;
import cz.maxtechnik.mteh.MtehMod;
import cz.maxtechnik.mteh.MtehServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
public class EnderHubMenu extends AbstractContainerMenu{
	private static final Map<UUID,Boolean> SHIFT_MODES=new ConcurrentHashMap<>();
	private static final Map<UUID,Boolean> GRID_MODES=new ConcurrentHashMap<>();
	private static final EquipmentSlot[] ARMOR_SLOTS=new EquipmentSlot[]{
			EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET
	};
	public static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET=ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE=ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS=ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS=ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
	public static final ResourceLocation EMPTY_ARMOR_SLOT_SHIELD=ResourceLocation.withDefaultNamespace("item/empty_armor_slot_shield");
	private static final ResourceLocation[] ARMOR_TEXTURES=new ResourceLocation[]{
			EMPTY_ARMOR_SLOT_HELMET,EMPTY_ARMOR_SLOT_CHESTPLATE,EMPTY_ARMOR_SLOT_LEGGINGS,EMPTY_ARMOR_SLOT_BOOTS
	};
	private final Container enderChest;
	private final CraftingContainer craftSlots=new TransientCraftingContainer(this,3,3);
	private final ResultContainer resultSlots=new ResultContainer();
	private final Player player;
	private boolean shiftMode;
	private boolean gridMode;
	public EnderHubMenu(int containerId,Inventory playerInventory){
		this(containerId,playerInventory,new SimpleContainer(27));
	}
	public EnderHubMenu(int containerId,Inventory playerInventory,Container enderChest){
		super(MenuType.GENERIC_9x3,containerId);
		this.enderChest=enderChest;
		this.player=playerInventory.player;
		this.shiftMode=SHIFT_MODES.getOrDefault(this.player.getUUID(),false);
		this.gridMode=GRID_MODES.getOrDefault(this.player.getUUID(),false);
		// Ender (3x9) - Index 0 - 26
		for(int row=0;row<3;++row){
			for(int col=0;col<9;++col){
				this.addSlot(new Slot(enderChest,col+row*9,44+col*18,18+row*18));
			}
		}
		// Inv (3x9) - Index 27 - 53
		for(int row=0;row<3;++row){
			for(int col=0;col<9;++col){
				this.addSlot(new Slot(playerInventory,col+row*9+9,44+col*18,85+row*18));
			}
		}
		// Hotbar (1x9) - Index 54 - 62
		for(int col=0;col<9;++col){
			this.addSlot(new Slot(playerInventory,col,44+col*18,143));
		}
		// Armor - Index 63 - 66
		for(int i=0;i<4;++i){
			final EquipmentSlot slotType=ARMOR_SLOTS[i];
			final ResourceLocation texture=ARMOR_TEXTURES[i];
			this.addSlot(new Slot(playerInventory,39-i,11,29+i*18){
				@Override
				public void setByPlayer(@NotNull ItemStack newStack,@NotNull ItemStack oldStack){
					player.onEquipItem(slotType,oldStack,newStack);
					super.setByPlayer(newStack,oldStack);
				}
				@Override
				public int getMaxStackSize(){
					return 1;
				}
				@Override
				public boolean mayPlace(@NotNull ItemStack itemStack){
					return itemStack.canEquip(slotType,player);
				}
				@Override
				public Pair<ResourceLocation,ResourceLocation> getNoItemIcon(){
					return Pair.of(InventoryMenu.BLOCK_ATLAS,texture);
				}
			});
		}
		// Offhand - Index 67
		this.addSlot(new Slot(playerInventory,40,11,107){
			@Override
			public Pair<ResourceLocation,ResourceLocation> getNoItemIcon(){
				return Pair.of(InventoryMenu.BLOCK_ATLAS,EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
		// Crafting Result - Index 68
		this.addSlot(new ResultSlot(playerInventory.player,this.craftSlots,this.resultSlots,0,239,94));
		// Crafting Grid (3x3) - Index 69 - 77
		for(int r=0;r<3;++r){
			for(int c=0;c<3;++c){
				final boolean isExtra=(r==2||c==2);
				this.addSlot(new Slot(this.craftSlots,c+r*3,221+c*18,29+r*18){
					@Override
					public boolean isActive(){
						return !isExtra||hasCraftingTable();
					}
				});
			}
		}
	}
	public boolean getShiftMode(){
		return this.shiftMode;
	}
	public boolean getGridMode(){
		return this.gridMode;
	}
	public void toggleShiftMode(){
		this.shiftMode=!this.shiftMode;
		SHIFT_MODES.put(this.player.getUUID(),this.shiftMode);
	}
	public void toggleGridMode(){
		this.gridMode=!this.gridMode;
		GRID_MODES.put(this.player.getUUID(),this.gridMode);
	}
	@Override
	public boolean clickMenuButton(@NotNull Player player,int id){
		switch(id){
			case 0 -> {
				this.toggleShiftMode();
				return true;
			}
			case 1 -> {
				this.toggleGridMode();
				return true;
			}
			default -> {
				return false;
			}
		}
	}
	public boolean hasCraftingTable(){
		if(!MtehServerConfig.REQUIRE_CRAFTING_TABLE.get()) return true;
		for(ItemStack itemStack: this.player.getInventory().items)
			if(isCraftingTable(itemStack)) return true;
		for(ItemStack itemStack: this.player.getInventory().offhand)
			if(isCraftingTable(itemStack)) return true;
		for(ItemStack itemStack: this.player.getInventory().armor)
			if(isCraftingTable(itemStack)) return true;
		for(int i=0;i<this.enderChest.getContainerSize();++i)
			if(isCraftingTable(this.enderChest.getItem(i))) return true;
		return false;
	}
	private boolean isCraftingTable(ItemStack itemStack){
		if(itemStack==null||itemStack.isEmpty()) return false;
		return itemStack.is(ItemTags.create(ResourceLocation.parse(MtehServerConfig.CRAFTING_TABLE_TAG.get())));
	}
	@Override
	public void clicked(int slotId,int button,@NotNull ClickType clickType,@NotNull Player player){
		super.clicked(slotId,button,clickType,player);
		this.slotsChanged(this.craftSlots);
	}
	@Override
	public void slotsChanged(@NotNull Container container){
		Level level=this.player.level();
		if(!level.isClientSide){
			if(!this.hasCraftingTable()){
				for(int r=0;r<3;++r){
					for(int c=0;c<3;++c){
						if(r==2||c==2){
							int slotIdx=c+r*3;
							ItemStack extraStack=this.craftSlots.getItem(slotIdx);
							if(!extraStack.isEmpty()){
								this.player.getInventory().placeItemBackInInventory(extraStack);
								this.craftSlots.setItem(slotIdx,ItemStack.EMPTY);
							}
						}
					}
				}
			}
			CraftingInput input=this.craftSlots.asCraftInput();
			Optional<RecipeHolder<CraftingRecipe>> recipe=Objects.requireNonNull(level.getServer()).getRecipeManager().getRecipeFor(RecipeType.CRAFTING,input,level);
			if(recipe.isPresent()) this.resultSlots.setItem(0,recipe.get().value().assemble(input,level.registryAccess()));
			else this.resultSlots.setItem(0,ItemStack.EMPTY);
			this.broadcastChanges();
		}
	}
	@Override
	public void removed(@NotNull Player player){
		super.removed(player);
		this.enderChest.stopOpen(player);
		this.clearContainer(player,this.craftSlots);
	}
	@Override
	public boolean stillValid(@NotNull Player player){
		return this.enderChest.stillValid(player);
	}
	private boolean moveToHotbarThenInv(ItemStack itemStack){
		boolean moved=this.moveItemStackTo(itemStack,54,63,false);
		if(!itemStack.isEmpty()&&this.moveItemStackTo(itemStack,27,54,false)) moved=true;
		return !moved;
	}
	private boolean moveToEnder(ItemStack itemStack){
		return !this.moveItemStackTo(itemStack,0,27,false);
	}
	private boolean moveToGrid(ItemStack itemStack){
		boolean hasTable=this.hasCraftingTable();
		boolean moved=false;
		for(int r=0;r<3;++r){
			for(int c=0;c<3;++c){
				if(!hasTable&&(r==2||c==2)) continue;
				int slotIdx=69+(c+r*3);
				Slot slot=this.slots.get(slotIdx);
				ItemStack slotStack=slot.getItem();
				if(!slotStack.isEmpty()&&ItemStack.isSameItemSameComponents(itemStack,slotStack)){
					int max=Math.min(slot.getMaxStackSize(slotStack),itemStack.getMaxStackSize());
					int space=max-slotStack.getCount();
					if(space>0){
						int toAdd=Math.min(itemStack.getCount(),space);
						slotStack.grow(toAdd);
						itemStack.shrink(toAdd);
						slot.setChanged();
						moved=true;
						if(itemStack.isEmpty()) return false;
					}
				}
			}
		}
		for(int r=0;r<3;++r){
			for(int c=0;c<3;++c){
				if(!hasTable&&(r==2||c==2)) continue;
				int slotIdx=69+(c+r*3);
				Slot slot=this.slots.get(slotIdx);
				if(!slot.hasItem()&&slot.mayPlace(itemStack)){
					int max=Math.min(slot.getMaxStackSize(itemStack),itemStack.getMaxStackSize());
					int toAdd=Math.min(itemStack.getCount(),max);
					slot.setByPlayer(itemStack.split(toAdd));
					slot.setChanged();
					moved=true;
					if(itemStack.isEmpty()) return false;
				}
			}
		}
		return !moved;
	}
	private boolean inRange(int index,int min,int max){
		return min<=index&&index<=max;
	}
	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player,int index){
		ItemStack itemstack=ItemStack.EMPTY;
		Slot slot=this.slots.get(index);
		if(MtehServerConfig.DEBUG.get()) MtehMod.LOGGER.debug("Slot index: [{}] ShiftMode: [{}] GridMode: [{}]",index,this.shiftMode,this.gridMode);
		if(slot.hasItem()){
			ItemStack slotStack=slot.getItem();
			itemstack=slotStack.copy();
			if(inRange(index,69,77)){ // Crafting Grid
				if(this.gridMode){
					if(this.moveToEnder(slotStack)){
						return ItemStack.EMPTY;
					}
				}else{
					if(this.moveToHotbarThenInv(slotStack)){
						return ItemStack.EMPTY;
					}
				}
			}else if(index==68){ // Crafting Result
				slotStack.getItem().onCraftedBy(slotStack,player.level(),player);
				if(this.gridMode){
					if(this.moveToEnder(slotStack)){
						return ItemStack.EMPTY;
					}
				}else{
					if(this.moveToHotbarThenInv(slotStack)){
						return ItemStack.EMPTY;
					}
				}
				slot.onQuickCraft(slotStack,itemstack);
			}else if(inRange(index,63,67)){ // Armor & Offhand
				if(this.shiftMode){
					if(this.moveToGrid(slotStack)){
						return ItemStack.EMPTY;
					}
				}else{
					if(this.moveToHotbarThenInv(slotStack)){
						return ItemStack.EMPTY;
					}
				}
			}else if(inRange(index,27,62)){ // Hotbar & Inv
				if(this.shiftMode){
					if(this.moveToGrid(slotStack)){
						return ItemStack.EMPTY;
					}
				}else{
					if(this.moveToEnder(slotStack)){
						return ItemStack.EMPTY;
					}
				}
			}else if(inRange(index,0,26)){ // Ender Chest
				if(this.shiftMode){
					if(this.moveToGrid(slotStack)){
						return ItemStack.EMPTY;
					}
				}else{
					if(this.moveToHotbarThenInv(slotStack)){
						return ItemStack.EMPTY;
					}
				}
			}
			if(slotStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
			else slot.setChanged();
			if(slotStack.getCount()==itemstack.getCount()) return ItemStack.EMPTY;
			slot.onTake(player,slotStack);
			if(index==68) player.drop(slotStack,false);
		}
		return itemstack;
	}
}