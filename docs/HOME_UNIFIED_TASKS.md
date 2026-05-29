# Home Screen: Unified Task List & Timed Delivery Alerts

## Overview

This document describes the implementation of the unified task list and timed delivery alert system. The alert bottom sheet is global — it appears on any screen after login (Home, AvailableTasks, TaskDetails).

## Feature Summary

1. **Unified Assigned List**: `appTasks` (HomeTask) and `assignedDeliveryTasks` (DeliveryTask) are displayed in a single list with different card styles per type.
2. **Queue-Based Single-Task Bottom Sheet**: When new delivery tasks arrive (from Firestore), a bottom sheet shows ONE task at a time with a 30-second countdown. Additional tasks queue behind it. User can accept or minimize.
3. **Summary Card**: Shows "You have X orders available to take" — tapping navigates to `AvailableTasksScreen`. Count reflects live Firebase `waitingDeliveryTasks.size`.
4. **Tracking Requirement**: All orders, statistics, and task lists are hidden unless user enables tracking toggle. Shows `StatisticsTrackingHint` when disabled.
5. **Persistent Seen Tracking**: Tasks explicitly handled (accepted, expired, or minimized) are persisted to SharedPreferences and never re-alert, even after app restart.

## Architecture

### Data Flow

```
Firestore (listenToNewDeliveryTasks)
    │
    ▼
HomeViewModel
    ├── waitingDeliveryTasks (all waiting tasks from Firestore)
    ├── currentAlertTask (single task shown in bottom sheet)
    ├── alertQueue (tasks waiting to be shown next)
    ├── _seenTaskIds (persisted to SharedPreferences — explicitly handled tasks)
    ├── appTasks (seller tasks from getAssignedOrders)
    └── assignedDeliveryTasks (delivery tasks assigned to user)
         │
         ▼
AppNavHost (global overlay)
    ├── ScreenFlashOverlay (pulsing yellow, wakes screen)
    └── NewDeliveryTaskBottomSheet (currentAlertTask, if not minimized)
         │
         ▼
HomeScreen / AvailableTasksScreen / TaskDetails (any screen after login)
    ├── Tracking toggle
    ├── Partner statistics
    ├── Summary card (available orders count = waitingDeliveryTasks.size)
    └── Unified list (appTasks cards + assignedDeliveryTasks cards)
```

### Queue-Based Single-Task System

- **One task at a time**: `currentAlertTask` holds the single task displayed in the bottom sheet.
- **Queue**: `alertQueue` holds tasks waiting to be shown next.
- **Fresh timer per task**: Each task gets its own 30s countdown when it becomes `currentAlertTask` (NOT when it enters the queue).
- **Accept**: Task marked as seen → popped from queue → next task becomes current with fresh 30s.
- **Timer expires**: Task marked as seen → popped from queue → next task becomes current with fresh 30s.
- **Minimize**: Only current task marked as seen. `currentAlertTask` cleared to `null`. Queue remains intact. When a new task arrives, it becomes current immediately.

### Timer Mechanism

- `_taskAlertTimestamps: MutableMap<Int, Long>` records when each task became `currentAlertTask`.
- `startAlertExpiryTimer()` coroutine runs every 500ms, checks only the single `currentAlertTask` timestamp.
- When `currentAlertTask` reaches 30s: marks as seen, removes timestamp, calls `popNextTaskFromQueue()`.
- Bottom sheet card shows `LinearProgressIndicator` that goes from 1.0 to 0.0 over 30s.

### Persistence (Seen Task Tracking)

- `_seenTaskIds: MutableSet<Int>` loaded from SharedPreferences on ViewModel init.
- Persisted via `persistSeenTaskIds()` helper (SharedPreferences key: `"seen_task_ids"`).
- **Marked as seen on**:
  - `acceptDeliveryOrder(orderId)` — user accepts the task
  - `startAlertExpiryTimer()` — task's 30s timer expires while displayed
  - `MinimizeBottomSheet` — user taps X button (only current task, NOT queue items)
- **Cleanup**: Stale IDs (tasks no longer in Firebase) are removed from `_seenTaskIds` and re-persisted on each Firestore emission to prevent unbounded growth.
- **Survives app restart**: Tasks already handled never re-alert. Only genuinely new tasks (not in `_seenTaskIds` and not currently in pipeline) trigger the bottom sheet.

### Filtering Logic in `listenToNewDeliveryTasks()`

