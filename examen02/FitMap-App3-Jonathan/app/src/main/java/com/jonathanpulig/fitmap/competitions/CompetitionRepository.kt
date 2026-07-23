package com.jonathanpulig.fitmap.competitions

object CompetitionRepository {
    fun around(center: GeoCoordinate): List<Competition> {
        fun point(latOffset: Double, lngOffset: Double) = GeoCoordinate(
            center.latitude + latOffset,
            center.longitude + lngOffset
        )

        val route5k = listOf(
            point(0.0000, 0.0000), point(0.0060, -0.0020), point(0.0090, 0.0040),
            point(0.0050, 0.0090), point(-0.0020, 0.0070), point(-0.0040, 0.0010), point(0.0000, 0.0000)
        )
        val calisthenicsRoute = listOf(
            point(0.0000, 0.0000), point(0.0035, 0.0030), point(0.0010, 0.0070),
            point(-0.0030, 0.0040), point(-0.0020, -0.0010), point(0.0000, 0.0000)
        )
        val strengthRoute = listOf(
            point(0.0000, 0.0000), point(0.0020, -0.0035), point(-0.0015, -0.0060),
            point(-0.0040, -0.0020), point(-0.0020, 0.0020), point(0.0000, 0.0000)
        )

        return listOf(
            Competition(
                id = "quito-5k",
                name = "Carrera FitMap 5K",
                category = "Running urbano",
                distanceKm = 5.0,
                schedule = "Sábado · 07:00",
                difficulty = Difficulty.EASY,
                route = route5k,
                checkpoints = checkpoints(route5k, "Hidratación", "Control 2", "Meta")
            ),
            Competition(
                id = "calistenia-urbana",
                name = "Circuito Calistenia Urbana",
                category = "Calistenia",
                distanceKm = 3.2,
                schedule = "Sábado · 10:00",
                difficulty = Difficulty.MODERATE,
                route = calisthenicsRoute,
                checkpoints = checkpoints(calisthenicsRoute, "Barras", "Resistencia", "Meta")
            ),
            Competition(
                id = "reto-fuerza",
                name = "Reto de Fuerza FitMap",
                category = "Fuerza funcional",
                distanceKm = 2.0,
                schedule = "Domingo · 09:00",
                difficulty = Difficulty.HARD,
                route = strengthRoute,
                checkpoints = checkpoints(strengthRoute, "Carga", "Potencia", "Meta")
            )
        )
    }

    private fun checkpoints(route: List<GeoCoordinate>, first: String, second: String, third: String): List<Checkpoint> {
        val firstIndex = (route.size / 3).coerceAtLeast(1)
        val secondIndex = (route.size * 2 / 3).coerceAtMost(route.lastIndex - 1)
        return listOf(
            Checkpoint(first, route[firstIndex]),
            Checkpoint(second, route[secondIndex]),
            Checkpoint(third, route[route.lastIndex])
        )
    }
}
