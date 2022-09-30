package com.anthonyhilyard.equipmentcompare.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.EquipmentCompareConfig;
import com.anthonyhilyard.equipmentcompare.events.RenderTooltipExtEvent;
import com.anthonyhilyard.equipmentcompare.util.ColorUtil;
import com.anthonyhilyard.equipmentcompare.util.Tooltips;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lwjgl.util.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.common.Loader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

import net.minecraft.util.text.translation.LanguageMap;
import net.minecraftforge.common.MinecraftForge;

@SuppressWarnings("null")
public class ComparisonTooltips
{
	private static final List<String> UNSUPPORTED_LT_VERSIONS = Lists.newArrayList("0.0", "1.1.5", "1.1.6");
	private static final int DEFAULT_BACKGROUND_COLOR = 0xF0100010;
	private static final int DEFAULT_BORDER_COLOR_START = 0x505000FF;
	private static final int DEFAULT_BORDER_COLOR_END = (DEFAULT_BORDER_COLOR_START & 0xFEFEFE) >> 1 | DEFAULT_BORDER_COLOR_START & 0xFF000000;

	private static void drawTooltip(ItemStack itemStack, Rectangle rect, List<String> tooltipLines, FontRenderer font, GuiScreen screen, int maxWidth, boolean showBadge, int index)
	{
		int bgColor = ColorUtil.parseColor(EquipmentCompareConfig.INSTANCE.badgeBackgroundColor);
		int borderStartColor = ColorUtil.parseColor(EquipmentCompareConfig.INSTANCE.badgeBorderStartColor);
		int borderEndColor = ColorUtil.parseColor(EquipmentCompareConfig.INSTANCE.badgeBorderEndColor);

		String equippedBadge;
		if (EquipmentCompareConfig.INSTANCE.overrideBadgeText && LanguageMap.getInstance().isKeyTranslated(EquipmentCompareConfig.INSTANCE.badgeText))
		{
			equippedBadge = LanguageMap.getInstance().translateKey(EquipmentCompareConfig.INSTANCE.badgeText);
		}
		else
		{
			equippedBadge = LanguageMap.getInstance().translateKey("equipmentcompare.general.badgeText");
		}

		boolean constrainToRect = false;

		if (showBadge)
		{
			if (rect.getY() + rect.getHeight() + 4 > screen.height)
			{
				rect = new Rectangle(rect.getX(), screen.height - rect.getHeight() - 4, rect.getWidth(), rect.getHeight());
			}

			GlStateManager.disableRescaleNormal();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.disableDepth();

			GlStateManager.pushMatrix();
			GlStateManager.translate(0, 0, 401);
			int badgeOffset = 0;

			String legendaryTooltipsVersion = Loader.isModLoaded("legendarytooltips") ? Loader.instance().getIndexedModList().get("legendarytooltips").getVersion() : "0.0";

			// Draw the "equipped" badge.
			// If a supported version of legendary tooltips is installed, AND this item needs a custom border display the badge lower and without a border.
			if (!UNSUPPORTED_LT_VERSIONS.contains(legendaryTooltipsVersion))
			{
				// Fire a color event to properly update the background color if needed.
				RenderTooltipExtEvent.Color colorEvent = new RenderTooltipExtEvent.Color(itemStack, tooltipLines, rect.getX(), rect.getY(), font, bgColor, borderStartColor, borderEndColor, showBadge, index);
				if (!MinecraftForge.EVENT_BUS.post(colorEvent))
				{
					bgColor = colorEvent.getBackground();
				}
				
				constrainToRect = true;
				badgeOffset = 6;

				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 17 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX(),						 rect.getY() - 16 + badgeOffset, rect.getX() + 1, 					rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 8,	rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 6 + badgeOffset,  bgColor, bgColor);
			}
			else
			{
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 17 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX(),						 rect.getY() - 16 + badgeOffset, rect.getX() + 1, 					rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + rect.getWidth() + 7, rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 8,	rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 4  + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 3 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 4 + badgeOffset,  bgColor, bgColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 15 + badgeOffset, rect.getX() + 2, 					rect.getY() - 5 + badgeOffset,  borderStartColor, borderEndColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + rect.getWidth() + 6, rect.getY() - 15 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 5 + badgeOffset,  borderStartColor, borderEndColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 16 + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 15 + badgeOffset, borderStartColor, borderStartColor);
				GuiUtils.drawGradientRect(-1, rect.getX() + 1,					 rect.getY() - 5  + badgeOffset, rect.getX() + rect.getWidth() + 7, rect.getY() - 4 + badgeOffset,  borderEndColor,   borderEndColor);
			}

