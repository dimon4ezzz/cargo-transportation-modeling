package ru.edu.urfu.dimon4ezzz.cargo

import ru.edu.urfu.dimon4ezzz.cargo.models.*
import java.util.Timer
import java.util.stream.IntStream
import kotlin.streams.toList

private const val POINTS_AMOUNT = 20
private const val TRUCKS_AMOUNT = 10

/**
 * Если 5 минут модели = 1 секунде реального времени,
 * то время моделирования 12 часов = 144 секунды (2m24s)
 */
private const val DURATION: Long = 12 * 60 / 5

private lateinit var points: List<Point>
private lateinit var trucks: List<Truck>
private lateinit var modeller: Modeller
private lateinit var timer: Timer

fun main() {
    println("Генерируем пункты")
    points = generatePoints()

    println("Генерируем машины")
    trucks = generateTrucks()

    println("Создаём источники заказов")
    generateOrderSources()

    println("Грузовики слушают заказы")
    setListeners()

    println("Создаём таймер")
    timer = Timer()

    println("Создаём генератор заказов")
    modeller = Modeller(points)

    println("Запускаем генерацию заказов")
    timer.schedule(
        modeller,
        DURATION
    )
}

/**
 * Генерирует пункт заказов.
 *
 * Пока что все названия — номер по порядку.
 */
private fun generatePoints(amount: Int = POINTS_AMOUNT) = IntStream.range(0, amount)
    .mapToObj<Point> {
        Point(
            name = it.toString()
        )
    }.toList()

/**
 * Предоставляет случайный пункт.
 */
private fun getRandomPoint() = points.random()

/**
 * Генерирует машины в случайных местах.
 *
 * Пока что все названия — номер по порядку.
 */
private fun generateTrucks(amount: Int = TRUCKS_AMOUNT) = IntStream.range(0, amount)
    .mapToObj<Truck> {
        Truck(
            name = it.toString(),
            location = getRandomPoint(),
            state = TruckState.SLEEPING
        )
    }.toList()

/**
 * Создаёт источники заказов в каждом пункте.
 */
private fun generateOrderSources() = points.forEach {
    it.orderSource = OrderSource(
        points = points,
        currentPoint = it
    )
}

/**
 * Задаёт действия грузовиков на новые заказы.
 */
fun setListeners() = trucks.forEach {
    it.location.orderSource?.addOrderListener(object : OrderListener {
        override fun onCreate(order: Order) {
//            it.state = TruckState.TAKING
            // order должен класться в контейнер и т.д.
            println("${it.name} took order ${order.name}")
        }
    })
}