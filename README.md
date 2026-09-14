# Hourly Task Planner & Smart Event Reminder (Android)

A production-grade Android application built with **Modern Android Development (MAD)** standards. 

## Features
- **Hourly Planner Check-ins (6:00 AM - 11:00 PM)**: Sends notifications every hour to prompt daily focus and progress updates using battery-friendly exact wake-alarms (`AlarmManager.setExactAndAllowWhileIdle`).
- **Gmail Integration & Smart Event Detection**: Automatically scans your connected Gmail account in the background using `WorkManager` for webinars, bootcamps, and workshops.
- **30-Minute Prior Reminders**: Automatically calculates and schedules alerts 30 minutes before any detected session starts, complete with direct "Join Meeting" quick actions.
- **Reboot Resilience**: Restores all alarm queues and schedules automatically upon device reboot (`RECEIVE_BOOT_COMPLETED`).
- **Modern Jetpack Compose UI**: Clean, responsive Material 3 interface with dark mode support.

## Tech Stack
- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose + Material 3
- **Local Persistence**: Room Database (SQLite with Kotlin Coroutines & Flow)
- **Background Processing**:
  - `AlarmManager`: Exact alarms waking from Doze mode (`RTC_WAKEUP`)
  - `WorkManager`: Periodic Gmail synchronization constrained by network & battery state
- **API Client**: Google API Client for Java / Gmail REST API
- **Architecture**: Clean Architecture + MVVM

## Setup Instructions

### 1. Push to GitHub (`patilsagar28290`)
To link and push this codebase to your GitHub repository:
```bash
git init
git add .
git commit -m "feat: initial commit of Hourly Task Planner Android app"
git branch -M main
git remote add origin https://github.com/patilsagar28290/task-planner-app.git
git push -u origin main
```
*(Ensure you have created the empty repository `task-planner-app` on GitHub first).*

### 2. Google Cloud Platform & Gmail OAuth Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project and enable the **Gmail API**.
3. Under **OAuth consent screen**, configure external user consent and add the scope `https://www.googleapis.com/auth/gmail.readonly`.
4. Create an **Android OAuth 2.0 Client ID** with:
   - Package name: `com.taskplanner.app`
   - SHA-1 fingerprint of your debug/release signing key.
5. Place the generated credentials into your app's configuration.

### 3. Permissions Handled
- `POST_NOTIFICATIONS` (Android 13+)
- `SCHEDULE_EXACT_ALARM` (Android 12+)
- `RECEIVE_BOOT_COMPLETED`
- `INTERNET`
