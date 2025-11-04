package invertedsleep.mixin;

import invertedsleep.utils.TimeUtils;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {

    @Unique
    private ServerWorld world = (ServerWorld) (Object) this;

    @Redirect(method = "sendSleepingStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"))
    private MutableText changeSleepingStatusMessage(String key) {
        return Text.translatable("sleep.invertedsleep.starting_night");
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    private void redirectSetTimeAfterSleep(ServerWorld world, long timeOfDay) {
        world.setTimeOfDay(world.getTimeOfDay() + 1);
    }


    @Redirect(method = "tickTime", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setTimeOfDay(J)V"))
    private void redirectStopTimeAtMiddday(ServerWorld world, long timeOfDay) {
        if (TimeUtils.isNoon(world)) return;
        world.setTimeOfDay(timeOfDay);
    }
}
