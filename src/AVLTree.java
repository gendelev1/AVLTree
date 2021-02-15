/**
 * name: Avital Gendelev
318266020
userName: gendelev1

name: Mirit Hadas
305248262
userName: mirithadas

 */
/**
 * 
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */

public class AVLTree {
	private IAVLNode root;
	public static int counterKeysToArray; 	// a static field used for the helper recursive method called from the method keysToArray()
	public static int counterInfoToArray;	// a static field used for the helper recursive method called from the method infoToArray()

	/**
	 * public AVLTree(IAVLNode root)
	 *
	 * constructs a tree with a given node as the root
	 * complexity: O(1)
	 */
	public AVLTree(IAVLNode root) { //Constructor
		this.root = root;
	}

	/**
	 * public AVLTree()
	 *
	 * empty constructor - constructs a tree null as root
	 * complexity: O(1)
	 *
	 */
	public AVLTree() { //Constructor
		this(null);
	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 * complexity: O(1)
	 */
	public boolean empty() { //Function to check if the tree is empty
		return (this.root == null || this.root.getKey() == -1);
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 * complexity: O(height of tree) = O(logn)
	 */
	public String search(int k) { //Function to search a node with key k
		IAVLNode curr = this.root;
		while(curr != null){
			if(curr.getKey() == k) {
				return curr.getValue(); //if the key of the node is k, returns the val of the node
			}
			else if (curr.getKey() > k) { //if the key of the node is bigger than k, the pointer will get the left node
				curr = curr.getLeft();
			}
			else {
				curr = curr.getRight(); //if the key of the node is smaller than k, the pointer will get the right node
			}
		}
		return null;
	}

	/**
	 * public IAVLNode searchPlaceInsert(int k)
	 *
	 * returns a place where we want to insert the node + update the sizes of the nodes from the root to that place
	 * used only as helper method for insert(int k, String i)
	 *
	 * complexity: O(height of tree) = O(logn)
	 */
	public IAVLNode searchPlaceInsert(int k) {
		IAVLNode curr = this.root;
		IAVLNode prev = this.root;
		while(curr != null && curr.getKey() != -1) {
			//if we want to insert the node, we need to add 1 to the sizes of all his parents
			curr.setSize(curr.getSize() + 1);//update the size
			if(curr.getKey() > k) { //if the key of the node is bigger than k, the pointer will get the left node
				prev = curr; //points to the last node visited
				curr = curr.getLeft();
			}
			else {
				prev = curr; //points to the last node visited
				curr = curr.getRight(); //if the key of the node is smaller than k, the pointer will get the right node
			}
		}
		return prev;
	}

	/**
	 * public IAVLNode searchAndReturn(int k, String condition)
	 *
	 * returns a node with key k + update the sizes if it's called from the method delete(int k) from the root to the returned node
	 * used only as helper method for delete(int k) and split(int x)
	 *
	 * complexity: O(height of tree) = O(logn)
	 */
	public IAVLNode searchAndReturn(int k, String condition) {
		IAVLNode curr = this.root;
		while(curr != null){
			if(condition.equals("delete")) {
				curr.setSize(curr.getSize() - 1);//if we want to delete the node, we need to dec 1 from the sizes of all his parents
			}
			if(curr.getKey() == k) {
				return curr; //if the key of the node is k, returns the node
			}
			else if (curr.getKey() > k) { //if the key of the node is bigger than k, the pointer will get the left node
				curr = curr.getLeft();
			}
			else {
				curr = curr.getRight(); //if the key of the node is smaller than k, the pointer will get the right node
			}
		}
		return null;
	}



	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree.
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
	 * promotion/rotation - counted as one rebalance operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k already exists in the tree.
	 *
	 * complexity: O(height of tree) = O(logn)
	 */
	public int insert(int k, String i) { //Function to insert a node with key k and info i
		boolean leaf = false;
		int count = 2;
		if(search(k) != null) { //if an item with key k already exists returns -1
			return -1;
		}
		IAVLNode curr;
		curr = searchPlaceInsert(k);
		if(curr == null) { //if the tree is empty it will be the root
			this.root = new AVLNode(i, k);
			return count;
		}
		if(curr.getLeft().getKey() == -1 && curr.getRight().getKey() == -1) { //The place where we want to insert is a leaf
			leaf = true;
		}
		IAVLNode newNode = new AVLNode(i, k); //creating a new node
		if(curr.getKey() > k) { //if the key of the node is bigger than k, the node will be inserted in the left side
			curr.setLeft(newNode);
			curr.getLeft().setHeight(0); //update the height to be 0;
			curr.getLeft().setSize(1); //update the size to be 1
			curr.getLeft().setParent(curr);
			curr = curr.getLeft();
		}
		else {
			curr.setRight(newNode); //else - in the right side
			curr.getRight().setHeight(0); //update the height to be o
			curr.getRight().setSize(1); //update the size to be 1
			curr.getRight().setParent(curr);
			curr = curr.getRight();
		}
		if (leaf) {
			count = count + rebalance(curr);
		}
		return count;
	}

	/**
	 * public int rebalance(IAVLNode node)
	 *
	 * receives a node as input, rebalances the entire tree and returns the number of rebalancing operations required
	 * if no operations were done, returns 0
	 *
	 * serves as a helper method called from either insert(int k) or join(IAVLNode x, AVLTree t)
	 * as we've learned in class rebalancing the tree after insert and join takes O(logn)
	 *
	 * complexity: O(height of tree) = O(logn)
	 */
	public int rebalance(IAVLNode node) {
		int count = 0;
		while (getRankDiff(node) == 0) { //Problem: rank difference is 0
			if((node.getParent().getHeight() - node.getParent().getLeft().getHeight()) + (node.getParent().getHeight() - (node.getParent().getRight().getHeight())) != 2){
				node.getParent().setHeight(node.getParent().getHeight() + 1);
				count = count + 1;
				node = node.getParent();
			}
			else {
				if(oneTwoLeft(node)) {//left rank difference: 1 and right rank difference: 2
					RightRotate(node);
					count = count + 2; //update the count
				}
				else if(twoOneRight(node)) {//left rank difference: 2 and right rank difference: 1 (the symmetry case)
					LeftRotate(node);
					count = count + 2; //update the count
				}
				else if(twoOneLeft(node)) {//left rank difference: 2 and right rank difference: 1
					doubleRotateLeftRight(node);
					count = count + 5; //update the count
				}
				else if(oneTwoRight(node)) {//left rank difference: 1 and right rank difference: 2 (the symmetry case)
					doubleRotateRightLeft(node);
					count = count + 5; //update the count
				}
				else if(onlyInJoinRight(node)) {//left rank difference: 1 and right rank difference: 1
					LeftRotate(node);
					if(node.getParent() == null) {
						this.root = node;
					}
					node.setHeight(Math.max(node.getRight().getHeight(), node.getLeft().getHeight()) + 1);
				}
				else if(onlyInJoinLeft(node)) {//left rank difference: 1 and right rank difference: 1 (the symmetry case)
					RightRotate(node);
					if(node.getParent() == null) {
						this.root = node;
					}
					node.setHeight(Math.max(node.getRight().getHeight(), node.getLeft().getHeight()) + 1);//update the height
				}
				if(node.getParent() == null) {
					this.root = node;
				}
				node = node.getParent();//going up until the rank difference is not 0 anymore
				if(node == null) {
					return count;
				}
			}

		}
		return count;
	}

	/**
	 * public int getRankDiff(IAVLNode node)
	 *
	 * returns the rank difference between the node and his parent
	 * if the node is the root, returns -1
	 *
	 * complexity: O(1)
	 */
	public int getRankDiff(IAVLNode node) {
		if(node.getParent() != null) {	//checks if the node has parent
			return node.getParent().getHeight() - node.getHeight(); //returns the rank difference
		}
		else {
			return -1;//returns -1 if this is the root
		}
	}

	/**
	 * public boolean oneTwoLeft(IAVLNode node)
	 *
	 * returns true if the current node has left rank difference: 1 and right rank difference: 2 and if it's a left child to her parent node
	 *
	 * complexity: O(1)
	 */
	public boolean oneTwoLeft(IAVLNode node) {
		if(node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 2) {
			return node.getParent().getLeft() == node;
		}
		return false;
	}

	/**
	 * public boolean oneTwoRight(IAVLNode node)
	 *
	 * returns true if the current node has left rank difference: 1 and right rank difference: 2 and if it's a right child to her parent node
	 *
	 * complexity: O(1)
	 */
	public boolean oneTwoRight(IAVLNode node) {//function that returns true if the current node has left rank difference: 1 and right rank difference: 2 (the symmetry case)
		if(node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 2) {
			return node.getParent().getRight() == node;
		}
		return false;
	}

	/**
	 * public boolean twoOneLeft(IAVLNode node)
	 *
	 * returns true if the current node has left rank difference: 2 and right rank difference: 1 and if it's a left child to her parent node
	 *
	 * complexity: O(1)
	 */
	public boolean twoOneLeft(IAVLNode node) {
		if(node.getHeight() - node.getLeft().getHeight() == 2 && node.getHeight() - node.getRight().getHeight() == 1) {
			return node.getParent().getLeft() == node;
		}
		return false;
	}

	/**
	 * public boolean twoOneRight(IAVLNode node)
	 *
	 * returns true if the current node has left rank difference: 2 and right rank difference: 1 and if it's a right child to her parent node
	 *
	 * complexity: O(1)
	 */
	public boolean twoOneRight(IAVLNode node) {
		if(node.getHeight() - node.getLeft().getHeight() == 2 && node.getHeight() - node.getRight().getHeight() == 1) {
			return node.getParent().getRight() == node;
		}
		return false;
	}

	/**
	 * public boolean onlyInJoinLeft(IAVLNode node)
	 *
	 * returns true if the current node has left rank difference: 1 and right rank difference: 1 and if it's a left child to her parent node
	 * relevant only when rebalancing the tree during the method join
	 *
	 * complexity: O(1)
	 */
	public boolean onlyInJoinLeft(IAVLNode node) {
		if(node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 1) {
			return node.getParent().getLeft() == node;
		}
		return false;
	}

	/**
	 * public boolean onlyInJoinRight(IAVLNode node)
	 *
	 * returns true if the current node has left rank difference: 1 and right rank difference: 1 and if it's a right child to her parent node
	 * relevant only when rebalancing the tree during the method join
	 *
	 * complexity: O(1)
	 */
	public boolean onlyInJoinRight(IAVLNode node) {
		if(node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 1) {
			return node.getParent().getRight() == node;
		}
		return false;
	}

	/**
	 * public void LeftRotate(IAVLNode node)
	 *
	 * performs a left rotate on the edge between the node and her parent
	 * updated the height and size of the two relevant nodes
	 * a rotation is a terminal operation, therefore O(1)
	 *
	 * complexity: O(1)
	 */
	public void LeftRotate(IAVLNode node) { //Function to do a left rotate
		IAVLNode prevParent = node.getParent().getParent();
		IAVLNode tmpParent = node.getParent();
		IAVLNode tmpLeft = node.getLeft();

		node.setLeft(tmpParent);  //the parent of the node will be inserted as his left subTree
		node.getLeft().setParent(node);//update the parents
		node.setParent(prevParent);//connecting the tree
		if(node.getParent() != null) {
			if(node.getKey() > node.getParent().getKey()) {
				node.getParent().setRight(node);
			}
			else {
				node.getParent().setLeft(node);
			}
		}
		node = node.getLeft();

		node.setRight(tmpLeft); //moving the original left subtree to be a right subTree of the moved node
		node.getRight().setParent(node);//update the parents

		updateHeightAndSizeInRotate(node);	//update size and height of node and its new parent
	}

	/**
	 * public void RightRotate(IAVLNode node)
	 *
	 * performs a right rotate on the edge between the node and her parent
	 * updated the height and size of the two relevant nodes
	 * a rotation is a terminal operation, therefore O(1)
	 *
	 * complexity: O(1)
	 */
	public void RightRotate(IAVLNode node) { //Function to do a right rotate
		IAVLNode prevParent = node.getParent().getParent();
		IAVLNode tmpParent = node.getParent();
		IAVLNode tmpRight = node.getRight();

		node.setRight(tmpParent); //the parent of the node will be inserted as his right subTree
		node.getRight().setParent(node);//update the parents
		node.setParent(prevParent);//connecting the tree
		if(node.getParent() != null) {
			if(node.getKey() > node.getParent().getKey()) {
				node.getParent().setRight(node);
			}
			else {
				node.getParent().setLeft(node);
			}
		}
		node = node.getRight();

		node.setLeft(tmpRight); //moving the original right subtree to be a left subTree of the moved node
		node.getLeft().setParent(node);//update the parents
		
		updateHeightAndSizeInRotate(node); //update size and height of node and its new parent
	}

	/**
	 * public void updateHeightAndSizeInRotate(IAVLNode node)
	 *
	 * updates size and height to the input node and its parent
	 *
	 * complexity: O(1)
	 */
	private void updateHeightAndSizeInRotate(IAVLNode node) {
		node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1); //update the heights
		node.getParent().setHeight(Math.max(node.getParent().getLeft().getHeight(), node.getParent().getRight().getHeight()) + 1);//update the height of the parent
		node.setSize(1 + node.getLeft().getSize()+ node.getRight().getSize()); //update the size
		node.getParent().setSize(1 + node.getParent().getLeft().getSize()+ node.getParent().getRight().getSize());//update the size of the parent
	}

	/**
	 * public void doubleRotateLeftRight(IAVLNode node)
	 *
	 * performs a double rotate - a left rotate on the edge between the node and her parent, and then a right rotate on the edge between the node and her new parent
	 * updated the height and size of the relevant nodes
	 * a rotation is a terminal operation, therefore O(1)
	 *
	 * complexity: O(1)
	 */
	public void doubleRotateLeftRight(IAVLNode node) {
		node = node.getRight();
		IAVLNode tmpLeft = node.getLeft();
		IAVLNode tmpRight = node.getRight();
		LeftRotate(node);
		RightRotate(node);
		updatePointersAfterDoubleRotate(node, tmpLeft, tmpRight);

	}


	/**
	 * public void doubleRotateRightLeft(IAVLNode node)
	 *
	 * performs a double rotate - a right rotate on the edge between the node and her parent, and then a left rotate on the edge between the node and her new parent
	 * updated the height and size of the relevant nodes
	 * a rotation is a terminal operation, therefore O(1)
	 *
	 * complexity: O(1)
	 */
	public void doubleRotateRightLeft(IAVLNode node) {
		node = node.getLeft();
		IAVLNode tmpLeft = node.getLeft();
		IAVLNode tmpRight = node.getRight();
		RightRotate(node);
		LeftRotate(node);
		updatePointersAfterDoubleRotate(node, tmpLeft, tmpRight);
	}

	/**
	 * public void changePointersAfterDoubleRotate(IAVLNode node, IAVLNode tmpLeft, IAVLNode tmpRight)
	 *
	 * updates fixed amount of pointers after a double rotate was performed
	 *
	 * complexity: O(1)
	 */
	private void updatePointersAfterDoubleRotate(IAVLNode node, IAVLNode tmpLeft, IAVLNode tmpRight) {
		node.getRight().setLeft(tmpRight);
		node.getLeft().setRight(tmpLeft);

		node.getRight().setSize(node.getRight().getRight().getSize() + node.getRight().getLeft().getSize() + 1);//update the size of the right node
		node.getLeft().setSize(node.getLeft().getRight().getSize() + node.getLeft().getLeft().getSize() + 1);//update the size of the left node
		node.setSize(1 + node.getLeft().getSize() + node.getRight().getSize());//update the size of the node

		node.getRight().setHeight(Math.max(node.getRight().getRight().getHeight(), node.getRight().getLeft().getHeight()) + 1);//update the height of the right node
		node.getLeft().setHeight(Math.max(node.getLeft().getRight().getHeight(), node.getLeft().getLeft().getHeight()) + 1);//update the height of the left node
		node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);//update the height of the node

		if(node.getParent() != null) {
			if(node.getKey() > node.getParent().getKey()) {
				node.getParent().setRight(node);
			}
			else {
				node.getParent().setLeft(node);
			}
		}
		if (node.getParent() == null) {
			this.root = node;
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there;
	 * the tree must remain valid (keep its invariants).
	 * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
	 * demotion/rotation - counted as one rebalance operation, double-rotation is counted as 2.
	 * returns -1 if an item with key k was not found in the tree.
	 *
	 * complexity: O(height of tree) = O(logn)
	 */
	public int delete(int k) {
		int count = 0;
		if (search(k) == null) {	//The node was not found in the tree;
			return -1;
		}
		IAVLNode node = searchAndReturn(k, "delete");//The node that we want to delete
		IAVLNode succ;
		++count;
		if ((this.getRoot().getKey()==node.getKey()) && (node.getSize() == 0)) { //our node is root and the only node in the tree
			this.root = new virtualNode(null);
		}
		else if ((this.getRoot().getKey()==node.getKey()) && (node.getSize() == 1)) { //our node is root and unary and his child is a leaf
			if (node.getRight().isRealNode()) { //our node is unary with right leaf
				this.root = node.getRight();
				node.getRight().setParent(null);
			}
			else {								//our node is unary with left leaf
				this.root = node.getLeft();
				node.getLeft().setParent(null);
			}
		}
		else if(twoSubTrees(node)) {
			succ = successor(node);
			((AVLTree.AVLNode) node).setKey(succ.getKey());//update the key
			((AVLTree.AVLNode) node).setValue(succ.getValue());//update the val
		}

		else if(twoLeavesRight(node)) {//our node is a leaf and he is a right leaf, his brother is a leaf too
			node.getParent().setRight(null);//the right sub tree is null after the deletion
			//the height didn't change
		}
		else if(twoLeavesLeft(node)) {//our node is a leaf and he is a Left leaf, his brother is a leaf too
			node.getParent().setLeft(null);//the left sub tree is null after the deletion
			//the height didn't change
		}
		else if(oneRightLeaf(node)) {//our node is a only right leaf
			node.getParent().setRight(null);//the right sub tree is null after the deletion
			count += rebalanceAfterDeletion(node);
		}
		else if(oneLeftLeaf(node)) {//our node is a only left leaf
			node.getParent().setLeft(null);//the left sub tree is null after the deletion
			count += rebalanceAfterDeletion(node);
		}
		else if(rightLeafAndSubTree(node)) {//our node is a right leaf and his brother is not a leaf
			node.getParent().setRight(null);//the right sub tree is null after the deletion
			count += rebalanceAfterDeletion(node);
		}
		else if(leftLeafAndSubTree(node)) {//our node is a left leaf and his brother is not a leaf
			node.getParent().setLeft(null);//the left sub tree is null after the deletion
			count += rebalanceAfterDeletion(node);
		}
		else if(leftUnaryWithRightNode(node)) {//our node is a left unary with right node
			IAVLNode successor = node.getRight();//the successor
			node.getParent().setLeft(successor);//delete the node
			successor.setParent(node.getParent());//update the parent
			if(!oneOne(node.getParent())) {
				count += rebalanceAfterDeletion(node);
			}
		}
		else if(leftUnaryWithLeftNode(node)) {//our node is a left unary with left node
			IAVLNode successor = node.getLeft();//the successor
			node.getParent().setLeft(successor);//delete the node
			successor.setParent(node.getParent());//update the parent
			if(!oneOne(node.getParent())) {
				count += rebalanceAfterDeletion(node);
			}
		}
		else if(rightUnaryWithRightNode(node)) {//our node is a right unary with right node
			IAVLNode successor = node.getRight();//the successor
			node.getParent().setRight(successor);//delete the node
			successor.setParent(node.getParent());//update the parent
			if(!oneOne(node.getParent())) {
				count += rebalanceAfterDeletion(node);
			}
		}
		else if(rightUnaryWithLeftNode(node)) {//our node is a right unary with left node
			IAVLNode successor = node.getLeft();//the successor
			node.getParent().setRight(successor);//delete the node
			successor.setParent(node.getParent());//update the parent
			if(!oneOne(node.getParent())) {
				count += rebalanceAfterDeletion(node);
			}
		}
		return count;
	}

	/**
	 * public IAVLNode successor(IAVLNode node)
	 *
	 * returns the successor of the input node
	 * follows the algorithm we've seen in class
	 *
	 * complexity: O(height of the tree) = O(logn)
	 */
	public IAVLNode successor(IAVLNode node) {
		IAVLNode prev = node.getParent();
		if(node.getRight().getKey() != -1) {//We have a right sub tree;
			node = node.getRight();//going right
			return this.minAndReturn(node);//return the minimum of the right sub tree
		}
		else {
			while(prev != null && node == prev.getRight()) {//going up and staying the right sub tree
				node = prev;//going up
				prev = prev.getParent();//going up
			}
		}
		return prev;
	}

	/**
	 * public int rebalanceAfterDeletion (IAVLNode node)
	 *
	 * receives a node as input, rebalances the entire tree and returns the number of rebalancing operations required
	 * if no operations were done, returns 0
	 *
	 * serves as a helper method called from delete(int k)
	 * as we've learned in class rebalancing the tree after delete takes O(logn)
	 *
	 * complexity: O(height of tree) = O(logn)
	 */
	public int rebalanceAfterDeletion (IAVLNode node) {
		int count = 0;
		while(node.getParent() != null) {
			node = node.getParent();//going up
			if(twoTwo(node)){//function that returns true if the current node has left rank difference: 2 and right rank difference: 2
				count = count + 1;
			}
			else if(threeOne(node)) {//function that returns true if the current node has left rank difference: 3 and right rank difference: 1
				if(oneOne(node.getRight()) || twoOne(node.getRight())) {//The right sub tree: left rank difference: 1 right rank difference: 1 or left rank difference: 2 right rank difference: 1
					LeftRotate(node.getRight());
					count = count + 2;	//update the count
				}
				else if(oneTwo(node.getRight())) {//The right sub tree: left rank difference: 1 right rank difference: 2
					doubleRotateRightLeft(node.getRight());
					count = count + 5; //update the count
				}
			}
			else if(oneThree(node)) {//function that returns true if the current node has left rank difference: 1 and right rank difference: 3
				if(oneOne(node.getLeft()) || twoOne(node.getLeft())) {//The left sub tree: left rank difference: 1 right rank difference: 1 or left rank difference: 2 right rank difference: 1
					RightRotate(node.getLeft());
					count = count + 2;	//update the count
				}
				else if(oneTwo(node.getLeft())) {//The left sub tree: left rank difference: 1 right rank difference: 2
					doubleRotateLeftRight(node.getLeft());
					count = count + 5; //update the count
				}
			}
			node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);
		}
		this.root = node;
		node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);
		return count;
	}

