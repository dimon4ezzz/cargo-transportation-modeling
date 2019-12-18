package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.listeners.OrderQueueListener
import ru.edu.urfu.dimon4ezzz.cargo.models.Order
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Аукцион заказов для пункта.
 */
class Auction {
    private val orders = ConcurrentLinkedQueue<Order>()
    private val listeners = ArrayList<OrderQueueListener>()

    fun addOrder(order: Order) {
        println("появился заказ ${order.name}-${order.destination.name}")
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
    fun addOrderQueueListener(listener: OrderQueueListener) {
        if (!checkOrdersAndAskItLast(listener)) {
            listeners.add(listener)
        }
    }

    /**
     * Удаляет из списка слушателей первого.
     *
     * Проверяет, нужны ли заказы следующим по очереди слушателям,
     * так как до них не дошли события `onPush`.
     */
    fun removeOrderQueueListener() {
        listeners.removeAt(0)
        orders.forEach {
            doesAnybodyTakeThis(it)
        }
    }

    /**
     * Проверяет, возьмёт ли кто-то этот заказ.
     */
    private fun doesAnybodyTakeThis(order: Order): Boolean {
        // пройдётся по всем листенерам
        listeners.forEach {
            // если кто-то взялся
            if (it.onPush(order)) {
                // если для него — это последний,
                // удалить его из списка слушателей
                if (it.isLast()) {
                    listeners.remove(it)
                }

                return true
            }
        }

        return false
    }

    /**
     * Для этого листенера смотрит все заказы.
     * Если какой-то заказ ему подходит, и этот заказ для него последний,
     * то передаёт `true`, иначе его можно добавить с список слушателей.
     */
    private fun checkOrdersAndAskItLast(listener: OrderQueueListener): Boolean {
        // подходит ли заказ и удалить, если подходит
        val took = orders.removeIf {
            listener.onPush(it)
        }

        return took && listener.isLast()
    }
}