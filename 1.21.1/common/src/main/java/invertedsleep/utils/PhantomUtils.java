package invertedsleep.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.spawner.PhantomSpawner;

public class PhantomUtils {
    public static boolean canSpawn(PhantomSpawner spawner, ServerPlayerEntity player) {
        return player.getWorld().isNight() && !player.isCreative();
    }

    public static int getCooldown(PhantomSpawner spawner, ServerWorld world) {
        return 3000 + world.getRandom().nextInt(3000);
    }
}
