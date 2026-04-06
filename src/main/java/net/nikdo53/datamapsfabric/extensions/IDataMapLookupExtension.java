package net.nikdo53.datamapsfabric.extensions;

import net.minecraft.resources.ResourceKey;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

public interface IDataMapLookupExtension<T> {
    default  <A> @Nullable A getData(DataMapType<T, A> type, ResourceKey<T> key){
        throw new IllegalStateException("not implemented");
    }
}
