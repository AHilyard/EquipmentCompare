package com.anthonyhilyard.equipmentcompare;

import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyReleasedEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.lwjgl.glfw.GLFW;


public class EquipmentCompare
{
	public static boolean tooltipActive = false;
	private static final KeyMapping showComparisonTooltip = new KeyMapping("equipmentcompare.key.showTooltips", KeyConflictContext.GUI, InputConstants.Type.KEYSYM.getOrCreate(GLFW.GLFW_KEY_LEFT_SHIFT), "key.categories.inventory");

	public void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.registerKeyBinding(showComparisonTooltip);
	}

	@SubscribeEvent
	public static void onPreKeyDown(KeyboardKeyPressedEvent.Pre event)
	{
		if (showComparisonTooltip.matches(event.getKeyCode(), 0))
		{
			tooltipActive = true;
		}
	}

	@SubscribeEvent
	public static void onPreKeyUp(KeyboardKeyReleasedEvent.Pre event)
	{
		if (showComparisonTooltip.matches(event.getKeyCode(), 0))
		{
			tooltipActive = false;
		}
	}
}
