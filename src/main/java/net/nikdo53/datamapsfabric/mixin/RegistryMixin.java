package net.nikdo53.datamapsfabric.mixin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.extensions.IRegistryDataMapExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Map;

@Mixin(Registry.class)
public interface RegistryMixin<T> extends IRegistryDataMapExtension<T> {
    @Override
    default Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> getDataMaps() {
        return IRegistryDataMapExtension.super.getDataMaps();
    }

    @Override
    default void setDataMaps(Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> maps) {
        IRegistryDataMapExtension.super.setDataMaps(maps);
    }

    @Override
    default <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
        return IRegistryDataMapExtension.super.getDataMap(type);
    }

    @Override
    @Nullable
    default <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        return IRegistryDataMapExtension.super.getData(type, key);
    }
}
