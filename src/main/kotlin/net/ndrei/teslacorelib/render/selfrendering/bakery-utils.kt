package net.ndrei.teslacorelib.render.selfrendering

fun Iterable<IBakery>.cache(): CachedBakery {
    val list = this.toList()
    if (list.isEmpty()) {
        throw Exception("At least one bakery is required.")
    }

    return CachedBakery(if (list.isEmpty()) list[0] else CombinedBakery(*list.toTypedArray()))
            .also {
                it.keyGetter = { _, _, _ -> "ALL" }
            }
}

fun Iterable<IBakery>.combine(): CombinedBakery
    = CombinedBakery(this)

fun IBakery.addTo(list: MutableList<IBakery>) {
    list.add(this)
}

fun IBakery.static(): StaticBakery = StaticBakery(this)