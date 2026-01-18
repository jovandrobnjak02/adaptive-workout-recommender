# Adaptive Workout Recommender

A in terminal workout generation system that adapts exercise selection, volume and weight based on user readiness and historical training data.

> **Design details, algorithms, and future plans are documented in the project Wiki.**

## About the Project

This project is an MVP built primarily for learning experimentation. It focuses on:

* Good modeling
* Clear separation of concerns (CLI, service, logic, persistence)
* A simple adaptive training algorithm

It is **not** intended to be a production-ready fitness application.

## Current Features

* Interactive terminal-based CLI
* User registration and login
* Per-user training profiles
* Profile configuration:
  * Experience level (beginner / intermediate / advanced)
  * Training frequency and split
* Daily workout generation based on:
  * Training split and sequence
  * User readiness (stress, fatigue, sleep, nutrition)
  * Exercise difficulty
* Exercise selection from a pre-defined catalog
* Volume prescription:
  * Sets, reps, rest time
* Load recommendations:
  * Based on previous training history
  * Progressive overload with safety measures
* Workout logging:
  * Reps and load per set
* PostgreSQL persistence
* Fully Dockerized development environment

## Tech Stack

### Language and Runtime

* **Clojure** (Leiningen)
* **JDK 21**

### Database

* **PostgreSQL**
* **next.jdbc** – SQL access layer

### Deployment and Testing

* **Docker & Docker Compose**
* **Midje** – testing framework

## Project Dependencies

Dependencies used in this project:

```clojure
[org.clojure/clojure "1.12.2"]
[com.github.seancorfield/next.jdbc "1.3.955"]
[org.postgresql/postgresql "42.7.4"]
[org.clojure/data.json "2.5.0"]
[buddy/buddy-hashers "2.0.167"]
```

Testing:

```clojure
[midje/midje "1.10.9"]
```

## Quick Starting

### Requirements

* Docker
* Docker Compose

### 1. Start the database

```bash
  docker compose up -d db
```

### 2. Run the app interactively

```bash
  docker compose run --rm app
```

You should see an interactive terminal menu allowing you to generate and log workouts.

---

## Resetting the Database (Development)

The database is intentionally designed to be easy to reset during development.

To reset everything:

```bash
    docker compose down -v
    docker compose up -d db
```

This will:

* Drop all tables
* Recreate the schema
* Seed exercises
* Seed the default user and profile

## Using the Application

### Authentication

At startup, the application asks you to either:

- **Register a new account**
- **Login with an existing account**
_(there is a default user available with email goat@gmail.com and password 12346578)_

During registration, you provide:
- Name
- Email
- Password

After logging in for the first time, you are asked to configure your training profile.

### Profile Setup

The profile setup includes:
- Experience level (beginner / intermediate / advanced)
- Training frequency (3, 4, or 6 days per week)
- Training split (automatically chosen or selected for 6-day plans)

The profile is saved and reused on future logins.

### Main Menu

After login, the application shows the main menu:

- Generate today's workout
- Log the last generated workout
- Logout
- Exit

### Generating a Workout

When generating a workout, the system asks for a short readiness check-in:

- Stress level (1–10)
- Fatigue level (1–10)
- Sleep duration (hours)
- Nutrition status (`deficit` / `neutral` / `bulk`)

Based on this input, the system:

- Selects appropriate exercises for the day
- Adjusts training volume and difficulty
- Recommends training loads when sufficient history exists

The generated workout is immediately saved to the database.

### Logging a Workout

After completing your training session, choose *Log the last generated workout*.

For each exercise and set, you will be prompted to enter:

- Repetitions performed
- Load used

This data is stored and later used to improve future load recommendations.

---

## Typical Usage Flow

1. Start the application
2. Generate today’s workout
3. Train
4. Log the performed workout
5. Repeat on the next training day

Over time, the system adapts its recommendations based on logged performance.


## Project Structure

```
src/
  adaptive_workout_recommender/
    core.clj              ;; CLI
    service/              ;; Use‑case orchestration
    logic/                ;; Workout generation logic
    progression/          ;; Load progression and regression
    persistence/          ;; Database access
resources/
  migrations/             ;; DB seed scripts
```

## Project Status & Limitations

This MVP intentionally omits:

* Web or mobile UI
* Injury or equipment constraints
* RIR tracking
* Deload logic
* Missed workout adaptation
* Periodization blocks

These are documented as future improvements in the Wiki.

## License

EPL‑2.0 OR GPL‑2.0‑or‑later WITH Classpath‑exception‑2.0
