package client.confis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * The Cosmetica page inside the Right Shift menu. ConfigScreen owns the window and the polled mouse state and
 * calls render() once per frame with the content rectangle; this class draws sidebar, grid, 3D preview, equipment
 * slots, info panel and controls, and changes CosmeticaState. Only flat fills are used (no blur, no shaders,
 * no framebuffer work); only the visible grid rows are drawn; icons / models / filtered lists are cached.
 */
public final class CosmeticaView {
    // ---- input, set by ConfigScreen before render() and read back afterwards (clicked is consumed here)
    public int mx, my;
    public boolean down, clicked;

    private static final int BTN = 0xFF1E1E1E, ACTIVE = 0xFFFFFFFF;
    private static final int GAP = 6;
    private static final String[] ABBR = {"ALL", "HAT", "HED", "FCE", "CAP", "WNG", "BCK", "SHL", "BDY", "HND", "SHO"};

    // view state (not saved, except what lives in CosmeticaState)
    private CosmeticSlot slotFilter;               // null = all categories
    private CosmeticRarity rarityFilter;           // null = all rarities
    private boolean favOnly, collection, spin;
    private CosmeticaState.Sort sort = CosmeticaState.Sort.NAME;
    private float yaw = CosmeticaPreview.DEFAULT_YAW;
    private boolean dragging;
    private int lastMx;
    private int scrollPx, maxScroll;
    private Cosmetic lastClickCard;
    private long lastClickAt;
    private String toast = "";
    private long toastUntil;

    // caches
    private final List<Cosmetic> list = new ArrayList<>();
    private int keyVersion = -1, keySlot = -2, keyRarity = -2, keySort = -1;
    private boolean keyFav, keyColl;
    private String keyText = null;
    private final HashMap<String, String[]> wrapCache = new HashMap<>();
    private int wrapW = -1;
    private final Cosmetic[] worn = new Cosmetic[CosmeticSlot.values().length];

    // draw target for the preview sink (a field, so no lambda is created per frame)
    private DrawContext cur;
    private final CosmeticaPreview.Sink sink = (x0, y0, x1, y1, c) -> cur.method_25294(x0, y0, x1, y1, c);

    // ------------------------------------------------------------------ helpers
    private boolean hit(int x, int y, int w, int h) { return mx >= x && mx < x + w && my >= y && my < y + h; }

    private boolean click(int x, int y, int w, int h) {
        if (clicked && hit(x, y, w, h)) {
            clicked = false;
            return true;
        }
        return false;
    }

    private static void txt(DrawContext g, TextRenderer f, String s, int x, int y, int col) {
        g.method_51433(f, s, x, y, col, !Theme.darkText);
    }

    private static void ctxt(DrawContext g, TextRenderer f, String s, int cx, int y, int col) {
        g.method_51433(f, s, cx - f.method_1727(s) / 2, y, col, !Theme.darkText);
    }

    private static String fit(TextRenderer f, String s, int avail) {
        if (f.method_1727(s) <= avail) return s;
        while (s.length() > 1 && f.method_1727(s + "..") > avail) s = s.substring(0, s.length() - 1);
        return s + "..";
    }

    private static void outline(DrawContext g, int x, int y, int w, int h, int col) {
        g.method_25294(x, y, x + w, y + 1, col);
        g.method_25294(x, y + h - 1, x + w, y + h, col);
        g.method_25294(x, y, x + 1, y + h, col);
        g.method_25294(x + w - 1, y, x + w, y + h, col);
    }

    private static int withAlpha(int rgb, int a) { return (a << 24) | (rgb & 0xFFFFFF); }

    private void say(String s) {
        toast = s;
        toastUntil = System.currentTimeMillis() + 1600;
    }

