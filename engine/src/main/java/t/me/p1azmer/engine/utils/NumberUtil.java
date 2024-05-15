package t.me.p1azmer.engine.utils;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.TreeMap;

public class NumberUtil {

  private static final DecimalFormat FORMAT_ROUND_HUMAN;
  private final static TreeMap<Integer, String> ROMAN_MAP = new TreeMap<>();

  static {
    FORMAT_ROUND_HUMAN = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.ENGLISH));

    ROMAN_MAP.put(1000, "M");
    ROMAN_MAP.put(900, "CM");
    ROMAN_MAP.put(500, "D");
    ROMAN_MAP.put(400, "CD");
    ROMAN_MAP.put(100, "C");
    ROMAN_MAP.put(90, "XC");
    ROMAN_MAP.put(50, "L");
    ROMAN_MAP.put(40, "XL");
    ROMAN_MAP.put(10, "X");
    ROMAN_MAP.put(9, "IX");
    ROMAN_MAP.put(5, "V");
    ROMAN_MAP.put(4, "IV");
    ROMAN_MAP.put(1, "I");
  }

  @NotNull
  public static String format(double value) {
    return FORMAT_ROUND_HUMAN.format(value);
  }

  public static double round(double value) {
    return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
  }

  @NotNull
  public static String toRoman(int number) {
    if (number <= 0) return String.valueOf(number);

    int key = ROMAN_MAP.floorKey(number);
    if (number == key) {
      return ROMAN_MAP.get(number);
    }
    return ROMAN_MAP.get(key) + toRoman(number - key);
  }

  public static int[] splitIntoParts(int whole, int parts) {
    int[] arr = new int[parts];
    int remain = whole;
    int partsLeft = parts;
    for (int i = 0; partsLeft > 0; i++) {
      int size = (remain + partsLeft - 1) / partsLeft; // rounded up, aka ceiling
      arr[i] = size;
      remain -= size;
      partsLeft--;
    }
    return arr;
  }

  public static double getDouble(@NotNull String input) {
    return getDouble(input, 0D);
  }

  public static double getDouble(@NotNull String input, double defaultValue) {
    return Math.abs(getAnyDouble(input, defaultValue));
  }

  public static double getAnyDouble(@NotNull String input, double defaultValue) {
    try {
      double amount = Double.parseDouble(input);
      if (!Double.isNaN(amount) && !Double.isInfinite(amount)) {
        return amount;
      }
    } catch (NumberFormatException ignored) {
    }

    return defaultValue;
  }

  public static int getInteger(@NotNull String input) {
    return getInteger(input, 0);
  }

  public static int getInteger(@NotNull String input, int defaultValue) {
    return Math.abs(getAnyInteger(input, defaultValue));
  }

  public static int getAnyInteger(@NotNull String input, int defaultValue) {
    try {
      return Integer.parseInt(input);
    } catch (NumberFormatException ignored) {
    }

    return defaultValue;
  }

  public static int[] getIntArray(@NotNull String str) {
    String[] split = str.split(",");
    int[] array = new int[split.length];
    for (int index = 0; index < split.length; index++) {
      try {
        array[index] = Integer.parseInt(split[index].trim());
      } catch (NumberFormatException e) {
        array[index] = 0;
      }
    }
    return array;
  }
}