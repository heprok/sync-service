package com.briolink.syncservice.api.config

import com.sun.istack.NotNull
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.app-endpoints")
class AppEndpointsProperties {
    @NotNull
    lateinit var user: String

    @NotNull
    lateinit var company: String

    @NotNull
    lateinit var companyService: String

    @NotNull
    lateinit var connection: String
}
