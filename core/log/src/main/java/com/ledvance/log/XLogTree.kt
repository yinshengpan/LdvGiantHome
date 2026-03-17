package com.ledvance.log

import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/4 10:55
 * Describe : XLogTree
 */
internal class XLogTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        LogManager.writeLog(priority, tag ?: "", "$message\n${t?.stackTraceToString() ?: ""}")
    }
}