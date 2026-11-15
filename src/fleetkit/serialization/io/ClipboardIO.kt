package fleetkit.serialization.io

import com.fs.starfarer.api.campaign.CampaignFleetAPI
import com.fs.starfarer.api.campaign.FleetDataAPI
import com.fs.starfarer.api.characters.PersonAPI
import com.fs.starfarer.api.combat.ShipHullSpecAPI
import com.fs.starfarer.api.combat.ShipVariantAPI
import com.fs.starfarer.api.fleet.FleetMemberAPI
import fleetkit.core.config.FTVariables
import fleetkit.core.util.DisplayMessage
import fleetkit.core.util.FTTxt
import fleetkit.serialization.content.MissingContent
import fleetkit.serialization.core.ConvertibleToEntity
import fleetkit.serialization.entities.fleet.LegacyFleetCode
import fleetkit.serialization.entities.fleet.DataFleet
import fleetkit.serialization.entities.fleet.DataFleet.getFleetDataFromFleet
import fleetkit.serialization.entities.fleet.FleetSettings
import fleetkit.serialization.entities.fleet.FleetJson
import fleetkit.serialization.entities.member.LegacyMemberCode
import fleetkit.serialization.entities.member.DataMember
import fleetkit.serialization.entities.member.DataMember.getMemberDataFromMember
import fleetkit.serialization.entities.member.MemberJson
import fleetkit.serialization.entities.member.MemberSettings
import fleetkit.serialization.entities.person.LegacyPersonCode
import fleetkit.serialization.entities.person.DataPerson
import fleetkit.serialization.entities.person.DataPerson.getPersonDataFromPerson
import fleetkit.serialization.entities.person.PersonJson
import fleetkit.serialization.entities.person.PersonSettings
import fleetkit.serialization.entities.variant.LegacyVariantCode
import fleetkit.serialization.entities.variant.DataVariant
import fleetkit.serialization.entities.variant.DataVariant.getVariantDataFromVariant
import fleetkit.serialization.entities.variant.VariantJson
import fleetkit.serialization.entities.variant.VariantSettings
import fleetkit.util.lib.ClipboardUtil
import org.lwjgl.input.Keyboard
import org.magiclib.util.MagicLookup
import org.magiclib.util.api.createHullVariant
import org.magiclib.util.reflection.boxed.BoxedCodexDialog
import java.awt.Color

object ClipboardIO {

    @JvmOverloads
    fun saveToClipboard(
        variant: ShipVariantAPI,
        shift: Boolean = false,
        settings: VariantSettings = VariantSettings()
    ): Boolean {
        val data = getVariantDataFromVariant(variant, filterParsed = false) // The filtering comes later
        return saveToClipboard(data, shift, settings)
    }

    @JvmOverloads
    fun saveToClipboard(
        variant: DataVariant.ParsedVariantData,
        shift: Boolean = false,
        settings: VariantSettings = VariantSettings()
    ): Boolean {
        val settings = settings.apply {
            includeDefaultJSON = true
            excludeTagsWithID += FTVariables.DEFAULT_EXCLUDE_TAGS_ON_VARIANT_COPY
        }

        val dataVariant = DataVariant.filterParsedVariantData(variant, settings)//.copy(variantId = VariantUtils.makeVariantID(variant.hullId, variant.displayName))

        if (dataVariant.tags.contains(FTVariables.NO_COPY_TAG) || MagicLookup.getHullSpec(dataVariant.hullId)?.hasTag(FTVariables.NO_COPY_TAG) == true) {
            DisplayMessage.showMessage(FTTxt.txt("no_copy_no_copy_tag"), Color.YELLOW)
            return false
        }

        if (!shift) {
            val comp = dataVariant.toCode()
            ClipboardUtil.setClipboardText(comp)
            DisplayMessage.showMessage(FTTxt.txt("variant_copied_to_clipboard_compressed"))
        } else {
            val json = VariantJson.saveVariantToJson(
                dataVariant, settings
            )
            ClipboardUtil.setClipboardText(json.toString(4))
            DisplayMessage.showMessage(FTTxt.txt("variant_copied_to_clipboard"))
        }

        return true
    }

    @JvmOverloads
    fun saveToClipboard(
        person: PersonAPI,
        shift: Boolean = false,
        settings: PersonSettings = PersonSettings()
    ): Boolean {
        if (person.isDefault) {
            DisplayMessage.showMessage(FTTxt.txt("no_copy_default_officer"), Color.YELLOW)
            return false
        }
        val data = getPersonDataFromPerson(person, settings)
        return saveToClipboard(data, shift)
    }

