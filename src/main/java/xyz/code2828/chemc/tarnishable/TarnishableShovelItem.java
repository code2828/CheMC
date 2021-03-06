package xyz.code2828.chemc.tarnishable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.world.World;
import xyz.code2828.chemc.CheMC;

public class TarnishableShovelItem extends ShovelItem implements Tarnishable
{
	private ItemConvertible nextItem;
	public int tarnishTick;

	public TarnishableShovelItem(ToolMaterial material, Settings settings, ItemConvertible nextItem, int tarnishTick)
	{
		super(material, 1.5F, -3.0F, settings);
		this.nextItem = nextItem;
		this.tarnishTick = tarnishTick;
	}

	@Override
	public ItemConvertible getNextItem()
	{ return nextItem; }

	public void setNextItem(ItemConvertible ic)
	{ nextItem = ic; }

	@Override
	public int getTarnishTick()
	{ return tarnishTick; }

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected)
	{
		if (stack.getNbt().getKeys().contains("tarnishTick"))
		{
			// already has a timestamp; check if world time is bigger than the timestamp:
			long timestamp = stack.getNbt().getLong("tarnishTick");
			if (world.getTime() >= timestamp)
			{
				if (!(entity instanceof PlayerEntity))
				{
					// is not a PlayerEntity
					return;
				}
				// need to be 'tarnish'-ed!
				Tarnishable oldItem = (Tarnishable) stack.getItem();
				ItemConvertible newItem = oldItem.getNextItem();
				ItemStack newStack = new ItemStack(newItem);
				newStack.setDamage(Math.min(newItem.asItem().getMaxDamage(), stack.getDamage()));
				PlayerEntity pEnt = (PlayerEntity) entity;
				pEnt.getInventory().setStack(slot, newStack);
				CheMC.LOGGER.debug("The item " + oldItem.toString() + " of " + entity.getName() + " tarnished into "
						+ newItem.asItem().getName() + "!");
			}
		}
		else // no tarnishment timestamp
		{
			// create one
			long timestamp = world.getTime() + this.tarnishTick;
			stack.getNbt().putLong("tarnishTick", timestamp);
			CheMC.LOGGER
					.debug("Created a timestamp for ItemStack " + stack.toString() + " at time " + String.valueOf(timestamp));
		}
	}

}
