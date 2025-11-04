package invertedsleep.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import invertedsleep.utils.PhantomUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.spawner.PhantomSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PhantomSpawner.class)
public class PhantomSpawnerMixin {

    @Unique
    private PhantomSpawner self = (PhantomSpawner) (Object) this;

    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/random/Random;nextInt(I)I", ordinal = 1))
    public int redirectSpawnAtNight(Random instance, int i, @Local ServerPlayerEntity player) {
        if (PhantomUtils.canSpawn(self, player)) {
            i = 72000 + 1;
        } else {
            i = 1;
        }
        return i;
    }

    @Redirect(method = "spawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/LocalDifficulty;isHarderThan(F)Z"))
    public boolean redirectSpawnAtNight(LocalDifficulty instance, float difficulty, @Local ServerPlayerEntity player) {
        return true;
    }


    @Redirect(method = "spawn", at = @At(value = "FIELD", target = "Lnet/minecraft/world/spawner/PhantomSpawner;cooldown:I", ordinal = 3))
    public int redirectCooldown(PhantomSpawner instance, @Local ServerWorld world) {
        return PhantomUtils.getCooldown(self, world);
    }
}