    /** Pill button. enabled = false draws it dimmed and ignores clicks. Returns true when clicked. */
    private boolean button(DrawContext g, TextRenderer f, int x, int y, int w, int h, String label, int fill, boolean enabled) {
        boolean hov = enabled && hit(x, y, w, h);
        int tc = Glass.button(g, x, y, w, h, enabled ? fill : BTN, hov);
        if (!enabled) tc = Theme.dim;
        ctxt(g, f, fit(f, label, w - 4), x + w / 2, y + (h - 8) / 2, tc);
        return enabled && click(x, y, w, h);
    }

    private void icon(DrawContext g, Cosmetic c, int x, int y, int scale) {
        int[] r = CosmeticArt.icon(c);
        for (int i = 0; i < r.length; i += 5)
            g.method_25294(x + r[i] * scale, y + r[i + 1] * scale, x + (r[i] + r[i + 2]) * scale, y + (r[i + 1] + r[i + 3]) * scale, r[i + 4]);
    }

    private void padlock(DrawContext g, int cx, int cy) {
        g.method_25294(cx - 3, cy - 1, cx + 4, cy + 4, 0xFFE8E8F0);       // body
        g.method_25294(cx - 2, cy - 4, cx - 1, cy - 1, 0xFFE8E8F0);       // shackle
        g.method_25294(cx + 2, cy - 4, cx + 3, cy - 1, 0xFFE8E8F0);
        g.method_25294(cx - 2, cy - 5, cx + 3, cy - 4, 0xFFE8E8F0);
        g.method_25294(cx, cy + 1, cx + 1, cy + 3, 0xFF30303C);
    }

    private String[] wrap(TextRenderer f, Cosmetic c, int width) {
        if (width != wrapW) {
            wrapCache.clear();
            wrapW = width;
        }
        String[] r = wrapCache.get(c.id);
        if (r == null) {
            List<String> lines = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            for (String word : c.description.split(" ")) {
                String t = cur.length() == 0 ? word : cur + " " + word;
                if (f.method_1727(t) > width && cur.length() > 0) {
                    lines.add(cur.toString());
                    cur = new StringBuilder(word);
                } else {
                    cur = new StringBuilder(t);
                }
            }
            if (cur.length() > 0) lines.add(cur.toString());
            r = lines.toArray(new String[0]);
            wrapCache.put(c.id, r);
        }
        return r;
    }

    public void scroll(double vertical) {
        scrollPx = (int) Math.max(0, Math.min(maxScroll, scrollPx - vertical * 28));
    }

    private Cosmetic selected() {
        Cosmetic c = CosmeticRegistry.byId(Config.get().cosmetica.selected);
        return c;
    }

    private void commit() { Config.save(); }

    // ------------------------------------------------------------------- render
    public void render(DrawContext g, TextRenderer f, int x, int y, int w, int h, String query) {
        CosmeticaState st = Config.get().cosmetica;
        cur = g;
        if (!down) dragging = false;

        // ---- layout
        int sideW = w >= 640 ? 88 : 26;
        int rightW = Math.max(180, Math.min(290, w * 34 / 100));
        int midW = w - sideW - rightW - 2 * GAP;
        if (midW < 150) {
            rightW = Math.max(150, rightW - (150 - midW));
            midW = w - sideW - rightW - 2 * GAP;
        }
        int sx = x, mxx = sx + sideW + GAP, rx = mxx + midW + GAP;

        // ---- filtered list (rebuilt only when something changed)
        String q = query == null ? "" : query;
        int ks = slotFilter == null ? -1 : slotFilter.ordinal(), kr = rarityFilter == null ? -1 : rarityFilter.ordinal();
        if (st.version() != keyVersion || ks != keySlot || kr != keyRarity || favOnly != keyFav || collection != keyColl
                || sort.ordinal() != keySort || !q.equals(keyText)) {
            st.query(list, slotFilter, rarityFilter, favOnly, collection, q, sort);
            keyVersion = st.version();
            keySlot = ks;
            keyRarity = kr;
            keyFav = favOnly;
            keyColl = collection;
            keySort = sort.ordinal();
            keyText = q;
            scrollPx = Math.min(scrollPx, maxScroll);
        }
        Cosmetic sel = selected();
        if (sel == null && !list.isEmpty()) {          // nothing selected yet: start on the first item
            st.select(list.get(0));
            sel = list.get(0);
        }

        drawSidebar(g, f, st, sx, y, sideW, h);
        drawCenter(g, f, st, mxx, y, midW, h);
        drawRight(g, f, st, rx, y, rightW, h, sel);

        if (System.currentTimeMillis() < toastUntil)
            ctxt(g, f, toast, mxx + midW / 2, y + h - 30, 0xFFFFFFFF);
    }

