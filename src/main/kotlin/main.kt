import java.io.File
import java.lang.Exception
import java.lang.IllegalArgumentException

open class Node(var value: String, var nodes: MutableList<Node> = mutableListOf())

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
            loadedNodes[i].nodes = currentNodes
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
    private val savedNodes: ArrayList<Node> = arrayListOf()

    private fun checkIndex(nodeInd: Int) {
        if(nodeInd >= savedNodes.size || nodeInd < 0) {
            throw IllegalArgumentException("Index out of range")
        }
    }

    private fun checkIndex(nodeInd: Int, childInd: Int) {
        checkIndex(nodeInd)
        if(childInd >= savedNodes[nodeInd].nodes.size || childInd < 0) {
            throw IllegalArgumentException("Index out of range")
        }
    }

    fun addNode(value: String) {
        savedNodes.add(Node(value))
    }

    fun loadNode(fileName: String) {
        loadFromFile(fileName)?.let { savedNodes.add(it) }
    }

    fun saveNode(index: Int, fileName: String) {
        checkIndex(index)
        saveToFile(fileName, savedNodes[index])
    }

    fun removeNode(index: Int) {
        checkIndex(index)
        savedNodes.removeAt(index)
    }

    fun showNode(index: Int): Node {
        checkIndex(index)
        return savedNodes[index]
    }

    fun changeValue(index: Int, newValue: String) {
        checkIndex(index)
        savedNodes[index].value = newValue
    }

    fun getSize(): Int = savedNodes.size

    fun takeChild(nodeInd: Int, childInd: Int) {
        checkIndex(nodeInd, childInd)
        savedNodes.add(savedNodes[nodeInd].nodes[childInd])
    }

    fun addChild(nodeInd: Int, childInd: Int) {
        checkIndex(nodeInd)
        checkIndex(childInd)
        savedNodes[nodeInd].nodes.add(savedNodes[childInd])
    }

    fun removeChild(nodeInd: Int, childInd: Int) {
        checkIndex(nodeInd, childInd)
        savedNodes[nodeInd].nodes.removeAt(childInd)
    }
}


fun main() {
    val nodeOperator = NodeOperator()
    var line: String?
    while(true) {
        print("> ")
        line = readLine()
        if(line == null) {
            break
        }
        val args = line.split(" ")
        if(args.isEmpty()) {
            continue
        }
        when (args[0]) {
            "add" -> {
                if(args.size < 2) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.addNode(args[1])
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
                nodeOperator.saveNode(args[1].toInt(), args[2])
            }
            "rm" -> {
                if(args.size < 2) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.removeNode(args[1].toInt())
            }
            "show" -> {
                if(args.size < 2) {
                    println("Wrong number of arguments")
                    continue
                }
                val ans = nodeOperator.showNode(args[1].toInt())
                println(ans.value)
                for (i in ans.nodes) {
                    print(i.value + " ")
                }
                println()
            }
            "size" -> {
                println(nodeOperator.getSize())
            }
            "list" -> {
                for (i in 0 until nodeOperator.getSize()) {
                    print(nodeOperator.showNode(i).value + " ")
                }
                println()
            }
            "change" -> {
                if(args.size < 3) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.changeValue(args[1].toInt(), args[2])
            }
            "take_child" -> {
                if(args.size < 3) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.takeChild(args[1].toInt(), args[2].toInt())
            }
            "add_child" -> {
                if(args.size < 3) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.addChild(args[1].toInt(), args[2].toInt())
            }
            "rm_child" -> {
                if(args.size < 3) {
                    println("Wrong number of arguments")
                    continue
                }
                nodeOperator.removeChild(args[1].toInt(), args[2].toInt())
            }
            "exit" -> {
                break
            }
            else -> {
                println("Unknown command " + args[0])
                continue
            }
        }
    }
}