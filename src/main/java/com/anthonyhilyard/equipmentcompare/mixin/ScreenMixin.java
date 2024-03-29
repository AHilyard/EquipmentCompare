package com.anthonyhilyard.equipmentcompare.mixin;

import java.util.List;
import javax.annotation.Nullable;

import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.mojang.blaze3d.vertex.PoseStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.item.ItemStack;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler
{
	@Nullable
	@Shadow
	protected Minecraft minecraft;

	@Shadow
	protected Font font;

	@Shadow(remap = false)
	private ItemStack tooltipStack;

	private boolean tooltipsDisplayed;

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo info)
	{
		Screen self = (Screen)(Object)this;
		tooltipsDisplayed = false;

		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(poseStack, x, y, itemStack, minecraft, font, self))
		{
			info.cancel();
			tooltipsDisplayed = true;
		}
		// Just in case, try it again with the hovered item.
		else if (self instanceof AbstractContainerScreen)
		{
			AbstractContainerScreen<?> containerSelf = (AbstractContainerScreen<?>)self;
			if (ComparisonTooltips.render(poseStack, x, y, containerSelf.hoveredSlot, minecraft, font, containerSelf))
			{
				info.cancel();
				tooltipsDisplayed = true;
			}
		}
	}

	@Inject(method = "renderTooltipInternal", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltipInternal(PoseStack poseStack, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info)
	{
		if (!tooltipsDisplayed && tooltipStack != ItemStack.EMPTY)
		{
			// If the comparison tooltips were displayed, cancel so the default functionality is not run.
			if (ComparisonTooltips.render(poseStack, x, y, tooltipStack, minecraft, font, (Screen)(Object)this))
			{
				info.cancel();
			}
		}
	}
}