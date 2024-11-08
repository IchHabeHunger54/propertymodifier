package ihh.propertymodifier.mixin;

import ihh.propertymodifier.ConfigResults;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.neoforge.common.CommonHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(CommonHooks.class)
public class MixinCommonHooks {
    @Shadow @Final private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> FORGE_ATTRIBUTES;

    @SuppressWarnings("ConstantValue")
    @Inject(at = @At("TAIL"), method = "modifyAttributes()V")
    private static void propertyModifier$afterAttributeEvents(CallbackInfo ci) {
        for (Map.Entry<EntityType<? extends LivingEntity>, Map<Attribute, Float>> entry : ConfigResults.ATTRIBUTE_BASE_VALUES.entrySet()) {
            AttributeSupplier supplier = FORGE_ATTRIBUTES.get(entry.getKey());
            if (supplier == null) {
                supplier = DefaultAttributes.getSupplier(entry.getKey());
            }
            AttributeSupplier.Builder builder = supplier != null ? new AttributeSupplier.Builder(supplier) : new AttributeSupplier.Builder();
            for (Map.Entry<Attribute, Float> value : entry.getValue().entrySet()) {
                builder.add(BuiltInRegistries.ATTRIBUTE.wrapAsHolder(value.getKey()), value.getValue());
            }
            FORGE_ATTRIBUTES.put(entry.getKey(), builder.build());
        }
    }
}
