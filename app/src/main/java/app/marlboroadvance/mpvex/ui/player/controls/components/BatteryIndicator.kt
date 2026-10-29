package app.marlboroadvance.mpvex.ui.player.controls.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BatteryIndicator(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(100) }

    DisposableEffect(context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
                val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
                if (level >= 0 && scale > 0) {
                    batteryLevel = (level * 100 / scale.toFloat()).toInt()
                }
            }
        }
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        onDispose { context.unregisterReceiver(receiver) }
    }

    // 颜色：>20% 绿色，<=20% 红色
    val batteryColor = if (batteryLevel > 20) Color(0xFF67C23A) else Color(0xFFF56C6C)
    val frameColor = Color.White

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 电池外壳（放大了尺寸）
        Box(
            modifier = Modifier
                .width(54.dp)   // 从 38 放大到 54
                .height(26.dp)  // 从 18 放大到 26
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp)) // 加深一点底色，在白画面下更清晰
                .border(2.dp, frameColor, RoundedCornerShape(6.dp)) // 2dp 粗边框
                .padding(3.dp), // 内部留白
            contentAlignment = Alignment.Center
        ) {
            // 内部电量填充条
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = batteryLevel / 100f)
                    .fillMaxHeight()
                    .background(batteryColor, RoundedCornerShape(2.dp))
                    .align(Alignment.CenterStart)
            )
            
            // 电量数字（绝对居中修正）
            Text(
                text = batteryLevel.toString(),
                color = Color.White,
                fontSize = 15.sp,          // 调大字号
                lineHeight = 15.sp,        // 👈 关键：强制行高等于字号，避免文字偏下
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                style = LocalTextStyle.current.copy(
                    // 禁用系统默认的字体上下留白
                    platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
                    shadow = Shadow(color = Color.Black, blurRadius = 3f, offset = Offset(1.5f, 1.5f))
                )
            )
        }
        
        // 电池右侧正极凸起
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(12.dp)
                .background(frameColor, RoundedCornerShape(topEnd = 3.dp, bottomEnd = 3.dp))
        )
    }
}
