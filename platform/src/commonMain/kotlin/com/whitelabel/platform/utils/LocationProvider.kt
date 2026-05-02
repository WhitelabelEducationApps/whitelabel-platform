package com.whitelabel.platform.utils

expect suspend fun getLocationLastKnown(context: Any): Pair<Double, Double>?
