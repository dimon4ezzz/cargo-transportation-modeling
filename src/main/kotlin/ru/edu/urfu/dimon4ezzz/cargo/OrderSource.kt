package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.lang.IllegalStateException
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
     * Итерационная переменная для заказов.
     */
    private var ordersAmount = 0L

    /**
     * Аукцион заказов
     */
    val auction = Auction()

    /**
     * При работе этой задачи просто создаётся новый заказ и кладётся в очередь.
     */
    override fun run() {
        auction.addOrder(generateOrder())

        // если рандом решит, что нужно два,
        // посылает ещё один заказ в очередь
        if (Random.nextInt(2) == 1)
            auction.addOrder(generateOrder())
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
}
