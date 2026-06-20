package banduty.kingdomsieges.platform;

import banduty.kingdomsieges.Kingdomsieges;
import banduty.kingdomsieges.config.NeoForgeKSConfigImpl;
import banduty.kingdomsieges.config.KSConfigImpl;
import banduty.kingdomsieges.platform.services.IPlatformHelper;
import banduty.stoneycore.networking.payload.SiegeYawS2CPacket;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgePlatformHelper implements IPlatformHelper {
    private static final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> REGISTRIES = new HashMap<>();

    @Override
    public String getPlatformName() {

        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {

        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {

        return !FMLLoader.isProduction();
    }

    @Override
    public KSConfigImpl getConfig() {
        return new NeoForgeKSConfigImpl();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Supplier<T> register(Registry<T> registry, String name, Supplier<T> entry) {
        DeferredRegister<T> deferredRegister = (DeferredRegister<T>) REGISTRIES.computeIfAbsent(
                registry.key(),
                k -> DeferredRegister.create(registry.key(), Kingdomsieges.MOD_ID)
        );

        return deferredRegister.register(name, entry);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Holder<T> registerHolder(ResourceKey<Registry<T>> registryKey, String name, Supplier<T> value) {
        DeferredRegister<T> deferredRegister = (DeferredRegister<T>) REGISTRIES.computeIfAbsent(
                registryKey,
                k -> DeferredRegister.create(registryKey, Kingdomsieges.MOD_ID)
        );

        return deferredRegister.register(name, value);
    }

    @Override
    public void syncSiegeYawPitch(ServerPlayer player, float yaw, float pitch, float wheelRotation) {
        PacketDistributor.sendToPlayer(player, new SiegeYawS2CPacket(yaw, pitch, wheelRotation));
    }

    public static void registerRegistries(IEventBus eventBus) {
        REGISTRIES.values().forEach(dr -> dr.register(eventBus));
    }
}