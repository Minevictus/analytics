package us.minevict.analytics

import co.aikar.commands.BaseCommand
import co.aikar.commands.CommandHelp
import co.aikar.commands.CommandIssuer
import co.aikar.commands.annotation.*
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.ComponentBuilder
import us.minevict.mvutil.common.ext.*
import java.util.concurrent.TimeUnit

@CommandAlias("analytics")
@CommandPermission(Permissions.ANALYTICS_COMMAND)
class AnalyticsCommand(
    private val main: Analytics
) : BaseCommand() {
    @HelpCommand
    @Default
    fun help(sender: CommandIssuer, help: CommandHelp) {
        help.showHelp(sender)
    }

    @Subcommand("lookup")
    @Description("Look up which domain a player joined from.")
    @CommandCompletion("@players")
    @CommandPermission(Permissions.ANALYTICS_COMMAND_LOOKUP_PLAYER)
    fun lookup(
        sender: CommandSender,
        playerName: String
    ) {
        main.proxy.scheduler.runAsync(main) {
            val row = main.database.getResults(
                "SELECT * FROM domain_analytics WHERE player_name = ? LIMIT 1",
                playerName
            ).firstOrNull() ?: return@runAsync

            sender.sendMessage(
                *ComponentBuilder(playerName).yellow()
                    .append(" (${row.getString("player_unique_id")}) ").green()
                    .append("joined from ").gray()
                    .append(row.getString("joined_domain")).green()
                    .append(" on the date ").gray()
                    .duration(row.getLong("joined_time"), TimeUnit.MILLISECONDS).yellow()
                    .create()
            )
        }
    }

    @Subcommand("report")
    @Description("Get a report of all unique joins within a timeframe.")
    @CommandPermission(Permissions.ANALYTICS_COMMAND_REPORT)
    fun report(
        sender: CommandSender,

        @Default("24h")
        period: String,

        @Optional
        @Default("") // empty string
        startsWith: String
    ) {
        val periodDuration = period.parseDuration()
        if (periodDuration.isZero || periodDuration.isNegative) return

        sender.sendMessage(*ComponentBuilder("Loading...").green().create())
        main.proxy.scheduler.runAsync(main) {
            val results = main.database.getResults(
                """
                    SELECT joined_domain, COUNT(joined_domain) AS count FROM domain_analytics 
                    WHERE (joined_domain LIKE '$startsWith%') AND (joined_time >= ?) GROUP BY joined_domain
                """.trimIndent(),
                System.currentTimeMillis() - periodDuration.toMillis()
            )

            if (results.isEmpty()) {
                sender.sendMessage(*ComponentBuilder("No analytics found for that timeframe!").red().create())
                return@runAsync
            }

            sender.sendMessage(*ComponentBuilder("Unique joins per domain for the past $period").gray().create())
            results.forEach {
                val domain = it.getString("joined_domain")
                val amount = it.getInt("count") ?: 0

                sender.sendMessage(
                    *ComponentBuilder(domain).green()
                        .append(": $amount unique joins past $period").gray()
                        .create()
                )
            }
        }
    }
}