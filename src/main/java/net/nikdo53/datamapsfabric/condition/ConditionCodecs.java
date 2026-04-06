package net.nikdo53.datamapsfabric.condition;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;

import java.util.List;

public class ConditionCodecs {
    public static final Codec<ResourceCondition> CODEC = ResourceCondition.CODEC;
    public static final Codec<List<ResourceCondition>> LIST_CODEC = CODEC.listOf();
}