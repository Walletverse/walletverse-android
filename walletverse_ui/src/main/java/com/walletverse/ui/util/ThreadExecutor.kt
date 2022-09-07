package com.walletverse.ui.util

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger


/**
 * ================================================================
 * Create_time：2022/8/12  12:37
 * Author：solomon
 * Detail：
 * ================================================================
 */
class ThreadExecutor(
    corePoolSize: Int,
    maximumPoolSize: Int,
    keepAliveTime: Long,
    unit: TimeUnit?,
    workQueue: BlockingQueue<Runnable?>?,
    threadFactory: ThreadFactory?
) :
    ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        unit,
        workQueue,
        threadFactory
    ) {
    companion object {
        private const val CORE_POOL_SIZE = 1

        private val MAXI_MUM_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2
        private const val KEEP_ALIVE_TIME = 3
        private var executor: ThreadExecutor? = null
        private val sThreadFactory: ThreadFactory = object : ThreadFactory {
            private val mCount: AtomicInteger = AtomicInteger(1)
            override fun newThread(r: Runnable?): Thread {
                return Thread(r, "ThreadExecutor #" + mCount.getAndIncrement())
            }
        }

        val instance: ThreadExecutor?
            get() {
                if (null == executor) {
                    synchronized(ThreadExecutor::class.java) {
                        if (null == executor) {
                            executor = ThreadExecutor(
                                CORE_POOL_SIZE,
                                MAXI_MUM_POOL_SIZE,
                                KEEP_ALIVE_TIME.toLong(),
                                TimeUnit.SECONDS,
                                SynchronousQueue<Runnable>(),
                                sThreadFactory
                            )
                        }
                    }
                }
                return executor
            }
    }
}

