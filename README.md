# KotlinBaseball
This is an app to pull stats from the MLB's data API and show it using Jetpack Compose, Hilt, MVVM, Coroutines, and Flows.

## Key Features
- **Team Stats**: List and detailed information for MLB teams.
- **Game Schedules**: Real-time scores and game schedules.
- **Player Profiles**: Detailed player statistics and career history.
- **Modern Android Stack**: Built with Jetpack Compose, Ktor for networking, and Hilt for dependency injection.

## Recent Fixes
- Fixed type mismatch in `TeamRepository` where nullable API data was being assigned to non-nullable domain models.
