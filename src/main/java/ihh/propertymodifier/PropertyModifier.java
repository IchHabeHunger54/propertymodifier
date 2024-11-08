package ihh.propertymodifier;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.api.SyntaxError;
import com.mojang.logging.LogUtils;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Mod(PropertyModifier.MOD_ID)
public final class PropertyModifier {
    public static final String MOD_ID = "propertymodifier";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Jankson JANKSON = new Jankson.Builder().build();

    public PropertyModifier(IEventBus bus) {
        bus.addListener(EventPriority.LOWEST, EntityAttributeModificationEvent.class, PropertyModifier::entityAttributeModification);
        bus.addListener(EventPriority.LOWEST, ModifyDefaultComponentsEvent.class,     PropertyModifier::modifyDefaultComponents);
        bus.addListener(EventPriority.LOWEST, FMLLoadCompleteEvent.class,             PropertyModifier::loadComplete);
    }

    // This is the only correct time we can populate the config at. Yes, I know it is cursed.
    private static void entityAttributeModification(EntityAttributeModificationEvent event) {
        try {
            ConfigBootstrap.init();
            ConfigResults.apply();
        } catch (IOException e) {
            LOGGER.error("Property Modifier failed to load config files due to exception", e);
        } catch (SyntaxError e) {
            LOGGER.error("Property Modifier encountered a malformed JSON5 file", e);
        }
    }
    
    private static void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        for (Map.Entry<Item, List<DataComponentMap>> entry : ConfigResults.DEFAULT_COMPONENTS.entrySet()) {
            Item item = entry.getKey();
            List<DataComponentMap> list = entry.getValue();
            event.modify(item, builder -> list.stream().flatMap(DataComponentMap::stream).forEach(builder::set));
        }
    }
    
    private static void loadComplete(FMLLoadCompleteEvent event) {
        ConfigResults.CORE_CONFIG.run();
    }
}
