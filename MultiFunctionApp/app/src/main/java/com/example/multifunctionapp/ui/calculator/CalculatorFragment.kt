package com.example.multifunctionapp.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.multifunctionapp.databinding.FragmentCalculatorBinding
import net.objectek.calculator.Calculator

class CalculatorFragment : Fragment() {
    
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    
    private var currentInput = ""
    private var result = ""
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupButtons()
    }
    
    private fun setupButtons() {
        binding.apply {
            button0.setOnClickListener { appendNumber("0") }
            button1.setOnClickListener { appendNumber("1") }
            button2.setOnClickListener { appendNumber("2") }
            button3.setOnClickListener { appendNumber("3") }
            button4.setOnClickListener { appendNumber("4") }
            button5.setOnClickListener { appendNumber("5") }
            button6.setOnClickListener { appendNumber("6") }
            button7.setOnClickListener { appendNumber("7") }
            button8.setOnClickListener { appendNumber("8") }
            button9.setOnClickListener { appendNumber("9") }
            
            buttonPlus.setOnClickListener { appendOperator("+") }
            buttonMinus.setOnClickListener { appendOperator("-") }
            buttonMultiply.setOnClickListener { appendOperator("*") }
            buttonDivide.setOnClickListener { appendOperator("/") }
            
            buttonDecimal.setOnClickListener { 
                if ("." !in currentInput) {
                    appendNumber(".")
                }
            }
            
            buttonClear.setOnClickListener { clearAll() }
            buttonEquals.setOnClickListener { calculateResult() }
            buttonBackspace.setOnClickListener { backspace() }
        }
    }
    
    private fun appendNumber(number: String) {
        currentInput += number
        updateDisplay()
    }
    
    private fun appendOperator(operator: String) {
        if (currentInput.isNotEmpty() && currentInput.last().isDigit()) {
            currentInput += operator
            updateDisplay()
        }
    }
    
    private fun clearAll() {
        currentInput = ""
        result = ""
        updateDisplay()
    }
    
    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
            updateDisplay()
        }
    }
    
    private fun calculateResult() {
        try {
            // Simple evaluation - in production, use a proper expression parser
            val expression = currentInput.replace("×", "*").replace("÷", "/")
            val scriptEngine = javax.script.ScriptEngineManager().getEngineByName("JavaScript")
            val calculatedResult = scriptEngine?.eval(expression)?.toString() ?: "Error"
            result = calculatedResult
            currentInput = calculatedResult
            updateDisplay()
        } catch (e: Exception) {
            result = "Error"
            updateDisplay()
        }
    }
    
    private fun updateDisplay() {
        binding.textviewDisplay.text = currentInput.ifEmpty { "0" }
        binding.textviewResult.text = if (result.isNotEmpty()) "= $result" else ""
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
