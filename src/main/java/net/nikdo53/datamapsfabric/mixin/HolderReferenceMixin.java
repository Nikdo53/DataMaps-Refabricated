package net.nikdo53.datamapsfabric.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.extensions.IDataMapHolderExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Holder.Reference.class)
public abstract class HolderReferenceMixin<T> implements IDataMapHolderExtension<T> {

    @Shadow
    @Final
    private HolderOwner<T> owner;

    @Shadow
    public abstract ResourceKey<T> key();

    @Override
    public @Nullable <A> A getData(DataMapType<T, A> type) {
        if (owner instanceof HolderLookup.RegistryLookup<T> lookup) {
            return lookup.getData(type, key());
        }
        return null;
    }

}
