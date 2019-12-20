package ru.edu.urfu.dimon4ezzz.cargo.comparators

import ru.edu.urfu.dimon4ezzz.cargo.InformationHolder
import ru.edu.urfu.dimon4ezzz.cargo.models.Point

/**
 * Сравнивает дальность до точек от текущего местоположения.
 *
 * Учитывает и веса рёбер.
 */
class PointComparator(
    /**
     * Текущее местоположение.
     */
    private val location: Point
) : Comparator<Point> {
    /**
     * Если первая точка дальше второй → +
     * Если первая точка на таком же расстоянии, как и до второй → 0
     * Если первая точка ближе второй → −
     */
    override fun compare(o1: Point?, o2: Point?): Int {
        return InformationHolder.getPath(location, o1!!).weight
            .compareTo(InformationHolder.getPath(location, o2!!).weight)
    }
}