package com.anthonyhilyard.equipmentcompare.mixin;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.input.KeyEvent;

/**
 * Anvil uses the same KeyEvent API as Screen in 26.3.
 */
@Mixin(AnvilScreen.class)
public class AnvilScreenMixin
{
	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void equipmentCompare$keyPressed(KeyEvent event, CallbackInfoReturnable<Boolean> info)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(event))
		{
			EquipmentCompare.comparisonsActive = true;
			info.setReturnValue(true);
		}
	}

	@Inject(method = "keyReleased", at = @At("HEAD"), cancellable = true)
	private void equipmentCompare$keyReleased(KeyEvent event, CallbackInfoReturnable<Boolean> info)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(event))
		{
			EquipmentCompare.comparisonsActive = false;
			info.setReturnValue(true);
		}
	}
}
