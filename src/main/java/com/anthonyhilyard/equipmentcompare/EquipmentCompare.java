package com.anthonyhilyard.equipmentcompare;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyPressedEvent;
import net.minecraftforge.client.event.ScreenEvent.KeyboardKeyReleasedEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


public class EquipmentCompare
{
	public static final Logger LOGGER = LogManager.getLogger(Loader.MODID);

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

	@SubscribeEvent
	public static void onRenderTick(RenderTickEvent event)
	{
		Minecraft minecraft = Minecraft.getInstance();
		if (event.phase == RenderTickEvent.Phase.END && minecraft.screen == null)
		{
			tooltipActive = false;
		}
	}
}
