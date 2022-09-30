package com.anthonyhilyard.equipmentcompare;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = EquipmentCompare.MODID)
public class EquipmentCompareConfig extends Configuration
{
	public static EquipmentCompareConfig INSTANCE;

	public boolean defaultOn;
	public boolean strict;
	public String badgeBackgroundColor;
	public String badgeBorderStartColor;
	public String badgeBorderEndColor;
	public boolean overrideBadgeText;
	public String badgeText;
	public String badgeTextColor;
	public List<String> blacklist;

	public static void loadConfig(File file)
	{
		INSTANCE = new EquipmentCompareConfig(file);
	}

	private EquipmentCompareConfig(File file)
	{
		super(file);
		load();

		// Update the data type of the categories collection so it maintains the proper order.
		try
		{
			Field categoriesField = Configuration.class.getDeclaredField("categories");
			categoriesField.setAccessible(true);
			Map<String, ConfigCategory> categories = new LinkedHashMap<>();

			// Get or create all the categories in the proper order by getting them here.
			categories.put("visual_options", getCategory("visual_options"));
			categories.put("control_options", getCategory("definitions"));

			categoriesField.set(this, categories);
		}
		catch (Exception e)
		{
			EquipmentCompare.LOGGER.error(e);
		}

		overrideBadgeText = getBoolean("override_badge_text", "visual_options", false, "If badge_text should override the built-in translatable text.");
		badgeText = getString("badge_text", "visual_options", "Equipped", "The text shown on the badge above equipped tooltips.");
		badgeTextColor = getString("badge_text_color", "visual_options", "#FFFFFFFF", "The color of the text shown on the badge above equipped tooltips.");
		badgeBackgroundColor = getString("badge_bg", "visual_options", "#F0101000", "The background color of the \"equipped\" badge.");
		badgeBorderStartColor = getString("badge_border_start", "visual_options", "#D0AA9113", "The start color of the border of the \"equipped\" badge.");
		badgeBorderEndColor = getString("badge_border_end", "visual_options", "#60C2850A", "The end color of the border of the \"equipped\" badge.");
		
		defaultOn = getBoolean("default_on", "control_options", false, "If the comparison tooltip should show by default (pressing bound key hides).");
		strict = getBoolean("strict", "control_options", false, "If held item comparisons should compare only the same types of items (can't compare a sword to an axe, for example).");
		blacklist = Arrays.asList(getStringList("blacklist", "control_options", new String[]{}, "Blacklist of items to show comparisons for.  Add item IDs to prevent them from being compared when hovered over or equipped."));

		save();
	}

	@SubscribeEvent
	public static void onLoad(final ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if (event.getModID().equals(EquipmentCompare.MODID))
		{
			ConfigManager.sync(EquipmentCompare.MODID, Config.Type.INSTANCE);
		}
	}
}