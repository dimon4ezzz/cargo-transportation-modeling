package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.lang.IllegalStateException
import java.util.TimerTask

/**
 * Источник заказов.
 */
class OrderSource (
    /**
     * Список всех пунктов в модели.
     */
    val points: List<Point>,

    /**
     * Пункт, из которого генерируются заказы.
     */
    val currentPoint: Point
) : TimerTask() {
    /**
     * Слушатель заказов.
     */
    private var listener: OrderListener? = null

    /**
     * Итерационная переменная для заказов.
     */
    private var ordersAmount = 0

    /**
     * При работе этой задачи просто создаётся новый заказ и отдаётся слушателю.
     *
     * @throws IllegalStateException когда слушатель не задан
     */
    override fun run() =
        listener
            ?.onCreate(generateOrder())
            ?:throw IllegalStateException("listener not found")

    /**
     * Добавление слушателя.
     */
    fun addOrderListener(listener: OrderListener) {
        this.listener = listener
    }

    /**
     * Удаление слушателя.
     *
     * Неизвестно, нужно ли будет; см. проблемы с памятью
     */
    fun removeOrderListener() {
        listener = null
    }

    /**
     * Генерирует один заказ.
     */
    private fun generateOrder() = Order(
        name = "${currentPoint.name}${ordersAmount++}",
        destination = getRandomPoint()
    )

    /**
     * Генерирует случайный пункт, за исключением текущего.
     */
    private fun getRandomPoint(): Point {
        var point = points.random()

        while (point == currentPoint) {
            point = points.random()
        }

        return point
    }
}