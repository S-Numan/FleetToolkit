package fleetkit.serialization.compressor

import com.fs.starfarer.api.Global
import fleetkit.core.util.FTTxt
import fleetkit.serialization.GameModInfo // adjust to wherever your existing GameModInfo(id, name, version) lives

/** Which game version and mods a code was made with. Stored inside every code (optionally). */
data class ModHeader(
    val mods: List<GameModInfo>,
    val gameVersion: String = Global.getSettings().gameVersion
) {
    fun toGameModInfos(): Set<GameModInfo> =
        LinkedHashSet<GameModInfo>().apply {
            if (gameVersion.isNotEmpty())
                add(GameModInfo(STARSECTOR_ID, "Starsector", gameVersion))
            addAll(mods)
        }

    /** Mods this code needs that the predicate says aren't enabled, e.g. `missing(modManager::isModEnabled)`. */
    fun missing(isEnabled: (String) -> Boolean): List<GameModInfo> =
        mods.filterNot { isEnabled(it.id) }

    /**
     * One-line text for the user, same shape as the old format: "(0.98a-RC8) : <prefix>(Mod A) , (Mod B)".
     * Pass FTTxt.txt("mods_used_prefix") as the prefix. Never contains line breaks, because the code goes on the next line.
     */
    fun describe(modsUsedPrefix: String = FTTxt.txt("mods_used_prefix")): String {
        val sb = StringBuilder("(").append(gameVersion).append(")")
        if (mods.isNotEmpty()) {
            sb.append(" : ").append(modsUsedPrefix)
            sb.append(mods.joinToString(" , ") { "(${it.name})" })
        }
        return sb.toString().replace('\n', ' ').replace('\r', ' ')
    }

    companion object {
        const val STARSECTOR_ID = "SS"
    }
}

/** A mod header with a presence flag, so codes made without mod info cost a single byte. */
fun Codec.optModHeader(v: ModHeader?): ModHeader? = opt(v) { h ->
    ModHeader(
        gameVersion = str(h?.gameVersion),
        mods = list<GameModInfo>(h?.mods) { m -> GameModInfo(str(m?.id), str(m?.name), str(m?.version)) }
    )
}

/** Result of decoding: the data plus the mod header, if the code carried one. */
data class Decoded<T>(val data: T, val mods: ModHeader?)