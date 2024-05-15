package t.me.p1azmer.engine.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.config.EngineConfig;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Deprecated
public class CollectionsUtil {

  @Deprecated
  public static final boolean[] BOOLEANS = new boolean[]{true, false};

  @NotNull
  public static List<String> playerNames() {
    return playerNames(null);
  }

  @NotNull
  public static List<String> playerNames(@Nullable Player viewer) {
    return playerNames(viewer, true);
  }

  @NotNull
  public static List<String> realPlayerNames() {
    return realPlayerNames(null);
  }

  @NotNull
  public static List<String> realPlayerNames(@Nullable Player viewer) {
    return playerNames(viewer, false);
  }

  @NotNull
  public static List<String> playerNames(@Nullable Player viewer, boolean includeCustom) {
    Set<String> names = new HashSet<>();
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      if (viewer != null && !viewer.canSee(player)) continue;

      names.add(player.getName());
      if (includeCustom && EngineConfig.RESPECT_PLAYER_DISPLAYNAME.get()) {
        names.add(Colorizer.strip(player.getDisplayName()));
      }
    }
    return names.stream().sorted(String::compareTo).toList();
  }

  @NotNull
  public static List<String> getSequentialMatches(@NotNull List<String> results, @NotNull String input) {
    char[] chars = input.toCharArray();
    List<String> goods = new ArrayList<>();

    Result:
    for (String sub : results) {
      int lastIndex = -1;

      for (char letter : chars) {
        int index = sub.indexOf(letter, lastIndex == -1 ? 0 : lastIndex);
        if (index <= lastIndex) {
          continue Result;
        }
        lastIndex = index;
      }
      goods.add(sub);
    }
    return goods;
  }

  /**
   * @param original List to remove empty lines from.
   * @return A list with no multiple empty lines in a row.
   */
  @NotNull
  public static List<String> stripEmpty(@NotNull List<String> original) {
    List<String> stripped = new ArrayList<>();
    for (int index = 0; index < original.size(); index++) {
      String line = original.get(index);
      if (line.isEmpty()) {
        String last = stripped.isEmpty() ? null : stripped.get(stripped.size() - 1);
        if (last == null || last.isEmpty() || index == (original.size() - 1)) continue;
      }
      stripped.add(line);
    }
    return stripped;
  }

  @NotNull
  public static List<String> worldNames() {
    return Bukkit.getServer().getWorlds().stream().map(WorldInfo::getName).toList();
  }

  @NotNull
  public static <T extends Entity> Collection<T> nearbyEntities(@NotNull Location location, @NotNull Class<T> type, int radius) {
    World world = location.getWorld();
    if (world == null) {
      world = Bukkit.getServer().getWorlds().get(0);
    }
    return world.getEntitiesByClass(type).stream()
                .filter(e -> e.getLocation().distance(location) <= radius)
                .collect(Collectors.toSet());
  }

  @NotNull
  public static <T> List<List<T>> split(@NotNull List<T> list, int targetSize) {
    List<List<T>> lists = new ArrayList<>();
    if (targetSize <= 0) return lists;

    for (int index = 0; index < list.size(); index += targetSize) {
      lists.add(list.subList(index, Math.min(index + targetSize, list.size())));
    }
    return lists;
  }

  @NotNull
  public static <K, V extends Comparable<? super V>> Map<K, V> sortAscent(@NotNull Map<K, V> map) {
    return sort(map, Map.Entry.comparingByValue());
  }

  @NotNull
  public static <K, V extends Comparable<? super V>> Map<K, V> sortDescent(@NotNull Map<K, V> map) {
    return sort(map, Collections.reverseOrder(Map.Entry.comparingByValue()));
  }

  @NotNull
  public static <K, V extends Comparable<? super V>> Map<K, V> sort(@NotNull Map<K, V> map, @NotNull Comparator<Map.Entry<K, V>> comparator) {
    return new LinkedList<>(map.entrySet()).stream().sorted(comparator)
                                           .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (old, nev) -> nev, LinkedHashMap::new));
  }

  @NotNull
  @Deprecated
  public static String getEnums(@NotNull Class<?> clazz) {
    return String.join(ChatColor.GRAY + ", " + ChatColor.WHITE, getEnumsList(clazz));
  }

  @NotNull
  public static List<String> getEnumsList(@NotNull Class<?> clazz) {
    return new ArrayList<>(Stream.of(clazz.getEnumConstants()).map(Object::toString).toList());
  }

  public static <T> T getNext(Collection<T> collection, T targetValue) {
    Iterator<T> iterator = collection.iterator();
    while (iterator.hasNext()) {
      T currentValue = iterator.next();
      if (currentValue.equals(targetValue) && iterator.hasNext()) {
        return iterator.next();
      }
    }
    return null;
  }

  public static <T> T getPrevious(Collection<T> collection, T targetValue) {
    Iterator<T> iterator = collection.iterator();
    T previousValue = null;
    while (iterator.hasNext()) {
      T currentValue = iterator.next();
      if (currentValue.equals(targetValue)) {
        return previousValue;
      }
      previousValue = currentValue;
    }
    return null;
  }

  @NotNull
  @Deprecated
  public static <T extends Enum<T>> T switchEnum(@NotNull Enum<T> en) {
    return next(en);
  }

  @NotNull
  public static <T extends Enum<T>> T next(@NotNull Enum<T> numeration) {
    return shifted(numeration, 1);
  }

  @NotNull
  public static <T extends Enum<T>> T next(@NotNull Enum<T> numeration, @NotNull Predicate<T> predicate) {
    return shifted(numeration, 1, predicate);
  }

  @NotNull
  public static <T extends Enum<T>> T previous(@NotNull Enum<T> numeration) {
    return shifted(numeration, -1);
  }

  @NotNull
  public static <T extends Enum<T>> T previous(@NotNull Enum<T> numeration, @NotNull Predicate<T> predicate) {
    return shifted(numeration, -1, predicate);
  }

  @NotNull
  public static <T extends Enum<T>> T shifted(@NotNull Enum<T> numeration, int shift) {
    return shifted(numeration, shift, null);
  }

  @NotNull
  private static <T extends Enum<T>> T shifted(@NotNull Enum<T> numeration, int shift, @Nullable Predicate<T> predicate) {
    T[] values = numeration.getDeclaringClass().getEnumConstants();
    return shifted(values, numeration/*.ordinal()*/, shift, predicate);
  }

  @NotNull
  private static <T extends Enum<T>> T shifted(T[] values, @NotNull Enum<T> origin, int shift, @Nullable Predicate<T> predicate) {
    if (predicate != null) {
      T source = origin.getDeclaringClass().cast(origin);
      List<T> filtered = new ArrayList<>(Arrays.asList(values));
      filtered.removeIf(num -> !predicate.test(num) && num != source);

      int currentIndex = filtered.indexOf(source);
      //List<T> filtered = Stream.of(values).filter(predicate).toList();
      if (currentIndex < 0 | filtered.isEmpty()) return source;//values[currentIndex];

      return shifted(filtered, currentIndex, shift);
    }
    return shifted(values, origin.ordinal(), shift);
  }

  @NotNull
  public static <T> T shifted(T[] values, int currentIndex, int shift) {
    int index = currentIndex + shift;
    return values[index >= values.length || index < 0 ? 0 : index];
  }

  @NotNull
  public static <T> T shifted(@NotNull List<T> values, int currentIndex, int shift) {
    int index = currentIndex + shift;
    if (index < 0) return values.get(values.size() - 1);

    return values.get(index >= values.size() ? 0 : index);
  }

  @Nullable
  @Deprecated
  public static <T extends Enum<T>> T getEnum(@NotNull String str, @NotNull Class<T> clazz) {
    return StringUtil.getEnum(str, clazz).orElse(null);
  }
}