package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.OrderQueueListener

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
     * Время забирания заказа из точки.
     *
     * 5 минут → 1 секунда, значит 30 минут → 6 секунд.
     */
    private val takingTime: Long = 30 / 5 // 6s

    /**
     * Время движения по одному ребру графа пунктов.
     *
     * 5 минут → 1 секунда, значит 1 час → 12 секунд.
     */
    private val movingTime: Long = 60 / 5 // 12s

    private var iter = 0L

    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null

    fun setOrderListener() {
        location.orderSource.addOrderQueueListener(object : OrderQueueListener {
            override fun onPush() {
                containerShip = ContainerShip(
                    name = "$name-${iter++}",
                    containers = listOf(
                        Container(
                            name = "$name-${iter++}",
                            orders = listOf(
                                location.orderSource.getOrder()
                            )
                        )
                    )
                )
                println("$name took ${containerShip!!.containers[0].orders[0].name}")
            }
        })
//        location.orderSource?.setOrderListener(object : OrderListener {
//            override fun onCreate(order: Order) {
//                println("$name delete listener from ${order.name}")
//                location.setDefaultOrderSourceListener()
//
//                println("$name took order ${order.name}")
//                state = TruckState.TAKING
//                Thread.sleep(takingTime)
//
//                println("$name going to move into ${order.destination.name}")
//                state = TruckState.MOVING
//                Thread.sleep(movingTime)
//
//                println("$name reached ${order.destination.name}")
//                state = TruckState.GIVING
//                Thread.sleep(takingTime)
//
//                println("$name is ready to taking")
//                state = TruckState.SLEEPING
//                // добавляем листенер в пункт
//            }
//        })
    }
}