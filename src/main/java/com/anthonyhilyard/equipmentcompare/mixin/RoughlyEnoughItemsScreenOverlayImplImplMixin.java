package com.anthonyhilyard.equipmentcompare.mixin;

import java.lang.reflect.Method;

import com.anthonyhilyard.equipmentcompare.Loader;
import com.mojang.blaze3d.vertex.PoseStack;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.impl.client.gui.fabric.ScreenOverlayImplImpl;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;

@Mixin(ScreenOverlayImplImpl.class)
public class RoughlyEnoughItemsScreenOverlayImplImplMixin
{
	@Inject(method ="renderTooltipInner(Lnet/minecraft/client/gui/screens/Screen;Lcom/mojang/blaze3d/vertex/PoseStack;Lme/shedaniel/rei/api/client/gui/widgets/Tooltip;II)V",
			at = @At(value = "HEAD"), require = 0)
	private static void addItemStackAccess(Screen screen, PoseStack poseStack, Tooltip tooltip, int mouseX, int mouseY, CallbackInfo info)
	{
		EntryStack<?> entryStack = tooltip.getContextStack();
		ItemStack itemStack = entryStack.getType() == VanillaEntryTypes.ITEM ? entryStack.castValue() : ItemStack.EMPTY;

		try
		{
			Method setTooltipStack = Screen.class.getDeclaredMethod("setTooltipStack", ItemStack.class);
			setTooltipStack.invoke(screen, itemStack);
		}
		catch (Exception e)
		{
			Loader.LOGGER.error(ExceptionUtils.getStackTrace(e));
		}
	}
}
