package com.anthonyhilyard.equipmentcompare;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public class EquipmentCompareConfig
{
	public static final ForgeConfigSpec SPEC;
	public static final EquipmentCompareConfig INSTANCE;

	public final BooleanValue defaultOn;
	public final BooleanValue strict;
	public final LongValue badgeBackgroundColor;
	public final LongValue badgeBorderStartColor;
	public final LongValue badgeBorderEndColor;
	public final ConfigValue<String> badgeText;

	static
	{
		Pair<EquipmentCompareConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(EquipmentCompareConfig::new);
		SPEC = specPair.getRight();
		INSTANCE = specPair.getLeft();
	}

	public EquipmentCompareConfig(ForgeConfigSpec.Builder build)
	{
		build.comment("Client Configuration").push("client").push("visual_options");

		badgeText = build.comment("The text shown on the badge above equipped tooltips.").define("badge_text", "Equipped");
		badgeBackgroundColor = build.comment("The background color of the \"equipped\" badge.").defineInRange("badge_bg", 0xF0101000L, 0x00000000L, 0xFFFFFFFFL);
		badgeBorderStartColor = build.comment("The start border color of the \"equipped\" badge.").defineInRange("badge_border_start", 0xD0AA9113L, 0x00000000L, 0xFFFFFFFFL);
		badgeBorderEndColor = build.comment("The end border color of the \"equipped\" badge.").defineInRange("badge_border_end", 0x60C2850AL, 0x00000000L, 0xFFFFFFFFL);

		build.pop().push("control_options");

		defaultOn = build.comment("If the comparison tooltip should show by default (pressing bound key hides).").define("default_on", false);
		strict = build.comment("If tool comparisons should compare only the same types of tools (can't compare a sword to an axe, for example).").define("strict", false);

		build.pop().pop();
	}

	@SubscribeEvent
	public static void onLoad(ModConfig.Loading e)
	{
		if (e.getConfig().getModId().equals(Loader.MODID))
		{
			Loader.LOGGER.info("Equipment Compare config reloaded.");
		}
	}

}