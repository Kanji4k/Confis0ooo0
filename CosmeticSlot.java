package client.confis;

/** The 10 Cosmetica categories. Each category owns exactly one equipment slot, so two items of the same category can never be worn together. */
public enum CosmeticSlot {
    HAT("Hats", "Hat"),
    HEAD("Head Accessories", "Head"),
    FACE("Face Accessories", "Face"),
    CAPE("Capes", "Cape"),
    WINGS("Wings", "Wings"),
    BACK("Back Accessories", "Back"),
    SHOULDER("Shoulder & Pets", "Shoulder"),
    BODY("Body Accessories", "Body"),
    HAND("Hand Accessories", "Hand"),
    SHOES("Shoes", "Shoes");

    public final String category;   // sidebar / category name
    public final String slotName;   // short slot label shown on the equipment slot

    CosmeticSlot(String category, String slotName) {
        this.category = category;
        this.slotName = slotName;
    }

    public static CosmeticSlot byName(String s) {
        if (s != null) for (CosmeticSlot c : values()) if (c.name().equalsIgnoreCase(s)) return c;
        return null;
    }
}
