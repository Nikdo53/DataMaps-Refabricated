/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.nikdo53.datamapsfabric.networking;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.datamapsfabric.DataMapsRefabricated;
import net.nikdo53.datamapsfabric.codecs.NeoForgeExtraCodecs;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.Internal
public record KnownRegistryDataMapsPayload(Map<ResourceKey<? extends Registry<?>>, List<KnownDataMap>> dataMaps) implements CustomPacketPayload {
    public static final Type<KnownRegistryDataMapsPayload> TYPE = new Type<>(DataMapsRefabricated.loc("known_registry_data_maps"));
    public static final StreamCodec<FriendlyByteBuf, KnownRegistryDataMapsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    Maps::newHashMapWithExpectedSize,
                    NeoForgeExtraCodecs.registryKeyStreamCodec(),
                    KnownDataMap.STREAM_CODEC.apply(ByteBufCodecs.list())),
            KnownRegistryDataMapsPayload::dataMaps,
            KnownRegistryDataMapsPayload::new);

    @Override
    public Type<KnownRegistryDataMapsPayload> type() {
        return TYPE;
    }

    public record KnownDataMap(ResourceLocation id, boolean mandatory) {
        public static final StreamCodec<FriendlyByteBuf, KnownDataMap> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, KnownDataMap::id,
                ByteBufCodecs.BOOL, KnownDataMap::mandatory,
                KnownDataMap::new);
    }


    public void handle(ClientConfigurationNetworking.Context context) {
        record MandatoryEntry(ResourceKey<? extends Registry<?>> registry, ResourceLocation id) {}
        final Set<MandatoryEntry> ourMandatory = new HashSet<>();
        DataMapsManager.getDataMaps().forEach((reg, values) -> values.values().forEach(attach -> {
            if (attach.mandatorySync()) {
                ourMandatory.add(new MandatoryEntry(reg, attach.id()));
            }
        }));

        final Set<MandatoryEntry> theirMandatory = new HashSet<>();
        this.dataMaps().forEach((reg, values) -> values.forEach(attach -> {
            if (attach.mandatory()) {
                theirMandatory.add(new MandatoryEntry(reg, attach.id()));
            }
        }));

        final List<Component> messages = new ArrayList<>();
        final var missingOur = Sets.difference(ourMandatory, theirMandatory);
        if (!missingOur.isEmpty()) {
            messages.add(Component.translatable("neoforge.network.data_maps.missing_our", Component.literal(missingOur.stream()
                    .map(e -> e.id() + " (" + e.registry().location() + ")")
                    .collect(Collectors.joining(", "))).withStyle(ChatFormatting.GOLD)));
        }

        final var missingTheir = Sets.difference(theirMandatory, ourMandatory);
        if (!missingTheir.isEmpty()) {
            messages.add(Component.translatable("neoforge.network.data_maps.missing_their", Component.literal(missingTheir.stream()
                    .map(e -> e.id() + " (" + e.registry().location() + ")")
                    .collect(Collectors.joining(", "))).withStyle(ChatFormatting.GOLD)));
        }

        if (!messages.isEmpty()) {
            MutableComponent message = Component.empty();
            final var itr = messages.iterator();
            while (itr.hasNext()) {
                message = message.append(itr.next());
                if (itr.hasNext()) {
                    message = message.append("\n");
                }
            }

            context.responseSender().disconnect(message);
            return;
        }

        final var known = new HashMap<ResourceKey<? extends Registry<?>>, Collection<ResourceLocation>>();
        DataMapsManager.getDataMaps().forEach((key, vals) -> known.put(key, vals.keySet()));
        context.responseSender().sendPacket(new KnownRegistryDataMapsReplyPayload(known));
    }
}
