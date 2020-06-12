package us.minevict.analytics

import net.md_5.bungee.api.event.LoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import us.minevict.mvutil.bungee.MvPlugin
import us.minevict.mvutil.common.acf.enableHelpFeature
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Analytics : MvPlugin(), Listener {
    lateinit var executor: ExecutorService

    override fun enable(): Boolean {
        executor = Executors.newSingleThreadExecutor()
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

        registerCommands(AnalyticsCommand(executor, database))
        return true
    }

    @EventHandler
    fun onLogin(event: LoginEvent) {
        executor.submit {
            val connection = event.connection ?: return@submit
            val host = connection.virtualHost ?: return@submit
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