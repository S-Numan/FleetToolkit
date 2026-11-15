package fleetkit.serialization.content

import fleetkit.core.util.FTTxt

class MissingContentExtended : MissingContent() {
    val blueprintWeaponIds: MutableSet<String> = mutableSetOf()
    val blueprintWingIds: MutableSet<String> = mutableSetOf()
    val blueprintHullIds: MutableSet<String> = mutableSetOf()
    val blueprintIndustryIds: MutableSet<String> = mutableSetOf()
    val hullModIdsKnown: MutableSet<String> = mutableSetOf()
    val cargoWeaponIds: MutableSet<String> = mutableSetOf()
    val cargoWingIds: MutableSet<String> = mutableSetOf()
    val itemIds: MutableSet<String> = mutableSetOf()

    override fun hasMissing(): Boolean {
        return super.hasMissing() || hasMissingBlueprints()
    }

    fun hasMissingBlueprints(): Boolean {
        return blueprintWeaponIds.isNotEmpty() ||
                blueprintWingIds.isNotEmpty() ||
                blueprintHullIds.isNotEmpty() ||
                blueprintIndustryIds.isNotEmpty() ||
                hullModIdsKnown.isNotEmpty() ||
                cargoWeaponIds.isNotEmpty() ||
                cargoWingIds.isNotEmpty() ||
                itemIds.isNotEmpty()
    }

    override fun add(other: MissingContent) {
        super.add(other)
        if (other is MissingContentExtended) {
            blueprintWeaponIds.addAll(other.blueprintWeaponIds)
            blueprintWingIds.addAll(other.blueprintWingIds)
            blueprintHullIds.addAll(other.blueprintHullIds)
            blueprintIndustryIds.addAll(other.blueprintIndustryIds)
            hullModIdsKnown.addAll(other.hullModIdsKnown)
            cargoWeaponIds.addAll(other.cargoWeaponIds)
            cargoWingIds.addAll(other.cargoWingIds)
            itemIds.addAll(other.itemIds)
        }
    }

    override fun getMissingContentString(doNotPrintEnabledMods: Boolean): String {
        val base = super.getMissingContentString(doNotPrintEnabledMods)
        val extra = mutableListOf<String>()

        printIfNotEmpty(FTTxt.txt("missing_weapon_blueprints"), extra, blueprintWeaponIds)
        printIfNotEmpty(FTTxt.txt("missing_wing_blueprints"), extra, blueprintWingIds)
        printIfNotEmpty(FTTxt.txt("missing_hull_blueprints"), extra, blueprintHullIds)
        printIfNotEmpty(FTTxt.txt("missing_industry_blueprints"), extra, blueprintIndustryIds)
        printIfNotEmpty(FTTxt.txt("missing_hullmods_known"), extra, hullModIdsKnown)
        printIfNotEmpty(FTTxt.txt("missing_weapon_cargo"), extra, cargoWeaponIds)
        printIfNotEmpty(FTTxt.txt("missing_wing_cargo"), extra, cargoWingIds)
        printIfNotEmpty(FTTxt.txt("missing_items"), extra, itemIds)

        return (extra.filter { it.isNotBlank() } + listOf(base))
            .filter { it.isNotBlank() }
            .joinToString(FTTxt.txt("missing_section_separator"))
    }
}