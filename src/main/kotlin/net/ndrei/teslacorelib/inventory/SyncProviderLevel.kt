package net.ndrei.teslacorelib.inventory

enum class SyncProviderLevel(private val syncWhenGuiOpened: Boolean, private val syncToClient: Boolean, private val store: Boolean) {
    TICK(true, true, true),
    GUI(true, false, true),
    SERVER_ONLY(false, false, true),
    GUI_ONLY(true, false, false),
    TICK_ONLY(false, true, false);

    fun shouldSync(hasGui: Boolean, isSyncing: Boolean, isSaving: Boolean) =
        (this.syncWhenGuiOpened && hasGui) || (this.syncToClient && isSyncing) || (this.store && isSaving)
}
