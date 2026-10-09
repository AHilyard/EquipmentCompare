package com.anthonyhilyard.equipmentcompare.config;

import java.util.List;
import java.util.function.Supplier;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.iceberg.config.IcebergConfig;
import com.anthonyhilyard.iceberg.services.IIcebergConfigSpecBuilder;
import com.anthonyhilyard.iceberg.util.Selectors;
import com.google.common.collect.Lists;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public class EquipmentCompareConfig extends IcebergConfig<EquipmentCompareConfig>
{
	public static EquipmentCompareConfig getInstance() { return (EquipmentCompareConfig)configInstances.get(EquipmentCompare.MODID); }

	public final Supplier<Boolean> defaultOn;
	public final Supplier<Boolean> strict;
	public final Supplier<Integer> maxComparisons;
	public final Supplier<Long> badgeBackgroundColor;
	public final Supplier<Long> badgeBorderStartColor;
	public final Supplier<Long> badgeBorderEndColor;
	public final Supplier<Boolean> overrideBadgeText;
	public final Supplier<String> badgeText;
	public final Supplier<Long> badgeTextColor;
	private final Supplier<List<? extends String>> blacklist;

	public EquipmentCompareConfig(IIcebergConfigSpecBuilder build)
	{
		build.comment("Client Configuration").push("client").push("visual_options");

		maxComparisons = build.comment(" The maximum number of comparison tooltips to show onscreen at once.").addInRange("max_comparisons", 3, 1, 10);
		overrideBadgeText = build.comment(" If badge_text should override the built-in translatable text.").add("override_badge_text", false);
		badgeText = build.comment(" The text shown on the badge above equipped tooltips.").add("badge_text", "Equipped");
		badgeTextColor = build.comment(" The color of the text shown on the badge above equipped tooltips.").addInRange("badge_text_color", 0xFFFFFFFFL, 0x00000000L, 0xFFFFFFFFL);
		badgeBackgroundColor = build.comment(" The background color of the \"equipped\" badge.").addInRange("badge_bg", 0xF0101000L, 0x00000000L, 0xFFFFFFFFL);
		badgeBorderStartColor = build.comment(" The start border color of the \"equipped\" badge.").addInRange("badge_border_start", 0xD0AA9113L, 0x00000000L, 0xFFFFFFFFL);
		badgeBorderEndColor = build.comment(" The end border color of the \"equipped\" badge.").addInRange("badge_border_end", 0x60C2850AL, 0x00000000L, 0xFFFFFFFFL);

		build.pop().push("control_options");

		defaultOn = build.comment(" If the comparison tooltip should show by default (pressing bound key hides).").add("default_on", false);
		strict = build.comment(" If tool comparisons should compare only the same types of tools (can't compare a sword to an axe, for example).").add("strict", false);
		blacklist = build.comment(" Item blacklist to disable comparisons for.  Add to prevent items from being compared when hovered over or equipped.  All Iceberg item selectors are supported (https://github.com/AHilyard/Iceberg/wiki/Item-Selectors-Documentation).").addListAllowEmpty("blacklist", Lists.newArrayList(), e -> Selectors.validateSelector((String)e) );

		build.pop().pop();
	}

	public static boolean isItemBlacklisted(ItemStack itemStack, HolderLookup.Provider provider)
	{
		for (String entry : getInstance().blacklist.get())
		{
			if (Selectors.itemMatches(itemStack, entry, provider))
			{
				return true;
			}
		}
		return false;
	}

}