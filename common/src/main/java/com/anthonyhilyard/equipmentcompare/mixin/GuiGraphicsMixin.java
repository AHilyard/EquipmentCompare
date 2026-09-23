package com.anthonyhilyard.equipmentcompare.mixin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.iceberg.services.Services;

import net.minecraft.resources.Identifier;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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
public class GuiGraphicsMixin
{
	@Unique
	private static Field tooltipStackField = null;

	@Unique
	public void setTooltipStack(ItemStack stack)
	{
		if (tooltipStackField == null)
		{
			try
			{
				switch (Services.getPlatformHelper().getPlatformName())
				{
					case "Fabric":
						tooltipStackField = GuiGraphics.class.getDeclaredField("icebergTooltipStack");
					default:
						tooltipStackField = GuiGraphics.class.getDeclaredField("tooltipStack");
						break;
				}
				
				tooltipStackField.setAccessible(true);
			}
			catch (Exception e)
			{
				EquipmentCompare.LOGGER.debug(ExceptionUtils.getStackTrace(e));
			}
		}
		
		try
		{
			tooltipStackField.set(this, stack);
		}
		catch (Exception e) {}
	}

	@Unique
	public ItemStack getTooltipStack()
	{
		if (tooltipStackField == null)
		{
			try
			{
				switch (Services.getPlatformHelper().getPlatformName())
				{
					case "Fabric":
						tooltipStackField = GuiGraphics.class.getDeclaredField("icebergTooltipStack");
					default:
						tooltipStackField = GuiGraphics.class.getDeclaredField("tooltipStack");
						break;
				}
				
				tooltipStackField.setAccessible(true);
			}
			catch (Exception e)
			{
				EquipmentCompare.LOGGER.debug(ExceptionUtils.getStackTrace(e));
			}
		}

		try
		{
			return (ItemStack)tooltipStackField.get(this);
		}
		catch (Exception e) {}

		return ItemStack.EMPTY;
	}

	private static boolean renderComparisonTooltips(GuiGraphics graphics, ClientTooltipPositioner positioner, int x, int y, ItemStack itemStack, Minecraft minecraft, Font font, Screen screen)
	{
		try
		{
			Method renderTooltipsMethod = Class.forName("com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips").getDeclaredMethod("render", GuiGraphics.class, ClientTooltipPositioner.class, int.class, int.class, ItemStack.class, Minecraft.class, Font.class, Screen.class);
			return (boolean)renderTooltipsMethod.invoke(null, graphics, positioner, x, y, itemStack, minecraft, font, screen);
		}
		catch (Exception e) { }

		return false;
	}

	private boolean tooltipsDisplayed;

	@Inject(method = "renderTooltip", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltip(Font font, List<ClientTooltipComponent> list, int x, int y, ClientTooltipPositioner clientTooltipPositioner, @Nullable Identifier resource, CallbackInfo info)
	{
		GuiGraphics self = (GuiGraphics)(Object)this;
		Minecraft minecraft = Minecraft.getInstance();
		Screen currentScreen = minecraft.screen;
		tooltipsDisplayed = false;
		ItemStack itemStack = getTooltipStack();

		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (renderComparisonTooltips(self, DefaultTooltipPositioner.INSTANCE, x, y, itemStack, minecraft, font, currentScreen))
		{
			info.cancel();
			tooltipsDisplayed = true;
		}
		// Just in case, try it again with the hovered item.
		else if (currentScreen instanceof AbstractContainerScreen<?> containerScreen)
		{
			ItemStack hoveredStack = (containerScreen.hoveredSlot != null && containerScreen.hoveredSlot.hasItem()) ? containerScreen.hoveredSlot.getItem() : ItemStack.EMPTY;
			if (renderComparisonTooltips(self, DefaultTooltipPositioner.INSTANCE, x, y, hoveredStack, minecraft, font, containerScreen))
			{
				info.cancel();
				tooltipsDisplayed = true;
			}
		}
	}

	@Inject(method = "setTooltipForNextFrameInternal", at = @At(value  = "HEAD"), cancellable = true)
	public void renderTooltipInternal(Font font, List<ClientTooltipComponent> list, int x, int y, ClientTooltipPositioner positioner, @Nullable Identifier resource, boolean bl, CallbackInfo info)
	{
		GuiGraphics self = (GuiGraphics)(Object)this;
		Minecraft minecraft = Minecraft.getInstance();
		Screen currentScreen = minecraft.screen;
		ItemStack tooltipStack = getTooltipStack();

		if (!tooltipsDisplayed && tooltipStack != ItemStack.EMPTY)
		{
			// If the comparison tooltips were displayed, cancel so the default functionality is not run.
			if (renderComparisonTooltips(self, positioner, x, y, tooltipStack, minecraft, font, currentScreen))
			{
				setTooltipStack(ItemStack.EMPTY);
				info.cancel();
			}
		}
	}
}
