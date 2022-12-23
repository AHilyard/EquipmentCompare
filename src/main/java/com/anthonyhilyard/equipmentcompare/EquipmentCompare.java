package com.anthonyhilyard.equipmentcompare;

import net.minecraft.client.KeyMapping;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyPressed;
import net.minecraftforge.client.event.ScreenEvent.KeyReleased;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.glfw.GLFW;

public class EquipmentCompare
{
	public static boolean tooltipActive = false;
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
			tooltipActive = true;
		}
	}

	@SubscribeEvent
	public static void onPreKeyUp(KeyReleased.Pre event)
	{
		if (showComparisonTooltip.matches(event.getKeyCode(), 0))
		{
			tooltipActive = false;
		}
	}

	@SubscribeEvent
	public static void onScreenClosing(ScreenEvent.Closing event)
	{
		tooltipActive = false;
	}
}
