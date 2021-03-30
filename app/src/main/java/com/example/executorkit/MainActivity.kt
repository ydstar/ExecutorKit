package com.example.executorkit

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.executor.kit.ExecutorKit
import com.log.kit.ILog
import com.log.kit.ILogManager
import com.log.kit.print.view.IViewPrintProvider
import com.log.kit.print.view.IViewPrinter

class MainActivity : AppCompatActivity() {

    private var paused = false

    private var mViewPrinter: IViewPrinter? = null
    private var mPrintProvider: IViewPrintProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mViewPrinter = IViewPrinter(this)
        ILogManager.getInstance().addPrinter(mViewPrinter)
        mPrintProvider = mViewPrinter?.getViewPrintProvider()
        mPrintProvider?.showFloatingView()
    }

    /**
     * 简单用法,开启子线程做一些耗时操作
     */
    fun show0(view: View?) {
        ExecutorKit.execute(runnable = Runnable {
            //开启子线程做耗时操作,比如数据的读写
            ILog.it("show0", "开启子线程")
        })

    }
    /**
     * 按照优先级去执行任务
     */
    fun show1(view: View?) {
        for (priority in 0..9) {
            ExecutorKit.execute(priority, Runnable {
                try {
                    //ps:如果在模拟器中测试因为cpu的多核有可能会导致线程优先级低的比优先级高的先执行完
                    Thread.sleep(1000 - priority * 100.toLong())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }finally {
                    ILog.it("show1", "已执行完的任务的优先级是：$priority")
                }
            })
        }
    }

    /**
     * 暂停恢复线程池
     */
    fun show2(view: View?) {
        if (paused) {
            ExecutorKit.resume()
            ILog.dt("show2", "恢复线程")
        } else {
            ExecutorKit.pause()
            ILog.et("show2", "暂停线程")
        }
        paused = !paused
    }

    /**
     * 异步任务结果回调主线程
     */
    fun show3(view: View?) {
        ExecutorKit.execute(0,object : ExecutorKit.Callable<String?>() {

            //任务执行前_主线程
            override fun onPrepare() {
                //可以转菊花
                ILog.vt("show3_onPrepare","任务执行前")
            }

            //任务执行中_子线程
            override fun onBackground(): String? {
                ILog.it("show3_onBackground","任务执行中")
                var total = 0
                for (x in 0..99999999) {
                    total = total + x
                }
                return total.toString() + ""
            }

            //任务执行结束_主线程
            override fun onCompleted(s: String?) {
                ILog.dt("show3_onCompleted", "onCompleted-任务结果是result:$s")
            }
        })
    }


    fun open(view: View?) {
        mPrintProvider?.showFloatingView()
    }

    fun close(view: View?) {
        mPrintProvider?.closeFloatingView()
    }

    override fun onDestroy() {
        ILogManager.getInstance().removePrinter(mViewPrinter)
        super.onDestroy()
    }
}