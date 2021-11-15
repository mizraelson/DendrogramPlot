
data class Node(
    val content: Any,
    val parent: Node?,
    val children: List<Node>?,
    val distanceFromParent: Double)

data class Tree(val root: Node?)