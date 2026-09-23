package com.anthonyhilyard.equipmentcompare;

import com.anthonyhilyard.equipmentcompare.config.EquipmentCompareConfig;
import com.anthonyhilyard.iceberg.services.Services;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class EquipmentCompare
{
	public static final String MODID = "equipmentcompare";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static boolean comparisonsActive = false;
	public static final KeyMapping showComparisonTooltip = Services.getKeyMappingRegistrar().registerMapping(new KeyMapping("equipmentcompare.key.showTooltips",
																InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_SHIFT, KeyMapping.Category.INVENTORY));

	public static void init()
	{
		EquipmentCompareConfig.register(EquipmentCompareConfig.class, MODID);
	}
}