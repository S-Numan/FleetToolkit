package fleetkit.serialization.core

import fleetkit.serialization.content.MissingContent
import java.util.Random

interface ConvertibleToEntity<out R> {
    /**
     * Generic function to convert this class into a game entity.
     *
     * Validates, then filters, then builds.
     */
    fun toEntity(
        missing: MissingContent = MissingContent(),
        random: Random = Random()
    ): R
}