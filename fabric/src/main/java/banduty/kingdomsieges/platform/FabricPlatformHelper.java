package banduty.kingdomsieges.platform;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.config.FabricKSConfigImpl;
import banduty.kingdomsieges.config.KSConfigImpl;
import banduty.kingdomsieges.platform.services.IPlatformHelper;
import banduty.stoneycore.networking.payload.SiegeYawS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

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

    @Override
    public void syncSiegeYawPitch(ServerPlayer player, float yaw, float pitch, float wheelRotation) {
        ServerPlayNetworking.send(player, new SiegeYawS2CPacket(yaw, pitch, wheelRotation));
    }
}
