# Modern Calculator

A clean, modern calculator app for Android built entirely with **Jetpack Compose**. Inspired by minimal calculator UIs with full light and dark theme support.

## Features

- ➕ Basic arithmetic — addition, subtraction, multiplication, division
- 🔢 Correct order of operations (multiplication/division before addition/subtraction)
- 💬 Live expression display with automatic thousands-separator formatting (e.g. `4,840 + 120 / 30`)
- ➖ Toggle positive/negative sign on the current number
- 🌗 Light and dark theme, switchable from the in-app menu
- ⛶ Fullscreen (immersive) mode toggle
- ⌫ Backspace and full clear
- 📱 Built with Material 3 and edge-to-edge display support

## Tech Stack

- **Kotlin**
- **Jetpack Compose** (declarative, code-based UI — no XML layouts)
- **Material 3** components and theming
- Minimum SDK: API 28 (Android 9.0 "Pie")

## Project Structure

```
app/src/main/java/com/example/modernappcalculator/
├── MainActivity.kt          # UI (Compose screens, keypad, top bar)
├── CalculatorEngine.kt       # Expression parsing & calculation logic
└── ui/theme/
    ├── Color.kt
    ├── Theme.kt
    └── Type.kt
```

## Getting Started

1. Clone the repository
   ```
   git clone https://github.com/clarxxinn/Modern-Calculator.git
   ```
2. Open the project in **Android Studio**
3. Let Gradle sync
4. Run on an emulator or physical device (API 28+)

## License

This project is open for personal and educational use.
