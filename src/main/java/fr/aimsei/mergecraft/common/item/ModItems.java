package fr.aimsei.mergecraft.common.item;

import fr.aimsei.mergecraft.MergeCraft;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MergeCraft.MODID);

    // Create the Merge Wand Item
    public static final DeferredItem<Item> MERGING_WAND = ITEMS.register("merging_wand", MergingWandItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
