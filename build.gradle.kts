import us.minevict.mvutilgradleplugin.*

plugins {
    id("us.minevict.mvutil") version "0.4.5"
}

group = "us.minevict"
version = "0.2.0"
mvUtilVersion = "6.3.5"

dependencies {
    compileOnly("io.github.waterfallmc:waterfall-api:1.18-R0.1-SNAPSHOT")
    compileOnly(mvutil("bungee"))
}

bungee {
    main = "us.minevict.analytics.Analytics"
    author = "NahuLD"
    depend()
}