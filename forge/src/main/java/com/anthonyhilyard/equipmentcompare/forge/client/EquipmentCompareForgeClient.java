package com.anthonyhilyard.equipmentcompare.forge.client;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@EventBusSubscriber(modid = EquipmentCompare.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class EquipmentCompareForgeClient
{
	@SubscribeEvent
	public static void onConstructMod(final FMLConstructModEvent evt)
	{
		EquipmentCompare.init();
	}
}
