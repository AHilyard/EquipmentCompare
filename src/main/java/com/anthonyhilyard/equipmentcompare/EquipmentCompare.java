package com.anthonyhilyard.equipmentcompare;

import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;


public class EquipmentCompare implements ClientModInitializer
{
	public static boolean tooltipActive = false;
	public static final KeyMapping showComparisonTooltip = KeyBindingHelper.registerKeyBinding(new KeyMapping("Show comparison tooltip",
																InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, KeyMapping.CATEGORY_INVENTORY));

	@Override
	public void onInitializeClient()
	{
		EquipmentCompareConfig.init();
	}
}
