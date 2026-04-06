package net.nikdo53.datamapsfabric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.extensions.IDataMapLookupExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;


@Mixin(HolderLookup.RegistryLookup.class)
public interface RegistryLookupMixin<T> extends IDataMapLookupExtension<T> {
    @Override
    @Nullable
    default <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        return IDataMapLookupExtension.super.getData(type, key);
    }
}


