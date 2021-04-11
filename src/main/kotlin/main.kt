import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

open class Node(var value: String, var nodes: List<Node> = listOf())

fun loadFromFile(fileName: String): Node? {
    return try {
        val loadedNodes: ArrayList<Node> = arrayListOf()
        val reader = File(fileName).bufferedReader()
        val size = reader.readLine().toInt()
        for (i in 0 until size) {
            loadedNodes.add(Node(reader.readLine()))
        }

        for (i in 0 until size) {
            val numbers = reader.readLine().split(" ").map { it.toInt() }
            val currentNodes: MutableList<Node> = mutableListOf()
            for (j in numbers) {
                currentNodes.add(loadedNodes[j])
            }
            loadedNodes[i].nodes = currentNodes.toList()
        }
        reader.close()

        loadedNodes[0]
    }
    catch (e: Exception) {
        null
    }
}

fun saveToFile(fileName: String, root: Node?) {
    try {
        val writer = File(fileName).bufferedWriter()
        if(root == null) {
            writer.write(0)
            return
        }
        val reachableNodes: ArrayList<Node> = arrayListOf()
        val number: MutableMap<Node, Int> = mutableMapOf()

        fun dfs(v: Node) {
            number[v] = reachableNodes.size
            reachableNodes.add(v)
            for(to in v.nodes) {
                if(number[to] == null) {
                    dfs(to)
                }
            }
        }; dfs(root)

        writer.write(reachableNodes.size.toString())
        writer.newLine()
        for (i in reachableNodes) {
            writer.write(i.value)
            writer.newLine()
        }
        for (i in reachableNodes) {
            for(child in i.nodes) {
                number[child]?.let { writer.write(it.toString()) }
                writer.write(" ")
            }
            writer.newLine()
        }

        writer.close()
    }
    catch (e: Exception) {}
}

class NodeOperator {
    val SavedNodes: ArrayList<Node> = arrayListOf();

    fun addIsolatedNode(value: String) {
        SavedNodes.add(Node(value))
    }

    fun loadNode(fileName: String) {
        loadFromFile(fileName)?.let { SavedNodes.add(it) }
    }

    fun saveNode(fileName: String, index: Int) {
        if(index >= SavedNodes.size) {
            throw IllegalArgumentException("Index out of range")
        }
        saveToFile(fileName, SavedNodes[index])
    }
}


fun main() {
    val nodeOperator = NodeOperator()
    var line: String?
    while(true) {
        line = readLine()
        if(line == null) {
            break
        }
        val args = line.split(" ")
        if(args.isEmpty()) {
            continue
        }
        when (args[0]) {
            "add_isolated" -> {
                if(args.size < 2) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.addIsolatedNode(args[1])
            }
            "load" -> {
                if(args.size < 2) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.loadNode(args[1])
            }
            "save" -> {
                if(args.size < 3) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.saveNode(args[1], args[2].toInt())
            }
        }
    }
}