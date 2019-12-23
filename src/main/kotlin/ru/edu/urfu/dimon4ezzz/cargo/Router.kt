package ru.edu.urfu.dimon4ezzz.cargo

import org.jgrapht.GraphPath
import org.jgrapht.Graphs
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import ru.edu.urfu.dimon4ezzz.cargo.comparators.PointComparator
import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import ru.edu.urfu.dimon4ezzz.cargo.models.Truck
import java.lang.Integer.min
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

private const val MAX_PATH_LENGTH = 3

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
    private var path: GraphPath<Point, DefaultEdge> = GraphWalk.singletonWalk(InformationHolder.graph, truck.location)

    /**
     * Заказы, которые себе взял грузовик.
     *
     * Сделан отдельный лист, потому что по нему легче искать.
     */
    private var orders = CopyOnWriteArrayList<Order>()

    fun addOrder(order: Order): Boolean {
        if (orders.count() > 5) return false

        // если путь грузовика ещё не задан
        // то есть в пути только одна вершина
        if (path.length == 0) {
            initPath(order)
            return true
        }
        // если путь грузовика включает в себя точку назначения
        else if (path.vertexList.contains(order.destination)) {
            // добавляем в очередь точку назначения
            pointsQueue.add(order.destination)
            // добавляем заказ в число заказов
            orders.add(order)
            return true
        }
        // если путь грузовика меньше трёх вершин
        // 2 + текущая (1) = 3
        else if (path.weight < MAX_PATH_LENGTH) {
            // если такой путь нашёлся
            try {
                getMatchPath(order.path)?.let {
                    // задать свой путь им
                    path = it
                    // остаток пути записать в заказ
                    order.path = deletePoint(order.path, it.endVertex)
                    order.name += "-t${it.endVertex.name}"
                    return true
                } ?: let {
                    return false
                }
            } catch (e: IndexOutOfBoundsException) {
                error("${truck.name} не справился с вычислением заказа ${order.name}\nошибка: ${e.message}")
            }
        }
        // если путь заказа меньше, чем 3, и он не прошёл проверки выше,
        //  значит, он находится по другую сторону пути
        else if (order.path.weight < MAX_PATH_LENGTH) {
            return false
        }
        else {
            // если существует точка передачи
            //  т.е. путь заказа и грузовика совпадает
            getTransferPoint(order.path)?.let {
                // задать в самом заказе путь как остаток пути
                // от передаточной точки до точки назначения
                order.path = deletePoint(order.path, it)
                order.name += "-t${it.name}"
                // добавляем его в очередь
                // TODO если в текущем пути < 3, добавить как дальнюю точку
                // добавляем в очередь точку назначения
                pointsQueue.add(it)
                // добавляем заказ в число заказов
                orders.add(order)
                return true
            }?:let {
                return false
            }
        }
    }

    fun isEmpty(): Boolean =
        orders.isEmpty()

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
        val point = pointsQueue.poll()
        // удалить из path всё до этой точки
        path = deletePoint(path, point) as GraphWalk<Point, DefaultEdge>
        return point
    }

    /**
     * Удаляет заказы, которые нужны были в этом пункте.
     */
    fun finishOrder() {
        orders.removeIf {
            it.destination == truck.location
        }

        // заказы, которые он сюда привёз кидает на аукцион
        orders.forEach {
            if (it.path.startVertex == truck.location) {
                truck.location
                    .orderSource
                    .auction
                    .addOrder(it)

                orders.remove(it)
            }
        }
    }

    /**
     * Инициирует путь в соответствии с требованиями к длине пути.
     *
     * Если путь длинее трёх пунктов, обрезает путь заказа и записывает остаток в сам заказ.
     */
    private fun initPath(order: Order) {
        // определяем, далеко ли пункт назначения заказа
        val isOrderFar = order.path.weight > MAX_PATH_LENGTH

        // если пункт назначения слишком далеко,
        //  берём только четвёртую вершину
        val endVertex = if (isOrderFar) {
            order.path.vertexList[MAX_PATH_LENGTH]
        } else {
            order.path.endVertex
        }

        // если пункт назначения слишком далеко,
        //  берём только путь до точки передачи
        path = if (isOrderFar) {
            // {1} привязка к остовному дереву
            InformationHolder.getPath(truck.location, endVertex)
        } else {
            order.path
        }

        // если пункт назначения слишком далеко,
        //  обрезаем путь заказа до точки передачи,
        //  чтобы следующий грузовик поехал уже
        //  по пути от точки передачи
        if (isOrderFar)
            order.path = deletePoint(order.path, endVertex)

        pointsQueue.add(endVertex)
        orders.add(order)

    }

    /**
     * Соединяет два пути воедино, создавая из них GraphWalk.
     *
     * @see Router.addOrder в нём есть вариант,
     *  когда грузовик наберёт себе заказов на всю карту
     *  и будет возить, всё время продлевая свой путь,
     *  всё время проезжая по одним и тем же дорогам
     */
    private fun concat(
        path1: GraphPath<Point, DefaultEdge>,
        path2: GraphPath<Point, DefaultEdge>
    ): GraphWalk<Point, DefaultEdge> {
        val walk1 = path1 as GraphWalk<Point, DefaultEdge> // cast
        val walk2 = path2 as GraphWalk<Point, DefaultEdge> // cast
        return walk1.concat(walk2) { walk1.weight + walk2.weight }
    }

    private fun deletePoint(
        path: GraphPath<Point, DefaultEdge>,
        point: Point
    ): GraphPath<Point, DefaultEdge> {
        // если удалять конечную точку,
        // возвращать пустой путь
        if (path.endVertex == point)
            return GraphWalk.singletonWalk(InformationHolder.graph, point)

        val vertexList: CopyOnWriteArrayList<Point> = CopyOnWriteArrayList(path.vertexList)
        for (v in vertexList) {
            if (v == point) {
                break
            }

            vertexList.remove(v)
        }

        // если останется всего лишь одно ребро,
        //  в этом списке будет лишь одна вершина,
        //  а значит, нужно отдавать путь из одного ребра
        if (vertexList.count() == 1) {
            val replaced = vertexList[0]
            vertexList[0] = Graphs.getOppositeVertex(InformationHolder.graph, path.edgeList.last(), replaced)
            vertexList.add(replaced)
        }

        // TODO вес рёбер нужно учитывать
        val weight = vertexList.count().toDouble()

        return GraphWalk<Point, DefaultEdge>(
            InformationHolder.graph,
            vertexList as List<Point>,
            weight
        )
    }

    /**
     * Ищет путь, который бы включал в себя текущий путь.
     */
    private fun getMatchPath(orderPath: GraphPath<Point, DefaultEdge>): GraphPath<Point, DefaultEdge>? {
        val currentLength = path.edgeList.count()
        var endVertex = path.startVertex

        // проходим по первым рёбрам
        for (i in 0 until currentLength) {
            endVertex = if (path.edgeList[i] == orderPath.edgeList[i]) {
                if (i == currentLength - 1) {
                    orderPath.vertexList[min(orderPath.length, MAX_PATH_LENGTH + 1)]
                } else {
                    orderPath.vertexList[i + 1]
                }
            } // если совпадений ни разу не было
            else if (i == 0) {
                return null
            } else {
                break
            }
        }

        return InformationHolder.getPath(path.startVertex, endVertex)
    }

    /**
     * Ищет точку передачи заказа в других пунктах.
     */
    private fun getTransferPoint(orderPath: GraphPath<Point, DefaultEdge>): Point? {
        // изначально считаем, что передаточных точек нет
        var lastMatch: Point? = null

        // грузовик не поедет дальше третьего пункта
        for (i in 1 until MAX_PATH_LENGTH) {
            if (path.vertexList[i] == orderPath.vertexList[i])
                lastMatch = path.vertexList[i]
        }

        return lastMatch
    }
}