# Adaptive Workout Recommender (Clojure)

## Overview

This project is an **adaptive workout recommender system** implemented in **Clojure**, designed initially as a **terminal-based backend MVP**. The system generates daily workouts based on a user's training experience, chosen split, workout history, and daily readiness inputs (stress, fatigue, sleep, nutrition).

The MVP focuses on being **scientifically reasonable, deterministic, and explainable**, while laying a clean foundation for future intelligence, automation, and machine-learning-driven personalization.

---

## 1. MVP Functionalities

### 1.1 User & Profile Configuration

* Create and select a user profile (no full auth in MVP)
* User profile includes:

  * Experience level: **Beginner / Intermediate / Advanced**
  * Training frequency:

    * 3 days → Full Body
    * 4 days → Upper / Lower
    * 6 days → PPL or Bro Split
  * Chosen split type (where applicable)

---

### 1.2 Workout Splits & Scheduling

#### Supported Splits

* **3 Days / Week**: Full Body **A / B / C** rotation
* **4 Days / Week**: **Upper A / Lower A / Upper B / Lower B**
* **6 Days / Week**:

  * **PPL**: Push / Pull / Legs / Push / Pull / Legs
  * **Bro Split**: Chest / Back / Legs / Shoulders / Arms / Legs (2)

#### Scheduling Logic

* The system always generates the **next workout in sequence**
* Missed days are ignored in MVP (no rescheduling logic)
* Each login generates **one workout for that day only**

---

### 1.3 Exercise Catalog

Exercises are stored in the database with the following attributes:

* Name
* Main muscle group
* Secondary muscle group
* Difficulty level (Beginner / Intermediate / Advanced)

Notes:

* Exercises hitting the same muscle group are interchangeable
* Exercise substitutions are treated as separate exercises
* No equipment, injury, or mobility constraints in MVP

---

### 1.4 Daily Readiness Inputs

Before generating a workout, the user provides:

* **Stress**: 1–10
* **Fatigue**: 1–10
* **Sleep**: 1–8 hours
* **Nutrition**: Deficit / Neutral / Bulk

These inputs affect:

* Total volume (sets)
* Intensity (target load)
* Exercise difficulty preference (easier movements on bad days)

---

### 1.5 Workout Generation

Each generated workout includes:

* Exercise list
* Sets per exercise (2–3)
* Repetition targets (6–8 / 6–10 / 8–10 depending on context)
* Target load per exercise
* Rest time per exercise (90–180s based on difficulty)

Workout structure is kept **scientifically reasonable but simple**.

---

### 1.6 Load Recommendation & Progression (ML-lite)

The MVP uses an **explainable progression model**, not full machine learning:

* Track performance history (sets, reps, load)
* Estimate **e1RM** (e.g., Epley formula) per exercise
* Generate target loads based on:

  * Desired rep range
  * Latest e1RM
  * Daily readiness modifiers
  * Nutrition state

#### First-Time Exercises

* If no history exists for an exercise:

  * Target load is set to **0**
  * User is instructed to choose a challenging weight within the rep range

---

### 1.7 Workout Logging

* Users log:

  * Exercise
  * Sets
  * Reps
  * Load
* Logged data is stored for future progression and adaptation

---

### 1.8 Technical Scope (MVP)

* Language: **Clojure**
* Database: **SQLite**
* Interface: **Terminal / CLI**
* Architecture: Pure functional core + IO boundaries

---

## 2. Future Improvements & Planned Extensions

### 2.1 Training Intelligence & Adaptation

* Missed-day adaptation (auto-shift or reschedule workouts)
* Auto-deload detection (performance drop over multiple sessions)
* Volume landmarks per muscle group (MEV / MAV / MRV)
* Smarter fatigue management across week

---

### 2.2 Exercise & User Constraints

* Equipment availability (home gym vs commercial gym)
* Injury and movement restrictions
* Exercise substitutions and equivalents
* Mobility and stability considerations

---

### 2.3 Readiness & Feedback

* RPE / RIR tracking per set
* Soreness tracking
* Subjective workout feedback ("too easy / too hard")
* Recovery score aggregation

---

### 2.4 Programming Features

* Warm-up sets and ramping logic
* Tempo prescriptions
* Supersets / giant sets
* Time-capped workouts
* Auto-regulated rest times

---

### 2.5 Machine Learning (Post-MVP)

* Learned load progression models across users
* Exercise recommendation optimization
* User clustering by response to volume/intensity
* Personalized fatigue and recovery prediction

---

### 2.6 LLM & UX Enhancements

* Natural-language workout explanations
* Coaching-style feedback and motivation
* Automatic note generation
* Conversational workout adjustments

---

### 2.7 Platform & Product

* REST / GraphQL API
* Web and mobile frontends
* Multi-user authentication
* Cloud database support
* Exportable workout plans

