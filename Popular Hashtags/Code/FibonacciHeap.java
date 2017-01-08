import java.io.*;
import java.util.*;

public class FibonacciHeap{

	Vertex currMax = null;

	int count = 0;

	public void insert(Vertex n){
		
		count = count + 1;							//increment the count
					
		if(currMax==null)							//check if its first node of heap
		{
			currMax = n;
		}		
		else{										//if there are existing nodes, then add node to right of currMax
			n.back = currMax;
			n.front = currMax.front;
			currMax.front = n;
			
			if(n.front!=null){						//check if currMax has a rightNode, if yes adjust previous pointer of n's rightNode
				n.front.back = n;
			}
			
			if(n.front==null){						//check if currMax has a rightNode, if not make currMax as rightNode of n
				n.front = currMax;
				currMax.back = n;
			}			
			setMax(n);								//update max node
		}
	}

	public void increaseKey(Vertex n, int freq){
		int currFreq = n.freq;
		
		if(currFreq > (currFreq + freq)){			//determine if sum of existing freq and passed freq is greater than existing one
			throw new IllegalArgumentException("Not valid freq");
		} 
		
		n.freq = currFreq + freq;					//calculate new freq
		
		Vertex myParent = n.parent;					//fetch parents detail
		
		if(myParent != null){						//If parent exist then check its freq and refactor tree if need be
			if(myParent.freq < n.freq){
				removeFromList(n);					//If parent's freq is less than remove curr node from tree 
				myParent.degree = myParent.degree - 1;	//and reduce parent's degree
				
				if(myParent.child == n){			//if n's parent have other childs, change myParent's child pointer to point to next child
					myParent.child = n.front;
				}
				
				if(myParent.degree == 0){			//if n's parent didnt have other childs, set child pointer to null
					myParent.child = null;
				}
					
				adjustAtRootLevel(n);				//add n to root level list
				
				n.parent = null;					//set n's parent pointer to null
				n.lostChild = false;				//mark n to false as we are putting it on root level
					//check
				cascadingCut(n);					// call cascading cut for its parents 
			}
		}

		setMax(n);									//update max node
	}

	public void cascadingCut(Vertex myParent){
		Vertex temp = myParent.parent;

		if(temp != null){
			
			if(myParent.lostChild == false){		//Stop if parent element is losing child for first time
				myParent.lostChild = true;
			}
			else{									//continue till you find a node whose lostChild is false
				removeFromList(myParent);
				temp.degree = temp.degree - 1;
				
				if(temp.child == myParent){			//if myParent's parent have other childs, change temp's child pointer to point to next child
					temp.child = myParent.front;
				}
				
				if(temp.degree == 0){				//if myParent's parent didnt have other childs, set child pointer to null
					temp.child = null;
				}
				
				adjustAtRootLevel(myParent);		//add myParent to root level list

				myParent.parent = null;				//set myparent's parent pointer to null
				myParent.lostChild = false;			//mark myparent to false as we are putting it on root level

				cascadingCut(temp);					// call cascading cut for its parents
			}
		}
	}

	public Vertex removeMax(){

		Vertex max = currMax;

		if(max != null){							// if currMax is not null then add each of its child to root level
			Vertex firstChild = max.child;
			Vertex right; 
			for(int i = 0; i < max.degree; i++){
				right = firstChild.front;

				removeFromList(firstChild);			//adjust left and right pointers of each child to free them

				adjustAtRootLevel(firstChild);		// add child on root level

				firstChild.parent = null;			//set child's parent pointer to null
				firstChild.lostChild = false;		//mark firstChild to false as we are putting it on root level
				firstChild = right;					// continue for all child
			}				

		removeFromList(max);						//adjust left and right pointers of currMax

		if (max == max.front) {						//if currMax was last node, set it to null	
	            currMax = null;
	    } 
	    else {
	        currMax = currMax.front;				//else set next element as new currMax
	        pairwiseCombine();						//consolidate
	    }
		count -= 1;									//decrement count of each element
		return max;									//return deleted max element
	}

	return null;
}

	public void pairwiseCombine(){

		int len = 100;								//define default len

		List<Vertex> degreeTable = new ArrayList<Vertex>(len);

		for(int i = 0; i< len; i++){				//fill in degree table
			degreeTable.add(null);
		}

		Vertex t = currMax;
		int totalNoOfRoot = 0;						//counter to determine number of max trees

		if(t!=null){								//determine number of trees only if currMax is not null
			do{
				totalNoOfRoot = totalNoOfRoot +1;
				t = t.front;
			}while(t != currMax);
		}

		for(int i = 0 ; i <totalNoOfRoot; i++){		//for each max tree

			Vertex right = t.front;					//store its next node
			int tDegree = t.degree;					//fetch max node's degree

			for(;;){
				Vertex x = degreeTable.get(tDegree);	//if there is already one max tree of same degree, then merge		
				if(x == null) break;

				if(t.freq < x.freq){	
					Vertex a = x;
					x = t;
					t = a;
				}

				removeFromList(x);

				x.parent = t;						//set smaller node as child of other

				if(t.child != null){				//if max node have childrens, then set new node as first child and connect it to other childrens
						x.back = t.child;
           				x.front = t.child.front;
           				t.child.front = x;
            			x.front.back = x;
				}
				else{								//if max node doesn't have child, then set new node as its child				
						t.child = x;
						x.front = x;
						x.back = x;
					}

				t.degree += 1;						//increment its degree

				x.lostChild = false;				//mark x to false as we are making it a child

				degreeTable.set(tDegree,null);		//remove the degree table entry
				tDegree = tDegree++;				//move to next degree
			}

			degreeTable.set(tDegree,t);				// store newly computed max tree in the location

			t = right;
		}

		currMax = null;

		for(Vertex y: degreeTable){					//add each max tree in degree table to root level
			if(y == null) continue;
			if(currMax != null){
					removeFromList(y);
					adjustAtRootLevel(y);
					setMax(y);
				}
			else{
					currMax = y;
				}
			}	
	}

	public void removeFromList(Vertex a){            //adjust previous and next node's pointer
		a.back.front = a.front;
		a.front.back = a.back;
	}

	public void adjustAtRootLevel(Vertex k){         /*add node at root list and adjust next pointer of current Max node and previous pointer of currMax previous node*/
			Vertex next = currMax.front;
			currMax.front = k;
			k.front = next;
			next.back = k;
			k.back = currMax;
	}

	public void setMax(Vertex n){                    //determine if new node has greater key than existing max node
		if(currMax.freq < n.freq){
			currMax = n;
		}
	}
}
