package fleetkit.serialization.compressor.formats.sic

import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.compressor.formats.PersonFormat
import fleetkit.serialization.fleet.mods.secondInCommand.DataSecondInCommand

object SecondInCommandOfficerFormat : Format<DataSecondInCommand.SecondInCommandOfficerData> {
    override val typeId = 's'
    override val version = 1
    override fun sync(c: Codec, d: DataSecondInCommand.SecondInCommandOfficerData?) = DataSecondInCommand.SecondInCommandOfficerData(
        person = c.opt(d?.person, PersonFormat)!!,
        aptitudeId = c.str(d?.aptitudeId),
        skillPoints = c.int(d?.skillPoints),
        experiencePoints = c.float(d?.experiencePoints),
        activeSkillIDs = c.strList(d?.activeSkillIDs),
        assignedSlot = c.optInt(d?.assignedSlot),
        level = c.int(d?.level)
    )
}
