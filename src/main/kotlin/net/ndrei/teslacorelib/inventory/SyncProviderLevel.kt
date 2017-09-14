package net.ndrei.teslacorelib.inventory

enum class SyncProviderLevel(private val requiresGui: Boolean) {
    TICK(false),
    GUI(true);

    fun shouldSync(hasGui: Boolean) = !this.requiresGui || (this.requiresGui && hasGui)
}
