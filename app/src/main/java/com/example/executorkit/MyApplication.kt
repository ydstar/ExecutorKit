package com.example.executorkit

import android.app.Application
import com.google.gson.Gson
import com.log.kit.LogConfig
import com.log.kit.LogKitManager
import com.log.kit.print.LogPrinter
import com.log.kit.print.console.ConsolePrinter


class MyApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        val logConfig: LogConfig = object : LogConfig() {

            override fun enable(): Boolean {
                return super.enable()
            }

            override fun includeThread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 0
            }

            override fun printers(): Array<LogPrinter?>? {
                return super.printers()
            }

            override fun injectJsonParser(): JsonParser? {
                return object : JsonParser {
                    override fun toJson(`object`: Any?): String? {
                        return Gson().toJson(`object`)
                    }
                }
            }
        }
        LogKitManager.getInstance().init(logConfig, ConsolePrinter())

    }
}