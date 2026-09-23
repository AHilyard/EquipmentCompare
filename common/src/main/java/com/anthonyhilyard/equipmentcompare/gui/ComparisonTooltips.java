package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.config.EquipmentCompareConfig;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents;
import com.anthonyhilyard.iceberg.events.client.RenderTooltipEvents.ColorExtResult;
import com.anthonyhilyard.iceberg.services.Services;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.ItemUtil;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joml.Matrix3x2fStack;

public class ComparisonTooltips
{
	private static final int DEFAULT_BACKGROUND_COLOR = 0xF0100010;
	private static final int DEFAULT_BORDER_COLOR_START = 0x505000FF;
	private static final int DEFAULT_BORDER_COLOR_END = 0x5028007F;

	private static MutableComponent getEquippedBadge()
	{
		MutableComponent equippedBadge;
		if (EquipmentCompareConfig.getInstance().overrideBadgeText.get())
		{
			equippedBadge = Component.translatable(EquipmentCompareConfig.getInstance().badgeText.get());
		}
		else
		{
			equippedBadge = Component.translatable("equipmentcompare.general.badgeText");
		}
		return equippedBadge;
	}

	private static void drawTooltip(GuiGraphics graphics, ClientTooltipPositioner positioner, ItemStack itemStack, Rect2i rect, List<ClientTooltipComponent> tooltipLines, Font font, Screen screen, int maxWidth, boolean showBadge, boolean centeredTitle, int index)
	{
		int bgColor = EquipmentCompareConfig.getInstance().badgeBackgroundColor.get().intValue();
		int borderStartColor = EquipmentCompareConfig.getInstance().badgeBorderStartColor.get().intValue();
		int borderEndColor = EquipmentCompareConfig.getInstance().badgeBorderEndColor.get().intValue();

		Style textColor = Style.EMPTY.withColor(TextColor.fromRgb(EquipmentCompareConfig.getInstance().badgeTextColor.get().intValue()));
		MutableComponent equippedBadge = getEquippedBadge().withStyle(textColor);

		boolean constrainToRect = false;
		Matrix3x2fStack matrix = graphics.pose();

		if (showBadge)
		{
			if (rect.getY() + rect.getHeight() + 4 > screen.height)
			{
				rect = new Rect2i(rect.getX(), screen.height - rect.getHeight() - 4, rect.getWidth(), rect.getHeight());
			}

			matrix.pushMatrix();
			matrix.translate(rect.getX(), rect.getY());

			int badgeOffset = 0;

			// Draw the "equipped" badge.
			// If legendary tooltips is installed, AND this item needs a custom border display the badge lower and without a border.
			if (Services.getPlatformHelper().isModLoaded("legendarytooltips"))
			{
				bgColor = DEFAULT_BACKGROUND_COLOR;
				int bgColorEnd = DEFAULT_BACKGROUND_COLOR;
				boolean gradientBackground = false, gradientBorder = false;

				// Fire a color event to properly update the colors if needed.
				ColorExtResult colorResult = RenderTooltipEvents.COLOREXT.invoker().onColor(itemStack, graphics, rect.getX(), rect.getY(), font, bgColor, bgColorEnd, borderStartColor, borderEndColor, tooltipLines, false, index, itemStack.get(DataComponents.TOOLTIP_STYLE), gradientBackground, gradientBorder);
				if (colorResult != null)
				{
					bgColor = colorResult.backgroundStart();
					bgColorEnd = colorResult.backgroundEnd();
					borderStartColor = colorResult.borderStart();
					borderEndColor = colorResult.borderEnd();
					gradientBackground = colorResult.gradientBackground();
					gradientBorder = colorResult.gradientBorder();
				}

				constrainToRect = true;
				badgeOffset = 6;

				// If either gradientBackground or gradientBorder is false here, we need to manually render that component to fill the badge area above the tooltip as well.
				if (!gradientBackground)
				{
					// Render the background resource.
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TooltipRenderUtil.getBackgroundSprite(itemStack.get(DataComponents.TOOLTIP_STYLE)), rect.getX() - 12, rect.getY() - 12 - 17 + badgeOffset, rect.getWidth() + 24 + 8, rect.getHeight() + 24 + 17 + 8);
				}
				else
				{
					matrix.pushMatrix();
					matrix.translate(0.0f, 0.0f);
					Tooltips.renderGradientBackground(graphics, 4, -14 + badgeOffset, rect.getWidth(), rect.getHeight() + 12, bgColor, bgColorEnd);
					matrix.popMatrix();
				}

				if (!gradientBorder)
				{
					// Render the border resource.
					graphics.blitSprite(RenderPipelines.GUI_TEXTURED, TooltipRenderUtil.getFrameSprite(itemStack.get(DataComponents.TOOLTIP_STYLE)), rect.getX() - 12, rect.getY() - 12 - 17 + badgeOffset, rect.getWidth() + 24 + 8, rect.getHeight() + 24 + 17 + 8);
				}
				else
				{
					matrix.pushMatrix();
					matrix.translate(0.0f, 0.0f);
					Tooltips.renderGradientBorder(graphics, 4, -14 + badgeOffset, rect.getWidth(), rect.getHeight() + 12, borderStartColor, borderEndColor);
					matrix.popMatrix();
				}

				matrix.popMatrix();
				Tooltips.renderItemTooltip(itemStack, new Tooltips.TooltipInfo(tooltipLines, font, Tooltips.calculateTitleLines(tooltipLines)), rect, screen.width, screen.height, DEFAULT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END, graphics, positioner, true, constrainToRect, centeredTitle, index, null, true, true);
			}
			else
			{
				GuiHelper.drawGradientRect(graphics, 1,					-17 + badgeOffset, rect.getWidth() + 7, -16 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(graphics, 0,					-16 + badgeOffset, 1,					 -4 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(graphics, rect.getWidth() + 7,	-16 + badgeOffset, rect.getWidth() + 8,  -4 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(graphics, 1,					 -4 + badgeOffset,  rect.getWidth() + 7, -3 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(graphics, 1,					-16 + badgeOffset, rect.getWidth() + 7,  -4 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(graphics, 1,					-15 + badgeOffset, 2,					 -5 + badgeOffset, borderStartColor, borderEndColor);
				GuiHelper.drawGradientRect(graphics, rect.getWidth() + 6,	-15 + badgeOffset, rect.getWidth() + 7,  -5 + badgeOffset, borderStartColor, borderEndColor);
				GuiHelper.drawGradientRect(graphics, 1,					-16 + badgeOffset, rect.getWidth() + 7, -15 + badgeOffset, borderStartColor, borderStartColor);
				GuiHelper.drawGradientRect(graphics, 1,					 -5 + badgeOffset,  rect.getWidth() + 7, -4 + badgeOffset, borderEndColor,   borderEndColor);

				matrix.popMatrix();
				Tooltips.renderItemTooltip(itemStack, new Tooltips.TooltipInfo(tooltipLines, font, Tooltips.calculateTitleLines(tooltipLines)), rect, screen.width, screen.height, DEFAULT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END, graphics, positioner, true, constrainToRect, centeredTitle, index);
			}

			matrix.pushMatrix();
			matrix.translate(rect.getX(), rect.getY());
			graphics.drawCenteredString(font, equippedBadge, rect.getWidth() / 2 + 4, badgeOffset - 14, -1);

			matrix.popMatrix();
		}
		else
		{
			Tooltips.renderItemTooltip(itemStack, new Tooltips.TooltipInfo(tooltipLines, font, Tooltips.calculateTitleLines(tooltipLines)), rect, screen.width, screen.height, DEFAULT_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END, graphics, positioner, false, constrainToRect, centeredTitle, index);
		}
	}

	@SuppressWarnings("unchecked")
	public static boolean render(GuiGraphics graphics, ClientTooltipPositioner positioner, int x, int y, ItemStack itemStack, Minecraft minecraft, Font font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null || minecraft == null || minecraft.player == null || minecraft.player.containerMenu == null || minecraft.level == null || itemStack == null)
		{
			return false;
		}

		if (minecraft.player.containerMenu.getCarried().isEmpty() && !itemStack.isEmpty() && !EquipmentCompareConfig.isItemBlacklisted(itemStack, minecraft.level.registryAccess()))
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlot slot = ItemUtil.getEquipmentSlot(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemBySlot(slot);
		
			boolean checkItem = true;

			// For held items, only check items with durability.
			if (slot == EquipmentSlot.MAINHAND)
			{
				// Ensure both items are comparable.
				// Any item with durability can be compared.
				if (!itemStack.isDamageableItem() || !equippedItem.isDamageableItem())
				{
					checkItem = false;
				}
				// If strict comparisons are enabled, only compare items of the same type.
				else if (EquipmentCompareConfig.getInstance().strict.get())
				{
					if (!itemStack.getItem().getClass().equals(equippedItem.getItem().getClass()))
					{
						checkItem = false;
					}
				}
			}

			if (checkItem)
			{
				equippedItems.add(equippedItem);
				equippedItems.remove(ItemStack.EMPTY);
				equippedItems.remove(itemStack);
			}

			// If Trinkets is installed, check for equipped trinkets to compare as well.
			if (Services.getPlatformHelper().isModLoaded("trinkets"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.TrinketsHandler").getMethod("getTrinketsMatchingSlot", LivingEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// If Curios is installed, check for equipped curios to compare.
			if (Services.getPlatformHelper().isModLoaded("curios") && Services.getPlatformHelper().getPlatformName().contentEquals("neoforge"))
			{
				// TODO: Make this a service if Curios comes back to Forge.
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.neoforge.compat.CuriosHandler").getMethod("getCuriosMatchingSlot", LivingEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// Filter blacklisted items.
			equippedItems.removeIf(stack -> EquipmentCompareConfig.isItemBlacklisted(stack, minecraft.level.registryAccess()));

			// Make sure we don't compare an item to itself (can happen with Trinkets slots).
			equippedItems.remove(itemStack);

			// Now prune the list down to the maximum number of comparisons.
			if (equippedItems.size() > EquipmentCompareConfig.getInstance().maxComparisons.get())
			{
				equippedItems = equippedItems.subList(0, EquipmentCompareConfig.getInstance().maxComparisons.get().intValue());
			}

			// Now prune the list down to the maximum number of comparisons.
			if (equippedItems.size() > EquipmentCompareConfig.getInstance().maxComparisons.get())
			{
				equippedItems = equippedItems.subList(0, EquipmentCompareConfig.getInstance().maxComparisons.get().intValue());
			}

			if (!equippedItems.isEmpty() && (EquipmentCompare.comparisonsActive ^ EquipmentCompareConfig.getInstance().defaultOn.get()))
			{
				int maxWidth = ((screen.width - (equippedItems.size() * 16)) / (equippedItems.size() + 1));

				Font itemFont = Services.getFontLookup().getTooltipFont(itemStack, screen);
				if (itemFont == null)
				{
					itemFont = font;
				}

				boolean centeredTitle = false, enforceMinimumWidth = false;

				// If Legendary Tooltips is loaded, check if we need to center the title or enforce a minimum width.
				if (Services.getPlatformHelper().isModLoaded("legendarytooltips"))
				{
					try
					{
						centeredTitle = (boolean)Class.forName("com.anthonyhilyard.equipmentcompare.compat.LegendaryTooltipsHandler").getMethod("getCenteredTitle").invoke(null, new Object[]{});
						enforceMinimumWidth = (boolean)Class.forName("com.anthonyhilyard.equipmentcompare.compat.LegendaryTooltipsHandler").getMethod("getEnforceMinimumWidth").invoke(null, new Object[]{});
					}
					catch (Exception e)
					{
						EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
					}
				}

				List<ClientTooltipComponent> itemStackTooltipLines = Tooltips.gatherTooltipComponents(itemStack, Screen.getTooltipFromItem(minecraft, itemStack), itemStack.getTooltipImage(), x, screen.width, screen.height, itemFont, font, maxWidth, 0);
				Rect2i itemStackRect = Tooltips.calculateRect(itemStack, graphics, positioner, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont, enforceMinimumWidth ? 48 : 0, centeredTitle);
				if (x + itemStackRect.getWidth() + 12 > screen.width)
				{
					itemStackRect = new Rect2i(screen.width - itemStackRect.getWidth() - 24, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}
				else
				{
					itemStackRect = new Rect2i(itemStackRect.getX() - 2, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				Map<ItemStack, Rect2i> tooltipRects = new HashMap<ItemStack, Rect2i>();
				Map<ItemStack, List<ClientTooltipComponent>> tooltipLines = new HashMap<ItemStack, List<ClientTooltipComponent>>();

				Rect2i previousRect = itemStackRect;
				boolean firstRect = true;

				// Keep track of the tooltip index.
				int tooltipIndex = 1;

				// Set up tooltip rects.
				for (ItemStack thisItem : equippedItems)
				{
					if (Services.getFontLookup().getTooltipFont(thisItem, screen) != null)
					{
						itemFont = Services.getFontLookup().getTooltipFont(thisItem, screen);
					}

					List<ClientTooltipComponent> equippedTooltipLines = Tooltips.gatherTooltipComponents(thisItem, Screen.getTooltipFromItem(minecraft, thisItem), thisItem.getTooltipImage(), x - previousRect.getWidth() - 14, screen.width, screen.height, itemFont, font, maxWidth, tooltipIndex++);
					Rect2i equippedRect = Tooltips.calculateRect(itemStack, graphics, positioner, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont, enforceMinimumWidth ? 48 : 0, centeredTitle);
					MutableComponent equippedBadge = getEquippedBadge();
					
					// Fix equippedRect x coordinate.
					int tooltipWidth = equippedRect.getWidth();
					equippedRect = new Rect2i(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), itemFont.width(equippedBadge) + 8), equippedRect.getHeight());

					if (firstRect)
					{
						equippedRect = new Rect2i(previousRect.getX() - equippedRect.getWidth() - 16 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
						firstRect = false;
					}
					else
					{
						equippedRect = new Rect2i(previousRect.getX() - equippedRect.getWidth() - 12 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
					}

					tooltipRects.put(thisItem, equippedRect);
					tooltipLines.put(thisItem, equippedTooltipLines);
					previousRect = equippedRect;
				}

				// Fix rects to fit onscreen, if possible.
				// If the last rect (which is the left-most one) is off the screen, move all the rects over.
				int xOffset = -tooltipRects.get(equippedItems.get(equippedItems.size() - 1)).getX();
				if (xOffset > 0)
				{
					// Move the equipped rects.
					for (ItemStack thisItem : equippedItems)
					{
						Rect2i equippedRect = tooltipRects.get(thisItem);
						tooltipRects.replace(thisItem, new Rect2i(equippedRect.getX() + xOffset, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight()));
					}

					// Move the hovered item rect.
					itemStackRect = new Rect2i(itemStackRect.getX() + xOffset, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				tooltipIndex = 1;

				// Now draw them all.
				for (ItemStack thisItem : equippedItems)
				{
					drawTooltip(graphics, positioner, thisItem, tooltipRects.get(thisItem), tooltipLines.get(thisItem), font, screen, maxWidth, true, centeredTitle, tooltipIndex++);
				}
				drawTooltip(graphics, positioner, itemStack, itemStackRect, itemStackTooltipLines, font, screen, maxWidth, false, centeredTitle, 0);

				return true;
			}
			// Otherwise display the tooltip normally.
			else
			{
				return false;
			}
		}
		return false;
	}
}