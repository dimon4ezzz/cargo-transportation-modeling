package ru.edu.urfu.dimon4ezzz.cargo.models

import ru.edu.urfu.dimon4ezzz.cargo.InformationHolder
import ru.edu.urfu.dimon4ezzz.cargo.Router
import ru.edu.urfu.dimon4ezzz.cargo.listeners.OrderQueueListener
import ru.edu.urfu.dimon4ezzz.cargo.listeners.TruckActionListener
import ru.edu.urfu.dimon4ezzz.cargo.tasks.TruckAction
import ru.edu.urfu.dimon4ezzz.cargo.tasks.TruckTakingTask
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.roundToLong

/**
 * Время движения по одному ребру графа пунктов.
 *
 * 5 минут → 1 секунда, значит 1 час → 12 секунд.
 */
private const val MOVING_TIME: Long = (60 / 5) * 1000 // 12s

/**
 * Время разгрузки; разгрузка параллельна.
 */
private const val GIVING_TIME: Long = (30 / 5) * 1000 // 6s

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
        /**
         * На событие получения нового заказа
         * добавляет его себе в путевой лист,
         * если путевой лист согласится.
         *
         * @return `true`, если согласен забрать этот заказ.
         * @see Router.addOrder
         */
        override fun onPush(order: Order): Boolean {
            // Функция addOrder также проверяет,
            // подходит ли этот заказ этому грузовику.
            if (router.addOrder(order)) {
                // пишет в консоль
                println("$name взял ${order.name}")
                state = TruckState.TAKING
                // создаёт задачу погрузки, и добавляет ей свой листенер
                val task = TruckTakingTask(defaultTruckActionListener)
                // добавляет в список незавершённых задач
                tasks.add(task)
                // запускает задачу
                Thread(task).start()
                return true
            }

            return false
        }
    }

    /**
     * Стандартный листенер, смотрит за своими задачами погрузки.
     */
    private val defaultTruckActionListener = object :
        TruckActionListener {
        override fun onComplete() {
            // удалить первую задачу из очереди,
            // так как она закончилась
            tasks.remove()
            // если больше задач нет
            // или грузовик полон заказами
            if (tasks.isEmpty() || router.isFull()) {
                // удалить свой листенер
                // если листенер не найдётся,
                // ничего страшного, пропустит
                location.orderSource
                    .auction.removeOrderQueueListener(listenerId)
                // поехали, если уже не едем
                // см. проблемы с многопоточностью
                if (state != TruckState.MOVING)
                    move()
            }
        }
    }

    /**
     * Роутер смотрит за заказами, которые закреплены за грузовиком.
     */
    private val router: Router = Router(this)

    /**
     * Очередь заданий на погрузку.
     */
    private val tasks = ConcurrentLinkedQueue<TruckAction>()

    /**
     * ID листенера на аукционе.
     *
     * Показывает, какой листенер
     * в списке листенеров относится к этому грузовику.
     */
    private var listenerId: Long = 0

    /**
     * Дефолтное действие на конструктор.
     */
    init {
        setListenerToLocationSource()
    }

    /**
     * Задаёт в текущем местоположении свой листенер заказов,
     * чтобы этому грузовику показывали список заказов.
     */
    private fun setListenerToLocationSource() {
        listenerId = location.orderSource
            .auction.addOrderQueueListener(defaultOrderQueueListener)
    }

    /**
     * Двигается ко следующему пункту.
     */
    private fun move() {
        // двигается к первому по пути месту назначения
        state = TruckState.MOVING
        // берёт следующую точку
        val point = router.getNextPointAndRecalculate()
        if (point == null) {
            state = TruckState.SLEEPING
            return
        }
        // узнаёт, сколько до неё ехать
        val pathWeight = InformationHolder.getPath(location, point).weight
        // умножает на время одного часа
        val pathTime = (pathWeight * MOVING_TIME).roundToLong()
        // ждёт, пока «поездка не закончится»
        Thread.sleep(pathTime)
        // записывает себя в эту точку
        location = point
        println("$name приехал в ${location.name}")
        // выгружает
        state = TruckState.GIVING
        Thread.sleep(GIVING_TIME)

        // удяляет из своего роутера заказы, которые привёз
        router.finishOrder()

        // если заказы есть, едем дальше
        if (router.isFull() && state != TruckState.MOVING) {
            move()
        } else {
            state = TruckState.SLEEPING
            // задаёт свой листенер в эту точку
            setListenerToLocationSource()
        }
    }
}