Two-level filtering prevents duplicates and re-alerts:

1. **Persisted `_seenTaskIds`** — Tasks explicitly handled (survives app restart)
2. **In-pipeline check** — Tasks currently being displayed or queued (session-only, prevents refresh duplicates)

```kotlin
val alreadyInPipeline = buildSet {
    state.currentAlertTask?.orderId?.let { add(it) }
    addAll(state.alertQueue.map { it.orderId })
}
val newTasks = tasks.filter { 
    it.orderId !in _seenTaskIds && it.orderId !in alreadyInPipeline 
}
```

### Bottom Sheet Behavior

- **Shows when**: `currentAlertTask != null && !bottomSheetMinimized && isTracking`
- **Global overlay**: Rendered at `AppNavHost` level — visible on ANY screen after login (not on Splash or Login).
- **X button only**: User can ONLY close via the X button (top-right). Tapping outside or swiping down does nothing — sheet stays open until explicitly closed.
- **Minimize action**: X button marks current task as seen, clears `currentAlertTask`, sets `bottomSheetMinimized = true`, summary card appears, alarm stops.
- **Accept**: Calls `changeOrderStatus(orderId, OrderSteps.Assigned)`, marks task as seen, pops next from queue (next task becomes current with fresh 30s), refreshes `getAssignedDeliveryOrders()`.
- **New task arrives after minimize**: Since `currentAlertTask == null`, new task becomes current immediately with fresh 30s, `bottomSheetMinimized = false`, bottom sheet opens.
- **Multiple tasks arrive simultaneously**: First becomes `currentAlertTask`, rest go to `alertQueue`. Each gets fresh 30s when it becomes current.

### Alert UX (Uber-style)

- **Looping alarm sound**: `OrderAlarmSound` plays custom mp3 file (`res/raw/new_order_alert.mp3`) with looping. Only starts when new tasks arrive AND tracking is enabled. Stops on: accept (if queue empty), minimize, all expired, tracking disabled, or ViewModel cleared.
- **Screen flash overlay**: `ScreenFlashOverlay` composable with pulsing yellow overlay (alpha 0→0.15, 600ms cycle). Also wakes screen from sleep (`FLAG_TURN_SCREEN_ON`, `FLAG_KEEP_SCREEN_ON`, `setShowWhenLocked`). Cleans up flags on dispose. Only shows when tracking is enabled.
- **Progress button**: Accept button in bottom sheet has draining background (fills from left, shrinks over 30s). Primary background drains to reveal gray track. No separate progress indicator.
- **Tracking requirement**: New tasks only queued when `isTracking = true`. When tracking is disabled, alarm stops, current task and queue cleared, bottom sheet hidden.
- **Re-enable tracking**: All `waitingDeliveryTasks` not in `_seenTaskIds` are queued. First becomes current with fresh 30s, rest go to queue, bottom sheet opens, alarm starts.

### Accept Button Progress

The accept button in `DeliveryTaskAlertCard` (bottom sheet) uses a `Box` with layered backgrounds:
- Outer Box: `Gray500.copy(alpha = 0.2f)` track (always visible)
- Inner Box: `Primary` colored, `fillMaxWidth(fraction = progress)` aligned to start
- Text overlaid on center

The accept button in `PendingDeliveryTaskItem` (AvailableTasksScreen) uses the same visual style but without the progress drain (static `Primary` background).

### Direction Display (Origin → Destination)

Both `PendingDeliveryTaskItem` and `NewDeliveryTaskBottomSheet` show pickup/dropoff addresses using a vertical connector layout:

```
● Origin address         (Primary dot, vertically centered with text)
│                        (vertical line, padding-start = 4dp, 16dp height)
● Destination address    (Gray500 dot, vertically centered with text)
```

- Each address is a `Row(verticalAlignment = CenterVertically)` with the dot + text on the same line
- Vertical line is a 2dp-wide `Box` between the rows, offset with `padding(start = 4dp)` to align with dot centers
- Dot shape: `RoundedCornerShape(5.dp)` (consistent across both components)

### Summary Card

- **Count**: `waitingDeliveryTasks.size` (live Firebase count — always reflects reality)
- **Shows when**: `bottomSheetMinimized == true` OR `(currentAlertTask == null && waitingDeliveryTasks.isNotEmpty())`
- **Tap**: Navigates to `AvailableTasksScreen`

## Files Modified

