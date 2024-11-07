package ihh.propertymodifier;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;

@Mod(PropertyModifier.MOD_ID)
public final class PropertyModifier {
    public static final String MOD_ID = "propertymodifier";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Jankson JANKSON = new Jankson.Builder().build();

    public PropertyModifier() {
        try {
            ConfigBootstrap.init();
        } catch (IOException e) {
            LOGGER.error("Property Modifier failed to load config files due to exception", e);
        } catch (SyntaxError e) {
            LOGGER.error("Property Modifier encountered a malformed JSON5 file", e);
        }
    }
}
