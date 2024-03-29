package com.github.zottaa.note.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.zottaa.core.ClearViewModels
import com.github.zottaa.core.Now
import com.github.zottaa.core.Screen
import com.github.zottaa.core.SharedPreferencesRepository
import com.github.zottaa.main.Navigation
import com.github.zottaa.note.core.CategoryRepository
import com.github.zottaa.note.core.NoteLiveDataWrapper
import com.github.zottaa.note.core.NotesRepository
import com.github.zottaa.note.core.RemoteNotesRepository
import com.github.zottaa.note.list.CategoryUi
import com.github.zottaa.note.list.ListLiveDataWrapper
import com.github.zottaa.note.list.NoteUi
import com.github.zottaa.note.list.NotesListScreen
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteDetailsViewModel(
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val remoteNotesRepository: RemoteNotesRepository.Edit,
    private val categoryRepository: CategoryRepository.All,
    private val noteLiveDataWrapper: NoteLiveDataWrapper.Mutable,
    private val noteListLiveDataWrapper: ListLiveDataWrapper.Edit,
    private val repository: NotesRepository.Edit,
    private val navigation: Navigation.Update,
    private val clear: ClearViewModels,
    private val dispatcher: CoroutineDispatcher,
    private val dispatcherMain: CoroutineDispatcher,
    private val now: Now
) : ViewModel(), NoteLiveDataWrapper.Read {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    val categoryLiveData: LiveData<List<CategoryUi>>
        get() = _categoryLiveData
    private val _categoryLiveData: MutableLiveData<List<CategoryUi>> = MutableLiveData()

    fun init(noteId: Long) {
        viewModelScope.launch(dispatcher) {
            val note = repository.note(noteId)
            withContext(dispatcherMain) {
                _categoryLiveData.value = categoryRepository.categories().map { it.toUi() }
                noteLiveDataWrapper.update(note.toUi())
            }
        }
    }

    fun deleteNote(noteId: Long) {
        viewModelScope.launch(dispatcher) {
            val categoryId = repository.note(noteId).categoryId()
            val result = remoteNotesRepository.delete(
                sharedPreferencesRepository.getUserId(),
                noteId,
                categoryId
            )
            if (result.isSuccess()) {
                repository.deleteNote(noteId)
                withContext(dispatcherMain) {
                    noteListLiveDataWrapper.delete(noteId)
                    comeback()
                }
            } else {
                println(result.message())
            }
        }
    }

    fun updateNote(
        noteId: Long,
        newTitle: String,
        newText: String,
        categoryId: Long,
        currentCategory: Long
    ) {
        viewModelScope.launch(dispatcher) {
            val result = remoteNotesRepository.update(
                sharedPreferencesRepository.getUserId(),
                noteId,
                newTitle,
                newText,
                now.timeInMillis(),
                categoryId
            )
            if (result.isSuccess()) {
                repository.updateNote(noteId, newTitle, newText, categoryId)
                withContext(dispatcherMain) {
                    if (categoryId != currentCategory && currentCategory != ALL_CATEGORY)
                        noteListLiveDataWrapper.delete(noteId)
                    else
                        noteListLiveDataWrapper.updateOrCreate(
                            noteId,
                            newTitle,
                            newText,
                            now.timeInMillis(),
                            categoryId
                        )
                }
            } else {
                println(result.message())
            }
        }
    }

    fun comeback() {
        clear.clear(this.javaClass)
        navigation.update(Screen.Pop)
    }

    override fun liveData(): LiveData<NoteUi> = noteLiveDataWrapper.liveData()

    companion object {
        private const val ALL_CATEGORY = 0L
    }
}