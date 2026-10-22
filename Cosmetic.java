package client.confis;

/** One cosmetic. Immutable registry data; the mutable parts (unlocked / favorite / equipped) live in CosmeticaState. */
public final class Cosmetic {
    public final String id, name, description;
    public final CosmeticSlot slot;
    public final CosmeticRarity rarity;
    /** Three palette colors (RGB). Icon and 3D model both read them. */
    public final int[] palette;
    /** 16x16 pixel-art icon, drawn from a tiny rectangle script (see CosmeticArt). */
    public final String iconSpec;
    /** 3D model, a list of boxes (see CosmeticArt). */
    public final String modelSpec;
    /** Legendary items start locked; everything else starts unlocked. */
    public final boolean defaultUnlocked;

    Cosmetic(String id, String name, String description, CosmeticSlot slot, CosmeticRarity rarity,
             int[] palette, String iconSpec, String modelSpec) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.slot = slot;
        this.rarity = rarity;
        this.palette = palette;
        this.iconSpec = iconSpec;
        this.modelSpec = modelSpec;
        this.defaultUnlocked = rarity != CosmeticRarity.LEGENDARY;
    }
}
