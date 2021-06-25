package com.perapps.notes.ui.addeditnote

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.perapps.notes.R
import com.perapps.notes.data.local.entities.Note
import com.perapps.notes.databinding.FragmentAddEditNoteBinding
import com.perapps.notes.databinding.FragmentNotesBinding
import com.perapps.notes.other.Constants.DEFAULT_NOTE_COLOR
import com.perapps.notes.other.Constants.KEY_LOGGED_IN_EMAIL
import com.perapps.notes.other.Constants.NO_EMAIL
import com.perapps.notes.other.Status
import com.perapps.notes.prefstore.PrefsStore
import com.perapps.notes.prefstore.dataStore
import com.perapps.notes.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.sasikanth.colorsheet.ColorSheet
import dev.sasikanth.colorsheet.utils.ColorSheetUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class AddEditNoteFragment : BaseFragment(R.layout.fragment_add_edit_note) {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddEditNoteViewModel by viewModels()

    private val args: AddEditNoteFragmentArgs by navArgs()

    private var curNote: Note? = null
    private var curNoteColor = DEFAULT_NOTE_COLOR

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddEditNoteBinding.bind(view)

        if (args.id.isNotEmpty()) {
            viewModel.getNoteById(args.id)
            subscribeToObservers()
        }

        setListeners()
    }

    private fun setListeners() {
        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {
                saveNote()
                navController.popBackStack()
            }
        }

        binding.viewNoteColor.setOnClickListener {
            ColorSheet().colorPicker(
                colors = resources.getIntArray(R.array.colors),
                listener = { color ->
                    val colorHex = ColorSheetUtils.colorToHex(color)
                    changeViewNoteColor(colorHex)
                }
            ).show(parentFragmentManager)
        }
        binding.btnBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun subscribeToObservers() {
        viewModel.note.observe(viewLifecycleOwner, { event ->
            event?.getContentIfHandled()?.let { result ->
                when (result.status) {
                    Status.SUCCESS -> {
                        val note = result.data!!
                        curNote = note
                        binding.etNoteTitle.setText(note.title)
                        binding.etNoteContent.setText(note.content)
                        changeViewNoteColor(note.color)
                    }
                    Status.ERROR -> {
                        showSnackBar(result.message ?: "An error occurred")
                    }
                    Status.LOADING -> {

                    }
                }
            }
        })
    }

    private suspend fun saveNote() {
        val authEmail = getAuthEmail()

        val title = binding.etNoteTitle.text.toString()
        val content = binding.etNoteContent.text.toString()
        if (title.isEmpty() || content.isEmpty()) {
            return
        }
        val date = System.currentTimeMillis()
        val color = curNoteColor

        val id = curNote?.id ?: UUID.randomUUID().toString()
        val ownersList = curNote?.owners ?: listOf(authEmail)

        val note = Note(title, content, date, ownersList, color, id = id)
        viewModel.insertNote(note)
    }

    private suspend fun getAuthEmail(): String {
        val pref = requireContext().dataStore.data.first()
        return pref[PrefsStore.LOGGED_IN_USER_EMAIL] ?: NO_EMAIL
    }

    private fun changeViewNoteColor(colorString: String) {
        val drawable = ResourcesCompat.getDrawable(resources, R.drawable.circle_shape, null)
        drawable?.let {
            val wrappedDrawable = DrawableCompat.wrap(it)
            val color = Color.parseColor(colorString)
            DrawableCompat.setTint(wrappedDrawable, color)
            binding.viewNoteColor.background = wrappedDrawable
            curNoteColor = colorString
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}