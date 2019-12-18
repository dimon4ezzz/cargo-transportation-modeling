package ru.edu.urfu.dimon4ezzz.cargo.tasks

import ru.edu.urfu.dimon4ezzz.cargo.InformationHolder
import java.util.*

/**
 * Специальный класс, который запускает
 * задачи для таймера каждые 5 минут (1s).
 */
class Modeller : TimerTask() {
    override fun run() {
        Thread(getRandomOrderSource()).start()
    }

    private fun getRandomOrderSource() = InformationHolder.getRandomPoint().orderSource
}