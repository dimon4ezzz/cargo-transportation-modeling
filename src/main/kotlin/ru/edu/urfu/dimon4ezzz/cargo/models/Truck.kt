package ru.edu.urfu.dimon4ezzz.cargo.models

import org.jgrapht.GraphPath
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.GraphWalk
import ru.edu.urfu.dimon4ezzz.cargo.InformationHolder
import ru.edu.urfu.dimon4ezzz.cargo.listeners.OrderQueueListener
import ru.edu.urfu.dimon4ezzz.cargo.listeners.TruckTakingListener
import ru.edu.urfu.dimon4ezzz.cargo.tasks.TruckTakingTask
import java.util.*

/**
 * Время движения по одному ребру графа пунктов.
 *
 * 5 минут → 1 секунда, значит 1 час → 12 секунд.
 */
private const val movingTime: Long = 60 / 5 // 12s

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
     * Стандартный листенер, смотрит за очередью заказов в пункте.
     */
    private val defaultOrderQueueListener = object :
        OrderQueueListener {
        override fun onPush(order: Order): Boolean {
            if (shouldIGetThis(order)) {
                println("$name taking ${order.name}")
                orders++
                val task = TruckTakingTask(this@Truck, order, defaultTruckTakingListener)
                tasks.add(task)
                Thread(task).start()
                return true
            }

            return false
        }

        // по сути не нужен,
        // говорит больше не предлагать заказов
        // TODO возможно удалить, т. к. onPush возвращает,
        //  берёт он заказ или нет
        override fun isLast(): Boolean {
            return orders == 6
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
                if (orders < 6) {
                    // удалить свой листенер
                    location.orderSource.auction.removeOrderQueueListener()
                }
                // поехали
                state = TruckState.MOVING
                println("$name едет ${path()}")
            }
        }
    }

    /**
     * TODO дебаг функция
     */
    private fun path(): String {
        val joiner = StringJoiner("-")
        cachedPath!!.vertexList.forEach {
            joiner.add(it.name)
        }
        return joiner.toString()
    }

    /**
     * Говорит, стоит ли грузовику брать этот заказ.
     * Проверяет путь, по которому собирается этот грузовик проехать.
     */
    private fun shouldIGetThis(order: Order): Boolean {
        // если у грузовика уже есть путь,
        // посмотреть, по пути ли ему довезти этот заказ
        cachedPath?.let {
            if (it.vertexList.contains(order.destination)) {
                return true
            } else {
                // где-то нужно найти такой алгоритм,
                // который бы не считал путь из конца этого пути,
                // так как если точка находится, условно говоря, сзади,
                // на неё придётся потратить 2x текущего пути + ещё до точки доехать
                val shortestPathToOrderDestination = InformationHolder.getPath(location, order.destination)
                val shortestPathFromCachedPathEnd = InformationHolder.getPath(it.endVertex, order.destination)

                // если из конца текущего пути проехать дешевле
                if (shortestPathFromCachedPathEnd.edgeList.count() < shortestPathToOrderDestination.edgeList.count()) {
                    // нужно сложить два путя
                    val old = GraphWalk<Point, DefaultEdge>(InformationHolder.graph, it.startVertex, it.endVertex, it.vertexList, it.edgeList, it.weight)
                    val new = GraphWalk<Point, DefaultEdge>(InformationHolder.graph, shortestPathFromCachedPathEnd.startVertex, shortestPathFromCachedPathEnd.endVertex, shortestPathFromCachedPathEnd.vertexList, shortestPathFromCachedPathEnd.edgeList, shortestPathFromCachedPathEnd.weight)
                    cachedPath = old.concat(new) { old.weight + new.weight }

                    return true
                }

                // по-другому не поедет, так как
                // если из этой точки выехать дешевле,
                // а из конечной дороже, значит
                // точка доставки не на текущем пути (см. пред. if)
                // или доступна по совершенно другому пути
            }
        }?:let {
            // если грузовик ещё не решил до этого,
            // куда он поедет, он поедет на этот заказ
            cachedPath = InformationHolder.getPath(location, order.destination)
            return true
        }

        return false
    }

    private var cachedPath: GraphPath<Point, DefaultEdge>? = null

    private var orders = 0
    private var tasks = ArrayDeque<TruckTakingTask>()

    /**
     * Прикреплённый контейнеровоз.
     */
    var containerShip: ContainerShip? = null

    init {
        location.orderSource.auction.addOrderQueueListener(defaultOrderQueueListener)
    }
}