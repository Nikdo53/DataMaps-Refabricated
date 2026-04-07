package net.nikdo53.datamapsfabric.networking;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerConfigurationPacketListenerImpl;
import net.nikdo53.datamapsfabric.DataMapsRefabricated;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.Internal
public record RegistryDataMapNegotiation(ServerConfigurationPacketListenerImpl listener) implements ConfigurationTask {
    public static final ResourceLocation ID = DataMapsRefabricated.loc( "registry_data_map_negotiation");
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ID.toString());

    @Override
    public ConfigurationTask.Type type() {
        return TYPE;
    }

    @Override
    public void start(Consumer<Packet<?>> sender) {
        run((payload) -> sender.accept(new ClientboundCustomPayloadPacket(payload)));
    }

    public void run(Consumer<CustomPacketPayload> sender) {
        if (!ServerConfigurationNetworking.canSend(listener, KnownRegistryDataMapsPayload.TYPE)) {
            final var mandatory = DataMapsManager.getDataMaps().values()
                    .stream()
                    .flatMap(map -> map.values().stream())
                    .filter(DataMapType::mandatorySync)
                    .map(type -> type.id() + " (" + type.registryKey().location() + ")")
                    .toList();
            if (!mandatory.isEmpty()) {
                // Use plain components as vanilla connections will be missing our translation keys
                listener.disconnect(Component.literal("This server does not support vanilla clients as it has mandatory registry data maps: ")
                        .append(Component.literal(String.join(", ", mandatory)).withStyle(ChatFormatting.GOLD)));
            } else {
                listener.finishCurrentTask(TYPE);
            }

            return;
        }

        final Map<ResourceKey<? extends Registry<?>>, List<KnownRegistryDataMapsPayload.KnownDataMap>> dataMaps = new HashMap<>();
        DataMapsManager.getDataMaps().forEach((key, attach) -> {
            final List<KnownRegistryDataMapsPayload.KnownDataMap> list = new ArrayList<>();
            attach.forEach((id, val) -> {
                if (val.networkCodec() != null) {
                    list.add(new KnownRegistryDataMapsPayload.KnownDataMap(id, val.mandatorySync()));
                }
            });
            dataMaps.put(key, list);
        });
        sender.accept(new KnownRegistryDataMapsPayload(dataMaps));
    }
}
