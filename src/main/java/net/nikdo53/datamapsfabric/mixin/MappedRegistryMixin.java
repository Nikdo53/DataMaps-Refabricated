package net.nikdo53.datamapsfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.extensions.IRegistryDataMapExtension;
import net.nikdo53.datamapsfabric.wrappers.RegistryLookupWrapper;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements IRegistryDataMapExtension<T> {

    @Unique
    Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> neoBackports$dataMaps = new HashMap<>();

    @Override
    public Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> getDataMaps() {
        return neoBackports$dataMaps;
    }

    @Override
    public void setDataMaps(Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> maps) {
        neoBackports$dataMaps = maps;
    }

    @Override
    public <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
        return (Map<ResourceKey<T>, A>) neoBackports$dataMaps.getOrDefault(type, Map.of());
    }

    @Override
    public @Nullable <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
        final var innerMap = neoBackports$dataMaps.get(type);
        return innerMap == null ? null : (A) innerMap.get(key);
    }


/*    @WrapOperation(method = "<init>(Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Lifecycle;Z)V", at = @At(value = "FIELD", target = "Lnet/minecraft/core/MappedRegistry;lookup:Lnet/minecraft/core/HolderLookup$RegistryLookup;", opcode = Opcodes.PUTFIELD))
    private void wrapLookupField(MappedRegistry<T> instance, HolderLookup.RegistryLookup<T> value, Operation<Void> original){
        original.call(instance, new RegistryLookupWrapper<>(value, instance));
    }*/

    @WrapOperation(method = "holderOwner", at = @At(value = "FIELD", target = "Lnet/minecraft/core/MappedRegistry;lookup:Lnet/minecraft/core/HolderLookup$RegistryLookup;", opcode = Opcodes.GETFIELD))
    public HolderLookup.RegistryLookup<T> holderOwnerWrapper(MappedRegistry<T> instance, Operation<HolderLookup.RegistryLookup<T>> original) {
        return new RegistryLookupWrapper<>(original.call(instance), instance);
    }

    @WrapOperation(method = "asLookup", at = @At(value = "FIELD", target = "Lnet/minecraft/core/MappedRegistry;lookup:Lnet/minecraft/core/HolderLookup$RegistryLookup;", opcode = Opcodes.GETFIELD))
    public HolderLookup.RegistryLookup<T> asLookupWrapper(MappedRegistry<T> instance, Operation<HolderLookup.RegistryLookup<T>> original) {
        return new RegistryLookupWrapper<>(original.call(instance), instance);
    }

}
