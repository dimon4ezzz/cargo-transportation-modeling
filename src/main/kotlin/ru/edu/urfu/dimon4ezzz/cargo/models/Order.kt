package ru.edu.urfu.dimon4ezzz.cargo.models

/**
 * Заказ.
 */
data class Order (
    /**
     * Название заказа.
     */
    val name: String,

    /**
     * Пункт доставки.
     */
    val destination: Point
)