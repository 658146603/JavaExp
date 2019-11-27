package xyz.nfcv.pupil.asmd.ui

import android.os.Bundle
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.animation.Crossfade
import androidx.ui.core.*
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import androidx.ui.tooling.preview.Preview
import xyz.nfcv.pupil.asmd.ui.widget.VectorImage
import xyz.nfcv.pupil.asmd.ui.widget.VectorImageButton
import xyz.nfcv.pupil.asmd.R
import xyz.nfcv.pupil.asmd.ui.theme.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Preview
@Composable
fun Main() {
    val (drawerState, onDrawerStateChange) = +state { DrawerState.Closed }

    MaterialTheme(colors = lightThemeColors, typography = themeTypography) {
        ModalDrawerLayout(
                drawerState = drawerState, onStateChange = onDrawerStateChange,
                gesturesEnabled = drawerState == DrawerState.Opened,
                drawerContent = { AppDrawer(currentScreen = MainStatus.nowScreen, closeDrawer = { onDrawerStateChange(DrawerState.Closed) }) },
                bodyContent = { AppContent { onDrawerStateChange(DrawerState.Opened) } }
        )
    }
}

@Composable
private fun AppContent(openDrawer: () -> Unit) {
    Crossfade(MainStatus.nowScreen) { screen ->
        Surface(color = +themeColor { background }) {
            when (screen) {
                is Screen.Exam -> ExamScreen { openDrawer() }
                is Screen.Manage -> ManageScreen { openDrawer() }
                is Screen.Analysis -> AnalysisScreen { openDrawer() }
            }
        }
    }
}

@Composable
private fun AppDrawer(currentScreen: Screen, closeDrawer: () -> Unit) {
    Column(crossAxisSize = LayoutSize.Expand, mainAxisSize = LayoutSize.Expand) {
        HeightSpacer(24.dp)
        Padding(16.dp) {
            Row {
                VectorImage(id = R.drawable.ic_logo, tint = +themeColor { primary })
                WidthSpacer(8.dp)
                Text(text = "PupilASMD", style = (+themeTextStyle { h6 }))
            }
        }
        Divider(color = Color(0x14333333))
        DrawerButton(icon = R.drawable.ic_edit_white, label = "测试", isSelected = currentScreen == Screen.Exam) {
            navigateToScreen(Screen.Exam)
            closeDrawer()
        }
        DrawerButton(icon = R.drawable.ic_data_white, label = "分析", isSelected = currentScreen == Screen.Analysis) {
            navigateToScreen(Screen.Analysis)
            closeDrawer()
        }
        DrawerButton(icon = R.drawable.ic_data_white, label = "管理", isSelected = currentScreen == Screen.Manage) {
            navigateToScreen(Screen.Manage)
            closeDrawer()
        }
    }
}

@Composable
private fun DrawerButton(@DrawableRes icon: Int, label: String, isSelected: Boolean, action: () -> Unit) {
    val textIconColor = if (isSelected) {
        +themeColor { primary }
    } else {
        (+themeColor { onSurface }).copy(alpha = 0.6f)
    }

    val backgroundColor = if (isSelected) {
        (+themeColor { primary }).copy(alpha = 0.12f)
    } else {
        (+themeColor { surface }).copy(alpha = 0.12f)
    }

    Padding(left = 8.dp, top = 8.dp, right = 8.dp) {
        Surface(color = backgroundColor, shape = RoundedCornerShape(4.dp)) {
            Button(onClick = action, style = TextButtonStyle()) {
                Row(mainAxisSize = LayoutSize.Expand, crossAxisAlignment = CrossAxisAlignment.Center) {
                    VectorImage(id = icon, tint = textIconColor)
                    WidthSpacer(16.dp)
                    Text(text = label, style = (+themeTextStyle { body2 }).copy(color = textIconColor))
                }
            }
        }
    }
}

@Composable
private fun HomeScreenDivider() {
    Padding(left = 14.dp, right = 14.dp) { Opacity(0.08f) { Divider() } }
}

@Composable
fun ExamScreen(openDrawer: () -> Unit) {
    val context = +ambient(ContextAmbient)
    FlexColumn {
        inflexible {
            TopAppBar(
                    title = { Padding(padding = EdgeInsets(0.dp, 16.dp, 0.dp, 16.dp)) { Text(text = "测试") } },
                    navigationIcon = { VectorImageButton(R.drawable.ic_edit_white) { openDrawer() } }
            )
        }
    }
}


@Composable
fun AnalysisScreen(openDrawer: () -> Unit) {
    val context = +ambient(ContextAmbient)
    FlexColumn {
        inflexible {
            TopAppBar(
                    title = { Padding(padding = EdgeInsets(0.dp, 16.dp, 0.dp, 16.dp)) { Text(text = "分析") } },
                    navigationIcon = { VectorImageButton(R.drawable.ic_data_white) { openDrawer() } }
            )
        }
    }
}

@Composable
fun ManageScreen(openDrawer: () -> Unit) {
    val context = +ambient(ContextAmbient)
    FlexColumn {
        inflexible {
            TopAppBar(
                    title = { Padding(padding = EdgeInsets(0.dp, 16.dp, 0.dp, 16.dp)) { Text(text = "管理") } },
                    navigationIcon = { VectorImageButton(R.drawable.ic_data_white) { openDrawer() } }
            )
        }
    }
}
