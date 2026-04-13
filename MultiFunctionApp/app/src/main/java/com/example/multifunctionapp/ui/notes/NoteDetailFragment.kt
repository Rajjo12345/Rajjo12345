package com.example.multifunctionapp.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.multifunctionapp.data.Note
import com.example.multifunctionapp.databinding.FragmentNoteDetailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class NoteDetailFragment : Fragment() {
    
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding get() = _binding!!
    
    private val args: NoteDetailFragmentArgs by navArgs()
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        if (args.noteId != -1L) {
            binding.editTextTitle.setText(args.title)
            binding.editTextContent.setText(args.content)
        }
        
        setupSaveButton()
    }
    
    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            saveNote()
        }
    }
    
    private fun saveNote() {
        val title = binding.editTextTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()
        
        if (title.isEmpty()) {
            binding.editTextTitle.error = "Title is required"
            binding.editTextTitle.requestFocus()
            return
        }
        
        if (content.isEmpty()) {
            binding.editTextContent.error = "Content is required"
            binding.editTextContent.requestFocus()
            return
        }
        
        val database = (requireActivity().application as com.example.multifunctionapp.MultiFunctionApp).database
        val noteDao = database.noteDao()
        
        lifecycleScope.launch {
            val note = Note(
                id = if (args.noteId != -1L) args.noteId else 0,
                title = title,
                content = content,
                timestamp = if (args.noteId != -1L) System.currentTimeMillis() else System.currentTimeMillis()
            )
            
            if (args.noteId != -1L) {
                noteDao.updateNote(note)
                Snackbar.make(binding.root, "Note updated", Snackbar.LENGTH_SHORT).show()
            } else {
                noteDao.insertNote(note)
                Snackbar.make(binding.root, "Note saved", Snackbar.LENGTH_SHORT).show()
            }
            
            findNavController().popBackStack()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
