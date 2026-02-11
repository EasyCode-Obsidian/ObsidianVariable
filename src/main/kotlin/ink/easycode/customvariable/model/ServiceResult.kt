package ink.easycode.customvariable.model

sealed interface ServiceResult<out T> {

    data class Ok<T>(val value: T) : ServiceResult<T>

    data class Error(
        val code: ServiceErrorCode,
        val args: Array<out Any> = emptyArray()
    ) : ServiceResult<Nothing> {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Error

            if (code != other.code) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = code.hashCode()
            result = 31 * result + args.contentHashCode()
            return result
        }
    }
}
