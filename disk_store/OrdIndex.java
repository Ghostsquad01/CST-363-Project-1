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

	// completed
	@Override
	public void insert(int key, int blockNum) {
		Entry entry = new Entry();
		ArrayList<BlockCount> list = new ArrayList<>();
		BlockCount block = new BlockCount();
		block.blockNo = blockNum;
		// not sure about block.count, no clue what they want with that variable
		block.count =+1;
		list.add(block);
		entry.key = key;
		entry.blocks = list;
		if(!entries.contains(entry)){
			entries.add(entry);
		}

		// testing purposes
//		for(int i=0; i < entries.size(); i++){
//			System.out.print(entries.get(i).key + " ");
//		}
//		System.out.println();
	}

	@Override
	public void delete(int key, int blockNum) {
		// lookup key 
		//  if key not found, should not occur.  Ignore it.
		//  decrement count for blockNum.
		//  if count is now 0, remove the blockNum.
		//  if there are no block number for this key, remove the key entry.
		throw new UnsupportedOperationException();
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