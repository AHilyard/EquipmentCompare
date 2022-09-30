package com.anthonyhilyard.equipmentcompare.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;

@Mixin(GuiRepair.class)
public abstract class GuiRepairMixin extends GuiContainer
{
	@Shadow
	private GuiTextField nameField;

	public GuiRepairMixin(Container inventorySlotsIn) { super(inventorySlotsIn); }

	@Inject(method = "func_73863_a(IIF)V", at = @At(value  = "HEAD"), cancellable = true, remap = false)
	public void drawScreenLayeringFix(int mouseX, int mouseY, float partialTicks, CallbackInfo info)
	{
		GuiRepair self = (GuiRepair)(Object)this;
		self.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.disableLighting();
		GlStateManager.disableBlend();
		nameField.drawTextBox();
		GlStateManager.enableLighting();
		GlStateManager.enableBlend();
		renderHoveredToolTip(mouseX, mouseY);
		info.cancel();
	}
}
