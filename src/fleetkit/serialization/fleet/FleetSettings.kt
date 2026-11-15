package fleetkit.serialization.fleet

import fleetkit.serialization.entities.member.MemberSettings
import fleetkit.serialization.entities.fleet.FleetSettings as NewFleetSettings

@Deprecated(
    message = "Moved to fleetkit.serialization.entities.fleet.FleetSettings",
    replaceWith = ReplaceWith(
        "FleetSettings",
        "fleetkit.serialization.entities.fleet.FleetSettings"
    )
)
data class FleetSettings(
    var aiMode: Boolean = true,
    var includeCommanderSetFlagship: Boolean = true,
    var includeCommanderAsOfficer: Boolean = true,
    var includeIdleOfficers: Boolean = true,
    var includeAggression: Boolean = true,
    var excludeMembersWithMissingHullSpec: Boolean = false,
    var excludeMembersWithID: MutableSet<String> = mutableSetOf(),
    var excludeMembersWithHullID: MutableSet<String> = mutableSetOf(),
    var includeMemKeys: Boolean = false,
    var memberSettings: MemberSettings = MemberSettings(),
) {
    fun toNew() = NewFleetSettings(
        aiMode = aiMode,
        includeCommanderSetFlagship = includeCommanderSetFlagship,
        includeCommanderAsOfficer = includeCommanderAsOfficer,
        includeIdleOfficers = includeIdleOfficers,
        includeAggression = includeAggression,
        excludeMembersWithMissingHullSpec = excludeMembersWithMissingHullSpec,
        excludeMembersWithID = excludeMembersWithID,
        excludeMembersWithHullID = excludeMembersWithHullID,
        includeMemKeys = includeMemKeys,
        memberSettings = memberSettings,
    )
}