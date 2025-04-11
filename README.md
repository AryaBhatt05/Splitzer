# Splitzer - Android Receipt Splitting App

## Overview
Splitzer is an Android application designed to simplify the process of splitting bills and expenses among friends and groups. The app uses advanced technologies like ML Kit for text recognition and Firebase for real-time data synchronization to provide a seamless experience for managing shared expenses.

## Technologies Used
- **Android Development**
  - Kotlin
  - Android SDK
  - AndroidX Libraries
  - Material Design Components
  - Navigation Components
  - CameraX API

- **Backend & Authentication**
  - Firebase Authentication
  - Firebase Realtime Database
  - Firebase Analytics

- **Machine Learning & Image Processing**
  - ML Kit Text Recognition
  - CameraX for image capture
  - Glide for image loading

- **UI/UX**
  - Material Design 3
  - RecyclerView
  - ConstraintLayout
  - Navigation Components

## Prerequisites
- Android Studio (latest version recommended)
- Java Development Kit (JDK) 11 or higher
- Android SDK (API level 24 or higher)
- Google Firebase account
- Physical Android device or emulator

## Setup Instructions

### For Windows
1. Clone the repository:
   ```bash
   git clone https://github.com/AryaBhatt05/Splitzer
   ```

2. Open Android Studio
3. Select "Open an existing project" and navigate to the cloned repository
4. Wait for the project to sync and download dependencies
5. Connect your Android device or start an emulator
6. Click the "Run" button (green play icon) to build and run the app

### For macOS
1. Clone the repository:
   ```bash
   git clone https://github.com/AryaBhatt05/Splitzer
   ```

2. Open Android Studio
3. Select "Open an existing project" and navigate to the cloned repository
4. Wait for the project to sync and download dependencies
5. Connect your Android device or start an emulator
6. Click the "Run" button (green play icon) to build and run the app

## Firebase Setup
1. Create a new Firebase project in the [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the Firebase project
3. Download the `google-services.json` file and place it in the `app` directory
4. Enable Authentication and Realtime Database in the Firebase Console

## Features
- Receipt scanning and text recognition
- Real-time expense splitting
- Group management
- User authentication
- Expense history tracking
- QR code scanning for quick group joining

## Project Structure
- `app/src/main/java/` - Contains all Kotlin source files
- `app/src/main/res/` - Contains resources (layouts, strings, drawables)
- `app/build.gradle.kts` - App-level build configuration
- `build.gradle.kts` - Project-level build configuration

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License
This project is licensed under the terms specified in the LICENSE file.

## Support
For support, please open an issue in the GitHub repository or contact the development team.

## Acknowledgments
- Android Jetpack libraries
- Firebase team
- Google ML Kit
- Open source community 