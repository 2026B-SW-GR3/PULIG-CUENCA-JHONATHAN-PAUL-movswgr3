package com.jonathanpulig.fitmap.competitions

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jonathanpulig.fitmap.competitions.databinding.ActivityMainBinding
import com.jonathanpulig.fitmap.competitions.databinding.ItemCompetitionBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {
    companion object {
        private const val DEFAULT_LAT = -0.180653
        private const val DEFAULT_LNG = -78.467834
    }

    private lateinit var binding: ActivityMainBinding
    private var incoming: IncomingRecommendations? = null
    private var competitions: List<Competition> = emptyList()
    private var selectedCompetition: Competition? = null
    private val registeredCompetitionIds = mutableSetOf<String>()
    private var receivedOverlay: FolderOverlay? = null
    private var checkpointOverlay: FolderOverlay? = null
    private var routeOverlay: Polyline? = null
    private var locationOverlay: MyLocationNewOverlay? = null

    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.values.any { it }) enableMyLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().apply {
            load(this@MainActivity, getSharedPreferences("osmdroid", MODE_PRIVATE))
            userAgentValue = packageName
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        applySystemInsets()
        setupMapBase()

        incoming = IntentContract.read(intent)
        val received = incoming
        if (received == null) {
            showWaitingState()
        } else {
            showReceivedFlow(received)
        }
        binding.buttonCenterRoute.setOnClickListener { centerSelectedRoute() }
        binding.buttonRegister.setOnClickListener { toggleRegistration() }
    }

    private fun applySystemInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
    }

    private fun setupMapBase() = with(binding.mapView) {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        minZoomLevel = 4.0
        maxZoomLevel = 20.0
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT)
        overlays.add(CopyrightOverlay(this@MainActivity))
        controller.setZoom(13.0)
        controller.setCenter(GeoPoint(DEFAULT_LAT, DEFAULT_LNG))
        requestLocation()
    }

    private fun showWaitingState() = with(binding) {
        textFlowStatus.text = "Esperando recomendaciones de App 2"
        textFlowDetails.text = "Las competiciones y rutas aparecerán cuando Dennis envíe las tiendas seleccionadas."
        textCompetitionTitle.text = "Sin competiciones cargadas"
        mapLegend.visibility = android.view.View.GONE
        competitionContainer.removeAllViews()
        buttonRegister.text = "Esperando datos de App 2"
        buttonRegister.isEnabled = false
    }

    private fun showReceivedFlow(data: IncomingRecommendations) = with(binding) {
        val eventLabel = data.eventName ?: "evento deportivo"
        textFlowStatus.text = "Flujo recibido desde App 2"
        textFlowDetails.text = "${data.stores.size} tienda${if (data.stores.size == 1) "" else "s"} recomendada${if (data.stores.size == 1) "" else "s"} · ${data.discipline} · $eventLabel"
        mapLegend.visibility = android.view.View.VISIBLE
        competitions = CompetitionRepository.around(data.eventCoordinate)
        textCompetitionTitle.text = "Competiciones locales (${competitions.size})"
        drawReceivedContext(data)
        selectCompetition(competitions.first())
    }

    private fun drawReceivedContext(data: IncomingRecommendations) {
        receivedOverlay?.let { binding.mapView.overlays.remove(it) }
        val overlay = FolderOverlay()
        overlay.add(Marker(binding.mapView).apply {
            position = data.eventCoordinate.toGeoPoint()
            title = data.eventName ?: "Evento recibido"
            snippet = "Origen del flujo · ${data.discipline}"
            icon = circularIcon("E", Color.rgb(21, 101, 192), 92)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        })
        data.stores.forEach { store ->
            overlay.add(Marker(binding.mapView).apply {
                position = store.coordinate.toGeoPoint()
                title = store.name
                snippet = "Tienda recibida · ${store.address}"
                icon = circularIcon("S", Color.rgb(23, 107, 82), 72)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
        }
        receivedOverlay = overlay
        binding.mapView.overlays.add(overlay)
    }

    private fun renderCompetitionList() {
        binding.competitionContainer.removeAllViews()
        competitions.forEach { competition ->
            val row = ItemCompetitionBinding.inflate(layoutInflater, binding.competitionContainer, false)
            val selected = competition.id == selectedCompetition?.id
            row.textCompetitionName.text = competition.name
            row.textCompetitionCategory.text = competition.category
            row.textCompetitionDetails.text = "%.1f km · %s · %d checkpoints".format(
                competition.distanceKm,
                competition.schedule,
                competition.checkpoints.size
            )
            row.textDifficulty.text = competition.difficulty.label
            row.textDifficulty.setTextColor(competition.difficulty.color)
            row.textDifficulty.background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 24f
                setColor(withAlpha(competition.difficulty.color, 28))
            }
            row.radioCompetition.isChecked = selected
            row.root.strokeWidth = if (selected) 2 else 1
            row.root.strokeColor = if (selected) competition.difficulty.color else ContextCompat.getColor(this, R.color.fitmap_stroke)
            row.root.setCardBackgroundColor(
                ContextCompat.getColor(this, if (selected) R.color.fitmap_sage_light else android.R.color.white)
            )
            row.root.setOnClickListener { selectCompetition(competition) }
            binding.competitionContainer.addView(row.root)
        }
    }

    private fun selectCompetition(competition: Competition) {
        selectedCompetition = competition
        renderCompetitionList()
        drawCompetitionRoute(competition)
        refreshRegistrationButton()
    }

    private fun drawCompetitionRoute(competition: Competition) {
        routeOverlay?.let { binding.mapView.overlays.remove(it) }
        checkpointOverlay?.let { binding.mapView.overlays.remove(it) }

        routeOverlay = Polyline(binding.mapView).apply {
            setPoints(competition.route.map { it.toGeoPoint() })
            title = competition.name
            snippet = "%.1f km · Dificultad %s".format(competition.distanceKm, competition.difficulty.label)
            outlinePaint.color = competition.difficulty.color
            outlinePaint.strokeWidth = 13f
        }
        binding.mapView.overlays.add(routeOverlay)

        val checkpoints = FolderOverlay()
        competition.checkpoints.forEachIndexed { index, checkpoint ->
            checkpoints.add(Marker(binding.mapView).apply {
                position = checkpoint.coordinate.toGeoPoint()
                title = "Checkpoint ${index + 1}: ${checkpoint.name}"
                snippet = "Registro de tiempo del atleta"
                icon = circularIcon((index + 1).toString(), competition.difficulty.color, 86)
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            })
        }
        checkpointOverlay = checkpoints
        binding.mapView.overlays.add(checkpoints)
        binding.mapLegend.text = "${competition.difficulty.label} · %.1f km · Checkpoints 1–${competition.checkpoints.size}".format(competition.distanceKm)
        binding.mapLegend.setTextColor(competition.difficulty.color)
        binding.mapView.invalidate()
        centerSelectedRoute()
    }

    private fun centerSelectedRoute() {
        val route = selectedCompetition?.route
        if (route.isNullOrEmpty()) {
            val target = locationOverlay?.myLocation ?: GeoPoint(DEFAULT_LAT, DEFAULT_LNG)
            binding.mapView.controller.animateTo(target)
            binding.mapView.controller.setZoom(14.0)
            return
        }
        val points = route.map { it.toGeoPoint() }
        val bounds = BoundingBox.fromGeoPoints(points)
        binding.mapView.post { binding.mapView.zoomToBoundingBox(bounds, true, 90) }
    }

    private fun toggleRegistration() {
        val competition = selectedCompetition ?: return
        val registered = if (competition.id in registeredCompetitionIds) {
            registeredCompetitionIds.remove(competition.id)
            false
        } else {
            registeredCompetitionIds.add(competition.id)
            true
        }
        Toast.makeText(
            this,
            if (registered) "Registro confirmado en ${competition.name}" else "Registro cancelado",
            Toast.LENGTH_SHORT
        ).show()
        refreshRegistrationButton()
    }

    private fun refreshRegistrationButton() {
        val competition = selectedCompetition
        binding.buttonRegister.isEnabled = competition != null
        binding.buttonRegister.text = when {
            competition == null -> "Selecciona una competición"
            competition.id in registeredCompetitionIds -> "Inscrito · Toca para cancelar"
            else -> "Registrarme en ${competition.name}"
        }
    }

    private fun circularIcon(label: String, color: Int, size: Int): BitmapDrawable {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        canvas.drawCircle(size / 2f, size / 2f, size * .42f, paint)
        paint.apply {
            this.color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = size * .44f
            isFakeBoldText = true
        }
        canvas.drawText(label, size / 2f, size * .66f, paint)
        return BitmapDrawable(resources, bitmap)
    }

    private fun requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            locationPermission.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    private fun enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) return
        locationOverlay?.disableMyLocation()
        locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), binding.mapView).apply { enableMyLocation() }
        binding.mapView.overlays.add(locationOverlay)
        binding.mapView.invalidate()
    }

    private fun GeoCoordinate.toGeoPoint() = GeoPoint(latitude, longitude)

    private fun withAlpha(color: Int, alpha: Int): Int = Color.argb(
        alpha,
        Color.red(color),
        Color.green(color),
        Color.blue(color)
    )

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        locationOverlay?.disableMyLocation()
        super.onDestroy()
    }
}
