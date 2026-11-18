package fleetkit.serialization.dictionaries

import fleetkit.serialization.dictionaries.PackDictionaryV1.v1
import fleetkit.serialization.dictionaries.PackDictionaryV2.v2

object PackDictionaries {
    // '0' means no dictionary
    val byVersion: Map<Char, ByteArray> = mapOf('1' to v1, '2' to v2) // Active dictionaries.
    const val LATEST = '2' // Dictionary used to encode with
}