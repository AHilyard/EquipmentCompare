package com.anthonyhilyard.equipmentcompare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Loader.MODID)
public class Loader
{
	public static final String MODID = "equipmentcompare";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public Loader()
	{
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			MinecraftForge.EVENT_BUS.register(EquipmentCompare.class);
			FMLJavaModLoadingContext.get().getModEventBus().addListener(EquipmentCompare::onRegisterKeyMappings);

			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EquipmentCompareConfig.SPEC);
		}

		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "ANY", (remote, isServer) -> true));
	}
}