package ru.edu.urfu.dimon4ezzz.cargo.tasks

import ru.edu.urfu.dimon4ezzz.cargo.listeners.TruckActionListener

/**
 * Время разгрузки; разгрузка параллельна.
 */
private const val GIVING_TIME: Long = (30 / 5) * 1000 // 6s

class TruckGivingTask(
    private val listener: TruckActionListener
) : TruckAction {
    override fun run() {
        Thread.sleep(GIVING_TIME)
        listener.onComplete()
    }
}