    // ------------------------------------------------------------------ sidebar
    private void drawSidebar(DrawContext g, TextRenderer f, CosmeticaState st, int x, int y, int w, int h) {
        CosmeticSlot[] slots = CosmeticSlot.values();
        int n = slots.length + 1;
        int reserve = 34;
        int bh = Math.max(13, Math.min(22, (h - reserve - n * 2) / n));
        boolean wide = w >= 60;
        for (int i = 0; i < n; i++) {
            CosmeticSlot s = i == 0 ? null : slots[i - 1];
            int by = y + i * (bh + 2);
            boolean active = s == slotFilter;
            boolean hov = hit(x, by, w, bh);
            int tc = Glass.button(g, x, by, w, bh, active ? ACTIVE : BTN, hov);
            Cosmetic rep = s == null ? CosmeticRegistry.ALL.get(0) : CosmeticRegistry.inSlot(s).get(0);
            if (bh >= 16) icon(g, rep, x + 3, by + (bh - 16) / 2, 1);
            else ctxt(g, f, ABBR[i], x + w / 2, by + (bh - 8) / 2, tc);     // short window: no room for the 16px icon
            if (wide) {
                String label = s == null ? "All" : s.category;
                txt(g, f, fit(f, label, w - 24), x + 22, by + (bh - 8) / 2, tc);
            }
            if (click(x, by, w, bh)) {
                slotFilter = s;
                scrollPx = 0;
            }
        }
        // unlocked counter + progress bar
        int un = st.unlockedCount(), total = CosmeticRegistry.ALL.size();
        int cy = y + h - 28;
        if (wide) txt(g, f, "Unlocked " + un + "/" + total, x + 2, cy, Theme.text);
        else txt(g, f, un + "", x + 2, cy, Theme.text);
        int bw = w;
        Glass.fillRound(g, x, cy + 12, bw, 8, 4, Glass.rimArgb(0x30));
        int fw = Math.max(0, Math.round(bw * (un / (float) total)));
        if (fw > 0) Glass.fillRound(g, x, cy + 12, Math.max(fw, 4), 8, 4, 0xFF5AD2FF);
        if (hit(x, cy + 12, bw, 8)) txt(g, f, Math.round(100f * un / total) + "%", x + bw / 2 - 8, cy + 22, Theme.dim);
    }

