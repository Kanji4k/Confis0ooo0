package client.confis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Turns the text specs of a Cosmetic into cached render data. Nothing here touches Minecraft, and nothing is
 * rebuilt per frame: every icon / model is baked once on first use.
 *
 * ICON SPEC (16x16): whitespace separated "c,x,y,w,h" rectangles painted in order. c = palette index 0-2,
 * 3 = white, 4 = near black, or a literal "#RRGGBB". The token "M" mirrors everything painted so far onto the
 * right half. After painting, a light top edge, a dark bottom edge and a 1px dark outline are added automatically.
 *
 * MODEL SPEC: optional flags and "|" (M = mirror left/right, B = bob up and down, O = orbit around the head),
 * then boxes separated by "/": "x y z w h d c" in player units (1 unit = 1/16 block). Origin: between the feet,
 * x to the right, y up, +z toward the front. Head = x -4..4, y 24..32, z -4..4. Torso y 12..24, legs y 0..12.
 */
public final class CosmeticArt {
    private CosmeticArt() {}

    public static final int SIZE = 16;
    private static final int OUTLINE = 0xFF14141E;

    // ------------------------------------------------------------------ icons
    private static final Map<String, int[]> ICONS = new HashMap<>();

    /** Merged rectangles of the icon: groups of 5 ints {x, y, w, h, argb}. */
    public static int[] icon(Cosmetic c) {
        int[] r = ICONS.get(c.id);
        if (r == null) {
            r = bakeIcon(c);
            ICONS.put(c.id, r);
        }
        return r;
    }

    static int color(String tok, int[] pal) {
        if (tok.startsWith("#")) return 0xFF000000 | Integer.parseInt(tok.substring(1), 16);
        int i = Integer.parseInt(tok);
        if (i == 3) return 0xFFFFFFFF;
        if (i == 4) return 0xFF15151F;
        return 0xFF000000 | pal[i];
    }

