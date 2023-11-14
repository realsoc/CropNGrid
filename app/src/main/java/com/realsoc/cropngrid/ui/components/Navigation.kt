package com.realsoc.cropngrid.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.realsoc.cropngrid.ui.theme.CropNGridTheme
import com.realsoc.cropngrid.ui.theme.ThemePreviews

@Composable
fun RowScope.CropNGridNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = CropNGridNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = CropNGridNavigationDefaults.navigationContentColor(),
            selectedTextColor = CropNGridNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = CropNGridNavigationDefaults.navigationContentColor(),
            indicatorColor = CropNGridNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

@Composable
fun CropNGridNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = CropNGridNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

@Composable
fun CropNGridNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () -> Unit = icon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = CropNGridNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = CropNGridNavigationDefaults.navigationContentColor(),
            selectedTextColor = CropNGridNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = CropNGridNavigationDefaults.navigationContentColor(),
            indicatorColor = CropNGridNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

@Composable
fun CropNGridNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = CropNGridNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

@ThemePreviews
@Composable
fun CropNGridNavigationPreview() {
    val items = listOf("Home", "Grid List")
    val icons = listOf(
        Icons.Outlined.Home,
        Icons.Outlined.List,
    )
    val selectedIcons = listOf(
        Icons.Filled.Home,
        Icons.Filled.List,
    )

    CropNGridTheme {
        CropNGridNavigationBar {
            items.forEachIndexed { index, item ->
                CropNGridNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

object CropNGridNavigationDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.primary

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.surface

}
