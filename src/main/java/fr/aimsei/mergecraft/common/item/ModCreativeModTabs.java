package fr.aimsei.mergecraft.common.item;

import fr.aimsei.mergecraft.MergeCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModTabs {

    public static DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MergeCraft.MODID);

    public static final Supplier<CreativeModeTab> MERGING_ITEMS_TAB = CREATIVE_MODE_TABS.register("merging_items_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.MERGING_WAND.get()))
                    .title(Component.translatable("creativetab.mergecraft.items"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(ModItems.MERGING_WAND);
                    }).build());

    public static void register(IEventBus bus) {
        CREATIVE_MODE_TABS.register(bus);
    }
}
