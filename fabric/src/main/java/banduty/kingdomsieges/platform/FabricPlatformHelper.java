package banduty.kingdomsieges.platform;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.config.FabricKSConfigImpl;
import banduty.kingdomsieges.config.KSConfigImpl;
import banduty.kingdomsieges.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public KSConfigImpl getConfig() {
        return new FabricKSConfigImpl();
    }

    @Override
    public <T> Supplier<T> register(Registry<T> registry, String name, Supplier<T> entry) {
        T result = Registry.register(registry, ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, name), entry.get());
        return () -> result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Holder<T> registerHolder(ResourceKey<Registry<T>> registryKey, String name, Supplier<T> value) {
        return Registry.registerForHolder(
                (Registry<T>) BuiltInRegistries.REGISTRY.get(registryKey.location()),
                ResourceLocation.fromNamespaceAndPath(Kingdomsieges.MOD_ID, name),
                value.get()
        );
    }
}
