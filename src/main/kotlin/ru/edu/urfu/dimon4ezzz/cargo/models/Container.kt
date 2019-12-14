package ru.edu.urfu.dimon4ezzz.cargo.models

/**
 * Контейнер с заказами.
 */
data class Container (
    /**
     * Название контейнера.
     */
    val name: String,

    /**
     * Список заказов внутри.
     */
    val orders: List<Order>
)