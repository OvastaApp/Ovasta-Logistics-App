# Home Screen: Unified Task List & Timed Delivery Alerts

## Overview

This document describes the implementation of the unified task list and timed delivery alert system on the Home screen.

## Feature Summary

1. **Unified Assigned List**: `appTasks` (HomeTask) and `assignedDeliveryTasks` (DeliveryTask) are displayed in a single list with different card styles per type.
2. **Timed Bottom Sheet for Waiting Tasks**: When new delivery tasks arrive (from Firestore), a bottom sheet appears with a 30-second per-task countdown (progress bar). User can accept or minimize.
3. **Summary Card**: After 30s expires or user minimizes, a card at the top says "You have X orders available to take" -- tapping navigates to `AvailableTasksScreen`.
4. **Tracking Requirement**: All orders, statistics, and task lists are hidden unless user enables tracking toggle. Shows `StatisticsTrackingHint` when disabled.

## Architecture

### Data Flow

```
Firestore (listenToNewDeliveryTasks)
    │
    ▼
HomeViewModel
    ├── waitingDeliveryTasks (all waiting tasks from Firestore)
    ├── activeAlertTasks (tasks within 30s window → shown in bottom sheet)
    ├── expiredWaitingTasks (tasks past 30s → count shown in summary card)
    ├── appTasks (seller tasks from getAssignedOrders)
    └── assignedDeliveryTasks (delivery tasks assigned to user)
         │
         ▼
HomeScreen
    ├── NewDeliveryTaskBottomSheet (activeAlertTasks, if not minimized)
    └── TasksContent
            ├── Tracking toggle
            ├── Partner statistics
            ├── Summary card (available orders count)
            └── Unified list (appTasks cards + assignedDeliveryTasks cards)
```

### Timer Mechanism

- `_taskAlertTimestamps: MutableMap<Int, Long>` records when each task first appeared
- `startAlertExpiryTimer()` coroutine runs every 500ms, moves tasks older than 30s from `activeAlertTasks` → `expiredWaitingTasks`
- Bottom sheet card shows `LinearProgressIndicator` that goes from 1.0 to 0.0 over 30s

### Bottom Sheet Behavior

- **Shows when**: `activeAlertTasks.isNotEmpty() && !bottomSheetMinimized && isTracking`
- **X button only**: User can ONLY close via the X button (top-right). Tapping outside or swiping down does nothing - sheet stays open until explicitly closed.
- **Minimize action**: X button triggers minimize (sets `bottomSheetMinimized = true`), summary card appears, alarm stops
- **Accept**: Calls `changeOrderStatus(orderId, OrderSteps.Assigned)`, optimistically removes from alerts, refreshes `getAssignedDeliveryOrders()`
- **New task arrives after minimize**: Resets `bottomSheetMinimized = false`, bottom sheet re-opens
- **30s expires**: Task moves to `expiredWaitingTasks`, when all expire the sheet auto-dismisses

### Alert UX (Uber-style)

- **Looping alarm sound**: `OrderAlarmSound` plays custom mp3 file (`res/raw/new_order_alert.mp3`) with looping. Only starts when new tasks arrive AND tracking is enabled. Stops on: accept (if no more active alerts), minimize, all expired, tracking disabled, or ViewModel cleared.
- **Screen flash overlay**: `ScreenFlashOverlay` composable with pulsing yellow overlay (alpha 0→0.15, 600ms cycle). Also wakes screen from sleep (`FLAG_TURN_SCREEN_ON`, `FLAG_KEEP_SCREEN_ON`, `setShowWhenLocked`). Cleans up flags on dispose. Only shows when tracking is enabled.
- **Progress button**: Accept button has draining background (fills from left, shrinks over 30s). Green/Primary background drains to reveal gray track. No separate progress indicator.
- **Tracking requirement**: New tasks only added to `activeAlertTasks` when `isTracking = true`. When tracking is disabled, alarm stops, active alerts cleared, bottom sheet hidden.
- **Re-enable tracking**: When tracking is re-enabled, all waiting tasks from `waitingDeliveryTasks` are moved to `activeAlertTasks`, timestamps **reset to NOW** (30s countdown starts fresh), bottom sheet opens, and alarm starts.

### Accept Button Progress

