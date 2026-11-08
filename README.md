# 📸 SnapTile

<p align="center">
  <img src="assets/logo.png" width="96" height="96" alt="SnapTile Logo" />
</p>

<p align="center">
  <b>Native Android Quick Settings & Floating Screenshot Utility</b><br>
  <i>Take native screenshots without physical hardware buttons via Quick Settings or a draggable floating camera button.</i>
</p>

<p align="center">
  <b>🇬🇧 English</b> | <a href="README_TR.md">🇹🇷 Türkçe</a>
</p>

<p align="center">
  <a href="https://github.com/akiffurkan/snaptile/releases"><img src="https://img.shields.io/github/v/release/akiffurkan/snaptile?color=blue&style=flat-square" alt="Release"></a>
  <img src="https://img.shields.io/badge/Platform-Android_9.0%2B_(API_28%2B)-green?style=flat-square" alt="Platform">
  <img src="https://img.shields.io/badge/Root-Not_Required_(No_Root)-brightgreen?style=flat-square" alt="No Root">
  <img src="https://img.shields.io/badge/License-MIT-orange?style=flat-square" alt="License">
</p>

---

## 🎯 Primary Objective

The primary motivation behind **SnapTile** is to solve a very common hardware frustration:
- **Legacy and older Android smartphones** often suffer from **worn-out, stiff, or completely broken hardware Power / Volume buttons**.
- Taking a standard screenshot (typically requiring pressing `Power + Volume Down` simultaneously) becomes nearly impossible without third-party workarounds.
- Existing screenshot utilities often require **Root access**, rely on resource-heavy background screen recorders (`MediaProjection`), or require ADB debugging commands.
- **SnapTile fixes this natively:** It triggers Android's official system-level screenshot global action (`AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT`) introduced in Android 9 (API 28). No root, no background screen recording, and no hardware keys required.

---

## 📱 Device Compatibility & Testing

### 🔬 Verified & Tested Hardware (Tested Devices)
- **Samsung Galaxy S10+** — **Android 12 (One UI 4.1)**  
  *(Extensively tested on hardware with a defective physical power button. Verified smooth notification shade collapse, native Samsung shutter animation, smart capture toolbar, and immediate automatic saving to Samsung Gallery.)*

### 📲 Supported Android Versions
SnapTile is fully compatible with Android 9.0 Pie up to Android 15:
- ✅ **Android 9.0 Pie** (API 28)
- ✅ **Android 10** (API 29) — One UI 2.x
- ✅ **Android 11** (API 30) — One UI 3.x
- ✅ **Android 12 & 12L** (API 31/32) — One UI 4.x
- ✅ **Android 13** (API 33) — One UI 5.x
- ✅ **Android 14** (API 34) — One UI 6.x
- ✅ **Android 15** (API 35)

### 🏢 Supported Manufacturers & ROMs
- **Samsung** (One UI)
- **Xiaomi / Redmi / POCO** (MIUI / HyperOS)
- **Google Pixel** (Stock Android)
- **OPPO / Realme / OnePlus** (ColorOS / OxygenOS)
- **Vivo / iQOO** (Funtouch OS / OriginOS)
- **Motorola, ASUS, Sony, Huawei** and all other standard Android 9+ devices.

---

## ⚡ Key Features

1. **Quick Settings (QS) Tile Button:**
   - Dedicated **"Ekran Görüntüsü" (Screenshot)** tile in your notification drawer featuring a crisp camera glyph.
   - **Smart Shade Collapse:** When tapped, it uses `GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE` (on Android 12+) to collapse the notification shade before capturing, ensuring a clean shot of your active screen.

2. **Draggable Floating Camera Button:**
   - A sleek, floating circular camera button always accessible on your screen.
   - **Freely Draggable:** Drag and place it anywhere along your screen edges.
   - **Smart Auto-Hide:** When tapped, the button disappears for 120 ms while Android captures the screen, and then reappears immediately—**the button never shows up in your screenshots!**
   - **Long-Press to Dismiss:** Press and hold the button to reveal a quick **"✕ Hide"** action to close it anytime.
   - **In-App Toggle:** Re-enable the floating button at any time with a single toggle inside the SnapTile dashboard.

3. **100% Native System Experience:**
   - Android and Samsung handle the entire screenshot process natively.
   - Preserves native shutter sounds, preview thumbnails, scroll capture, and quick edit options.
   - Directly stored in your device's standard `DCIM/Screenshots` folder.

4. **Zero Permissions & Absolute Privacy:**
   - ❌ **NO Internet Permission** (`android.permission.INTERNET` is not even declared).
   - ❌ **NO Storage Reading Permission**.
   - ❌ **NO Screen Recording / MediaProjection**.
   - ❌ **NO Analytics, Tracking, or Advertisements**.
   - The Accessibility Service configuration specifically sets `canRetrieveWindowContent = false` and `canPerformGestures = false`.

---

## 📥 Installation & Setup

1. Download the latest `SnapTile-v1.0.0.apk` from the [**Releases**](https://github.com/akiffurkan/snaptile/releases) section.
2. Install the APK on your device (allow install from unknown sources if prompted).
3. Open **SnapTile**, tap **"Erişilebilirliği Etkinleştir" (Enable Accessibility)**, and turn on the **SnapTile Screenshot Service**.
4. Choose your preferred capture mode:
   - **Quick Settings Tile:** Swipe down your notification panel twice, tap **Edit / Button Order**, and drag the **SnapTile Camera Button** into your active tiles.
   - **Floating Button:** Turn on the **"Yüzen Ekran Butonu"** switch in the app (grants Display over other apps permission).

---

## 🛠️ Building from Source

```bash
# Clone the repository
git clone https://github.com/akiffurkan/snaptile.git
cd snaptile

# Build debug APK on Windows
gradlew.bat assembleDebug

# Build debug APK on Linux/macOS
./gradlew assembleDebug
```

Output APK location:  
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