	/**
	 * public boolean twoSubTrees (IAVLNode node)
	 *
	 * returns true if the left and right nodes are sub trees (not leaves)
	 *
	 * complexity: O(1)
	 */
	public boolean twoSubTrees (IAVLNode node) {
		return node.getLeft().getHeight() >= 0 && node.getRight().getHeight() >= 0;
	}

	/**
	 * public boolean twoLeavesLeft (IAVLNode node)
	 *
	 * returns true if the node is a left leaf and his brother is also a leaf
	 *
	 * complexity: O(1)
	 */
	public boolean twoLeavesLeft (IAVLNode node) {
		if(node.getHeight() == 0 && node.getParent().getLeft() == node) {//our node is a leaf and he is a left leaf
			return node.getParent().getRight().getHeight() == 0;				//The right node is also a leaf
		}
		return false;
	}

	/**
	 * public boolean twoLeavesRight (IAVLNode node)
	 *
	 * returns true if the node is a right leaf and his brother is also a leaf
	 *
	 * complexity: O(1)
	 */
	public boolean twoLeavesRight (IAVLNode node) {
		if(node.getHeight() == 0 && node.getParent().getRight() == node) {//our node is a leaf and he is a right leaf
			return node.getParent().getLeft().getHeight() == 0;				//The left node is also a leaf
		}
		return false;
	}

