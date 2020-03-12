package com.github.moholiaghat.smsPostman.config

class SmsConfig {
    var iranOrder = listOf<String>()
    var iranFirst = SmsSenderConfig()
    var iranSecond = SmsSenderConfig()
    var foreign = SmsSenderConfig()
    var threadPoolSize = 20
}

class SmsSenderConfig {
    var name = ""
    var url = ""
    var userId = "0"
    var password = ""
    var message = ""
    var originator = ""
}