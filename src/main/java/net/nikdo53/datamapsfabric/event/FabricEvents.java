package net.nikdo53.datamapsfabric.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.mixin.networking.accessor.ServerCommonNetworkHandlerAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import net.nikdo53.datamapsfabric.datamaps.RegisterDataMapTypesEvent;
import net.nikdo53.datamapsfabric.networking.RegistryDataMapNegotiation;
import net.nikdo53.datamapsfabric.networking.RegistryDataMapSyncPayload;
import net.nikdo53.datamapsfabric.test.TestDataMaps;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FabricEvents {
    public static void register(){
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(FabricEvents::onDatapackSync);
       // UseBlockCallback.EVENT.register(FabricEvents::testOnUseBlock);
        RegisterDataMapTypesEvent.EVENT.register(FabricEvents::onRegisterDataMapTypes);

        ServerConfigurationConnectionEvents.CONFIGURE.register(FabricEvents::onRegisterConfigurationTasks);

    }

    private static void onRegisterConfigurationTasks(ServerConfigurationPacketListenerImpl handler, MinecraftServer server) {
        if (ServerConfigurationNetworking.canSend(handler, RegistryDataMapNegotiation.ID)) {
            handler.addTask(new RegistryDataMapNegotiation(handler));
        } else {
            //i dunno 😔
        }
    }

    private static void onRegisterDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(TestDataMaps.TEST_DATA_MAP);
    }

    private static InteractionResult testOnUseBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack stack = player.getItemInHand(interactionHand);
        System.out.println(stack.getItemHolder().getData(TestDataMaps.TEST_DATA_MAP));

        return InteractionResult.PASS;
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
