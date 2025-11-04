package invertedsleep.utils;

import net.minecraft.world.World;

public class TimeUtils {
    public static long NOON = 6000;

    public static long getSimpleDaytime(World world) {
        return world.getTimeOfDay() % 24000L;
    }

    public static boolean isNoon(World world) {
        return getSimpleDaytime(world) == NOON;
    }
}
