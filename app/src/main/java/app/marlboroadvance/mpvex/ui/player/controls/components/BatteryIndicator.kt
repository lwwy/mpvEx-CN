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
        // 电池外壳
        Box(
            modifier = Modifier
                .width(38.dp)  // 稍微加宽，给数字留出空间
                .height(18.dp) // 高度
                .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp)) // 半透明黑底，防止在亮色画面上白框看不清
                .border(1.5.dp, frameColor, RoundedCornerShape(4.dp)) // 白色粗边框
                .padding(2.dp), // 边框和内部填充的间隙
            contentAlignment = Alignment.Center
        ) {
            // 内部电量填充条
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = batteryLevel / 100f) // 宽度根据电量变化
                    .fillMaxHeight()
                    .background(batteryColor, RoundedCornerShape(1.dp))
                    .align(Alignment.CenterStart) // 靠左排列
            )
            
            // 电量数字
            Text(
                text = batteryLevel.toString(),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                // 加个黑色阴影，确保在绿色底和透明底上都清晰可见
                style = LocalTextStyle.current.copy(
                    shadow = Shadow(color = Color.Black, blurRadius = 2f, offset = Offset(1f, 1f))
                )
            )
        }
        
        // 电池右侧正极凸起
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(8.dp)
                .background(frameColor, RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
        )
    }
}
