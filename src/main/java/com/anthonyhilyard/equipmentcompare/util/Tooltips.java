package com.anthonyhilyard.equipmentcompare.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.lwjgl.util.Rectangle;

import com.anthonyhilyard.equipmentcompare.events.RenderTooltipExtEvent;

import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

@SuppressWarnings("null")
public class Tooltips
{
	public static class TooltipInfo
	{
		private int tooltipWidth = 0;
		private int titleLines = 1;
		private FontRenderer font;
		private List<String> textLines = new ArrayList<>();

		public TooltipInfo(List<String> textLines, FontRenderer font)
		{
			this.textLines = textLines;
			this.font = font;
			this.tooltipWidth = getMaxLineWidth();
		}

		public TooltipInfo(List<String> textLines, FontRenderer font, int titleLines)
		{
			this.textLines = textLines;
			this.font = font;
			this.titleLines = titleLines;
			this.tooltipWidth = getMaxLineWidth();
		}

		public int getTooltipWidth() { return tooltipWidth; }
		public int getTooltipHeight() { return 8 + ((textLines.size() > 1) ? ((textLines.size() - 1) * 10) + ((textLines.size() > titleLines) ? 2 : 0) : 0); }
		public int getTitleLines() { return titleLines; }
		public FontRenderer getFont() { return font; }
		public List<String> getTextLines() { return textLines; }

		public void setFont(FontRenderer font) { this.font = font; }

		public int getMaxLineWidth()
		{
			return getMaxLineWidth(0);
		}

		public int getMaxLineWidth(int minWidth)
		{
			int textWidth = minWidth;
			for (String textLine : textLines)
			{
				int textLineWidth = font.getStringWidth(textLine);
				if (textLineWidth > textWidth)
				{
					textWidth = textLineWidth;
				}
			}
			return textWidth;
		}

		public void wrap(int maxWidth)
		{
			wrap(maxWidth, 0);
		}

		public void wrap(int maxWidth, int minWidth)
		{
			tooltipWidth = minWidth;
			List<String> wrappedLines = new ArrayList<>();
			for (int i = 0; i < textLines.size(); i++)
			{
				String textLine = textLines.get(i);
				List<String> wrappedLine = font.listFormattedStringToWidth(textLine, maxWidth);
				if (i == 0)
				{
					titleLines = wrappedLine.size();
				}

				for (String line : wrappedLine)
				{
					int lineWidth = font.getStringWidth(line);
					if (lineWidth > tooltipWidth)
					{
						tooltipWidth = lineWidth;
					}
					wrappedLines.add(line);
				}
			}

			if (tooltipWidth > maxWidth)
			{
				tooltipWidth = maxWidth;
			}

			textLines = wrappedLines;
		}
	}

	public static void renderItemTooltip(@Nonnull final ItemStack stack, TooltipInfo info,
										Rectangle rect, int screenWidth, int screenHeight,
										int backgroundColor, int borderColorStart, int borderColorEnd)
	{
		renderItemTooltip(stack, info, rect, screenWidth, screenHeight, backgroundColor, borderColorStart, borderColorEnd, false);
	}

	public static void renderItemTooltip(@Nonnull final ItemStack stack, TooltipInfo info,
										Rectangle rect, int screenWidth, int screenHeight,
										int backgroundColor, int borderColorStart, int borderColorEnd, boolean comparison)
	{
		renderItemTooltip(stack, info, rect, screenWidth, screenHeight, backgroundColor, borderColorStart, borderColorEnd, comparison, false);
	}

	public static void renderItemTooltip(@Nonnull final ItemStack stack, TooltipInfo info,
										Rectangle rect, int screenWidth, int screenHeight,
										int backgroundColor, int borderColorStart, int borderColorEnd, boolean comparison, boolean constrain)
	{
		renderItemTooltip(stack, info, rect, screenWidth, screenHeight, backgroundColor, borderColorStart, borderColorEnd, comparison, constrain, 0);
	}

