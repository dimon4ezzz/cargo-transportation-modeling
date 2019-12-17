package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.util.*

/**
 * Специальный класс, который запускает
 * задачи для таймера каждые 5 минут (1s).
 */
class Modeller (
    private val points: List<Point>
) : TimerTask() {
    override fun run() {
        Thread(getRandomOrderSource()).start()
    }

    private fun getRandomOrderSource() = points.random().orderSource
}