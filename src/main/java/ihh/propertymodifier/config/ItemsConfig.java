package ihh.propertymodifier.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.comments.CommentMapCodec;
import ihh.propertymodifier.ConfigHelper;
import ihh.propertymodifier.ConfigResults;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

public record ItemsConfig(
        Map<String, DataComponentMap> defaultComponents,
        Map<String, Item>             craftingRemainders,
        Map<String, Boolean>          repairable
) {
    public static final MapCodec<ItemsConfig> CODEC = CommentMapCodec.of(RecordCodecBuilder.mapCodec(inst -> inst.group(
            ConfigHelper.codecStringMap(DataComponentMap.CODEC)              .fieldOf("default_components") .forGetter(ItemsConfig::defaultComponents),
            ConfigHelper.codecStringMap(BuiltInRegistries.ITEM.byNameCodec()).fieldOf("crafting_remainders").forGetter(ItemsConfig::craftingRemainders),
            ConfigHelper.codecStringMap(Codec.BOOL)                          .fieldOf("repairable")         .forGetter(ItemsConfig::repairable)
    ).apply(inst, ItemsConfig::new)), Map.of(
            "default_components", """
                    Specify the components of items here, using the item name (or a regex for item names) as the key. For example:
                    ".*": {"minecraft:max_stack_size": 64}
                    This example will make all (non-damageable) items stackable to 64. Be aware that this will be merged with existing components, overwriting the values where applicable.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "crafting_remainders", """
                    Specify crafting remainder items for other items here, using the item name (or a regex for item names) as the key. For example:
                    "minecraft:.*_(soup|stew)": "minecraft:bowl"
                    This example will make all of Minecraft's soup and stew items return a bowl if used in a crafting table.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "repairable", """
                    Specify whether items are repairable or not here, using the item name (or a regex for item names) as the key. For example:
                    "minecraft:trident": false
                    This example will make tridents unrepairable. Note: This currently cannot be used to make un-repairable items repairable.
                    When modifying this map, the game must be restarted for the changes to take effect."""
    ));
    public static final ItemsConfig DEFAULT = new ItemsConfig(Map.of(), Map.of(), Map.of());

    public void process() {
        process("default_components",  defaultComponents(),  (item, value) -> ConfigResults.DEFAULT_COMPONENTS.getOrDefault(item, new ArrayList<>()).add(value));
        process("crafting_remainders", craftingRemainders(), ConfigResults.CRAFTING_REMAINDERS::put);
        process("repairable",          repairable(),         (item, value) -> {
            if (!value) {
                ConfigResults.REPAIRABLE.put(item, false);
            }
        });
    }

    private static <T> void process(String configName, Map<String, T> configValue, BiConsumer<Item, T> consumer) {
        ConfigHelper.processConfigMap(configValue, BuiltInRegistries.ITEM, consumer, key -> "Key " + key + " for " + configName + " in items.json did not match any items");
    }
}
