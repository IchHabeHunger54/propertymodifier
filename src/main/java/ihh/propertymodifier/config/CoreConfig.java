package ihh.propertymodifier.config;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.lukebemish.codecextras.comments.CommentMapCodec;
import ihh.propertymodifier.ConfigException;
import ihh.propertymodifier.ConfigHelper;
import ihh.propertymodifier.ConfigResults;
import ihh.propertymodifier.PropertyModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;

import java.util.Map;
import java.util.function.Function;

public record CoreConfig(boolean logBlocks, boolean logItems, boolean logEntities) {
    public static final MapCodec<CoreConfig> CODEC = CommentMapCodec.of(RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.BOOL.fieldOf("log_blocks")  .forGetter(CoreConfig::logBlocks),
            Codec.BOOL.fieldOf("log_items")   .forGetter(CoreConfig::logItems),
            Codec.BOOL.fieldOf("log_entities").forGetter(CoreConfig::logEntities)
    ).apply(inst, CoreConfig::new)), Map.of(
            "log_blocks", """
                    Whether to print all blocks' properties or not. This will only log non-default properties.
                    Default property values are:
                    - Explosion Resistance: 0
                    - Friction: 0.6
                    - Jump Factor: 1
                    - Speed Factor: 1
                    - Has Collision: true
                    - Is Randomly Ticking: false
                    - Light Level: 0 (printed per-blockstate if not the same for all blockstates)
                    - Destroy Time: 0 (printed per-blockstate if not the same for all blockstates)
                    - Ignited By Lava: false (printed per-blockstate if not the same for all blockstates)
                    - Requires Correct Tool To Drop: false (printed per-blockstate if not the same for all blockstates)
                    """,
            "log_items", """
                    Whether to print all items' properties or not. This will only log non-default data components.
                    Default data components include: {
                        "minecraft:max_stack_size": 64,
                        "minecraft:lore": [],
                        "minecraft:enchantments": {
                            "levels": {},
                            "show_in_tooltip": true
                        },
                        "minecraft:repair_cost": 0,
                        "minecraft:attribute_modifiers": {
                            "modifiers": [],
                            "show_in_tooltip": true
                        },
                        "minecraft:rarity": "common"
                    }
                    """,
            "log_entities", """
                    Whether to print all entities' properties or not.
                    """
    ));
    public static final CoreConfig DEFAULT = new CoreConfig(false, false, false);

    public void process() {
        ConfigResults.CORE_CONFIG = this;
    }

    @SuppressWarnings({"DataFlowIssue", "unchecked", "ConstantValue"})
    public void run() {
        if (logBlocks) {
            PropertyModifier.LOGGER.info("Block Properties:");
            for (Block block : BuiltInRegistries.BLOCK) {
                String name = BuiltInRegistries.BLOCK.getKey(block).toString();
                StringBuilder sb = new StringBuilder(name);
                sb.append(": ");
                if (block.explosionResistance != 0) {
                    sb.append("Explosion Resistance: ");
                    sb.append(block.explosionResistance);
                    sb.append(", ");
                }
                if (block.friction != 0.6f) {
                    sb.append("Friction: ");
                    sb.append(block.friction);
                    sb.append(", ");
                }
                if (block.jumpFactor != 1) {
                    sb.append("Jump Factor: ");
                    sb.append(block.jumpFactor);
                    sb.append(", ");
                }
                if (block.speedFactor != 1) {
                    sb.append("Speed Factor: ");
                    sb.append(block.speedFactor);
                    sb.append(", ");
                }
                if (!block.hasCollision) {
                    sb.append("Has Collision: false, ");
                }
                if (block.isRandomlyTicking) {
                    sb.append("Is Randomly Ticking: true, ");
                }
                appendBlockState(block, b -> b.lightEmission, 0, s -> "Light Level: " + s + ", ", sb);
                appendBlockState(block, b -> b.destroySpeed, 0f, s -> "Destroy Time: " + s + ", ", sb);
                appendBlockState(block, b -> b.ignitedByLava, false, s -> "Ignited By Lava: " + s + ", ", sb);
                appendBlockState(block, b -> b.requiresCorrectToolForDrops, false, s -> "Requires Correct Tool For Drops: " + s + ", ", sb);
                PropertyModifier.LOGGER.info(sb.substring(0, sb.length() - 2));
            }
        }
        if (logItems) {
            PropertyModifier.LOGGER.info("Item Properties:");
            for (Item item : BuiltInRegistries.ITEM) {
                StringBuilder sb = new StringBuilder(BuiltInRegistries.ITEM.getKey(item).toString());
                sb.append(": ");
                ItemStack stack = new ItemStack(item);
                if (stack.has(DataComponents.MAX_STACK_SIZE) && stack.get(DataComponents.MAX_STACK_SIZE) == 64) {
                    stack.remove(DataComponents.MAX_STACK_SIZE);
                }
                if (stack.has(DataComponents.LORE) && stack.get(DataComponents.LORE) == ItemLore.EMPTY) {
                    stack.remove(DataComponents.LORE);
                }
                if (stack.has(DataComponents.ENCHANTMENTS) && stack.get(DataComponents.ENCHANTMENTS) == ItemEnchantments.EMPTY) {
                    stack.remove(DataComponents.ENCHANTMENTS);
                }
                if (stack.has(DataComponents.REPAIR_COST) && stack.get(DataComponents.REPAIR_COST) == 0) {
                    stack.remove(DataComponents.REPAIR_COST);
                }
                if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS) && stack.get(DataComponents.ATTRIBUTE_MODIFIERS) == ItemAttributeModifiers.EMPTY) {
                    stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
                }
                if (stack.has(DataComponents.RARITY) && stack.get(DataComponents.RARITY) == Rarity.COMMON) {
                    stack.remove(DataComponents.RARITY);
                }
                if (!stack.getComponents().isEmpty()) {
                    try {
                        String json = ConfigHelper.encodeJson(DataComponentMap.CODEC, stack.getComponents()).toJson();
                        sb.append("Default Components: ");
                        sb.append(json);
                        sb.append(", ");
                    } catch (ConfigException e) {
                        PropertyModifier.LOGGER.error("Failed to append default components for item {}. Please look them up in-game using /neoforge data_components list", BuiltInRegistries.ITEM.getKey(item));
                    }
                }
                if (stack.getCraftingRemainingItem() != ItemStack.EMPTY) {
                    sb.append("Crafting Remaining Item: ");
                    sb.append(stack.getCraftingRemainingItem().getItemHolder().getKey().location());
                    sb.append(", ");
                }
                PropertyModifier.LOGGER.info(sb.substring(0, sb.length() - 2));
            }
        }
        if (logEntities) {
            PropertyModifier.LOGGER.info("Entity Properties:");
            for (EntityType<?> entity : BuiltInRegistries.ENTITY_TYPE) {
                try {
                    EntityType<? extends LivingEntity> livingEntity = (EntityType<? extends LivingEntity>) entity;
                    AttributeSupplier attributeSupplier = DefaultAttributes.getSupplier(livingEntity);
                    if (attributeSupplier == null) continue;
                    StringBuilder sb = new StringBuilder(BuiltInRegistries.ENTITY_TYPE.getKey(entity).toString());
                    sb.append(": ");
                    for (Attribute attribute : BuiltInRegistries.ATTRIBUTE) {
                        Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE.wrapAsHolder(attribute);
                        if (!attributeSupplier.hasAttribute(holder)) continue;
                        double value = attributeSupplier.getValue(holder);
                        if (value == attribute.getDefaultValue()) continue;
                        sb.append(BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString());
                        sb.append(": ");
                        sb.append(value);
                        sb.append(", ");
                    }
                    PropertyModifier.LOGGER.info(sb.substring(0, sb.length() - 2));
                } catch (ClassCastException ignored) {
                }
            }
        }
    }
    
    private <T> void appendBlockState(Block block, Function<BlockState, T> accessor, T globalDefaultValue, Function<String, String> formatter, StringBuilder builder) {
        T defaultValue = accessor.apply(block.defaultBlockState());
        ImmutableList<BlockState> states = block.getStateDefinition().getPossibleStates();
        if (states.size() == 1) {
            if (!defaultValue.equals(globalDefaultValue)) {
                builder.append(formatter.apply(String.valueOf(defaultValue)));
            }
            return;
        }
        if (states.stream().map(accessor).allMatch(e -> e.equals(defaultValue))) {
            if (!defaultValue.equals(globalDefaultValue)) {
                builder.append(formatter.apply(String.valueOf(defaultValue)));
            }
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < states.size(); i++) {
                if (i > 0) {
                    sb.append(",");
                }
                BlockState state = states.get(i);
                sb.append(state.toString().substring(BuiltInRegistries.BLOCK.getKey(block).toString().length() + 7));
                sb.append("=");
                sb.append(accessor.apply(state));
            }
            builder.append(formatter.apply(sb.toString()));
        }
    }
}