    private static int shade(int argb, float f) {
        int r = Math.min(255, Math.max(0, Math.round(((argb >> 16) & 255) * f)));
        int g = Math.min(255, Math.max(0, Math.round(((argb >> 8) & 255) * f)));
        int b = Math.min(255, Math.max(0, Math.round((argb & 255) * f)));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int[] bakeIcon(Cosmetic c) {
        int[] px = new int[SIZE * SIZE];
        for (String tok : c.iconSpec.trim().split("\\s+")) {
            if (tok.equals("M")) {
                for (int y = 0; y < SIZE; y++)
                    for (int x = 0; x < SIZE / 2; x++)
                        if (px[y * SIZE + x] != 0 && px[y * SIZE + (SIZE - 1 - x)] == 0) px[y * SIZE + (SIZE - 1 - x)] = px[y * SIZE + x];
                continue;
            }
            String[] p = tok.split(",");
            int col = color(p[0], c.palette);
            int x0 = Integer.parseInt(p[1]), y0 = Integer.parseInt(p[2]), w = Integer.parseInt(p[3]), h = Integer.parseInt(p[4]);
            for (int y = Math.max(0, y0); y < Math.min(SIZE, y0 + h); y++)
                for (int x = Math.max(0, x0); x < Math.min(SIZE, x0 + w); x++) px[y * SIZE + x] = col;
        }
        // bevel: lighter where the pixel above is empty, darker where the pixel below is empty
        int[] out = px.clone();
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++) {
                int v = px[y * SIZE + x];
                if (v == 0) continue;
                boolean topEdge = y == 0 || px[(y - 1) * SIZE + x] == 0;
                boolean botEdge = y == SIZE - 1 || px[(y + 1) * SIZE + x] == 0;
                if (topEdge && !botEdge) out[y * SIZE + x] = shade(v, 1.22f);
                else if (botEdge && !topEdge) out[y * SIZE + x] = shade(v, 0.74f);
            }
        // outline
        int[] fin = out.clone();
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++) {
                if (px[y * SIZE + x] != 0) continue;
                boolean near = (x > 0 && px[y * SIZE + x - 1] != 0) || (x < SIZE - 1 && px[y * SIZE + x + 1] != 0)
                        || (y > 0 && px[(y - 1) * SIZE + x] != 0) || (y < SIZE - 1 && px[(y + 1) * SIZE + x] != 0);
                if (near) fin[y * SIZE + x] = OUTLINE;
            }
        return merge(fin);
    }

    /** Greedy rectangle merge: same-colored runs, then stacked rows with an identical run. */
    private static int[] merge(int[] px) {
        boolean[] used = new boolean[SIZE * SIZE];
        List<int[]> rects = new ArrayList<>();
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++) {
                int v = px[y * SIZE + x];
                if (v == 0 || used[y * SIZE + x]) continue;
                int w = 1;
                while (x + w < SIZE && px[y * SIZE + x + w] == v && !used[y * SIZE + x + w]) w++;
                int h = 1;
                outer:
                while (y + h < SIZE) {
                    for (int i = 0; i < w; i++) if (px[(y + h) * SIZE + x + i] != v || used[(y + h) * SIZE + x + i]) break outer;
                    h++;
                }
                for (int j = 0; j < h; j++) for (int i = 0; i < w; i++) used[(y + j) * SIZE + x + i] = true;
                rects.add(new int[]{x, y, w, h, v});
            }
        int[] r = new int[rects.size() * 5];
        for (int i = 0; i < rects.size(); i++) System.arraycopy(rects.get(i), 0, r, i * 5, 5);
        return r;
    }

    // ----------------------------------------------------------------- models
    /** Baked 3D model: n boxes, box[i*6..] = x0,y0,z0,x1,y1,z1 ; argb[i]. */
    public static final class Model {
        public final float[] box;
        public final int[] argb;
        public final boolean bob, orbit;
        public final int n;

        Model(float[] box, int[] argb, boolean bob, boolean orbit) {
            this.box = box;
            this.argb = argb;
            this.n = argb.length;
            this.bob = bob;
            this.orbit = orbit;
        }
    }

    private static final Map<String, Model> MODELS = new HashMap<>();

    public static Model model(Cosmetic c) {
        Model m = MODELS.get(c.id);
        if (m == null) {
            m = bakeModel(c.modelSpec, c.palette);
            MODELS.put(c.id, m);
        }
        return m;
    }

    /** Parses any model spec (also used for the base player body). */
    public static Model bakeModel(String spec, int[] pal) {
        boolean mirror = false, bob = false, orbit = false;
        int bar = spec.indexOf('|');
        if (bar >= 0) {
            String f = spec.substring(0, bar);
            mirror = f.indexOf('M') >= 0;
            bob = f.indexOf('B') >= 0;
            orbit = f.indexOf('O') >= 0;
            spec = spec.substring(bar + 1);
        }
        List<float[]> boxes = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();
        for (String part : spec.split("/")) {
            String s = part.trim();
            if (s.isEmpty()) continue;
            String[] t = s.split("\\s+");
            float x = Float.parseFloat(t[0]), y = Float.parseFloat(t[1]), z = Float.parseFloat(t[2]);
            float w = Float.parseFloat(t[3]), h = Float.parseFloat(t[4]), d = Float.parseFloat(t[5]);
            int col = color(t[6], pal);
            boxes.add(new float[]{x, y, z, x + w, y + h, z + d});
            cols.add(col);
            if (mirror) {
                boxes.add(new float[]{-(x + w), y, z, -x, y + h, z + d});
                cols.add(col);
            }
        }
        float[] box = new float[boxes.size() * 6];
        int[] argb = new int[boxes.size()];
        for (int i = 0; i < boxes.size(); i++) {
            System.arraycopy(boxes.get(i), 0, box, i * 6, 6);
            argb[i] = cols.get(i);
        }
        return new Model(box, argb, bob, orbit);
    }
}
