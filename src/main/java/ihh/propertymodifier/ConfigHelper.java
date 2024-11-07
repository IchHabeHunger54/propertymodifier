package ihh.propertymodifier;

import blue.endless.jankson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import dev.lukebemish.codecextras.compat.jankson.JanksonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class ConfigHelper {
    public static <T> JsonElement encodeJson(MapCodec<T> codec, T input) {
        return codec.codec().encodeStart(JanksonOps.COMMENTED, input).getOrThrow();
    }

    public static <T> T decodeJson(MapCodec<T> codec, JsonElement input) {
        return codec.codec().decode(JanksonOps.COMMENTED, input).map(Pair::getFirst).getOrThrow();
    }

    public static <T> UnboundedMapCodec<String, T> codecStringMap(Codec<T> codec) {
        return Codec.unboundedMap(Codec.STRING, codec);
    }

    public static <R, T> void processConfigMap(Map<String, T> configValue, Registry<R> registry, BiConsumer<R, T> consumer, @Nullable Function<String, String> emptyListMessage) {
        processConfigMap(configValue, key -> resolveRegex(key, registry), consumer, emptyListMessage);
    }

    public static <R, T> void processConfigMap(Map<String, T> configValue, Function<String, List<R>> registryGetter, BiConsumer<R, T> consumer, @Nullable Function<String, String> emptyListMessage) {
        for (Map.Entry<String, T> entry : configValue.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            List<R> list = registryGetter.apply(key);
            if (list.isEmpty() && emptyListMessage != null) {
                PropertyModifier.LOGGER.warn(emptyListMessage.apply(key));
            }
            for (R r : list) {
                consumer.accept(r, value);
            }
        }
    }

    public static <T> List<T> resolveRegex(String regex, Registry<T> registry) {
        return registry.stream()
                .map(registry::getKey)
                .filter(Objects::nonNull)
                .filter(e -> e.toString().matches(regex))
                .map(registry::get)
                .toList();
    }
    
    public static List<BlockState> resolveBlockStatesRegex(String regex) {
        return BuiltInRegistries.BLOCK.stream()
                .map(block -> block.getStateDefinition().getPossibleStates())
                .flatMap(Collection::stream)
                .filter(state -> state.toString().matches(regex))
                .toList();
    }
}
