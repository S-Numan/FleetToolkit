package fleetkit.serialization.dictionaries

import fleetkit.serialization.dictionaries.PackDictionaryV1.v1
import fleetkit.serialization.dictionaries.PackDictionaryV2.v2

object PackDictionaries {
    // When adding new dictionary: add v2 to byVersion, set LATEST = '2'
    val byVersion: Map<Char, ByteArray> = mapOf('1' to v1, '2' to v2)
    const val LATEST = '2'
}