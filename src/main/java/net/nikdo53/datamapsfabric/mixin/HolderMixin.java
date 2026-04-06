package net.nikdo53.datamapsfabric.mixin;

import net.minecraft.core.Holder;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.extensions.IDataMapHolderExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Holder.class)
public interface HolderMixin<T> extends IDataMapHolderExtension<T> {
    @Override
    @Nullable
    default <A> A getData(DataMapType<T, A> type) {
        return IDataMapHolderExtension.super.getData(type);
    }
}
