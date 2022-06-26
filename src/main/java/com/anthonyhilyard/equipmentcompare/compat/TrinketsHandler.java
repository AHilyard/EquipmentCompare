package com.anthonyhilyard.equipmentcompare.compat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import dev.emi.trinkets.api.TrinketsApi;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.Trinket;
import dev.emi.trinkets.api.TrinketComponent;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;


public class TrinketsHandler
{
	public static List<ItemStack> getTrinketsMatchingSlot(LivingEntity player, ItemStack stack)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();
		Trinket trinket = TrinketsApi.getTrinket(stack.getItem());

		// If this item isn't a trinket, bail.
		if (trinket == TrinketsApi.getDefaultTrinket())
		{
			return result;
		}

		Optional<TrinketComponent> trinketComponent = TrinketsApi.getTrinketComponent(player);
		if (trinketComponent.isPresent())
		{
			List<Tuple<SlotReference, ItemStack>> equippedTrinkets = trinketComponent.get().getAllEquipped();
			for (Tuple<SlotReference, ItemStack> equipped : equippedTrinkets)
			{
				// If the item we're checking can be inserted into the same slot as a currently equipped item, add it for comparison!
				if (TrinketSlot.canInsert(stack, equipped.getA(), player))
				{
					result.add(equipped.getB());
				}
			}
		}

		return result;
	}
}
