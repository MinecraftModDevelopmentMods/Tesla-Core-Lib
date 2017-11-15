package net.modcrafters.mclib

inline fun<T, R: Any> Iterable<T>.mapFirstOrNull(filter: (T) -> R?): R? {
    this.forEach {
        val value = filter(it)
        if (value != null)
            return value
    }
    return null
}