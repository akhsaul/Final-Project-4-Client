package kelompok.tiga.app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.OvershootInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import kelompok.tiga.app.net.Connector
import kelompok.tiga.app.ui.theme.GreenMint
import kelompok.tiga.app.ui.theme.KaDoInTheme
import kelompok.tiga.app.util.Singleton

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Singleton.init(this)
        setContent {
            KaDoInTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = GreenMint
                ) {
                    val scale = remember { Animatable(0f) }

                    LaunchedEffect(key1 = true) {
                        scale.animateTo(
                            targetValue = 0.5f,
                            animationSpec = tween(
                                durationMillis = 1500,
                                delayMillis = 500,
                                easing = {
                                    OvershootInterpolator(2f).getInterpolation(it)
                                }
                            )
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.launcher),
                            contentDescription = "Splash Image",
                            modifier = Modifier.fillMaxSize()
                                .scale(scale.value)
                        )
                    }
                }
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
        },5000)
    }
}