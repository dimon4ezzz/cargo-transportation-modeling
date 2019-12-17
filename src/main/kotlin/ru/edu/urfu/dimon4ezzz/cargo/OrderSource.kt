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
        var point = InformationHolder.getRandomPoint()

        while (point == currentPoint) {
            point = InformationHolder.getRandomPoint()
        }

        return point
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
            listeners.first().onPush(poll())
            // удалить первый в очереди листенер
            if (listeners.first().isLast())
                listeners.remove()
        }
    }
}
