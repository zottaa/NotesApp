package com.github.zottaa.core

import androidx.lifecycle.ViewModel
import org.junit.Assert.assertEquals

interface FakeClear : ClearViewModels {

    companion object {
        const val CLEAR = "FakeClear#clear"
    }

    fun check(expected: List<Class<out ViewModel>>)

    class Base(private val order: Order) : FakeClear {

        private val actual = mutableListOf<Class<out ViewModel>>()

        override fun clear(vararg viewModelClasses: Class<out ViewModel>) {
            actual.clear()
            actual.addAll(viewModelClasses)
            order.add(CLEAR)
        }

        override fun check(expected: List<Class<out ViewModel>>) {
            assertEquals(expected, actual)
        }
    }
}