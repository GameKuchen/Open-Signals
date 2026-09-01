package com.troblecodings.signals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import com.troblecodings.contentpacklib.ContentPackHandler;
import com.troblecodings.core.net.NetworkHandler;
import com.troblecodings.guilib.ecs.GuiHandler;
import com.troblecodings.signals.config.ConfigHandler;
import com.troblecodings.signals.handler.NameHandler;
import com.troblecodings.signals.handler.SignalBoxHandler;
import com.troblecodings.signals.init.OSBlocks;
import com.troblecodings.signals.init.OSItems;
import com.troblecodings.signals.init.OSModels;
import com.troblecodings.signals.init.OSSounds;
import com.troblecodings.signals.proxy.ClientProxy;
import com.troblecodings.signals.proxy.CommonProxy;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.DistExecutor;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;

@Mod(OpenSignalsMain.MODID)
public class OpenSignalsMain {

    public static final String MODID = "opensignals";
    private static OpenSignalsMain instance;

    public static OpenSignalsMain getInstance() {
        return instance;
    }

    public OpenSignalsMain() {
        instance = this;
        final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        eventBus.register(OSBlocks.class);
        eventBus.register(OSItems.class);
        eventBus.register(OSSounds.class);
        NeoForge.EVENT_BUS.register(NameHandler.class);
        NeoForge.EVENT_BUS.register(SignalBoxHandler.class);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHandler.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHandler.GENERAL_SPEC);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> eventBus.register(OSModels.class));
    }

    public static CommonProxy proxy = DistExecutor.safeRunForDist(() -> ClientProxy::new,
            () -> CommonProxy::new);
    public static GuiHandler handler = null;
    public static NetworkHandler network = null;
    public static ContentPackHandler contentPacks = null;
    private static boolean debug;
    private static Logger log = null;

    /**
     * @return the debug
     */
    public static boolean isDebug() {
        return debug;
    }

    public IModFile file;

    @SubscribeEvent
    public void preInit(final FMLConstructModEvent event) {
        debug = false;
        log = LoggerContext.getContext().getLogger(MODID);
        final IModInfo modInfo = ModLoadingContext.get().getActiveContainer().getModInfo();
        file = modInfo.getOwningFile().getFile();
        contentPacks = new ContentPackHandler(MODID, "assets/" + MODID, log,
                name -> file.findResource(name));
        proxy.initModEvent(event);
    }

    @SubscribeEvent
    public void client(final FMLClientSetupEvent event) {
        OSBlocks.BLOCKS_TO_REGISTER.forEach(block -> {
            ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutoutMipped());
        });
    }

    @SubscribeEvent
    public void init(final FMLCommonSetupEvent event) {
        proxy.preinit(event);
    }

    public static Logger getLogger() {
        if (log == null)
            log = LogManager.getLogger(MODID);
        return log;
    }

    public static void exitMinecraftWithMessage(final String message) {
        getLogger().error(message);
        System.exit(0);
    }
}
