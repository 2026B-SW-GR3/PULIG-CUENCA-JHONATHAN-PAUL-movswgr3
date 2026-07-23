package com.jonathanpulig.fitmap.competitions

import android.content.Intent
import org.json.JSONArray
import org.json.JSONObject

object IntentContract {
    const val ACTION_RECEIVE = "com.fitmap.app3.RECEIVE_RECOMMENDATIONS"
    const val ACTION_RECEIVE_LEGACY = "com.fitmap.app3.ACTION_TIENDAS_RECOMENDADAS"

    private const val EXTRA_PAYLOAD = "fitmap.recommended_stores_json"
    private const val EXTRA_PAYLOAD_LEGACY = "tiendas_recomendadas"
    private const val EXTRA_STORES = "fitmap.stores"

    fun read(intent: Intent): IncomingRecommendations? {
        val fullPayload = firstString(intent, EXTRA_PAYLOAD, EXTRA_PAYLOAD_LEGACY)
        if (!fullPayload.isNullOrBlank()) {
            runCatching { parsePayload(JSONObject(fullPayload)) }.getOrNull()?.let { return it }
        }

        val storesPayload = intent.getStringExtra(EXTRA_STORES)
        if (!storesPayload.isNullOrBlank()) {
            val stores = runCatching { parseStores(JSONArray(storesPayload)) }.getOrNull().orEmpty()
            if (stores.isNotEmpty()) {
                val center = GeoCoordinate(
                    stores.map { it.coordinate.latitude }.average(),
                    stores.map { it.coordinate.longitude }.average()
                )
                return IncomingRecommendations("Entrenamiento", null, center, stores)
            }
        }
        return null
    }

    private fun parsePayload(root: JSONObject): IncomingRecommendations? {
        val stores = parseStores(root.optJSONArray("recommendedStores") ?: JSONArray())
        val event = root.optJSONObject("event") ?: return null
        val latitude = event.optDouble("latitude", Double.NaN)
        val longitude = event.optDouble("longitude", Double.NaN)
        if (!latitude.isFinite() || !longitude.isFinite()) return null
        return IncomingRecommendations(
            discipline = root.optString("discipline", "Entrenamiento"),
            eventName = event.optString("name").takeIf { it.isNotBlank() && it != "null" },
            eventCoordinate = GeoCoordinate(latitude, longitude),
            stores = stores
        )
    }

    private fun parseStores(array: JSONArray): List<RecommendedStore> = buildList {
        for (index in 0 until array.length()) {
            val item = array.optJSONObject(index) ?: continue
            val latitude = item.optDouble("latitude", Double.NaN)
            val longitude = item.optDouble("longitude", Double.NaN)
            if (!latitude.isFinite() || !longitude.isFinite()) continue
            add(
                RecommendedStore(
                    id = item.optString("id", "store-$index"),
                    name = item.optString("name", "Tienda recomendada"),
                    address = item.optString("address", "Sin dirección"),
                    coordinate = GeoCoordinate(latitude, longitude),
                    distanceKm = item.optDouble("distanceKm", 0.0)
                )
            )
        }
    }

    private fun firstString(intent: Intent, vararg keys: String): String? =
        keys.firstNotNullOfOrNull { intent.getStringExtra(it) }
}
