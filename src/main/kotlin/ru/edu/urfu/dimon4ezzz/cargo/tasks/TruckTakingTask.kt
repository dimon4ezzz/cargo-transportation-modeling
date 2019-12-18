package ru.edu.urfu.dimon4ezzz.cargo.tasks

import ru.edu.urfu.dimon4ezzz.cargo.listeners.TruckTakingListener
import ru.edu.urfu.dimon4ezzz.cargo.models.*

class TruckTakingTask(
    private val truck: Truck,
    private val order: Order,
    private val listener: TruckTakingListener
) : Runnable {
    /**
     * Время забирания заказа из точки.
     *
     * 5 минут → 1 секунда, значит 30 минут → 6 секунд.
     */
    private val takingTime: Long = (30 / 5) * 1000 // 6s

    override fun run() {
        truck.state = TruckState.TAKING
        truck.containerShip?.add(order) ?:let {
            truck.containerShip = buildContainerShip(order)
        }

        Thread.sleep(takingTime)
        println("${truck.name} took ${order.name}")
        listener.onComplete()
    }

    private fun buildContainerShip(order: Order) = ContainerShip(
        name = "cs-${truck.name}",
        containers = arrayListOf(
            Container(
                name = "c-${truck.name}-${order.name}",
                orders = arrayListOf(order)
            )
        )
    )
}