	/**
	 * public boolean oneRightLeaf (IAVLNode node)
	 *
	 * returns true if the node is a right leaf and has no brother (its brother is a virtual node)
	 *
	 * complexity: O(1)
	 */
	public boolean oneRightLeaf (IAVLNode node) {
		if(node.getHeight() == 0 && node.getParent().getRight() == node) {//our node is a leaf and he is a right leaf
			return node.getParent().getLeft().getKey() == -1;				//There is no left node
		}
		return false;
	}

	/**
	 * public boolean oneLeftLeaf (IAVLNode node)
	 *
	 * returns true if the node is a left leaf and has no brother (its brother is a virtual node)
	 *
	 * complexity: O(1)
	 */
	public boolean oneLeftLeaf (IAVLNode node) {
		if(node.getHeight() == 0 && node.getParent().getLeft() == node) {//our node is a leaf and he is a left leaf
			return node.getParent().getRight().getKey() == -1;				//There is no right node
		}
		return false;
	}

	/**
	 * public boolean leftLeafAndSubTree (IAVLNode node)
	 *
	 * returns true if the node is a left leaf and the right sub tree is not a leaf
	 *
	 * complexity: O(1)
	 */
	public boolean leftLeafAndSubTree (IAVLNode node) {
		if(node.getHeight() == 0 && node.getParent().getLeft() == node) {//our node is a leaf and he is a left leaf
			return node.getParent().getRight().getHeight() > 0;				//The sub right tree is not a leaf
		}
		return false;
	}

