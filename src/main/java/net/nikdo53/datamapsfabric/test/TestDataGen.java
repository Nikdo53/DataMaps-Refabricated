package net.nikdo53.datamapsfabric.test;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.nikdo53.datamapsfabric.datagen.DataMapProvider;

import java.util.concurrent.CompletableFuture;

public class TestDataGen implements DataGeneratorEntrypoint{


    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(DataMaps::new);
    }

    public static class DataMaps extends DataMapProvider {

        protected DataMaps(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(packOutput, lookupProvider);
        }

        @Override
        protected void gather(HolderLookup.Provider provider) {
            super.gather(provider);

            var map = this.builder(TestDataMaps.TEST_DATA_MAP);
            map.add(ItemTags.AXES, "an axe", false);
            map.add(Items.LAPIS_LAZULI.builtInRegistryHolder(), "lapiuz lazul", false, ResourceConditions.allModsLoaded("some_mod_yeah"));
            map.add(Items.WOODEN_SWORD.builtInRegistryHolder(), "NO triple t 😡", false, ResourceConditions.not(ResourceConditions.anyModsLoaded("optifine", "some_other_mod")));


            var map2 = this.builder(TestDataMaps.ADADAD1);
            map2.add(ItemTags.IRON_ORES, "irroron", false);

            var map3 = this.builder(TestDataMaps.ADADAD2);
            map3.add(ItemTags.DIRT, "dirtt", false);

        }
    }

}
