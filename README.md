# MediTrack-AI-Smart-Healthcare
An AI-powered Android healthcare application that enables symptom analysis, appointment management, medicine reminders, digital prescriptions, and doctor-patient collaboration through a secure healthcare ecosystem.
MediTrack AI

# User Roles & Access Flow
## Administrator
The application includes a pre-configured administrator account responsible for managing the entire healthcare ecosystem.

### Admin Login Credentials
Email:admin@gmail.com
Password: 123456

### Admin Responsibilities

* Approve or reject doctor registration requests.
* Manage doctors, patients, and system users.
* Monitor appointments and prescriptions.
* Access platform analytics and reports.
* Review AI chatbot interactions.
* Maintain system security and operations.

## Patient

Patients can directly register and access the application without requiring administrative approval.
### Patient Features

* Direct account registration and login.
* AI-powered symptom analysis.
* Appointment booking with approved doctors.
* Prescription viewing and management.
* Medicine reminders and adherence tracking.
* Profile management.
* Medicine inventory management.

## Doctor
Doctors must submit a registration request before gaining access to doctor functionalities.

### Doctor Registration Process

1. Doctor creates an account.
2. Registration request is submitted.
3. Admin reviews the request.
4. Admin approves the doctor account.
5. Doctor gains access to all doctor features.

### Doctor Features

* Manage patient appointments.
* Accept or decline appointment requests.
* Create digital prescriptions.
* Generate prescription PDF documents.
* Review patient history.
* Manage healthcare records.

# System Workflow

Patient Registration → Instant Access
Doctor Registration → Admin Approval Required
Admin Approval → Doctor Account Activated
Patient Books Appointment → Doctor Reviews Request
Doctor Provides Treatment → Prescription Generated
Patient Receives Prescription & Reminders
AI Health Analyzer Assists Patient with Preliminary Health Guidance

# About the App

MediTrack AI is a full-featured Android application developed using Java, Firebase, and Groq AI. The system supports three user roles: Patients, Doctors, and Administrators.

Patients can manage appointments, medicine schedules, prescriptions, and receive AI-powered health guidance. Doctors can handle patient appointments, create digital prescriptions, and review patient records. Administrators oversee the entire platform, manage doctor approvals, monitor activities, and maintain system integrity.

# Application Information

| Property       | Details                    |
| -------------- | -------------------------- |
| App Name       | MediTrack AI               |
| Version        | 1.0                        |
| Platform       | Android                    |
| Language       | Java                       |
| IDE            | Android Studio             |
| Architecture   | MVVM                       |
| Database       | Firebase Realtime Database |
| Authentication | Firebase Authentication    |
| AI Integration | Groq API                   |
| Minimum SDK    | 24                         |
| Target SDK     | 34                         |

# Core Features

## Authentication & Security

* Email & Password Authentication
* Google Sign-In Integration
* Phone OTP Verification
* Role-Based Access Control
* Secure Firebase Authentication

## AI Health Analyzer

* Groq AI-powered symptom analysis
* Intelligent healthcare chatbot
* Preliminary health recommendations
* Doctor recommendation assistance

## Appointment Management

* Appointment booking
* Appointment approval system
* Appointment history tracking
* Real-time appointment updates

## Digital Prescriptions

* Create prescriptions digitally
* PDF prescription generation
* Prescription history management
* Secure prescription storage

## Medicine Reminder System

* Scheduled medicine reminders
* Notification alerts
* Adherence tracking
* Medication management

## Medicine Inventory

* Medicine stock management
* Quantity tracking
* Refill monitoring

## Push Notifications

* Firebase Cloud Messaging (FCM)
* Appointment alerts
* Prescription notifications
* Reminder notifications

## Profile Management

* Update personal information
* Change password
* Upload profile picture
* Account customization

## Admin Dashboard

* User management
* Doctor approval management
* Appointment monitoring
* Prescription management
* Chat log monitoring
* System analytics

# Technology Stack

| Category             | Technology                 |
| -------------------- | -------------------------- |
| Programming Language | Java                       |
| UI Design            | XML                        |
| Architecture         | MVVM                       |
| Database             | Firebase Realtime Database |
| Authentication       | Firebase Authentication    |
| Storage              | Firebase Storage           |
| Notifications        | Firebase Cloud Messaging   |
| AI Integration       | Groq API                   |
| Networking           | Retrofit2                  |
| HTTP Client          | OkHttp3                    |
| Image Loading        | Glide                      |
| Animations           | Lottie                     |
| PDF Generation       | iText7                     |
| Build System         | Gradle Kotlin DSL          |

# Installation Guide
## Prerequisites

* Android Studio
* Android SDK API 24+
* Firebase Project Configuration
* Groq API Key

## Setup Instructions
### 1. Clone Repository

```bash
git clone https://github.com/YOUR_USERNAME/MediTrackAI.git
```
### 2. Open Project
Open the project using Android Studio.
### 3. Configure Firebase
Replace the existing:
```text
google-services.json
```
with your Firebase configuration file.
### 4. Add Groq API Key
Insert your API key inside:
```text
RetrofitClient.java
```
### 5. Sync Gradle

Sync project dependencies and build files.
### 6. Run Application

Build and run the application on an Android Emulator or Physical Device.
# APK Installation

1. Download the APK file.
2. Enable installation from unknown sources.
3. Open the APK file.
4. Install the application.
5. Launch MediTrack AI.
6. Register or login.
7. Start using the healthcare platform.

# Privacy & Security
MediTrack AI prioritizes user privacy and data protection.

* Secure Firebase Authentication.
* Encrypted cloud-based data storage.
* Role-based access control.
* Protected healthcare records.
* Secure prescription management.
* No commercial sharing of user data.

User information is only used to provide healthcare-related services within the application.
# Future Enhancements

* Video Consultation System
* Electronic Health Records Integration
* Advanced AI Disease Prediction
* Multi-Language Support
* Wearable Device Integration
* Health Analytics Dashboard
* Cloud Backup System

# Developers

### Noman Hameed

* BS Computer Science
* University of Layyah
* Email:gxnoman235@gmail.com
* GitHub: https://github.com/NOMAN23535

### Muhammad Saad

* BS Computer Science
* University of Layyah
* Email: msaad17060@gmail.com
* GitHub: https://github.com/Msaad1122

# License

This project was developed as an academic healthcare management system for educational and research purposes.

© 2026 MediTrack AI. All Rights Reserved.