    // ------------------------------------------------------------------- center
    private void drawCenter(DrawContext g, TextRenderer f, CosmeticaState st, int x, int y, int w, int h) {
        // row 1: rarity chips, sort, favorites-only
        int cx = x;
        int chipW = 18;
        for (int i = -1; i < CosmeticRarity.values().length; i++) {
            CosmeticRarity r = i < 0 ? null : CosmeticRarity.values()[i];
            boolean active = r == rarityFilter;
            int cw = r == null ? 24 : chipW;
            boolean hov = hit(cx, y, cw, 14);
            int tc = Glass.button(g, cx, y, cw, 14, active ? ACTIVE : BTN, hov);
            if (r == null) ctxt(g, f, "ALL", cx + cw / 2, y + 3, tc);
            else {
                g.method_25294(cx + 3, y + 11, cx + cw - 3, y + 12, r.argb());
                ctxt(g, f, r.letter, cx + cw / 2, y + 2, active ? tc : r.argb());
            }
            if (click(cx, y, cw, 14)) {
                rarityFilter = r;
                scrollPx = 0;
            }
            cx += cw + 2;
        }
        int favW = 18;
        int sortW = Math.max(40, Math.min(84, x + w - cx - favW - 4));
        if (button(g, f, cx + 2, y, sortW, 14, "Sort: " + sort.label, BTN, true)) sort = sort.next();
        int fx = cx + 2 + sortW + 2;
        boolean fh = hit(fx, y, favW, 14);
        Glass.button(g, fx, y, favW, 14, favOnly ? ACTIVE : BTN, fh);
        Icons.draw(g, "heart", fx + 5, y + 4, 1, favOnly ? 0xFFFF4D6D : ((Theme.text & 0xFFFFFF) | 0x90000000));
        if (click(fx, y, favW, 14)) {
            favOnly = !favOnly;
            scrollPx = 0;
        }

        // bottom bar: Wardrobe / Collection tabs, presets, Save Wardrobe
        int barH = 16, by = y + h - barH;
        int tw = Math.min(70, (w - 120) / 3);
        tw = Math.max(48, tw);
        if (button(g, f, x, by, tw, barH, "Wardrobe", !collection ? ACTIVE : BTN, true)) { collection = false; scrollPx = 0; }
        if (button(g, f, x + tw + 2, by, tw, barH, "Collection", collection ? ACTIVE : BTN, true)) { collection = true; scrollPx = 0; }
        int saveW = Math.min(88, Math.max(60, w - 2 * tw - 4 - 3 * 20 - 10));
        int saveX = x + w - saveW;
        int px0 = saveX - 3 * 20 - 4;
        if (px0 > x + 2 * tw + 8) {
            for (int p = 1; p <= CosmeticaState.PRESETS; p++) {
                int bx = px0 + (p - 1) * 20;
                boolean act = st.activePreset == p;
                boolean hov = hit(bx, by, 18, barH);
                int tc = Glass.button(g, bx, by, 18, barH, act ? ACTIVE : BTN, hov);
                ctxt(g, f, String.valueOf(p), bx + 9, by + 4, tc);
                if (st.hasPreset(p)) g.method_25294(bx + 14, by + 2, bx + 16, by + 4, 0xFF4ADE80);
                if (click(bx, by, 18, barH)) {
                    if (st.loadPreset(p)) say("Loaded wardrobe " + p);
                    else say("Wardrobe " + p + " is empty");
                    commit();
                }
            }
        }
        if (button(g, f, saveX, by, saveW, barH, "Save Wardrobe", BTN, true)) {
            st.savePreset(st.activePreset);
            say("Saved to wardrobe " + st.activePreset);
            commit();
        }

        // grid
        int gy = y + 18, gh = by - 4 - gy;
        drawGrid(g, f, st, x, gy, w, gh);
    }

