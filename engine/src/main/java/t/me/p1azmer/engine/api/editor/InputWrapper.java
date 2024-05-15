package t.me.p1azmer.engine.api.editor;

import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.engine.utils.Colorizer;
import t.me.p1azmer.engine.utils.NumberUtil;
import t.me.p1azmer.engine.utils.StringUtil;
import t.me.p1azmer.engine.utils.wrapper.UniDouble;
import t.me.p1azmer.engine.utils.wrapper.UniInt;

public class InputWrapper {

    private final String text;
    private final String textRaw;
    private final String textColored;

    public InputWrapper(@NotNull AsyncPlayerChatEvent e) {
        this(e.getMessage());
    }

    public InputWrapper(@NotNull String text) {
        this.text = text;
        this.textRaw = Colorizer.restrip(text);
        this.textColored = Colorizer.apply(text);
    }

    public int asInt() {
        return this.asInt(0);
    }

    public int asInt(int def) {
        return NumberUtil.getInteger(this.getTextRaw(), def);
    }

    public int asAnyInt(int def) {
        return NumberUtil.getAnyInteger(this.getTextRaw(), def);
    }

    public double asDouble() {
        return this.asDouble(0D);
    }

    public double asDouble(double def) {
        return NumberUtil.getDouble(this.getTextRaw(), def);
    }

    @NotNull
    public UniDouble asUniDouble() {
        return this.asUniDouble(0, 0);
    }

    @NotNull
    public UniDouble asUniDouble(double min, double max) {
        String[] split = this.getTextRaw().split(" ");
        return UniDouble.of(NumberUtil.getDouble(split[0], min), NumberUtil.getDouble(split.length >= 2 ? split[1] : split[0], max));
    }

    @NotNull
    public UniInt asUniInt() {
        return this.asUniDouble().asInt();
    }

    public double asAnyDouble(double def) {
        return NumberUtil.getAnyDouble(this.getTextRaw(), def);
    }

    @Nullable
    public <E extends Enum<E>> E asEnum(@NotNull Class<E> clazz) {
        return StringUtil.getEnum(this.getTextRaw(), clazz).orElse(null);
    }

    @NotNull
    public <E extends Enum<E>> E asEnum(@NotNull Class<E> clazz, @NotNull E def) {
        return StringUtil.getEnum(this.getTextRaw(), clazz).orElse(def);
    }

    @NotNull
    public String getText() {
        return text;
    }

    @NotNull
    public String getTextRaw() {
        return textRaw;
    }

    @NotNull
    public String getTextColored() {
        return textColored;
    }
}