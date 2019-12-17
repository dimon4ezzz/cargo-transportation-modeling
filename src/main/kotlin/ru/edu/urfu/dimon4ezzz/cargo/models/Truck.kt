package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.OrderListener

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
    var location: Point,

    /**
     * Состояние грузовика.
     */
    var state: TruckState
) {
    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null

    fun setOrderListener() {
        location.orderSource?.setOrderListener(object : OrderListener {
            override fun onCreate(order: Order) {
//            it.state = TruckState.TAKING
                // TODO order должен класться в контейнер и т.д.
                println("$name took order ${order.name}")
            }
        })
    }
}