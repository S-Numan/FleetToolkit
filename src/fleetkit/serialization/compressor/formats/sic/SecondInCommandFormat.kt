package fleetkit.serialization.compressor.formats.sic

import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.fleet.mods.secondInCommand.DataSecondInCommand

object SecondInCommandFormat : Format<DataSecondInCommand.SecondInCommandData> {
    override val typeId = 'S'
    override val version = 1
    override fun sync(c: Codec, d: DataSecondInCommand.SecondInCommandData?) = DataSecondInCommand.SecondInCommandData(
        officers = c.list(d?.officers, SecondInCommandOfficerFormat).toMutableList(),
    )
}
