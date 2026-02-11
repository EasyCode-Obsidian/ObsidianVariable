package ink.easycode.customvariable.service

import ink.easycode.customvariable.codec.DefaultTypeCodecRegistry
import ink.easycode.customvariable.model.RegistryEntry
import ink.easycode.customvariable.model.VariableType

object RegistryDefaultResolver {

    fun normalizeForRegister(type: VariableType, raw: String?): String? {
        if (raw == null) {
            return null
        }
        return DefaultTypeCodecRegistry.normalizeOrNull(type, raw)
    }

    fun resolveForTypeChange(previousDefault: String?, targetType: VariableType): String {
        val normalized = previousDefault?.let {
            DefaultTypeCodecRegistry.normalizeOrNull(targetType, it)
        }
        return normalized ?: DefaultTypeCodecRegistry.defaultRaw(targetType)
    }

    fun resolveForRead(entry: RegistryEntry): String {
        val normalized = entry.defaultRaw?.let {
            DefaultTypeCodecRegistry.normalizeOrNull(entry.type, it)
        }
        return normalized ?: DefaultTypeCodecRegistry.defaultRaw(entry.type)
    }
}
