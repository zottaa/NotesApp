package com.github.zottaa.core

import androidx.lifecycle.LiveData
import com.github.zottaa.main.Navigation
import org.junit.Assert.assertEquals

interface FakeNavigation : Navigation.Mutable {

    interface Update : Navigation.Update {
        fun checkScreen(expected: Screen)
    }

    interface Mutable : Update, Navigation.Mutable

    companion object {
        const val NAVIGATE = "Navigation#navigate"
    }

    class Base(private val order: Order) : Mutable {

        private lateinit var actual: Screen

        override fun update(screen: Screen) {
            actual = screen
            order.add(NAVIGATE)
        }

        override fun liveData(): LiveData<Screen> {
            throw IllegalStateException("Not used here")
        }

        override fun checkScreen(expected: Screen) {
            assertEquals(expected, actual)
        }
    }
}