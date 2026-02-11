package ink.easycode.customvariable

import taboolib.common.platform.Plugin

object CustomVariable : Plugin() {

    init {
        bootstrapSlf4jProvider()
    }

    private fun bootstrapSlf4jProvider() {
        val property = "slf4j.provider"
        if (!System.getProperty(property).isNullOrBlank()) {
            return
        }

        val classLoader = CustomVariable::class.java.classLoader
        val provider = findProvider(classLoader) ?: return
        System.setProperty(property, provider)
    }

    private fun findProvider(classLoader: ClassLoader): String? {
        val candidates = listOf(
            "org.apache.logging.slf4j.SLF4JServiceProvider",
            "org.slf4j.jul.JULServiceProvider",
            "org.slf4j.helpers.NOP_FallbackServiceProvider"
        )
        return candidates.firstOrNull { name ->
            runCatching { Class.forName(name, false, classLoader) }.isSuccess
        }
    }
}
