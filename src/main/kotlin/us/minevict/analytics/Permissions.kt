package us.minevict.analytics

object Permissions {
    private const val BASE = "analytics"
    const val ANALYTICS_COMMAND = "$BASE.command"
    const val ANALYTICS_COMMAND_LOOKUP_PLAYER = "$ANALYTICS_COMMAND.lookup"
    const val ANALYTICS_COMMAND_REPORT = "$ANALYTICS_COMMAND.report"
}