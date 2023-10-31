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

class MainActivity : ComponentActivity() {

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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterGrid(lettersInWord: Int, numberOfRows: Int) {
    // list of characters
    var characters by remember { mutableStateOf(" ".repeat(lettersInWord * numberOfRows)) }

    // list of colors
    var colors by remember { mutableStateOf(List(lettersInWord * numberOfRows) { Color.Black }) }

    // list of characters
    var keyboardCharacters by remember { mutableStateOf("QWERTYUIOPASDFGHJKLZXCVBNM") }

    // list of colors
    var keyboardColors by remember { mutableStateOf(List(24) { Color.LightGray }) }

    // next character placement position
    var currentChar by remember { mutableStateOf(0)}

    // reference word thing
    var winningWord by remember { mutableStateOf("HELLO") }

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
                                //colors = Color(colors, winningWord, keyboardCharacters[c], currentChar)
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
                for (c in 10..18) {
                    Button(
                        onClick = {
                            if (currentChar <= maxIndex) {
                                characters = addCharacter(characters, keyboardCharacters[c], currentChar)
                                //colors = addColor(colors, winningWord, keyboardCharacters[c], currentChar)
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
                            keyboardColors = updateKeyboardColors(keyboardColors, characters, maxIndex, minIndex, winningWord)
                            colors = setGridColors(colors, winningWord, maxIndex, minIndex, characters)
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
                for (c in 19..23) {
                    Button(
                        onClick = {
                            if (currentChar <= maxIndex) {
                                characters = addCharacter(characters, keyboardCharacters[c], currentChar)
                                //colors = addColor(colors, winningWord, keyboardCharacters[c], currentChar)
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

fun setGridColors(
    keyboardColors: List<Color>,
    winningWord: String,
    maxIndex: Int,
    minIndex: Int,
    characters: String
): List<Color> {
    val updatedColors = keyboardColors.toMutableList()
    val characters = characters.toList()

    for (i in minIndex..maxIndex) {
        if (winningWord.contains(characters[i])) {
            updatedColors[i] = Color.Yellow
        } else {
            updatedColors[i] = Color.DarkGray
        }
    }

    return updatedColors
}

fun updateKeyboardColors(keyboardColors: List<Color>, characters: String, maxIndex: Int, minIndex: Int, winningWord: String): List<Color> {
    val updatedColors = keyboardColors.toMutableList()

    for (i in minIndex..maxIndex) {
        val str = "QWERTYUIOPASDFGHJKLZXCVBNM"
        if (winningWord.contains(characters[i])) {
            updatedColors[str.indexOf(characters[i])] = Color.Yellow
        } else {
            updatedColors[str.indexOf(characters[i])] = Color.DarkGray
        }
    }
    return updatedColors
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

fun addColor(colors: List<Color>, winningWord: String, character: Char, index: Int): List<Color> {
    val updatedColors = colors.toMutableList()

    if (winningWord.contains(character)) {
        updatedColors[index] = Color.Yellow
    } else {
        updatedColors[index] = Color.Red
    }

    return updatedColors
}