	/**
	 * public boolean rightLeafAndSubTree
	 *
	 * returns true if the node is a right leaf and the left sub tree is not a leaf
	 *
	 * complexity: O(1)
	 */
	public boolean rightLeafAndSubTree (IAVLNode node) {
		if(node.getHeight() == 0 && node.getParent().getRight() == node) {//our node is a leaf and he is a right leaf
			return node.getParent().getLeft().getHeight() > 0;				//The sub left tree is not a leaf
		}
		return false;
	}

	/**
	 * public boolean leftUnaryWithRightNode (IAVLNode node)
	 *
	 * returns true if the node is a left unary with only right node
	 *
	 * complexity: O(1)
	 */
	public boolean leftUnaryWithRightNode (IAVLNode node) {
		if(node.getParent().getLeft() == node) {	//our node is a left node
			return node.getRight().getHeight() >= 0 && node.getLeft().getKey() == -1;	// there is right node and no left node
		}
		return false;
	}

	/**
	 * public boolean leftUnaryWithLeftNode (IAVLNode node)
	 *
	 * returns true if the node is a left unary with only left node
	 *
	 * complexity: O(1)
	 */
	public boolean leftUnaryWithLeftNode (IAVLNode node) {
		if(node.getParent().getLeft() == node) { 	//our node is a left node
			return node.getLeft().getHeight() >= 0 && node.getRight().getKey() == -1;	// there is left node and no right node
		}
		return false;
	}

