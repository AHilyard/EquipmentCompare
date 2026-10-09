package com.anthonyhilyard.equipmentcompare.compat;

/**
 * Optional Legendary Tooltips compat — defaults when the mod is absent.
 */
public final class LegendaryTooltipsHandler {
	private LegendaryTooltipsHandler() {}

	public static boolean getCenteredTitle() {
		return false;
	}

	public static boolean getEnforceMinimumWidth() {
		return false;
	}
}
