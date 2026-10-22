package client.confis;

/**
 * Software 3D player preview. The GUI only offers axis-aligned fills, so the model is a set of boxes that is
 * rotated around the vertical axis and projected orthographically with a small downward camera tilt. Vertical
 * faces and top faces are emitted as 1-pixel columns (equal neighbours merged into one fill). Nothing is
 * allocated per frame: scratch arrays are preallocated and every model is baked once (see CosmeticArt).
 * It draws through a Sink, so it has no dependency on Minecraft.
 */
public final class CosmeticaPreview {
    private CosmeticaPreview() {}

    public interface Sink {
        void fill(int x0, int y0, int x1, int y1, int argb);
    }

    private static final float PITCH_SIN = 0.30f, PITCH_COS = 0.954f;
    private static final float LX = -0.49f, LZ = 0.87f;
    public static final float DEFAULT_YAW = 0.45f;

    /** The undressed player (skin, shirt, trousers). Literal colors, so the palette argument is unused. */
    private static final CosmeticArt.Model BODY = CosmeticArt.bakeModel(
            "-4 0 -2 4 12 4 #3C44AA / 0 0 -2 4 12 4 #3C44AA / -4 12 -2 8 12 4 #1FA9B2"
                    + " / -8 18 -2 4 6 4 #1FA9B2 / 4 18 -2 4 6 4 #1FA9B2 / -8 12 -2 4 6 4 #B98863 / 4 12 -2 4 6 4 #B98863"
                    + " / -4 24 -4 8 8 8 #B98863 / -4.1 30 -4.1 8.2 2.1 8.2 #3B2A1A / -4.1 24 -4.1 8.2 6.2 1.6 #3B2A1A"
                    + " / -3 27 4.01 2 1 .1 #FFFFFF / 1 27 4.01 2 1 .1 #FFFFFF / -2 27 4.02 1 1 .1 #3B3B8B / 1 27 4.02 1 1 .1 #3B3B8B"
                    + " / -1 25.2 4.01 2 .6 .1 #8A5A44", new int[3]);

    /** Overall height of the figure incl. a tall hat, used to fit the picture into its area. */
    public static final float UNITS_HIGH = 44f, UNITS_WIDE = 40f;

    // scratch buffers (max 600 boxes)
    private static final int MAX = 600;
    private static final float[] BX = new float[MAX * 6];
    private static final int[] BC = new int[MAX];
    private static final float[] KEY = new float[MAX];
    private static final int[] ORDER = new int[MAX];
    private static int n;
    private static float cs, sn;

    private static void add(float x0, float y0, float z0, float x1, float y1, float z1, int argb) {
        if (n >= MAX) return;
        int o = n * 6;
        BX[o] = x0; BX[o + 1] = y0; BX[o + 2] = z0; BX[o + 3] = x1; BX[o + 4] = y1; BX[o + 5] = z1;
        BC[n] = argb;
        KEY[n] = -((x0 + x1) * 0.5f) * sn + ((z0 + z1) * 0.5f) * cs;
        n++;
    }

    private static void addModel(CosmeticArt.Model m, float t, float phase) {
        float bob = m.bob ? (float) Math.sin(t * 2.2 + phase) * 0.7f : 0f;
        float ang = m.orbit ? t * 1.1f : 0f;
        float ca = (float) Math.cos(ang), sa = (float) Math.sin(ang);
        for (int i = 0; i < m.n; i++) {
            int o = i * 6;
            float x0 = m.box[o], y0 = m.box[o + 1] + bob, z0 = m.box[o + 2], x1 = m.box[o + 3], y1 = m.box[o + 4] + bob, z1 = m.box[o + 5];
            if (m.orbit) {          // move the box center around the vertical axis, keep the box axis-aligned
                float cx = (x0 + x1) * 0.5f, cz = (z0 + z1) * 0.5f, hx = (x1 - x0) * 0.5f, hz = (z1 - z0) * 0.5f;
                float nx = cx * ca + cz * sa, nz = -cx * sa + cz * ca;
                x0 = nx - hx; x1 = nx + hx; z0 = nz - hz; z1 = nz + hz;
            }
            add(x0, y0, z0, x1, y1, z1, m.argb[i]);
        }
    }

    /**
     * Draws the player wearing the given cosmetics.
     * worn[slot.ordinal()] may be null. (ox, oy) = screen position of the point between the feet, scale = pixels per unit.
     */
    public static void render(Sink k, Cosmetic[] worn, float yaw, int ox, int oy, float scale, float time) {
        cs = (float) Math.cos(yaw);
        sn = (float) Math.sin(yaw);
        n = 0;
        addModel(BODY, time, 0f);
        for (int i = 0; i < worn.length; i++) if (worn[i] != null) addModel(CosmeticArt.model(worn[i]), time, i * 1.3f);
        // painter's algorithm: far -> near (insertion sort on a few hundred entries, no allocation)
        for (int i = 0; i < n; i++) {
            int j = i - 1;
            float key = KEY[i];
            while (j >= 0 && KEY[ORDER[j]] > key) { ORDER[j + 1] = ORDER[j]; j--; }
            ORDER[j + 1] = i;
        }
        for (int i = 0; i < n; i++) {
            int b = ORDER[i], o = b * 6;
            drawBox(k, BX[o], BX[o + 1], BX[o + 2], BX[o + 3], BX[o + 4], BX[o + 5], BC[b], ox, oy, scale);
        }
    }

