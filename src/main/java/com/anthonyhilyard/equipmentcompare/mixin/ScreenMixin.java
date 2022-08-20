package com.anthonyhilyard.equipmentcompare.mixin;

import java.lang.reflect.Field;
import java.util.List;

import javax.annotation.Nullable;

import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.anthonyhilyard.iceberg.Loader;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

@Mixin(Screen.class)
public abstract class ScreenMixin extends FocusableGui
{
	@Nullable
	@Shadow
	protected Minecraft minecraft;

	@Shadow
	protected FontRenderer font;

	private boolean tooltipsDisplayed;

	@Unique
	private static ItemStack getTooltipStack()
	{
		ItemStack result = ItemStack.EMPTY;
		try
		{
			Field cachedTooltipStackField = GuiUtils.class.getDeclaredField("cachedTooltipStack");
			cachedTooltipStackField.setAccessible(true);
			result = (ItemStack) cachedTooltipStackField.get(null);
			cachedTooltipStackField.setAccessible(false);
		}
		catch (Exception e)
		{
			Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
		return result;
	}

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/item/ItemStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int x, int y, CallbackInfo info)
	{
		Screen self = (Screen)(Object)this;
		tooltipsDisplayed = false;

		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(matrixStack, x, y, itemStack, minecraft, font, self))
		{
			info.cancel();
			tooltipsDisplayed = true;
		}
		// Just in case, try it again with the hovered item.
		else if (self instanceof ContainerScreen)
		{
			ContainerScreen<?> containerSelf = (ContainerScreen<?>)self;
			if (ComparisonTooltips.render(matrixStack, x, y, containerSelf.hoveredSlot, minecraft, font, containerSelf))
			{
				info.cancel();
				tooltipsDisplayed = true;
			}
		}
	}

	@Inject(method = "renderWrappedToolTip", remap = false, at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltipInternal(MatrixStack matrixStack, List<? extends net.minecraft.util.text.ITextProperties> tooltips, int mouseX, int mouseY, FontRenderer font, CallbackInfo info)
	{
		ItemStack tooltipStack = getTooltipStack();
		if (!tooltipsDisplayed && tooltipStack != ItemStack.EMPTY)
		{
			// If the comparison tooltips were displayed, cancel so the default functionality is not run.
			if (ComparisonTooltips.render(matrixStack, mouseX, mouseY, tooltipStack, minecraft, font, (Screen)(Object)this))
			{
				info.cancel();
			}
		}
	}
}