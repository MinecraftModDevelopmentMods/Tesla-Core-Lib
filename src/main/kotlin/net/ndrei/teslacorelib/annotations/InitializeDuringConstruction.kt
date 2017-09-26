package net.ndrei.teslacorelib.annotations

@Target(AnnotationTarget.CLASS)
annotation class InitializeDuringConstruction()

object InitializeDuringConstructionHandler: BaseAnnotationHandler<Any>({ _, _, _ ->
    // nothing, just wanted the class/object be "touched"
}, InitializeDuringConstruction::class)
