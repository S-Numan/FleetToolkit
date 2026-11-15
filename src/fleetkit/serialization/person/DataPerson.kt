package fleetkit.serialization.person

import com.fs.starfarer.api.characters.PersonAPI
import fleetkit.serialization.entities.person.DataPerson.buildPerson
import fleetkit.serialization.entities.person.DataPerson.getPersonDataFromPerson

@Deprecated("")
object DataPerson {
    @Deprecated("")
    fun clonePerson(
        person: PersonAPI,
    ): PersonAPI {
        val data = getPersonDataFromPerson(person)
        return buildPerson(data)
    }
}