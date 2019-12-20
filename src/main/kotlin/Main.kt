import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph
import ru.edu.urfu.dimon4ezzz.cargo.InformationHolder
import ru.edu.urfu.dimon4ezzz.cargo.models.Point
import ru.edu.urfu.dimon4ezzz.cargo.models.Truck
import ru.edu.urfu.dimon4ezzz.cargo.models.TruckState
import ru.edu.urfu.dimon4ezzz.cargo.tasks.Modeller
import java.util.*
import java.util.stream.IntStream
import kotlin.streams.toList
import kotlin.system.exitProcess

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

    println("Инициализируем граф")
    InformationHolder.graph = graphInit()

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
        1000
    )

    // через 2 мин 24 сек «выключить» потоки
    Thread.sleep(DURATION)
    timer.cancel()
    // TODO завершить все потоки рекурсивно
    exitProcess(0)
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

private fun graphInit(): SimpleGraph<Point, DefaultEdge> {
    val graph = SimpleGraph<Point, DefaultEdge>(DefaultEdge::class.java)

    InformationHolder.points?.let {points ->
        // добавляет все пункты
        points.forEach {point ->
            graph.addVertex(point)
        }

        graph.addEdge(points[0], points[3])
        graph.addEdge(points[3], points[4])

        graph.addEdge(points[3], points[2])
        graph.addEdge(points[2], points[1])

        graph.addEdge(points[0], points[8])
        graph.addEdge(points[8], points[9])
        graph.addEdge(points[9], points[5])

        graph.addEdge(points[0], points[13])
        graph.addEdge(points[13], points[19])
        graph.addEdge(points[19], points[14])
        graph.addEdge(points[19], points[18])

        // пока с остовным деревом посидим
        // потому что просчёты пока ведутся
        // не по рёбрам, а по кратчайшим путям
//        graph.addEdge(points[0], points[7])
        graph.addEdge(points[7], points[6])
        graph.addEdge(points[6], points[10])
        graph.addEdge(points[10], points[15])
        graph.addEdge(points[15], points[16])
        graph.addEdge(points[16], points[11])
        graph.addEdge(points[16], points[17])
        graph.addEdge(points[17], points[12])
        graph.addEdge(points[12], points[0])
    }

    return graph
}

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