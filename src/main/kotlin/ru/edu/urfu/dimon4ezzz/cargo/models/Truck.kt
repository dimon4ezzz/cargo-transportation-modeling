package ru.edu.urfu.dimon4ezzz.cargo.models

/**
 * Грузовик.
 */
data class Truck (
    /**
     * Название грузовика.
     */
    val name: String,

    /**
     * Местоположение грузовика.
     */
    var location: Point
) {
    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null
}