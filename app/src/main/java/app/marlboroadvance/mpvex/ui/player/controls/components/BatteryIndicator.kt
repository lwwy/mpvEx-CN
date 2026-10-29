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
import androidx.compose.foundation.layout.offset
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
    // 电池外壳（尺寸保持不变）
    Box(
        modifier = Modifier
            .width(70.dp)   
            .height(34.dp)  
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp)) 
            .border(2.5.dp, frameColor, RoundedCornerShape(8.dp)) 
            .padding(3.dp), 
        contentAlignment = Alignment.Center
    ) {
        // 内部电量填充条
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = batteryLevel / 100f)
                .fillMaxHeight()
                .background(batteryColor) 
                .align(Alignment.CenterStart)
        )
        
        // 电量数字（放大到 24sp，并向上微调）
        Text(
            text = batteryLevel.toString(),
            color = Color.White,
            fontSize = 24.sp,          // 👈 字号加大到 24
            lineHeight = 24.sp,        // 👈 行高同步
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.offset(y = (-1).dp), // 👈 核心微调：整体向上偏移 1dp，解决字体偏下的视觉错觉
            style = LocalTextStyle.current.copy(
                platformStyle = androidx.compose.ui.text.PlatformTextStyle(includeFontPadding = false),
                shadow = Shadow(color = Color.Black, blurRadius = 4f, offset = Offset(2f, 2f))
            )
        )
    }
    
    // 电池右侧正极凸起（保持不变）
    Box(
        modifier = Modifier
            .width(4.dp)    
            .height(16.dp)  
            .background(frameColor, RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
    )
    }
