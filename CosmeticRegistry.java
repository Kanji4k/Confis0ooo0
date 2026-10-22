package client.confis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static client.confis.CosmeticRarity.COMMON;
import static client.confis.CosmeticRarity.EPIC;
import static client.confis.CosmeticRarity.LEGENDARY;
import static client.confis.CosmeticRarity.RARE;
import static client.confis.CosmeticRarity.UNCOMMON;
import static client.confis.CosmeticSlot.*;

/**
 * The one cosmetic registry: 10 categories x 10 items = 100 cosmetics. Every item has its own unique id, palette,
 * 16x16 icon script and 3D box model (see CosmeticArt for the formats). Created once, never modified.
 */
public final class CosmeticRegistry {
    private CosmeticRegistry() {}

    public static final List<Cosmetic> ALL;
    private static final Map<String, Cosmetic> BY_ID = new HashMap<>();
    private static final Map<CosmeticSlot, List<Cosmetic>> BY_SLOT = new HashMap<>();

    public static Cosmetic byId(String id) { return id == null ? null : BY_ID.get(id); }

    public static List<Cosmetic> inSlot(CosmeticSlot s) { return BY_SLOT.get(s); }

    private static final List<Cosmetic> TMP = new ArrayList<>();

    private static void a(CosmeticSlot slot, String name, CosmeticRarity r, String desc, int c0, int c1, int c2, String icon, String model) {
        String id = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_").replaceAll("^_|_$", "");
        TMP.add(new Cosmetic(id, name, desc, slot, r, new int[]{c0, c1, c2}, icon, model));
    }

    // ---- shared model shapes (recolored per item through the palette) ------------------------------------------
    private static final String CAPE_BASE = "-4.2 2 -2.9 8.4 21.4 .9 0 / -4.3 2 -3.0 8.6 1.3 1.0 1 / -4.4 22 -2.9 8.8 1.4 1.4 1";
    private static final String CAPE_PLAIN = CAPE_BASE;
    private static final String CAPE_GEM = CAPE_BASE + " / -1 18.5 -3.25 2 2.4 .35 2 / -.4 19.4 -3.3 .8 .8 .2 3";
    private static final String CAPE_ROYAL = CAPE_BASE + " / -4.7 21.4 -3.1 9.4 2.4 1.8 3 / -3.6 22.2 -3.3 .8 .8 .2 4 / -.4 22.2 -3.3 .8 .8 .2 4 / 2.8 22.2 -3.3 .8 .8 .2 4 / -1 18 -3.25 2 2 .35 1";
    private static final String CAPE_STARS = CAPE_BASE + " / -3 18 -3.15 .7 .7 .2 2 / 1.8 20 -3.15 .7 .7 .2 3 / -1 14 -3.15 .7 .7 .2 2 / 2.2 12.5 -3.15 .7 .7 .2 3 / -2.6 9.5 -3.15 .7 .7 .2 3 / .6 7.5 -3.15 .7 .7 .2 2 / -1.8 4.5 -3.15 .7 .7 .2 2 / 2.4 16.5 -3.15 .5 .5 .2 2 / -3.4 13 -3.15 .5 .5 .2 3";
    private static final String CAPE_TORN = "-4.2 12 -2.9 8.4 11.4 .9 0 / -4.4 22 -2.9 8.8 1.4 1.4 1 / -4.2 4 -2.9 1.7 8 .9 0 / -2.5 2 -2.9 1.7 10 .9 0 / -.8 5.5 -2.9 1.7 6.5 .9 0 / .9 3 -2.9 1.7 9 .9 0 / 2.6 6 -2.9 1.6 6 .9 0 / -1 18 -3.1 2 .6 .2 2";
    private static final String CAPE_FUR = CAPE_BASE + " / -4.9 21 -3.2 9.8 3 2.4 3 / -3.6 .3 -2.9 .8 1.8 .9 3 / -1.6 .1 -2.9 .8 2 .9 3 / .6 .3 -2.9 .8 1.8 .9 3 / 2.6 .1 -2.9 .8 2 .9 3";
    private static final String CAPE_RAINBOW = "-4.2 2 -2.9 8.4 3.2 .9 #E53935 / -4.2 5 -2.9 8.4 3.2 .9 #FB8C00 / -4.2 8 -2.9 8.4 3.2 .9 #FDD835 / -4.2 11 -2.9 8.4 3.2 .9 #43A047 / -4.2 14 -2.9 8.4 3.2 .9 #1E88E5 / -4.2 17 -2.9 8.4 3.2 .9 #5E35B1 / -4.2 20 -2.9 8.4 3.2 .9 #8E24AA / -4.4 22 -2.9 8.8 1.4 1.4 3";

    private static final String BOOTS = "M|-4.3 .2 -2.4 4.6 4.2 4.8 0 / -4.4 3.4 -2.5 4.8 1 5 1 / -4.4 0 -2.6 4.8 .8 5.2 1";
    private static final String BOOTS_SPIKE = "M|-4.3 .2 -2.4 4.6 4.2 4.8 0 / -4.4 3.4 -2.5 4.8 1 5 1 / -4.4 0 -2.6 4.8 .8 5.2 1 / -4.6 4.4 -1.6 .6 1.4 .6 2 / -4.6 4.4 .4 .6 1.4 .6 2 / -3.2 4.4 -2.7 .6 1.6 .6 2";
    private static final String BOOTS_FUR = "M|-4.3 .2 -2.4 4.6 4.2 4.8 0 / -4.4 0 -2.6 4.8 .8 5.2 1 / -4.7 3.8 -2.7 5.4 1.8 5.4 3 / -4.5 .8 2.45 .8 .8 .3 3";
    private static final String BOOTS_CUFF = "M|-4.3 .2 -2.4 4.6 6.2 4.8 0 / -4.4 5.4 -2.5 4.8 1.2 5 1 / -4.4 0 -2.6 4.8 .8 5.2 1 / -2.9 3 2.45 1.3 1.3 .3 2";
    private static final String SNEAKER = "M|-4.3 .6 -2.4 4.6 2.2 5.4 0 / -4.4 0 -2.5 4.8 .8 5.6 3 / -4.3 2.8 -2.2 4.6 1.2 2.2 1 / -3.8 2.8 .1 3.6 .4 .3 3 / -3.8 2.2 .3 3.6 .4 .3 3";
    private static final String CLOUD = "B|-4.6 .6 -2.7 5.2 2.2 5.4 0 / -4.3 2.6 -2.4 4.6 1.4 4.6 0 / -3.4 3.7 -1.6 2.8 1 2.8 1 / -5 1 .6 1.6 1.4 1.6 0 / -3.2 -.4 -2 3.4 .8 4 1";
    private static final String BOOTS_MAGMA = "M|-4.3 .2 -2.4 4.6 4.2 4.8 0 / -4.4 3.4 -2.5 4.8 1 5 1 / -4.4 0 -2.6 4.8 .8 5.2 1 / -3.2 1.4 2.45 .5 2 .3 2 / -2 .8 2.45 .5 2.4 .3 2 / -1 2.2 2.45 .5 1.4 .3 2";

