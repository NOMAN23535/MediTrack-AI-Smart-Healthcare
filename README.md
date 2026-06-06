# MediTrack AI

## MediTrack-AI-Smart-Healthcare

MediTrack AI is an AI-powered Android healthcare application that enables symptom analysis, appointment management, medicine reminders, digital prescriptions, and secure doctor-patient collaboration through an integrated healthcare ecosystem.

## App Information

| Property       | Details                    |
| -------------- | -------------------------- |
| App Name       | MediTrack AI               |
| Category       | Medical                    |
| Version        | 1.0                        |
| Platform       | Android                    |
| Language       | Java                       |
| Architecture   | MVVM                       |
| Database       | Firebase Realtime Database |
| Authentication | Firebase Authentication    |
| AI Integration | Groq API                   |
| Minimum SDK    | 24                         |
| Target SDK     | 36                         |

### App Assets

* App Icon: Available in the **Logo Icon** folder.
* Screenshots: Available in the **Screenshots** folder.

### App Permissions

* Internet Access
* Notifications
* Camera Access (Profile Picture Upload)
* Storage Access (Prescription PDFs)
* Phone Authentication (OTP Verification)

### Privacy Policy

User data is securely stored using Firebase services and is utilized only for healthcare-related features such as appointments, prescriptions, reminders, and AI-powered health analysis. No user data is sold or shared for commercial purposes.

# User Roles & Access Flow

## Administrator

### Login Credentials

**Email:** [admin@gmail.com](mailto:admin@gmail.com)

**Password:** 123456

### Responsibilities

* Approve or reject doctor registration requests.
* Manage users, appointments, and prescriptions.
* Monitor system activity and analytics.
* Review AI chatbot interactions.
* Maintain platform security and operations.

## Patient

Patients can register and access the application instantly without administrative approval.

### Features

* Account Registration & Login
* AI Symptom Analysis
* Appointment Booking
* Prescription Management
* Medicine Reminders
* Profile Management
* Medicine Inventory Tracking

## Doctor

Doctors require administrator approval before accessing doctor functionalities.

### Registration Flow

1. Doctor Registration
2. Request Submission
3. Admin Review
4. Account Approval
5. Full Doctor Access

### Features

* Appointment Management
* Appointment Approval/Rejection
* Digital Prescription Creation
* PDF Prescription Generation
* Patient History Review
* Healthcare Record Management

# System Workflow

```text
Patient Registration → Instant Access

Doctor Registration → Admin Approval Required

Admin Approval → Doctor Account Activated

Patient Books Appointment → Doctor Reviews Request

Doctor Provides Treatment → Prescription Generated

Patient Receives Prescription & Reminders

AI Health Analyzer → Preliminary Health Guidance
```

# Core Features

### Authentication & Security

* Email/Password Authentication
* Google Sign-In
* Phone OTP Verification
* Role-Based Access Control

### AI Health Analyzer

* Groq AI Symptom Analysis
* Healthcare Chatbot
* Preliminary Recommendations
* Doctor Assistance

### Appointment Management

* Appointment Booking
* Approval System
* History Tracking
* Real-Time Updates

### Digital Prescriptions

* Prescription Creation
* PDF Generation
* Prescription History
* Secure Storage

### Medicine Management

* Medicine Reminders
* Adherence Tracking
* Inventory Management
* Refill Monitoring

### Notifications

* Firebase Cloud Messaging (FCM)
* Appointment Alerts
* Prescription Notifications
* Reminder Notifications

### Admin Dashboard

* User Management
* Doctor Approval Management
* Appointment Monitoring
* Prescription Monitoring
* Analytics & Reports

# Technology Stack

| Component            | Technology                 |
| -------------------- | -------------------------- |
| Programming Language | Java                       |
| UI Design            | XML                        |
| Database             | Firebase Realtime Database |
| Authentication       | Firebase Authentication    |
| Storage              | Firebase Storage           |
| Notifications        | Firebase Cloud Messaging   |
| AI                   | Groq API                   |
| Networking           | Retrofit2 + OkHttp3        |
| Image Loading        | Glide                      |
| Animations           | Lottie                     |
| PDF Generation       | iText7                     |
| Build System         | Gradle Kotlin DSL          |

# Installation

## Requirements

* Android Studio
* Android SDK 24+
* Firebase Project
* Groq API Key

## Setup

```bash
git clone https://github.com/YOUR_USERNAME/MediTrackAI.git
```

1. Open the project in Android Studio.
2. Add your `google-services.json` file.
3. Configure your Groq API key in `RetrofitClient.java`.
4. Sync Gradle dependencies.
5. Build and run the application.

# Privacy & Security

* Secure Firebase Authentication
* Encrypted Cloud Storage
* Role-Based Access Control
* Protected Healthcare Records
* Secure Prescription Management

# Future Enhancements

* Video Consultation
* Electronic Health Records Integration
* Advanced AI Disease Prediction
* Multi-Language Support
* Wearable Device Integration
* Health Analytics Dashboard

# Developers

### Noman Hameed

* BS Computer Science, University of Layyah
* Email: [gxnoman235@gmail.com](mailto:gxnoman235@gmail.com)
* GitHub: https://github.com/NOMAN23535

### Muhammad Saad

* BS Computer Science, University of Layyah
* Email: [msaad17060@gmail.com](mailto:msaad17060@gmail.com)
* GitHub: https://github.com/Msaad1122

# License

This project was developed for educational and research purposes.

© 2026 MediTrack AI. All Rights Reserved.
