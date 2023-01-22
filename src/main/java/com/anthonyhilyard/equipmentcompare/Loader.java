package com.anthonyhilyard.equipmentcompare;

import com.anthonyhilyard.equipmentcompare.config.EquipmentCompareConfig;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Loader.MODID)
public class Loader
{
	public static final String MODID = "equipmentcompare";

	public Loader()
	{
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			EquipmentCompare mod = new EquipmentCompare();
			FMLJavaModLoadingContext.get().getModEventBus().addListener(mod::onClientSetup);
			MinecraftForge.EVENT_BUS.register(EquipmentCompare.class);

			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EquipmentCompareConfig.SPEC);
		}

		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
	}
}