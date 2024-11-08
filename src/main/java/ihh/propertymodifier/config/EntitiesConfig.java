package ihh.propertymodifier.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.comments.CommentMapCodec;
import ihh.propertymodifier.ConfigHelper;
import ihh.propertymodifier.ConfigResults;
import ihh.propertymodifier.PropertyModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.HashMap;
import java.util.Map;

public record EntitiesConfig(
        Map<String, Map<String, Float>> attributeBaseValues
) {
    public static final MapCodec<EntitiesConfig> CODEC = CommentMapCodec.of(RecordCodecBuilder.mapCodec(inst -> inst.group(
            ConfigHelper.codecStringMap(ConfigHelper.codecStringMap(Codec.FLOAT)).fieldOf("attribute_base_values").forGetter(EntitiesConfig::attributeBaseValues)
    ).apply(inst, EntitiesConfig::new)), Map.of(
            "attribute_base_values", """
                    Specify an entity to set the given attribute values for. Regex may be used for the attribute names as well. For example:
                    "minecraft:.*": {"minecraft:generic.max_health": 40}
                    This example will set the max health of all vanilla living entities to 40.
                    When modifying this map, the game must be restarted for the changes to take effect."""
    ));
    public static final EntitiesConfig DEFAULT = new EntitiesConfig(Map.of());
    
    @SuppressWarnings({"unused", "unchecked"})
    public void process() {
        ConfigHelper.processConfigMap(attributeBaseValues(), BuiltInRegistries.ENTITY_TYPE, (k, v) -> {
            try {
                EntityType<? extends LivingEntity> entity = (EntityType<? extends LivingEntity>) k;
                Map<Attribute, Float> map = new HashMap<>();
                ConfigHelper.processConfigMap(v, BuiltInRegistries.ATTRIBUTE, map::put, key -> "Key " + key + " for attribute_base_values in entities.json5 did not match any attributes");
                ConfigResults.ATTRIBUTE_BASE_VALUES.put(entity, map);
            } catch (ClassCastException e) {
                PropertyModifier.LOGGER.error("Entity {} is not a living entity, but was a match for a regex in attribute_base_values in entities.json5", BuiltInRegistries.ENTITY_TYPE.getId(k));
            }
        }, key -> "Key " + key + " for attribute_base_values in entities.json5 did not match any entities");
    }
}
