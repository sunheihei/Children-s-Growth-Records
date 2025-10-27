## ChildGrowthTracking

A simple and lightweight Android app built in Jetpack Compose for tracking children’s growth metrics — adding and editing child profiles, capturing measurements, and setting unit preferences.

## 🧒 Overview

ChildGrowthTracking allows parents or caregivers to:

- Create child profiles with name, gender, avatar, birthday and notes.

- Edit existing child records with updated details.

- Choose measurement units (height, weight, head circumference) via settings.

- Persist data using a local data store and/or repository.

- Use a modern Android architecture: ViewModel, Flow, StateFlow, Compose UI, Hilt for DI.

## 🔧 Features

- Add / edit child profile interface (avatar selection, gender toggle, birthday picker, notes).

- Data retrieval and update via ViewModel and repository (supports both “add” and “edit” modes).

- DataStore preferences for unit settings (height unit, weight unit, head circumference unit, first‐launch, etc.).

- Responsive UI made with Jetpack Compose (Material 3, SegmentedButton, DatePicker dialog).

- Clean architecture separating UI layer (Composables) and business/data logic (ViewModels, Repositories, DataStore).

## 🚀 Tech Stack

- Kotlin

- Jetpack Compose

- ViewModel + Kotlin Coroutines + Flow / StateFlow

- Hilt (for dependency injection)

- DataStore (for settings persistence)

- Room (or custom repository) for child profile storage *(adjust depending on your implementation)*

- Material 3 UI components

## Getting Started

1. Clone the repository:
   
   `git clone https://github.com/sunheihei/ChildGrowthTracking.git`

2. Open it in Android Studio (Arctic Fox or later recommended).

3. Update your `applicationId`, version, and any settings as needed.

4. Build and run the app on an Android device or emulator (API level X+).

5. Customize the UI, add new features, or adjust the data model for your use case.

 

## 📄 License

This project is licensed under the MIT License — feel free to use, modify, and distribute it.