The accept button in `DeliveryTaskAlertCard` uses a `Box` with layered backgrounds:
- Outer Box: `Gray500.copy(alpha = 0.2f)` track (always visible)
- Inner Box: `Primary` colored, `fillMaxWidth(fraction = progress)` aligned to start
- Text overlaid on center

### Direction Display (Origin → Destination)

Both `PendingDeliveryTaskItem` and `NewDeliveryTaskBottomSheet` show pickup/dropoff addresses using a vertical connector layout:

```
● Origin address         (dot vertically centered with text)
│                        (vertical line, padding-start = 4dp)
● Destination address    (dot vertically centered with text)
```

- Each address is a `Row(verticalAlignment = CenterVertically)` with the dot + text on the same line
- Vertical line is a 2dp-wide `Box` between the rows, offset with `padding(start = 4dp)` to align with dot centers
- `PendingDeliveryTaskItem`: SellerAction (green) origin dot, CustomerAction (orange) destination dot
- `NewDeliveryTaskBottomSheet`: Primary color origin dot, Gray500 destination dot

### Summary Card

- Shows when: `bottomSheetMinimized == true` OR `expiredWaitingTasks.isNotEmpty()`
- Count: If minimized, includes both active + expired. Otherwise just expired.
- Tap: Navigates to `AvailableTasksScreen`

## Files Modified

| File | Change |
|------|--------|
| `presentation/home/presentation/HomeViewState.kt` | Added `activeAlertTasks`, `expiredWaitingTasks`, `bottomSheetMinimized`; removed `newDeliveryTasksAlert` |
| `presentation/home/presentation/HomeAction.kt` | Removed `DismissNewTaskAlert`; added `MinimizeBottomSheet`, `NavigateToAvailableTasks` |
| `presentation/home/presentation/HomeViewModel.kt` | Timer logic, minimize handling, fixed accept flow (API + refresh), exposed `taskAlertTimestamps` |
| `presentation/home/presentation/HomeScreen.kt` | Updated bottom sheet condition, added `NavigateToAvailableTasks` handler |
| `presentation/home/presentation/components/TasksContent.kt` | Summary card at top, unified list (appTasks + assignedDeliveryTasks) |
| `presentation/home/presentation/components/NewDeliveryTaskBottomSheet.kt` | Removed dismiss, added minimize icon, progress bar countdown |
| `presentation/home/presentation/components/AvailableOrdersSummaryCard.kt` | **New** - clickable card showing available order count |
| `presentation/home/presentation/availableTasks/AvailableTasksScreen.kt` | **New** - screen listing waiting tasks with accept buttons |
| `presentation/home/presentation/availableTasks/AvailableTasksViewModel.kt` | **New** - ViewModel for available tasks screen |
| `presentation/home/di/HomeModule.kt` | Registered `AvailableTasksViewModel` in Koin |
| `presentation/nav/AppNavHost.kt` | Added `AvailableTasks` route + NavEntry |
| `base/ext/OrderAlarmSound.kt` | **New** - looping alarm sound using MediaPlayer + system alarm URI |
| `base/di/hapticsModule.kt` | Registered `OrderAlarmSound` in Koin |
| `res/values/strings.xml` | Added `orders_available_to_take`, `available_orders` |
| `res/values-ar/strings.xml` | Added Arabic translations |

## Key Decisions

- **No `AssignedItem` sealed interface**: The unified list uses separate `items` blocks in LazyColumn with different key prefixes (`app_`, `delivery_`) instead of a sealed wrapper class. Simpler, same result.
- **Per-task timer via timestamps map**: Instead of per-task coroutines, a single timer coroutine checks all timestamps every 500ms.
- **`AvailableTasksScreen` has its own ViewModel**: Re-fetches from the same `IHomeRepository.listenToNewDeliveryTasks()`. Independent of `HomeViewModel` lifecycle.
- **Optimistic removal on accept**: Task removed from UI immediately, `getAssignedDeliveryOrders()` refreshes in background.
- **All Text uses named styles from `base/styles.kt`**: No inline `fontSize`/`fontWeight`/`MaterialTheme.typography`. Button text uses `smMedium.copy(color = Base_white)`, headers use `lgSemiBold`.

## String Resources

| Key | English | Arabic |
|-----|---------|--------|
| `orders_available_to_take` | You have %1$d orders available to take | لديك %1$d طلبات متاحة للاستلام |
| `available_orders` | Available Orders | الطلبات المتاحة |
