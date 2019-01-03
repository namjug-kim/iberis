package com.iberis

import com.iberis.application.ApplicationStarter
import com.iberis.db.DatabaseService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


/**
 * Please describe the role of the IberisApplication
 * <B>History:</B>
 * Created by namjug.kim on 2018. 9. 21.
 *
 * @author namjug.kim
 * @since 2018. 9. 21.
 * @version 0.1
 */
@SpringBootApplication
open class IberisApplication {
    @Bean
    open fun databaseService(): DatabaseService {
        return DatabaseService()
    }
}

fun main(args: Array<String>) {
    val runApplication = runApplication<IberisApplication>(*args)

    runApplication.getBean(ApplicationStarter::class.java).run()
}
