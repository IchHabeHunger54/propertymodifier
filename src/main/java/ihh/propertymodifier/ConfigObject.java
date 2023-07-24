package ihh.propertymodifier;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.NullObject;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraftforge.common.ForgeConfigSpec;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Helpers for creating configs and defining complex objects in configs.
 * Taken and adapted from <a href="https://github.com/Commoble/databuddy/blob/main/src/main/java/commoble/databuddy/config/ConfigHelper.java">Commoble's Databuddy library</a>.
 */
public class ConfigObject<T> implements Supplier<T> {
    private final ForgeConfigSpec.ConfigValue<Object> value;
    private final Codec<T> codec;
    private final T defaultValue;
    private Object cached;
    private T parsed;

    private ConfigObject(ForgeConfigSpec.ConfigValue<Object> value, Codec<T> codec, T defaultValue, Object cached) {
        this.value = value;
        this.codec = codec;
        this.defaultValue = parsed = defaultValue;
        this.cached = cached;
    }

    public static <T> ConfigObject<T> of(ForgeConfigSpec.Builder builder, String name, Codec<T> codec, T defaultValue) {
        Object o = codec.encodeStart(TomlConfigOps.INSTANCE, defaultValue).getOrThrow(false, e -> Logger.forceError("Unable to encode default value: " + e));
        return new ConfigObject<>(builder.define(name, o), codec, defaultValue, o);
    }

    @Override
    public T get() {
        Object freshValue = value.get();
        if (!Objects.equals(cached, freshValue)) {
            cached = freshValue;
            parsed = codec.parse(TomlConfigOps.INSTANCE, freshValue).get().map(e -> e, e -> {
                Logger.forceError("Using default config value due to parsing error: " + e.message());
                return defaultValue;
            });
        }
        return parsed;
    }

    private static class TomlConfigOps implements DynamicOps<Object> {
        public static TomlConfigOps INSTANCE = new TomlConfigOps();

        @Override
        public Object empty() {
            return NullObject.NULL_OBJECT;
        }

        @Override
        public <T> T convertTo(DynamicOps<T> ops, Object o) {
            if (o instanceof com.electronwill.nightconfig.core.Config) return this.convertMap(ops, o);
            if (o instanceof Collection) return this.convertList(ops, o);
            if (o == null || o instanceof NullObject) return ops.empty();
            if (o instanceof Enum<?> e) return ops.createString(e.name());
            if (o instanceof Temporal) return ops.createString(o.toString());
            if (o instanceof String s) return ops.createString(s);
            if (o instanceof Boolean b) return ops.createBoolean(b);
            if (o instanceof Number n) return ops.createNumeric(n);
            throw new UnsupportedOperationException("TomlConfigOps was unable to convert toml value: " + o);
        }

        @Override
        public DataResult<Number> getNumberValue(Object o) {
            return o instanceof Number n ? DataResult.success(n) : DataResult.error(() -> "Not a number: " + o);
        }

        @Override
        public Object createNumeric(Number n) {
            return n;
        }

        @Override
        public DataResult<Boolean> getBooleanValue(Object o) {
            return o instanceof Boolean b ? DataResult.success(b) : DataResult.error(() -> "Not a boolean: " + o);
        }

        @Override
        public Object createBoolean(boolean b) {
            return b;
        }

        @Override
        public DataResult<String> getStringValue(Object o) {
            return o instanceof com.electronwill.nightconfig.core.Config || o instanceof Collection ? DataResult.error(() -> "Not a string: " + o) : DataResult.success(String.valueOf(o));
        }

        @Override
        public Object createString(String s) {
            return s;
        }

        @Override
        public DataResult<Object> mergeToList(Object list, Object value) {
            if (!(list instanceof Collection) && list != empty()) return DataResult.error(() -> "mergeToList called with not a list: " + list, list);
            Collection<Object> result = new ArrayList<>();
            if (list != empty() && list instanceof Collection<?> collection) {
                result.addAll(collection);
            }
            result.add(value);
            return DataResult.success(result);
        }

        @Override
        public DataResult<Object> mergeToList(Object list, List<Object> values) {
            return DynamicOps.super.mergeToList(list, values).map(e -> e == empty() ? new ArrayList<>() : e);
        }

        @Override
        public Object createList(Stream<Object> s) {
            return s.toList();
        }

        @Override
        public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
            if (!(map instanceof com.electronwill.nightconfig.core.Config) && map != empty()) return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
            DataResult<String> dr = getStringValue(key);
            return dr.error().isPresent() ? DataResult.error(() -> "Key is not a string: " + key, map) : dr.flatMap(e -> {
                CommentedConfig result = TomlFormat.newConfig();
                if (map != empty() && map instanceof com.electronwill.nightconfig.core.Config config) {
                    result.addAll(config);
                }
                result.add(e, value);
                return DataResult.success(result);
            });
        }

        @Override
        public Object createMap(Stream<Pair<Object, Object>> s) {
            CommentedConfig result = TomlFormat.newConfig();
            s.forEach(e -> result.add(getStringValue(e.getFirst()).getOrThrow(false, p -> {}), e.getSecond()));
            return result;
        }

        @Override
        public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object o) {
            return o instanceof com.electronwill.nightconfig.core.Config config ? DataResult.success(config.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()))) : DataResult.error(() -> "Not a config: " + o);
        }

        @Override
        public DataResult<Stream<Object>> getStream(Object o) {
            return o instanceof Collection<?> collection ? DataResult.success(collection.stream().map(e -> e)) : DataResult.error(() -> "Not a collection: " + o);
        }

        @Override
        public Object remove(Object input, String key) {
            if (!(input instanceof com.electronwill.nightconfig.core.Config config)) return input;
            CommentedConfig result = TomlFormat.newConfig();
            config.entrySet().stream().filter(e -> !Objects.equals(e.getKey(), key)).forEach(e -> result.add(e.getKey(), e.getValue()));
            return result;
        }

        @Override
        public boolean compressMaps() {
            return false;
        }

        @Override
        public String toString() {
            return "TOML";
        }
    }
}
