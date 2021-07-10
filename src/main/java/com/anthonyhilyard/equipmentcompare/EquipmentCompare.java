package com.anthonyhilyard.equipmentcompare;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;


public class EquipmentCompare
{
	@SuppressWarnings("unused")
	public static final Logger LOGGER = LogManager.getLogger();
	private static final KeyBinding showComparisonTooltip = new KeyBinding("Show comparison tooltip", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.inventory");
	public static boolean tooltipActive = false;

	public void onClientSetup(FMLClientSetupEvent event)
	{
		ClientRegistry.registerKeyBinding(showComparisonTooltip);
	}

	@SubscribeEvent
	public static void onKeyInputEvent(KeyInputEvent event)
	{
		tooltipActive = showComparisonTooltip.isPressed();
	}

}
