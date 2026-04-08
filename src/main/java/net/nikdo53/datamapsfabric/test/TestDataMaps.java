package net.nikdo53.datamapsfabric.test;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.nikdo53.datamapsfabric.DataMapsRefabricated;
import net.nikdo53.datamapsfabric.datamaps.DataMapType;
import net.nikdo53.datamapsfabric.datamaps.DataMapsManager;

public class TestDataMaps {
    public static void init() {

    }

    public static final DataMapType<Item, String> TEST_DATA_MAP = DataMapType.builder(DataMapsRefabricated.loc("test"), Registries.ITEM, Codec.STRING).build();

    public static final DataMapType<Item, String> ADADAD1 = DataMapsManager.register(DataMapType.builder(DataMapsRefabricated.loc("adada1"), Registries.ITEM, Codec.STRING).build());
    public static final DataMapType<Item, String> ADADAD2 = DataMapsManager.register(DataMapType.builder(DataMapsRefabricated.loc("adada2"), Registries.ITEM, Codec.STRING).build());
    public static final DataMapType<Item, String> ADADAD3 = DataMapsManager.register(DataMapType.builder(DataMapsRefabricated.loc("adada3"), Registries.ITEM, Codec.STRING).build());

}
