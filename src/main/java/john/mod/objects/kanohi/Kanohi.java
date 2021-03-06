package john.mod.objects.kanohi;

import com.google.common.base.Predicates;
import com.google.common.collect.Multimap;
import john.mod.Main;
import john.mod.init.ItemInit;
import john.mod.util.interfaces.IHasModel;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.UUID;

public class Kanohi extends Item implements IHasModel
{

	/** Holds the 'base' maxDamage that each armorType have. */
	private static final int[] MAX_DAMAGE_ARRAY = new int[] {13, 15, 16, 11};
	private static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	public static final String[] EMPTY_SLOT_NAMES = new String[] {"minecraft:items/empty_armor_slot_boots", "minecraft:items/empty_armor_slot_leggings", "minecraft:items/empty_armor_slot_chestplate", "minecraft:items/empty_armor_slot_helmet"};
	public static final IBehaviorDispenseItem DISPENSER_BEHAVIOR = new BehaviorDefaultDispenseItem()
	{
		/**
		 * Dispense the specified stack, play the dispense sound and spawn particles.
		 */
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack)
		{
			ItemStack itemstack = ItemArmor.dispenseArmor(source, stack);
			return itemstack.isEmpty() ? super.dispenseStack(source, stack) : itemstack;
		}
	};
	/** Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots */
	public final EntityEquipmentSlot armorType;
	/** Holds the amount of damage that the armor reduces at full durability. */
	public final int damageReduceAmount;
	public final float toughness;
	/**
	 * Used on RenderPlayer to select the correspondent armor to be rendered on the player: 0 is cloth, 1 is chain, 2 is
	 * iron, 3 is diamond and 4 is gold.
	 */
	public final int renderIndex;

	public static ItemStack dispenseArmor(IBlockSource blockSource, ItemStack stack)
	{
		BlockPos blockpos = blockSource.getBlockPos().offset((EnumFacing)blockSource.getBlockState().getValue(BlockDispenser.FACING));
		List<EntityLivingBase> list = blockSource.getWorld().<EntityLivingBase>getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(blockpos), Predicates.and(EntitySelectors.NOT_SPECTATING, new EntitySelectors.ArmoredMob(stack)));

		if (list.isEmpty())
		{
			return ItemStack.EMPTY;
		}
		else
		{
			EntityLivingBase entitylivingbase = list.get(0);
			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(stack);
			ItemStack itemstack = stack.splitStack(1);
			entitylivingbase.setItemStackToSlot(entityequipmentslot, itemstack);

			if (entitylivingbase instanceof EntityLiving)
			{
				((EntityLiving)entitylivingbase).setDropChance(entityequipmentslot, 2.0F);
			}

			return stack;
		}
	}

	public Kanohi(String name)
	{
		this.armorType = EntityEquipmentSlot.HEAD;
		this.renderIndex = 1;
		this.damageReduceAmount = 2;
		//this.setMaxDamage(materialIn.getDurability(equipmentSlotIn));
		this.toughness = 0;
		this.maxStackSize = 1;
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, DISPENSER_BEHAVIOR);

		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(Main.BIOTAB);

		ItemInit.ITEMS.add(this);
	}

	/**
	 * Gets the equipment slot of this armor piece (formerly known as armor type)
	 */
	@SideOnly(Side.CLIENT)
	public EntityEquipmentSlot getEquipmentSlot()
	{
		return EntityEquipmentSlot.HEAD;
	}

	/**
	 * Return the enchantability factor of the item, most of the time is based on material.
	 */
	public int getItemEnchantability()
	{
		return 0;
	}

	@Override
	public boolean isEnchantable(ItemStack stack)
	{
		return false;
	}

	/**
	 * Return the armor material for this armor item.
	 */
	/* public ItemArmor.ArmorMaterial getArmorMaterial()
	{
		return this.material;
	} */

	/**
	 * Return whether the specified armor ItemStack has a color.
	 */
	public boolean hasColor(ItemStack stack)
	{
		NBTTagCompound nbttagcompound = stack.getTagCompound();
		return nbttagcompound != null && nbttagcompound.hasKey("display", 10) ? nbttagcompound.getCompoundTag("display").hasKey("color", 3) : false;
	}

	/**
	 * Return the color for the specified armor ItemStack.
	 */
	public int getColor(ItemStack stack)
	{
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound != null)
		{
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

			if (nbttagcompound1 != null && nbttagcompound1.hasKey("color", 3))
			{
				return nbttagcompound1.getInteger("color");
			}
		}

		return 16777215; //10511680;
	}

	/**
	 * Remove the color from the specified armor ItemStack.
	 */
	public void removeColor(ItemStack stack)
	{
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound != null)
		{
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

			if (nbttagcompound1.hasKey("color"))
			{
				nbttagcompound1.removeTag("color");
			}
		}
	}

	/**
	 * Sets the color of the specified armor ItemStack
	 */
	public void setColor(ItemStack stack, int color)
	{
		NBTTagCompound nbttagcompound = stack.getTagCompound();

		if (nbttagcompound == null)
		{
			nbttagcompound = new NBTTagCompound();
			stack.setTagCompound(nbttagcompound);
		}

		NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");

		if (!nbttagcompound.hasKey("display", 10))
		{
			nbttagcompound.setTag("display", nbttagcompound1);
		}

		nbttagcompound1.setInteger("color", color);
	}

	/**
	 * Return whether this item is repairable in an anvil.
	 *
	 * @param toRepair the {@code ItemStack} being repaired
	 * @param repair the {@code ItemStack} being used to perform the repair
	 */
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return false;
	}

	/**
	 * Called when the equipped item is right clicked.
	 */
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);
		ItemStack itemstack1 = playerIn.getItemStackFromSlot(entityequipmentslot);

		if (itemstack1.isEmpty())
		{
			playerIn.setItemStackToSlot(entityequipmentslot, itemstack.copy());
			itemstack.setCount(0);
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}
		else
		{
			return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
		}
	}

	/**
	 * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
	 */
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot)
	{
		Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

		if (equipmentSlot == this.armorType)
		{
			multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double)this.damageReduceAmount, 0));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double)this.toughness, 0));
		}

		return multimap;
	}

	/**
	 * Determines if this armor will be rendered with the secondary 'overlay' texture.
	 * If this is true, the first texture will be rendered using a tint of the color
	 * specified by getColor(ItemStack)
	 *
	 * @param stack The stack
	 * @return true/false
	 */
	public boolean hasOverlay(ItemStack stack)
	{
		return true;
	}

	@Override
	public void registerModels()
	{
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
	{
		if(armorType == EntityEquipmentSlot.HEAD)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public EntityEquipmentSlot getEquipmentSlot(ItemStack stack)
	{
		return EntityEquipmentSlot.HEAD;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, net.minecraft.client.gui.ScaledResolution resolution, float partialTicks)
	{

	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){}
}
