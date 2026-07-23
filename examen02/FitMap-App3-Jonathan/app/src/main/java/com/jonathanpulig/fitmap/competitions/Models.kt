package com.jonathanpulig.fitmap.competitions

data class GeoCoordinate(val latitude: Double, val longitude: Double)

data class RecommendedStore(
    val id: String,
    val name: String,
    val address: String,
    val coordinate: GeoCoordinate,
    val distanceKm: Double
)

data class IncomingRecommendations(
    val discipline: String,
    val eventName: String?,
    val eventCoordinate: GeoCoordinate,
    val stores: List<RecommendedStore>
)

enum class Difficulty(val label: String, val color: Int) {
    EASY("Fácil", 0xFF2E7D32.toInt()),
    MODERATE("Media", 0xFFF57C00.toInt()),
    HARD("Alta", 0xFFC62828.toInt())
}

data class Checkpoint(
    val name: String,
    val coordinate: GeoCoordinate
)

data class Competition(
    val id: String,
    val name: String,
    val category: String,
    val distanceKm: Double,
    val schedule: String,
    val difficulty: Difficulty,
    val route: List<GeoCoordinate>,
    val checkpoints: List<Checkpoint>
)
