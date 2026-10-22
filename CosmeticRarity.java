package client.confis;

public enum CosmeticRarity {
    COMMON("Common", "C", 0xB4BCC8),
    UNCOMMON("Uncommon", "U", 0x4ADE80),
    RARE("Rare", "R", 0x3B9BFF),
    EPIC("Epic", "E", 0xB45CFF),
    LEGENDARY("Legendary", "L", 0xFFB02E);

    public final String label, letter;
    public final int rgb;

    CosmeticRarity(String label, String letter, int rgb) {
        this.label = label;
        this.letter = letter;
        this.rgb = rgb;
    }

    public int argb() { return 0xFF000000 | rgb; }
}
