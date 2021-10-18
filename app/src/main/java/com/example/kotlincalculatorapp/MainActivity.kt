//
//  MainActivity.kt
//  MainActivity
//
//  Created by Adan Vivero on 10/18/21.
//  Copyright © 2020 Adan Vivero. All rights reserved.
//
package com.example.kotlincalculatorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var decimal = true
    private var operation = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.calculator_menu, menu)

        return true
    }

    fun numberAction(view: View)
    {
        if(view is Button)
        {
            if(view.text == ".")
            {
                if(decimal)
                    calculationsTextView.append(view.text)

                decimal = false
            }
            else
                calculationsTextView.append(view.text)

            operation = true
        }

    }
    fun operationAction(view: View)
    {
        if(view is Button && operation)
        {
            calculationsTextView.append(view.text)
            operation = false
            decimal = true
        }
    }
    fun clearAction(view: View) {
        calculationsTextView.text = ""
        resultsTextView.text = ""
    }
    /**
     * The two menu options that I have with the basic calculator and the logout
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.item1 -> {
                Toast.makeText(this, "basic Calculator", Toast.LENGTH_LONG).show()
                true
            }
            R.id.item2 -> {
                Toast.makeText(this, "Logout", Toast.LENGTH_LONG).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * We display the results when the equal button is pressed in the results text view
     */
    fun equalAction(view: View) {
        resultsTextView.text = calculateResults()
    }

    /**
     * We set everything to clear when we click the clear button
     * We do that in the calculations text view and the results text view
     */
    fun allClearAction(view: android.view.View) {
        calculationsTextView.text = ""
        resultsTextView.text = ""
    }

    /**
     * We delete whatever text by one character
     */
    fun deleteAction(view: android.view.View) {
        val length = calculationsTextView.length()
        if (length > 0) {
            calculationsTextView.text = calculationsTextView.text.subSequence(0, length - 1)
        }
    }
    /**
     * Calculation results are done here
     */
    private fun calculateResults(): String
    {
        val digitsOperators = digitsOperators() //
        if(digitsOperators.isEmpty()) return "" // If it is empty in the the area where we are doing the calculations, we return nothing

        val divMultExp = divMultExp(digitsOperators)
        if(digitsOperators.isEmpty()) return ""

        val result = addSubtractionCalculation(divMultExp)
        return result.toString()
    }
    /**
    This will separate all of the characters that are in the calculations text view into indexes to be able to be parsed through
     */
    private fun digitsOperators(): MutableList<Any> // Will have a collection of doubles and operators such as +, -, *, ^, /
    {
        val list = mutableListOf<Any>() // Create the list of numbers and operators
        var currentDigit = "" // We set the current one to nothing

        for(character in calculationsTextView.text)
        {
            // Loop through the text in the textView where we punch in the numbers
            if(character.isDigit() || character == '.')
                // will take into the first [0] as long as it is a number with or not a decimal. For example if we have 24.2, it will take it in as a whole as the first index.
                currentDigit += character
            else //Otherwise, it is an operator such as +, -, *, /, or ^ and will store that into the list.
            {
                // adds that number or decimal to the list,
                list.add(currentDigit.toDouble())
                // sets the current digit back to an empty string
                currentDigit = ""
                // Adds the character of that operator.
                list.add(character)
            }
        }
        if(currentDigit != "")
            list.add(currentDigit.toDouble())

        return list // Returns the whole list of the operations and the digits.
    }

    private fun addSubtractionCalculation(index: MutableList<Any>) : Double
    {
        var result = index[0] as Double

        for (i in index.indices)
        {
            if (index[i] is Char && i != index.lastIndex)
            {
                val operator = index[i] // the operator for + or -
                val next = index[i+1] as Double // takes in the next value
                if(operator == '+')
                {
                    // addition
                    result += next
                }
                if(operator == '-')
                {
                    // subtraction
                    result -= next
                }
            }
        }
            return result // return the result of the addition or subtraction
    }
    private fun divMultExp(index: MutableList<Any>): MutableList<Any> {
        var list = index // We set the list at index
        while(list.contains('*') || list.contains('/') || list.contains('^'))
        {
            // If it contains either of the three operations, then we go calculate whichever operator was prompted passing that operator onto the function of the calculation
            list = calcTimesDivMultExp(list)
        }
        return list
    }

    private fun calcTimesDivMultExp(index: MutableList<Any>): MutableList<Any> // Calculates the division, multiplication and exponents
    {
        val newList = mutableListOf<Any>() // New list where we will be implementing our calculations
        var restartIndex = index.size // restart at the index size if we decide to do more operations

        for(i in index.indices)
        {
            if(index[i] is Char && i != index.lastIndex && i < restartIndex)
            {
                val operator = index[i] // the current index is the operator that was passed down from divMultExp function
                val previous = index[i - 1] as Double // the previous index which is the first number inserted
                val next = index[i + 1] as Double // the next index which is the following number inserted
                when(operator)
                {
                    '*' ->
                    {
                        // We implement the multiplication operation
                        newList.add(previous * next)
                        restartIndex = i + 1
                    }

                    '^' ->
                    {
                        // We implement the exponent operation
                        newList.add(Math.pow(previous, next))
                        restartIndex = i + 1
                    }

                    '/' ->
                    {
                        // We implement the division operation
                        newList.add(previous / next)
                        restartIndex = i + 1
                    }
                    // Otherwise we found an addition or subtraction, and we will create a new list where we add the previous digit and the operator
                    else ->
                    {
                        newList.add(previous)
                        newList.add(operator)
                    }
                }
            }
            // if we decide to do further operations
            if(i > restartIndex)
                newList.add(index[i])
        }
        return newList
    }
}