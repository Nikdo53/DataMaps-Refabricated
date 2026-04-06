package net.nikdo53.datamapsfabric.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.tags.TagManager;
import net.nikdo53.datamapsfabric.datamaps.DataMapLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class AddReloadableServerResourceMixin {
    @Shadow
    public abstract ReloadableServerRegistries.Holder fullRegistries();

    @Unique
    private static DataMapLoader DATA_MAPS;

    @WrapMethod(method = "listeners")
    public List<PreparableReloadListener> listeners(Operation<List<PreparableReloadListener>> original) {
        ArrayList<PreparableReloadListener> list = new ArrayList<>(original.call());
        DATA_MAPS = new DataMapLoader(fullRegistries().get());
        list.add(DATA_MAPS);
        return list;
    }

    @Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
    private static void updateRegistryTags(CallbackInfo ci) {
        if (DATA_MAPS != null) {
            DATA_MAPS.apply();
        }
    }

}
