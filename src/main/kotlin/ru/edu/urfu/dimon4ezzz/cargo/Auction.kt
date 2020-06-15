package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.listeners.OrderQueueListener
import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Аукцион заказов для пункта.
 */
class Auction {
    /**
     * Очередь для заказов, которые никто не взял.
     */
    private val orders = ConcurrentLinkedQueue<Order>()

    /**
     * Map из листенеров, которые слушают новые заказы.
     */
    private val listeners = ConcurrentHashMap<Long, OrderQueueListener>()

    /**
     * Итератор для листенеров.
     */
    private var listenersAmount = 0L

    /**
     * Новый заказ появляется.
     */
    fun addOrder(order: Order) {
        println("появился заказ ${order.name}")
        // если кто-то берётся за заказ, он его забирает
        // иначе (условие внутри if) — кладётся в очередь
        if (!doesAnybodyTakeThis(order)) {
            orders.add(order)
        }
    }

    /**
     * Добавляет слушателя в очередь.
     * Сразу же проверяет, нужен ли ему какой-либо заказ
     * из уже имеющихся. Если не нужен, или
     * этот заказ для него не последний,
     * то добавляет его в список слушателей.
     */
    fun addOrderQueueListener(listener: OrderQueueListener): Long {
        checkOrdersAndAskItLast(listener)
        listeners[listenersAmount] = listener

        // пофик на самом деле,
        // сколько итератор просуммировался,
        // в аукционе итератор по сути
        // уникальное число, по которому можно
        // идентифицировать листенер
        return listenersAmount++
    }

    /**
     * Удаляет из списка слушателей первого.
     *
     * Проверяет, нужны ли заказы следующим по очереди слушателям,
     * так как до них не дошли события `onPush`.
     */
    fun removeOrderQueueListener(key: Long) {
        // если такого листенера нет, просто вернёт null,
        // а его тут никто не проверит
        listeners.remove(key)
        // опросить остальных, нужны ли им заказы
        orders.forEach {
            doesAnybodyTakeThis(it)
        }
    }

    /**
     * Проверяет, возьмёт ли кто-то этот заказ.
     */
    private fun doesAnybodyTakeThis(order: Order): Boolean {
        // пройдётся по всем листенерам
        listeners.forEach { (_, value) ->
            // если кто-то взялся
            if (value.onPush(order))
                return true

            Thread.sleep(100)
        }

        return false
    }

    /**
     * Для этого листенера смотрит все заказы.
     * Если какой-то заказ ему подходит, и этот заказ для него последний,
     * то передаёт `true`, иначе его можно добавить с список слушателей.
     */
    private fun checkOrdersAndAskItLast(listener: OrderQueueListener) = orders.removeIf {
        Thread.sleep(100)
        listener.onPush(it)
    }
}