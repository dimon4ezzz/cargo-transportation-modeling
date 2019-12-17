package ru.edu.urfu.dimon4ezzz.cargo.models

import java.lang.IllegalStateException

/**
 * Контейнеровоз, который прицепляется к грузовику.
 */
data class ContainerShip (
    /**
     * Название контейнеровоза.
     */
    val name: String,

    /**
     * Список контейнеров на контейнеровозе.
     */
    val containers: ArrayList<Container>
) {
    private val capacity = 3

    /**
     * @throws IllegalStateException когда не может добавить заказ
     */
    fun add(order: Order): Boolean {
        containers.forEach {
            if (it.add(order)) {
                return true
            }
        }

        if (containers.count() < capacity) {
            containers.add(buildContainer(order))
            return true
        }

        return false
    }

    private fun buildContainer(order: Order) = Container(
        name = "$name-0",
        orders = arrayListOf(order)
    )
}