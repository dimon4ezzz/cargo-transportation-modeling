package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Point

/**
 * Хранитель данных.
 *
 * Является Singleton объектом.
 */
object InformationHolder {
    var points: List<Point>? = null

    /**
     * Передаёт случайную точку в модели.
     *
     * @throws IllegalStateException когда список пунктов не задан.
     */
    fun getRandomPoint(): Point {
        points
            ?.let { return it.random() }
            ?:throw IllegalStateException("list of points did not set")
    }
}