	/**
	 * public boolean RightUnaryWithRightNode (IAVLNode node)
	 *
	 * returns true if the node is a right unary with only right node
	 *
	 * complexity: O(1)
	 */
	public boolean rightUnaryWithRightNode (IAVLNode node) {
		if(node.getParent().getRight() == node) {	//our node is a right node
			return node.getRight().getHeight() >= 0 && node.getLeft().getKey() == -1;	// there is right node and no left node
		}
		return false;
	}

	/**
	 * public boolean RightUnaryWithLeftNode (IAVLNode node)
	 *
	 * returns true if the node is a right unary with only left node
	 *
	 * complexity: O(1)
	 */
	public boolean rightUnaryWithLeftNode (IAVLNode node) {
		if(node.getParent().getRight() == node) {	//our node is a right node
			return node.getLeft().getHeight() >= 0 && node.getRight().getKey() == -1;	// there is left node and no right node
		}
		return false;
	}

	/**
	 * public boolean twoTwo(IAVLNode node)
	 *
	 * returns true if the node is a 2,2 node
	 *
	 * complexity: O(1)
	 */
	public boolean twoTwo(IAVLNode node) {
		return node.getHeight() - node.getLeft().getHeight() == 2 && node.getHeight() - node.getRight().getHeight() == 2;
	}

	/**
	 * public boolean oneOne(IAVLNode node)
	 *
	 * returns true if the node is a 1,1 node
	 *
	 * complexity: O(1)
	 */
	public boolean oneOne(IAVLNode node) {//left rank difference: 1 and right rank difference: 1
		return node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 1;
	}

	/**
	 * public boolean oneTwo(IAVLNode node)
	 *
	 * returns true if the node is a 1,2 node
	 *
	 * complexity: O(1)
	 */
	public boolean oneTwo(IAVLNode node) {//left rank difference: 1 and right rank difference: 2
		return node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 2;
	}

	/**
	 * public boolean twoOne(IAVLNode node)
	 *
	 * returns true if the node is a 2,1 node
	 *
	 * complexity: O(1)
	 */
	public boolean twoOne(IAVLNode node) {//left rank difference: 2 and right rank difference: 1
		return node.getHeight() - node.getLeft().getHeight() == 2 && node.getHeight() - node.getRight().getHeight() == 1;
	}

