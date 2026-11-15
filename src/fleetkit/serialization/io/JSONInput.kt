package fleetkit.serialization.io

import org.json.JSONObject

object JSONInput {
    @JvmStatic
    fun cleanJsonStringInput(raw: String): String {
        return raw.lines()
            .map { line ->
                var inQuotes = false
                val sb = StringBuilder()

                var i = 0
                while (i < line.length) {
                    val c = line[i]

                    if (c == '"') {
                        // Check for escaped quote
                        val escaped = i > 0 && line[i - 1] == '\\'
                        if (!escaped) inQuotes = !inQuotes
                    }

                    // If we hit a # and we're not in quotes, stop processing this line
                    if (c == '#' && !inQuotes) break

                    sb.append(c)
                    i++
                }

                sb.toString().trimEnd()
            }
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }

    @JvmStatic
    fun getJSONFromStringSafe(inputText: String): JSONObject? {
        var text = inputText
        text = cleanJsonStringInput(text)

        if (text.isEmpty()) return null

        return try {
            JSONObject(text)
        } catch (_: Exception) {
            null
        }
    }
}