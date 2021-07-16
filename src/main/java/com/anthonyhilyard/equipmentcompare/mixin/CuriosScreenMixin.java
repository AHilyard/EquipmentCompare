package com.anthonyhilyard.equipmentcompare.mixin;

import com.anthonyhilyard.equipmentcompare.gui.ComparisonTooltips;
import com.mojang.blaze3d.matrix.MatrixStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import top.theillusivec4.curios.client.gui.CuriosScreen;
import top.theillusivec4.curios.common.inventory.container.CuriosContainer;

@Mixin(CuriosScreen.class)
public class CuriosScreenMixin extends ContainerScreen<CuriosContainer>
{
	public CuriosScreenMixin(CuriosContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) { super(screenContainer, inv, titleIn); }

	@Shadow
	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {  }

	@Inject(method = "renderHoveredTooltip(Lcom/mojang/blaze3d/matrix/MatrixStack;II)V", at = @At(value  = "HEAD"), cancellable = true)
	public void renderHoveredTooltip(MatrixStack matrixStack, int x, int y, CallbackInfo info)
	{
		// If the comparison tooltips were displayed, cancel so the default functionality is not run.
		if (ComparisonTooltips.render(matrixStack, x, y, hoveredSlot, minecraft, font, this))
		{
			info.cancel();
		}
	}
}
