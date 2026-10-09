package com.anthonyhilyard.equipmentcompare.mixin;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.Screen;

@Mixin(Screen.class)
public class ScreenMixin
{
	@Inject(method = "onClose", at = @At("HEAD"))
	private void equipmentcompare$onClose(CallbackInfo info)
	{
		EquipmentCompare.comparisonsActive = false;
	}
}
