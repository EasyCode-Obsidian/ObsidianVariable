package ink.easycode.customvariable.i18n

import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.function.console
import taboolib.common.platform.function.info
import taboolib.module.lang.getLocaleFile
import taboolib.module.lang.registerLanguage

object LanguageBootstrap {

    @Volatile
    private var started = false

    @Awake(LifeCycle.ENABLE)
    fun start() {
        if (started) {
            return
        }
        registerLanguage("zh_CN", "en_US")
        val loaded = console().getLocaleFile() != null
        info("[CustomVariable] Language bootstrap loaded=$loaded")
        started = true
    }
}
