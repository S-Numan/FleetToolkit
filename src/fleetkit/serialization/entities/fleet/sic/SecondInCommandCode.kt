package fleetkit.serialization.entities.fleet.sic

import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.Format
import fleetkit.serialization.entities.person.PersonCode

object SecondInCommandCode : Format<DataSecondInCommand.SecondInCommandData> {
    override val typeId = 'S'
    override val version = 1
    override fun sync(c: Codec, d: DataSecondInCommand.SecondInCommandData?) = DataSecondInCommand.SecondInCommandData(
        officers = c.list(d?.officers, SecondInCommandOfficerFormat).toMutableList(),
    )
}

object SecondInCommandOfficerFormat : Format<DataSecondInCommand.SecondInCommandOfficerData> {
    override val typeId = 's'
    override val version = 1
    override fun sync(c: Codec, d: DataSecondInCommand.SecondInCommandOfficerData?) = DataSecondInCommand.SecondInCommandOfficerData(
        person = c.opt(d?.person, PersonCode)!!,
        aptitudeId = c.str(d?.aptitudeId),
        skillPoints = c.int(d?.skillPoints),
        experiencePoints = c.float(d?.experiencePoints),
        activeSkillIDs = c.strList(d?.activeSkillIDs),
        assignedSlot = c.optInt(d?.assignedSlot),
        level = c.int(d?.level)
    )
}
