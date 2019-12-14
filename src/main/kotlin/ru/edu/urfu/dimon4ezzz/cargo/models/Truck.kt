package ru.edu.urfu.dimon4ezzz.cargo.models

/**
 * Грузовик.
 */
data class Truck (
    /**
     * Название грузовика.
     */
    val name: String
) {
    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null
}