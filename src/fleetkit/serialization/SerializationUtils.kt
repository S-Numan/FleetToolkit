package fleetkit.serialization

import fleetkit.serialization.content.MissingContent
import fleetkit.serialization.core.ConvertibleToEntity
import fleetkit.serialization.entities.fleet.DataFleet
import fleetkit.serialization.io.CodeImporter.extractDataFromJSON
import fleetkit.serialization.io.CodeImporter.extractDataFromString
import fleetkit.serialization.io.JSONInput
import org.json.JSONObject
import fleetkit.serialization.fleet.DataFleet.ParsedFleetData as OldParsedFleetData

@Deprecated("")
object SerializationUtils {
    fun extractDataFromAny(value: Any): Any? {
        if (value is String) {
            val output = extractDataFromString(value)
            if (output is DataFleet.ParsedFleetData) {
                @Suppress("DEPRECATION")
                return OldParsedFleetData.fromNew(output)
            }
        }

        return null
    }
    fun getJSONFromStringSafe(inputText: String): JSONObject? {
        return JSONInput.getJSONFromStringSafe(inputText)
    }
}