| File | Change |
|------|--------|
| `presentation/home/presentation/HomeViewState.kt` | Replaced `activeAlertTasks` with `currentAlertTask: DeliveryTask?`, added `alertQueue: List<DeliveryTask>`, removed `expiredWaitingTasks` |
| `presentation/home/presentation/HomeAction.kt` | Removed `DismissNewTaskAlert`; added `MinimizeBottomSheet`, `NavigateToAvailableTasks` |
| `presentation/home/presentation/HomeViewModel.kt` | Queue-based single-task system, `_seenTaskIds` persistence via SharedPreferences, `popNextTaskFromQueue()`, cleanup of stale IDs, updated accept/expire/minimize handlers |
| `presentation/home/presentation/HomeScreen.kt` | Removed bottom sheet + flash overlay (moved to AppNavHost), changed `ScreenFlashOverlay` from `private` to `internal` |
| `presentation/home/presentation/components/TasksContent.kt` | Summary card uses `waitingDeliveryTasks.size`, updated show condition |
| `presentation/home/presentation/components/NewDeliveryTaskBottomSheet.kt` | Accepts single `DeliveryTask` instead of list, removed LazyColumn |
| `presentation/home/presentation/components/PendingDeliveryTaskItem.kt` | Restyled to match bottom sheet card: Primary/Gray500 dots, no elevation, simplified fees layout, added notes display, Box-based accept button |
| `presentation/home/presentation/components/AvailableOrdersSummaryCard.kt` | **New** - clickable card showing available order count |
| `presentation/home/presentation/availableTasks/AvailableTasksScreen.kt` | **New** - screen listing waiting tasks with accept buttons |
| `presentation/home/presentation/availableTasks/AvailableTasksViewModel.kt` | **New** - ViewModel for available tasks screen |
| `presentation/home/di/HomeModule.kt` | Registered `AvailableTasksViewModel` in Koin |
| `presentation/nav/AppNavHost.kt` | Added `AvailableTasks` route + NavEntry, hoisted `HomeViewModel` to nav-level, added global bottom sheet overlay |
| `base/ext/OrderAlarmSound.kt` | **New** - looping alarm sound using MediaPlayer + system alarm URI |
| `base/di/hapticsModule.kt` | Registered `OrderAlarmSound` in Koin |
| `res/values/strings.xml` | Added `orders_available_to_take`, `available_orders` |
| `res/values-ar/strings.xml` | Added Arabic translations |

## Key Decisions

- **Queue-based single-task bottom sheet**: Instead of showing all tasks at once, one task is displayed with others queued behind it. Each gets a fresh 30s when it becomes current. Simpler UX, less overwhelming.
- **Persistent seen tracking via SharedPreferences**: Tasks explicitly handled (accepted, expired, minimized) are persisted and never re-alert, even after app restart. Only genuinely new tasks trigger alerts.
- **Global bottom sheet at AppNavHost level**: Bottom sheet is rendered at the navigation root, not inside `HomeScreen`. This ensures it appears on ANY screen after login (AvailableTasks, TaskDetails, etc.).
- **No `AssignedItem` sealed interface**: The unified list uses separate `items` blocks in LazyColumn with different key prefixes (`app_`, `delivery_`) instead of a sealed wrapper class. Simpler, same result.
- **Per-task timer via timestamps map**: Instead of per-task coroutines, a single timer coroutine checks the current task timestamp every 500ms.
- **`AvailableTasksScreen` has its own ViewModel**: Re-fetches from the same `IHomeRepository.listenToNewDeliveryTasks()`. Independent of `HomeViewModel` lifecycle.
- **Optimistic removal on accept**: Task removed from UI immediately, `getAssignedDeliveryOrders()` refreshes in background.
- **All Text uses named styles from `base/styles.kt`**: No inline `fontSize`/`fontWeight`/`MaterialTheme.typography`. Button text uses `smMedium.copy(color = Base_white)`, headers use `lgSemiBold`.
- **Consistent card styling**: `PendingDeliveryTaskItem` and `DeliveryTaskAlertCard` share the same visual style (Primary/Gray500 dots, flat card, same button style) for a unified look.

## String Resources

| Key | English | Arabic |
|-----|---------|--------|
| `orders_available_to_take` | You have %1$d orders available to take | لديك %1$d طلبات متاحة للاستلام |
| `available_orders` | Available Orders | الطلبات المتاحة |
