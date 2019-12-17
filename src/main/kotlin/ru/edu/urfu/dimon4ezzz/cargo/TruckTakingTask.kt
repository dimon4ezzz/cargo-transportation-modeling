package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Container
import ru.edu.urfu.dimon4ezzz.cargo.models.ContainerShip
import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Truck
import java.lang.IllegalStateException

class TruckTakingTask(
    private val truck: Truck,
    private val order: Order
) : Runnable {
    /**
     * Время забирания заказа из точки.
     *
     * 5 минут → 1 секунда, значит 30 минут → 6 секунд.
     */
    private val takingTime: Long = (30 / 5) * 1000 // 6s

    override fun run() {
        truck.containerShip?.let {
            if (!it.add(order)) {
                throw IllegalStateException("cannot add order into container ship")
            }
        }?:let {
            truck.containerShip = buildContainerShip(order)
        }

        Thread.sleep(takingTime)
        println("${truck.name} took ${order.name}")
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
