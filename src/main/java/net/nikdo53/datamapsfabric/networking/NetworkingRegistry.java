package net.nikdo53.datamapsfabric.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;

public class NetworkingRegistry {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(RegistryDataMapSyncPayload.TYPE, RegistryDataMapSyncPayload.STREAM_CODEC);
        ClientPlayNetworking.registerGlobalReceiver(RegistryDataMapSyncPayload.TYPE, RegistryDataMapSyncPayload::handle);

        PayloadTypeRegistry.configurationS2C().register(KnownRegistryDataMapsPayload.TYPE, KnownRegistryDataMapsPayload.STREAM_CODEC);
        ClientConfigurationNetworking.registerGlobalReceiver(KnownRegistryDataMapsPayload.TYPE, KnownRegistryDataMapsPayload::handle);

        PayloadTypeRegistry.configurationC2S().register(KnownRegistryDataMapsReplyPayload.TYPE, KnownRegistryDataMapsReplyPayload.STREAM_CODEC);
        ServerConfigurationNetworking.registerGlobalReceiver(KnownRegistryDataMapsReplyPayload.TYPE, KnownRegistryDataMapsReplyPayload::handle);
    }
}
