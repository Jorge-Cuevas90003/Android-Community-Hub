# Android Community Hub

![App Demo](app-demo.gif)

## 📖 Overview

**Android Community Hub** is a native Android application developed as the final project for a Software Engineering course. It serves as a comprehensive management tool for residential communities, designed to streamline communication, enhance transparency, and centralize administrative tasks.

This project addresses common neighborhood challenges such as disorganized decision-making, a lack of financial transparency, and difficult access to official documents. It showcases the application of software engineering principles—from requirements to deployment—using modern Android development practices and a real-time cloud backend.

---

## ✨ Core Features

*   **Secure User Authentication**: A robust system allowing residents to register and log in with a unique house number and password.
*   **Democratic Proposal & Voting System**: A dynamic, real-time feature where residents can submit proposals and vote on them. The system tracks votes, expiration dates, and automatically updates the proposal's status.
*   **Cloud-Based Document Viewer**: An in-app PDF viewer for accessing community documents like official regulations. The viewer fetches documents directly from a shared **Google Drive link**, ensuring easy updates without modifying the app.
*   **Financial Transparency Modules**:
    *   **Budget Overview**: Displays the monthly budget in a user-friendly, expandable list format.
    *   **Account Statements**: Allows residents to check their monthly payment status.
*   **Modern & Intuitive UI**: A clean interface built with Google's Material Design components, including `CardView`, `RecyclerView`, and `DialogFragment`.
*   **Firebase Integration**: Leverages Firebase for its Realtime Database and Cloud Messaging capabilities for future push notifications.

---

## 🛠️ Tech Stack & Architecture

*   **Language**: **Java**
*   **IDE**: Android Studio
*   **Backend & Database**: **Firebase Realtime Database** for all data storage.
*   **Document Hosting**: **Google Drive** for hosting PDF files, allowing for dynamic content management.
*   **Key Android Components**:
    *   `AppCompatActivity`, `DialogFragment`
    *   `RecyclerView` & `CardView` for displaying dynamic lists.
    *   `ViewPager2` with a custom `PdfPageAdapter` and `PdfRenderer` for the document viewer.
*   **Security**: Sensitive configuration files like `google-services.json` are explicitly excluded from the repository via `.gitignore` to maintain security best practices.

---

## 🚀 How to Set Up and Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Jorge-Cuevas90003/Android-Community-Hub.git
    ```
2.  **Firebase Setup**:
    *   Create a new project on the [Firebase Console](https://console.firebase.google.com/).
    *   Add an Android app with the package name `com.example.myhouse`.
    *   Download the generated `google-services.json` file and place it in the project's `/app` directory. This file is intentionally ignored by Git and will not be committed.
3.  **Database & Storage Setup**:
    *   In Firebase, create a Realtime Database and configure its security rules.
    *   Populate the database with the required initial structure (e.g., `/Casas` for users, `/Reglamento/Link` with a public Google Drive URL to a PDF).
4.  **Build and run** the app from Android Studio.

---

