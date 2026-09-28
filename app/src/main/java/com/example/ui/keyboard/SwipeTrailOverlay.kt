package com.example.ui.keyboard

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.engine.swipe.SwipePoint

@Composable
fun SwipeTrailOverlay(
    points: List<SwipePoint>,
    accentColor: Color,
    isSwiping: Boolean,
    modifier: Modifier = Modifier
) {
    val alphaAnim = remember { Animatable(1f) }

    LaunchedEffect(isSwiping) {
        if (!isSwiping && points.isNotEmpty()) {
            alphaAnim.animateTo(0f, animationSpec = tween(280))
        } else if (isSwiping) {
            alphaAnim.snapTo(1f)
        }
    }

    if (points.size >= 2 && alphaAnim.value > 0.02f) {
        Canvas(modifier = modifier.fillMaxSize()) {
            val path = Path()
            val first = points.first()
            path.moveTo(first.x, first.y)

            for (i in 1 until points.size) {
                val prev = points[i - 1]
                val curr = points[i]
                val midX = (prev.x + curr.x) / 2f
                val midY = (prev.y + curr.y) / 2f
                path.quadraticTo(prev.x, prev.y, midX, midY)
            }
            val last = points.last()
            path.lineTo(last.x, last.y)

            val currentAlpha = alphaAnim.value

            // Outer glow path
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.25f * currentAlpha),
                        accentColor.copy(alpha = 0.5f * currentAlpha)
                    ),
                    start = Offset(first.x, first.y),
                    end = Offset(last.x, last.y)
                ),
                style = Stroke(
                    width = 16f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Inner core vivid trail
            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.6f * currentAlpha),
                        Color.White.copy(alpha = 0.9f * currentAlpha)
                    ),
                    start = Offset(first.x, first.y),
                    end = Offset(last.x, last.y)
                ),
                style = Stroke(
                    width = 6f,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Head touch dot
            drawCircle(
                color = Color.White.copy(alpha = 0.95f * currentAlpha),
                radius = 8f,
                center = Offset(last.x, last.y)
            )
        }
    }
}
