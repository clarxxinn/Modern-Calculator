package com.example.modernappcalculator

import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.modernappcalculator.ui.theme.CalcGreen
import com.example.modernappcalculator.ui.theme.DarkKeyDigit
import com.example.modernappcalculator.ui.theme.DarkKeyFunction
import com.example.modernappcalculator.ui.theme.LightKeyDigit
import com.example.modernappcalculator.ui.theme.LightKeyFunction
import com.example.modernappcalculator.ui.theme.ModernAppCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(true) }
            ModernAppCalculatorTheme(darkTheme = darkTheme) {
                Surface(color = MaterialTheme.colorScheme.background) {
                    CalculatorScreen(
                        darkTheme = darkTheme,
                        onToggleTheme = { darkTheme = !darkTheme },
                        window = window
                    )
                }
            }
        }
    }
}

/** Hides or shows the status bar + navigation bar for a true fullscreen (immersive) mode. */
private fun setFullscreen(window: Window, enable: Boolean) {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    if (enable) {
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
    } else {
        insetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun CalculatorScreen(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    window: Window? = null
) {
    val calculator = remember { CalculatorState() }
    var menuExpanded by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                contentDescription = if (isFullscreen) "Exit fullscreen" else "Fullscreen",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isFullscreen = !isFullscreen
                        window?.let { setFullscreen(it, isFullscreen) }
                    }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CalcGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text("=", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.Apps,
                    contentDescription = "Converter",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            Toast.makeText(context, "Converter mode coming soon", Toast.LENGTH_SHORT).show()
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Box {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { menuExpanded = true }
                    )
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(if (darkTheme) "Switch to Light mode" else "Switch to Dark mode") },
                            onClick = {
                                menuExpanded = false
                                onToggleTheme()
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Expression + result display
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = CalculatorState.formatDisplayExpression(calculator.expression),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 28.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (calculator.result.isNotEmpty()) "= ${calculator.result}" else "",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Keypad
        val buttonSpacing = 12.dp

        Column(
            verticalArrangement = Arrangement.spacedBy(buttonSpacing),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Row 1: C, backspace, /, *
            Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing), modifier = Modifier.fillMaxWidth()) {
                FunctionKey(text = "C", modifier = Modifier.weight(1f)) {
                    calculator.onClear()
                }
                IconKey(icon = Icons.AutoMirrored.Filled.Backspace, modifier = Modifier.weight(1f)) {
                    calculator.onBackspace()
                }
                OperatorKey(text = "÷", modifier = Modifier.weight(1f)) {
                    calculator.onOperator('/')
                }
                OperatorKey(text = "×", modifier = Modifier.weight(1f)) {
                    calculator.onOperator('*')
                }
            }

            // Row 2: 7 8 9 -
            Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing), modifier = Modifier.fillMaxWidth()) {
                DigitKey("7", Modifier.weight(1f)) { calculator.onDigit('7') }
                DigitKey("8", Modifier.weight(1f)) { calculator.onDigit('8') }
                DigitKey("9", Modifier.weight(1f)) { calculator.onDigit('9') }
                OperatorKey("-", Modifier.weight(1f)) { calculator.onOperator('-') }
            }

            // Row 3: 4 5 6 +
            Row(horizontalArrangement = Arrangement.spacedBy(buttonSpacing), modifier = Modifier.fillMaxWidth()) {
                DigitKey("4", Modifier.weight(1f)) { calculator.onDigit('4') }
                DigitKey("5", Modifier.weight(1f)) { calculator.onDigit('5') }
                DigitKey("6", Modifier.weight(1f)) { calculator.onDigit('6') }
                OperatorKey("+", Modifier.weight(1f)) { calculator.onOperator('+') }
            }

            // Row 4-5 combined: (1 2 3 / icon 0 .) + tall "="
            Row(
                horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .height((64.dp * 2) + buttonSpacing)
            ) {
                Column(
                    modifier = Modifier
                        .weight(3f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                        modifier = Modifier.weight(1f)
                    ) {
                        DigitKey("1", Modifier.weight(1f).fillMaxHeight()) { calculator.onDigit('1') }
                        DigitKey("2", Modifier.weight(1f).fillMaxHeight()) { calculator.onDigit('2') }
                        DigitKey("3", Modifier.weight(1f).fillMaxHeight()) { calculator.onDigit('3') }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing),
                        modifier = Modifier.weight(1f)
                    ) {
                        IconKey(
                            icon = Icons.AutoMirrored.Filled.CompareArrows,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        ) { calculator.onToggleSign() }
                        DigitKey("0", Modifier.weight(1f).fillMaxHeight()) { calculator.onDigit('0') }
                        FunctionKey(".", Modifier.weight(1f).fillMaxHeight()) { calculator.onDecimal() }
                    }
                }

                // Tall "=" button
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CalcGreen)
                        .clickable { calculator.onEquals() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("=", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DigitKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(digitKeyColor())
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 25.sp, fontWeight = FontWeight.Medium, color = digitTextColor())
    }
}

@Composable
private fun FunctionKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(functionKeyColor())
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 22.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun IconKey(icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(functionKeyColor())
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun OperatorKey(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(64.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CalcGreen)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun digitKeyColor() = MaterialTheme.colorScheme.let {
    if (it.background.luminance() < 0.5f) DarkKeyDigit else LightKeyDigit
}

@Composable
private fun functionKeyColor() = MaterialTheme.colorScheme.let {
    if (it.background.luminance() < 0.5f) DarkKeyFunction else LightKeyFunction
}

@Composable
private fun digitTextColor() = MaterialTheme.colorScheme.let {
    if (it.background.luminance() < 0.5f) CalcGreen else MaterialTheme.colorScheme.onBackground
}

private fun Color.luminance(): Float {
    return (0.299f * red + 0.587f * green + 0.114f * blue)
}

@Preview(showBackground = true, name = "Calculator - Dark")
@Composable
fun CalculatorPreviewDark() {
    ModernAppCalculatorTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CalculatorScreen(darkTheme = true, onToggleTheme = {})
        }
    }
}

@Preview(showBackground = true, name = "Calculator - Light")
@Composable
fun CalculatorPreviewLight() {
    ModernAppCalculatorTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            CalculatorScreen(darkTheme = false, onToggleTheme = {})
        }
    }
}