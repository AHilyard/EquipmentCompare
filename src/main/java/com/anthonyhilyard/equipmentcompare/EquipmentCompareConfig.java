package com.anthonyhilyard.equipmentcompare;

import java.util.ArrayList;
import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "equipmentcompare")
public class EquipmentCompareConfig implements ConfigData
{
	@ConfigEntry.Gui.Excluded
	public static EquipmentCompareConfig INSTANCE;

	public static void init()
	{
		AutoConfig.register(EquipmentCompareConfig.class, JanksonConfigSerializer::new);
		INSTANCE = AutoConfig.getConfigHolder(EquipmentCompareConfig.class).getConfig();
	}

	@Comment("The text shown on the badge above equipped tooltips.")
	public String badgeText = "Equipped";
	@Comment("The color of the text shown on the badge above equipped tooltips.")
	public long badgeTextColor = 0xFFFFFFFFL;
	@Comment("The background color of the \"equipped\" badge.")
	public long badgeBackgroundColor = 0xF0101000L;
	@Comment("The start border color of the \"equipped\" badge.")
	public long badgeBorderStartColor = 0xD0AA9113L;
	@Comment("The end border color of the \"equipped\" badge.")
	public long badgeBorderEndColor = 0x60C2850AL;

	@Comment("If the comparison tooltip should show by default (pressing bound key hides).")
	public boolean defaultOn = false;
	@Comment("If tool comparisons should compare only the same types of tools (can't compare a sword to an axe, for example).")
	public boolean strict = false;
	@Comment("Blacklist of items to show comparisons for.  Add item IDs to prevent them from being compared when hovered over or equipped.")
	public List<String> blacklist = new ArrayList<String>();
}