    @JvmOverloads
    fun saveToClipboard(
        person: DataPerson.ParsedPersonData,
        shift: Boolean = false,
        settings: PersonSettings? = null
    ): Boolean {
        val dataPerson = if (settings != null)
            DataPerson.filterParsedPersonData(person, settings)
        else
            person

        if (dataPerson.tags.contains(FTVariables.NO_COPY_TAG)) {
            DisplayMessage.showMessage(FTTxt.txt("no_copy_no_copy_tag"), Color.YELLOW)
            return false
        }

        if (!shift) {
            val comp = dataPerson.toCode()
            ClipboardUtil.setClipboardText(comp)
            DisplayMessage.showMessage(FTTxt.txt("officer_copied_to_clipboard_compressed"))
        } else {
            val json = PersonJson.savePersonToJson(dataPerson)
            ClipboardUtil.setClipboardText(json.toString(4))
            DisplayMessage.showMessage(FTTxt.txt("officer_copied_to_clipboard"))
        }

        return true
    }

    @JvmOverloads
    fun saveToClipboard(
        member: FleetMemberAPI,
        shift: Boolean = false,
        settings: MemberSettings = MemberSettings()
    ): Boolean {
        val data = getMemberDataFromMember(member, settings)
        return saveToClipboard(data, shift)
    }

    @JvmOverloads
    fun saveToClipboard(
        member: DataMember.ParsedMemberData,
        shift: Boolean = false,
        settings: MemberSettings? = null
    ): Boolean {
        val dataMember = if (settings != null)
            DataMember.filterParsedMemberData(member, settings)
        else
            member

        if (dataMember.variantData != null && (dataMember.variantData.tags.contains(FTVariables.NO_COPY_TAG) || MagicLookup.getHullSpec(dataMember.variantData.hullId)?.hasTag(FTVariables.NO_COPY_TAG) == true)) {
            DisplayMessage.showMessage(FTTxt.txt("no_copy_no_copy_tag"), Color.YELLOW)
            return false
        }

        if (!shift) {
            val comp = dataMember.toCode()
            ClipboardUtil.setClipboardText(comp)
            DisplayMessage.showMessage(FTTxt.txt("member_copied_to_clipboard_compressed"))
        } else {
            val json = MemberJson.saveMemberToJson(dataMember)
            ClipboardUtil.setClipboardText(json.toString(4))
            DisplayMessage.showMessage(FTTxt.txt("member_copied_to_clipboard"))
        }

        return true
    }

    @JvmOverloads
    fun saveToClipboard(fleet: CampaignFleetAPI, shift: Boolean = false, settings: FleetSettings = FleetSettings()): Boolean =
        saveToClipboard(getFleetDataFromFleet(fleet, settings), shift)

    @JvmOverloads
    fun saveToClipboard(fleet: FleetDataAPI, shift: Boolean = false, settings: FleetSettings = FleetSettings()): Boolean =
        saveToClipboard(getFleetDataFromFleet(fleet, settings), shift)

    @JvmOverloads
    fun saveToClipboard(
        fleet: DataFleet.ParsedFleetData,
        shift: Boolean = false,
        settings: FleetSettings? = null
    ): Boolean {
        val dataFleet = if (settings != null)
            DataFleet.filterParsedFleetData(fleet, settings)
        else
            fleet

        if (!shift) {
            val comp = dataFleet.toCode()
            ClipboardUtil.setClipboardText(comp)
            DisplayMessage.showMessage(FTTxt.txt("copied_entire_fleet_to_clipboard_compressed"))
        } else {
            val json = FleetJson.saveFleetToJson(dataFleet)
            ClipboardUtil.setClipboardText(json.toString(4))
            DisplayMessage.showMessage(FTTxt.txt("copied_entire_fleet_to_clipboard"))
        }

        return true
    }

    fun codexEntryToClipboard() {
        val param = BoxedCodexDialog.get()?.getEntryParam() ?: return

        when (param) {
            is ShipHullSpecAPI -> {
                val emptyVariant = param.createHullVariant()
                saveToClipboard(emptyVariant, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            }

            is FleetMemberAPI -> {
                saveToClipboard(param, Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            }
        }
    }

    @JvmOverloads
    fun loadDataFromClipboard(missing: MissingContent = MissingContent()): ConvertibleToEntity<*>? {
        val contents = ClipboardUtil.getClipboardContents() ?: return null

        return CodeImporter.extractDataFromString(contents, missing)
    }
}