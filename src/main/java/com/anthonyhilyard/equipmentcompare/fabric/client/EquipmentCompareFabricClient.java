package com.anthonyhilyard.equipmentcompare.fabric.client;

import java.lang.reflect.Field;
import java.util.List;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;

public final class EquipmentCompareFabricClient implements ClientModInitializer
{
	/** Prevent PREEXT → ComparisonTooltips.render → nested tooltip → PREEXT recursion. */
	private static final ThreadLocal<Boolean> RENDERING = ThreadLocal.withInitial(() -> false);

	private static Field keyField;

	static
	{
		try
		{
			keyField = KeyMapping.class.getDeclaredField("key");
			keyField.setAccessible(true);
		}
		catch (Exception e)
		{
			keyField = null;
		}
	}

	private static boolean isCompareKeyDown()
	{
		KeyMapping mapping = EquipmentCompare.showComparisonTooltip;
		if (mapping == null)
		{
			return false;
		}
		try
		{
			if (keyField != null)
			{
				InputConstants.Key bound = (InputConstants.Key) keyField.get(mapping);
				if (bound != null && bound.getValue() != -1 && InputConstants.isKeyDown(bound.getValue()))
				{
					return true;
				}
			}
		}
		catch (Throwable ignored) {}
		return mapping.isDown();
	}

	private static Screen currentScreen(Minecraft mc)
	{
		try
		{
			return mc.gui.screen();
		}
		catch (Throwable ignored) {}
		try
		{
			Field f = Minecraft.class.getDeclaredField("screen");
			f.setAccessible(true);
			return (Screen) f.get(mc);
		}
		catch (Throwable ignored) {}
		return null;
	}

	@Override
	public void onInitializeClient()
	{
		EquipmentCompare.init();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null)
			{
				EquipmentCompare.comparisonsActive = false;
				return;
			}
			EquipmentCompare.comparisonsActive = isCompareKeyDown();
		});

		RenderTooltipEvents.PREEXT.register((ItemStack stack, GuiGraphicsExtractor graphics, int x, int y,
				int screenWidth, int screenHeight, Font font, List<ClientTooltipComponent> components,
				ClientTooltipPositioner positioner, boolean flag, int index, boolean indexFlag) -> {

			if (Boolean.TRUE.equals(RENDERING.get()))
			{
				return new RenderTooltipEvents.PreExtResult(InteractionResult.PASS, x, y, screenWidth, screenHeight, font);
			}
			if (stack == null || stack.isEmpty())
			{
				return new RenderTooltipEvents.PreExtResult(InteractionResult.PASS, x, y, screenWidth, screenHeight, font);
			}

			Minecraft mc = Minecraft.getInstance();
			Screen screen = currentScreen(mc);
			ClientTooltipPositioner pos = positioner != null ? positioner : DefaultTooltipPositioner.INSTANCE;

			RENDERING.set(true);
			try
			{
				boolean drawn = ComparisonTooltips.render(graphics, pos, x, y, stack, mc, font, screen);
				if (drawn)
				{
					return new RenderTooltipEvents.PreExtResult(InteractionResult.FAIL, x, y, screenWidth, screenHeight, font);
				}
			}
			catch (Throwable t)
			{
				EquipmentCompare.LOGGER.error("ComparisonTooltips.render failed", t);
			}
			finally
			{
				RENDERING.set(false);
			}
			return new RenderTooltipEvents.PreExtResult(InteractionResult.PASS, x, y, screenWidth, screenHeight, font);
		});

		EquipmentCompare.LOGGER.info("EquipmentCompare 26.3 client ready (PREEXT + recursion guard)");
	}
}
