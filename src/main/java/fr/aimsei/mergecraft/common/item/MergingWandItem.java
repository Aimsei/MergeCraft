package fr.aimsei.mergecraft.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class MergingWandItem extends Item {

    public MergingWandItem() {
        super(new Item.Properties()
                .durability(30)
                .rarity(Rarity.RARE)
                .stacksTo(1)
        );
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // TODO : Logique de sélection (Étape 1.3)
        return super.use(level, player, hand);
    }

    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.literal("Utilisez un clic droit sur un mob pour le sélectionner."));
        tooltipComponents.add(Component.literal("Durabilité : " + (stack.getMaxDamage() - stack.getDamageValue()) +"/" +stack.getMaxDamage()));
    }

}
