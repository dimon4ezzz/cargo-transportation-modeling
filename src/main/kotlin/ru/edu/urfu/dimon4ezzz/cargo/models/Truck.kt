package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.listeners.OrderQueueListener
import ru.edu.urfu.dimon4ezzz.cargo.listeners.TruckTakingListener
import ru.edu.urfu.dimon4ezzz.cargo.tasks.TruckTakingTask
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

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
     * Стандартный листенер, смотрит за очередью заказов в пункте.
     */
    private val defaultOrderQueueListener = object :
        OrderQueueListener {
        override fun onPush(order: Order) {
            println("$name start taking ${order.name}")
            orders.add(order)

            val task = TruckTakingTask(
                this@Truck,
                order,
                defaultTruckTakingListener
            )
            tasks.add(task)
            Thread(task).start()
        }

        override fun isLast(): Boolean {
            return orders.count() == 6
        }
    }

    /**
     * Стандартный листенер, смотрит за своими задачами погрузки.
     */
    private val defaultTruckTakingListener = object :
        TruckTakingListener {
        override fun onComplete() {
            // удалить первую задачу из очереди,
            // так как она закончилась
            tasks.remove()
            // если больше задач нет
            if (tasks.count() == 0) {
                // и ещё может взять заказов
                // (см. isLast() в OrderQueueListener —
                // тот автоматически удаляет)
                if (orders.count() < 6) {
                    // удалить свой листенер
                    location.orderSource.removeOrderQueueListener()
                }
                // поехали
                state = TruckState.MOVING
            }
        }
    }

    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null

    private var orders = ConcurrentLinkedQueue<Order>()
    private var tasks = ArrayDeque<TruckTakingTask>()

    init {
        location.orderSource.addOrderQueueListener(defaultOrderQueueListener)
    }
}