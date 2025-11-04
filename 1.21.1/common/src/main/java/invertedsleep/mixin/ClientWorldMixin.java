package invertedsleep.mixin;

import invertedsleep.utils.TimeUtils;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Unique
    private ClientWorld world = (ClientWorld) (Object) this;

    @Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;setTimeOfDay(J)V"))
    private void redirectStopTimeAtMiddday(ClientWorld world, long timeOfDay) {
        if (TimeUtils.isNoon(world)) return;
        world.setTimeOfDay(timeOfDay);
    }
}
