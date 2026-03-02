package me.udnek.coreu.resourcepack.misc

import me.udnek.coreu.serializabledata.SerializableData
import java.util.Map


class RpInfo : SerializableData {
    @JvmField
    var extractDirectory: String? = null
    @JvmField
    var checksum_zip: String? = null
    @JvmField
    var checksum_folder: String? = null
    @JvmField
    var ip: String = "null"
    @JvmField
    var port: Int = 0

    override fun serialize(): String {
        return SerializableData.serializeMap(
            Map.of<String, Any>(
                "extract_directory", if (extractDirectory == null) "null" else extractDirectory,
                "checksum_zip", if (checksum_zip == null) "null" else checksum_zip,
                "checksum_folder", if (checksum_folder == null) "null" else checksum_folder,
                "ip", ip,
                "port", port
            )
        )
    }

    override fun deserialize(data: String?) {
        val map = SerializableData.deserializeMap(data)
        extractDirectory = map.getOrDefault("extract_directory", "")
        checksum_zip = map.getOrDefault("checksum_zip", "")
        checksum_folder = map.getOrDefault("checksum_folder", "")
        ip = map.getOrDefault("ip", "127.0.0.1")
        port = map.getOrDefault("port", "25566").toInt()
    }

    override fun getDataName(): String {
        return "resourcepack_settings"
    }
}
