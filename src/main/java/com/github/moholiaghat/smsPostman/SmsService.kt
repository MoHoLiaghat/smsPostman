package com.github.moholiaghat.smsPostman

import com.github.moholiaghat.smsPostman.config.ForeignSmsSender
import com.github.moholiaghat.smsPostman.config.IranFirstSmsSender
import com.github.moholiaghat.smsPostman.config.IranSecondSmsSender
import com.github.moholiaghat.smsPostman.config.SmsConfig


class SmsService(private val config: SmsConfig) {
    private val iranSmsSenders =
            listOf(IranFirstSmsSender(config), IranSecondSmsSender(config)).sortedBy { sender ->
                config.iranOrder.indexOfFirst { it == sender.name }
            }

    fun sendTestSms(senderName: String, phoneNumber: String, message: String) {
        val sender = (iranSmsSenders + ForeignSmsSender(config))
                .firstOrNull { it.name == senderName }
                ?: error("No smsSender found with given name!")

        sender.sendSmsCode(phoneNumber, message)
    }
}
