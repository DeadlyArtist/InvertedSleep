package invertedsleep.neoforge.mixin;

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
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private ServerPlayerEntity self = (ServerPlayerEntity) (Object) this;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Redirect(method = "trySleep", at = @At(value = "INVOKE", target = "Lnet/neoforged/neoforge/event/EventHooks;canPlayerStartSleeping(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/datafixers/util/Either;)Lcom/mojang/datafixers/util/Either;"))
    public Either<SleepFailureReason, Unit> changeTrySleepCriteria(ServerPlayerEntity player, BlockPos pos, Either<SleepFailureReason, Unit> vanillaResult) {
        if (self.getWorld().getTimeOfDay() == TimeUtils.NOON) {
            if (vanillaResult.left().isPresent() && vanillaResult.left().get().equals(PlayerEntity.SleepFailureReason.NOT_POSSIBLE_NOW)) {
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
                        return Either.left(SleepFailureReason.NOT_SAFE);
                    }
                }

                return Either.right(Unit.INSTANCE);
            }
        } else {
            if (vanillaResult.left().isEmpty()) {
                return Either.left(SleepFailureReason.NOT_POSSIBLE_NOW);
            }
        }

        return vanillaResult;
    }
}