	/**
	 * public boolean threeOne(IAVLNode node)
	 *
	 * returns true if the node is a 3,1 node
	 *
	 * complexity: O(1)
	 */
	public boolean threeOne(IAVLNode node) {//left rank difference: 3 and right rank difference: 1
		return node.getHeight() - node.getLeft().getHeight() == 3 && node.getHeight() - node.getRight().getHeight() == 1;
	}

	/**
	 * public boolean oneThree(IAVLNode node)
	 *
	 * returns true if the node is a 1,3 node
	 *
	 * complexity: O(1)
	 */
	public boolean oneThree(IAVLNode node) {//left rank difference: 1 and right rank difference: 3
		return node.getHeight() - node.getLeft().getHeight() == 1 && node.getHeight() - node.getRight().getHeight() == 3;
	}


	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty
	 *
	 * complexity: O(height of the tree) = O(logn)
	 */
	public String min()
	{
		if(this.empty()) {//If the tree is empty, return null
			return null;
		}
		IAVLNode curr = this.root;
		IAVLNode prev = this.root;
		while(curr.getKey() != -1) {//going left
			prev = curr;
			curr = curr.getLeft();
		}
		return prev.getValue();
	}

	/**
	 * public IAVLNode minAndReturn(IAVLNode root)
	 *
	 * Returns the node with the smallest key in the tree,
	 * or null if the tree is empty
	 *
	 * complexity: O(height of the tree) = O(logn)
	 */
	public IAVLNode minAndReturn(IAVLNode root)	{
		if (root == null || !root.isRealNode()) {//If the tree is empty, return null
			return null;
		}
		IAVLNode curr = root;
		IAVLNode prev = root;
		while (curr.getKey() != -1) {//going left
			prev = curr;
			curr = curr.getLeft();
		}
		return prev;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty
	 *
	 * complexity: O(height of the tree) = O(logn)
	 */
	public String max()
	{
		if(this.empty()) {//If the tree is empty, return null
			return null;
		}
		IAVLNode curr = this.root;
		IAVLNode prev = this.root;
		while(curr.getKey() != -1) {//going right
			prev = curr;
			curr = curr.getRight();
		}
		return prev.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 *
	 * complexity: O(n)
	 */
	public int[] keysToArray() {
		int[] keys = new int[this.size()]; // new array at the size of the tree
		counterKeysToArray = 0;		// assign the static field to zero
		keysToArrayRec(keys, this.root); // calling to the recursive function
		return keys;
	}

	/**
	 * public void keysToArrayRec(int[] array, IAVLNode curr)
	 *
	 * assigns the counterKeysToArray index in the array to be the key of the curr node
	 *
	 * complexity: O(curr.size)
	 */
	public void keysToArrayRec(int[] array, IAVLNode curr) {//A recursive function that builds sorted array of the keys in inorder
		if(curr.getKey() == -1) {//If the tree is empty, return null
			return;
		}
		keysToArrayRec(array, curr.getLeft()); //going left
		array[counterKeysToArray++] = curr.getKey(); //Adding the key to the array
		keysToArrayRec(array, curr.getRight());//going right
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 *
	 * complexity: O(n)
	 */
	public String[] infoToArray() {
		String[] info = new String[this.size()]; //New array at the size of the tree
		counterInfoToArray = 0;	// assign the static field to zero
		infoToArrayRec(info, this.root); //Calling to the recursive function
		return info;
	}

	/**
	 * public void infoToArrayRec(String[] array, IAVLNode curr)
	 *
	 * assigns the counterInfoToArray index in the array to be the value of the curr node
	 *
	 * complexity: O(curr.size)
	 */
	public void infoToArrayRec(String[] array, IAVLNode curr) {
		if(curr.getKey() == -1) {//If the tree is empty, return null
			return;
		}
		infoToArrayRec(array, curr.getLeft()); //going left
		array[counterInfoToArray++] = curr.getValue(); //Adding the info to the array
		infoToArrayRec(array, curr.getRight());//going right
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none
	 * postcondition: none
	 *
	 * complexity: O(1)
	 */
	public int size()
	{
		if(this.empty()) {//The tree is empty
			return 0;
		}
		return this.root.getSize(); //Calculating the size
	}

	/**
	 * public IAVLNode getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none
	 * postcondition: none
	 *
	 * complexity: O(1)
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public int join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree.
	 * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	 * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
	 * postcondition: none
	 *
	 * complexity: O(|tree.rank-t.rank| + 1)
	 */
	public int join(IAVLNode x, AVLTree t) {
		AVLTree smaller = new AVLTree();
		AVLTree bigger = new AVLTree();
		if(t.empty()) {//if t is empty we will insert x in the tree
			this.insert(x.getKey(), x.getValue());
			return this.getRoot().getHeight() + 1;
		}
		if(this.empty()){//if tree is empty we will insert x in t
			this.root  = smaller.root;
			this.insert(x.getKey(), x.getValue());
			return smaller.getRoot().getHeight() + 1;
		}
		if(this.empty() && t.empty()) {
			return 1;
		}
		if(this.getRoot().getHeight() < t.getRoot().getHeight()) {//if the height of our tree is less than the height of t
			smaller.root = this.getRoot();
			bigger.root = t.getRoot();
		}
		else {
			smaller.root = t.getRoot();
			bigger.root = this.getRoot();
		}

		int ret = Math.abs(bigger.root.getHeight() - smaller.getRoot().getHeight()) + 1;
		IAVLNode curr = bigger.root;
		if(x.getKey() < bigger.root.getKey()) { //keys(x,t) < keys()
			while(smaller.getRoot().getHeight() < curr.getHeight()) { // going down until rank <= k
				curr = curr.getLeft();
			}
			x.setLeft(smaller.root);//connecting the left sub tree
			x.setRight(curr);//connecting the right sub tree
			if(curr.getParent() == null) {
				bigger.root = x;//update the root if x is the root (curr does not have a parent)
			}
			else {
				x.setParent(curr.getParent());//connecting the rest of the tree
				curr.getParent().setLeft(x);//update the parent of the rest of the tree
			}
		}
		else {//keys(x,t) > keys()
			while(smaller.getRoot().getHeight() < curr.getHeight()) { // going down until rank <= k
				curr = curr.getRight();
			}
			x.setLeft(curr);//connecting the left sub tree
			x.setRight(smaller.root);//connecting the right sub tree
			if(curr.getParent() == null) {
				bigger.root = x;//update the root if x is the root (curr does not have a parent)
			}
			else {
				x.setParent(curr.getParent());//connecting the rest of the tree
				curr.getParent().setRight(x);//update the parent of the rest of the tree
			}
		}
		x.getLeft().setParent(x);//update the parent of left sub tree
		x.getRight().setParent(x);//update the parent of right sub tree
		x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);//update the height
		x.setSize(1 + x.getLeft().getSize() + x.getRight().getSize());//update the size
		rebalance(x.getLeft());
		rebalance(x.getRight());
		rebalance(x);//if needed
		if(this.root.getHeight() < bigger.root.getHeight()) {
			this.root = bigger.root;//update the root to be the root of the bigger tree
		}
		curr = x.getParent();
		while(curr != null) {
			curr.setHeight(Math.max(curr.getLeft().getHeight(), curr.getRight().getHeight()) + 1);//update the height
			curr.setSize(1 + curr.getLeft().getSize() + curr.getRight().getSize());//update the size
			curr = curr.getParent();
		}

		return ret;
	}

	/**
	 * public AVLTree[] split(int x)
	 *
	 * splits the tree into 2 trees according to the key x.
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
	 * postcondition: none
	 *
	 * complexity: O(logn) (since the joins used are efficient)
	 */
	public AVLTree[] split(int x)
	{
		if (search(x)==null) {
			return null;
		}
		IAVLNode curr = searchAndReturn(x,"split");
		IAVLNode prev = curr.getParent();
		AVLTree smaller = new AVLTree();
		AVLTree bigger = new AVLTree();
		AVLTree t1 = new AVLTree();
		AVLTree t2 = new AVLTree();
		smaller.root = curr.getLeft();//sub tree that is smaller than node of x
		disconnectTheParent(smaller.getRoot());//the sub tree is not connected anymore
		bigger.root = curr.getRight();//sub tree that is bigger than node of x
		disconnectTheParent(bigger.getRoot());//the sub tree is not connected anymore

		while(curr != null && prev != null) {
			if(curr.getParent().getKey() < curr.getKey()) {//curr is the right sub tree of his parent
				curr = curr.getParent();//going up
				prev = prev.getParent();
				t1.root = curr.getLeft();
				curr = joinForSplit(curr, prev, smaller, t1);
			}
			else {//curr is the left sub tree of his parent
				curr = curr.getParent();//going up
				prev = prev.getParent();
				t2.root = curr.getRight();
				curr = joinForSplit(curr, prev, bigger, t2);
			}
		}
		AVLTree[] trees = new AVLTree[2];
		trees[0]  = smaller;
		trees[1]  = bigger;
		return trees;
	}

	/**
	 * public IAVLNode joinForSplit(IAVLNode curr, IAVLNode prev, AVLTree retTree, AVLTree helperTree)
	 *
	 * prepares the input arguments to the join operation
	 * joins them, and returns the root of the joined tree
	 *
	 * complexity: O(|tree.rank-t.rank| + 1), since it will be called only from split, we know it's actually O(1)
	 */
	private IAVLNode joinForSplit(IAVLNode curr, IAVLNode prev, AVLTree retTree, AVLTree helperTree) {
		disconnectTheParent(helperTree.root);//the sub tree is not connected anymore from the top
		disconnectTheSubTrees(curr);//the node is not connected anymore from the bottom
		if(curr.getParent() != null) {
			disconnectTheParent(curr);//the node is not connected anymore from the top
		}
		curr.setHeight(0);	//set the height to 0
		curr.setSize(1);	//set the size to 1
		if(retTree.root.getParent() != null) {
			disconnectTheParent(retTree.root);//the node is not connected anymore from the top
		}
		helperTree.join(curr, retTree);//join
		retTree.root = helperTree.getRoot();
		helperTree.getRoot().setParent(prev);//connecting the sub tree
		curr = helperTree.getRoot();
		return curr;
	}

	/**
	 * public void disconnectTheParent (IAVLNode node)
	 *
	 * disconnects the input node from its parent (reciprocally)
	 *
	 * complexity: O(1)
	 */
	public void disconnectTheParent (IAVLNode node) {
		if(node.getParent().getRight() == node) {
			node.getParent().setRight(null);
		}
		else {
			node.getParent().setLeft(null);
		}
		node.setParent(null);//the parent is null
	}

	/**
	 * public void disconnectTheParent (IAVLNode node)
	 *
	 * sets the input node's left and right child to be null
	 *
	 * complexity: O(1)
	 */
	public void disconnectTheSubTrees(IAVLNode node) {
		node.setLeft(null);//left sub tree is null
		node.setRight(null);//right sub tree is null
	}


	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{
		public int getKey(); //returns node's key (for virtual node return -1)
		public String getValue(); //returns node's value [info] (for virtual node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
		public void setHeight(int height); // sets the height of the node
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes
		public void setSize(int currSize);//sets the size
		public int getSize();//returns the size
	}


	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in
	 * another file.
	 * This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode {
		private String val;
		private int key;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;
		private int height;
		private int size;

		/**
		 * public AVLNode(String val, int key, IAVLNode left, IAVLNode right, IAVLNode parent, int height, int size)
		 *
		 * constructs a real node, assigns the given input to the fields
		 * complexity: O(1)
		 */
		public AVLNode(String val, int key, IAVLNode left, IAVLNode right, IAVLNode parent, int height, int size) {
			this.val = val;
			this.key = key;
			this.left = left;
			this.right = right;
			this.parent = parent;
			this.height = height;
			this.size = size;
		}

		/**
		 * public AVLNode(String val, int key)
		 *
		 * constructs a real node, assigns the given val and key to the relevant fields
		 *
		 * complexity: O(1)
		 */
			public AVLNode(String val, int key) {
			this(val, key, null, null, null, 0, 0);
		}

		/**
		 * public int getKey()
		 *
		 * returns the key of the node
		 *
		 * complexity: O(1)
		 */
		public int getKey() {
			return this.key;
		}

		/**
		 * public void setKey(int key)
		 *
		 * sets the key of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setKey(int key) {
			this.key = key;
		}

		/**
		 * public String getValue()
		 *
		 * returns the value of the node
		 *
		 * complexity: O(1)
		 */
		public String getValue() {
			return this.val;
		}

		/**
		 * public void setValue(String val)
		 *
		 * sets the value of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setValue(String val) {
			this.val = val;
		}

		/**
		 * public void setLeft(IAVLNode node)
		 *
		 * sets the left child of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setLeft(IAVLNode node) {
			this.left = node;
		}

		/**
		 * public IAVLNode getLeft()
		 *
		 * returns the left child of the node
		 * if the left child is null, then it creates a new virtual node, assigns it to be the left node and returns it
		 *
		 * complexity: O(1)
		 */
		public IAVLNode getLeft() {
			if(this.left == null) {
				this.left = new virtualNode(this);
			}
			return this.left;
		}

		/**
		 * public void setRight(IAVLNode node)
		 *
		 * sets the right child of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setRight(IAVLNode node) {
			this.right = node;
		}

		/**
		 * public IAVLNode getRight()
		 *
		 * returns the right child of the node
		 * if the right child is null, then it creates a new virtual node, assigns it to be the right node and returns it
		 *
		 * complexity: O(1)
		 */
		public IAVLNode getRight() {
			if(this.right == null) {
				this.right = new virtualNode(this);
			}
			return this.right;
			}

		/**
		 * public void setParent(IAVLNode node)
		 *
		 * sets the parent of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setParent(IAVLNode node) {
			this.parent = node;
		}

		/**
		 * public IAVLNode getParent()
		 *
		 * returns the parent of the node
		 *
		 * complexity: O(1)
		 */
		public IAVLNode getParent() {
			return this.parent;
		}

		/**
		 * public boolean isRealNode()
		 *
		 * returns true since this is a class of real nodes only
		 *
		 * complexity: O(1)
		 */
		public boolean isRealNode() {
			return true;
		}

		/**
		 * public void setHeight(int height)
		 *
		 * sets the height of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setHeight(int height) {
			this.height = height;
		}

		/**
		 * public int getHeight()
		 *
		 * returns the height of the node
		 *
		 * complexity: O(1)
		 */
		public int getHeight() {
			return this.height;
		}

		/**
		 * public void setSize(int currSize)
		 *
		 * sets the size of the node to the given input
		 *
		 * complexity: O(1)
		 */
		public void setSize(int currSize) {
			this.size = currSize;
		}

		/**
		 * public int getSize()
		 *
		 * returns the size of the node
		 *
		 * complexity: O(1)
		 */
		public int getSize() {
			return this.size;
		}
	}

	/**
	 * public class virtualNode
	 */
	public class virtualNode implements IAVLNode {
		private IAVLNode left = null;
		private IAVLNode right = null;
		private IAVLNode parent;
		private int height = -1;

		/**
		 * public virtualNode(IAVLNode parent)
		 *
		 * constructs a virtual node with a given node as the parent
		 * complexity: O(1)
		 */
		public virtualNode(IAVLNode parent) {
			this.parent = parent;
		}

		/**
		 * public int getKey()
		 *
		 * returns -1 since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		@Override
		public int getKey() {
			return -1;
		}

		/**
		 * public String getValue()
		 *
		 * returns null since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		@Override
		public String getValue() {//Function to get the value
			return null;
		}

		/**
		 * public void setLeft(IAVLNode node)
		 *
		 * returns void. Here because setLeft is a method in IAVLNode (the interface that the class implements)
		 *
		 * complexity: O(1)
		 */
		@Override
		public void setLeft(IAVLNode node) {
		}

		/**
		 * public IAVLNode getLeft()
		 *
		 * returns null since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		@Override
		public IAVLNode getLeft() { //Function to get the left node
			return null;
		}

		/**
		 * public void setRight(IAVLNode node)
		 *
		 * void. Here because setRight is a method in IAVLNode (the interface that the class implements)
		 *
		 * complexity: O(1)
		 */
		@Override
		public void setRight(IAVLNode node) {
		}

		/**
		 * public IAVLNode getRight()
		 *
		 * returns null since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		@Override
		public IAVLNode getRight() {
			return null;
		}

		/**
		 * public void setParent(IAVLNode node)
		 *
		 * sets the parent of the node to the given input
		 *
		 * complexity: O(1)
		 */
		@Override
		public void setParent(IAVLNode node) {
			this.parent = node;
		}

		/**
		 * public IAVLNode getParent()
		 *
		 * returns the parent of the node
		 *
		 * complexity: O(1)
		 */
		@Override
		public IAVLNode getParent() {
			return this.parent;
		}

		@Override
		/**
		 * public boolean isRealNode()
		 *
		 * returns false since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		public boolean isRealNode() {
			return false;
		}

		/**
		 * public void setHeight(int height)
		 *
		 * void. Here because setHeight is a method in IAVLNode (the interface that the class implements)
		 *
		 * complexity: O(1)
		 */
		@Override
		public void setHeight(int height) {
		}

		/**
		 * public int getHeight()
		 *
		 * returns -1 since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		@Override
		public int getHeight() {
			return -1;
		}

		/**
		 * public void setHeight(int height)
		 *
		 * void. Here because setSize is a method in IAVLNode (the interface that the class implements)
		 *
		 * complexity: O(1)
		 */
		public void setSize(int currSize) {
		}

		/**
		 * public int getSize()
		 *
		 * returns 0 since this is a class of virtual nodes only
		 *
		 * complexity: O(1)
		 */
		public int getSize() {
			return 0;
		}
	}
}



  

