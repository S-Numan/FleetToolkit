package fleetkit.serialization.fleet

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import fleetkit.serialization.content.MissingContent
import fleetkit.serialization.entities.fleet.sic.DataSecondInCommand
import fleetkit.serialization.entities.member.DataMember
import fleetkit.serialization.entities.person.DataPerson
import java.util.*
import fleetkit.serialization.entities.fleet.DataFleet as NewDataFleet
import fleetkit.serialization.fleet.FleetSettings as OldFleetSettings

@Deprecated("")
object DataFleet {

    @Deprecated(
        message = "Moved to fleetkit.serialization.entities.fleet.DataFleet.ParsedFleetData",
        replaceWith = ReplaceWith(
            "DataFleet.ParsedFleetData",
            "fleetkit.serialization.entities.fleet.DataFleet"
        )
    )
    data class ParsedFleetData(
        val fleetName: String? = null,
        val aggression: Int = -1,
        val factionID: String? = null,
        val commanderIfNoFlagship: DataPerson.ParsedPersonData? = null,
        val members: List<DataMember.ParsedMemberData> = emptyList(),
        val idleOfficers: List<DataPerson.ParsedPersonData> = emptyList(),
        val memKeys: Map<String, Any?> = emptyMap(),
        val secondInCommandData: DataSecondInCommand.SecondInCommandData? = null,
    ) {
        fun toNew() = NewDataFleet.ParsedFleetData(
            fleetName = fleetName,
            aggression = aggression.takeIf { it > -1 },
            factionID = factionID,
            commanderIfNoFlagship = commanderIfNoFlagship,
            members = members,
            idleOfficers = idleOfficers,
            memKeys = memKeys,
            secondInCommandData = secondInCommandData,
        )
        companion object {
            @Suppress("DEPRECATION")
            fun fromNew(new: NewDataFleet.ParsedFleetData) = ParsedFleetData(
                fleetName = new.fleetName,
                aggression = new.aggression ?: 2,
                factionID = new.factionID,
                commanderIfNoFlagship = new.commanderIfNoFlagship,
                members = new.members,
                idleOfficers = new.idleOfficers,
                memKeys = new.memKeys,
                secondInCommandData = new.secondInCommandData,
            )
        }
    }

    @Deprecated("Use other one", ReplaceWith("createCampaignFleetFromData(data, settings, missing, random)", "fleetkit.serialization.entities.fleet.DataFleet.createCampaignFleetFromData"))
    @JvmOverloads
    @Suppress("DEPRECATION")
    fun createCampaignFleetFromData(
        data: ParsedFleetData,
        aiMode: Boolean,
        settings: OldFleetSettings = OldFleetSettings(),
        missing: MissingContent = MissingContent(),
        random: Random = Random()
    ): CampaignFleetAPI {
        return NewDataFleet.createCampaignFleetFromData(data.toNew(), settings.toNew(), missing, random)
    }
}