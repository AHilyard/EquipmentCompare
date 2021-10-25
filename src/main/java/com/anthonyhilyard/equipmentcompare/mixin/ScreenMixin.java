package com.anthonyhilyard.equipmentcompare.mixin;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler
{
	@Shadow
	protected Minecraft minecraft;

	@Shadow
	protected Font font;

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo info)
	{
		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(poseStack, x, y, itemStack, minecraft, font, (Screen)(Object)this))
		{
			info.cancel();
		}
	}

	@Inject(method = "keyPressed(III)Z", at = @At(value = "HEAD"), cancellable = true)
	public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> info)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(i, j))
		{
			EquipmentCompare.tooltipActive = true;
			info.setReturnValue(true);
			info.cancel();
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(i, j))
		{
			EquipmentCompare.tooltipActive = false;
			return true;
		}
		return super.keyReleased(i, j, k);
	}
}