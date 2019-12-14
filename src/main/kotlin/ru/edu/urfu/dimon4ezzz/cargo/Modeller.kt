package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.util.*

/**
 * Специальная задача для таймера,
 * которая запускает раз в 5 минут (1s)
 * генерацию нового заказа
 */
class Modeller (
    val points: List<Point>
) : TimerTask() {
    private val ORDER_DELAY = 1_000L // ms

    private val timer = Timer()

    override fun run() {
        timer.schedule(
            getRandomOrderSource(),
            ORDER_DELAY
        )
    }

    private fun getRandomOrderSource() = points.random().orderSource
}