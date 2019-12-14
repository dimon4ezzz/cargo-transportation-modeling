package ru.edu.urfu.dimon4ezzz.cargo.models

/**
 * Состояния грузовика.
 */
enum class TruckState {
    /**
     * Грузовик едет.
     */
    MOVING,

    /**
     * Грузовик берёт заказ.
     */
    TAKING,

    /**
     * Грузовик отгружает заказ.
     */
    GIVING,

    /**
     * Грузовик спит.
     */
    SLEEPING
}