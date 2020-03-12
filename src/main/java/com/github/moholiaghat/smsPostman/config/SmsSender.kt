package com.github.moholiaghat.smsPostman.config

import com.github.moholiaghat.smsPostman.SmsPostman

abstract class SmsSender(protected val config: SmsConfig, val name: String) {
    protected val postman = SmsPostman(config.threadPoolSize)
    protected abstract fun getMessageContent(code: String): String
    abstract fun sendSmsCode(phoneNumber: String, code: String)
}

class IranFirstSmsSender(config: SmsConfig) : SmsSender(config, config.iranFirst.name) {
    override fun sendSmsCode(phoneNumber: String, code: String) {
        val smsConfig = config.iranFirst

        val parameters = mutableMapOf<String, String>().apply {
            this["userId"] = smsConfig.userId
            this["password"] = smsConfig.password
            this["message"] = getMessageContent(code)
            this["recipient"] = phoneNumber
            this["originator"] = smsConfig.originator
        }

        postman.getRequest(parameters, smsConfig.url)
    }

    override fun getMessageContent(code: String): String =
            config.iranFirst.message.replace("%code%", code)
}

class IranSecondSmsSender(config: SmsConfig) : SmsSender(config, config.iranSecond.name) {
    override fun sendSmsCode(phoneNumber: String, code: String) {
        val smsConfig = config.iranSecond

        val parameters = mutableMapOf<String, String>().apply {
            this["username"] = smsConfig.userId
            this["password"] = smsConfig.password
            this["dstaddress"] = phoneNumber
            this["body"] = getMessageContent(code)
        }

        postman.getRequest(parameters, smsConfig.url)
    }

    // FixMe MoHoLiaghat: iran first or second?
    override fun getMessageContent(code: String): String =
            config.iranSecond.message.replace("%code%", code)
}

class ForeignSmsSender(config: SmsConfig) : SmsSender(config, config.foreign.name) {
    override fun sendSmsCode(phoneNumber: String, code: String) {
        val smsConfig = config.foreign
        val parameters = mutableMapOf<String, String>().apply {
            this["recipients"] = phoneNumber
            this["originator"] = smsConfig.originator
            this["body"] = getMessageContent(code).replace("\n", "%0a")
        }

        val headers = mapOf("Authorization" to "AccessKey ${smsConfig.password}")

        postman.postRequest(parameters, smsConfig.url, headers)
    }

    override fun getMessageContent(code: String): String =
            config.foreign.message.replace("%code%", code)
}