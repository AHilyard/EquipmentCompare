package com.anthonyhilyard.equipmentcompare.mixin;

import java.util.List;
import javax.annotation.Nullable;

import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.world.item.ItemStack;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin
{
	@Nullable
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow(remap = false)
	private ItemStack tooltipStack = ItemStack.EMPTY;

	private boolean tooltipsDisplayed;

	@Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(Font font, ItemStack itemStack, int x, int y, CallbackInfo info)
	{
		GuiGraphics self = (GuiGraphics)(Object)this;
		Screen currentScreen = minecraft.screen;
		tooltipsDisplayed = false;

		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(self, DefaultTooltipPositioner.INSTANCE, x, y, itemStack, minecraft, font, currentScreen))
		{
			info.cancel();
			tooltipsDisplayed = true;
		}
		// Just in case, try it again with the hovered item.
		else if (currentScreen instanceof AbstractContainerScreen<?> containerScreen)
		{
			ItemStack hoveredStack = (containerScreen.hoveredSlot != null && containerScreen.hoveredSlot.hasItem()) ? containerScreen.hoveredSlot.getItem() : ItemStack.EMPTY;
			if (ComparisonTooltips.render(self, DefaultTooltipPositioner.INSTANCE, x, y, hoveredStack, minecraft, font, containerScreen))
			{
				info.cancel();
				tooltipsDisplayed = true;
			}
		}
	}

	@Inject(method = "renderTooltipInternal", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltipInternal(Font font, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info)
	{
		GuiGraphics self = (GuiGraphics)(Object)this;
		Screen currentScreen = minecraft.screen;

		if (!tooltipsDisplayed && tooltipStack != ItemStack.EMPTY)
		{
			// If the comparison tooltips were displayed, cancel so the default functionality is not run.
			if (ComparisonTooltips.render(self, positioner, x, y, tooltipStack, minecraft, font, currentScreen))
			{
				info.cancel();
			}
		}
	}
}