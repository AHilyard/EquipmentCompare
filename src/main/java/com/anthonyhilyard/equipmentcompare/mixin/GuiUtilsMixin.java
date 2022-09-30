package com.anthonyhilyard.equipmentcompare.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.anthonyhilyard.equipmentcompare.EquipmentCompare;
import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.config.GuiUtils;

@Mixin(GuiUtils.class)
public class GuiUtilsMixin
{
	@Inject(method = "drawHoveringText(Lnet/minecraft/item/ItemStack;Ljava/util/List;IIIIILnet/minecraft/client/gui/FontRenderer;)V", at = @At(value  = "HEAD"), cancellable = true, remap = false)
	private static void drawHoveringTextExt(final ItemStack itemStack, List<String> textLines, int mouseX, int mouseY, int screenWidth, int screenHeight, int maxTextWidth, FontRenderer font, CallbackInfo info)
	{
		Minecraft mc = Minecraft.getMinecraft();
		if (ComparisonTooltips.render(mouseX, mouseY, itemStack, mc, font, mc.currentScreen, textLines))
		{
			EquipmentCompare.renderingSuccessful = true;
			info.cancel();
		}
	}
}
