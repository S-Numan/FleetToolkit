package fleetkit.serialization.compressor.formats

import com.fs.starfarer.api.characters.FullName
import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.person.DataPerson

@Suppress("EnumValuesSoftDeprecate")
object PersonFormat : Format<DataPerson.ParsedPersonData> {
    override val typeId = 'p'
    override val version = 1
    override fun sync(c: Codec, d: DataPerson.ParsedPersonData?) = DataPerson.ParsedPersonData(
        aiCoreId = c.s(d?.aiCoreId),
        first = c.s(d?.first),
        last = c.s(d?.last),
        gender = c.enum(d?.gender, FullName.Gender.values()),
        portrait = c.optS(d?.portrait),
        tags = c.strSet(d?.tags),
        rankId = c.s(d?.rankId),
        postId = c.s(d?.postId),
        personality = c.s(d?.personality),
        level = c.optI(d?.level),
        skills = c.map(d?.skills) { c.f(it) },
        xp = c.l(d?.xp),
        bonusXp = c.l(d?.bonusXp),
        points = c.i(d?.points),
        memKeys = c.map(d?.memKeys) { c.any(it) }
    )
}