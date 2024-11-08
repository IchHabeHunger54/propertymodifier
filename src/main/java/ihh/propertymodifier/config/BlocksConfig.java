package ihh.propertymodifier.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.comments.CommentMapCodec;
import ihh.propertymodifier.ConfigHelper;
import ihh.propertymodifier.ConfigResults;
import ihh.propertymodifier.PropertyModifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public record BlocksConfig(
        Map<String, Float>   explosionResistance,
        Map<String, Float>   friction,
        Map<String, Float>   jumpFactor,
        Map<String, Float>   speedFactor,
        Map<String, Boolean> hasCollision,
        Map<String, Boolean> isRandomlyTicking,
        Map<String, Integer> lightLevel,
        Map<String, Float>   destroyTime,
        Map<String, Boolean> ignitedByLava,
        Map<String, Boolean> requiresCorrectToolForDrops
) {
    public static final MapCodec<BlocksConfig> CODEC = CommentMapCodec.of(RecordCodecBuilder.mapCodec(inst -> inst.group(
            ConfigHelper.codecStringMap(Codec.FLOAT).fieldOf("explosion_resistance")           .forGetter(BlocksConfig::explosionResistance),
            ConfigHelper.codecStringMap(Codec.FLOAT).fieldOf("friction")                       .forGetter(BlocksConfig::friction),
            ConfigHelper.codecStringMap(Codec.FLOAT).fieldOf("jump_factor")                    .forGetter(BlocksConfig::jumpFactor),
            ConfigHelper.codecStringMap(Codec.FLOAT).fieldOf("speed_factor")                   .forGetter(BlocksConfig::speedFactor),
            ConfigHelper.codecStringMap(Codec.BOOL) .fieldOf("has_collision")                  .forGetter(BlocksConfig::hasCollision),
            ConfigHelper.codecStringMap(Codec.BOOL) .fieldOf("is_randomly_ticking")            .forGetter(BlocksConfig::isRandomlyTicking),
            ConfigHelper.codecStringMap(Codec.INT)  .fieldOf("light_level")                    .forGetter(BlocksConfig::lightLevel),
            ConfigHelper.codecStringMap(Codec.FLOAT).fieldOf("destroy_time")                   .forGetter(BlocksConfig::destroyTime),
            ConfigHelper.codecStringMap(Codec.BOOL) .fieldOf("ignited_by_lava")                .forGetter(BlocksConfig::ignitedByLava),
            ConfigHelper.codecStringMap(Codec.BOOL) .fieldOf("requires_correct_tool_for_drops").forGetter(BlocksConfig::requiresCorrectToolForDrops)
    ).apply(inst, BlocksConfig::new)), Map.of(
            "explosion_resistance", """
                    Specify the explosion resistance of a block.
                    Dirt has 0.5, stone and has 6, obsidian has 1200. 3600000 or more makes the block completely explosion resistant. For example:
                    "minecraft:dirt": 1000
                    This example will make dirt nearly as explosion-proof as obsidian.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "friction", """
                    Specify the friction values of the block.
                    Default is 0.6, slime blocks use 0.8, and ice uses 0.98. For example:
                    "minecraft:stone": 0.98
                    This example will make walking on stone feel like walking on ice.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "jump_factor", """
                    Specify a modifier with which the jump height of players jumping while on the block will be multiplied.
                    Most blocks use 1 here, honey blocks use 0.5. For example:
                    "minecraft:cobblestone": 0.8
                    This example will make players no longer jump a full block while on cobblestone.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "speed_factor", """
                    Specify a modifier with which the movement speed of players walking over the block will be multiplied.
                    Most blocks use 1 here, honey blocks and soul sand use 0.4. For example:
                    "minecraft:cobblestone": 0.9
                    This example will make walking on cobblestone slightly slower.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "has_collision", """
                    Specify whether the block has collision or not.
                    By default, stone and dirt have collision, while torches and tall grass do not. For example:
                    "minecraft:.*_sapling": true
                    This example will make saplings block the player's movement.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "is_randomly_ticking", """
                    Specify whether the block is randomly ticking.
                    Random ticking is used by e.g. crops to grow, or ice to melt. For example:
                    "minecraft:snow": false
                    This example will make snow layers not melt.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "light_level", """
                    Specify the light level. Must be between 0 and 15. This can be modified for blocks, or for single block states only. For example:
                    "minecraft:redstone_torch[lit=true]": 15
                    This example will make powered redstone torches give off the same light level as glowstone.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "destroy_time", """
                    Specify the destroy time of a block, i.e. how long it takes to break the specified block.
                    Dirt has 0.5, stone has 1.5, obsidian has 50. -1 makes the block unbreakable. For example:
                    "minecraft:.*_planks": 50
                    This example will make all vanilla planks require the same time as obsidian to be broken.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "ignited_by_lava", """
                    Specify whether the block can be naturally ignited by lava. For example:
                    "minecraft:.*_log": false
                    This example will make logs not burn when next to e.g. a lava lake.
                    When modifying this map, the game must be restarted for the changes to take effect.""",
            "requires_correct_tool_for_drops", """
                    Specify whether a tool must be used for the block to drop, or if using a tool just makes breaking faster. For example:
                    "minecraft:diamond_ore": false
                    This example will make diamond ore drop diamonds when broken by hand or with any tool.
                    When modifying this map, the game must be restarted for the changes to take effect."""
    ));
    public static final BlocksConfig DEFAULT = new BlocksConfig(Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of(), Map.of());

    public void process() {
        processBlock("explosion_resistance", explosionResistance(), ConfigResults.EXPLOSION_RESISTANCE::put);
        processBlock("friction",             friction(),            ConfigResults.FRICTION::put);
        processBlock("jump_factor",          jumpFactor(),          ConfigResults.JUMP_FACTOR::put);
        processBlock("speed_factor",         speedFactor(),         ConfigResults.SPEED_FACTOR::put);
        processBlock("has_collision",        hasCollision(),        ConfigResults.HAS_COLLISION::put);
        processBlock("is_randomly_ticking",  isRandomlyTicking(),   ConfigResults.IS_RANDOMLY_TICKING::put);
        processBlockState("destroy_time",                    destroyTime(),                 ConfigResults.DESTROY_TIME::put);
        processBlockState("light_level",                     lightLevel(),                  ConfigResults.LIGHT_LEVEL::put);
        processBlockState("ignited_by_lava",                 ignitedByLava(),               ConfigResults.IGNITED_BY_LAVA::put);
        processBlockState("requires_correct_tool_for_drops", requiresCorrectToolForDrops(), ConfigResults.REQUIRES_CORRECT_TOOL_FOR_DROPS::put);
    }

    private static <T> void processBlock(String configName, Map<String, T> configValue, BiConsumer<Block, T> consumer) {
        ConfigHelper.processConfigMap(configValue, BuiltInRegistries.BLOCK, consumer, key -> "Key " + key + " for " + configName + " in blocks.json5 did not match any blocks");
    }

    private static <T> void processBlockState(String configName, Map<String, T> configValue, BiConsumer<BlockState, T> consumer) {
        Map<BlockState, T> blockStates = new HashMap<>();
        for (Map.Entry<String, T> entry : configValue.entrySet()) {
            Map<BlockState, T> temp = new HashMap<>();
            String key = entry.getKey();
            T value = entry.getValue();
            ConfigHelper.resolveRegex(key, BuiltInRegistries.BLOCK)
                    .stream()
                    .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                    .forEach(state -> temp.put(state, value));
            ConfigHelper.resolveBlockStatesRegex(key)
                    .forEach(state -> temp.put(state, value));
            if (temp.isEmpty()) {
                PropertyModifier.LOGGER.error("Key {} for {} in blocks.json5 did not match any blocks", configName, configName);
            }
            blockStates.putAll(temp);
        }
        blockStates.forEach(consumer);
    }
}
