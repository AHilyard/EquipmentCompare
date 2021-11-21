package com.anthonyhilyard.equipmentcompare.mixin;

import java.util.Iterator;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

@Mixin(KeyMapping.class)
public class KeyMappingMixin
{
	@Final
	@Shadow
	private static Map<String, KeyMapping> ALL;

	@Final
	@Shadow
	private static Map<InputConstants.Key, KeyMapping> MAP;

	@Inject(method = "resetMapping", at = @At(value  = "HEAD"), cancellable = true)
	private static void resetMapping(CallbackInfo info)
	{
		MAP.clear();
		Iterator<?> var0 = ALL.values().iterator();

		while (var0.hasNext())
		{
			KeyMapping keyMapping = (KeyMapping)var0.next();
			if (!keyMapping.equals(EquipmentCompare.showComparisonTooltip))
			{
				MAP.put(keyMapping.key, keyMapping);
			}
		}
		info.cancel();
	}
}