	public static void renderItemTooltip(@Nonnull final ItemStack stack, TooltipInfo info,
										Rectangle rect, int screenWidth, int screenHeight,
										int backgroundColor, int borderColorStart, int borderColorEnd,
										boolean comparison, boolean constrain, int index)
	{
		if (info.getTextLines().isEmpty())
		{
			return;
		}

		int rectX = rect.getX() + 4;
		int rectY = rect.getY() + 4;
		int maxTextWidth = rect.getWidth();

		RenderTooltipExtEvent.Pre preEvent = new RenderTooltipExtEvent.Pre(stack, info.getTextLines(), rectX, rectY, screenWidth, screenHeight, maxTextWidth, info.getFont(), comparison, index);
		if (MinecraftForge.EVENT_BUS.post(preEvent))
		{
			return;
		}

		rectX = preEvent.getX();
		rectY = preEvent.getY();
		screenWidth = preEvent.getScreenWidth();
		screenHeight = preEvent.getScreenHeight();
		maxTextWidth = preEvent.getMaxWidth();
		info.setFont(preEvent.getFontRenderer());

		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int tooltipTextWidth = info.getMaxLineWidth();

		if (tooltipTextWidth > maxTextWidth)
		{
			info.wrap(maxTextWidth);
		}

		final int zLevel = 400;
		RenderTooltipExtEvent.Color colorEvent = new RenderTooltipExtEvent.Color(stack, info.getTextLines(), rectX, rectY, info.getFont(), backgroundColor, borderColorStart, borderColorEnd, comparison, index);
		MinecraftForge.EVENT_BUS.post(colorEvent);

		backgroundColor = colorEvent.getBackground();
		borderColorStart = colorEvent.getBorderStart();
		borderColorEnd = colorEvent.getBorderEnd();

		GlStateManager.pushMatrix();

		GuiUtils.drawGradientRect(zLevel, rectX - 3, rectY - 4, rectX + rect.getWidth() + 3, rectY - 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(zLevel, rectX - 3, rectY + rect.getHeight() + 3, rectX + rect.getWidth() + 3, rectY + rect.getHeight() + 4, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(zLevel, rectX - 3, rectY - 3, rectX + rect.getWidth() + 3, rectY + rect.getHeight() + 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(zLevel, rectX - 4, rectY - 3, rectX - 3, rectY + rect.getHeight() + 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(zLevel, rectX + rect.getWidth() + 3, rectY - 3, rectX + rect.getWidth() + 4, rectY + rect.getHeight() + 3, backgroundColor, backgroundColor);
		GuiUtils.drawGradientRect(zLevel, rectX - 3, rectY - 3 + 1, rectX - 3 + 1, rectY + rect.getHeight() + 3 - 1, borderColorStart, borderColorEnd);
		GuiUtils.drawGradientRect(zLevel, rectX + rect.getWidth() + 2, rectY - 3 + 1, rectX + rect.getWidth() + 3, rectY + rect.getHeight() + 3 - 1, borderColorStart, borderColorEnd);
		GuiUtils.drawGradientRect(zLevel, rectX - 3, rectY - 3, rectX + rect.getWidth() + 3, rectY - 3 + 1, borderColorStart, borderColorStart);
		GuiUtils.drawGradientRect(zLevel, rectX - 3, rectY + rect.getHeight() + 2, rectX + rect.getWidth() + 3, rectY + rect.getHeight() + 3, borderColorEnd, borderColorEnd);

		MinecraftForge.EVENT_BUS.post(new RenderTooltipExtEvent.PostBackground(stack, info.getTextLines(), rectX, rectY, info.getFont(), rect.getWidth(), rect.getHeight(), comparison, index));

		GlStateManager.translate(0.0f, 0.0f, zLevel);

		int tooltipTop = rectY;

		for (int lineNumber = 0; lineNumber < info.getTextLines().size(); ++lineNumber)
		{
			String line = info.getTextLines().get(lineNumber);
			if (line != null)
			{
				info.getFont().drawStringWithShadow(line, (float)rectX, (float)rectY, -1);
			}

			if (lineNumber + 1 == info.getTitleLines())
			{
				rectY += 2;
			}

			rectY += 10;
		}

		GlStateManager.popMatrix();

		MinecraftForge.EVENT_BUS.post(new RenderTooltipExtEvent.PostText(stack, info.getTextLines(), rectX, tooltipTop, info.getFont(), rect.getWidth(), rect.getHeight(), comparison, index));

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
	}

	public static Rectangle calculateRect(final ItemStack stack, List<String> textLines, int mouseX, int mouseY,
												int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font)
	{
		return calculateRect(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font, 0);
	}

	public static Rectangle calculateRect(final ItemStack stack, List<String> textLines, int mouseX, int mouseY,
												int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font, int minWidth)
	{
		Rectangle rect = new Rectangle(0, 0, 0, 0);
		if (textLines == null || textLines.isEmpty() || stack == null)
		{
			return rect;
		}

		// Generate a tooltip event even though we aren't rendering anything in case the event handlers are modifying the input values.
		RenderTooltipEvent.Pre event = new RenderTooltipEvent.Pre(stack, textLines, mouseX, mouseY, screenWidth, screenHeight, maxTextWidth, font);
		if (MinecraftForge.EVENT_BUS.post(event))
		{
			return rect;
		}

		// Fire off a matching post event because some mods rely on both events being fired.
		MinecraftForge.EVENT_BUS.post(new RenderTooltipExtEvent.PostText(ItemStack.EMPTY, new ArrayList<String>(), mouseX, mouseY, font, rect.getWidth(), rect.getHeight(), false, 0));

		mouseX = event.getX();
		mouseY = event.getY();
		screenWidth = event.getScreenWidth();
		screenHeight = event.getScreenHeight();
		maxTextWidth = event.getMaxWidth();
		font = event.getFontRenderer();

		TooltipInfo info = new TooltipInfo(textLines, font);
		int tooltipTextWidth = info.getMaxLineWidth(minWidth);

		boolean needsWrap = false;

		int tooltipX = mouseX + 12;
		int tooltipY = mouseY - 12;
		if (tooltipX + tooltipTextWidth > screenWidth)
		{
			tooltipX -= 28 + tooltipTextWidth;
			if (tooltipX < 4) // if the tooltip doesn't fit on the screen
			{
				if (mouseX > screenWidth / 2)
				{
					tooltipTextWidth = mouseX - 20;
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
			info.wrap(tooltipTextWidth, minWidth);
			tooltipTextWidth = info.getTooltipWidth();
		}

		int tooltipHeight = info.getTooltipHeight();

		if (tooltipX < 6)
		{
			tooltipX = 6;
		}

		if (tooltipY < 6)
		{
			tooltipY = 6;
		}

		if (tooltipY + tooltipHeight + 6 > screenHeight)
		{
			tooltipY = screenHeight - tooltipHeight - 6;
		}

		rect = new Rectangle(tooltipX - 2, tooltipY - 4, tooltipTextWidth, tooltipHeight);
		return rect;
	}
}