package fleetkit.serialization.dictionaries

import fleetkit.serialization.dictionaries.PackDictionaryV1.v1

object PackDictionaries {
    // '0' means no dictionary
    val byVersion: Map<Char, ByteArray> = mapOf('1' to v1) // Active dictionaries.
    const val LATEST = '1' // Dictionary used to encode with
}