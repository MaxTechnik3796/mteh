package cz.maxtechnik.mteh;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
public class EnderHubMenu extends AbstractContainerMenu{
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
	public EnderHubMenu(int containerId,Inventory playerInventory){
		this(containerId,playerInventory,new SimpleContainer(27));
	}
	public EnderHubMenu(int containerId,Inventory playerInventory,Container enderChest){
		super(null,containerId);
		this.enderChest=enderChest;
		this.player=playerInventory.player;
		enderChest.startOpen(this.player);
		//Ender (3x9) - Index 0 - 26
		for(int row=0;row<3;++row){
			for(int col=0;col<9;++col){
				this.addSlot(new Slot(enderChest,col+row*9,44+col*18,18+row*18));
			}
		}
		//inv (3x9) - Index 27 - 53
		for(int row=0;row<3;++row){
			for(int col=0;col<9;++col){
				this.addSlot(new Slot(playerInventory,col+row*9+9,44+col*18,85+row*18));
			}
		}
		//Hotbar (1x9) - Index 54 - 62
		for(int col=0;col<9;++col){
			this.addSlot(new Slot(playerInventory,col,44+col*18,143));
		}
		//Armor - Index 63 - 66
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
		//Offhand - Index 67
		this.addSlot(new Slot(playerInventory,40,11,107){
			@Override
			public Pair<ResourceLocation,ResourceLocation> getNoItemIcon(){
				return Pair.of(InventoryMenu.BLOCK_ATLAS,EMPTY_ARMOR_SLOT_SHIELD);
			}
		});
		//Crafting Result - Index 68
		this.addSlot(new ResultSlot(playerInventory.player,this.craftSlots,this.resultSlots,0,239,94));
		//Crafting Grid (3x3) - Index 69 - 77
		for(int r=0;r<3;++r){
			for(int c=0;c<3;++c){
				final boolean isExtra=(r==2||c==2); // 5 dodatečných políček
				this.addSlot(new Slot(this.craftSlots,c+r*3,221+c*18,29+r*18){
					@Override
					public boolean isActive(){
						return !isExtra||hasCraftingTable();
					}
				});
			}
		}
	}
	public boolean hasCraftingTable(){
		//Hotbar, inv, Offhand, Armor) Check
		for(ItemStack stack: this.player.getInventory().items)
			if(isCraftingTable(stack)) return true;
		for(ItemStack stack: this.player.getInventory().offhand)
			if(isCraftingTable(stack)) return true;
		for(ItemStack stack: this.player.getInventory().armor)
			if(isCraftingTable(stack)) return true;
		//Ender Check
		for(int i=0;i<this.enderChest.getContainerSize();++i)
			if(isCraftingTable(this.enderChest.getItem(i))) return true;
		return false;
	}
	private boolean isCraftingTable(ItemStack stack){
		if(stack==null||stack.isEmpty()) return false;
		return stack.is(Items.CRAFTING_TABLE)||stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","crafting_tables")));
	}
	@Override
	public void slotsChanged(@NotNull Container container){
		Level level=this.player.level();
		if(!level.isClientSide){
			//On Crafting Lost:
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
			Optional<RecipeHolder<CraftingRecipe>> recipe=Objects.requireNonNull(level.getServer())
					.getRecipeManager()
					.getRecipeFor(RecipeType.CRAFTING,input,level);
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
	@Override
	public @NotNull ItemStack quickMoveStack(@NotNull Player player,int index){
		ItemStack itemstack=ItemStack.EMPTY;
		Slot slot=this.slots.get(index);
		if(slot.hasItem()){
			ItemStack slotStack=slot.getItem();
			itemstack=slotStack.copy();
			// Crafting Result (68) -> inv/Hotbar
			if(index==68){
				if(!this.moveItemStackTo(slotStack,27,63,true)) return ItemStack.EMPTY;
				slot.onQuickCraft(slotStack,itemstack);
			}
			// Ender (0-26) -> inv/Hotbar
			else if(index<27){
				if(!this.moveItemStackTo(slotStack,27,63,false)) return ItemStack.EMPTY;
			}
			// inv/Hotbar (27-62) -> Ender
			else if(index<63){
				if(!this.moveItemStackTo(slotStack,0,27,false)) return ItemStack.EMPTY;
			}
			// Armor (63-66), Offhand (67), Crafting Grid (69-77) -> inv
			else if(!this.moveItemStackTo(slotStack,27,63,false)) return ItemStack.EMPTY;
			if(slotStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
			else slot.setChanged();
			if(slotStack.getCount()==itemstack.getCount()) return ItemStack.EMPTY;
			slot.onTake(player,slotStack);
		}
		return itemstack;
	}
}