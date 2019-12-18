package ru.edu.urfu.dimon4ezzz.cargo.listeners

import ru.edu.urfu.dimon4ezzz.cargo.models.Order

interface OrderQueueListener {
    fun onPush(order: Order): Boolean
    fun isLast(): Boolean
}