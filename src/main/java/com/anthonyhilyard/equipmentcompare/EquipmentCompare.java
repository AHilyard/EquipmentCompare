package com.anthonyhilyard.equipmentcompare;

import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyPressed;
import net.minecraftforge.client.event.ScreenEvent.KeyReleased;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

public class EquipmentCompare
{
	public static final Logger LOGGER = LogManager.getLogger(Loader.MODID);

	public static boolean comparisonsActive = false;
	private static final KeyMapping showComparisonTooltip = new KeyMapping("equipmentcompare.key.showTooltips", KeyConflictContext.GUI, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_SHIFT), "key.categories.inventory");

	@SubscribeEvent
	public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event)
	{
		event.register(showComparisonTooltip);
	}

	@SubscribeEvent
	public static void onPreKeyDown(KeyPressed.Pre event)
	{
		if (showComparisonTooltip.matches(event.getKeyCode(), 0))
		{
			comparisonsActive = true;
		}
	}

	@SubscribeEvent
	public static void onPreKeyUp(KeyReleased.Pre event)
	{
		if (showComparisonTooltip.matches(event.getKeyCode(), 0))
		{
			comparisonsActive = false;
		}
	}

	@SubscribeEvent
	public static void onScreenClosing(ScreenEvent.Closing event)
	{
		comparisonsActive = false;
	}
}
