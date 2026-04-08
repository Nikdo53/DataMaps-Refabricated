/*
 * Copyright (c) NeoForged and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.nikdo53.datamapsfabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Event fired to register {@link DataMapType data map types}.
 */
public class RegisterDataMapTypesEvent  {
    private final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> attachments;

    @ApiStatus.Internal
    public RegisterDataMapTypesEvent(Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> attachments) {
        this.attachments = attachments;
    }

    public static final Event<RegisterDataMapTypesEvent.EventListener> EVENT = EventFactory.createArrayBacked(RegisterDataMapTypesEvent.EventListener.class,
            (listeners) -> (event) -> {
                for (var listener : listeners) {
                    listener.onRegisterDataMapTypes(event);
                }
            }
    );

    /**
     * Register a registry data map.
     *
     * @param type the data map type to register
     * @param <T>  the type of the data map
     * @param <R>  the type of the registry
     * @throws IllegalArgumentException      if a type with the same ID has already been registered for that registry
     * @throws UnsupportedOperationException if the registry is a non-synced datapack registry and the data map is synced
     */
    public <T, R> void register(DataMapType<R, T> type) {
        DataMapsManager.register(type);
    }

    @FunctionalInterface
    public interface EventListener {
        void onRegisterDataMapTypes(RegisterDataMapTypesEvent event);
    }
}
