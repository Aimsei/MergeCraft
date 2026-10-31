package fr.aimsei.mergecraft;

import fr.aimsei.mergecraft.common.item.ModCreativeModTabs;
import fr.aimsei.mergecraft.common.item.ModItems;
import fr.aimsei.mergecraft.common.loot.MergeLevelCondition;
import fr.aimsei.mergecraft.common.merge.MergeManager;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
@Mod(MergeCraft.MODID)
public class MergeCraft {
    public static final String MODID = "mergecraft";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MergeCraft(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(MergeManager::onServerTick);
        NeoForge.EVENT_BUS.addListener(MergeManager::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(MergeManager::onPlayerLoggedOut);
        NeoForge.EVENT_BUS.addListener(MergeManager::onStartTracking);

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        MergeLevelCondition.LOOT_CONDITION_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.MERGING_WAND);
        }
    }
}