    private void drawGrid(DrawContext g, TextRenderer f, CosmeticaState st, int x, int y, int w, int h) {
        if (list.isEmpty()) {
            txt(g, f, favOnly ? "No favorites here yet." : collection ? "No cosmetics match." : "Nothing unlocked matches. Try Collection.", x + 4, y + 6, Theme.dim);
            return;
        }
        int gap = 4, minW = 54, ch = 58;
        int cols = Math.max(1, (w - 6 + gap) / (minW + gap));
        int cw = (w - 6 - (cols - 1) * gap) / cols;
        int rows = (list.size() + cols - 1) / cols;
        int content = rows * (ch + gap);
        maxScroll = Math.max(0, content - h);
        scrollPx = Math.max(0, Math.min(maxScroll, scrollPx));
        Cosmetic selC = selected();
        boolean inGrid = hit(x, y, w, h);
        g.method_44379(x, y, x + w, y + h);
        int firstRow = Math.max(0, scrollPx / (ch + gap));
        for (int n = firstRow * cols; n < list.size(); n++) {
            int cy = y + (n / cols) * (ch + gap) - scrollPx;
            if (cy > y + h) break;
            if (cy + ch < y) continue;
            Cosmetic c = list.get(n);
            int cx = x + (n % cols) * (cw + gap);
            boolean eq = st.isEquipped(c), unl = st.isUnlocked(c), isSel = c == selC;
            boolean hov = inGrid && hit(cx, cy, cw, ch);
            Glass.card(g, cx, cy, cw, ch, hov || isSel, eq);
            outline(g, cx, cy, cw, ch, withAlpha(c.rarity.rgb, isSel ? 0xFF : hov ? 0xC0 : 0x80));
            icon(g, c, cx + (cw - 32) / 2, cy + 6, 2);
            if (!unl) {
                g.method_25294(cx + 2, cy + 2, cx + cw - 2, cy + ch - 2, 0x99080810);
                padlock(g, cx + cw / 2, cy + 20);
            }
            ctxt(g, f, fit(f, c.name, cw - 4), cx + cw / 2, cy + 41, unl ? Theme.text : Theme.dim);
            g.method_25294(cx + 5, cy + ch - 5, cx + cw - 5, cy + ch - 3, withAlpha(c.rarity.rgb, unl ? 0xFF : 0x70));
            if (eq) g.method_25294(cx + 4, cy + 4, cx + 9, cy + 9, 0xFF4ADE80);
            // favorite heart
            int hx = cx + cw - 11, hy = cy + 4;
            boolean fav = st.isFavorite(c);
            Icons.draw(g, "heart", hx, hy, 1, fav ? 0xFFFF4D6D : ((Theme.text & 0xFFFFFF) | 0x60000000));
            if (inGrid && click(hx - 2, hy - 2, 11, 10)) {
                st.toggleFavorite(c);
                commit();
            } else if (inGrid && click(cx, cy, cw, ch)) {
                long now = System.currentTimeMillis();
                if (c == lastClickCard && now - lastClickAt < 350) {      // double click = equip / unequip
                    if (eq) st.unequip(c.slot);
                    else if (!st.equip(c)) say("Locked: unlock it first");
                    commit();
                }
                lastClickCard = c;
                lastClickAt = now;
                st.select(c);
            }
        }
        g.method_44380();
        if (maxScroll > 0) {
            int kh = Math.max(14, h * h / content);
            int ky = y + (h - kh) * scrollPx / maxScroll;
            Glass.fillRound(g, x + w - 3, y, 3, h, 2, Glass.rimArgb(0x30));
            Glass.fillRound(g, x + w - 3, ky, 3, kh, 2, Glass.rimArgb(0xA0));
        }
    }

