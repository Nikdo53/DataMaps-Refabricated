/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.nikdo53.datamapsfabric.networking;

import com.google.common.collect.Maps;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.datamapsfabric.codecs.NeoForgeExtraCodecs;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@ApiStatus.Internal
public record KnownRegistryDataMapsReplyPayload(
        Map<ResourceKey<? extends Registry<?>>, Collection<ResourceLocation>> dataMaps) implements CustomPacketPayload {
    public static final Type<KnownRegistryDataMapsReplyPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("neoforge", "known_registry_data_maps_reply"));
    public static final StreamCodec<FriendlyByteBuf, KnownRegistryDataMapsReplyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    Maps::newHashMapWithExpectedSize,
                    NeoForgeExtraCodecs.registryKeyStreamCodec(),
                    ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.collection(ArrayList::new))),
            KnownRegistryDataMapsReplyPayload::dataMaps,
            KnownRegistryDataMapsReplyPayload::new);

    @Override
    public Type<KnownRegistryDataMapsReplyPayload> type() {
        return TYPE;
    }

    @ApiStatus.Internal
    public void handle(ServerConfigurationNetworking.Context context) {
        context.channelHandlerContext().attr(DataMapsManager.ATTRIBUTE_KNOWN_DATA_MAPS).set(this.dataMaps());
        context.finishCurrentTask(RegistryDataMapNegotiation.TYPE);
    }
}
