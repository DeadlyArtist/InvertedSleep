package invertedsleep.mixin;

import net.minecraft.server.world.SleepManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SleepManager.class)
public class SleepManagerMixin {

    @Inject(method = "getNightSkippingRequirement", at = @At("HEAD"), cancellable = true)
    public void injectCanSkipNight(int percentage, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
}
