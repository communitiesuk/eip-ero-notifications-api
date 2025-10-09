# Statistics

- The majority of communications are tracked in Qlik
  - This tracks the number of each communication type sent
- When adding a new communication we should always consider if new statistics are needed

## Tracking a new communication statistics

1. In Notifications API
   * Adding a new communication to `CommunicationsStatisticsResponse` used in [StatisticsRetrievalService.kt](../src/main/kotlin/uk/gov/dluhc/notificationsapi/service/StatisticsRetrievalService.kt) and the necessary mapping
2. Update Applications API to use this new communication statistics [documented here](https://github.com/communitiesuk/eip-ero-applications-api/blob/main/docs/guidelines/statistics.md#communication-statistics-and-the-notifications-api)

- Note communications are grouped together under clear communication names to make the statistics easier to follow
  - This is done in `statisticsNotificationCategories` in [NotificationType.kt](../src/main/kotlin/uk/gov/dluhc/notificationsapi/dto/NotificationType.kt)
