package com.perapps.notes.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.perapps.notes.R

class AddOwnerDialog : DialogFragment() {

    private var positiveListener: ((String) -> Unit)? = null

    fun setPositiveListener(listener: (String) -> Unit) {
        positiveListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val emailEditText = LayoutInflater.from(requireContext()).inflate(
            R.layout.layout_add_owner_email,
            requireActivity().findViewById(R.id.clNoteContainer),
            false
        ) as TextInputLayout

        return MaterialAlertDialogBuilder(requireContext())
            .setIcon(R.drawable.ic_person_add)
            .setTitle("Add Owner")
            .setMessage("Enter an E-mail of a person you want to share the note with.")
            .setView(emailEditText)
            .setPositiveButton("Yes") { _, _ ->
                val email =
                    emailEditText.findViewById<EditText>(R.id.etAddOwnerEmail).text.toString()
                positiveListener?.let {
                    it(email)
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }
}