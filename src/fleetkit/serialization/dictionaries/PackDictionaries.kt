package fleetkit.serialization.dictionaries

import fleetkit.serialization.dictionaries.PackDictionaryV1.v1

object PackDictionaries {
    // When adding new dictionary: add v2 to byVersion, set LATEST = '2'
    val byVersion: Map<Char, ByteArray> = mapOf('1' to v1)
    const val LATEST = '1'
}