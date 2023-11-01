import WordTree
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Wordle class creates wordle object; sets a random winning word, keeps track of the number of guesses used,
 * uses wordTree to check validity of word
 */
class Wordle {
    var m_WordTree: WordTree? = null //all available words
    var numGuesses = 0
        private set
    private var winningWord: String = "abcde" //choose randomly
    private var winningWordLetterMap: Map<Char, Int>? = null

    /**
     * Empty constructor
     */
    constructor()

    /**
     * Wordle Constructor; makes wordle object with a path to a configuration file
     * Reads config file and creates wordTree with vocab path in config file
     * Sets number of guesses and random word.
     * @param configPath the file path to a file that configures the number of guesses and vocab file
     */
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(configPath: String?) {
        try {
            val configFile = File(configPath)
            val scanner = Scanner(configFile)
            while (scanner.hasNextLine()) {
                val data = scanner.nextLine()
                if (data.contains("VocabPATH")) {
                    val index = data.indexOf('\"')
                    val vocabPath = data.substring(index + 1, data.length - 1)
                    m_WordTree = WordTree(vocabPath)
                }
            }
            numGuesses = MAX_NUM_GUESSES
            setWord()
        } catch (exception: IOException) {
            println("config file not found")
        }
    }

    /**
     * Allows controllers to restart wordle without needed to create a new Wordle object
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun restart() {
        numGuesses = MAX_NUM_GUESSES
        setWord()
    }

    /**
     * First checks if guessed word is winning word.
     * Check letters of guessed word against winning word.
     * If they match spots, 1 is added to array; if letter in guessed word is in winning word, add 2 to array;
     * else, 3 is added
     * @param guessedWord the string the user entered as a guess
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun validateLetter(guessedWord: String): IntArray {

        //if you don't run out of guesses, you can continue to guess after winning
        val validLetterArray = IntArray(guessedWord.length)
        if (guessedWord == winningWord) {
            Arrays.fill(validLetterArray, 1)
        } else {
            val winningWordCharMap = createWordCharMap(winningWord)
            //Identify and record letters that need to be green
            for (i in 0 until guessedWord.length) {
                val curLetter = guessedWord[i]
                if (curLetter == winningWord!![i]) {
                    validLetterArray[i] = 1
                    var value = winningWordCharMap[curLetter]!!
                    value--
                    if (value == 0) {
                        winningWordCharMap.remove(curLetter)
                    } else {
                        winningWordCharMap.replace(curLetter, value)
                    }
                }
            }

            //Identify and record letters that aren't green
            for (i in 0 until guessedWord.length) {
                val curLetter = guessedWord[i]
                //If winning word contains that letter and hasn't already been set to green then it's in the wrong spot
                if (winningWordCharMap.containsKey(curLetter) && validLetterArray[i] != 1) {
                    validLetterArray[i] = 2
                    var value = winningWordCharMap[curLetter]!!
                    value--
                    if (value == 0) {
                        winningWordCharMap.remove(curLetter)
                    } else {
                        winningWordCharMap.replace(curLetter, value)
                    }
                } else if (validLetterArray[i] != 1) {
                    validLetterArray[i] = 3
                }
            }
        }
        return validLetterArray
    }

    //TODO: remove
    @RequiresApi(Build.VERSION_CODES.O)
    fun printWordTree() {
        m_WordTree?.printWordTree()
    }

    /**
     * Helper method to create a map of the letters in the
     * @param word the word to make a character map of
     * @return the hashmap of the characters and the amount of each in the word passed in
     */
    private fun createWordCharMap(word: String?): HashMap<Char, Int> {
        val winningWordArray = word!!.toCharArray()
        val returnedHashMap = HashMap<Char, Int>()
        for (c in winningWordArray) {
            if (returnedHashMap.containsKey(c)) {
                returnedHashMap[c] = returnedHashMap[c]!! + 1
            } else {
                returnedHashMap[c] = 1
            }
        }
        return returnedHashMap
    }

    /**
     * Uses WordTree method checkWord() to check if the guessed word is an available word in the dictionary
     * @param guessedWord the word guessed by the user
     * @return true if the word is in the word tree
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isWord(guessedWord: String): Boolean {
        return m_WordTree?.checkWord(guessedWord) ?: false
    }

    /**
     * Checks if the guessed word is the winning word
     * Decrements num guesses
     * @param guessedWord the word the user guessed
     */
    fun checkWord(guessedWord: String): Boolean {
        --numGuesses
        return guessedWord == winningWord
    }

    /**
     * Picks a random number and finds that place in the WordTree to select a random word as the winning word.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun setWord() {
        //get random number and get that location from wordtree
        val range: Int = m_WordTree?.returnTree()?.size ?: -1
        val rand = (Math.random() * range).toInt() + 1

        //No way to directly get a specific index from TreeSet; converted to arraylist
        val wordTreeArrayList: ArrayList<Any?>? = m_WordTree?.returnTree()?.let { ArrayList<Any?>(it) }
        word = wordTreeArrayList?.get(rand)?.toString()
    }

    val tree: WordTree?
        get() = m_WordTree
    var word: String?
        get() = winningWord
        /**
         * Allows Tester to set winning word easily and helper method to set the winning word.
         * @param winningWord the word to be set as the winning word
         */
        set(winningWord) {
            if (winningWord != null) {
                this.winningWord = winningWord
            }
            winningWordLetterMap = createWordCharMap(winningWord)
        }

    companion object {
        private const val MAX_NUM_GUESSES = 6
    }
}
