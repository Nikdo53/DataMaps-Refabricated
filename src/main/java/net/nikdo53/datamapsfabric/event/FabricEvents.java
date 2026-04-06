package net.nikdo53.datamapsfabric.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import net.nikdo53.datamapsfabric.networking.RegistryDataMapSyncPayload;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricEvents {
    public static void register(){
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(FabricEvents::onDatapackSync);
    }

    private static void onDatapackSync(ServerPlayer serverPlayer, boolean joined) {
        DataMapsManager.getDataMaps().forEach((registry, values) -> {
            final var regOpt = serverPlayer.getServer().overworld().registryAccess().registry(registry);
            if (regOpt.isEmpty()) return;

            ServerCommonNetworkHandlerAccessor connection = (ServerCommonNetworkHandlerAccessor) serverPlayer.connection;

            // Note: don't send data maps over in-memory connections for normal registries, else the client-side handling will wipe non-synced data maps.
            // Sending them for synced datapack registries is fine and required as those registries are recreated on the client
            if (connection.getConnection().isMemoryConnection()
                    && RegistryDataLoader.SYNCHRONIZED_REGISTRIES.stream().map(RegistryDataLoader.RegistryData::key).noneMatch(key-> key.equals(registry))) {
                return;
            }
            final var playerMaps = connection.getConnection().channel.attr(DataMapsManager.ATTRIBUTE_KNOWN_DATA_MAPS).get();
            if (playerMaps == null) return; // Skip gametest players for instance
            handleSync(serverPlayer, regOpt.get(), playerMaps.getOrDefault(registry, List.of()));

        });

    }


    private static <T> void handleSync(ServerPlayer player, Registry<T> registry, Collection<ResourceLocation> attachments) {
        if (attachments.isEmpty()) return;
        final Map<ResourceLocation, Map<ResourceKey<T>, ?>> att = new HashMap<>();
        attachments.forEach(key -> {
            final var attach = DataMapsManager.getDataMap(registry.key(), key);
            if (attach == null || attach.networkCodec() == null) return;
            att.put(key, registry.getDataMap(attach));
        });
        if (!att.isEmpty()) {
            ServerPlayNetworking.send(player, new RegistryDataMapSyncPayload<>(registry.key(), att));
        }
    }


}
