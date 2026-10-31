package com.ovasta.logisticsapp.presentation.nav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.base.Base_white
import com.ovasta.logisticsapp.base.Gray200
import com.ovasta.logisticsapp.base.Gray500
import com.ovasta.logisticsapp.base.Primary
import com.ovasta.logisticsapp.base.xsMedium

// Top-level screens reachable from the bottom navigation bar
enum class BottomNavTab(
    val route: Any,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    HomeTab(Home, R.drawable.ic_home, R.string.home),
    ProfileTab(Profile, R.drawable.ic_profile, R.string.profile);

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
