import ru.edu.urfu.dimon4ezzz.cargo.InformationHolder
import ru.edu.urfu.dimon4ezzz.cargo.Modeller
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
private const val DURATION: Long = (12 * 60 / 5) * 1000

private lateinit var trucks: List<Truck>
private lateinit var timer: Timer

fun main() {
    println("Генерируем пункты")
    InformationHolder.points = generatePoints()

    println("Генерируем машины")
    trucks = generateTrucks()
    trucks.forEach {
        println("${it.name} находится в ${it.location.name}")
    }

    println("Создаём таймер")
    timer = Timer()

    println("Запускаем генерацию заказов")
    timer.scheduleAtFixedRate(
        Modeller(),
        1000,
        5000
    )

    // через 2 мин 24 сек «выключить» потоки
    Thread.sleep(DURATION)
    timer.cancel()
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
 * Генерирует машины в случайных местах.
 *
 * Пока что все названия — номер по порядку.
 */
private fun generateTrucks(amount: Int = TRUCKS_AMOUNT) = IntStream.range(0, amount)
    .mapToObj<Truck> {
        Truck(
            name = it.toString(),
            location = InformationHolder.getRandomPoint(),
            state = TruckState.SLEEPING
        )
    }.toList()