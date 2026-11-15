package fleetkit.util.api.kotlin

import com.fs.starfarer.api.combat.ShipVariantAPI
import fleetkit.serialization.entities.variant.DataVariant
import fleetkit.serialization.entities.variant.VariantSettings
import fleetkit.util.api.VariantUtils

fun ShipVariantAPI.isEquivalentTo(
    other: ShipVariantAPI,
    options: VariantUtils.CompareOptions = VariantUtils.CompareOptions()
): Boolean {
    return VariantUtils.compareVariantContents(
        this,
        other,
        options
    )
}
