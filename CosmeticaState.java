package client.confis;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Everything the player changes in Cosmetica. It is a plain data class stored inside Config (confis0oooooo.json),
 * so it is saved and restored together with all other settings. No Minecraft classes here.
 *
 * Equip rule: one item per slot (= per category). Equipping an item replaces the item already in its slot, so
 * two conflicting items can never be worn together, while a hat + cape + wings + shoes (different slots) can.
 */
public class CosmeticaState {
    public static final int PRESETS = 3;

    /** slot name -> cosmetic id of the worn item. */
    public Map<String, String> equipped = new LinkedHashMap<>();
    public List<String> favorites = new ArrayList<>();
    /** Ids unlocked in addition to the ones that start unlocked. */
    public List<String> unlocked = new ArrayList<>();
    /** Saved wardrobes "1".."3": slot name -> cosmetic id. */
    public Map<String, Map<String, String>> presets = new LinkedHashMap<>();
    public int activePreset = 1;
    public String selected;

    /** Bumped on every change; lets the view cache its filtered list. */
    private transient int version;

    public int version() { return version; }

    /** Drops unknown ids and items stored in the wrong slot (old / hand-edited config files). */
    public void sanitize() {
        if (equipped == null) equipped = new LinkedHashMap<>();
        if (favorites == null) favorites = new ArrayList<>();
        if (unlocked == null) unlocked = new ArrayList<>();
        if (presets == null) presets = new LinkedHashMap<>();
        if (activePreset < 1 || activePreset > PRESETS) activePreset = 1;
        favorites.removeIf(id -> CosmeticRegistry.byId(id) == null);
        unlocked.removeIf(id -> CosmeticRegistry.byId(id) == null);
        clean(equipped);
        for (Map<String, String> p : presets.values()) if (p != null) clean(p);
        presets.values().removeIf(p -> p == null);
        if (selected != null && CosmeticRegistry.byId(selected) == null) selected = null;
        // an equipped item must be unlocked
        equipped.values().removeIf(id -> !isUnlocked(CosmeticRegistry.byId(id)));
        version++;
    }

    private static void clean(Map<String, String> m) {
        m.entrySet().removeIf(e -> {
            Cosmetic c = CosmeticRegistry.byId(e.getValue());
            return c == null || CosmeticSlot.byName(e.getKey()) != c.slot;
        });
    }

    // ----------------------------------------------------------------- queries
    public boolean isUnlocked(Cosmetic c) { return c != null && (c.defaultUnlocked || unlocked.contains(c.id)); }

    public boolean isFavorite(Cosmetic c) { return favorites.contains(c.id); }

    public boolean isEquipped(Cosmetic c) { return c.id.equals(equipped.get(c.slot.name())); }

    public Cosmetic wornIn(CosmeticSlot s) { return CosmeticRegistry.byId(equipped.get(s.name())); }

    public int unlockedCount() {
        int n = 0;
        for (Cosmetic c : CosmeticRegistry.ALL) if (isUnlocked(c)) n++;
        return n;
    }

    /** Fills out[slot.ordinal()] with the worn item of every slot (null = empty). No allocation. */
    public void fillWorn(Cosmetic[] out) {
        for (CosmeticSlot s : CosmeticSlot.values()) out[s.ordinal()] = wornIn(s);
    }

    // ----------------------------------------------------------------- changes
    /** Equips c into its slot, replacing whatever was there. False if c is locked. */
    public boolean equip(Cosmetic c) {
        if (!isUnlocked(c)) return false;
        equipped.put(c.slot.name(), c.id);
        version++;
        return true;
    }

    public void unequip(CosmeticSlot s) {
        if (equipped.remove(s.name()) != null) version++;
    }

    public void unequipAll() {
        if (!equipped.isEmpty()) {
            equipped.clear();
            version++;
        }
    }

    public boolean toggleFavorite(Cosmetic c) {
        boolean now = !favorites.remove(c.id);
        if (now) favorites.add(c.id);
        version++;
        return now;
    }

    public void unlock(Cosmetic c) {
        if (!isUnlocked(c)) {
            unlocked.add(c.id);
            version++;
        }
    }

    public void select(Cosmetic c) {
        selected = c == null ? null : c.id;
        version++;
    }

    public boolean hasPreset(int n) {
        Map<String, String> p = presets.get(String.valueOf(n));
        return p != null;
    }

    public void savePreset(int n) {
        presets.put(String.valueOf(n), new LinkedHashMap<>(equipped));
        activePreset = n;
        version++;
    }

    /** Replaces the worn items with a saved wardrobe (items that are no longer unlocked are skipped). */
    public boolean loadPreset(int n) {
        Map<String, String> p = presets.get(String.valueOf(n));
        activePreset = n;
        version++;
        if (p == null) return false;
        equipped.clear();
        for (Map.Entry<String, String> e : p.entrySet()) {
            Cosmetic c = CosmeticRegistry.byId(e.getValue());
            if (c != null && isUnlocked(c) && e.getKey().equals(c.slot.name())) equipped.put(e.getKey(), c.id);
        }
        return true;
    }

    // ------------------------------------------------------- filtering / sorting
    public enum Sort {
        NAME("Name"), RARITY("Rarity"), EQUIPPED("Equipped");
        public final String label;
        Sort(String l) { label = l; }
        public Sort next() { return values()[(ordinal() + 1) % values().length]; }
    }

    /**
     * Fills out with the cosmetics to show. collection = false: only unlocked ones (Wardrobe), true: all 100 (Collection).
     * slot / rarity null = no filter; text = case-insensitive name search.
     */
    public void query(List<Cosmetic> out, CosmeticSlot slot, CosmeticRarity rarity, boolean favOnly, boolean collection,
                      String text, Sort sort) {
        out.clear();
        String q = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);
        for (Cosmetic c : CosmeticRegistry.ALL) {
            if (slot != null && c.slot != slot) continue;
            if (rarity != null && c.rarity != rarity) continue;
            if (favOnly && !isFavorite(c)) continue;
            if (!collection && !isUnlocked(c)) continue;
            if (!q.isEmpty() && !c.name.toLowerCase(Locale.ROOT).contains(q)) continue;
            out.add(c);
        }
        out.sort((a, b) -> {
            int r = 0;
            switch (sort) {
                case RARITY -> r = Integer.compare(b.rarity.ordinal(), a.rarity.ordinal());
                case EQUIPPED -> {
                    r = Boolean.compare(isEquipped(b), isEquipped(a));
                    if (r == 0) r = Integer.compare(b.rarity.ordinal(), a.rarity.ordinal());
                }
                default -> { }
            }
            return r != 0 ? r : a.name.compareToIgnoreCase(b.name);
        });
    }
}
