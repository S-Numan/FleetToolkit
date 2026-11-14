package fleetkit.serialization.compressor.formats

import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import fleetkit.serialization.GameModInfo
import fleetkit.serialization.compressor.Codec
import fleetkit.serialization.compressor.Format
import fleetkit.serialization.compressor.ModHeader
import fleetkit.serialization.person.DataPerson
import fleetkit.serialization.variant.DataVariant
import fleetkit.util.api.PersonUtils
import fleetkit.util.api.VariantUtils

@Suppress("EnumValuesSoftDeprecate")
object PersonFormat : Format<DataPerson.ParsedPersonData> {
    override val typeId = 'p'
    override val version = 1
    override fun sync(c: Codec, d: DataPerson.ParsedPersonData?) = DataPerson.ParsedPersonData(
        aiCoreId = c.str(d?.aiCoreId),
        first = c.str(d?.first),
        last = c.str(d?.last),
        gender = c.enum(d?.gender, FullName.Gender.values()),
        portrait = c.optStr(d?.portrait),
        tags = c.strSet(d?.tags),
        rankId = c.str(d?.rankId),
        postId = c.str(d?.postId),
        personality = c.str(d?.personality),
        level = c.optInt(d?.level),
        skills = c.map(d?.skills) { c.float2(it) },
        xp = c.long(d?.xp),
        bonusXp = c.long(d?.bonusXp),
        points = c.int(d?.points),
        memKeys = c.map(d?.memKeys) { c.any(it) }
    )
    /** If [mods] is null, it will automatically be generated */
    override fun encode(data: DataPerson.ParsedPersonData, withPrepend: Boolean, mods: ModHeader?): String? {
        return super.encode(data, withPrepend, mods ?: ModHeader(PersonUtils.getAllSourceModsFromPerson(data).map { GameModInfo(it.id, it.name, it.version) }))
    }

    fun encode(person: PersonAPI, withPrepend: Boolean = true): String? {
        return encode(DataPerson.getPersonDataFromPerson(person), withPrepend)
    }
}