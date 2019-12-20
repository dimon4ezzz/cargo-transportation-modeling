package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import ru.edu.urfu.dimon4ezzz.cargo.comparators.PointComparator
import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import ru.edu.urfu.dimon4ezzz.cargo.models.Truck
import java.util.*
import kotlin.collections.ArrayList

/**
 * Маршрутчик грузовика. Управляет всеми точками, которые посетит этот грузовик.
 */
class Router(
    private val truck: Truck
) {
    /**
     * Стандартный компаратор пунктов:
     * кто ближе к местоположению грузовика, тот и ближе к началу очереди.
     *
     * FIXME не умеет учитывать порядок в пути,
     *  если следующая точка ближе
     *  скорее всего нужно остовное дерево
     */
    private var pointComparator = PointComparator(truck.location)

    /**
     * Очередь из точек, которые грузовик собирается посетить.
     */
    private var pointsQueue = PriorityQueue<Point>(pointComparator)

    /**
     * Текущий путь, по которому поедет грузовик.
     *
     * Изначально — пустой путь на заданном графе,
     * у которого задана лишь одна точка.
     *
     * FIXME перестроить путь по приезде в новый пункт
     */
    private var path = GraphWalk.singletonWalk(InformationHolder.graph, truck.location)

    /**
     * Заказы, которые себе взял грузовик.
     *
     * Сделан отдельный лист, потому что по нему легче искать.
     */
    private var orders = ArrayList<Order>()

    fun addOrder(order: Order): Boolean {
        // TODO проверить подходит ли
        // TODO сделать concat с точкой из order

        // если путь грузовика ещё не задан
        // то есть в пути только одна вершина
        if (path.length == 0) {
            initPath(order)
            return true
        }
        // если путь грузовика включает в себя точку назначения
        else if (path.vertexList.contains(order.destination)) {
            addToQueues(order)
            return true
        }
        // не стал делать else if, т. к. читаемость кода ухудшается,
        // потому что не понятно сходу, если оставить shortestPath… в качестве выражения
        else {
            // путь из конечной точки
            val shortestPathFromTheEnd = InformationHolder.getPath(path.endVertex, order.destination)

            // если доехать из конечной точки ближе,
            // чем из текущей, то…
            println("${truck.name} сравнивает ${shortestPathFromTheEnd.getStringForLog()} и ${order.path.getStringForLog()}")
            if (shortestPathFromTheEnd.weight <= order.path.weight) {
                // теперь путь для заказа —
                // это часть пути от точки передачи
                // до точки назначения
//                order.path = GraphWalkBuilder.subtract(order.path, path)
//                pointsQueue.add(order.path.startVertex)

                // == сложение путей ==
                //// добавляет к текущему пути частичку до точки назначения
                 path = GraphWalkBuilder.concat(path, shortestPathFromTheEnd)
                 pointsQueue.add(order.destination)

                // добавляем заказ в число заказов
                orders.add(order)
                return true
            }
        }

        // по-другому не поедет, так как
        // если из этой точки выехать дешевле,
        // а из конечной дороже, значит
        // точка доставки не на текущем пути (см. пред. if)
        // или доступна по совершенно другому пути
        println("${truck.name} против добавления ${order.name}")
        return false
    }

    /**
     * @return полна ли очередь заказов,
     *  которые везёт грузовик
     */
    fun isFull(): Boolean =
        orders.count() == 6

    /**
     * Форматирует путь как строку вида 1-2-3.
     */
    fun getPathAsString(): String =
        path.getStringForLog()

    /**
     * Передаёт следующую точку
     * и удаляет её из списка и из общего пути.
     */
    fun getNextPointAndRecalculate(): Point {
        println("${truck.name} посетит $pointsQueue")
        val point = pointsQueue.poll()
        println("осторожно, двери закрываются!")
        println("следующая остановка ${point.name} (${truck.name})")
        // удалить из path всё до этой точки
        path = GraphWalkBuilder.deletePoint(path, point) as GraphWalk<Point, DefaultEdge>
        return point
    }

    /**
     * Удаляет заказы, которые нужны были в этом пункте.
     */
    fun finishOrder() = orders.removeIf {
        it.destination == truck.location
    }

    private fun initPath(order: Order) {
        pointsQueue.add(order.destination)
        path = order.path as GraphWalk<Point, DefaultEdge>
        println("${truck.name} инициирует путь ${path.getStringForLog()}")
        orders.add(order)
    }

    private fun addToQueues(order: Order) {
        println("${truck.name} добавляет в очередь ${order.name}")
        // добавляем в очередь точку назначения
        pointsQueue.add(order.destination)
        println("${truck.name} уже едет по ${path.getStringForLog()}")
        // добавляем в общий путь следования весь путь до точки
//        path = GraphWalkBuilder.concat(path, order.path)
        // добавляем заказ в число заказов
        orders.add(order)
    }
}