			font.drawString(equippedBadge, (float)rect.getX() + (rect.getWidth() - font.getStringWidth(equippedBadge)) / 2 + 4, (float)rect.getY() - 14 + badgeOffset, ColorUtil.parseColor(EquipmentCompareConfig.INSTANCE.badgeTextColor), false);
			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.enableRescaleNormal();
		}

		Tooltips.renderItemTooltip(itemStack, new Tooltips.TooltipInfo(tooltipLines, font), rect, screen.width, screen.height, DEFAULT_BACKGROUND_COLOR, DEFAULT_BORDER_COLOR_START, DEFAULT_BORDER_COLOR_END, showBadge, constrainToRect, index);
	}

	public static boolean render(int x, int y, Slot hoveredSlot, Minecraft minecraft, FontRenderer font, GuiScreen screen)
	{
		ItemStack itemStack = hoveredSlot != null ? hoveredSlot.getStack() : ItemStack.EMPTY;
		return render(x, y, itemStack, minecraft, font, screen, null);
	}

	public static boolean render(int x, int y, ItemStack itemStack, Minecraft minecraft, FontRenderer font, GuiScreen screen)
	{
		return render(x, y, itemStack, minecraft, font, screen, null);
	}

	@SuppressWarnings({"unchecked", "deprecation"})
	public static boolean render(int x, int y, ItemStack itemStack, Minecraft minecraft, FontRenderer font, GuiScreen screen, List<String> customTooltipLines)
	{
		// The screen must be valid to render tooltips.
		if (screen == null)
		{
			return false;
		}

		if (minecraft.player.inventory.getItemStack().isEmpty() && !itemStack.isEmpty() && !EquipmentCompareConfig.INSTANCE.blacklist.contains(itemStack.getItem().getRegistryName().toString()))
		{
			// If this is a piece of equipment and we are already wearing the same type, display an additional tooltip as well.
			EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(itemStack);

			List<ItemStack> equippedItems = new ArrayList<ItemStack>();
			ItemStack equippedItem = minecraft.player.getItemStackFromSlot(slot);
		
			boolean checkItem = true;

			// For held items, only check items with durability.
			if (slot == EntityEquipmentSlot.MAINHAND)
			{
				// Ensure both items are comparable.
				// Any item with durability can be compared.
				if (itemStack.getItem().getMaxDamage() <= 0 || equippedItem.getItem().getMaxDamage() <= 0)
				{
					checkItem = false;
				}
				// If strict comparisons are enabled, only compare items of the same type.
				else if (EquipmentCompareConfig.INSTANCE.strict)
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
				equippedItems.removeIf(i -> i.getItem() == Items.AIR);
			}

			// If The Aether is installed, check for equipped Aether accessories to compare as well.
			if (Loader.isModLoaded("aether_legacy"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.AetherHandler").getMethod("getAetherAccessoriesMatchingSlot", EntityPlayer.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// If Baubles is installed, check for equipped baubles to compare as well.
			if (Loader.isModLoaded("baubles"))
			{
				try
				{
					equippedItems.addAll((List<ItemStack>) Class.forName("com.anthonyhilyard.equipmentcompare.compat.BaublesHandler").getMethod("getBaublesMatchingSlot", EntityPlayer.class, ItemStack.class).invoke(null, minecraft.player, itemStack));
				}
				catch (Exception e)
				{
					EquipmentCompare.LOGGER.error(ExceptionUtils.getStackTrace(e));
				}
			}

			// Filter blacklisted items.
			equippedItems.removeIf(stack -> EquipmentCompareConfig.INSTANCE.blacklist.contains(stack.getItem().getRegistryName().toString()));

			// Make sure we don't compare an item to itself (can happen with Curios slots).
			equippedItems.remove(itemStack);

			if (!equippedItems.isEmpty() && (EquipmentCompare.comparisonsActive ^ EquipmentCompareConfig.INSTANCE.defaultOn))
			{
				int maxWidth = ((screen.width - (equippedItems.size() * 16)) / (equippedItems.size() + 1));
				FontRenderer itemFont = itemStack.getItem().getFontRenderer(itemStack);
				if (itemFont == null)
				{
					itemFont = font;
				}

				List<String> itemStackTooltipLines = customTooltipLines == null ? screen.getItemToolTip(itemStack) : customTooltipLines;
				Rectangle itemStackRect = Tooltips.calculateRect(itemStack, itemStackTooltipLines, x, y, screen.width, screen.height, maxWidth, itemFont, 0);
				if (x + itemStackRect.getWidth() + 12 > screen.width)
				{
					itemStackRect = new Rectangle(screen.width - itemStackRect.getWidth() - 24, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}
				else
				{
					itemStackRect = new Rectangle(itemStackRect.getX() - 2, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				Map<ItemStack, Rectangle> tooltipRects = new HashMap<ItemStack, Rectangle>();
				Map<ItemStack, List<String>> tooltipLines = new HashMap<ItemStack, List<String>>();

				Rectangle previousRect = itemStackRect;
				boolean firstRect = true;

				// Keep track of the tooltip index.
				int tooltipIndex = 1;

				// Set up tooltip rects.
				for (ItemStack thisItem : equippedItems)
				{
					if (thisItem.getItem().getFontRenderer(thisItem) != null)
					{
						itemFont = thisItem.getItem().getFontRenderer(thisItem);
					}

					List<String> equippedTooltipLines = screen.getItemToolTip(thisItem);
					Rectangle equippedRect = Tooltips.calculateRect(itemStack, equippedTooltipLines, x - previousRect.getWidth() - 14, y, screen.width, screen.height, maxWidth, itemFont, 0);
					String equippedBadge;
					if (EquipmentCompareConfig.INSTANCE.overrideBadgeText && LanguageMap.getInstance().isKeyTranslated(EquipmentCompareConfig.INSTANCE.badgeText))
					{
						equippedBadge = LanguageMap.getInstance().translateKey(EquipmentCompareConfig.INSTANCE.badgeText);
					}
					else
					{
						equippedBadge = LanguageMap.getInstance().translateKey("equipmentcompare.general.badgeText");
					}

					// Fix equippedRect x coordinate.
					int tooltipWidth = equippedRect.getWidth();
					equippedRect = new Rectangle(equippedRect.getX(), equippedRect.getY(), Math.max(equippedRect.getWidth(), itemFont.getStringWidth(equippedBadge) + 8), equippedRect.getHeight());

					if (firstRect)
					{
						equippedRect = new Rectangle(previousRect.getX() - equippedRect.getWidth() - 16 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
						firstRect = false;
					}
					else
					{
						equippedRect = new Rectangle(previousRect.getX() - equippedRect.getWidth() - 12 - (equippedRect.getWidth() - tooltipWidth) / 2, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight());
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
						Rectangle equippedRect = tooltipRects.get(thisItem);
						tooltipRects.replace(thisItem, new Rectangle(equippedRect.getX() + xOffset, equippedRect.getY(), equippedRect.getWidth(), equippedRect.getHeight()));
					}

					// Move the hovered item rect.
					itemStackRect = new Rectangle(itemStackRect.getX() + xOffset, itemStackRect.getY(), itemStackRect.getWidth(), itemStackRect.getHeight());
				}

				tooltipIndex = 1;

				// Now draw them all.
				for (ItemStack thisItem : equippedItems)
				{
					drawTooltip(thisItem, tooltipRects.get(thisItem), tooltipLines.get(thisItem), font, screen, maxWidth, true, tooltipIndex++);
				}
				drawTooltip(itemStack, itemStackRect, itemStackTooltipLines, font, screen, maxWidth, false, 0);

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
