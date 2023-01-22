package com.anthonyhilyard.equipmentcompare.compat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.theillusivec4.curios.api.CuriosApi;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;

public class CuriosHandler
{
	public static List<ItemStack> getCuriosMatchingSlot(LivingEntity player, ItemStack curio)
	{
		List<ItemStack> result = new ArrayList<ItemStack>();
		Set<String> tags = CuriosApi.getCuriosHelper().getCurioTags(curio.getItem());

		if (tags.isEmpty())
		{
			return result;
		}

		LazyOptional<IItemHandlerModifiable> allCurios = CuriosApi.getCuriosHelper().getEquippedCurios(player);

		if (allCurios.isPresent())
		{
			for (int i = 0; i < allCurios.resolve().get().getSlots(); i++)
			{
				ItemStack itemStack = allCurios.resolve().get().getStackInSlot(i);

				// If this curio shares any tags with the input curio, add it.
				Set<String> itemTags = CuriosApi.getCuriosHelper().getCurioTags(itemStack.getItem());
				Set<String> sharedTags = new HashSet<String>(tags);
				sharedTags.retainAll(itemTags);
				if (!sharedTags.isEmpty())
				{
					result.add(itemStack);
				}
			}
		}

		return result;
	}
}
