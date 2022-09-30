package com.anthonyhilyard.equipmentcompare.compat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;

import baubles.api.cap.BaublesCapabilities;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BaublesHandler
{
	public static List<ItemStack> getBaublesMatchingSlot(EntityPlayer player, ItemStack itemStack)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();

		IBauble bauble = itemStack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);

		// If this item isn't a bauble, return now.
		if (bauble == null)
		{
			return result;
		}

		// Get all the valid slots for the input bauble.
		Set<Integer> baubleSlots = Arrays.stream(bauble.getBaubleType(itemStack).getValidSlots()).boxed().collect(Collectors.toSet());
		IBaublesItemHandler allBaubles = BaublesApi.getBaublesHandler(player);

		if (allBaubles != null)
		{
			for (int i = 0; i < allBaubles.getSlots(); i++)
			{
				ItemStack stack = allBaubles.getStackInSlot(i);

				// If this bauble shares any valid slots with the input bauble, add it.
				if (stack != ItemStack.EMPTY)
				{
					IBauble equippedBauble = stack.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
					if (equippedBauble != null)
					{
						Set<Integer> sharedSlots = Arrays.stream(equippedBauble.getBaubleType(stack).getValidSlots()).boxed().collect(Collectors.toSet());
						sharedSlots.retainAll(baubleSlots);

						if (!sharedSlots.isEmpty())
						{
							result.add(stack);
						}
					}
				}
			}
		}

		return result;
	}
}
