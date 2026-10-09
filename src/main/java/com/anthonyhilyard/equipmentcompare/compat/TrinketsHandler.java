package com.anthonyhilyard.equipmentcompare.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Optional Trinkets compat — compiled without Trinkets on the classpath.
 * Real integration requires Trinkets at runtime and is invoked via reflection from ComparisonTooltips.
 */
public final class TrinketsHandler {
	private TrinketsHandler() {}

	public static List<ItemStack> getTrinketsMatchingSlot(LivingEntity entity, ItemStack stack) {
		return new ArrayList<>();
	}
}
