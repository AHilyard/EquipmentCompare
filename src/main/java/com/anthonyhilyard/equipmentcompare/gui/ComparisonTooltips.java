package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.anthonyhilyard.equipmentcompare.Loader;
import com.anthonyhilyard.iceberg.events.RenderTooltipExtEvent;
import com.anthonyhilyard.iceberg.util.GuiHelper;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.Rect2i;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joml.Matrix4f;

import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.extensions.common.IClientItemExtensions.FontContext;
import net.minecraftforge.client.gui.ScreenUtils;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class ComparisonTooltips
{
	@SuppressWarnings("null")
	private static void drawTooltip(PoseStack poseStack, ItemStack itemStack, Rect2i rect, List<ClientTooltipComponent> tooltipLines, Font font, Screen screen, int maxWidth, boolean showBadge, boolean centeredTitle, int index)
	{
		int bgColor = (int)EquipmentCompareConfig.INSTANCE.badgeBackgroundColor.get().longValue();
		int borderStartColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderStartColor.get().longValue();
		int borderEndColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderEndColor.get().longValue();
		
		Style textColor = Style.EMPTY.withColor(TextColor.fromRgb((int)EquipmentCompareConfig.INSTANCE.badgeTextColor.get().longValue()));
		
		MutableComponent equippedBadge;
		if (EquipmentCompareConfig.INSTANCE.overrideBadgeText.get())
		{
			equippedBadge = Component.literal(EquipmentCompareConfig.INSTANCE.badgeText.get()).withStyle(textColor);
		}
		else
		{
			equippedBadge = Component.translatable("equipmentcompare.general.badgeText").withStyle(textColor);
		}
		
		boolean constrainToRect = false;

		if (showBadge)
		{
			if (rect.getY() + rect.getHeight() + 4 > screen.height)
			{
				rect = new Rect2i(rect.getX(), screen.height - rect.getHeight() - 4, rect.getWidth(), rect.getHeight());
			}

			poseStack.pushPose();
			poseStack.translate(0, 0, 401);
			BufferSource renderType = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

			Matrix4f matrix = poseStack.last().pose();

			int badgeOffset = 0;

			// Draw the "equipped" badge.
			// If legendary tooltips is installed, AND this item needs a custom border display the badge lower and without a border.
			if (ModList.get().isLoaded("legendarytooltips"))
			{
				// Fire a color event to properly update the background color if needed.
				RenderTooltipExtEvent.Color colorEvent = new RenderTooltipExtEvent.Color(itemStack, poseStack, rect.getX(), rect.getY(), font, bgColor, borderStartColor, borderEndColor, tooltipLines, showBadge, index);
				if (!MinecraftForge.EVENT_BUS.post(colorEvent))
				{
					bgColor = colorEvent.getBackgroundStart();
				}
				else
				{
					bgColor = ScreenUtils.DEFAULT_BACKGROUND_COLOR;
				}

				constrainToRect = true;
				badgeOffset = 6;

				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 17 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX(),						 rect.getY() - 16 + badgeOffset, rect.getX() + 1, 					rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 8,	rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 6 + badgeOffset,  bgColor, bgColor);
			}
			else
			{
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 17 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX(),						 rect.getY() - 16 + badgeOffset, rect.getX() + 1, 					rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 8,	rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 4 + badgeOffset,  rect.getX() + rect.getWidth() + 7, rect.getY() - 3 + badgeOffset,  bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 15 + badgeOffset, rect.getX() + 2, 					rect.getY() - 5 + badgeOffset,  borderStartColor, borderEndColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() + 6, rect.getY() - 15 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 5 + badgeOffset,  borderStartColor, borderEndColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 15 + badgeOffset, borderStartColor, borderStartColor);
				GuiHelper.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 5 + badgeOffset,  rect.getX() + rect.getWidth() + 7, rect.getY() - 4 + badgeOffset,  borderEndColor,   borderEndColor);
			}
			
			font.drawInBatch(Language.getInstance().getVisualOrder(equippedBadge), (float)rect.getX() + (rect.getWidth() - font.width(equippedBadge)) / 2 + 4, (float)rect.getY() - 14 + badgeOffset, -1, true, poseStack.last().pose(), renderType, false, 0x000000, 0xF000F0);
			renderType.endBatch();
			poseStack.popPose();
		}

		Tooltips.renderItemTooltip(itemStack, poseStack, new Tooltips.TooltipInfo(tooltipLines, font, Tooltips.calculateTitleLines(tooltipLines)), rect, screen.width, screen.height, ScreenUtils.DEFAULT_BACKGROUND_COLOR, ScreenUtils.DEFAULT_BACKGROUND_COLOR, ScreenUtils.DEFAULT_BORDER_COLOR_START, ScreenUtils.DEFAULT_BORDER_COLOR_END, showBadge, constrainToRect, centeredTitle, index);
	}

	public static boolean render(PoseStack poseStack, int x, int y, Slot hoveredSlot, Minecraft minecraft, Font font, Screen screen)
	{
		ItemStack itemStack = hoveredSlot != null ? hoveredSlot.getItem() : ItemStack.EMPTY;
		return render(poseStack, x, y, itemStack, minecraft, font, screen);
	}
	
	@SuppressWarnings({"unchecked", "null"})
	public static boolean render(PoseStack poseStack, int x, int y, ItemStack itemStack, Minecraft minecraft, Font font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}
		
		if (minecraft.player.containerMenu.getCarried().isEmpty() && !itemStack.isEmpty() && !EquipmentCompareConfig.INSTANCE.blacklist.get().contains(ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString()))
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlot slot = Mob.getEquipmentSlotForItem(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemBySlot(slot);
		
			boolean checkItem = true;

			// For held items, only check items with durability.
			if (slot == EquipmentSlot.MAINHAND)
			{
				// Ensure both items are comparable.
				// Any item with durability can be compared.
				if (!itemStack.getItem().canBeDepleted() || !equippedItem.getItem().canBeDepleted())
				{
					checkItem = false;
				}
				// If strict comparisons are enabled, only compare items of the same type.
				else if (EquipmentCompareConfig.INSTANCE.strict.get())
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

			// If Curios is installed, check for equipped curios to compare as well.
			if (ModList.get().isLoaded("curios"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.CuriosHandler").getMethod("getCuriosMatchingSlot", LivingEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// If Baubles is installed, check for equipped baubles to compare as well.
			if (ModList.get().isLoaded("baubles"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.BaublesHandler").getMethod("getBaublesMatchingSlot", Player.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// Filter blacklisted items.
			equippedItems.removeIf(stack -> EquipmentCompareConfig.INSTANCE.blacklist.get().contains(ForgeRegistries.ITEMS.getKey(stack.getItem()).toString()));

			// Make sure we don't compare an item to itself (can happen with Curios slots).
			equippedItems.remove(itemStack);

			if (!equippedItems.isEmpty() && (EquipmentCompare.tooltipActive ^ EquipmentCompareConfig.INSTANCE.defaultOn.get()))
			{
				int maxWidth = ((screen.width - (equippedItems.size() * 16)) / (equippedItems.size() + 1));
				Font itemFont = IClientItemExtensions.of(itemStack).getFont(itemStack, FontContext.TOOLTIP);
				if (itemFont == null)
				{
					itemFont = font;
				}

				boolean centeredTitle = false, enforceMinimumWidth = false;

				// If Legendary Tooltips is loaded, check if we need to center the title or enforce a minimum width.
				if (ModList.get().isLoaded("legendarytooltips"))
				{
					try
					{
						centeredTitle = (boolean)Class.forName("com.anthonyhilyard.equipmentcompare.compat.LegendaryTooltipsHandler").getMethod("getCenteredTitle").invoke(null, new Object[]{});
						enforceMinimumWidth = (boolean)Class.forName("com.anthonyhilyard.equipmentcompare.compat.LegendaryTooltipsHandler").getMethod("getEnforceMinimumWidth").invoke(null, new Object[]{});
					}
					catch (Exception e)
					{
						Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
					}
				}

				List<ClientTooltipComponent> itemStackTooltipLines = Tooltips.gatherTooltipComponents(itemStack, screen.getTooltipFromItem(itemStack), itemStack.getTooltipImage(), x, screen.width, screen.height, itemFont, font, maxWidth, 0);
				Rect2i itemStackRect = Tooltips.calculateRect(itemStack, poseStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont, enforceMinimumWidth ? 48 : 0, centeredTitle);
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
					if (IClientItemExtensions.of(itemStack).getFont(itemStack, FontContext.TOOLTIP) != null)
					{
						itemFont = IClientItemExtensions.of(itemStack).getFont(itemStack, FontContext.TOOLTIP);
					}

					List<ClientTooltipComponent> equippedTooltipLines = Tooltips.gatherTooltipComponents(thisItem, screen.getTooltipFromItem(thisItem), thisItem.getTooltipImage(), x - previousRect.getWidth() - 14, screen.width, screen.height, itemFont, font, maxWidth, tooltipIndex++);
					Rect2i equippedRect = Tooltips.calculateRect(itemStack, poseStack, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont, enforceMinimumWidth ? 48 : 0, centeredTitle);
					MutableComponent equippedBadge = Component.literal(EquipmentCompareConfig.INSTANCE.badgeText.get());
					
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
						equippedRect = new Rect2i(previousRect.getX() - equippedRect.getWidth() - 4 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
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
					drawTooltip(poseStack, thisItem, tooltipRects.get(thisItem), tooltipLines.get(thisItem), font, screen, maxWidth, true, centeredTitle, tooltipIndex++);
				}
				drawTooltip(poseStack, itemStack, itemStackRect, itemStackTooltipLines, font, screen, maxWidth, false, centeredTitle, 0);

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
