package fleetkit.serialization.entities.person

import com.fs.starfarer.api.characters.FullName
import com.fs.starfarer.api.characters.PersonAPI
import fleetkit.serialization.content.ModVersionData
import fleetkit.serialization.core.Codec
import fleetkit.serialization.core.EntityFormat
import fleetkit.serialization.core.ModHeader
import fleetkit.util.api.PersonUtils

@Suppress("EnumValuesSoftDeprecate")
object PersonCode : EntityFormat<DataPerson.ParsedPersonData> {
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
    override fun encode(data: DataPerson.ParsedPersonData, withPrepend: Boolean, mods: ModHeader?): String {
        val mods = mods ?: ModHeader(PersonUtils.getAllSourceModsFromPerson(data).map { ModVersionData(it.id, it.name, it.version) })
        val output = super.encode(data, withPrepend = false, mods)
        if (!withPrepend) {
            return output
        } else {
            val readable = "${data.first}${if (data.last.isNotEmpty()) " " + data.last else ""}" // Add skill count maybe?
            return "$readable ${mods.describe()}\n$output"
        }
    }

    fun encode(person: PersonAPI, withPrepend: Boolean = true) =
        encode(DataPerson.getPersonDataFromPerson(person), withPrepend)

}