    private static int shadeColor(int argb, float f) {
        int r = Math.min(255, Math.round(((argb >> 16) & 255) * f));
        int g = Math.min(255, Math.round(((argb >> 8) & 255) * f));
        int b = Math.min(255, Math.round((argb & 255) * f));
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static float light(float nxr, float nzr) {
        float d = 0.5f + 0.5f * (nxr * LX + nzr * LZ);
        return 0.52f + 0.48f * d;
    }

    private static void drawBox(Sink k, float x0, float y0, float z0, float x1, float y1, float z1, int argb, int ox, int oy, float S) {
        // projected footprint corners A(x0,z0) B(x1,z0) C(x1,z1) D(x0,z1)
        float axr = x0 * cs + z0 * sn, azr = -x0 * sn + z0 * cs;
        float bxr = x1 * cs + z0 * sn, bzr = -x1 * sn + z0 * cs;
        float cxr = x1 * cs + z1 * sn, czr = -x1 * sn + z1 * cs;
        float dxr = x0 * cs + z1 * sn, dzr = -x0 * sn + z1 * cs;
        // vertical faces: back, right, front, left
        if (-cs > 0.001f) face(k, axr, azr, bxr, bzr, y0, y1, shadeColor(argb, light(-sn, -cs)), ox, oy, S);
        if (-sn > 0.001f) face(k, bxr, bzr, cxr, czr, y0, y1, shadeColor(argb, light(cs, -sn)), ox, oy, S);
        if (cs > 0.001f) face(k, dxr, dzr, cxr, czr, y0, y1, shadeColor(argb, light(sn, cs)), ox, oy, S);
        if (sn > 0.001f) face(k, axr, azr, dxr, dzr, y0, y1, shadeColor(argb, light(-cs, sn)), ox, oy, S);
        top(k, axr, azr, bxr, bzr, cxr, czr, dxr, dzr, y1, shadeColor(argb, 1.12f), ox, oy, S);
    }

    private static int ry(float v) { return Math.round(v); }

    private static void face(Sink k, float xa, float za, float xb, float zb, float y0, float y1, int col, int ox, int oy, float S) {
        float sa = ox + xa * S, sb = ox + xb * S;
        float lo = Math.min(sa, sb), hi = Math.max(sa, sb);
        if (hi - lo < 0.4f) return;
        int i0 = Math.round(lo), i1 = Math.max(i0 + 1, Math.round(hi));
        float zA = sa <= sb ? za : zb, zB = sa <= sb ? zb : za;
        float yTopBase = oy - y1 * PITCH_COS * S, yBotBase = oy - y0 * PITCH_COS * S, zk = PITCH_SIN * S;
        int runX = i0, runT = 0, runB = 0;
        boolean open = false;
        for (int i = i0; i < i1; i++) {
            float t = hi - lo < 0.001f ? 0f : Math.max(0f, Math.min(1f, (i + 0.5f - lo) / (hi - lo)));
            float z = zA + t * (zB - zA);
            int top = ry(yTopBase + z * zk), bot = ry(yBotBase + z * zk);
            if (bot <= top) bot = top + 1;
            if (open && (top != runT || bot != runB)) {
                k.fill(runX, runT, i, runB, col);
                open = false;
            }
            if (!open) { runX = i; runT = top; runB = bot; open = true; }
        }
        if (open) k.fill(runX, runT, i1, runB, col);
    }

    private static void top(Sink k, float axr, float azr, float bxr, float bzr, float cxr, float czr, float dxr, float dzr,
                            float y, int col, int ox, int oy, float S) {
        float yb = oy - y * PITCH_COS * S, zk = PITCH_SIN * S;
        float px0 = ox + axr * S, py0 = yb + azr * zk, px1 = ox + bxr * S, py1 = yb + bzr * zk;
        float px2 = ox + cxr * S, py2 = yb + czr * zk, px3 = ox + dxr * S, py3 = yb + dzr * zk;
        float lo = Math.min(Math.min(px0, px1), Math.min(px2, px3)), hi = Math.max(Math.max(px0, px1), Math.max(px2, px3));
        int i0 = Math.round(lo), i1 = Math.max(i0 + 1, Math.round(hi));
        int runX = i0, runT = 0, runB = 0;
        boolean open = false;
        for (int i = i0; i < i1; i++) {
            float xc = i + 0.5f;
            float mn = Float.MAX_VALUE, mx = -Float.MAX_VALUE;
            for (int e = 0; e < 4; e++) {
                float ex0, ey0, ex1, ey1;
                switch (e) {
                    case 0 -> { ex0 = px0; ey0 = py0; ex1 = px1; ey1 = py1; }
                    case 1 -> { ex0 = px1; ey0 = py1; ex1 = px2; ey1 = py2; }
                    case 2 -> { ex0 = px2; ey0 = py2; ex1 = px3; ey1 = py3; }
                    default -> { ex0 = px3; ey0 = py3; ex1 = px0; ey1 = py0; }
                }
                float elo = Math.min(ex0, ex1), ehi = Math.max(ex0, ex1);
                if (xc < elo || xc > ehi || ehi - elo < 0.0001f) continue;
                float yy = ey0 + (xc - ex0) / (ex1 - ex0) * (ey1 - ey0);
                if (yy < mn) mn = yy;
                if (yy > mx) mx = yy;
            }
            int top = 0, bot = 0;
            boolean has = mx - mn > 0.35f;
            if (has) { top = ry(mn); bot = Math.max(top + 1, ry(mx)); }
            if (open && (!has || top != runT || bot != runB)) {
                k.fill(runX, runT, i, runB, col);
                open = false;
            }
            if (has && !open) { runX = i; runT = top; runB = bot; open = true; }
        }
        if (open) k.fill(runX, runT, i1, runB, col);
    }
}
