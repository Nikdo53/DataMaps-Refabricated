package net.nikdo53.datamapsfabric.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.nikdo53.datamapsfabric.extensions.IConditionOpsExtension;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(RegistryOps.class)
public class RegistryOpsConditionsMixin implements IConditionOpsExtension {
    @Unique
    HolderLookup.Provider provider;

    @Override
    @Nullable
    public HolderLookup.Provider getProvider() {
        return provider;
    }

    @Override
    public void setProvider(HolderLookup.Provider provider) {
        this.provider = provider;
    }
}
