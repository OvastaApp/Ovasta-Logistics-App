package com.ovasta.logisticsapp.presentation.nav

import com.ovasta.logisticsapp.shared.resources.Res
import com.ovasta.logisticsapp.shared.resources.home
import com.ovasta.logisticsapp.shared.resources.ic_home
import com.ovasta.logisticsapp.shared.resources.ic_profile
import com.ovasta.logisticsapp.shared.resources.profile

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.xsMedium

// Top-level screens reachable from the bottom navigation bar
enum class BottomNavTab(
    val route: Any,
    val iconRes: DrawableResource,
    val labelRes: StringResource
) {
    HomeTab(Home, Res.drawable.ic_home, Res.string.home),
    ProfileTab(Profile, Res.drawable.ic_profile, Res.string.profile);

    companion object {
        fun isTopLevel(route: Any?): Boolean = entries.any { it.route == route }
    }
}

@Composable
fun AppBottomNavigation(
    currentRoute: Any?,
    onTabSelected: (BottomNavTab) -> Unit
) {
    Column {
        HorizontalDivider(color = Gray200)
        NavigationBar(containerColor = Base_white) {
            BottomNavTab.entries.forEach { tab ->
                val isSelected = tab.route == currentRoute
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { if (!isSelected) onTabSelected(tab) },
                    icon = {
                        Icon(
                            painter = painterResource(tab.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(tab.labelRes),
                            style = xsMedium,
                            color = if (isSelected) Primary else Gray500
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Primary,
                        unselectedIconColor = Gray500,
                        indicatorColor = Primary.copy(alpha = 0.12f)
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomNavigationPreview() {
    AppBottomNavigation(currentRoute = Home, onTabSelected = {})
}
