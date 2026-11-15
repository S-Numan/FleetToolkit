package fleetkit.util.api.kotlin

import com.fs.starfarer.api.characters.MutableCharacterStatsAPI
import com.fs.starfarer.api.characters.PersonAPI
import fleetkit.serialization.entities.person.DataPerson
import fleetkit.serialization.entities.person.PersonSettings

/**
 * Checks if the person has any skills with a level greater than 0.
 */
fun PersonAPI.hasAnySkill(): Boolean {
    for (skill in stats.skillsCopy) {
        if (skill.level > 0f) {
            return true
        }
    }
    return false
}

fun PersonAPI.getCombatOfficerSkills(): List<MutableCharacterStatsAPI.SkillLevelAPI> {
    return stats.skillsCopy.mapNotNull { skill ->
        if (skill.level <= 0f)
            null
        else if (skill.skill.isCombatOfficerSkill)
            skill
        else
            null
    }
}

fun PersonAPI.getAdmiralSkills(): List<MutableCharacterStatsAPI.SkillLevelAPI> {
    return stats.skillsCopy.mapNotNull { skill ->
        if (skill.level <= 0f)
            null
        else if (skill.skill.isAdmiralSkill)
            skill
        else
            null
    }
}

fun PersonAPI.getAdminSkills(): List<MutableCharacterStatsAPI.SkillLevelAPI> {
    return stats.skillsCopy.mapNotNull { skill ->
        if (skill.level <= 0f)
            null
        else if (skill.skill.isAdminSkill)
            skill
        else
            null
    }
}

fun PersonAPI.getEliteSkills(): List<MutableCharacterStatsAPI.SkillLevelAPI> {
    return stats.skillsCopy.mapNotNull { skill ->
        if (skill.level <= 0f)
            null
        else if (skill.skill.isElite)
            skill
        else
            null
    }
}