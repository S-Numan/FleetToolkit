package fleetkit.serialization.compressor

import com.fs.starfarer.api.impl.campaign.ids.Personalities

object PackDictionaries {
    /** Builds a dictionary from strings ordered least common -> most common. Max 32 KB. */
    fun build(strings: List<String>): ByteArray {
        val out = ByteBuffers.Out()
        for (s in strings) out.writeUTF(s)
        val bytes = out.toByteArray()
        // zlib only uses the last 32 KB, so drop the front (least common) if too large
        return if (bytes.size > 32768) bytes.copyOfRange(bytes.size - 32768, bytes.size) else bytes
    }

    // NEVER edit this list after codes have shipped. Make a new version instead.
    val v1: ByteArray = build(listOf(
        // rarest first ... most common last
        "ALTERNATING", "LINKED",
        Personalities.TIMID, Personalities.CAUTIOUS, Personalities.STEADY, Personalities.AGGRESSIVE, Personalities.RECKLESS,
        "spaceLieutenant", "spaceCommander", "officer",
        "player", "pirates", "hegemony", "tritachyon", "persean", "sindrian_diktat", "lions_guard", "independent",
        "hullmod_placeholder_replace_with_real_ids",
        "hullmod_placeholder_2"
        // ...fill in real IDs, see below
    ))

    // When adding new dictionary: add v2 to byVersion, set LATEST = '2'
    val byVersion: Map<Char, ByteArray> = mapOf('1' to v1)
    const val LATEST = '1'
}