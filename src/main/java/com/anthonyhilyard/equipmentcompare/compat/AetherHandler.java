package com.anthonyhilyard.equipmentcompare.compat;

import java.util.ArrayList;
import java.util.List;

import com.gildedgames.the_aether.api.AetherAPI;
import com.gildedgames.the_aether.api.accessories.AccessoryType;
import com.gildedgames.the_aether.api.accessories.AetherAccessory;
import com.gildedgames.the_aether.api.player.IPlayerAether;
import com.gildedgames.the_aether.api.player.util.IAccessoryInventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class AetherHandler
{
	public static List<ItemStack> getAetherAccessoriesMatchingSlot(EntityPlayer player, ItemStack itemStack)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();

		if (!AetherAPI.getInstance().isAccessory(itemStack))
		{
			return result;
		}

		AetherAccessory accessory = AetherAPI.getInstance().getAccessory(itemStack);

		// If this item isn't an aether accessory, return now.
		if (accessory == null)
		{
			return result;
		}

		// Get the slot for the input accessory.
		AccessoryType accessoryType = accessory.getAccessoryType();

		IPlayerAether aetherPlayer = AetherAPI.getInstance().get(player);
		if (aetherPlayer == null)
		{
			return result;
		}

		IAccessoryInventory accessories = aetherPlayer.getAccessoryInventory();
		if (accessories == null)
		{
			return result;
		}

		// Now find all equipped accessories that match the slot.
		for (ItemStack stack : accessories.getAccessories())
		{
			if (stack != ItemStack.EMPTY)
			{
				AetherAccessory equippedAccessory = AetherAPI.getInstance().getAccessory(stack);
				if (equippedAccessory != null)
				{
					if (equippedAccessory.getAccessoryType() == accessoryType)
					{
						result.add(stack);
					}
				}
			}
		}

		return result;
	}
}
