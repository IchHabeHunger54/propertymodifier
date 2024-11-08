package ihh.propertymodifier;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigResults {
    public static final Map<Block,      Float>                  EXPLOSION_RESISTANCE = new HashMap<>();
    public static final Map<Block,      Float>                  FRICTION = new HashMap<>();
    public static final Map<Block,      Float>                  JUMP_FACTOR = new HashMap<>();
    public static final Map<Block,      Float>                  SPEED_FACTOR = new HashMap<>();
    public static final Map<Block,      Boolean>                HAS_COLLISION = new HashMap<>();
    public static final Map<Block,      Boolean>                IS_RANDOMLY_TICKING = new HashMap<>();
    public static final Map<BlockState, Integer>                LIGHT_LEVEL = new HashMap<>();
    public static final Map<BlockState, Float>                  DESTROY_TIME = new HashMap<>();
    public static final Map<BlockState, Boolean>                IGNITED_BY_LAVA = new HashMap<>();
    public static final Map<BlockState, Boolean>                REQUIRES_CORRECT_TOOL_FOR_DROPS = new HashMap<>();
    public static final Map<Item,       List<DataComponentMap>> DEFAULT_COMPONENTS = new HashMap<>();
    public static final Map<Item,       Item>                   CRAFTING_REMAINDERS = new HashMap<>();

    public static void apply() {
        EXPLOSION_RESISTANCE.forEach((block, f) -> {
            block.explosionResistance = f;
            block.properties().explosionResistance(f);
        });
        FRICTION.forEach((block, f) -> {
            block.friction = f;
            block.properties().friction(f);
        });
        JUMP_FACTOR.forEach((block, f) -> {
            block.jumpFactor = f;
            block.properties().jumpFactor(f);
        });
        SPEED_FACTOR.forEach((block, f) -> {
            block.speedFactor = f;
            block.properties().speedFactor(f);
        });
        HAS_COLLISION.forEach((block, b) -> {
            block.hasCollision = b;
            block.properties().hasCollision = b;
        });
        IS_RANDOMLY_TICKING.forEach((block, b) -> {
            block.isRandomlyTicking = b;
            block.properties().isRandomlyTicking = b;
        });
        LIGHT_LEVEL.forEach((state, i) -> {
            state.lightEmission = i;
            state.getBlock().properties().lightLevel(e -> e == state ? i : e.getBlock().properties().lightEmission.applyAsInt(e));
        });
        DESTROY_TIME.forEach((state, f) -> {
            state.destroySpeed = f;
            if (state == state.getBlock().defaultBlockState()) {
                state.getBlock().properties().destroyTime(f);
            }
        });
        IGNITED_BY_LAVA.forEach((state, b) -> {
            state.ignitedByLava = b;
            if (state == state.getBlock().defaultBlockState()) {
                state.getBlock().properties().ignitedByLava = b;
            }
        });
        REQUIRES_CORRECT_TOOL_FOR_DROPS.forEach((state, b) -> {
            state.requiresCorrectToolForDrops = b;
            if (state == state.getBlock().defaultBlockState()) {
                state.getBlock().properties().requiresCorrectToolForDrops = b;
            }
        });
        CRAFTING_REMAINDERS.forEach((item, remainder) -> item.craftingRemainingItem = remainder);
    }
}
