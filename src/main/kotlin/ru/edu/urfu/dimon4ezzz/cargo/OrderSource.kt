package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.lang.IllegalStateException
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.random.Random

/**
 * Источник заказов.
 */
class OrderSource (
    /**
     * Пункт, из которого генерируются заказы.
     */
    private val currentPoint: Point
) : Runnable {
    /**
     * Очередь заказов в пункте.
     */
    private val orderQueue = ConcurrentLinkedQueue<Order>()

    /**
     * Слушатели заказов.
     */
    private var listeners = ConcurrentLinkedQueue<OrderQueueListener>()

    /**
     * Итерационная переменная для заказов.
     */
    private var ordersAmount = 0L

    /**
     * При работе этой задачи просто создаётся новый заказ и кладётся в очередь.
     */
    override fun run() {
        orderQueue.push(generateOrder())

        if (Random.nextInt(2) == 1)
            orderQueue.push(generateOrder())
    }

    /**
     * Добавление слушателя очереди.
     */
    fun addOrderQueueListener(listener: OrderQueueListener) {
        listeners.add(listener)
    }

    /**
     * Передаёт первый из очереди заказ.
     */
    fun getOrder() = orderQueue.pull()

    /**
     * Генерирует один заказ.
     */
    private fun generateOrder() = Order(
        name = "${currentPoint.name}-${ordersAmount++}",
        destination = getRandomPoint()
    )

    /**
     * Генерирует случайный пункт, за исключением текущего.
     *
     * @throws IllegalStateException когда не задан список пунктов
     */
    private fun getRandomPoint(): Point {
        InformationHolder.points?.let {
            var point = it.random()

            while (point == currentPoint) {
                point = it.random()
            }

            return point
        }?: throw IllegalStateException("there is no list of points")
    }

    /**
     * Добавляет заказ в очередь, оповещает первого в списке слушателя и удаляет его.
     *
     * Расширяет возможности ConcurrentLinkedQueue.
     * @see ConcurrentLinkedQueue
     */
    private fun ConcurrentLinkedQueue<Order>.push(order: Order) {
        println("Получен новый заказ ${order.name} в ${order.destination.name}")
        add(order)

        if (!listeners.isEmpty()) {
            // оповестить листенер
            listeners.first().onPush()
            // удалить первый в очереди листенер
            listeners.remove()
        }
    }

    /**
     * Получает первый из очереди заказ и удаляет его из очереди.
     *
     * Расширяет возможности ConcurrentLinkedQueue.
     * @see ConcurrentLinkedQueue
     */
    private fun ConcurrentLinkedQueue<Order>.pull(): Order {
        val order = first()
        remove()
        return order
    }
}
