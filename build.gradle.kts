import us.minevict.mvutilgradleplugin.*

plugins {
    id("us.minevict.mvutil") version "0.2.0"
}

group = "us.minevict"
version = "0.1.1"

mvUtilVersion = "5.2.0"

dependencies {
    compileOnly(waterfallApi("1.15"))
    compileOnly(mvutil("bungee"))
}

bungee {
    main = "us.minevict.analytics.Analytics"
    author = "NahuLD"
    depends = setOf("MV-Util")
}

relocateBungee()