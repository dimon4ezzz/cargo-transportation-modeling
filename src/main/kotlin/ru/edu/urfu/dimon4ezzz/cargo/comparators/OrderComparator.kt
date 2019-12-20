package ru.edu.urfu.dimon4ezzz.cargo.comparators

import ru.edu.urfu.dimon4ezzz.cargo.models.Order

/**
 * Сравнивает заказы по длине пути.
 */
class OrderComparator : Comparator<Order> {
    /**
     * Если первый заказ дальше, чем второй                 → +
     * Если первый заказ и второй на одинаковом расстоянии  → 0
     * Если первый заказ ближе, чем второй                  → −
     */
    override fun compare(o1: Order?, o2: Order?): Int {
        return o1!!.path.edgeList.count() - o2!!.path.edgeList.count()
    }
}