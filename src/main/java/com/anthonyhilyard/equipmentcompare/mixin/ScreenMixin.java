package com.anthonyhilyard.equipmentcompare.mixin;

import javax.annotation.Nullable;

import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;

@Mixin(Screen.class)
public abstract class ScreenMixin extends FocusableGui
{
	@Nullable
	@Shadow
	protected Minecraft minecraft;

	@Shadow
	protected FontRenderer font;

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int x, int y, CallbackInfo info)
	{
		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(matrixStack, x, y, itemStack, minecraft, font, (Screen)(Object)this))
		{
			info.cancel();
		}
	}
}