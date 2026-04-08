package net.nikdo53.datamapsfabric.datamaps;

import io.netty.util.AttributeKey;
import net.fabricmc.fabric.impl.registry.sync.DynamicRegistriesImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.nikdo53.datamapsfabric.DataMapsRefabricated;
import net.nikdo53.datamapsfabric.event.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DataMapsManager {
    private static final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMaps = new IdentityHashMap<>();

    @Nullable
    public static <R> DataMapType<R, ?> getDataMap(ResourceKey<? extends Registry<R>> registry, ResourceLocation key) {
        final var map = dataMaps.get(registry);
        return map == null ? null : (DataMapType<R, ?>) map.get(key);
    }

    public static void initDataMaps() {
        final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMapTypes = new HashMap<>();
        RegisterDataMapTypesEvent.EVENT.invoker().onRegisterDataMapTypes(new RegisterDataMapTypesEvent(dataMapTypes));
        dataMapTypes.forEach((key, values) -> dataMaps.put(key, Collections.unmodifiableMap(values)));
    }

    public static final AttributeKey<Map<ResourceKey<? extends Registry<?>>, Collection<ResourceLocation>>> ATTRIBUTE_KNOWN_DATA_MAPS = AttributeKey.valueOf(DataMapsRefabricated.loc("known_data_maps").toString());

    /**
     * Don't use this or I'll kick your ass
     */
    @ApiStatus.Internal
    public static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> getDataMapTypesMutable() {
        return dataMaps;
    }

    /**
     * {@return a view of all registered data maps}
     */
    public static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> getDataMaps() {
        return Collections.unmodifiableMap(dataMaps);
    }

    public static  <T, R> DataMapType<R, T> register(DataMapType.Builder<T, R> type) {
        return register(type.build());
    }


    public static  <T, R> DataMapType<R, T> register(DataMapType<R, T> type) {
        final var registry = type.registryKey();
        if (DynamicRegistriesImpl.DYNAMIC_REGISTRY_KEYS.stream().anyMatch(resourceKey -> resourceKey.equals(registry)) &&
                RegistryDataLoader.SYNCHRONIZED_REGISTRIES.stream().noneMatch(data -> data.key().equals(registry))) {
            throw new UnsupportedOperationException("Cannot register synced data map " + type.id() + " for datapack registry " + registry.location() + " that is not synced!");
        }

        final var map = dataMaps.computeIfAbsent((ResourceKey) registry, k -> new HashMap<>());
        if (map.containsKey(type.id())) {
            throw new IllegalArgumentException("Tried to register data map type with ID " + type.id() + " to registry " + registry.location() + " twice");
        }
        map.put(type.id(), type);

        return type;
    }
}
