package ru.edu.urfu.dimon4ezzz.cargo.tasks

import ru.edu.urfu.dimon4ezzz.cargo.listeners.TruckActionListener

/**
 * Время забирания заказа из точки.
 *
 * 5 минут → 1 секунда, значит 30 минут → 6 секунд.
 */
private const val TAKING_TIME: Long = (30 / 5) * 1000 // 6s

class TruckTakingTask(
    private val listener: TruckActionListener
) : TruckAction {
    override fun run() {
        Thread.sleep(TAKING_TIME)
        listener.onComplete()
    }
}
