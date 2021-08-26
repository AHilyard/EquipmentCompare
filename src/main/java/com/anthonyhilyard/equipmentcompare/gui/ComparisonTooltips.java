package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TieredItem;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;

public class ComparisonTooltips
{
	private static Rectangle2d calculateTooltipRect(final ItemStack stack, MatrixStack mStack, List<? extends ITextProperties> textLines, int mouseX, int mouseY,
												int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
	{
		Rectangle2d rect = new Rectangle2d(0, 0, 0, 0);
		if (textLines.isEmpty())
		{
			return rect;
		}

		// Generate a tooltip event even though we aren't rendering anything in case the event handlers are modifying the input values.
		RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mStack, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return rect;
		}

		mouseX = event.getX();
		mouseY = event.getY();
		screenWidth = event.getScreenWidth();
		screenHeight = event.getScreenHeight();
		maxTextWidth = event.getMaxWidth();
		font = event.getFontRenderer();

		int tooltipTextWidth = 0;

		for (ITextProperties textLine : textLines)
		{
			int textLineWidth = font.getStringPropertyWidth(textLine);
			if (textLineWidth > tooltipTextWidth)
			{
				tooltipTextWidth = textLineWidth;
			}
		}

		boolean needsWrap = false;

		int titleLinesCount = 1;
		int tooltipX = mouseX + 14;
		if (tooltipX + tooltipTextWidth + 4 > screenWidth)
		{
			tooltipX = mouseX - 16 - tooltipTextWidth;
			if (tooltipX < 4) // if the tooltip doesn't fit on the screen
			{
				if (mouseX > screenWidth / 2)
				{
					tooltipTextWidth = mouseX - 14 - 8;
				}
				else
				{
					tooltipTextWidth = screenWidth - 16 - mouseX;
				}
				needsWrap = true;
			}
		}

		if (maxTextWidth > 0 && tooltipTextWidth > maxTextWidth)
		{
			tooltipTextWidth = maxTextWidth;
			needsWrap = true;
		}

		if (needsWrap)
		{
			int wrappedTooltipWidth = 0;
			List<ITextProperties> wrappedTextLines = new ArrayList<>();
			for (int i = 0; i < textLines.size(); i++)
			{
				ITextProperties textLine = textLines.get(i);
				List<ITextProperties> wrappedLine = font.getCharacterManager().func_238362_b_(textLine, tooltipTextWidth, Style.EMPTY);
				if (i == 0)
				{
					titleLinesCount = wrappedLine.size();
				}

				for (ITextProperties line : wrappedLine)
				{
					int lineWidth = font.getStringPropertyWidth(line);
					if (lineWidth > wrappedTooltipWidth)
					{
						wrappedTooltipWidth = lineWidth;
					}
					wrappedTextLines.add(line);
				}
			}
			tooltipTextWidth = wrappedTooltipWidth;
			textLines = wrappedTextLines;

			if (mouseX > screenWidth / 2)
			{
				tooltipX = mouseX - 16 - tooltipTextWidth;
			}
			else
			{
				tooltipX = mouseX + 14;
			}
		}

		int tooltipY = mouseY - 14;
		int tooltipHeight = 8;

		if (textLines.size() > 1)
		{
			tooltipHeight += (textLines.size() - 1) * 10;
			if (textLines.size() > titleLinesCount)
			{
				tooltipHeight += 2; // gap between title lines and next lines
			}
		}

		if (tooltipY < 4)
		{
			tooltipY = 4;
		}
		else if (tooltipY + tooltipHeight + 4 > screenHeight)
		{
			tooltipY = screenHeight - tooltipHeight - 4;
		}

		rect = new Rectangle2d(tooltipX - 4, tooltipY - 4, tooltipTextWidth + 8, tooltipHeight + 8);
		return rect;
	}

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

			matrixStack.push();
			matrixStack.translate(0, 0, 401);
			IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

			Matrix4f matrix = matrixStack.getLast().getMatrix();

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

			font.func_238416_a_(LanguageMap.getInstance().func_241870_a(equippedBadge), (float)rect.getX() + (rect.getWidth() - font.getStringPropertyWidth(equippedBadge)) / 2, (float)rect.getY() - 12, -1, true, matrixStack.getLast().getMatrix(), renderType, false, 0x000000, 0xF000F0);
			renderType.finish();
			matrixStack.pop();
		}

		GuiUtils.drawHoveringText(matrixStack, tooltipLines, rect.getX() - 8, rect.getY() + 18, screen.width, screen.height, maxWidth, font);
		GuiUtils.postItemToolTip();
	}

	@SuppressWarnings("unchecked")
	public static boolean render(MatrixStack matrixStack, int x, int y, Slot hoveredSlot, Minecraft minecraft, FontRenderer font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}

		ItemStack itemStack = hoveredSlot != null ? hoveredSlot.getStack() : ItemStack.EMPTY;
		if (minecraft.player.inventory.getItemStack().isEmpty() && !itemStack.isEmpty())
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlotType slot = MobEntity.getSlotForItemStack(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemStackFromSlot(slot);
		
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

			if (!equippedItems.isEmpty() && (EquipmentCompare.tooltipActive ^ EquipmentCompareConfig.INSTANCE.defaultOn.get()))
			{
				int maxWidth = (screen.width / (equippedItems.size() + 1)) - ((equippedItems.size() + 1) * 16);
				FontRenderer itemFont = itemStack.getItem().getFontRenderer(itemStack);
				if (itemFont == null)
				{
					itemFont = font;
				}

				List<ITextComponent> itemStackTooltipLines = screen.getTooltipFromItem(itemStack);
				Rectangle2d itemStackRect = calculateTooltipRect(itemStack, matrixStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont);
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
					Rectangle2d equippedRect = calculateTooltipRect(itemStack, matrixStack, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont);
					StringTextComponent equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());
					
					// Fix equippedRect x coordinate.
					int tooltipWidth = equippedRect.getWidth();
					equippedRect = new Rectangle2d(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), itemFont.getStringPropertyWidth(equippedBadge) + 8), equippedRect.getHeight());
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
