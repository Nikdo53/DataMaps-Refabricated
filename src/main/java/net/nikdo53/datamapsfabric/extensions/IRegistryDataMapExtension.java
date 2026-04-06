package net.nikdo53.datamapsfabric.extensions;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;

public interface IRegistryDataMapExtension<T> extends IDataMapLookupExtension<T> {
    default Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> getDataMaps(){
        throw new IllegalStateException("not implemented");
    }

    @ApiStatus.Internal
    default void setDataMaps(Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> maps){
        throw new IllegalStateException("not implemented");
    }

    default <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type){
        throw new IllegalStateException("not implemented");
    }
}
