package invertedsleep.mixin;

import invertedsleep.utils.TimeUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Unique
    private PlayerEntity self = (PlayerEntity) (Object) this;

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z", ordinal = 0))
    public boolean changeTrySleepCriteria(World world) {
        if (!self.getWorld().isClient && !TimeUtils.isNoon(world)) {
            self.wakeUp(false, true);
        }
        return true;
    }
}
