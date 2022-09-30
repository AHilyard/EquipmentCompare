package com.anthonyhilyard.equipmentcompare.events;

import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

import net.minecraftforge.client.event.RenderTooltipEvent;

@SuppressWarnings("null")
public class RenderTooltipExtEvent
{
	public interface IRenderTooltipExt
	{
		public boolean isComparison();
		public int getIndex();
	}

	public static class Pre extends RenderTooltipEvent.Pre implements IRenderTooltipExt
	{
		private final boolean comparisonTooltip;
		private final int index;

		public Pre(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, int screenWidth, int screenHeight, int maxWidth, @Nonnull FontRenderer font, boolean comparison, int index)
		{
			super(stack, textLines, x, y, screenWidth, screenHeight, maxWidth, font);
			this.comparisonTooltip = comparison;
			this.index = index;
		}

		public Pre(@Nonnull ItemStack stack, @Nonnull List<String> textLines, int x, int y, int screenWidth, int screenHeight, int maxWidth, @Nonnull FontRenderer font, boolean comparison)
		{
			this(stack, textLines, x, y, screenWidth, screenHeight, maxWidth, font, comparison, 0);
		}
		@Override
		public boolean isComparison() { return comparisonTooltip; }
		@Override
		public int getIndex() { return index; }
	}

	public static class PostBackground extends RenderTooltipEvent.PostBackground implements IRenderTooltipExt
	{
		private final boolean comparisonTooltip;
		private final int index;

		public PostBackground(ItemStack stack, List<String> textLines, int x, int y, FontRenderer font, int width, int height, boolean comparison, int index)
		{
			super(stack, textLines, x, y, font, width, height);
			this.comparisonTooltip = comparison;
			this.index = index;
		}

		public PostBackground(ItemStack stack, List<String> textLines, int x, int y, FontRenderer font, int width, int height, boolean comparison)
		{
			this(stack, textLines, x, y, font, width, height, comparison, 0);
		}
		@Override
		public boolean isComparison() { return comparisonTooltip; }
		@Override
		public int getIndex() { return index; }
	}

	public static class PostText extends RenderTooltipEvent.PostText implements IRenderTooltipExt
	{
		private final boolean comparisonTooltip;
		private final int index;

		public PostText(ItemStack stack, List<String> textLines, int x, int y, FontRenderer font, int width, int height, boolean comparison, int index)
		{
			super(stack, textLines, x, y, font, width, height);
			this.comparisonTooltip = comparison;
			this.index = index;
		}

		public PostText(ItemStack stack, List<String> textLines, int x, int y, FontRenderer font, int width, int height, boolean comparison)
		{
			this(stack, textLines, x, y, font, width, height, comparison, 0);
		}
		@Override
		public boolean isComparison() { return comparisonTooltip; }
		@Override
		public int getIndex() { return index; }
	}

	public static class Color extends RenderTooltipEvent.Color implements IRenderTooltipExt
	{
		private final boolean comparisonTooltip;
		private final int index;

		public Color(ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer font, int background, int borderStart, int borderEnd, boolean comparison, int index)
		{
			super(stack, textLines, x, y, font, background, borderStart, borderEnd);
			this.comparisonTooltip = comparison;
			this.index = index;
		}
		public Color(ItemStack stack, @Nonnull List<String> textLines, int x, int y, @Nonnull FontRenderer font, int background, int borderStart, int borderEnd, boolean comparison)
		{
			this(stack, textLines, x, y, font, background, borderStart, borderEnd, comparison, 0);
		}
		@Override
		public boolean isComparison() { return comparisonTooltip; }
		@Override
		public int getIndex() { return index; }
	}
}