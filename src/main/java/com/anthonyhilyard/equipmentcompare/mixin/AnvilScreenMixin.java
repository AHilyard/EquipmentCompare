package com.anthonyhilyard.equipmentcompare.mixin;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AnvilMenu;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin extends ItemCombinerScreen<AnvilMenu>
{
	public AnvilScreenMixin(AnvilMenu itemCombinerMenu, Inventory inventory, Component component, ResourceLocation resourceLocation)
	{
		super(itemCombinerMenu, inventory, component, resourceLocation);
	}

	@Inject(method = "keyPressed", at = @At(value = "HEAD"), cancellable = true)
	public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> info)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(i, j))
		{
			EquipmentCompare.comparisonsActive = true;
			info.setReturnValue(true);
		}
	}

	@Override
	public boolean keyReleased(int i, int j, int k)
	{
		if (EquipmentCompare.showComparisonTooltip.matches(i, j))
		{
			EquipmentCompare.comparisonsActive = false;
			return true;
		}
		return super.keyReleased(i, j, k);
	}

	@Shadow
	protected void renderErrorIcon(GuiGraphics guiGraphics, int i, int j) { }
}