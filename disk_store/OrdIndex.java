package disk_store;

import java.lang.reflect.Array;
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
	private ArrayList<Integer> knownIndex = new ArrayList<>();
	private ArrayList<Integer> knownBlockNo = new ArrayList<>();


	private class Entry {
		int key;
		ArrayList<BlockCount> blocks;
	}
	
	private class BlockCount {
		int blockNo;
		int count = 0;
	}
	
	ArrayList<Entry> entries;
	int size = 0;
	
	/**
	 * Create an new ordered index.
	 */
	public OrdIndex() {
		entries = new ArrayList<>();
	}

	@Override
	public List<Integer> lookup(int key) {
		//Get length
		int lo = 0;
		int hi = entries.size()-1;

		//Create list
		List<Integer> blocksFound = new ArrayList<>();

		//Use Binary Search
		int numExist = binarySearch(entries, lo, hi, key);

		if (numExist != -1) {
			//Return list of block numbers (no duplicates)
			blocksFound = binarySearchForBlock(entries, lo, hi, key);

			return blocksFound;
		}

		return blocksFound;
	}

	@Override
	public void insert(int key, int blockNum) {
		size = size +1;
		boolean checkForMatchingKey = true;

		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).key == key) {
				checkForMatchingKey = false;

				for (int b = 0; b < entries.get(i).blocks.size(); b++) {
					if ((entries.get(i).blocks.get(b).blockNo) == blockNum) {
						entries.get(i).blocks.get(b).count++;
						break;
					}else {
						BlockCount newBlock = new BlockCount();

						newBlock.blockNo = blockNum;
						newBlock.count++;

						entries.get(i).blocks.add(newBlock);
						break;
					}
				}
			}
		}

		if (checkForMatchingKey) {
			Entry entry = new Entry();

			entry.key = key;

			ArrayList<BlockCount> listBlock = new ArrayList<>();
			entry.blocks = listBlock;

			BlockCount block = new BlockCount();

			block.blockNo = blockNum;
			block.count++;

			entry.blocks.add(block);

			entries.add(entry);
		}
	}

	@Override
	public void delete(int key, int blockNum) {
		// lookup key 
		//  if key not found, should not occur.  Ignore it.
		//  decrement count for blockNum.
		//  if count is now 0, remove the blockNum.
		//  if there are no block number for this key, remove the key entry.

		if(!lookup(key).isEmpty()){
			for(int i=0; i < entries.size(); i++){
				if(entries.get(i).key == key){
					for(int b=0; b < entries.get(i).blocks.size(); b++){
						if(entries.get(i).blocks.get(b).blockNo == blockNum){
							size--;
							if(entries.get(i).blocks.get(b).count >= 1){
								entries.get(i).blocks.get(b).count--;
							}
							if(entries.get(i).blocks.get(b).count == 0){
								entries.get(i).blocks.remove(b);
								break;
							}

						}
					}
					if(entries.get(i).blocks.size() == 1){
						entries.remove(i);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Return the number of entries in the index
	 * @return
	 */

	// completed? not sure if this is correct
	public int size() {
		return size;
		// you may find it useful to implement this
	}
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}

	public static int binarySearch(ArrayList<Entry> arr, int lo, int hi, int key) {

		int mid = lo + (hi - lo)/2;

		while (lo <= hi) {
			//
			if (arr.get(mid).key < key) {
				lo = mid + 1;
			}else if (arr.get(mid).key == key) {
				//Return found el
				return arr.get(mid).key;
			}else {
				hi = mid - 1;
			}

			mid = lo + (hi - lo)/2;
		}

		return -1;
	}

	public static List<Integer>binarySearchForBlock(ArrayList<Entry> arr, int lo, int hi, int key) {

		List<Integer> blockNumsFound = new ArrayList<>();

		List<Integer> noDupList;

		int mid = lo + (hi - lo)/2;

		while (lo <= hi) {
			//
			if (arr.get(mid).key < key) {
				lo = mid + 1;
			}else if (arr.get(mid).key == key) {

				for (int x = 0; x < arr.get(mid).blocks.size(); x++) {
					blockNumsFound.add(arr.get(mid).blocks.get(x).blockNo);
				}

				noDupList = removeDups(blockNumsFound);

				return noDupList;

			}else {
				hi = mid - 1;
			}

			mid = lo + (hi - lo)/2;
		}
		return blockNumsFound;
	}

	public static List<Integer> removeDups(List<Integer> listToBeEdited)
	{
		List<Integer> finalList = new ArrayList<>();

		for (int el : listToBeEdited) {
			if (!finalList.contains(el)) {
				finalList.add(el);
			}
		}

		return finalList;
	}
}