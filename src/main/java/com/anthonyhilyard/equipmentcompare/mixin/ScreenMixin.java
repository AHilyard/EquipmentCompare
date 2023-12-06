package com.anthonyhilyard.equipmentcompare.mixin;

import java.util.List;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;


@Mixin(Screen.class)
public class ScreenMixin extends AbstractContainerEventHandler
{
	@Shadow
	public List<? extends GuiEventListener> children() { throw new UnsupportedOperationException("Unimplemented method 'children'"); }

	@Inject(method = "onClose", at = @At(value = "HEAD"))
	public void screenClosed(CallbackInfo info)
	{
		EquipmentCompare.comparisonsActive = false;
	}

	@Inject(method = "keyPressed(III)Z", at = @At(value = "HEAD"), cancellable = true)
	public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> info)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(i, j))
		{
			EquipmentCompare.comparisonsActive = true;
			info.setReturnValue(true);
			info.cancel();
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(i, j))
		{
			EquipmentCompare.comparisonsActive = false;
			return true;
		}
		return super.keyReleased(i, j, k);
	}
}