    private static final String WING_FEATHER = "M|-6.5 14 -3.8 3 9 .9 0 / -9.5 16 -3.6 3 11 .9 0 / -12.5 18 -3.4 3 11 .9 0 / -15 20 -3.2 2.6 9 .9 0 / -17.2 22 -3.0 2 6 .9 1 / -3.6 17 -3.0 3 6 .9 0";
    private static final String WING_BAT = "M|-6.5 12 -3.5 3 12 .6 0 / -9.5 14 -3.5 3 10 .6 0 / -12.5 16 -3.5 3 8 .6 0 / -15 18 -3.5 2.5 6 .6 0 / -15.2 24 -3.6 11.7 1 .9 1 / -9.6 14 -3.7 .6 10 .5 1 / -12.6 16 -3.7 .6 8 .5 1";
    private static final String WING_BUTTERFLY = "M|-12 18 -3.4 8.5 8 .5 0 / -14 22 -3.4 4 4 .5 0 / -10 12 -3.4 6.5 6.5 .5 1 / -12 14 -3.4 3 3 .5 1 / -10 22 -3.5 2 2 .2 2 / -8 15 -3.5 1.6 1.6 .2 3";
    private static final String WING_DRAGON = "M|-7 12 -3.5 3.2 13 .6 0 / -10.2 13 -3.5 3.2 12 .6 0 / -13.4 15 -3.5 3.2 10 .6 0 / -16.6 17 -3.5 3 8 .6 0 / -18 25 -3.2 14.5 1.2 1.2 1 / -18 16 -3.2 1.2 10 1.2 1 / -18.8 25.6 -3.2 1 1.2 1 3";
    private static final String WING_CRYSTAL = "M|-5 14 -3.4 1.6 11 1 0 / -7 15 -3.4 1.6 14 1 1 / -9 16 -3.4 1.6 16 1 0 / -11 18 -3.4 1.6 15 1 1 / -13 20 -3.4 1.4 11 1 0 / -14.8 22 -3.4 1.2 7 1 1";
    private static final String WING_SPIKED = "M|-14 12 -3.4 10.5 1.2 1 0 / -16 16 -3.4 12.5 1.2 1 0 / -17 20 -3.4 13.5 1.2 1 0 / -15 24 -3.4 11.5 1.2 1 0 / -16.5 12 -3.4 2.5 1.2 1 2 / -18.5 16 -3.4 2.5 1.2 1 2 / -19.5 20 -3.4 2.5 1.2 1 2 / -17.5 24 -3.4 2.5 1.2 1 2";
    private static final String WING_ENDER = "M|-8 14 -3.4 4.5 10 .8 0 / -12 16 -3.4 4 9 .8 0 / -15.5 19 -3.4 3.5 7 .8 0 / -10 20 -3.5 1 1 .2 2 / -13.5 22 -3.5 1 1 .2 2 / -6.5 18 -3.5 1 1 .2 2 / -5 22 -3.4 1.4 3 .8 1";
    private static final String WING_ICICLE = "M|-5 10 -3.4 1.4 12 1 0 / -7 8 -3.4 1.4 15 1 1 / -9 10 -3.4 1.4 17 1 0 / -11 12 -3.4 1.4 15 1 1 / -13 15 -3.4 1.4 11 1 0 / -15 18 -3.4 1.4 8 1 1 / -5 22 -3.4 11 1.4 1 0";
    private static final String WING_PHOENIX = "M|-6.5 14 -3.8 3 9 .9 0 / -9.5 16 -3.6 3 11 .9 0 / -12.5 18 -3.4 3 11 .9 1 / -15 20 -3.2 2.6 9 .9 1 / -17 23 -3.0 2 7 .9 2 / -9.5 27 -3.6 2 2.5 .8 2 / -12.5 29 -3.4 2 2.5 .8 2 / -15 29 -3.2 2 2.5 .8 2 / -6.5 11 -3.8 3 3 .9 2";
    private static final String WING_CYBER = "M|-6 12 -3.4 .8 15 .6 0 / -8.5 13 -3.4 .8 16 .6 0 / -11 15 -3.4 .8 16 .6 0 / -13.5 17 -3.4 .8 15 .6 0 / -14 27 -3.4 10.5 .8 .6 1 / -13.5 17 -3.4 10 .8 .6 1";

    private static final String NECKLACE = "-3.2 22.4 2.1 2.4 .5 .4 0 / .8 22.4 2.1 2.4 .5 .4 0 / -1.6 21.6 2.1 3.2 .5 .4 0 / -.7 20.2 2.1 1.4 1.4 .4 1";
    private static final String SWORD = "-6.5 14 2.6 1 8 .8 0 / -6.5 22 2.6 1 1 .8 3 / -7.7 13.2 2.4 3.4 1 1.2 1 / -6.5 11.4 2.6 1 1.8 .8 2 / -6.7 10.8 2.5 1.4 .8 1 1";

