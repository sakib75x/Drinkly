# Drinkly — The Complete Beginner's Guide

Welcome! This guide takes you from **"I just downloaded a ZIP"** to
**"my app is running on my phone (or emulator) and I have an APK"**.

It is written for a complete beginner using **Android Studio Quail 3
(2026.1.3) on Windows**. Follow the steps top to bottom. Every step tells you
what you should see and what to do if something looks different.

---

## 1. What you need

| Thing | Do you have it? |
|---|---|
| Android Studio Quail 3 (2026.1.3) | ✅ Yes — you have it |
| Java (JDK) | Comes **bundled inside** Android Studio — nothing to install |
| Android SDK | Android Studio installs it (or asks you to) the first time |
| Internet connection | Needed for the first build (downloads libraries) |
| A phone **or** an emulator | Optional, but needed to actually run the app |

You do **not** need to install Gradle, Kotlin, or anything else by hand.
Android Studio handles all of that for you.

---

## 2. Unzip the project

1. Find `Drinkly-Android-Studio.zip` on your computer.
2. Right-click it → **Extract All...** → choose a destination such as
   `C:\Users\YOURNAME\AndroidStudioProjects\`.
3. After extraction you should have a folder named **`Drinkly`** containing
   files like `settings.gradle.kts`, `build.gradle.kts`, `app`, `gradle`,
   `README.md`, and `BEGINNER_GUIDE.md`.

> ⚠️ **Check:** you want `Drinkly\settings.gradle.kts` directly inside the
> folder. If you instead see `Drinkly\Drinkly\...` (a folder inside a
> folder), just cut the inner `Drinkly` folder out and use that one.

---

## 3. Open the project in Android Studio

1. Start Android Studio.
2. On the welcome screen click **Open** (or go to **File > Open**).
3. Navigate to the `Drinkly` folder. It shows an **Android robot icon**.
   Select it and click **OK**.
4. If Android Studio asks **"Trust Project?"**, click **Trust Project**.
5. If it asks about generating a **Gradle wrapper**, you can click
   **OK / Yes** — it is optional and is only needed for command-line builds
   (this project intentionally ships without wrapper files to stay tiny).

Now wait. Android Studio is about to do its first **Gradle sync** — see the
next step.

---

## 4. The first Gradle sync (be patient)

Gradle is the tool that builds your project. A **sync** reads the build
files, downloads all the libraries, and prepares everything.

This project uses these exact versions (all stored in one place,
`gradle/libs.versions.toml`):

| Tool | Version |
|---|---|
| Android Gradle Plugin | 8.7.3 (needs Gradle 8.9 or newer) |
| Kotlin | 2.0.21 |
| Compose UI / Material 3 | BOM 2024.12.01 |
| DataStore Preferences | 1.1.1 |
| WorkManager | 2.10.0 |
| compileSdk / targetSdk / minSdk | 35 / 35 / 24 |
| Java | 17 |

**What you will see:** a progress bar at the bottom of the window and a
balloon saying **"Gradle sync finished"** when it is done.

- First sync can take **5–15 minutes** (it downloads dependencies). Later
  syncs take seconds.
- It is normal for the Windows firewall to ask about Java/Gradle — allow it.

### If the sync shows an error

| Error message | What it means | Fix |
|---|---|---|
| `SDK location not found` | Android Studio can't find the Android SDK | **File > Project Structure > SDK Location** → set the path (e.g. `C:\Users\YOURNAME\AppData\Local\Android\Sdk`) → OK. Or edit `local.properties` (see below). |
| `Minimum supported Gradle version is 8.9` | The Gradle version being used is too old | **File > Settings > Build, Execution, Deployment > Build Tools > Gradle** → set the Gradle distribution to **8.9 or newer** (e.g. 8.14) → OK → sync again. |
| `Could not resolve ... com.android.tools.build:gradle:8.7.3` | Network problem downloading libraries | Check your internet connection; if you're behind a proxy: **Settings > Appearance & Behavior > System Settings > HTTP Proxy** → "Auto-detect proxy settings" → OK → sync again. |
| Java-related errors | Wrong JDK selected | **File > Project Structure > SDK Location > JDK** → choose the embedded JDK (17 or newer) → OK. |

**About `local.properties`:** this file stores your SDK path and is
machine-specific. It ships almost empty on purpose — Android Studio normally
writes the correct `sdk.dir` line for you. If sync still complains, open
`local.properties` and add your own line:

```
sdk.dir=C:/Users/YOURNAME/AppData/Local/Android/Sdk
```

(replace `YOURNAME` with your Windows user name — forward slashes are fine).

---

## 5. Run the app

### Option A — Emulator (no phone needed)

1. Click the **Device Manager** icon (a small phone, on the right toolbar).
2. Click **+** → **Create device**.
3. Pick a phone, e.g. **Pixel 8**, click **Next**.
4. Choose a system image (e.g. **API 34 or 35**) → **Download** if needed →
   **Next** → **Finish**.
5. Back in the main window, make sure your virtual device appears in the
   dropdown next to the **Run ▶** button.
6. Click **Run ▶** (or press **Shift+F10**).

### Option B — Real Android phone

1. On your phone: **Settings > About phone** → tap **"Build number"**
   **7 times** until it says you are a developer.
2. Go to **Settings > System > Developer options** and turn on
   **USB debugging**.
3. Plug the phone into your PC with a USB cable. On the phone, accept the
   **"Allow USB debugging?"** prompt (tick "Always allow").
4. In Android Studio, choose your phone from the device dropdown and click
   **Run ▶**. (Windows usually installs the phone driver automatically; if
   your phone never appears, install its USB driver from the manufacturer.)

### What you should see

1. A **splash screen**: teal background with a white water drop.
2. The **Drinkly home screen**: the app name, a big progress ring showing
   **0 of 8 glasses**, an encouraging message, and buttons.
3. On Android 13+: a **"Allow Drinkly to send notifications?"** dialog —
   tap **Allow** so reminders work.

---

## 6. Use the app

- **Add glass** — counts one glass of water (ring fills up).
- **Remove** — subtracts one (never below zero).
- **Daily goal** — drag the slider between 1 and 16 glasses (default 8).
- **Water reminders** — switch on/off. When on, a notification is posted
  **every 3 hours between 08:00 and 21:00**. Your counter resets to 0 each
  new day automatically.
- **Reset today's count** — starts the day over.

All of this is saved with **DataStore** — close the app and reopen it; your
count, goal, and reminder setting are still there.

---

## 7. Build an APK (the installable file)

1. In the menu: **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
2. When it finishes, Android Studio shows a notification. Click **"locate
   in Explorer"** (or find it yourself) at:

```
Drinkly\app\build\outputs\apk\debug\app-debug.apk
```

3. **Install it on your phone:** copy the `.apk` file to the phone (USB
   cable, Google Drive, WhatsApp, etc.), tap it on the phone, and allow
   "install unknown apps" when asked. This debug APK is perfect for
   personal use and for sharing with friends.

> 💡 The `app\build` folder is created by Android Studio on your PC — it is
> not part of the project ZIP.

---

## 8. Release (signed) APK — optional

The debug APK is fine for almost everything. If you ever want a "proper"
signed APK (for example to upload somewhere):

1. **Build > Generate Signed App Bundle / APK...**
2. Choose **APK** → **Next**.
3. **Create new...** keystore: fill in a password and at least one name.
   ⚠️ **Keep the `.jks` file and passwords safe** — you need them for every
   future update.
4. Choose **release** → **Finish**.

Output: `Drinkly\app\release\app-release.apk`.
(Release builds currently have minification turned off, so they behave
exactly like debug builds — see `app/build.gradle.kts`.)

---

## 9. Project anatomy — what each file does

| File | What it does |
|---|---|
| `settings.gradle.kts` | Project name, where to find libraries, the `:app` module |
| `build.gradle.kts` (root) | Declares the plugins used by the whole project |
| `gradle.properties` | Memory and AndroidX settings |
| `local.properties` | Your PC's Android SDK path (machine-specific) |
| `gradle/libs.versions.toml` | **Version catalog** — every version lives here |
| `app/build.gradle.kts` | App module: SDK levels, Compose enabled, dependencies |
| `AndroidManifest.xml` | App identity, launcher icon, notification permission |
| `MainActivity.kt` | Entry point; shows splash, asks for notification permission, schedules reminders |
| `data/PreferencesManager.kt` | Saves/loads count, goal, reminder setting (DataStore) |
| `notification/NotificationHelper.kt` | Creates the notification channel and posts reminders |
| `worker/WaterReminderWorker.kt` | Periodic background task (WorkManager) |
| `ui/theme/ModernTheme.kt` | Material 3 theme + teal color schemes |
| `ui/screens/HomeScreenModern.kt` | The entire user interface |
| `res/values/strings.xml` | All user-visible text |
| `res/values/themes.xml` | Splash + app themes |
| `res/drawable/*` | Vector icons (water drop, launcher layers) — **no PNGs** |
| `res/mipmap-anydpi*/` | Launcher icons (adaptive + vector fallback) |
| `res/xml/*` | Android backup rules |

---

## 10. Easy customizations

All in `app/src/main/...`:

| Change | File | Edit |
|---|---|---|
| Goal range (1–16) | `java/com/drinkly/app/data/PreferencesManager.kt` | `MIN_GOAL`, `MAX_GOAL` |
| Reminder frequency (3 h) | `java/com/drinkly/app/worker/WaterReminderWorker.kt` | `REPEAT_INTERVAL_HOURS` |
| Reminder window (8–21) | same file | `ACTIVE_START_HOUR`, `ACTIVE_END_HOUR` |
| Colors (teal palette) | `java/com/drinkly/app/ui/theme/ModernTheme.kt` | `LightColorScheme` / `DarkColorScheme` |
| App name & all texts | `res/values/strings.xml` | any string |
| App icon | `res/drawable/ic_launcher_background.xml` (teal) and `ic_launcher_foreground.xml` (white drop) | colors / shape |

---

## 11. How the reminder system actually works

1. On app start, `MainActivity` tells **WorkManager** to schedule a
   **periodic worker** (`WaterReminderWorker`) — every 3 hours, first run
   after 1 hour. WorkManager is Android's reliable background-task system;
   it survives app restarts and reboots.
2. Each time the worker runs it checks: are reminders enabled? Is it
   between 08:00 and 21:00? If yes, it reads your current glasses count,
   builds a message ("You still have 5 glasses to go. Drink up!"), and posts
   a **notification**.
3. Notifications use a **channel** ("Water reminders") created on
   Android 8+.
4. On **Android 13+** the app must ask for the **notification permission**
   at runtime — that's the dialog you see on first launch. If you denied it,
   enable it later at **Settings > Apps > Drinkly > Notifications**.

---

## 12. FAQ & troubleshooting

**Q: Where are the Gradle wrapper files (`gradlew`, `gradle-wrapper.jar`)?**
A: They were intentionally left out to keep the ZIP tiny. Android Studio
uses its own bundled Gradle, so you don't need them to build from the IDE.
If you want command-line builds later, Android Studio can generate the
wrapper for you when you first open the project (click "OK" on the prompt),
or you can keep using the **Build** menu.

**Q: "App not installed" when installing the APK on my phone?**
A: You already have another version installed. Uninstall it first, then
install again.

**Q: Notifications never appear.**
A: Check, in order: (1) the **Water reminders** switch is on in the app,
(2) notification permission is allowed in phone settings, (3) it is between
8:00 and 21:00, (4) the phone doesn't have aggressive battery
optimization killing the app.

**Q: The emulator is very slow.**
A: Enable hardware acceleration in the AVD settings, pick a device without
the highest resolution, and close other programs.

**Q: Sync gets stuck at "Downloading..."**
A: It's downloading dependencies — wait a few minutes. If it never finishes,
your antivirus/firewall may be blocking Gradle; add an exception and retry.
Restarting Android Studio also helps.

**Q: Can I just delete `local.properties`?**
A: Yes — Android Studio will recreate it with the correct SDK path on the
next sync. (It's listed in `.gitignore` for a reason.)

**Q: Why does my count reset every morning?**
A: That's a feature — the daily counter starts fresh each day. The reset
happens when the app is opened or a reminder fires on a new day.

---

## You're done! 🎉

You unzipped a project, opened it in Android Studio, synced it, ran it, and
built an APK. That's the full lifecycle of an Android app — everything else
is just doing more of this, with bigger apps.

For a quick overview, see [README.md](README.md).
