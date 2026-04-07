package net.nikdo53.datamapsfabric.extensions;

import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.Nullable;

public interface IConditionOpsExtension {
    @Nullable HolderLookup.Provider getProvider();
    void setProvider(HolderLookup.Provider provider);

}
