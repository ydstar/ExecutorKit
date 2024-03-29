package com.executor.kit

import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * Author: 信仰年轻
 * Date: 2020-11-12 16:23
 * Email: hydznsqk@163.com
 * Des:管理线程
 * 支持按任务的优先级去执行
 * 支持线程池暂停.恢复(批量文件下载，上传)
 * 异步结果主动回调主线程
 *
 * 具体实现思路
 * 1.任务优先级
 *使用PriorityBlockingQueue这个排序的队列
 *创建一个PriorityRunnable类(具有可比较线程优先级的Runnable),去实现Runnable接口和Comparable接口,然后实现run()方法和compareTo
 *在compareTo方法中比较优先级,这样一来优先级高的任务先执行
 *在run方法中去执行接口Runnable中的run方法
 *
 *2.暂停和恢复
 *使用ReentrantLock,暂停的时候在线程执行前方法中 调用 mReentrantLock.lock和 mNewCondition.await()
 *恢复的时候用mNewCondition.signalAll()和mReentrantLock.unlock()
 *
 *3.异步任务回调
 *通过handler的post方法去实现
 */
object ExecutorKit {
    private const val TAG: String = "ExecutorKit"

    private var mExecutor: ThreadPoolExecutor
    private var mHandler: Handler = Handler(Looper.getMainLooper())

    private var mIsPause = false
    private val mReentrantLock: ReentrantLock = ReentrantLock() //可重入锁
    private var mNewCondition: Condition

    init {
        mNewCondition = mReentrantLock.newCondition()
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePooSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        val blockingQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()
        val keepAliveTime = 30L
        val unit = TimeUnit.SECONDS

        val atomicLong = AtomicLong()
        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            thread.name = TAG + atomicLong.getAndIncrement()
            return@ThreadFactory thread
        }

        mExecutor = object : ThreadPoolExecutor(
            corePooSize, maxPoolSize, keepAliveTime,
            unit, blockingQueue as BlockingQueue<Runnable>, threadFactory
        ) {
            /**
             * 线程执行前
             */
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                if (mIsPause) {
                    mReentrantLock.lock()
                    try {
                        //如果暂停了就让线程等待
                        mNewCondition.await()
                    } finally {
                        mReentrantLock.unlock()
                    }
                }
            }

            /**
             * 线程执行后
             */
            override fun afterExecute(r: Runnable?, t: Throwable?) {
                //监控线程池耗时任务,线程创建数量,正在运行的数量
            }
        }
    }

    /**
     * 开启子线程,普通版Runnable回调接口
     */
    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Runnable) {
        mExecutor.execute(PriorityRunnable(priority, runnable))
    }

    /**
     * 开启子线程,加强版Callable回调
     */
    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Callable<*>) {
        mExecutor.execute(PriorityRunnable(priority, runnable))
    }

    /**
     * 暂停线程
     */
    @Synchronized
    fun pause() {
        mIsPause = true
    }

    /**
     * 恢复线程
     */
    fun resume() {
        mIsPause = false
        mReentrantLock.lock()
        try {
            //唤醒所有阻塞的线程
            mNewCondition.signalAll()
        } finally {
            mReentrantLock.unlock()
        }
    }

    /**
     * 对Runnable对象的包装类,是具有可比较线程优先级的Runnable
     */
    class PriorityRunnable(val priority: Int, private val runnable: Runnable) : Runnable,
        Comparable<PriorityRunnable> {

        override fun run() {
            runnable.run()
        }

        override fun compareTo(other: PriorityRunnable): Int {
            if (this.priority < other.priority) {
                return 1
            } else if (this.priority > other.priority) {
                return -1
            } else {
                return 0
            }
        }
    }

    abstract class Callable<T> : Runnable {
        override fun run() {
            mHandler.post {
                onPrepare()
            }

            val t = onBackground()
            //移除所有消息.防止需要执行onCompleted了，onPrepare还没被执行，那就不需要执行了
            mHandler.removeCallbacksAndMessages(null)
            mHandler.post {
                onCompleted(t)
            }
        }

        //任务执行前
        open fun onPrepare() {
            //可以转菊花
        }

        //真正后台任务的地方
        abstract fun onBackground(): T

        //任务执行完
        abstract fun onCompleted(t: T)
    }
}