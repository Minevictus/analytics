import us.minevict.mvutilgradleplugin.*

plugins {
    id("us.minevict.mvutil") version "0.2.5"
}

group = "us.minevict"
version = "0.1.2"
mvUtilVersion = "6.0.1"

dependencies {
    compileOnly(waterfallApi("1.15"))
    compileOnly(mvutil("bungee"))
}

bungee {
    main = "us.minevict.analytics.Analytics"
    author = "NahuLD"
    depend()
}