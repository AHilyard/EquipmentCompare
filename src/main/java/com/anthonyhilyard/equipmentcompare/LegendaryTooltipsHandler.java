package com.anthonyhilyard.equipmentcompare;

import com.anthonyhilyard.legendarytooltips.LegendaryTooltipsConfig;

public class LegendaryTooltipsHandler
{
	public static boolean getCenteredTitle()
	{
		return LegendaryTooltipsConfig.INSTANCE.centeredTitle.get();
	}

	public static boolean getEnforceMinimumWidth()
	{
		return LegendaryTooltipsConfig.INSTANCE.enforceMinimumWidth.get();
	}
}