    // -------------------------------------------------------------------- right
    private void drawRight(DrawContext g, TextRenderer f, CosmeticaState st, int x, int y, int w, int h, Cosmetic sel) {
        int btnsH = 32;
        int pH = Math.max(100, Math.min(h * 56 / 100, h - btnsH - 64 - 2 * GAP));
        // ---- preview panel
        Glass.card(g, x, y, w, pH, false, false);
        int ix = x + 2, iy = y + 2, iw = w - 4, ih = pH - 4;
        for (int b = 0; b < 8; b++) {                                   // static vertical gradient, 8 flat bands
            int y0 = iy + ih * b / 8, y1 = iy + ih * (b + 1) / 8;
            int r = 14 + b * 2, gg = 22 + b * 4, bl = 48 + b * 7;
            g.method_25294(ix, y0, ix + iw, y1, (0xC0 << 24) | (r << 16) | (gg << 8) | bl);
        }
        int slotSz = Math.max(18, Math.min(28, (ih - 8) / 5 - 3));
        int slotGap = (ih - 8 - 5 * slotSz) / 4;
        CosmeticSlot[] left = {CosmeticSlot.HAT, CosmeticSlot.HEAD, CosmeticSlot.FACE, CosmeticSlot.BODY, CosmeticSlot.CAPE};
        CosmeticSlot[] right = {CosmeticSlot.WINGS, CosmeticSlot.BACK, CosmeticSlot.SHOULDER, CosmeticSlot.HAND, CosmeticSlot.SHOES};
        String hoverLine = null;
        for (int i = 0; i < 5; i++) {
            int sy = iy + 4 + i * (slotSz + Math.max(1, slotGap));
            String a = slotCell(g, f, st, left[i], ix + 4, sy, slotSz);
            String b = slotCell(g, f, st, right[i], ix + iw - 4 - slotSz, sy, slotSz);
            if (a != null) hoverLine = a;
            if (b != null) hoverLine = b;
        }
        // figure area
        int fx0 = ix + 4 + slotSz + 4, fx1 = ix + iw - 4 - slotSz - 4;
        int figW = Math.max(40, fx1 - fx0);
        float scale = Math.max(1.5f, Math.min((ih - 26) / CosmeticaPreview.UNITS_HIGH, figW / CosmeticaPreview.UNITS_WIDE));
        int ox = (fx0 + fx1) / 2, oy = iy + ih - 14;
        // floor glow (flat)
        int fw = Math.round(scale * 14);
        Glass.fillRound(g, ox - fw, oy - 1, fw * 2, 5, 2, 0x50000000);
        Glass.fillRound(g, ox - fw + 4, oy, fw * 2 - 8, 3, 1, withAlpha(0x5AD2FF, 0x40));

        // drag to rotate (only inside the figure area)
        boolean inFig = hit(fx0, iy, figW, ih);
        if (clicked && inFig && !hit(fx1 - 80, iy, 80, 14)) {
            dragging = true;
            lastMx = mx;
            clicked = false;
        }
        if (dragging && down) {
            yaw += (mx - lastMx) * 0.018f;
            lastMx = mx;
        } else if (spin) {
            yaw += 0.012f;
        }
        // worn items + the selected item as a try-on
        st.fillWorn(worn);
        boolean tryOn = sel != null && !st.isEquipped(sel);
        if (tryOn) worn[sel.slot.ordinal()] = sel;
        g.method_44379(fx0, iy, fx1, iy + ih);
        CosmeticaPreview.render(sink, worn, yaw, ox, oy, scale, (System.nanoTime() / 1_000_000L % 3_600_000L) / 1000f);
        g.method_44380();

        // reset camera + spin toggle
        if (button(g, f, ix + iw - 4 - slotSz - 4 - 34, iy + 3, 34, 12, "Reset", BTN, true)) { yaw = CosmeticaPreview.DEFAULT_YAW; spin = false; }
        if (button(g, f, ix + iw - 4 - slotSz - 4 - 34 - 36, iy + 3, 34, 12, spin ? "Spin*" : "Spin", spin ? ACTIVE : BTN, true)) spin = !spin;
        String foot = hoverLine != null ? hoverLine : tryOn ? "Preview: " + sel.name + " (not equipped)" : "Drag to rotate";
        ctxt(g, f, fit(f, foot, iw - 8), ix + iw / 2, iy + ih - 10, hoverLine != null || tryOn ? Theme.text : Theme.dim);

        // ---- info panel
        int infoY = y + pH + GAP, infoH = h - pH - GAP - btnsH - GAP;
        Glass.card(g, x, infoY, w, infoH, false, false);
        if (sel != null) {
            outline(g, x, infoY, w, infoH, withAlpha(sel.rarity.rgb, 0x70));
            icon(g, sel, x + 6, infoY + 5, 1);
            txt(g, f, fit(f, sel.name, w - 32), x + 26, infoY + 4, sel.rarity.argb());
            String meta = sel.rarity.label + "  |  " + sel.slot.category;
            txt(g, f, fit(f, meta, w - 32), x + 26, infoY + 14, Theme.dim);
            String status = st.isEquipped(sel) ? "Equipped" : st.isUnlocked(sel) ? "Unlocked" : "Locked";
            int ly = infoY + 26;
            String[] lines = wrap(f, sel, w - 12);
            for (int i = 0; i < lines.length && ly + 9 <= infoY + infoH - 11; i++, ly += 10) txt(g, f, lines[i], x + 6, ly, Theme.text);
            txt(g, f, status, x + 6, infoY + infoH - 11, st.isEquipped(sel) ? 0xFF4ADE80 : st.isUnlocked(sel) ? Theme.dim : 0xFFFFB02E);
        } else {
            txt(g, f, "Select a cosmetic", x + 6, infoY + 6, Theme.dim);
        }

        // ---- controls
        int by = infoY + infoH + GAP;
        int third = (w - 4) / 3;
        boolean has = sel != null;
        boolean unl = has && st.isUnlocked(sel), eq = has && st.isEquipped(sel);
        if (has && !unl) {
            if (button(g, f, x, by, third * 2 + 2, 14, "Unlock (free)", ACTIVE, true)) {
                st.unlock(sel);
                say("Unlocked " + sel.name);
                commit();
            }
        } else {
            if (button(g, f, x, by, third, 14, "Equip", ACTIVE, has && !eq)) {
                st.equip(sel);
                commit();
            }
            if (button(g, f, x + third + 2, by, third, 14, "Unequip", BTN, has && eq)) {
                st.unequip(sel.slot);
                commit();
            }
        }
        boolean fav = has && st.isFavorite(sel);
        int fx = x + third * 2 + 4, fwid = w - (third * 2 + 4);
        if (button(g, f, fx, by, fwid, 14, fav ? "Unfavorite" : "Favorite", fav ? ACTIVE : BTN, has)) {
            st.toggleFavorite(sel);
            commit();
        }
        int half = (w - 2) / 2;
        if (button(g, f, x, by + 16, half, 14, "Unequip All", BTN, !st.equipped.isEmpty())) {
            st.unequipAll();
            say("All cosmetics removed");
            commit();
        }
        if (button(g, f, x + half + 2, by + 16, w - half - 2, 14, "Save Wardrobe", BTN, true)) {
            st.savePreset(st.activePreset);
            say("Saved to wardrobe " + st.activePreset);
            commit();
        }
    }

