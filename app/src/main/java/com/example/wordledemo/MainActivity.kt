package com.example.wordledemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.unit.min

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black
            ) {
                CharacterGrid(5, 6)
            }


        }
    }
}


@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterGrid(lettersInWord: Int, numberOfRows: Int) {
    //test()
    // list of characters
    var characters by remember { mutableStateOf(" ".repeat(lettersInWord * numberOfRows)) }

    // list of colors
    var colors by remember { mutableStateOf(List(lettersInWord * numberOfRows) { Color.Black }) }

    // list of characters
    var keyboardCharacters by remember { mutableStateOf("QWERTYUIOPASDFGHJKLZXCVBNM") }

    // list of colors
    var keyboardColors by remember { mutableStateOf(List(26) { Color.LightGray }) }

    // next character placement position
    var currentChar by remember { mutableStateOf(0)}

    // reference word thing
    val winningWord by remember { mutableStateOf("DOING") }

    var maxIndex by remember { mutableStateOf(4) }
    var minIndex by remember { mutableStateOf(0) }


    // Grid Generation
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyVerticalGrid(
            cells = GridCells.Fixed(lettersInWord),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(colors) { index, color ->
                Box(
                    modifier = Modifier
                        .size(75.dp)
                        .background(color = color)
                        .border(1.dp, color = Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = characters[index].toString(), // sets text to " " initially
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Keyboard
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                for (c in 0..9) {
                    Button(
                        onClick = {
                            if (currentChar <= maxIndex) {
                                characters = addCharacter(characters, keyboardCharacters[c], currentChar)
                                currentChar++
                            }
                        },
                        modifier = Modifier
                            .width(32.dp) // Set a smaller width for the buttons
                            .padding(horizontal = 2.dp, vertical = 4.dp), // Reduce padding
                        contentPadding = PaddingValues(2.dp), // Adjust the content padding
                        colors = ButtonDefaults.buttonColors(keyboardColors[c])
                    ) {
                        Text(text = keyboardCharacters[c].toString(), fontSize = 10.sp) // Set a smaller font size for the text
                    }
                    // keyboardCharacters[c].toString()
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                for (c in 10..18) {
                    Button(
                        onClick = {
                            if (currentChar <= maxIndex) {
                                characters = addCharacter(characters, keyboardCharacters[c], currentChar)
                                currentChar++
                            }
                        },
                        modifier = Modifier
                            .width(32.dp) // Set a smaller width for the buttons
                            .padding(horizontal = 2.dp, vertical = 4.dp), // Reduce padding
                        contentPadding = PaddingValues(2.dp), // Adjust the content padding
                        colors = ButtonDefaults.buttonColors(keyboardColors[c])
                    ) {
                        Text(text = keyboardCharacters[c].toString(), fontSize = 10.sp) // Set a smaller font size for the text
                    }
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row {
                Button(
                    onClick = {
                        if (currentChar == maxIndex + 1 && maxIndex != numberOfRows * lettersInWord - 1) {
                            var guess = getGuess(characters, minIndex, maxIndex)
                            keyboardColors = updateKeyboardColors(keyboardColors, guess, winningWord)
                            colors = setGridColors(colors, winningWord, maxIndex, minIndex, guess)
                            maxIndex += 5
                            minIndex += 5
                        }
                    },
                    modifier = Modifier
                        .width(32.dp) // Set a smaller width for the buttons
                        .padding(horizontal = 2.dp, vertical = 4.dp), // Reduce padding
                    contentPadding = PaddingValues(2.dp), // Adjust the content padding
                    colors = ButtonDefaults.buttonColors(Color.LightGray)
                ) {
                    Text(text = "sub", fontSize = 10.sp) // Set a smaller font size for the text
                }
                for (c in 19..25) {
                    Button(
                        onClick = {
                            if (currentChar <= maxIndex) {
                                characters = addCharacter(characters, keyboardCharacters[c], currentChar)
                                currentChar++
                            }
                        },
                        modifier = Modifier
                            .width(32.dp) // Set a smaller width for the buttons
                            .padding(horizontal = 2.dp, vertical = 4.dp), // Reduce padding
                        contentPadding = PaddingValues(2.dp), // Adjust the content padding
                        colors = ButtonDefaults.buttonColors(keyboardColors[c])
                    ) {
                        Text(text = keyboardCharacters[c].toString(), fontSize = 10.sp) // Set a smaller font size for the text
                    }
                }
                Button(
                    onClick = {
                        if (currentChar > minIndex) {
                            currentChar--
                            characters = deleteCharacter(characters, currentChar)
                            colors = deleteColor(colors, currentChar)
                        }
                    },
                    modifier = Modifier
                        .width(32.dp) // Set a smaller width for the buttons
                        .padding(horizontal = 2.dp, vertical = 4.dp), // Reduce padding
                    contentPadding = PaddingValues(2.dp), // Adjust the content padding
                    colors = ButtonDefaults.buttonColors(Color.LightGray)
                ) {
                    Text(text = "del", fontSize = 10.sp) // Set a smaller font size for the text
                }
            }
        }
    }
}

fun setGridColors(colors: List<Color>, winningWord: String, maxIndex: Int, minIndex: Int, guess: String): List<Color> {
    val valArr = validateWord(winningWord, guess)
    val updatedColors = colors.toMutableList()

    for (i in minIndex..maxIndex) {
        when (valArr[i-minIndex]) {
            1 -> updatedColors[i] = Color(32, 111, 17)
            2 -> updatedColors[i] = Color(218, 218, 93)
            3 -> updatedColors[i] = Color.DarkGray
            else -> updatedColors[i] = Color.Red
        }
    }

    return updatedColors
}

//--------------------------------------------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.N)
fun updateKeyboardColors(keyboardColors: List<Color>, guess: String, winningWord: String): List<Color> {
    val valArr = validateWord(winningWord, guess)
    val keyboardChar = "QWERTYUIOPASDFGHJKLZXCVBNM"

    val updatedColors = keyboardColors.toMutableList()
    for (i in 0 until 5) {
        val index = keyboardChar.indexOf(guess[i])
        when (valArr[i]) {
            1 -> updatedColors[index] = Color(32, 111, 17)
            2 -> updatedColors[index] = Color(218, 218, 93)
            3 -> updatedColors[index] = Color.DarkGray
            else -> updatedColors[index] = Color.Red
        }
    }
    return updatedColors.toList()
}

fun getGuess(characters: String, minIndex: Int, maxIndex: Int): String {
    var word = ""
    for (i in minIndex..maxIndex) {
        word += characters[i]
    }
    return word
}

fun deleteCharacter(characters: String, index: Int): String {
    // get current array
    val cells = characters.toMutableList()

    // set character at index
    cells[index] = ' '

    // return array and rerender it
    return cells.toCharArray().joinToString(separator = "") { it.toString() }
}

fun deleteColor(colors: List<Color>, index: Int): List<Color> {
    val updatedColors = colors.toMutableList()

    updatedColors[index] = Color.Black

    return updatedColors
}

fun addCharacter(characters: String, character: Char, index: Int): String {
    // get current array, set character at index,
    val cells = characters.toMutableList()
    cells[index] = character

    // return array and rerender it
    return cells.toCharArray().joinToString(separator = "") { it.toString() }
}

// ------------------ Wordle methods ---------------


fun validateWord(winningWord: String, guessedWord: String): IntArray {
    if (winningWord.equals(guessedWord)) {
        return IntArray(5) {1}
    }
    val intArr = IntArray(5)
    for (i in winningWord.indices) {
        if (winningWord[i] == guessedWord[i]) {
            intArr[i] = 1
        } else if (winningWord.contains(guessedWord[i])) {
            intArr[i] = 2
        } else {
            intArr[i] = 3
        }
    }
    return intArr
}