package ihh.propertymodifier;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConfigResults {
    public static final Map<Block,      Float>                  DESTROY_TIME = new HashMap<>();
    public static final Map<Block,      Float>                  EXPLOSION_RESISTANCE = new HashMap<>();
    public static final Map<Block,      Boolean>                REQUIRES_CORRECT_TOOL_FOR_DROPS = new HashMap<>();
    public static final Map<Block,      Boolean>                IS_RANDOMLY_TICKING = new HashMap<>();
    public static final Map<Block,      Boolean>                IGNITED_BY_LAVA = new HashMap<>();
    public static final Map<Block,      Boolean>                HAS_COLLISION = new HashMap<>();
    public static final Map<Block,      Float>                  FRICTION = new HashMap<>();
    public static final Map<Block,      Float>                  SPEED_FACTOR = new HashMap<>();
    public static final Map<Block,      Float>                  JUMP_FACTOR = new HashMap<>();
    public static final Map<BlockState, Integer>                LIGHT_LEVEL = new HashMap<>();
    public static final Map<Item,       List<DataComponentMap>> DEFAULT_COMPONENTS = new HashMap<>();
    public static final Map<Item,       Item>                   CRAFTING_REMAINDERS = new HashMap<>();
    public static final Map<Item,       Boolean>                REPAIRABLE = new HashMap<>();
}
