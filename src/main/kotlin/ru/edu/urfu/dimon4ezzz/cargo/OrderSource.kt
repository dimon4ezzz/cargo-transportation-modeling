package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import kotlin.random.Random
import kotlin.system.exitProcess

/**
 * Источник заказов.
 */
class OrderSource(
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
    private fun generateOrder(): Order {
        val destination = getRandomPoint()
        val path = InformationHolder.getPath(currentPoint, destination)

        return Order(
            name = "${currentPoint.name}-${ordersAmount++}-${destination.name}",
            destination = destination,
            path = path
        )
    }

    /**
     * Генерирует случайный пункт, за исключением текущего.
     * TODO перенести в InformationHolder
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
