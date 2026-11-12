package fleetkit;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.loading.WeaponGroupType;

import java.io.*;
import java.util.*;
import java.util.Base64;
import java.util.zip.*;

public class VariantCodec {

    private static final int VERSION = 1;

    // ---------- ENCODE ----------
    public static String encode(ShipVariantAPI variant) {
        Out out = new Out();
        out.writeInt(VERSION);
        writeVariant(out, variant);
        byte[] raw = out.toByteArray();

        Deflater d = new Deflater(Deflater.BEST_COMPRESSION);
        d.setInput(raw);
        d.finish();
        byte[] tmp = new byte[raw.length + 64];
        int n = 0;
        while (!d.finished()) {
            if (n == tmp.length) tmp = Arrays.copyOf(tmp, tmp.length * 2);
            n += d.deflate(tmp, n, tmp.length - n);
        }
        d.end();

        // prefix with raw length so decode knows how much to inflate
        Out wrapped = new Out();
        wrapped.writeInt(raw.length);
        byte[] header = wrapped.toByteArray();
        byte[] result = new byte[header.length + n];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(tmp, 0, result, header.length, n);
        return Base64.getEncoder().encodeToString(result);
    }

    private static void writeVariant(Out out, ShipVariantAPI v) {
        out.writeUTF(v.getHullSpec().getHullId());
        out.writeUTF(v.getDisplayName() == null ? "" : v.getDisplayName());
        out.writeInt(v.getNumFluxVents());
        out.writeInt(v.getNumFluxCapacitors());

        //writeStrings(out, v.getNonBuiltInHullMods());
        writeStrings(out, v.getPermaMods());
        writeStrings(out, v.getSMods());

        // Weapons: slotId -> weaponId
        /*List<String> slots = v.getFittedWeaponSlots();
        out.writeInt(slots.size());
        for (String slot : slots) {
            out.writeUTF(slot);
            out.writeUTF(v.getWeaponId(slot));
        }*/

        // Fighter wings (index-based, may contain nulls)
        List<String> wings = v.getWings();
        out.writeInt(wings.size());
        for (String w : wings) {
            out.writeBoolean(w != null);
            if (w != null) out.writeUTF(w);
        }

        // Weapon groups
        List<WeaponGroupSpec> groups = v.getWeaponGroups();
        out.writeInt(groups.size());
        for (WeaponGroupSpec g : groups) {
            out.writeUTF(g.getType().name());
            out.writeBoolean(g.isAutofireOnByDefault());
            writeStrings(out, g.getSlots());
        }

        // Modules (recursive)
        Map<String, String> modules = v.getStationModules();
        out.writeInt(modules.size());
        for (String slot : modules.keySet()) {
            out.writeUTF(slot);
            writeVariant(out, v.getModuleVariant(slot));
        }
    }

    private static void writeStrings(Out out, Collection<String> c) {
        out.writeInt(c.size());
        for (String s : c) out.writeUTF(s);
    }

    // ---------- DECODE ----------
    public static ShipVariantAPI decode(String base64) {
        byte[] data = Base64.getDecoder().decode(base64);
        int rawLen = new In(data).readInt();

        Inflater inf = new Inflater();
        inf.setInput(data, 4, data.length - 4);
        byte[] raw = new byte[rawLen];
        try {
            int off = 0;
            while (off < rawLen && !inf.finished()) {
                off += inf.inflate(raw, off, rawLen - off);
            }
        } catch (DataFormatException e) {
            throw new IllegalStateException("Corrupt variant code", e);
        } finally {
            inf.end();
        }

        In in = new In(raw);
        int version = in.readInt();
        if (version != VERSION) throw new IllegalStateException("Unsupported version " + version);
        return readVariant(in);
    }

    private static ShipVariantAPI readVariant(In in) {
        String hullId = in.readUTF();
        String displayName = in.readUTF();
        int vents = in.readInt();
        int caps = in.readInt();

        ShipHullSpecAPI hull = Global.getSettings().getHullSpec(hullId);
        ShipVariantAPI v = Global.getSettings().createEmptyVariant(hullId + "_imported", hull);
        v.setVariantDisplayName(displayName);
        v.setNumFluxVents(vents);
        v.setNumFluxCapacitors(caps);

        //for (String m : readStrings(in)) v.addMod(m);
        for (String m : readStrings(in)) v.addPermaMod(m);
        for (String m : readStrings(in)) v.addPermaMod(m, true); // true = s-mod

        /*int weaponCount = in.readInt();
        for (int i = 0; i < weaponCount; i++) {
            String slot = in.readUTF();
            v.addWeapon(slot, in.readUTF());
        }*/

        int wingCount = in.readInt();
        for (int i = 0; i < wingCount; i++) {
            if (in.readBoolean()) v.setWingId(i, in.readUTF());
        }

        int groupCount = in.readInt();
        v.getWeaponGroups().clear();
        for (int i = 0; i < groupCount; i++) {
            WeaponGroupSpec g = new WeaponGroupSpec(WeaponGroupType.valueOf(in.readUTF()));
            g.setAutofireOnByDefault(in.readBoolean());
            for (String s : readStrings(in)) g.addSlot(s);
            v.addWeaponGroup(g);
        }

        int moduleCount = in.readInt();
        for (int i = 0; i < moduleCount; i++) {
            String slot = in.readUTF();
            v.setModuleVariant(slot, readVariant(in));
        }
        return v;
    }

    private static List<String> readStrings(In in) {
        int n = in.readInt();
        List<String> list = new ArrayList<String>(n);
        for (int i = 0; i < n; i++) list.add(in.readUTF());
        return list;
    }
}