    /** One equipment slot cell. Click: unequip when filled, otherwise filter the grid to that category. Returns hover text. */
    private String slotCell(DrawContext g, TextRenderer f, CosmeticaState st, CosmeticSlot s, int x, int y, int sz) {
        Cosmetic c = st.wornIn(s);
        boolean hov = hit(x, y, sz, sz);
        Glass.fillRound(g, x, y, sz, sz, 4, c != null ? withAlpha(c.rarity.rgb, 0x40) : 0x30000000);
        outline(g, x, y, sz, sz, c != null ? withAlpha(c.rarity.rgb, hov ? 0xFF : 0xB0) : hov ? 0xA0FFFFFF : 0x50FFFFFF);
        if (c != null) {
            int sc = sz >= 32 ? 2 : 1;
            int off = (sz - 16 * sc) / 2;
            icon(g, c, x + off, y + off, sc);
        } else {
            ctxt(g, f, fit(f, s.slotName, sz), x + sz / 2, y + (sz - 8) / 2, Theme.dim);
        }
        if (click(x, y, sz, sz)) {
            if (c != null) {
                st.unequip(s);
                commit();
            } else {
                slotFilter = s;
                scrollPx = 0;
            }
        }
        if (!hov) return null;
        return s.slotName + ": " + (c != null ? c.name + " (click to remove)" : "empty (click to filter)");
    }
}
