package com.github.zottaa.note.details

import androidx.fragment.app.FragmentManager
import com.github.zottaa.core.Screen

data class NoteDetailsScreen(
    private val noteId: Long
) : Screen {
    override fun show(supportFragmentManager: FragmentManager, containerId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(
                containerId,
                NoteDetailsFragment(noteId)
            )
            .commit()
    }
}