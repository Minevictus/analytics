package us.minevict.analytics

import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import us.minevict.mvutil.bungee.MvPlugin
import us.minevict.mvutil.common.acf.enableHelpFeature

class Analytics : MvPlugin(), Listener {
    override fun enable(): Boolean {
        acf.enableHelpFeature()

        database.executeUpdate(
            """
                CREATE TABLE IF NOT EXISTS domain_analytics (
                    player_unique_id VARCHAR(36) NOT NULL, 
                    player_name VARCHAR(16) NOT NULL,
                    joined_domain VARCHAR(99) NOT NULL,
                    joined_time BIGINT NOT NULL,
                    PRIMARY KEY (player_unique_id)
                )
            """.trimIndent()
        )

        listeners(
            this
        )

        registerCommands(AnalyticsCommand(this))
        return true
    }

    @EventHandler
    fun onLogin(event: LoginEvent) {
        proxy.scheduler.runAsync(this) {
            val connection = event.connection ?: return@runAsync
            val host = connection.virtualHost ?: return@runAsync
            database.executeInsert(
                "INSERT IGNORE INTO domain_analytics (player_unique_id, player_name, joined_domain, joined_time) VALUES (?, ?, ?, ?)",
                connection.name,
                connection.uniqueId.toString(),
                host.hostName,
                System.currentTimeMillis()
            )
        }
    }
}