package com.example.executorkit

import android.app.Application
import com.google.gson.Gson
import com.log.kit.ILogConfig
import com.log.kit.ILogManager
import com.log.kit.print.ILogPrinter
import com.log.kit.print.console.IConsolePrinter

class MyApplication :Application(){

    override fun onCreate() {
        super.onCreate()
        val iLogConfig: ILogConfig = object : ILogConfig() {

            override fun enable(): Boolean {
                return super.enable()
            }

            override fun includeThread(): Boolean {
                return true
            }

            override fun stackTraceDepth(): Int {
                return 0
            }

            override fun printers(): Array<ILogPrinter?>? {
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
        ILogManager.getInstance().init(iLogConfig, IConsolePrinter())

    }
}