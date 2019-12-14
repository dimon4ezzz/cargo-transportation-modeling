package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Order

interface OrderListener {
    fun onCreate(order: Order)
}