package com.pepperspray;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

/** Hold right-click to spray. Unlimited uses (no durability, no consumption). */
public class PepperSprayItem extends Item {
    private static final int EFFECT_TICKS = 100;      // 5 seconds
    private static final double RANGE = 5.0;
    private static final double CONE = 0.85;          // dot product threshold
    private static final DustParticleEffect ORANGE =
            new DustParticleEffect(new Vector3f(1.0f, 0.5f, 0.05f), 1.6f);

    public PepperSprayItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient || !(world instanceof ServerWorld sw)) return;

        Vec3d start = user.getEyePos();
        Vec3d dir = user.getRotationVec(1.0f);

        // Orange mist
        for (int i = 1; i <= 10; i++) {
            Vec3d p = start.add(dir.multiply(i * 0.5)).add(0, -0.15, 0);
            double spread = 0.05 + i * 0.03;
            sw.spawnParticles(ORANGE, p.x, p.y, p.z, 4, spread, spread, spread, 0.01);
        }

        int used = getMaxUseTime(stack) - remainingUseTicks;
        if (used % 4 == 0) {
            world.playSound(null, user.getBlockPos(), SoundEvents.BLOCK_FIRE_EXTINGUISH,
                    SoundCategory.PLAYERS, 0.5f, 2.0f);
        }

        // Apply effects every 5 ticks
        if (used % 5 == 0) {
            Box box = new Box(start, start.add(dir.multiply(RANGE))).expand(1.0);
            for (LivingEntity e : world.getEntitiesByClass(LivingEntity.class, box,
                    x -> x != user && x.isAlive())) {
                Vec3d to = e.getEyePos().subtract(start);
                if (to.length() > RANGE + 0.5) continue;
                if (to.normalize().dotProduct(dir) > CONE) {
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, EFFECT_TICKS, 0), user);
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_TICKS, 1), user);
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, EFFECT_TICKS, 0), user);
                }
            }
        }
    }
}
