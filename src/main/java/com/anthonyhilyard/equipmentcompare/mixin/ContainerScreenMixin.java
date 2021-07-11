package com.anthonyhilyard.equipmentcompare.mixin;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraft.entity.MobEntity;

@Mixin(ContainerScreen.class)
public class ContainerScreenMixin extends Screen
{
	protected ContainerScreenMixin(ITextComponent titleIn) { super(titleIn); }

	@Shadow
	@Nullable
	protected Slot hoveredSlot;

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
					titleLinesCount = wrappedLine.size();

				for (ITextProperties line : wrappedLine)
				{
					int lineWidth = font.getStringPropertyWidth(line);
					if (lineWidth > wrappedTooltipWidth)
						wrappedTooltipWidth = lineWidth;
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

	@Inject(method = "renderHoveredTooltip(Lcom/mojang/blaze3d/matrix/MatrixStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	private void renderHoveredTooltip(MatrixStack matrixStack, int x, int y, CallbackInfo info)
	{
		int bgColor = (int)EquipmentCompareConfig.INSTANCE.badgeBackgroundColor.get().longValue();
		int borderStartColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderStartColor.get().longValue();
		int borderEndColor = (int)EquipmentCompareConfig.INSTANCE.badgeBorderEndColor.get().longValue();

		ItemStack itemStack = this.hoveredSlot != null ? this.hoveredSlot.getStack() : ItemStack.EMPTY;
		if (this.minecraft.player.inventory.getItemStack().isEmpty() && !itemStack.isEmpty())
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EquipmentSlotType slot = MobEntity.getSlotForItemStack(itemStack);

			ItemStack equippedItem = this.minecraft.player.getItemStackFromSlot(slot);
			if (equippedItem != ItemStack.EMPTY && equippedItem != itemStack && (EquipmentCompare.tooltipActive ^ EquipmentCompareConfig.INSTANCE.defaultOn.get()))
			{
				FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
				if (font == null)
				{
					font = this.font;
				}

				GuiUtils.preItemToolTip(itemStack);
				List<ITextComponent> itemStackTooltipLines = this.getTooltipFromItem(itemStack);
				List<ITextComponent> equippedTooltipLines = this.getTooltipFromItem(equippedItem);

				Rectangle2d itemStackRect = calculateTooltipRect(itemStack, matrixStack, itemStackTooltipLines, x, y, width, height, 250, font);
				Rectangle2d equippedRect = calculateTooltipRect(itemStack, matrixStack, equippedTooltipLines, x - itemStackRect.getWidth() - 14, y, width, height, 250, font);

				if (x + itemStackRect.getWidth() + 16 > width)
				{
					itemStackRect = new Rectangle2d(width - itemStackRect.getWidth() - 4, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				StringTextComponent equippedBadge = new StringTextComponent(EquipmentCompareConfig.INSTANCE.badgeText.get());
				
				// Fix equippedRect x coordinate.
				int tooltipWidth = equippedRect.getWidth();
				equippedRect = new Rectangle2d(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), font.getStringPropertyWidth(equippedBadge) + 8), equippedRect.getHeight());
				equippedRect = new Rectangle2d(itemStackRect.getX() - equippedRect.getWidth() - 16 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());

				// Ensure it's still on the screen on the left side.
				if (equippedRect.getX() < 0)
				{
					equippedRect = new Rectangle2d(0, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
					itemStackRect = new Rectangle2d(equippedRect.getWidth() + 16, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				GuiUtils.drawHoveringText(matrixStack, itemStackTooltipLines, itemStackRect.getX() - 10, y, width, height, 250, font);
				GuiUtils.postItemToolTip();

				GuiUtils.preItemToolTip(equippedItem);

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

				font.func_238416_a_(LanguageMap.getInstance().func_241870_a(equippedBadge), (float)equippedRect.getX() + (equippedRect.getWidth() - font.getStringPropertyWidth(equippedBadge)) / 2, (float)equippedRect.getY() - 12, -1, true, matrixStack.getLast().getMatrix(), renderType, false, 0x000000, 0xF000F0);
				renderType.finish();
				matrixStack.pop();

				GuiUtils.drawHoveringText(matrixStack, equippedTooltipLines, equippedRect.getX() - 8, y, width, height, 250, font);
				GuiUtils.postItemToolTip();

				info.cancel();
				return;
			}
			// Otherwise display the tooltip normally.
			else
			{
				return;
			}
		}
	}
}