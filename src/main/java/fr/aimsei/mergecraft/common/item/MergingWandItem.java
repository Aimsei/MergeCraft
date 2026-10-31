package fr.aimsei.mergecraft.common.item;

import fr.aimsei.mergecraft.common.merge.MergeManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

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
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }
        HitResult hit = player.pick(6.0, 0.0f, false);
        if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target) {
            MergeManager.selectMob(serverPlayer, target, stack, hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13.0F * (stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage());
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return 0x8A2BE2;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.mergecraft.merging_wand.usage"));
        tooltipComponents.add(Component.translatable("tooltip.mergecraft.merging_wand.durability",
                stack.getMaxDamage() - stack.getDamageValue(), stack.getMaxDamage()));
    }
}
