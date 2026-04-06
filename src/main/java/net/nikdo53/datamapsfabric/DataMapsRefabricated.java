package net.nikdo53.datamapsfabric;

import net.fabricmc.api.ModInitializer;

import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import net.nikdo53.datamapsfabric.event.FabricEvents;
import net.nikdo53.datamapsfabric.networking.NetworkingRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataMapsRefabricated implements ModInitializer {
	public static final String MOD_ID = "datamaps-refabricated";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		NetworkingRegistry.init();
		FabricEvents.register();
		DataMapsManager.initDataMaps();
	}
}