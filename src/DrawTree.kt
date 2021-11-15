class DrawTree(val rootNode: Node, val parent: DrawTree? = null, val number: Int = 1) {
    val x = -1
    val y = rootNode.distanceFromParent
    val children = rootNode.children?.mapIndexed { index, child ->
        DrawTree(child, this, index + 1)
    }
    val mod = 0
    val thread: DrawTree? = null
    val ancestor = this
    val change = 0
    val shift = 0
    val mostLeftSibling: DrawTree? = null

}