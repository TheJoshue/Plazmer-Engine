package t.me.p1azmer.engine.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.lang.LangManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LocationUtil {

    @Nullable
    public static String serialize(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        return location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getPitch() + "," + location.getYaw() + "," + world.getName();
    }

    @NotNull
    public static List<String> serialize(@NotNull Collection<Location> list) {
        return new ArrayList<>(list.stream().map(LocationUtil::serialize).filter(Objects::nonNull).toList());
    }

    @Nullable
    public static Location deserialize(@NotNull String raw) {
        String[] split = raw.split(",");
        if (split.length != 6) return null;

        World world = Bukkit.getWorld(split[5]);
        if (world == null) {
            EngineUtils.ENGINE.error("Invalid/Unloaded world for: '" + raw + "' location!");
            return null;
        }

        double x = NumberUtil.getAnyDouble(split[0], 0);
        double y = NumberUtil.getAnyDouble(split[1], 0);
        double z = NumberUtil.getAnyDouble(split[2], 0);
        float pitch = (float) NumberUtil.getAnyDouble(split[3], 0);
        float yaw = (float) NumberUtil.getAnyDouble(split[4], 0);

        return new Location(world, x, y, z, yaw, pitch);
    }

    @NotNull
    public static List<Location> deserialize(@NotNull Collection<String> list) {
        return new ArrayList<>(list.stream().map(LocationUtil::deserialize).filter(Objects::nonNull).toList());
    }

    @NotNull
    public static String getWorldName(@NotNull Location location) {
        World world = location.getWorld();
        return world == null ? "null" : world.getName();
    }

    @NotNull
    @Deprecated
    public static String getWorldName(@NotNull World world) {
        return LangManager.getWorld(world);
    }

    @NotNull
    @Deprecated
    public static List<String> getWorldNames() {
        return CollectionsUtil.worldNames();
    }

    @NotNull
    public static Location getFirstGroundBlock(@NotNull Location loc) {
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();

        Block under = loc.getBlock();
        while ((under.isEmpty() || !under.getType().isSolid()) && under.getY() > 0) {
            under = under.getRelative(BlockFace.DOWN);
        }

        loc = under.getRelative(BlockFace.UP).getLocation();
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }

    @NotNull
    public static Location getCenter(@NotNull Location location) {
        return getCenter(location, true);
    }

    @NotNull
    public static Location getCenter(@NotNull Location location, boolean doVertical) {
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        double x = getRelativeCoord(location.getBlockX());
        double y = doVertical ? getRelativeCoord(location.getBlockY()) : location.getBlockY();
        double z = getRelativeCoord(location.getBlockZ());

        location = new Location(location.getWorld(), x, y, z);
        location.setYaw(yaw);
        location.setPitch(pitch);
        return location;
    }

    private static double getRelativeCoord(double cord) {
        return cord < 0 ? cord + 0.5 : cord + 0.5;
    }

    @NotNull
    public static Location getPointOnCircle(@NotNull Location loc, double n, double n2, double n3) {
        return getPointOnCircle(loc, true, n, n2, n3);
    }

    @NotNull
    public static Location getPointOnCircle(@NotNull Location loc, boolean doCopy, double n, double n2, double n3) {
        return (doCopy ? loc.clone() : loc).add(Math.cos(n) * n2, n3, Math.sin(n) * n2);
    }

    @Nullable
    public static BlockFace getDirection(@NotNull Entity entity) {
        float yaw = Math.round(entity.getLocation().getYaw() / 90F);

        if ((yaw == -4.0F) || (yaw == 0.0F) || (yaw == 4.0F)) {
            return BlockFace.SOUTH;
        }
        if ((yaw == -1.0F) || (yaw == 3.0F)) {
            return BlockFace.EAST;
        }
        if ((yaw == -2.0F) || (yaw == 2.0F)) {
            return BlockFace.NORTH;
        }
        if ((yaw == -3.0F) || (yaw == 1.0F)) {
            return BlockFace.WEST;
        }
        return null;
    }

    @NotNull
    @Deprecated
    public static Vector getDirectionTo(@NotNull Location from, @NotNull Location to) {
        Location origin = from.clone();
        Vector target = to.clone().toVector();
        origin.setDirection(target.subtract(origin.toVector()));

        return origin.getDirection();
    }

    @NotNull
    public static Vector getDirection(@NotNull Location from, @NotNull Location to) {
        Location origin = from.clone();
        origin.setDirection(to.toVector().subtract(origin.toVector()));
        return origin.getDirection();
    }

    public static <T extends Entity> Collection<T> getNearbyEntities(@NotNull Location location, @NotNull Class<T> type, int radius) {
        @NotNull World world = Objects.requireNonNull(location.getWorld(), "World of location is null, " + location);
        return world.getEntitiesByClass(type).stream().filter(e -> e.getLocation().distance(location) <= radius).collect(Collectors.toSet());
    }
}