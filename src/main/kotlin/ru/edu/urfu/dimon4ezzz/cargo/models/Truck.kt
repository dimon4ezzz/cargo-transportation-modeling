package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.OrderQueueListener
import ru.edu.urfu.dimon4ezzz.cargo.TruckTakingTask
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.system.exitProcess

/**
 * Грузовик.
 */
data class Truck(
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
     * Время движения по одному ребру графа пунктов.
     *
     * 5 минут → 1 секунда, значит 1 час → 12 секунд.
     */
    private val movingTime: Long = 60 / 5 // 12s

    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null

    private var orders = ConcurrentLinkedQueue<Order>()

    init {
        setOrderSourceListener()
    }

    private fun setOrderSourceListener() {
        location.orderSource.addOrderQueueListener(object : OrderQueueListener {
            override fun onPush(order: Order) {
                println("$name start taking ${order.name}")
                orders.add(order)

                try {
                    Thread(TruckTakingTask(this@Truck, order)).start()
                } catch (e: IllegalStateException) {
                    println(e.message)
                    exitProcess(1)
                }
            }

            override fun isLast(): Boolean {
                return orders.count() == 5
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