    static {
        // =========================================================== 1. HATS
        a(HAT, "Crown of Kings", LEGENDARY, "A towering crown with royal arches and a sapphire orb.", 0xF5C542, 0xD7263D, 0x3BA7FF,
                "0,3,10,10,3 0,3,6,2,4 0,11,6,2,4 0,7,4,2,6 0,7,1,2,3 0,6,2,4,1 2,7,2,2,1 1,5,11,1,1 1,10,11,1,1 2,7,11,2,1 3,4,6,1,1 3,12,6,1,1",
                "-4.6 32 -4.6 9.2 2 9.2 0 / -4.6 34 -4.6 1.8 2.8 1.8 0 / 2.8 34 -4.6 1.8 2.8 1.8 0 / -4.6 34 2.8 1.8 2.8 1.8 0 / 2.8 34 2.8 1.8 2.8 1.8 0 / -4.6 36.8 -.8 9.2 .8 1.6 0 / -.8 36.4 -4.6 1.6 .8 9.2 0 / -1.1 37.6 -1.1 2.2 2.2 2.2 2 / -1 32.4 4.6 2 1.2 .3 1");
        a(HAT, "Wizard Hat", RARE, "A tall, star-marked hat full of old magic.", 0x4B3FA8, 0xF5C542, 0xFFF3A0,
                "0,7,1,2,2 0,6,3,4,2 0,5,5,6,2 0,4,7,8,2 1,4,9,8,1 0,1,10,14,2 2,8,3,1,3 2,7,4,3,1",
                "-6.5 32 -6.5 13 .9 13 0 / -4.2 32.9 -4.2 8.4 3 8.4 0 / -3.2 35.9 -3.2 6.4 3 6.4 0 / -2.2 38.9 -2.2 4.4 3 4.4 0 / -1.2 41.9 -1.2 2.4 3 2.4 0 / -4.3 32.9 -4.3 8.6 1 8.6 1 / -.5 36.5 3.25 1 1 .3 2");
        a(HAT, "Classic Top Hat", RARE, "Formal, tall, and always in fashion.", 0x1E1E26, 0xB02A37, 0xF5C542,
                "0,4,2,8,9 1,4,8,8,2 0,1,11,14,2 3,5,3,1,5 2,7,8,2,2",
                "-6 32 -6 12 1 12 0 / -4 33 -4 8 7.5 8 0 / -4.1 33 -4.1 8.2 1.8 8.2 1 / -1 34.2 4.1 2 1.2 .3 2");
        a(HAT, "Santa Hat", UNCOMMON, "Festive red hat with a fluffy white pom-pom.", 0xD7263D, 0xFFFFFF, 0xFFFFFF,
                "0,5,5,7,2 0,4,7,9,3 3,3,10,11,3 0,10,4,2,2 3,10,2,3,3",
                "-4.6 32 -4.6 9.2 1.6 9.2 3 / -4.1 33.6 -4.1 8.2 3 8.2 0 / -2.5 36.6 -3 5 2 4 0 / -2 38.4 -4.2 4 1.8 3.2 0 / -1.5 36.6 -7.8 3 3 3 3");
        a(HAT, "Diamond Helmet", RARE, "A gleaming helm forged from pure diamond.", 0x4AEDD9, 0x2A9C94, 0x1A5F5B,
                "0,3,3,10,9 4,5,7,6,3 1,3,3,10,1 1,3,10,3,2 1,10,10,3,2 3,4,4,3,1",
                "-4.6 30.4 -4.6 9.2 1.8 9.2 0 / -4.6 24 -4.6 1.6 6.6 9.2 0 / 3 24 -4.6 1.6 6.6 9.2 0 / -3 24 -4.6 6 6.6 1.6 0 / -3 29 4.4 6 1.4 .4 1 / -.7 25.5 4.4 1.4 3.5 .4 1");
        a(HAT, "Golden Crown", EPIC, "A ring of golden spikes set with fine gems.", 0xF5C542, 0xE53935, 0x3BA7FF,
                "0,2,10,12,3 0,2,6,2,4 0,5,4,2,6 0,7,3,2,7 0,9,4,2,6 0,12,6,2,4 2,7,11,2,1 1,3,11,1,1 1,12,11,1,1 3,3,7,1,1 3,8,4,1,1",
                "-4.6 32 -4.6 9.2 1.6 9.2 0 / -4.6 33.6 -4.6 1.8 3 1.8 0 / 2.8 33.6 -4.6 1.8 3 1.8 0 / -4.6 33.6 2.8 1.8 3 1.8 0 / 2.8 33.6 2.8 1.8 3 1.8 0 / -.9 33.6 2.8 1.8 2 1.8 0 / -.9 33.6 -4.6 1.8 2 1.8 0 / -4.6 33.6 -.9 1.8 2 1.8 0 / 2.8 33.6 -.9 1.8 2 1.8 0 / -.9 32.2 4.6 1.8 1 .3 2 / -3.8 32.2 4.6 1 1 .3 1 / 2.8 32.2 4.6 1 1 .3 1");
        a(HAT, "Cowboy Hat", UNCOMMON, "Wide brim, wild west attitude.", 0x8B5A2B, 0x4A2F17, 0xF5C542,
                "0,5,3,6,6 1,5,8,6,1 0,1,9,14,2 0,0,7,3,3 0,13,7,3,3 1,6,4,4,1",
                "-7.5 32 -5.5 15 .8 11 0 / -4.1 32.8 -4.1 8.2 4 8.2 0 / -1.2 36.6 -4.1 2.4 .5 8.2 1 / -4.2 32.8 -4.2 8.4 1.2 8.4 1 / -7.7 32.8 -5.5 .8 1.6 11 0 / 6.9 32.8 -5.5 .8 1.6 11 0");
        a(HAT, "Black Beanie", COMMON, "Warm, simple, and goes with everything.", 0x24262E, 0x3A3D48, 0xFFFFFF,
                "0,3,5,10,6 1,3,10,10,3 4,4,10,1,3 4,7,10,1,3 4,10,10,1,3 3,7,2,2,3 0,4,4,8,1",
                "-4.6 31 -4.6 9.2 3.4 9.2 0 / -4.7 31 -4.7 9.4 1.8 9.4 1 / -1 34.4 -1 2 1.2 2 0 / -1.5 35.2 -1.5 3 2.4 3 3");
        a(HAT, "Party Hat", COMMON, "Every day is a celebration.", 0xE91E8C, 0x3BA7FF, 0xFFD54A,
                "2,7,0,2,2 0,7,2,2,2 0,6,4,4,2 0,5,6,6,2 0,4,8,8,2 0,3,10,10,2 1,6,5,2,1 1,5,7,2,1 1,4,9,2,1 1,9,9,2,1",
                "-2.6 32 -2.6 5.2 1.8 5.2 0 / -2 33.8 -2 4 1.8 4 1 / -1.4 35.6 -1.4 2.8 1.8 2.8 0 / -.8 37.4 -.8 1.6 1.6 1.6 1 / -1 39 -1 2 2 2 2");
        a(HAT, "Dragon Horn Helmet", EPIC, "Iron helm crowned with two curved dragon horns.", 0x7B8594, 0xE8E2CF, 0xFF8A3D,
                "0,4,5,8,7 4,5,8,6,2 1,2,3,2,6 1,12,3,2,6 3,2,2,1,2 3,13,2,1,2 2,7,5,2,1",
                "M|-4.6 30.2 -4.6 9.2 2 9.2 0 / -4.6 24 -4.6 1.4 6.2 9.2 0 / -3.2 24 -4.6 6.4 6.2 1.4 0 / -3 29.2 4.4 6 1.2 .4 1 / -6.6 31.4 -1 2.2 2 2 1 / -8.2 33 -1 2 3 2 1 / -9 35.6 -1 1.6 3 1.6 1");

        // ===================================================== 2. HEAD ACCESSORIES
        a(HEAD, "Neon Headphones", RARE, "Studio headphones with glowing rings.", 0x23252E, 0x3B3F4C, 0x00F0FF,
                "0,3,3,10,1 0,2,4,2,6 0,12,4,2,6 1,1,8,3,5 1,12,8,3,5 2,2,9,1,3 2,13,9,1,3",
                "M|-5.6 26 -2.2 1.6 4.4 4.4 0 / -5.1 29.5 -.5 1 3.3 1 1 / -5.1 32.4 -.5 5.2 1 1 1 / -6.1 27 -1.4 .4 2.4 2.4 2");
        a(HEAD, "Golden Halo", LEGENDARY, "A radiant golden ring that floats above your head.", 0xFFD54A, 0xFFF3A0, 0xFFFFFF,
                "0,4,4,8,1 0,3,5,2,1 0,11,5,2,1 0,2,6,1,2 0,13,6,1,2 0,3,8,2,1 0,11,8,2,1 0,4,9,8,1 3,6,4,2,1",
                "B|-3.5 34.5 -3.5 7 .7 1 0 / -3.5 34.5 2.5 7 .7 1 0 / -3.5 34.5 -2.5 1 .7 5 0 / 2.5 34.5 -2.5 1 .7 5 0 / -3.8 34.2 -3.8 .6 .2 .6 2");
        a(HEAD, "Demon Horns", RARE, "Curved crimson horns with glowing tips.", 0x8E1B2B, 0x5A0F1B, 0xFF5A36,
                "0,4,10,3,3 0,3,7,3,3 0,3,5,2,2 0,2,3,2,2 1,2,2,1,1 M 4,5,12,6,2",
                "M|-4 32 -1.4 2.4 1.6 2.8 0 / -5.2 33.4 -1.2 2.4 2 2.4 0 / -6.2 35 -1 1.8 2.6 2 1 / -6.4 37.4 -.8 1.2 1.6 1.4 2");
        a(HEAD, "Angel Halo", EPIC, "A pale, shimmering halo blessed by the heavens.", 0xFFFFFF, 0xBFEFFF, 0x7FD8FF,
                "0,3,6,10,1 0,4,5,8,1 0,4,8,8,1 0,2,7,1,1 0,13,7,1,1 1,5,6,6,1 3,13,2,1,3 3,12,3,3,1",
                "B|-4 35 -4 8 .5 .8 0 / -4 35 3.2 8 .5 .8 0 / -4 35 -3.2 .8 .5 6.4 0 / 3.2 35 -3.2 .8 .5 6.4 0 / -4.4 35.4 -4.4 .4 .6 .4 2 / 4 35.4 4 .4 .6 .4 2");
        a(HEAD, "Cat Ears", COMMON, "Two perky ears for the feline-inclined.", 0x3A3A44, 0xFF8FB8, 0xFFFFFF,
                "0,2,6,4,5 0,3,4,2,2 0,10,6,4,5 0,11,4,2,2 1,3,7,2,3 1,11,7,2,3",
                "M|-4 32 -1.6 2.8 1.8 3 0 / -3.6 33.6 -1.2 2 1.6 2.2 0 / -3.4 32.2 1.3 1.6 1.4 .3 1");
        a(HEAD, "Bunny Ears", COMMON, "Long, soft ears that bounce with every step.", 0xF4EFEA, 0xFFA8C4, 0xFFFFFF,
                "0,3,1,3,10 0,10,1,3,10 1,4,3,1,7 1,11,3,1,7",
                "M|-3.4 32 -1.2 2 6.4 1.8 0 / -3 32.4 .5 1.2 5.2 .3 1");
        a(HEAD, "Floating Crystal", EPIC, "A crystal that hovers and hums above you.", 0x56D6FF, 0xB6F0FF, 0xFFFFFF,
                "0,7,1,2,2 0,6,3,4,3 0,5,6,6,3 0,6,9,4,3 0,7,12,2,2 3,7,3,1,4 1,8,5,1,6",
                "B|-1 35.5 -1 2 1.2 2 0 / -1.8 36.7 -1.8 3.6 2.4 3.6 0 / -1 39.1 -1 2 1.4 2 0 / -.5 37 1.9 1 1.6 .3 1");
        a(HEAD, "Glowing Stars", RARE, "Little stars orbiting your head.", 0xFFD54A, 0xFF8AE2, 0x7FF7FF,
                "0,4,2,1,5 0,2,4,5,1 1,11,4,1,5 1,9,6,5,1 2,7,9,1,5 2,5,11,5,1 3,4,4,1,1 3,11,6,1,1 3,7,11,1,1",
                "BO|-5.5 34 0 1.6 1.6 1.6 0 / 4 35.5 -.8 1.6 1.6 1.6 1 / -.8 37 4 1.6 1.6 1.6 2 / -.8 35 -5 1.6 1.6 1.6 0");
        a(HEAD, "Cyber Antenna", UNCOMMON, "A tiny antenna that pulses with signal.", 0x6B7280, 0xFF3860, 0x00F0FF,
                "0,7,2,2,9 1,6,0,4,3 2,7,1,2,1 0,4,11,8,2 3,7,0,1,1",
                "-.4 32 -.4 .8 3.6 .8 0 / -1 35.6 -1 2 2 2 1 / -1.6 32 -1.6 3.2 .5 3.2 2 / -.4 37.6 -.4 .8 1 .8 3");
        a(HEAD, "Mini Crown", UNCOMMON, "A small crown for a modest ruler.", 0xF5C542, 0xE53935, 0x3BA7FF,
                "0,4,9,8,3 0,4,6,2,3 0,7,5,2,4 0,10,6,2,3 2,7,10,2,1 3,5,10,1,1",
                "-2.6 32 -2.6 5.2 1 5.2 0 / -2.6 33 -2.6 1.2 1.4 1.2 0 / 1.4 33 -2.6 1.2 1.4 1.2 0 / -2.6 33 1.4 1.2 1.4 1.2 0 / 1.4 33 1.4 1.2 1.4 1.2 0 / -.6 32.2 2.6 1.2 .6 .3 2");

        // ====================================================== 3. FACE ACCESSORIES
        a(FACE, "Black Sunglasses", UNCOMMON, "Dark lenses. Cool factor: maximum.", 0x14141A, 0x2B2B35, 0xFFFFFF,
                "0,2,5,5,3 0,9,5,5,3 1,7,6,2,1 1,1,5,1,1 1,14,5,1,1 3,3,6,1,1 3,10,6,1,1",
                "-4.4 27.4 4.05 3.8 2.2 .5 0 / .6 27.4 4.05 3.8 2.2 .5 0 / -.6 28.4 4.05 1.2 .6 .5 1 / -4.5 28.4 -4 .5 .6 8 1 / 4 28.4 -4 .5 .6 8 1 / -3.8 28.8 4.5 .8 .6 .15 3 / 1.2 28.8 4.5 .8 .6 .15 3");
        a(FACE, "Pixel Glasses", UNCOMMON, "Square, blocky frames with a cyan tint.", 0x7FE9FF, 0x2A2F3C, 0xFFFFFF,
                "1,2,4,5,1 1,2,8,5,1 1,2,4,1,5 1,6,4,1,5 1,9,4,5,1 1,9,8,5,1 1,9,4,1,5 1,13,4,1,5 0,3,5,3,3 0,10,5,3,3 1,7,5,2,1 3,3,5,1,1 3,10,5,1,1",
                "-4.5 27 4.05 4 3 .5 1 / .5 27 4.05 4 3 .5 1 / -3.7 27.6 4.4 2.4 1.8 .2 0 / 1.3 27.6 4.4 2.4 1.8 .2 0 / -.5 28.6 4.05 1 .6 .5 1");
        a(FACE, "White Mask", RARE, "A smooth, expressionless porcelain mask.", 0xF4F1EA, 0xD9455B, 0xFFFFFF,
                "0,3,2,10,12 4,4,6,3,2 4,9,6,3,2 4,6,11,4,1 1,7,8,2,2 3,4,3,2,1",
                "-4.3 24.3 4.05 8.6 7.4 .6 0 / -3 28.2 4.6 2 1.2 .2 4 / 1 28.2 4.6 2 1.2 .2 4 / -1.5 25.6 4.6 3 .6 .2 1");
        a(FACE, "Ninja Mask", RARE, "Silent cloth wrap with a bold red band.", 0x20222B, 0xC62828, 0xFFFFFF,
                "0,3,2,10,12 1,3,3,10,2 3,4,6,8,2 4,5,6,2,2 4,9,6,2,2",
                "-4.4 24 -4.4 8.8 3.6 8.8 0 / -4.4 29 -4.4 8.8 1.8 8.8 1");
        a(FACE, "Creeper Mask", RARE, "That unmistakable face. Sssss...", 0x4CAF50, 0x2E7D32, 0x1B5E20,
                "0,3,2,10,12 1,3,2,10,1 4,4,5,3,3 4,9,5,3,3 4,7,8,2,3 4,6,10,2,3 4,8,10,2,3",
                "-4.3 24 4.05 8.6 8 .6 0 / -3.2 27.8 4.65 2.2 2.2 .2 4 / 1 27.8 4.65 2.2 2.2 .2 4 / -1 26 4.65 2 2.2 .2 4 / -2 24.8 4.65 1 1.4 .2 4 / 1 24.8 4.65 1 1.4 .2 4");
        a(FACE, "Golden Monocle", LEGENDARY, "A refined golden lens with a dangling chain.", 0xFFD54A, 0xFFF3A0, 0xFFFFFF,
                "0,6,4,5,1 0,5,5,1,3 0,11,5,1,3 0,6,8,5,1 3,7,5,1,1 1,11,8,1,6 1,12,13,1,2",
                "0.8 27.2 4.05 3 .5 .4 0 / .8 29.7 4.05 3 .5 .4 0 / .8 27.2 4.05 .5 3 .4 0 / 3.3 27.2 4.05 .5 3 .4 0 / 3.6 24 4.05 .3 3.2 .3 1");
        a(FACE, "Red Bandana", UNCOMMON, "A dusty red bandana pulled up over the nose.", 0xC62828, 0x8E1B1B, 0xFFFFFF,
                "0,2,3,12,3 0,3,6,10,2 0,4,8,8,2 0,5,10,6,1 0,7,11,2,2 3,4,4,1,1 3,8,5,1,1 3,11,4,1,1 3,6,7,1,1 3,9,8,1,1",
                "-4.4 24 -4.4 8.8 3 8.8 0 / -3.2 22.8 4.4 6.4 1.2 .4 0 / -2.4 25.5 4.7 .7 .7 .2 3 / 1.5 26 4.7 .7 .7 .2 3");
        a(FACE, "Blue Face Mask", COMMON, "A simple surgical-style face mask.", 0x5BA8E8, 0x3F7DB5, 0xFFFFFF,
                "0,3,5,10,6 1,4,7,8,1 1,4,9,8,1 3,2,6,1,5 3,13,6,1,5",
                "-4.2 24.6 4.05 8.4 3.6 .5 0 / -4.2 25.4 4.5 8.4 .3 .2 1 / -4.2 26.6 4.5 8.4 .3 .2 1 / -4.5 26 -4 .4 .5 8 1 / 4.1 26 -4 .4 .5 8 1");
        a(FACE, "Neon Visor", EPIC, "A wrap-around visor with a glowing strip.", 0x00E5FF, 0x23252E, 0xFF2BD6,
                "0,1,5,14,5 1,1,4,14,1 1,1,10,14,1 3,3,6,10,1 2,4,8,8,1",
                "-4.5 27 4.05 9 2.6 .9 0 / -4.6 27 -4.6 .9 2.6 8.7 1 / 3.7 27 -4.6 .9 2.6 8.7 1 / -3.8 27.8 4.95 7.6 .8 .15 3");
        a(FACE, "Skull Mask", EPIC, "A bleached skull mask. Spooky, but stylish.", 0xE8E2CF, 0xB8B2A0, 0xFFFFFF,
                "0,3,2,10,10 0,4,12,8,2 4,4,5,3,3 4,9,5,3,3 4,7,8,2,2 1,5,12,1,2 1,7,12,1,2 1,9,12,1,2",
                "-4.3 24 4.05 8.6 8 .6 0 / -3.3 27.4 4.65 2.4 2.2 .2 4 / .9 27.4 4.65 2.4 2.2 .2 4 / -.5 26.2 4.65 1 1 .2 4 / -2.4 24.4 4.65 .5 1.5 .2 4 / -.8 24.4 4.65 .5 1.5 .2 4 / .9 24.4 4.65 .5 1.5 .2 4 / 2.3 24.4 4.65 .5 1.5 .2 4");

        // =============================================================== 4. CAPES
        a(CAPE, "Royal Cape", RARE, "Crimson velvet with a white fur collar.", 0xB0172B, 0xF5C542, 0xFFFFFF,
                "3,3,1,10,2 4,4,2,1,1 4,7,2,1,1 4,10,2,1,1 0,3,3,10,11 1,7,3,2,2 1,3,13,10,1",
                CAPE_ROYAL);
        a(CAPE, "Ender Cape", EPIC, "Purple night with watching eyes.", 0x2A1245, 0x7B2FBE, 0xE05CFF,
                "0,3,1,10,13 1,4,3,3,2 1,9,6,3,2 1,5,9,3,2 2,5,3,1,1 2,10,6,1,1 2,6,9,1,1 1,3,13,10,1",
                CAPE_STARS);
        a(CAPE, "Nether Cape", UNCOMMON, "Scorched red cloth edged in ember orange.", 0x7A1414, 0xFF7A1A, 0xFFC247,
                "0,3,1,10,13 1,3,13,10,1 1,4,4,2,3 1,9,7,3,3 2,4,10,3,2 2,8,2,1,2",
                CAPE_PLAIN);
        a(CAPE, "Diamond Cape", RARE, "A shimmering cyan cape with a diamond clasp.", 0x35C8C0, 0x1E8A85, 0xBFFFF8,
                "0,3,1,10,13 1,3,1,10,1 1,3,13,10,1 2,7,5,2,1 2,6,6,4,2 2,7,8,2,1 3,7,6,1,1",
                CAPE_GEM);
        a(CAPE, "Ruby Cape", COMMON, "Deep ruby red, simple and bold.", 0xB3123A, 0x7A0C27, 0xFF5C84,
                "0,3,1,10,13 1,3,13,10,1 1,3,1,10,1 2,7,4,2,2 3,7,4,1,1",
                CAPE_PLAIN);
        a(CAPE, "Galaxy Cape", LEGENDARY, "An entire night sky stitched into a cape.", 0x120B2E, 0x5B2BA8, 0xFFFFFF,
                "0,3,1,10,13 1,4,3,4,4 1,7,8,5,4 2,5,2,1,1 2,10,4,1,1 2,7,7,1,1 2,11,10,1,1 2,5,11,1,1 2,9,12,1,1 3,4,5,1,1",
                CAPE_STARS);
        a(CAPE, "Shadow Cape", RARE, "A ragged cape woven from living shadow.", 0x15151C, 0x2A2A38, 0x8C5CFF,
                "0,3,1,10,12 0,3,13,2,2 0,6,13,2,3 0,9,13,2,2 0,12,13,1,2 1,4,4,3,1 1,9,7,2,1 2,6,3,1,1",
                CAPE_TORN);
        a(CAPE, "Frost Cape", UNCOMMON, "A fur-trimmed cape dusted with frost.", 0x8CC8F0, 0x5A9ACB, 0xFFFFFF,
                "0,3,3,10,11 3,3,1,10,3 3,3,3,1,1 3,5,3,1,1 3,7,3,1,1 3,9,3,1,1 3,11,3,1,1 1,4,13,1,2 1,7,13,1,2 1,10,13,1,2",
                CAPE_FUR);
        a(CAPE, "Emerald Cape", UNCOMMON, "Rich green cloth fastened with an emerald.", 0x1E9E4F, 0x14693A, 0x6BFFA0,
                "0,3,1,10,13 1,3,13,10,1 1,3,1,10,1 2,6,5,4,4 3,7,6,1,1",
                CAPE_GEM);
        a(CAPE, "Rainbow Cape", EPIC, "Every color of the rainbow in a single cape.", 0xE53935, 0xFFFFFF, 0xFFFFFF,
                "#E53935,3,1,10,2 #FB8C00,3,3,10,2 #FDD835,3,5,10,2 #43A047,3,7,10,2 #1E88E5,3,9,10,2 #5E35B1,3,11,10,2 #8E24AA,3,13,10,1",
                CAPE_RAINBOW);

        // ============================================================== 5. WINGS
        a(WINGS, "Angel Wings", RARE, "Soft white feathers fanning out behind you.", 0xFFFFFF, 0xDCE9F5, 0xFFD54A,
                "0,6,5,2,8 0,4,3,2,10 0,2,2,2,10 0,0,4,2,8 1,5,12,1,2 1,3,12,1,2 M",
                WING_FEATHER);
        a(WINGS, "Demon Wings", RARE, "Leathery crimson wings with bony ribs.", 0x7A1020, 0x2A0A10, 0xFF4A3D,
                "1,0,3,7,1 0,1,4,2,4 0,3,4,2,6 0,5,4,2,8 1,7,3,1,10 1,0,3,1,3 M",
                WING_BAT);
        a(WINGS, "Butterfly Wings", COMMON, "Delicate, patterned butterfly wings.", 0xFF8A3D, 0xFFD54A, 0x2A1F3D,
                "0,0,2,6,5 1,2,8,4,4 2,2,3,2,2 3,3,9,1,1 M 4,7,3,2,10",
                WING_BUTTERFLY);
        a(WINGS, "Dragon Wings", EPIC, "Huge, scaled wings with clawed tips.", 0x2E7D4F, 0x1B4A30, 0xE8E2CF,
                "1,0,2,8,1 0,1,3,7,1 0,1,4,2,6 0,3,5,2,7 0,5,6,2,8 1,7,3,1,10 3,0,1,1,2 M",
                WING_DRAGON);
        a(WINGS, "Crystal Wings", RARE, "Wings of glittering, razor-sharp crystal.", 0x6EE0FF, 0xB48CFF, 0xFFFFFF,
                "0,6,9,2,6 0,4,6,2,9 1,2,4,2,11 0,0,3,2,12 3,3,6,1,3 M",
                WING_CRYSTAL);
        a(WINGS, "Nether Wings", UNCOMMON, "Jagged wings with glowing lava tips.", 0x5C1010, 0x2B0808, 0xFF8A1F,
                "0,7,3,1,4 0,5,5,2,8 0,3,2,2,10 0,1,5,2,7 2,1,12,1,2 2,3,12,1,2 2,5,13,1,1 1,6,4,1,6 M",
                WING_SPIKED);
        a(WINGS, "Ender Wings", UNCOMMON, "Dark, angular wings with violet eyes.", 0x1B1230, 0x3A2466, 0xE05CFF,
                "0,4,4,4,6 0,2,6,3,6 0,0,8,3,4 2,3,7,1,1 2,5,8,1,1 2,1,9,1,1 1,5,3,3,1 M",
                WING_ENDER);
        a(WINGS, "Ice Wings", COMMON, "Frozen wings that hang like icicles.", 0xA9E4FF, 0xDFF6FF, 0xFFFFFF,
                "0,7,2,1,6 0,5,3,2,8 0,3,3,2,11 0,1,4,2,10 3,6,4,1,5 3,4,5,1,5 3,2,6,1,5 M",
                WING_ICICLE);
        a(WINGS, "Phoenix Wings", LEGENDARY, "Wings of living flame that never burn out.", 0xFF7A1A, 0xFFB02E, 0xFFE066,
                "0,6,3,2,6 1,4,2,2,8 0,2,1,2,10 2,0,0,2,12 2,2,10,2,2 M",
                WING_PHOENIX);
        a(WINGS, "Neon Cyber Wings", EPIC, "Wings drawn in glowing neon light bars.", 0x00F0FF, 0xFF2BD6, 0xFFFFFF,
                "0,7,3,1,9 0,5,4,1,9 0,3,5,1,9 0,1,6,1,9 1,1,5,7,1 2,1,14,7,1 M",
                WING_CYBER);

        // ===================================================== 6. BACK ACCESSORIES
        a(BACK, "Explorer Backpack", COMMON, "A sturdy pack for long expeditions.", 0x8B5A2B, 0x5E3A18, 0xF5C542,
                "0,3,3,10,11 1,3,3,10,4 1,5,9,6,4 2,7,6,2,2 2,2,5,1,8 2,13,5,1,8 2,6,1,4,2",
                "-3.2 12.5 -5.6 6.4 9 3.6 0 / -3.3 18.5 -5.8 6.6 3.5 3.8 1 / -2 14 -6 4 4 .5 1 / -.8 19 -6.1 1.6 1.4 .3 2");
        a(BACK, "Mini Creeper Backpack", UNCOMMON, "A little creeper hanging on to your back.", 0x4CAF50, 0x2E7D32, 0x1B5E20,
                "0,3,3,10,11 4,5,5,2,2 4,9,5,2,2 4,7,7,2,3 4,6,10,1,2 4,9,10,1,2 1,2,4,1,9 1,13,4,1,9",
                "-3.2 12.5 -5.6 6.4 9 3.6 0 / -2.4 18 -6 1.8 1.8 .3 4 / .6 18 -6 1.8 1.8 .3 4 / -.6 15.5 -6 1.2 2.6 .3 4 / -1.4 14.5 -6 .8 1.6 .3 4 / .6 14.5 -6 .8 1.6 .3 4");
        a(BACK, "Jetpack", LEGENDARY, "Twin boosters with blazing exhaust.", 0xC8CDD6, 0x6B7280, 0xFF8A1F,
                "0,3,2,4,9 0,9,2,4,9 1,3,1,4,1 1,9,1,4,1 1,2,11,5,2 1,8,11,5,2 2,4,13,2,2 2,10,13,2,2 1,7,5,2,2",
                "M|-3.8 11.5 -5.6 2.8 10.5 3 0 / -3.9 21.5 -5.7 3 .8 3.2 1 / -3.5 10.2 -5.4 2.2 1.4 2.6 1 / -3.2 8.2 -5.2 1.6 2 2.2 2 / -1 15 -5.2 2 1.2 2 1");
        a(BACK, "Sword Scabbard", UNCOMMON, "A sheathed blade slung across the back.", 0x4A3A2A, 0x8B8F99, 0xF5C542,
                "0,3,12,2,2 0,4,10,2,2 0,5,8,2,2 0,6,6,2,2 0,7,4,2,2 0,8,2,2,2 1,9,1,4,1 2,11,0,1,3",
                "-4.2 18.5 -2.5 8.4 1.2 .7 1 / -4 13 -3.4 2 2.6 1 0 / -2.8 15.2 -3.4 2 2.6 1 0 / -1.6 17.4 -3.4 2 2.6 1 0 / -.4 19.6 -3.4 2 2.6 1 0 / .8 21.8 -3.4 2 2.6 1 0 / 2 24 -3.4 2 2.6 1 0 / 2.4 26.4 -3.4 1.2 1.6 1 2 / 1.2 26 -3.5 3.6 .8 1.2 1");
        a(BACK, "Golden Shield", RARE, "A polished golden shield worn on the back.", 0xF5C542, 0xB8860B, 0xFFFFFF,
                "0,3,2,10,8 0,4,10,8,2 0,6,12,4,2 0,7,14,2,1 1,7,3,2,9 1,4,6,8,2 3,4,3,1,1 2,7,6,2,2",
                "-3.8 12.5 -3.8 7.6 9.5 1.2 0 / -3.9 12.5 -3.9 7.6 1 1.3 1 / -3.9 21 -3.9 7.6 1 1.3 1 / -3.9 12.5 -3.9 1 9.5 1.3 1 / 2.9 12.5 -3.9 1 9.5 1.3 1 / -1.2 15.8 -4.2 2.4 2.4 .5 1");
        a(BACK, "Floating Lantern", RARE, "A warm lantern drifting over your shoulder.", 0x3A3F4A, 0xB8860B, 0xFFC247,
                "1,6,1,4,2 0,4,3,8,2 0,4,11,8,2 0,4,3,1,10 0,11,3,1,10 2,5,5,6,6 3,7,6,2,3 1,7,13,2,2",
                "B|5 18 -5.5 3.4 4.4 3.4 0 / 5.6 22.4 -4.9 2.2 1 2.2 1 / 5.4 17.2 -5.1 2.6 .8 2.6 1 / 5.7 19 -5.3 2.6 2.4 2.6 2 / 6.2 23.4 -4.4 .8 1.6 .8 1");
        a(BACK, "Crystal Backpack", RARE, "A pack with crystals growing out of it.", 0x5B4A8A, 0x56D6FF, 0xE05CFF,
                "0,3,6,10,8 1,3,6,10,2 2,5,1,2,6 2,8,0,2,7 1,10,2,2,5 3,6,2,1,3 1,2,8,1,5 1,13,8,1,5",
                "-3.2 12.5 -5.6 6.4 9 3.6 0 / -2.6 21.5 -5.2 1.8 4.4 1.8 1 / .2 21.5 -4.6 1.8 5.6 1.8 2 / -1.2 21.5 -5.8 1.4 3.4 1.4 1");
        a(BACK, "Mechanical Backpack", EPIC, "Gears, pipes, and a steady mechanical hum.", 0x6B7280, 0xB8860B, 0x00F0FF,
                "0,3,4,10,10 1,4,5,4,4 4,5,6,2,2 1,9,9,3,3 2,10,10,1,1 3,3,2,3,2 1,2,6,1,7",
                "-3.8 12.5 -5.8 7.6 9 3.8 0 / -3 14 -6 2.6 2.6 .4 1 / .2 17 -6 2.6 2.6 .4 2 / -4.2 13 -4.4 .8 7 .8 3 / 3.4 13 -4.4 .8 7 .8 3 / -1 21.5 -5 2 1.2 2 1 / -.4 22.7 -4.4 .8 2 .8 3");
        a(BACK, "Dragon Tail", EPIC, "A long scaled tail with a bony spike.", 0x2E7D4F, 0x1B4A30, 0xE8E2CF,
                "0,2,10,3,3 0,4,8,3,3 0,6,6,3,3 0,8,4,3,3 0,10,3,3,3 0,12,4,2,3 1,13,2,2,2 3,13,0,2,2",
                "-1 10.5 -2.8 2 2 3 0 / -.9 10 -5.6 1.8 1.8 3 0 / -.8 9.5 -8.4 1.6 1.6 3 0 / -.7 10 -11.2 1.4 1.4 3 0 / -.6 11 -13.8 1.2 1.2 3 0 / -.5 11.5 -16.5 1 1.6 1.6 1");
        a(BACK, "Mini Ender Chest", UNCOMMON, "A tiny ender chest strapped to your back.", 0x1F3A3A, 0x0E2020, 0xB06CFF,
                "0,2,5,12,9 1,2,5,12,3 1,2,8,12,1 2,7,7,2,3 4,2,9,12,1",
                "-3.2 11.5 -6.2 6.4 5 4 0 / -3.2 16.5 -6.2 6.4 1.6 4 1 / -.6 15 -6.4 1.2 2 .3 2");

        // ============================================== 7. SHOULDER ACCESSORIES AND PETS
        a(SHOULDER, "Shoulder Parrot", COMMON, "A talkative parrot perched on your shoulder.", 0xE53935, 0x1E88E5, 0xFFC247,
                "0,5,6,5,6 0,6,2,4,4 2,10,3,2,2 3,8,3,1,1 1,5,7,2,4 1,4,12,2,3 1,6,12,2,4",
                "5.2 24 -1 2.2 3 2 0 / 5.4 27 -.8 1.8 1.8 1.8 0 / 7.2 27.4 -.2 .8 .8 .8 2 / 5.6 21.8 -1.4 1.4 2.6 1.2 1 / 4.8 24.6 -1.2 .5 2 1.4 1 / 7.4 24.6 -1.2 .5 2 1.4 1");
        a(SHOULDER, "Mini Creeper", UNCOMMON, "A tiny creeper. It promises not to explode.", 0x4CAF50, 0x2E7D32, 0x1B5E20,
                "0,4,1,8,7 0,5,8,6,5 1,4,13,3,2 1,9,13,3,2 4,5,3,2,2 4,9,3,2,2 4,7,5,2,2 4,6,6,1,2 4,9,6,1,2",
                "4.6 27.2 -1.3 3.2 3.2 3.2 0 / 5 24 -1 2.4 3.2 2 0 / 5 29 1.9 .7 .7 .2 4 / 6.5 29 1.9 .7 .7 .2 4 / 5.6 27.6 1.9 1 1.2 .2 4");
        a(SHOULDER, "Baby Enderman", RARE, "A tall, shy baby enderman. Don't stare.", 0x1A1426, 0x2E2442, 0xE05CFF,
                "0,6,6,4,6 0,5,1,6,5 0,6,12,1,3 0,9,12,1,3 2,5,3,2,1 2,9,3,2,1 1,4,7,1,5 1,11,7,1,5",
                "5.4 24 -.6 1.6 4 1.6 0 / 5.2 28 -.8 2 2 2 0 / 5.5 28.8 1.2 .6 .4 .2 2 / 6.4 28.8 1.2 .6 .4 .2 2 / 5 24.4 -.4 .4 3.4 .8 1 / 7 24.4 -.4 .4 3.4 .8 1");
        a(SHOULDER, "Tiny Dragon", LEGENDARY, "A hatchling dragon that flaps along beside you.", 0x3FA66B, 0x7FD8A0, 0xFFC247,
                "0,4,7,7,5 0,10,5,4,4 0,13,6,2,1 1,2,6,3,2 1,3,4,3,3 0,1,11,3,2 2,11,4,1,1 2,13,4,1,1 4,12,6,1,1 0,5,12,2,2 0,9,12,2,2",
                "B|4.8 24.5 -2.2 2.6 2 4.4 0 / 5 25 2 2.2 2 2 0 / 5.2 27 2.8 .5 .8 .5 2 / 6.4 27 2.8 .5 .8 .5 2 / 4 26 -1.4 .5 2.6 2.6 1 / 7.8 26 -1.4 .5 2.6 2.6 1 / 5.4 24.2 -4.4 1.4 1.2 2.4 0 / 5.3 26.4 3.9 .5 .5 .2 3");
        a(SHOULDER, "Floating Bee", RARE, "A buzzing bee that hovers near your ear.", 0xFFC800, 0x2A2A2A, 0xFFFFFF,
                "0,4,6,8,6 4,6,6,1,6 4,9,6,1,6 3,5,2,3,3 3,9,2,3,3 4,2,8,2,2 4,12,9,2,1",
                "B|6 28 -1 3 2.6 2.4 0 / 7 28 -1.1 .7 2.6 2.6 4 / 8.2 28 -1.1 .7 2.6 2.6 4 / 6.4 30.6 -.6 1.6 .4 1.2 3 / 8 30.6 -.6 1.6 .4 1.2 3 / 6.1 28.9 1.4 .5 .5 .2 4");
        a(SHOULDER, "Mini Slime", UNCOMMON, "A bouncy little slime cube.", 0x6BCB4B, 0x9BE87A, 0x2E7D32,
                "0,3,4,10,9 1,5,6,6,5 4,5,7,2,2 4,9,7,2,2 4,7,10,2,1 3,4,5,1,1",
                "4.8 24 -1.4 3.6 3.6 3.6 0 / 5.2 24.4 -1 2.8 2.8 2.8 1 / 5.4 26 2.2 .7 .7 .2 4 / 7 26 2.2 .7 .7 .2 4 / 6 25 2.2 .9 .4 .2 4");
        a(SHOULDER, "Shoulder Cat", COMMON, "A calm cat that rides on your shoulder.", 0x8A8F98, 0xFFB6C8, 0x4CAF50,
                "0,4,3,8,6 0,4,1,2,2 0,10,1,2,2 0,3,9,10,5 2,5,5,2,1 2,9,5,2,1 1,7,7,2,1 0,12,10,2,5",
                "4.8 24 -2 2.4 2 3.8 0 / 4.9 26 1.4 2.2 2 2 0 / 5 28 1.6 .7 .7 .7 0 / 6.4 28 1.6 .7 .7 .7 0 / 5.5 24.4 -4 .7 .7 2 1 / 5.5 25 -4.4 .7 2.2 .7 0");
        a(SHOULDER, "Shoulder Fox", RARE, "A cunning little fox with a white-tipped tail.", 0xE8772A, 0xFFFFFF, 0x2A2A2A,
                "0,4,1,2,3 0,10,1,2,3 0,3,3,10,6 3,5,7,6,3 4,7,8,2,1 0,3,9,10,5 3,6,10,4,3 0,12,8,3,5",
                "4.8 24 -2.2 2.6 2 4 0 / 4.9 26 1.4 2.4 2 2.2 0 / 5.1 28 1.6 .7 1 .7 0 / 6.5 28 1.6 .7 1 .7 0 / 5.6 26 3.2 .8 .8 1 4 / 5.4 24 -5.6 1.8 1.8 3.4 0 / 5.4 24 -7.6 1.8 1.8 1.4 1 / 5 24.6 1.2 1.6 1.2 .2 1");
        a(SHOULDER, "Baby Ghast", EPIC, "A floating baby ghast that cries glowing tears.", 0xF4F4F4, 0xD8D8D8, 0xFF8A8A,
                "0,2,2,12,12 1,2,12,12,2 4,4,6,3,1 4,9,6,3,1 4,6,10,4,1 1,3,14,2,2 1,6,14,2,2 1,9,14,2,2",
                "B|4.2 25 -1.8 4 4 4 0 / 4.4 23.4 -1.4 .8 1.6 .8 0 / 5.7 23.4 -1.4 .8 1.6 .8 0 / 6.9 23.4 -1.4 .8 1.6 .8 0 / 4.8 27.4 2.25 .8 1 .2 4 / 6.8 27.4 2.25 .8 1 .2 4 / 5.6 26 2.25 1.6 .6 .2 2");
        a(SHOULDER, "Mini Allay", EPIC, "A glowing little allay that follows you around.", 0x45C5F0, 0x9FE6FF, 0xFFFFFF,
                "0,5,3,6,6 1,3,5,2,5 1,11,5,2,5 2,6,9,4,4 4,6,5,1,2 4,9,5,1,2",
                "B|5 25 -.9 2.2 2.6 1.8 0 / 5.2 27.6 -.9 1.8 1.8 1.8 0 / 5.5 28.3 .9 .4 .4 .2 4 / 6.2 28.3 .9 .4 .4 .2 4 / 4 26 -.3 .6 2.6 1.6 1 / 7.2 26 -.3 .6 2.6 1.6 1 / 5.4 25.4 .9 1.2 1.2 .2 2");

        // ======================================================= 8. BODY ACCESSORIES
        a(BODY, "Royal Scarf", UNCOMMON, "A thick scarf in royal purple and gold.", 0x6A2FA8, 0xF5C542, 0xFFFFFF,
                "0,2,3,12,3 0,2,6,3,8 1,2,3,12,1 1,2,6,3,1 1,2,9,3,1 1,2,12,3,1",
                "-4.5 21.8 -2.5 9 2.2 5 0 / -4.5 22.6 -2.5 9 .6 5 1 / -4 14 2.5 2.2 8 .8 0 / -4 15.6 2.55 2.2 .6 .8 1 / -4 18 2.55 2.2 .6 .8 1");
        a(BODY, "Red Bandana Scarf", COMMON, "A red neckerchief with white polka dots.", 0xD32F2F, 0xFFFFFF, 0xFFFFFF,
                "0,2,3,12,3 0,4,6,8,2 0,6,8,4,2 0,7,10,2,2 3,4,4,1,1 3,7,4,1,1 3,10,4,1,1 3,6,7,1,1",
                "-4.4 22 -2.4 8.8 1.6 4.8 0 / -3.2 20.8 2.4 6.4 1.4 .5 0 / -1.6 19.6 2.4 3.2 1.4 .5 0 / -2.4 21.6 2.9 .7 .7 .2 3 / 1.5 21.6 2.9 .7 .7 .2 3");
        a(BODY, "Golden Necklace", UNCOMMON, "A fine golden chain with a small charm.", 0xF5C542, 0xFFF3A0, 0xFFFFFF,
                "0,3,2,1,4 0,4,6,1,2 0,5,8,1,2 0,6,9,4,1 0,10,8,1,1 0,11,6,1,2 0,12,2,1,4 1,7,10,2,3 3,7,10,1,1",
                NECKLACE);
        a(BODY, "Diamond Necklace", RARE, "A silver chain set with a brilliant diamond.", 0xBFEFFF, 0x4AEDD9, 0xFFFFFF,
                "1,3,2,1,4 1,4,6,1,2 1,5,8,1,2 1,6,9,4,1 1,10,8,1,1 1,11,6,1,2 1,12,2,1,4 0,6,10,4,1 0,7,11,2,2 3,7,10,1,1",
                NECKLACE);
        a(BODY, "Emerald Pendant", RARE, "A large emerald hanging on a thin cord.", 0x2FCB6E, 0x7A5A2A, 0xFFFFFF,
                "1,7,1,2,4 1,6,2,1,2 1,9,2,1,2 0,6,6,4,6 2,7,7,2,4 3,7,7,1,1",
                "-2.4 22.2 2.1 1 .4 .3 1 / 1.4 22.2 2.1 1 .4 .3 1 / -1.4 21.4 2.1 2.8 .4 .3 1 / -1.2 19.4 2.1 2.4 2.4 .5 0 / -.6 20 2.5 1.2 1.2 .3 3");
        a(BODY, "Royal Badge", UNCOMMON, "A badge of royal service pinned to your chest.", 0xB0172B, 0xF5C542, 0xFFFFFF,
                "0,4,2,8,9 1,5,3,6,7 0,7,5,2,3 2,5,11,2,4 2,9,11,2,4",
                "1.4 19 2.05 2.6 2.8 .5 1 / 1.8 19.4 2.4 1.8 1.8 .3 0 / 2.3 19.9 2.5 .8 .8 .2 2");
        a(BODY, "Cyber Chest Plate", EPIC, "A hard plate with glowing circuit lines.", 0x4A5160, 0xFF2BD6, 0x00F0FF,
                "0,2,2,12,12 1,3,3,3,10 1,10,3,3,10 2,7,4,2,2 2,7,8,2,2 3,6,12,4,1",
                "-4.4 12.2 -2.4 8.8 11.6 4.8 0 / -3 14 2.45 .8 8 .2 2 / 2.2 14 2.45 .8 8 .2 2 / -1 19.5 2.45 2 2 .2 2 / -2.4 17 2.45 4.8 .5 .2 1 / -4.5 22.6 -2.4 9 1.2 4.8 1");
        a(BODY, "Frost Armor Decoration", RARE, "Icy spikes and a crystal sit on your shoulders.", 0x9ADCF5, 0xE6FAFF, 0x5AA0D0,
                "0,1,6,3,4 0,2,3,2,4 0,12,6,3,4 0,12,3,2,4 1,5,8,6,5 3,7,5,2,2",
                "M|-7 24 -1 2.4 1.2 2.4 0 / -7.6 25 -.6 1.4 2.2 1.6 1 / -6.2 25 -2 1 1.6 1 1 / -.7 17.4 2.1 1.4 2.4 .5 1");
        a(BODY, "Nether Shoulder Armor", EPIC, "Spiked netherite pauldrons with ember glow.", 0x3B2A2A, 0x6A4A4A, 0xFF7A1A,
                "0,0,5,6,6 1,1,4,4,1 2,1,2,1,3 2,3,1,1,4 M",
                "M|-8.8 23.4 -2.6 5.2 1.6 5.2 0 / -8.4 25 -2.2 4.4 1 4.4 1 / -8.2 26 -1.6 1 2.2 1 2 / -6 26 -.6 1 2.6 1 2 / -7 26 -2 .8 1.6 .8 2");
        a(BODY, "Glowing Chest Emblem", LEGENDARY, "An emblem of pure light burning on your chest.", 0x2B2B3A, 0xFFD54A, 0x7FF7FF,
                "0,6,2,4,12 0,2,6,12,4 2,7,3,2,10 2,3,7,10,2 3,7,7,2,2",
                "-2.8 17.6 2.05 5.6 .9 .3 1 / -.45 15.2 2.05 .9 5.6 .3 1 / -1 17 2.1 2 2 .4 0 / -.5 17.5 2.4 1 1 .2 2");

        // ======================================================= 9. HAND ACCESSORIES
        a(HAND, "Diamond Sword Cosmetic", COMMON, "A cosmetic diamond blade. Looks sharp, cuts nothing.", 0x4AEDD9, 0x8B8F99, 0x6B4A2B,
                "0,12,1,2,2 0,10,3,2,2 0,8,5,2,2 0,6,7,2,2 1,4,8,4,1 1,5,7,1,3 2,3,10,2,2 2,1,12,2,2 1,0,14,2,2",
                SWORD);
        a(HAND, "Golden Sword Cosmetic", UNCOMMON, "A broad golden blade for show.", 0xF5C542, 0xB8860B, 0x6B4A2B,
                "0,11,1,3,3 0,9,3,3,3 0,7,5,3,3 0,5,7,3,3 1,3,8,5,1 1,4,7,1,3 2,3,10,2,2 2,1,12,2,2 1,0,14,2,2",
                "-6.9 14 2.6 1.8 7 .8 0 / -6.5 21 2.6 1 1.4 .8 0 / -8.4 13 2.4 4.8 1.2 1.2 1 / -6.5 11 2.6 1 2 .8 2 / -6.9 10.2 2.4 1.8 1 1.2 1");
        a(HAND, "Energy Blade", LEGENDARY, "A humming blade of pure energy.", 0x00E5FF, 0x2B2B3A, 0xFFFFFF,
                "0,12,0,3,3 3,12,1,1,2 0,10,2,3,3 3,10,3,1,2 0,8,4,3,3 3,8,5,1,2 0,6,6,3,3 3,6,7,1,2 1,3,9,4,2 4,2,11,2,2 1,0,13,3,2",
                "-6.3 13.6 2.7 .6 9 .6 3 / -6.7 13.4 2.5 1.4 9.4 1 0 / -6.4 11.6 2.6 .8 2 .8 4 / -7.2 13 2.4 2.4 .7 1.2 1");
        a(HAND, "Crystal Wand", RARE, "A slim wand topped with a glowing crystal.", 0xB48CFF, 0x6B4A2B, 0xFFFFFF,
                "0,10,0,4,4 3,11,1,1,1 1,9,4,2,2 1,7,6,2,2 1,5,8,2,2 1,3,10,2,2 1,1,12,2,2",
                "-6.3 12 2.8 .6 6 .6 1 / -7.2 17.8 2.4 2.4 3 1.2 0 / -6.6 18.6 2.9 1.2 1.4 .3 3");
        a(HAND, "Magic Staff", EPIC, "A tall staff crowned with a swirling orb.", 0x56D6FF, 0x6B4A2B, 0xFFFFFF,
                "0,5,0,6,6 3,6,1,2,2 1,7,6,2,10 1,6,16,4,0 2,6,3,1,1",
                "-6.3 7 2.7 .7 17 .7 1 / -7.4 23.6 2.2 3.1 3.1 3.1 0 / -6.7 24.4 2.9 1.5 1.5 1.5 2");
        a(HAND, "Royal Scepter", RARE, "A golden scepter set with a ruby.", 0xF5C542, 0xB8860B, 0xE53935,
                "0,6,5,4,10 1,5,4,6,2 0,5,1,6,4 2,7,2,2,2 1,4,13,8,1",
                "-6.3 9 2.7 .7 13 .7 0 / -7.2 21.8 2.3 2.5 2.5 1.5 0 / -6.5 22.4 2.9 1.1 1.1 .6 2 / -7 15.5 2.4 1.7 .8 1.4 1");
        a(HAND, "Pixel Hammer", UNCOMMON, "A chunky hammer built from big square pixels.", 0x8B8F99, 0x6B4A2B, 0xFF5A36,
                "0,3,2,10,5 1,3,2,10,1 2,3,2,10,1 1,7,7,2,8 1,6,14,4,1",
                "-6.3 8.5 2.7 .8 13 .8 1 / -8.5 21 2 5.2 3.2 2.4 0 / -8.7 21.2 1.9 .4 2.8 2.6 2");
        a(HAND, "Energy Shield", EPIC, "A shimmering shield of hard light.", 0x00E5FF, 0x0A7FA0, 0xFFFFFF,
                "0,4,2,8,1 0,2,4,12,8 0,4,12,8,1 1,4,4,8,8 2,5,5,6,6 3,7,7,2,2",
                "-9 11.8 -1.5 .8 8 5.5 0 / -9.3 12.4 -.9 .3 6.8 4.3 1 / -9.4 15 .3 .3 1.8 1.8 3");
        a(HAND, "Floating Orb", COMMON, "A glowing orb that hovers by your hand.", 0xB48CFF, 0xE6D4FF, 0xFFFFFF,
                "0,4,4,8,8 0,5,3,6,10 1,5,5,3,3 3,6,5,1,1 3,2,2,1,1 3,13,11,1,1",
                "B|-8.5 14 1.4 3 3 3 0 / -7.8 14.7 2 1.6 1.6 1.6 1 / -7.2 15.3 2.6 .6 .6 .6 2");
        a(HAND, "Neon Gauntlet", RARE, "A heavy gauntlet lined with neon light.", 0x3A3F4C, 0x6B7280, 0x00F0FF,
                "0,3,4,10,9 1,3,4,10,2 2,5,7,6,1 2,5,10,6,1 1,4,13,8,2 3,5,5,1,1",
                "-8.3 11.8 -2.3 4.6 5 4.6 0 / -8.4 14.8 -2.35 4.8 .6 4.7 2 / -8.4 12.8 -2.35 4.8 .5 4.7 2 / -8.6 16.4 -2.4 5 1 4.8 1");

        // =============================================================== 10. SHOES
        a(SHOES, "Diamond Boots", RARE, "Boots forged from solid diamond.", 0x4AEDD9, 0x2A9C94, 0xBFFFF8,
                "0,5,2,4,8 0,5,10,8,3 1,5,2,4,1 1,4,13,9,1 3,6,4,1,5 1,5,10,8,1",
                BOOTS);
        a(SHOES, "Golden Boots", UNCOMMON, "Shiny golden boots. Surprisingly light.", 0xF5C542, 0xB8860B, 0xFFF3A0,
                "0,5,2,4,8 0,5,10,8,3 1,5,2,4,1 1,4,13,9,1 3,6,4,1,5",
                BOOTS);
        a(SHOES, "Nether Boots", RARE, "Dark boots with spikes still warm from the Nether.", 0x4A1A1A, 0x8A2A1A, 0xFF7A1A,
                "0,5,2,4,8 0,5,10,8,3 1,5,2,4,1 1,4,13,9,1 2,4,3,1,1 2,10,4,1,1 2,5,10,1,1",
                BOOTS_SPIKE);
        a(SHOES, "Frost Boots", RARE, "Fur-lined boots that never feel the cold.", 0x8CC8F0, 0x5A9ACB, 0xFFFFFF,
                "0,5,3,4,7 0,5,10,8,3 1,4,13,9,1 3,4,1,6,3 3,3,2,1,1",
                BOOTS_FUR);
        a(SHOES, "Shadow Boots", UNCOMMON, "Boots that leave no footprints.", 0x1A1A24, 0x2E2E3E, 0x8C5CFF,
                "0,5,2,4,8 0,5,10,8,3 1,5,13,8,1 2,5,6,4,1",
                BOOTS);
        a(SHOES, "Emerald Boots", UNCOMMON, "Green boots set with an emerald.", 0x1E9E4F, 0x14693A, 0x6BFFA0,
                "0,5,2,4,8 0,5,10,8,3 1,5,2,4,1 1,4,13,9,1 2,6,5,1,1",
                BOOTS);
        a(SHOES, "Neon Sneakers", EPIC, "Low-top sneakers with glowing soles.", 0xFF2BD6, 0xFFFFFF, 0x00F0FF,
                "0,2,7,10,4 3,2,11,12,2 1,8,8,3,1 2,9,6,2,1 3,4,7,1,1 3,6,7,1,1 3,8,7,1,1",
                SNEAKER);
        a(SHOES, "Royal Boots", EPIC, "Tall royal boots trimmed in gold.", 0x6A2FA8, 0xF5C542, 0xE53935,
                "0,4,0,5,11 1,4,0,5,2 1,4,12,9,1 0,4,10,9,2 2,6,4,1,2",
                BOOTS_CUFF);
        a(SHOES, "Cloud Shoes", COMMON, "Puffy cloud shoes that bounce softly.", 0xFFFFFF, 0xCFE6FA, 0xFFFFFF,
                "0,2,8,5,5 0,6,6,6,5 0,9,9,5,4 3,3,9,2,2 1,2,12,12,1",
                CLOUD);
        a(SHOES, "Magma Boots", LEGENDARY, "Boots of molten rock. The ground glows beneath.", 0x3A1F1A, 0x7A2A1A, 0xFF8A1F,
                "0,4,2,5,8 0,4,9,9,4 2,5,5,1,3 2,7,10,1,2 2,10,10,1,2 1,4,13,9,1",
                BOOTS_MAGMA);

        ALL = Collections.unmodifiableList(new ArrayList<>(TMP));
        for (CosmeticSlot s : CosmeticSlot.values()) BY_SLOT.put(s, new ArrayList<>());
        for (Cosmetic c : ALL) {
            if (BY_ID.put(c.id, c) != null) throw new IllegalStateException("duplicate cosmetic id " + c.id);
            BY_SLOT.get(c.slot).add(c);
        }
        for (CosmeticSlot s : CosmeticSlot.values())
            if (BY_SLOT.get(s).size() != 10) throw new IllegalStateException(s + " must have exactly 10 cosmetics");
        if (ALL.size() != 100) throw new IllegalStateException("expected 100 cosmetics, got " + ALL.size());
    }
}
