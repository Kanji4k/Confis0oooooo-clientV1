package client.confis;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.entity.projectile.windcharge.AbstractWindCharge;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/** Draws colored hitbox outlines, only for the entity types switched on in the Hitboxes screen. */
public final class HitboxRenderer {
    public static final String[] KEYS = {"crystal", "exp", "friendly", "hostile", "firework", "pearl", "windcharge"};
    public static final String[] LABELS = {"Crystal", "XP orb", "Friendly mob", "Hostile mob", "Firework", "Ender pearl", "Wind charge"};
    private static final int[] COLORS = {0xFF55FFFF, 0xFF55FF55, 0xFF55FF55, 0xFFFF5555, 0xFFFFAA00, 0xFFAA55FF, 0xFFFFFFFF};

    private HitboxRenderer() {}

    public static boolean on(String key) { return Config.get().hitboxes.getOrDefault(key, false); }

    private static int typeOf(Entity e) {
        if (e instanceof EndCrystal) return 0;
        if (e instanceof ExperienceOrb) return 1;
        if (e instanceof FireworkRocketEntity) return 4;
        if (e instanceof ThrownEnderpearl) return 5;
        if (e instanceof AbstractWindCharge) return 6;
        if (e instanceof Enemy) return 3;
        if (e instanceof Mob) return 2;
        return -1;
    }

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(ctx -> {
            Config cfg = Config.get();
            if (!cfg.hitboxMaster) return;
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null || ctx.consumers() == null) return;
            Vec3 cam = ctx.camera().getPosition();
            VertexConsumer vc = ctx.consumers().getBuffer(RenderType.lines());
            for (Entity e : mc.level.entitiesForRendering()) {
                int t = typeOf(e);
                if (t < 0 || !on(KEYS[t]) || e == mc.player) continue;
                AABB b = e.getBoundingBox().move(-cam.x, -cam.y, -cam.z);
                int c = COLORS[t];
                ShapeRenderer.renderLineBox(ctx.matrixStack(), vc, b,
                        ((c >> 16) & 255) / 255f, ((c >> 8) & 255) / 255f, (c & 255) / 255f, 1f);
            }
        });
    }
}
