package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import java.util.*

/**
 * Специальная задача для таймера,
 * которая запускает раз в 5 минут (1s)
 * генерацию нового заказа
 */
class Modeller (
    private val points: List<Point>
) : TimerTask() {
    private val orderDelay = 1_000L // 1ms
    private val timer = Timer()

    override fun run() {
        timer.schedule(
            getRandomOrderSource(),
            orderDelay
        )
    }

    private fun getRandomOrderSource() = points.random().orderSource
}