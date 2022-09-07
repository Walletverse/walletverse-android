package com.walletverse.ui.util

import org.greenrobot.eventbus.EventBus




object EventBusUtil {

    fun post(event: Any?) {
        EventBus.getDefault().post(event)
    }

    fun cancelEventDelivery(event: Any?) {
        EventBus.getDefault().cancelEventDelivery(event)
    }

    fun postSticky(event: Any?) {
        EventBus.getDefault().postSticky(event)
    }

    fun <T> removeStickyEvent(eventType: Class<T>?) {
        val stickyEvent = EventBus.getDefault().getStickyEvent(eventType)
        if (stickyEvent != null) {
            EventBus.getDefault().removeStickyEvent(stickyEvent)
        }
    }

    fun removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents()
    }
}