package com.anthonyhilyard.equipmentcompare;

import com.anthonyhilyard.equipmentcompare.config.EquipmentCompareConfig;
import com.mojang.blaze3d.platform.InputConstants;

import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.fml.config.ModConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class EquipmentCompare implements ClientModInitializer
{
	public static final Logger LOGGER = LogManager.getLogger(Loader.MODID);

	public static boolean comparisonsActive = false;
	public static final KeyMapping showComparisonTooltip = KeyBindingHelper.registerKeyBinding(new KeyMapping("equipmentcompare.key.showTooltips",
																InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, KeyMapping.CATEGORY_INVENTORY));

	@Override
	public void onInitializeClient()
	{
		ForgeConfigRegistry.INSTANCE.register(Loader.MODID, ModConfig.Type.COMMON, EquipmentCompareConfig.SPEC);
	}
}
