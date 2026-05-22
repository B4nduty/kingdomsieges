package banduty.kingdomsieges.platform.services;

import banduty.kingdomsieges.config.KSConfigImpl;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public interface IPlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {

        return isDevelopmentEnvironment() ? "development" : "production";
    }

    KSConfigImpl getConfig();

    <T> Supplier<T> register(Registry<T> registry, String name, Supplier<T> entry);

    <T> Holder<T> registerHolder(ResourceKey<Registry<T>> registryKey, String name, Supplier<T> value);

    void syncSiegeYawPitch(ServerPlayer player, float yaw, float pitch, float wheelRotation);
}