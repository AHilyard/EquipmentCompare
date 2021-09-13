package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.anthonyhilyard.iceberg.util.Tooltips;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.ModList;

public class ComparisonTooltips
{
	private static void drawTooltip(MatrixStack matrixStack, ItemStack itemStack, Rectangle2d rect, List<ITextComponent> tooltipLines, FontRenderer font, Screen screen, int maxWidth, boolean showBadge)
	{
		int bgColor = (int)EquipmentCompareConfig.INSTANCE.badgeBackgroundColor.get().longValue();
		int borderStartColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderStartColor.get().longValue();
		int borderEndColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderEndColor.get().longValue();
		StringTextComponent equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());

		GuiUtils.preItemToolTip(itemStack);

		if (showBadge)
		{
			if (rect.getY() + rect.getHeight() + 4 > screen.height)
			{
				rect = new Rectangle2d(rect.getX(), screen.height - rect.getHeight() - 4, rect.getWidth(), rect.getHeight());
			}

			matrixStack.pushPose();
			matrixStack.translate(0, 0, 401);
			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());

			Matrix4f matrix = matrixStack.last().pose();

			// Draw the "equipped" badge.
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 15, rect.getX() + rect.getWidth() - 1, rect.getY() - 14, bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 2,  rect.getX() + rect.getWidth() - 1, rect.getY() - 1,  bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 14, rect.getX() + rect.getWidth() - 1, rect.getY() - 2,  bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX(),						 rect.getY() - 14, rect.getX() + 1, 				  rect.getY() - 2,  bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() - 1, rect.getY() - 14, rect.getX() + rect.getWidth(),	  rect.getY() - 2,  bgColor, bgColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 13, rect.getX() + 2, 				  rect.getY() - 3,  borderStartColor, borderEndColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + rect.getWidth() - 2, rect.getY() - 13, rect.getX() + rect.getWidth() - 1, rect.getY() - 3,  borderStartColor, borderEndColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 14, rect.getX() + rect.getWidth() - 1, rect.getY() - 13, borderStartColor, borderStartColor);
			GuiUtils.drawGradientRect(matrix, -1, rect.getX() + 1,					 rect.getY() - 3,  rect.getX() + rect.getWidth() - 1, rect.getY() - 2,  borderEndColor, borderEndColor);

			font.drawInBatch(LanguageMap.getInstance().getVisualOrder(equippedBadge), (float)rect.getX() + (rect.getWidth() - font.width(equippedBadge)) / 2, (float)rect.getY() - 12, -1, true, matrixStack.last().pose(), renderType, false, 0x000000, 0xF000F0);
			renderType.endBatch();
			matrixStack.popPose();
		}

		GuiUtils.drawHoveringText(matrixStack, tooltipLines, rect.getX() - 8, rect.getY() + 18, screen.width, screen.height, maxWidth, font);
		GuiUtils.postItemToolTip();
	}

	public static boolean render(MatrixStack matrixStack, int x, int y, Slot hoveredSlot, Minecraft minecraft, FontRenderer font, Screen screen)
	{
		ItemStack itemStack = hoveredSlot != null ? hoveredSlot.getItem() : ItemStack.EMPTY;
		return render(matrixStack, x, y, itemStack, minecraft, font, screen);
	}
	
	@SuppressWarnings("unchecked")
	public static boolean render(MatrixStack matrixStack, int x, int y, ItemStack itemStack, Minecraft minecraft, FontRenderer font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}

		if (minecraft.player.inventory.getCarried().isEmpty() && !itemStack.isEmpty() && !EquipmentCompareConfig.INSTANCE.blacklist.get().contains(itemStack.getItem().getRegistryName().toString()))
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemBySlot(slot);
		
			boolean checkItem = true;

			// For held items, only check tools.
			if (slot == EquipmentSlotType.MAINHAND)
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
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.BaublesHandler").getMethod("getBaublesMatchingSlot", PlayerEntity.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(e);
				}
			}

			// Filter blacklisted items.
			equippedItems.removeIf(stack -> EquipmentCompareConfig.INSTANCE.blacklist.get().contains(stack.getItem().getRegistryName().toString()));

			if (!equippedItems.isEmpty() && (EquipmentCompare.tooltipActive ^ EquipmentCompareConfig.INSTANCE.defaultOn.get()))
			{
				int maxWidth = (screen.width / (equippedItems.size() + 1)) - ((equippedItems.size() + 1) * 16);
				FontRenderer itemFont = itemStack.getItem().getFontRenderer(itemStack);
				if (itemFont == null)
				{
					itemFont = font;
				}

				List<ITextComponent> itemStackTooltipLines = screen.getTooltipFromItem(itemStack);
				Rectangle2d itemStackRect = Tooltips.calculateRect(itemStack, matrixStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont);
				if (x + itemStackRect.getWidth() + 12 > screen.width)
				{
					itemStackRect = new Rectangle2d(screen.width - itemStackRect.getWidth() - 24, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}
				else
				{
					itemStackRect = new Rectangle2d(itemStackRect.getX() - 2, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				Map<ItemStack, Rectangle2d> tooltipRects = new HashMap<ItemStack, Rectangle2d>();
				Map<ItemStack, List<ITextComponent>> tooltipLines = new HashMap<ItemStack, List<ITextComponent>>();

				Rectangle2d previousRect = itemStackRect;

				// Set up tooltip rects.
				for (ItemStack thisItem : equippedItems)
				{
					if (thisItem.getItem().getFontRenderer(thisItem) != null)
					{
						itemFont = thisItem.getItem().getFontRenderer(thisItem);
					}

					List<ITextComponent> equippedTooltipLines = screen.getTooltipFromItem(thisItem);
					Rectangle2d equippedRect = Tooltips.calculateRect(itemStack, matrixStack, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont);
					StringTextComponent equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());
					
					// Fix equippedRect x coordinate.
					int tooltipWidth = equippedRect.getWidth();
					equippedRect = new Rectangle2d(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), itemFont.width(equippedBadge) + 8), equippedRect.getHeight());
					equippedRect = new Rectangle2d(previousRect.getX() - equippedRect.getWidth() - 16 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());

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
						Rectangle2d equippedRect = tooltipRects.get(thisItem);
						tooltipRects.replace(thisItem, new Rectangle2d(equippedRect.getX() + xOffset, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight()));
					}

					// Move the hovered item rect.
					itemStackRect = new Rectangle2d(itemStackRect.getX() + xOffset, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				// Now draw them all.
				for (ItemStack thisItem : equippedItems)
				{
					drawTooltip(matrixStack, thisItem, tooltipRects.get(thisItem), tooltipLines.get(thisItem), font, screen, maxWidth, true);
				}
				drawTooltip(matrixStack, itemStack, itemStackRect, itemStackTooltipLines, font, screen, maxWidth, false);

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
