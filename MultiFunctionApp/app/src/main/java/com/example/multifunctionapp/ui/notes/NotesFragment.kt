package com.example.multifunctionapp.ui.notes

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.multifunctionapp.R
import com.example.multifunctionapp.data.Note
import com.example.multifunctionapp.databinding.FragmentNotesBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class NotesFragment : Fragment() {
    
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var noteAdapter: NoteAdapter
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupFab()
        observeNotes()
    }
    
    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(
            emptyList(),
            onItemClick = { note ->
                val action = NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(note.id, note.title, note.content)
                findNavController().navigate(action)
            },
            onItemLongClick = { note ->
                showDeleteConfirmationDialog(note)
            }
        )
        
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }
    
    private fun setupFab() {
        binding.fabAddNote.setOnClickListener {
            val action = NotesFragmentDirections.actionNotesFragmentToNoteDetailFragment(-1L, "", "")
            findNavController().navigate(action)
        }
    }
    
    private fun observeNotes() {
        val database = (requireActivity().application as com.example.multifunctionapp.MultiFunctionApp).database
        val noteDao = database.noteDao()
        
        lifecycleScope.launch {
            noteDao.getAllNotes().collectLatest { notes ->
                noteAdapter.updateNotes(notes)
            }
        }
    }
    
    private fun showDeleteConfirmationDialog(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Delete") { _, _ ->
                deleteNote(note)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun deleteNote(note: Note) {
        val database = (requireActivity().application as com.example.multifunctionapp.MultiFunctionApp).database
        val noteDao = database.noteDao()
        
        lifecycleScope.launch {
            noteDao.deleteNote(note)
            Snackbar.make(binding.root, "Note deleted", Snackbar.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
