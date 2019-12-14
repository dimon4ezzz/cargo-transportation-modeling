package ru.edu.urfu.dimon4ezzz.cargo.models

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
    val containers: List<Container>
)