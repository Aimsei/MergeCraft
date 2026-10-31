package fr.aimsei.mergecraft.common.merge;

import fr.aimsei.mergecraft.Config;
import fr.aimsei.mergecraft.network.MergeLevelSyncPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MergeManager {
    private static final Map<UUID, PlayerSelection> SELECTIONS = new HashMap<>();
    private static final List<Fusion> ACTIVE_FUSIONS = new ArrayList<>();

    private static class PlayerSelection {
        final List<UUID> entityIds = new ArrayList<>();
        long expireGameTime;
    }

    private static class Fusion {
        final UUID playerId;
        final UUID survivorId;
        final List<UUID> consumedIds;
        final ServerLevel level;
        int ticksLeft;
        boolean survivorWasInvulnerable;
        boolean survivorWasNoAi;
        final Map<UUID, Boolean> consumedWasNoAi = new HashMap<>();

        Fusion(UUID playerId, UUID survivorId, List<UUID> consumedIds, ServerLevel level, int ticks) {
            this.playerId = playerId;
            this.survivorId = survivorId;
            this.consumedIds = consumedIds;
            this.level = level;
            this.ticksLeft = ticks;
        }
    }

    public static boolean isMergeable(Entity entity) {
        if (entity == null || !entity.isAlive()) return false;
        if (entity instanceof Player) return false;
        if (entity instanceof EnderDragon || entity instanceof WitherBoss) return false;
        if (entity instanceof ItemEntity) return false;
        return entity instanceof LivingEntity;
    }

    public static void selectMob(ServerPlayer player, LivingEntity target, ItemStack wand, InteractionHand hand) {
        ServerLevel level = player.serverLevel();
        if (!isMergeable(target)) {
            player.displayClientMessage(Component.translatable("message.mergecraft.not_mergeable"), true);
            return;
        }
        PlayerSelection selection = SELECTIONS.computeIfAbsent(player.getUUID(), k -> new PlayerSelection());
        selection.expireGameTime = level.getGameTime() + Config.SELECTION_DURATION_SECONDS.get() * 20L;
        selection.entityIds.removeIf(id -> {
            Entity e = level.getEntity(id);
            return e == null || !e.isAlive();
        });
        if (selection.entityIds.contains(target.getUUID())) {
            player.displayClientMessage(Component.translatable("message.mergecraft.already_selected"), true);
            return;
        }
        selection.entityIds.add(target.getUUID());
        wand.hurtAndBreak(1, level, player, item -> player.onEquippedItemBroken(item,
                hand == InteractionHand.MAIN_HAND ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND));
        player.displayClientMessage(Component.translatable("message.mergecraft.selected", selection.entityIds.size()), true);
        if (selection.entityIds.size() >= 3) {
            attemptFusion(player, level, selection);
        }
    }

    private static void attemptFusion(ServerPlayer player, ServerLevel level, PlayerSelection selection) {
        List<UUID> ids = new ArrayList<>(selection.entityIds.subList(0, 3));
        List<LivingEntity> mobs = new ArrayList<>();
        for (UUID id : ids) {
            Entity e = level.getEntity(id);
            if (e instanceof LivingEntity le && le.isAlive()) {
                mobs.add(le);
            }
        }
        selection.entityIds.clear();
        if (mobs.size() < 3) {
            player.displayClientMessage(Component.translatable("message.mergecraft.fusion_fail_mismatch"), true);
            return;
        }
        LivingEntity first = mobs.get(0);
        int level0 = MergeLevelData.getMergeLevel(first);
        boolean same = mobs.stream().allMatch(m -> m.getType() == first.getType() && MergeLevelData.getMergeLevel(m) == level0);
        if (!same) {
            player.displayClientMessage(Component.translatable("message.mergecraft.fusion_fail_mismatch"), true);
            return;
        }
        if (level0 >= Config.MAX_MERGE_LEVEL.get()) {
            player.displayClientMessage(Component.translatable("message.mergecraft.fusion_fail_max"), true);
            return;
        }
        startFusion(player, level, first, mobs.subList(1, 3));
    }

    private static void startFusion(ServerPlayer player, ServerLevel level, LivingEntity survivor, List<LivingEntity> consumed) {
        Fusion fusion = new Fusion(player.getUUID(), survivor.getUUID(),
                consumed.stream().map(Entity::getUUID).toList(), level, Config.FUSION_ANIMATION_TICKS.get());
        fusion.survivorWasInvulnerable = survivor.isInvulnerable();
        survivor.setInvulnerable(true);
        if (survivor instanceof Mob mob) {
            fusion.survivorWasNoAi = mob.isNoAi();
            mob.setNoAi(true);
        }
        for (LivingEntity c : consumed) {
            c.setInvulnerable(true);
            if (c instanceof Mob mob) {
                fusion.consumedWasNoAi.put(c.getUUID(), mob.isNoAi());
                mob.setNoAi(true);
            }
        }
        ACTIVE_FUSIONS.add(fusion);
        player.displayClientMessage(Component.translatable("message.mergecraft.fusion_started"), true);
    }

    private static boolean tickFusion(Fusion fusion) {
        ServerLevel level = fusion.level;
        Entity survivor = level.getEntity(fusion.survivorId);
        List<LivingEntity> consumed = new ArrayList<>();
        for (UUID id : fusion.consumedIds) {
            Entity e = level.getEntity(id);
            if (e instanceof LivingEntity le && le.isAlive()) {
                consumed.add(le);
            }
        }
        if (survivor == null || !survivor.isAlive()) {
            for (LivingEntity c : consumed) {
                c.setInvulnerable(false);
                if (c instanceof Mob mob) {
                    mob.setNoAi(fusion.consumedWasNoAi.getOrDefault(c.getUUID(), false));
                }
            }
            return false;
        }
        if (fusion.ticksLeft > 0) {
            Vec3 target = survivor.position().add(0, survivor.getBbHeight() / 2.0, 0);
            for (LivingEntity c : consumed) {
                Vec3 next = c.position().lerp(target, 0.25);
                c.setPos(next.x, next.y, next.z);
                c.setDeltaMovement(Vec3.ZERO);
                level.sendParticles(ParticleTypes.PORTAL, c.getX(), c.getY() + 0.5, c.getZ(), 4, 0.2, 0.4, 0.2, 0.01);
            }
            fusion.ticksLeft--;
            return fusion.ticksLeft > 0;
        }
        for (LivingEntity c : consumed) {
            level.sendParticles(ParticleTypes.POOF, c.getX(), c.getY() + 0.5, c.getZ(), 12, 0.3, 0.5, 0.3, 0.02);
            c.setInvulnerable(false);
            if (c instanceof Mob mob) {
                mob.setNoAi(fusion.consumedWasNoAi.getOrDefault(c.getUUID(), false));
            }
            c.discard();
        }
        survivor.setInvulnerable(fusion.survivorWasInvulnerable);
        if (survivor instanceof Mob mob) {
            mob.setNoAi(fusion.survivorWasNoAi);
        }
        int newLevel = Math.min(MergeLevelData.getMergeLevel(survivor) + 1, Config.MAX_MERGE_LEVEL.get());
        MergeLevelData.setMergeLevel(survivor, newLevel);
        PacketDistributor.sendToPlayersTrackingEntity(survivor, new MergeLevelSyncPayload(survivor.getId(), newLevel));
        MinecraftServer server = level.getServer();
        ServerPlayer owner = server.getPlayerList().getPlayer(fusion.playerId);
        if (owner != null) {
            owner.displayClientMessage(Component.translatable("message.mergecraft.fusion_success", newLevel), true);
        }
        return false;
    }

    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        long now = server.overworld().getGameTime();
        SELECTIONS.entrySet().removeIf(entry -> {
            if (entry.getValue().expireGameTime <= now) {
                ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
                if (player != null) {
                    player.displayClientMessage(Component.translatable("message.mergecraft.selection_expired"), true);
                }
                return true;
            }
            return false;
        });
        Iterator<Fusion> it = ACTIVE_FUSIONS.iterator();
        while (it.hasNext()) {
            if (!tickFusion(it.next())) {
                it.remove();
            }
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        SELECTIONS.remove(event.getEntity().getUUID());
    }

    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        SELECTIONS.remove(event.getEntity().getUUID());
    }

    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (event.getTarget() != null && event.getEntity() instanceof ServerPlayer player) {
            int level = MergeLevelData.getMergeLevel(event.getTarget());
            if (level > 1) {
                PacketDistributor.sendToPlayer(player, new MergeLevelSyncPayload(event.getTarget().getId(), level));
            }
        }
    }
}
