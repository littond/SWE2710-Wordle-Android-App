import android.os.Build
import androidx.annotation.RequiresApi
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * Class used for checking validity of words
 */
@RequiresApi(Build.VERSION_CODES.O)
class WordTree
/**
 * Primary constructor
 * @param vocabFilePath the path to the vocab file
 * @throws IOException when the file cannot be accessed
 */(vocabFilePath: String) {
    private val wordTree: MutableCollection<String>?
    private val head: Node

    init {
        wordTree = TreeSet()
        head = Node("")
        loadVocabulary(Paths.get(vocabFilePath))
        generateLetterTreeFromVocabulary()
    }

    //TODO: remove
    fun printWordTree() {
        if (wordTree != null) {
            val iterator: Iterator<String> = wordTree.iterator()
            while (iterator.hasNext()) {
                println(iterator.next())
            }
        }
    }

    /**
     * Get words from vocabulary file
     * @param path the path to the vocab file
     * @throws IOException when the file cannot be accessed
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @Throws(IOException::class)
    private fun loadVocabulary(path: Path) {
        BufferedReader(InputStreamReader(
            Files.newInputStream(path))).use { reader ->
            while (reader.ready()) {
                val line = reader.readLine()
                wordTree!!.add(line)
            }
        }
    }

    /**
     * Generate a letter tree from the vocabulary
     */
    private fun generateLetterTreeFromVocabulary() {
        for (word in wordTree!!) {
            populateLetterTree(word, head)
        }
    }

    /**
     * Add letters to the letter tree
     * @param remainingLetters the letters remaining in the current word
     * @param currentNode the current node in the tree
     */
    private fun populateLetterTree(remainingLetters: String, currentNode: Node) {
        if (remainingLetters.length >= 1) {
            val currentLetter = remainingLetters[0].toString()
            var childNode = currentNode.getChild(currentLetter)
            if (childNode == null) {
                childNode = Node(currentLetter)
                currentNode.addChild(childNode)
            }
            if (remainingLetters.length == 1) {
                childNode.markAsEndOfWord()
            } else {
                populateLetterTree(remainingLetters.substring(1), childNode)
            }
        }
    }

    /**
     * Recursively search for a word in the tree
     * @param index the current index in the word
     * @param letters the letters in the word
     * @param partialWord the part of the word that is valid so far
     * @param parentNode the parent of the current node
     * @return true if the word is valid or false if not
     */
    private fun recursiveSearch(index: Int, letters: CharArray, partialWord: String, parentNode: Node): Boolean {
        val currentLetter = letters[index]
        val currentNode = parentNode.getChild("" + currentLetter)
        if (currentNode != null) {
            val newWord = partialWord + currentLetter
            return if (index == letters.size - 1 && currentNode.isEndOfWord) {
                true
            } else recursiveSearch(index + 1, letters, newWord, currentNode)
        }
        return false
    }

    /**
     * Check if a word is in the vocabulary
     * @param guessedWord the word to check
     */
    fun checkWord(guessedWord: String): Boolean {
        val letters = guessedWord.toCharArray()
        return recursiveSearch(0, letters, "", head)
    }

    /**
     * Return the word tree
     * @return the tree
     */
    fun returnTree(): Collection<String>? {
        return wordTree
    }

    /**
     * Subclass for creating the letter tree
     */
    private class Node(private var value: String) {
        private val children: ArrayList<Node> = ArrayList()

        fun addChild(child: Node) {
            children.add(child)
        }

        fun markAsEndOfWord() {
            if (!this.isEndOfWord) {
                value += "*"
            }
        }

        val isEndOfWord: Boolean
            get() = value.endsWith("*")

        fun getChild(childValue: String): Node? {
            for (child in children) {
                if (child.value == childValue || child.value == "$childValue*") {
                    return child
                }
            }
            return null
        }
    }
}
