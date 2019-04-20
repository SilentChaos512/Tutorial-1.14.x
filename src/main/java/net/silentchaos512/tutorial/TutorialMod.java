package net.silentchaos512.tutorial;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Main mod class, the entry point for our mod. I also use this for storing common constants, like
 * mod ID. Most initialization occurs in {@link SideProxy}. A couple of helpful methods are also
 * included in this class.
 * <p>
 * By the way, this is a Javadoc comment. Try putting your cursor on "TutorialMod" below and press
 * Ctrl+Q (Windows) if you are using IntelliJ. You can use this to get quick details on many classes
 * and methods.
 */
@Mod(TutorialMod.MOD_ID)
public class TutorialMod {
    /**
     * The mod ID, which should match the one in your mods.toml
     */
    public static final String MOD_ID = "tutorial";

    public static final Logger LOGGER = LogManager.getLogger();

    public TutorialMod() {
        // Create proxy instance. DistExecutor.runForDist also returns the created object, so you
        // could store that in a variable if you need it.
        // We cannot use method references here because it could load classes on invalid sides.
        //noinspection Convert2MethodRef
        DistExecutor.runForDist(
                () -> () -> new SideProxy.Client(),
                () -> () -> new SideProxy.Server()
        );
    }

    /**
     * Get the version of the mod. In a development environment, the version will always be "NONE".
     *
     * @return The version number, or NONE
     */
    @Nonnull
    public static String getVersion() {
        Optional<? extends ModContainer> o = ModList.get().getModContainerById(MOD_ID);
        if (o.isPresent()) {
            return o.get().getModInfo().getVersion().toString();
        }
        return "NONE";
    }

    /**
     * Determines if the mod is a dev build. Sometimes it is useful to register objects or event
     * handlers for debugging purposes, but you may not want these to make it into release builds.
     * <p>
     * This method is a bit naive, as it assumes that if the version is "NONE" we are in a
     * development environment. But it works. If you know a better way, let me know.
     *
     * @return True if this is a development environment, false otherwise.
     */
    public static boolean isDevBuild() {
        String version = getVersion();
        return "NONE".equals(version);
    }

    /**
     * Convenience method for creating {@link ResourceLocation}s. These are needed frequently, and
     * the namespace will typically be your mod ID. This creates a {@link ResourceLocation} with the
     * mod ID as the namespace and the given path. Note that this can throw an exception if the path
     * is invalid.
     *
     * @param path The path of the resource
     * @return A ResourceLocation equivalent to {@code "mod_id:path"}
     */
    @Nonnull
    public static ResourceLocation getId(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
