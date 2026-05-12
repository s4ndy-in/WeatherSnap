# WeatherSnap 🌤️

A polished Android app that lets users search live weather for any city, capture photo evidence using a custom CameraX screen, compress the image, add notes, and save/view weather reports locally.

---

## Tech Stack

- **Kotlin** + **Jetpack Compose**
- **MVVM** architecture with **ViewModel** + **StateFlow**
- **Hilt** for dependency injection
- **Retrofit** + **Gson** + **OkHttp** for networking
- **Room Database** for local persistence
- **CameraX** for custom camera
- **Navigation Compose** for screen navigation
- **Coroutines** for async operations
- **Material 3** for UI
- **Coil** for image loading

---

## API

Uses [Open-Meteo](https://open-meteo.com/) — **no API key required**.

| Endpoint | Purpose |
|----------|---------|
| `https://geocoding-api.open-meteo.com/v1/search` | City autocomplete |
| `https://api.open-meteo.com/v1/forecast` | Current weather data |

---

## App Screens

| Screen | Description |
|--------|-------------|
| **Weather Screen** | Search city, view weather, navigate to report |
| **Create Report Screen** | Capture photo, add notes, save report |
| **Custom Camera Screen** | CameraX live preview, capture & compress image |
| **Saved Reports Screen** | View all saved reports with image, weather, notes, sizes |

---

## Prerequisites

Before running the project, make sure you have:

- **Android Studio** Hedgehog (2023.1.1) or newer
- **JDK 11** or higher
- **Android SDK** with min API level 24 (Android 7.0)
- A physical device or emulator with **camera support**
- Internet connection (for weather API calls)

---

## Setup & Run Steps

### 1. Clone or Extract the Project

**If using GitHub:**
```bash
git clone https://github.com/yourusername/WeatherSnap.git
cd WeatherSnap
```

**If using ZIP file:**
- Extract the ZIP
- Note the folder path

---

### 2. Open in Android Studio

1. Open **Android Studio**
2. Click **File → Open**
3. Navigate to the project folder and click **OK**
4. Wait for **Gradle sync** to complete (this may take a few minutes on first open)

---

### 3. Verify Gradle Sync

Once the project opens, Android Studio will automatically start syncing. If it doesn't:

- Click **File → Sync Project with Gradle Files**

You should see **BUILD SUCCESSFUL** in the Build output panel.

> If you see Hilt/kapt errors, go to **File → Invalidate Caches → Invalidate and Restart**

---

### 4. Set Up Emulator or Connect a Device

**Option A — Physical Device:**
1. Enable **Developer Options** on your Android phone
2. Enable **USB Debugging**
3. Connect via USB
4. Allow USB debugging when prompted

**Option B — Emulator:**
1. Open **Device Manager** in Android Studio (right sidebar)
2. Click **Create Device**
3. Choose any phone (e.g. Pixel 6), API 28 or higher
4. Make sure the emulator has **camera support** enabled (it does by default)

---

### 5. Run the App

1. Select your device/emulator from the top device dropdown
2. Click the green **Run ▶** button (or press `Shift + F10`)
3. The app will build and install automatically
4. On first launch, **allow camera permission** when prompted

---

## Full App Flow (for Screen Recording)

1. **Open app** → Weather Screen loads
2. **Type a city name** (more than 2 letters) → suggestions appear with animation
3. **Select a city** → weather loads (temperature, condition, humidity, wind, pressure)
4. **Tap "Create Report"** → navigates to Create Report Screen
5. **Tap "Capture Photo"** → Custom Camera Screen opens with live preview
6. **Tap "Capture"** → photo taken, compressed, returned to report screen
7. **View original vs compressed size** shown below the image preview
8. **Enter notes** in the Field Notes section
9. **Tap "Save Report"** → saved to Room DB, navigates to Saved Reports Screen
10. **View saved report** with image, weather, notes, sizes, and timestamp

---

## Bonus Features Included

- **Debug-only network logging** via OkHttp `HttpLoggingInterceptor` (only active in debug builds)
- **City suggestion caching** — same query never hits the API twice
- **Image compression** — original vs compressed size displayed on both Create Report and Saved Reports screens

---

## Notes

- No API key is required — Open-Meteo is completely free
- All reports are saved locally on the device using Room DB
- Images are stored in the app's internal files directory
- The app does **not** use the device camera intent — it uses a fully custom CameraX implementation
- No mock/hardcoded weather data is used anywhere
