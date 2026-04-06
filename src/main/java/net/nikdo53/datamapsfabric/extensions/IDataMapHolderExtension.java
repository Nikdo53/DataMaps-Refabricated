package net.nikdo53.datamapsfabric.extensions;

import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import org.jetbrains.annotations.Nullable;

public interface IDataMapHolderExtension<T> {
    default  <A> @Nullable A getData(DataMapType<T, A> type){
        throw new IllegalStateException("not implemented");
    }
}
