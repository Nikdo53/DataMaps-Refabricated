package net.nikdo53.datamapsfabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.nikdo53.datamapsfabric.networking.KnownRegistryDataMapsPayload;
import net.nikdo53.datamapsfabric.networking.RegistryDataMapSyncPayload;

@Environment(EnvType.CLIENT)
public class DataMapsRefabricatedClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        ClientConfigurationNetworking.registerGlobalReceiver(KnownRegistryDataMapsPayload.TYPE, KnownRegistryDataMapsPayload::handle);
        ClientPlayNetworking.registerGlobalReceiver(RegistryDataMapSyncPayload.TYPE, RegistryDataMapSyncPayload::handle);

    }
}
