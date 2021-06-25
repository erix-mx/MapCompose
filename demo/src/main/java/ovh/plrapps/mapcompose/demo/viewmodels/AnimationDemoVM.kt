package ovh.plrapps.mapcompose.demo.viewmodels

import android.app.Application
import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.SnapSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ovh.plrapps.mapcompose.api.*
import ovh.plrapps.mapcompose.demo.providers.makeTileStreamProvider
import ovh.plrapps.mapcompose.demo.ui.widgets.Marker
import ovh.plrapps.mapcompose.ui.state.MapState

/**
 * This demo shows how animations can be chained one after another.
 * Since animations APIs are suspending functions, this is easy to do.
 */
class AnimationDemoVM(application: Application) : AndroidViewModel(application) {
    private val appContext: Context by lazy {
        getApplication<Application>().applicationContext
    }
    private val tileStreamProvider = makeTileStreamProvider(appContext)
    private var job: Job? = null

    val state: MapState by mutableStateOf(
        MapState(4, 4096, 4096, tileStreamProvider).apply {
            shouldLoopScale = true
            enableRotation()
            addMarker("m0", 0.5, 0.5) { Marker() }
            addMarker("m1", 0.78, 0.78) { Marker() }
            addMarker("m2", 0.79, 0.79) { Marker() }
            addMarker("m3", 0.785, 0.72) { Marker() }
            onTouchDown {
                job?.cancel()
            }
        }
    )

    fun startAnimation() {
        /* Cancel ongoing animation */
        job?.cancel()

        /* Start a new one */
        with(state) {
            job = viewModelScope.launch {
                scrollToAndCenter(0.5, 0.5, 0f, SnapSpec())
                scrollToAndCenter(0.0, 0.0, 2f, TweenSpec(2000, easing = LinearEasing))
                scrollToAndCenter(0.8, 0.8, 2f, TweenSpec(2000, easing = FastOutSlowInEasing))
                rotateTo(180f, TweenSpec(2000, easing = FastOutSlowInEasing))
                scrollToAndCenter(0.5, 0.5, 0.5f, TweenSpec(2000, easing = FastOutSlowInEasing))
                scrollToAndCenter(0.5, 0.5, 2f, TweenSpec(800, easing = FastOutSlowInEasing))
                rotateTo(0f, TweenSpec(1000, easing = FastOutSlowInEasing))
            }
        }
    }
}