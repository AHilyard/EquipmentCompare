package com.anthonyhilyard.equipmentcompare.mixin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.mojang.blaze3d.vertex.PoseStack;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

@Mixin(Screen.class)
public abstract class ScreenMixin extends AbstractContainerEventHandler
{
	@Shadow
	protected Minecraft minecraft;

	@Shadow
	protected Font font;

	public void setTooltipStack(ItemStack stack)
	{
		try
		{
			Field tooltipStackField = Screen.class.getDeclaredField("tooltipStack");
			tooltipStackField.setAccessible(true);
			tooltipStackField.set(this, stack);
		}
		catch (Exception e)
		{
			EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
	}

	public ItemStack getTooltipStack()
	{
		try
		{
			Field tooltipStackField = Screen.class.getDeclaredField("tooltipStack");
			tooltipStackField.setAccessible(true);
			return (ItemStack)tooltipStackField.get(this);
		}
		catch (Exception e)
		{
			EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
			return ItemStack.EMPTY;
		}
	}

	private static boolean renderComparisonTooltips(PoseStack poseStack, int x, int y, Slot slot, Minecraft minecraft, Font font, Screen self)
	{
		try
		{
			Method renderTooltipsMethod = Class.forName("com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips").getDeclaredMethod("render", PoseStack.class, int.class, int.class, Slot.class, Minecraft.class, Font.class, Screen.class);
			return (boolean)renderTooltipsMethod.invoke(null, poseStack, x, y, slot, minecraft, font, self);
		}
		catch (Exception e) { }
		
		return false;
	}

	private static boolean renderComparisonTooltips(PoseStack poseStack, int x, int y, ItemStack itemStack, Minecraft minecraft, Font font, Screen self)
	{
		try
		{
			Method renderTooltipsMethod = Class.forName("com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips").getDeclaredMethod("render", PoseStack.class, int.class, int.class, ItemStack.class, Minecraft.class, Font.class, Screen.class);
			return (boolean)renderTooltipsMethod.invoke(null, poseStack, x, y, itemStack, minecraft, font, self);
		}
		catch (Exception e) { }
		
		return false;
	}

	private boolean tooltipsDisplayed;

	@Inject(method = "renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/item/ItemStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(PoseStack poseStack, ItemStack itemStack, int x, int y, CallbackInfo info)
	{
		Screen self = (Screen)(Object)this;
		tooltipsDisplayed = false;

		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (renderComparisonTooltips(poseStack, x, y, itemStack, minecraft, font, self))
		{
			info.cancel();
			tooltipsDisplayed = true;
		}
		// Just in case, try it again with the hovered item.
		else if (self instanceof AbstractContainerScreen)
		{
			AbstractContainerScreen<?> containerSelf = (AbstractContainerScreen<?>)self;
			if (renderComparisonTooltips(poseStack, x, y, containerSelf.hoveredSlot, minecraft, font, containerSelf))
			{
				info.cancel();
				tooltipsDisplayed = true;
			}
		}
	}

	@Inject(method = "renderTooltipInternal", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltipInternal(PoseStack poseStack, List<ClientTooltipComponent> components, int x, int y, ClientTooltipPositioner positioner, CallbackInfo info)
	{
		ItemStack tooltipStack = getTooltipStack();
		if (!tooltipsDisplayed && tooltipStack != ItemStack.EMPTY)
		{
			// If the comparison tooltips were displayed, cancel so the default functionality is not run.
			if (renderComparisonTooltips(poseStack, x, y, tooltipStack, minecraft, font, (Screen)(Object)this))
			{
				setTooltipStack(ItemStack.EMPTY);
				info.cancel();
			}
		}
	}

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