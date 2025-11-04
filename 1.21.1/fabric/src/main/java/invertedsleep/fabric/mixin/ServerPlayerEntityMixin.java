package invertedsleep.fabric.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import invertedsleep.utils.TimeUtils;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getWorld()Lnet/minecraft/world/World;", ordinal = 3), cancellable = true)
    public void changeTrySleepCriteria(BlockPos pos, CallbackInfoReturnable<Either<SleepFailureReason, Unit>> cir) {
        if (!TimeUtils.isNoon(self.getWorld())) {
            cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW));
        } else {
            if (!self.isCreative()) {
                double d = 8.0;
                double e = 5.0;
                Vec3d vec3d = Vec3d.ofBottomCenter(pos);
                List<HostileEntity> list = self.getWorld()
                        .getEntitiesByClass(
                                HostileEntity.class,
                                new Box(vec3d.getX() - 8.0, vec3d.getY() - 5.0, vec3d.getZ() - 8.0, vec3d.getX() + 8.0, vec3d.getY() + 5.0, vec3d.getZ() + 8.0),
                                entity -> entity.isAngryAt(self)
                        );
                if (!list.isEmpty()) {
                    cir.setReturnValue(Either.left(PlayerEntity.SleepFailureReason.NOT_SAFE));
                }
            }

            Either<PlayerEntity.SleepFailureReason, Unit> either = super.trySleep(pos).ifRight(unit -> {
                this.incrementStat(Stats.SLEEP_IN_BED);
                Criteria.SLEPT_IN_BED.trigger(self);
            });
            if (!self.getServerWorld().isSleepingEnabled()) {
                this.sendMessage(Text.translatable("sleep.not_possible"), true);
            }

            ((ServerWorld) this.getWorld()).updateSleepingPlayers();
            cir.setReturnValue(either);
        }
    }
}
