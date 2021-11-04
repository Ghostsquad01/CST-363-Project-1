package disk_store;

import java.util.ArrayList;
import java.util.List;

/**
 * An ordered index.  Duplicate search key values are allowed,
 * but not duplicate index table entries.  In DB terminology, a
 * search key is not a superkey.
 * 
 * A limitation of this class is that only single integer search
 * keys are supported.
 *
 */


public class OrdIndex implements DBIndex {
	
	private class Entry {
		int key;
		ArrayList<BlockCount> blocks;
	}
	
	private class BlockCount {
		int blockNo;
		int count;
	}
	
	ArrayList<Entry> entries;
	int size = 0;
	
	/**
	 * Create an new ordered index.
	 */
	public OrdIndex() {
		entries = new ArrayList<>();
	}

	// not completed
	@Override
	public List<Integer> lookup(int key) {
		// binary search of entries arraylist
		// return list of block numbers (no duplicates). 
		// if key not found, return empty list
		int lo = 0;
		int hi = entries.size()-1;
		int mid;
		List<Integer> infoFound = new ArrayList<>();
		if(key < entries.get(lo).key || key > entries.get(hi).key){
			return infoFound;
		}

//		 linear search - testing purposes
//		for(int i=0; i < entries.size(); i++){
//			if(!(infoFound.contains(entries.get(i).blocks.get(0).blockNo)) && entries.get(i).key == key){
//				infoFound.add(entries.get(i).blocks.get(0).blockNo);
//			}
//		}
//		System.out.println(infoFound);

		// binary search on entries arrayList
		 while(lo < hi){
			mid = lo + (hi-lo)/2;
			if(entries.get(mid).key == key){
				if(!infoFound.contains(entries.get(mid).blocks.get(0).blockNo)){
					infoFound.add(entries.get(mid).blocks.get(0).blockNo);
					lo = mid - 1;
				}
			}
			if(entries.get(hi).key == key){
				if(!infoFound.contains(entries.get(hi).blocks.get(0).blockNo)){
					infoFound.add(entries.get(hi).blocks.get(0).blockNo);
				}
			}
			if(entries.get(lo).key == key){
				if(!infoFound.contains(entries.get(lo).blocks.get(0).blockNo)){
					infoFound.add(entries.get(lo).blocks.get(0).blockNo);
				}
			}
			if(key < entries.get(mid).key){
				hi = mid - 1;
			}else{
				lo = mid + 1;
			}
		}
		if(entries.get(lo).key == key){
			if(!infoFound.contains(entries.get(lo).blocks.get(0).blockNo)){
				infoFound.add(entries.get(lo).blocks.get(0).blockNo);
			}
		}
		if(entries.get(hi).key == key){
			if(!infoFound.contains(entries.get(hi).blocks.get(0).blockNo)){
				infoFound.add(entries.get(hi).blocks.get(0).blockNo);
			}
		}
		return infoFound;
	}

	// insert function = somewhat-comepleted
	// need to change how count gets incremented
		// so every block has a counter on how many keys there are in that block
		// if there are 4 keys in block 1. then count for block 1 should be 4
		// emailed the professor for some clarification on the variable
	// uses binary search to check if there is a duplicated in our entries
	@Override
	public void insert(int key, int blockNum) {
		Entry entry = new Entry(); // create an entry variable to fill up
		ArrayList<BlockCount> list = new ArrayList<>(); // create an arrayList storing BlockCount variables
		BlockCount block = new BlockCount(); // create an BlockCount variable to use to fill our BlockCount arrayList

		block.blockNo = blockNum; // declare and initialize our blockNo variable for our block variable
		block.count =+1; // not sure about block.count, no clue what they want with that variable
		list.add(block); // add the block to the BlockCount list
		entry.key = key; // declare and intialize the key value for the entry
		entry.blocks = list; // declare and initialize the BlockCount list for the entry

		// no need to check if there is a duplicate
		// if the entries arrayList is empty
		// else check if there is a duplicate
			// if no duplicate, add it to our entries list
		// use a binary search to check whether there is a duplicate or not
		if (!entries.isEmpty()) {
			int lo = 0;
			int hi = entries.size() - 1;
			int mid;
			while (lo < hi) {
				mid = lo + (hi - lo) / 2;
				if (entries.get(mid).key == key && entries.get(mid).blocks.get(0).blockNo == blockNum) {
//					System.out.println("duplicate"); - testing/debugging purposes
					return; // call a return statement to exit the function, don't want to add this duplicated entry
				}
				if (entries.get(hi).key == key && entries.get(hi).blocks.get(0).blockNo == blockNum) {
//					System.out.println("duplicate");
					return;
				}
				if (entries.get(lo).key == key && entries.get(lo).blocks.get(0).blockNo == blockNum) {
//					System.out.println("duplicate");
					return;
				}
				// change our low and high points for our binary search
				if (key < entries.get(mid).key) {
					hi = mid - 1;
				} else {
					lo = mid + 1;
				}
			}
		}
		entries.add(entry);

		// testing/debugging purposes
//		for(int i=0; i < entries.size(); i++){
//			System.out.print(entries.get(i).key + " ");
//		}
//		System.out.println();
	}

	// don't think this is right
	// not really using count very well
	// also not sure if this for-loop is okay to use for this function
	@Override
	public void delete(int key, int blockNum) {
		// lookup key 
		//  if key not found, should not occur.  Ignore it.
		//  decrement count for blockNum.
		//  if count is now 0, remove the blockNum.
		//  if there are no block number for this key, remove the key entry.
		
		if(!lookup(key).isEmpty()){
			for(int i=0; i < entries.size(); i++){
				if(entries.get(i).key == key && entries.get(i).blocks.get(0).blockNo == blockNum){
					entries.remove(i);
				}
			}
		}
		for(int i=0; i < lookup(key).size(); i++){
			System.out.println(lookup(key).get(i));
		}
	}
	
	/**
	 * Return the number of entries in the index
	 * @return
	 */

	// completed? not sure if this is correct
	public int size() {
		return entries.size();
		// you may find it useful to implement this
		
	}
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}
}