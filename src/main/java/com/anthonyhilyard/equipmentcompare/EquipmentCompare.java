package com.anthonyhilyard.equipmentcompare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@Mod(modid=EquipmentCompare.MODID, name=EquipmentCompare.MODNAME, version=EquipmentCompare.MODVERSION, acceptedMinecraftVersions = "[1.12.2]")
@EventBusSubscriber(modid = EquipmentCompare.MODID)
public class EquipmentCompare
{
	public static final String MODID = "equipmentcompare";
	public static final String MODNAME = "Equipment Compare";
	public static final String MODVERSION = "1.3.3";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static boolean comparisonsActive = false;
	public static boolean renderingSuccessful = false;
	private static final KeyBinding showComparisonTooltip = new KeyBinding("equipmentcompare.key.showTooltips", KeyConflictContext.GUI, Keyboard.KEY_LSHIFT, "key.categories.inventory");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ClientRegistry.registerKeyBinding(showComparisonTooltip);
		EquipmentCompareConfig.loadConfig(event.getSuggestedConfigurationFile());
	}

	@SubscribeEvent
	public static void onKeyInput(GuiScreenEvent.KeyboardInputEvent event)
	{
		// For some reason GUI conflict context does not seem to work properly for modifier keys?
		// In any case, this lower level approach works...
		comparisonsActive = Keyboard.isKeyDown(showComparisonTooltip.getKeyCode());
	}
}
