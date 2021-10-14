package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
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
import net.minecraft.world.item.TieredItem;
import com.mojang.math.Matrix4f;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.fmlclient.gui.GuiUtils;
import net.minecraftforge.client.RenderProperties;
import net.minecraftforge.fml.ModList;

public class ComparisonTooltips
{
	@SuppressWarnings("removal")
	private static void drawTooltip(PoseStack poseStack, ItemStack itemStack, Rect2i rect, List<Component> tooltipLines, Font font, Screen screen, int maxWidth, boolean showBadge)
	{
		int bgColor = (int)EquipmentCompareConfig.INSTANCE.badgeBackgroundColor.get().longValue();
		int borderStartColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderStartColor.get().longValue();
		int borderEndColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderEndColor.get().longValue();
		
		Style textColor = Style.EMPTY.withColor(TextColor.fromRgb((int)EquipmentCompareConfig.INSTANCE.badgeTextColor.get().longValue()));
		MutableComponent equippedBadge = new TextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get()).withStyle(textColor);

		GuiUtils.preItemToolTip(itemStack);

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

			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 15 + badgeOffset, rect.getX() + rect.getWidth() - 1, rect.getY() - 14 + badgeOffset, bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX(),						 rect.getY() - 14 + badgeOffset, rect.getX() + 1, 				  rect.getY() - 2 + badgeOffset,    bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() - 1, rect.getY() - 14 + badgeOffset, rect.getX() + rect.getWidth(),	  rect.getY() - 2 + badgeOffset,    bgColor, bgColor);

			// Draw the "equipped" badge.
			// If legendary tooltips is installed, AND this item needs a custom border display the badge lower and without a border.
			if (ModList.get().isLoaded("legendarytooltips"))
			{
				try
				{
					if ((boolean) Class.forName("com.anthonyhilyard.equipmentcompare.LegendaryTooltipsHandler").getMethod("shouldDisplayEquippedBorder", ItemStack.class).invoke(null, itemStack))
					{
						badgeOffset = 6;
						bgColor = GuiUtils.DEFAULT_BACKGROUND_COLOR;
						GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1, rect.getY() - 14 + badgeOffset, rect.getX() + rect.getWidth() - 1, rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
					}
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(e);
				}
			}
			else
			{
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 2 + badgeOffset,  rect.getX() + rect.getWidth() - 1, rect.getY() - 1 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 14 + badgeOffset, rect.getX() + rect.getWidth() - 1, rect.getY() - 2 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 13 + badgeOffset, rect.getX() + 2, 				  rect.getY() - 3 + badgeOffset,    borderStartColor, borderEndColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() - 2, rect.getY() - 13 + badgeOffset, rect.getX() + rect.getWidth() - 1, rect.getY() - 3 + badgeOffset,  borderStartColor, borderEndColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 14 + badgeOffset, rect.getX() + rect.getWidth() - 1, rect.getY() - 13 + badgeOffset, borderStartColor, borderStartColor);
				GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 3 + badgeOffset,  rect.getX() + rect.getWidth() - 1, rect.getY() - 2 + badgeOffset,  borderEndColor,   borderEndColor);
			}

			font.drawInBatch(Language.getInstance().getVisualOrder(equippedBadge), (float)rect.getX() + (rect.getWidth() - font.width(equippedBadge)) / 2, (float)rect.getY() - 12 + badgeOffset, -1, true, poseStack.last().pose(), renderType, false, 0x000000, 0xF000F0);
			renderType.endBatch();
			poseStack.popPose();
		}

		Tooltips.renderItemTooltip(itemStack, poseStack, new Tooltips.TooltipInfo(tooltipLines, font), rect, screen.width, screen.height, GuiUtils.DEFAULT_BACKGROUND_COLOR, GuiUtils.DEFAULT_BORDER_COLOR_START, GuiUtils.DEFAULT_BORDER_COLOR_END, showBadge);
	}

	public static boolean render(PoseStack poseStack, int x, int y, Slot hoveredSlot, Minecraft minecraft, Font font, Screen screen)
	{
		ItemStack itemStack = hoveredSlot != null ? hoveredSlot.getItem() : ItemStack.EMPTY;
		return render(poseStack, x, y, itemStack, minecraft, font, screen);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean render(PoseStack poseStack, int x, int y, ItemStack itemStack, Minecraft minecraft, Font font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}

		if (!itemStack.isEmpty() && !EquipmentCompareConfig.INSTANCE.blacklist.get().contains(itemStack.getItem().getRegistryName().toString()))
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlot slot = Mob.getEquipmentSlotForItem(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemBySlot(slot);
		
			boolean checkItem = true;

			// For held items, only check tools.
			if (slot == EquipmentSlot.MAINHAND)
			{
				// If they aren't both tools, don't compare them.
				if (!(itemStack.getItem() instanceof TieredItem) || !(equippedItem.getItem() instanceof TieredItem))
				{
					checkItem = false;
				}
				// If strict comparisons are enabled, only compare tools of the same type.
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
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.CuriosHandler").getMethod("getCuriosMatchingSlot", LivingEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(e);
				}
			}

			// If Baubles is installed, check for equipped baubles to compare as well.
			if (ModList.get().isLoaded("baubles"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.BaublesHandler").getMethod("getBaublesMatchingSlot", Player.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(e);
				}
			}

			// Filter blacklisted items.
			equippedItems.removeIf(stack -> EquipmentCompareConfig.INSTANCE.blacklist.get().contains(stack.getItem().getRegistryName().toString()));

			// Make sure we don't compare an item to itself (can happen with Curios slots).
			equippedItems.remove(itemStack);

			if (!equippedItems.isEmpty() && (EquipmentCompare.tooltipActive ^ EquipmentCompareConfig.INSTANCE.defaultOn.get()))
			{
				int maxWidth = ((screen.width - (equippedItems.size() * 16)) / (equippedItems.size() + 1));
				Font itemFont = RenderProperties.get(itemStack).getFont(itemStack);
				if (itemFont == null)
				{
					itemFont = font;
				}

				List<Component> itemStackTooltipLines = screen.getTooltipFromItem(itemStack);
				Rect2i itemStackRect = Tooltips.calculateRect(itemStack, poseStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont);
				if (x + itemStackRect.getWidth() + 12 > screen.width)
				{
					itemStackRect = new Rect2i(screen.width - itemStackRect.getWidth() - 24, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}
				else
				{
					itemStackRect = new Rect2i(itemStackRect.getX() - 2, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				Map<ItemStack, Rect2i> tooltipRects = new HashMap<ItemStack, Rect2i>();
				Map<ItemStack, List<Component>> tooltipLines = new HashMap<ItemStack, List<Component>>();

				Rect2i previousRect = itemStackRect;
				boolean firstRect = true;

				// Set up tooltip rects.
				for (ItemStack thisItem : equippedItems)
				{
					if (RenderProperties.get(thisItem).getFont(thisItem) != null)
					{
						itemFont = RenderProperties.get(thisItem).getFont(thisItem);
					}

					List<Component> equippedTooltipLines = screen.getTooltipFromItem(thisItem);
					Rect2i equippedRect = Tooltips.calculateRect(itemStack, poseStack, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont);
					MutableComponent equippedBadge = new TextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());
					
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

				// Now draw them all.
				for (ItemStack thisItem : equippedItems)
				{
					drawTooltip(poseStack, thisItem, tooltipRects.get(thisItem), tooltipLines.get(thisItem), font, screen, maxWidth, true);
				}
				drawTooltip(poseStack, itemStack, itemStackRect, itemStackTooltipLines, font, screen, maxWidth, false);

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
