package com.anthonyhilyard.equipmentcompare.compat;

import com.anthonyhilyard.legendarytooltips.config.LegendaryTooltipsConfig;

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
