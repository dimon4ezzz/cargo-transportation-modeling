package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Order

interface OrderQueueListener {
    fun onPush(order: Order)
    fun isLast(): Boolean
}