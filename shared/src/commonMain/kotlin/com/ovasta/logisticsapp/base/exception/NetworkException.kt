package com.ovasta.logisticsapp.base.exception

import kotlinx.io.IOException

class NetworkException(override var message: String) : IOException()
