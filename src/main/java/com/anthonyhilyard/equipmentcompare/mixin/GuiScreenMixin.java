package com.anthonyhilyard.equipmentcompare.mixin;

import java.lang.reflect.Field;
import java.util.List;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;

import org.apache.commons.lang3.exception.ExceptionUtils;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;

@Mixin(GuiScreen.class)
public abstract class GuiScreenMixin extends Gui
{
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
			EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
		return result;
	}

	@Inject(method = "drawHoveringText(Ljava/util/List;IILnet/minecraft/client/gui/FontRenderer;)V", at = @At(value  = "HEAD"), cancellable = true, remap = false)
	public void renderTooltip(List<String> textLines, int x, int y, FontRenderer font, CallbackInfo info)
	{
		GuiScreen self = (GuiScreen)(Object)this;

		ItemStack itemStack = getTooltipStack();

		EquipmentCompare.renderingSuccessful = false;

		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(x, y, itemStack, self.mc, font, self))
		{
			EquipmentCompare.renderingSuccessful = true;
			info.cancel();
		}
		// Just in case, try it again with the hovered item.
		else if (self instanceof GuiContainer)
		{
			GuiContainer containerSelf = (GuiContainer)self;
			if (ComparisonTooltips.render(x, y, containerSelf.hoveredSlot, self.mc, font, containerSelf))
			{
				EquipmentCompare.renderingSuccessful = true;
				info.cancel();
			}
		}
	}
}