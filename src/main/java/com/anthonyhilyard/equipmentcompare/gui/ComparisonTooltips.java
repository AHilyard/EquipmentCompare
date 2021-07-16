package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.List;

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
import net.minecraft.item.ToolItem;
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

	@SuppressWarnings("unchecked")
	public static boolean render(MatrixStack matrixStack, int x, int y, Slot hoveredSlot, Minecraft minecraft, FontRenderer font, Screen screen)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}

		int bgColor = (int)EquipmentCompareConfig.INSTANCE.badgeBackgroundColor.get().longValue();
		int borderStartColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderStartColor.get().longValue();
		int borderEndColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderEndColor.get().longValue();
		final int maxTooltipWidth = 200;

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
				if (!(itemStack.getItem() instanceof ToolItem) || !(equippedItem.getItem() instanceof ToolItem))
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
				for (ItemStack thisItem : equippedItems)
				{
					FontRenderer itemFont = itemStack.getItem().getFontRenderer(itemStack);
					if (itemFont == null)
					{
						itemFont = font;
					}

					GuiUtils.preItemToolTip(itemStack);
					List<ITextComponent> itemStackTooltipLines = screen.getTooltipFromItem(itemStack);
					List<ITextComponent> equippedTooltipLines = screen.getTooltipFromItem(thisItem);

					Rectangle2d itemStackRect = calculateTooltipRect(itemStack, matrixStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxTooltipWidth, itemFont);
					Rectangle2d equippedRect = calculateTooltipRect(itemStack, matrixStack, equippedTooltipLines, x - itemStackRect.getWidth() - 14, y, screen.width, screen.height, maxTooltipWidth, itemFont);

					if (x + itemStackRect.getWidth() + 16 > screen.width)
					{
						itemStackRect = new Rectangle2d(screen.width - itemStackRect.getWidth() - 4, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
					}

					StringTextComponent equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());
					
					// Fix equippedRect x coordinate.
					int tooltipWidth = equippedRect.getWidth();
					equippedRect = new Rectangle2d(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), itemFont.getStringPropertyWidth(equippedBadge) + 8), equippedRect.getHeight());
					equippedRect = new Rectangle2d(itemStackRect.getX() - equippedRect.getWidth() - 16 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());

					// Ensure it's still on the screen on the left side.
					if (equippedRect.getX() < 0)
					{
						equippedRect = new Rectangle2d(0, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
						itemStackRect = new Rectangle2d(equippedRect.getWidth() + 16, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
					}

					GuiUtils.drawHoveringText(matrixStack, itemStackTooltipLines, itemStackRect.getX() - 10, y, screen.width, screen.height, maxTooltipWidth, itemFont);
					GuiUtils.postItemToolTip();

					GuiUtils.preItemToolTip(thisItem);

					matrixStack.push();
					matrixStack.translate(0, 0, 401);
					IRenderTypeBuffer.Impl renderType = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

					Matrix4f matrix = matrixStack.getLast().getMatrix();

					// Draw the "equipped" badge.
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + 1,							 equippedRect.getY() - 15, equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 14, bgColor, bgColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + 1,							 equippedRect.getY() - 2,  equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 1,  bgColor, bgColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + 1,							 equippedRect.getY() - 14, equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 2,  bgColor, bgColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX(),								 equippedRect.getY() - 14, equippedRect.getX() + 1, 						  equippedRect.getY() - 2,  bgColor, bgColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 14, equippedRect.getX() + equippedRect.getWidth(),	  equippedRect.getY() - 2,  bgColor, bgColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + 1,							 equippedRect.getY() - 13, equippedRect.getX() + 2, 						  equippedRect.getY() - 3,  borderStartColor, borderEndColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + equippedRect.getWidth() - 2, equippedRect.getY() - 13, equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 3,  borderStartColor, borderEndColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + 1,							 equippedRect.getY() - 14, equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 13, borderStartColor, borderStartColor);
					GuiUtils.drawGradientRect(matrix, -1, equippedRect.getX() + 1,							 equippedRect.getY() - 3,  equippedRect.getX() + equippedRect.getWidth() - 1, equippedRect.getY() - 2,  borderEndColor, borderEndColor);

					itemFont.func_238416_a_(LanguageMap.getInstance().func_241870_a(equippedBadge), (float)equippedRect.getX() + (equippedRect.getWidth() - itemFont.getStringPropertyWidth(equippedBadge)) / 2, (float)equippedRect.getY() - 12, -1, true, matrixStack.getLast().getMatrix(), renderType, false, 0x000000, 0xF000F0);
					renderType.finish();
					matrixStack.pop();

					GuiUtils.drawHoveringText(matrixStack, equippedTooltipLines, equippedRect.getX() - 8, y, screen.width, screen.height, maxTooltipWidth, itemFont);
					GuiUtils.postItemToolTip();

					// info.cancel();
					// return;

					// TODO: don't return here, in case there are more tooltips to draw.
					return true;
				}
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
