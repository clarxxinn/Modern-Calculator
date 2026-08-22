# Modern Calculator

A clean, modern calculator app for Android built entirely with **Jetpack Compose**. Inspired by minimal calculator UIs, it features a fully working arithmetic engine, live expression formatting, and both light and dark themes — all built with 100% code-based UI (no XML layouts).

## 📖 About the Project

Modern Calculator is a native Android app that replicates the core experience of an everyday calculator app while showcasing modern Android development practices. Rather than using the traditional XML `View` system, the entire interface — from the keypad to the top navigation bar to the number display — is written declaratively in Kotlin using Jetpack Compose.

The app was built from scratch starting with an Android Studio "Empty Activity" (Compose) template, and every screen, button, and piece of logic was custom-built:

- 🧮 The **calculator engine** is a hand-written recursive-descent expression parser (no third-party math libraries), so it correctly respects order of operations (e.g. `4840 + 120 / 30` evaluates the division first).
- ⚡ The **UI** is fully reactive — Compose automatically re-renders the expression and result the moment the underlying state changes, with no manual refresh logic needed.
- 🌗 The app supports **both Android's light and dark aesthetics**, matching a two-panel reference design (one light, one dark) pixel-for-pixel in layout.

## ✨ Features

### Core Calculation
- ➕ **Basic arithmetic**: addition, subtraction, multiplication (×), division (÷)
- 🔢 **Correct order of operations**: multiplication and division are evaluated before addition and subtraction, just like a real calculator (not left-to-right)
- 🔟 **Decimal support**: type a decimal point mid-number, with automatic protection against multiple decimal points in the same number
- ➖ **Sign toggle**: flip the current number between positive and negative with a dedicated key
- 🚫 **Division-by-zero handling**: gracefully caught instead of crashing the app

### Display & Formatting
- 💬 **Live expression display**: shows exactly what you've typed at the top of the screen, e.g. `4,840 + 120 / 30`
- 🔢 **Automatic thousands separators**: numbers are formatted with commas as you type (`4840` → `4,840`) without affecting the underlying math
- 🟰 **Result only on "="**: the calculated answer appears only after pressing the equals key, keeping the display uncluttered while you're still typing
- 🔁 **Continue from result**: after pressing "=", typing an operator continues the calculation from the previous result (like a real calculator)

### Interface & Controls
- 🧹 **Clear (C)**: resets the entire expression
- ⌫ **Backspace**: deletes one character at a time
- 🌗 **Light / Dark theme toggle**: switch instantly from the top-right menu, no restart required
- ⛶ **Fullscreen (immersive) mode**: hide the status bar and navigation bar for a distraction-free view, toggled from the top bar icon
- 📱 **Material 3 design**: modern rounded buttons, proper color theming, and edge-to-edge display support

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| 💻 Language | Kotlin |
| 🎨 UI Toolkit | Jetpack Compose (declarative, code-based UI) |
| 🧩 Design System | Material 3 |
| 🏗️ Architecture | Single-Activity, Compose state-driven (no XML) |
| 📦 Minimum SDK | API 28 (Android 9.0 "Pie") |
| 🔧 Version Control | Git, GitHub |

## 🏗️ How It Works (Architecture)

```
app/src/main/java/com/example/modernappcalculator/
├── MainActivity.kt          # All UI: top bar, expression/result display, keypad layout
├── CalculatorEngine.kt       # CalculatorState (reactive state) + expression parser/evaluator
└── ui/theme/
    ├── Color.kt               # Color palette for light & dark themes
    ├── Theme.kt                # MaterialTheme wrapper supporting theme switching
    └── Type.kt                 # Typography definitions
```

**`CalculatorEngine.kt`** contains two main pieces:

1. 🧠 **`CalculatorState`** — holds the current expression and result as Compose `State`, so any change automatically triggers a UI redraw. It exposes functions like `onDigit()`, `onOperator()`, `onDecimal()`, `onBackspace()`, `onClear()`, `onToggleSign()`, and `onEquals()` that the UI calls whenever a button is tapped.
2. 🔍 **`ExpressionParser`** — a small recursive-descent parser that evaluates the raw expression string (e.g. `"4840+120/30"`) while correctly handling operator precedence, decimals, and unary minus.

**`MainActivity.kt`** builds the entire visual layout using Compose functions — a top bar with theme/fullscreen controls, an expression/result `Text` display, and a 4-column keypad grid built from reusable composables (`DigitKey`, `OperatorKey`, `FunctionKey`, `IconKey`).

## 🚀 Installation

To get started with the project, follow these steps:

1. Clone the repository to your local machine:

```
git clone https://github.com/clarxxinn/Modern-Calculator.git
```

2. Open the project folder in **Android Studio**.
3. Let Gradle sync and automatically download the required dependencies.
4. Run the app on an emulator or a physical Android device (API 28 or higher).

## 📱 Usage

1. Open the app on your Android device or emulator.
2. Tap number and operator keys to build your expression — it appears live at the top of the screen.
3. Use the sign-toggle key to switch the current number between positive and negative.
4. Tap **"="** to calculate and reveal the result.
5. Use **"C"** to clear everything, or the backspace key to remove the last character.
6. Tap the **⋮** menu to switch between light and dark theme.
7. Tap the fullscreen icon to hide the status/navigation bars for an immersive view.

## 🤝 How to Contribute

Contributions are welcome! To contribute:

1. Fork the repository.
2. Create a new branch for your changes.
3. Make your changes and commit them.
4. Push your branch to your forked repository.
5. Submit a pull request with a description of your changes.

## 📄 License

This project is open for personal and educational use.
