data class DendroPlotTree(val root: DendroPlotNode)


data class DendroPlotNode(
    val item: Node,
    val parent: DendroPlotNode?,
    val nodeNumber: Int = 1
) {
    var x: Int = -1
    var y: Double = parent?.let { parent.y + item.distanceFromParent } ?: 0.0
    var children: List<DendroPlotNode>? = item.children?.mapIndexed { index, child ->
        DendroPlotNode(child, this, index + 1)
    }
    val contour = mutableMapOf<Double, Double>()

    //    val siblingDistance: Int = 0
//    val treeDistance: Int = 0
    var mod: Int = 0

    fun isLeaf() = this.children == null

    fun isFarLeft() = if (this.parent != null) {
        true
    } else {
        this.parent?.children?.get(0) == this
    }

    fun isFarRight() = if (this.parent == null) {
        true
    } else {
        this.parent.children?.get(this.parent.children!!.count() - 1) == this
    }

    fun getPrevSibling(): DendroPlotNode? {
        if (this.parent == null || this.isFarLeft()) {
            return null
        }
        return this.parent.children?.get(this.parent.children!!.indexOf(this) - 1)
    }

    fun getNextSibling(): DendroPlotNode? {
        if (this.parent == null || this.isFarRight()) {
            return null
        }
        return this.parent.children?.get(this.parent.children!!.indexOf(this) + 1)
    }

    fun getFarLeftSibling() = if (this.parent == null) {
        null
    } else if (this.isFarLeft()) {
        this
    } else {
        this.parent.children!![0]
    }

    fun getFarLeftChild() = if (this.children == null) {
        null
    } else {
        this.children!![0]
    }

    fun getFarRightChild() = if (this.children == null) {
        null
    } else {
        this.children!![children!!.count() - 1]
    }

    fun countContour(node: DendroPlotNode = this, modSum: Double = 0.0) {
        if (!node.contour.contains(node.y)) {
            node.contour[node.y] = node.x + modSum
        } else {
            node.contour[node.y] = (contour[node.y]!!).coerceAtMost(node.x + modSum)
        }
        node.children?.forEach { countContour(it, modSum + node.mod) }
    }
}

class DendroTreeDraw<T> {
    var nodeSize = 1
    var siblingDistance = 0
    var treeDistance = 0

    private fun calculateInitialX(node: DendroPlotNode) {
        node.children?.forEach { calculateInitialX(it) }

        //if no children
        if (node.isLeaf()) {
            // if there is a previous sibling in this set, set X to prevous sibling + designated distance
            // if this is the first node in a set, set X to 0
            if (!node.isFarLeft())
                node.x = node.getPrevSibling()!!.x.plus(siblingDistance)
            else
                node.x = 0;
        }
        // if there is only one child
        else if (node.children!!.count() == 1) {
            // if this is the first node in a set, set it's X value equal to it's child's X value
            if (node.isFarLeft()) {
                node.x = node.children!![0].x
            } else {
                node.x = node.getPrevSibling()?.x?.plus(siblingDistance)!!
                node.mod = node.x - node.children!![0].x;
            }
        } else {
            var leftChild = node.getFarLeftChild()
            var rightChild = node.getFarRightChild()
            var mid = (leftChild!!.x + rightChild!!.x) / 2

            if (node.isFarLeft()) {
                node.x = mid
            } else {
                node.x = node.getPrevSibling()!!.x + siblingDistance
                node.mod = node.x - mid
            }
        }
        if (node.children!!.isNotEmpty() && !node.isFarLeft())
        //check for conflicts and shift tree right if needed
            checkForConflicts(node)
    }

    fun checkForConflicts(node: DendroPlotNode) {
        var minDistance = treeDistance + nodeSize
        var shift = 0.0

        node.countContour()

        var sibling = node.getFarLeftSibling()
        val d = (node.y + 1) .. sibling?.contour!!.maxOf { it.value }.coerceAtMost(node.contour.maxOf { it.value })
        while (sibling != null && sibling != node) {
            sibling.countContour()

            for (i in 5..7) {

            }
            // TODO: 11/15/21
        }
    }

    fun centerNodesBetween(leftNode: DendroPlotNode, rightNode: DendroPlotNode) {
        val leftIndex = leftNode.parent?.children!!.indexOf(leftNode)
        val rightIndex = leftNode.parent.children!!.indexOf(rightNode)
        val numNodesBetween = (rightIndex - leftIndex) - 1

        if (numNodesBetween > 0) {
            var distanceBetweenNodes = (leftNode.x - rightNode.x) / (numNodesBetween + 1)

            var count = 1
            for (i in (leftIndex + 1)..rightIndex) {
                val middleNode = leftNode.parent.children!![i]
                val targetX = rightNode.x + (distanceBetweenNodes * count)
                val offset = targetX - middleNode.x
                middleNode.x += offset
                middleNode.mod += offset
                count++
            }
            checkForConflicts(leftNode)
        }
    }

    fun checkAllChildrenOnScreen(node: DendroPlotNode) {
        // TODO: 11/10/21
    }
}




