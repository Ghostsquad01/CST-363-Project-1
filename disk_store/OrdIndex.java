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
		size++;
		if(entries.size() > 0){
			int whereToInsert = insertSearch(entries, 0, entries.size()-1, key);

			// check if the key needs to be inserted at the end
			if(whereToInsert > entries.size()-1){
				Entry entry = new Entry();

				entry.key = key;

				ArrayList<BlockCount> listBlock = new ArrayList<>();
				entry.blocks = listBlock;

				BlockCount block = new BlockCount();

				block.blockNo = blockNum;
				block.count++;

				entry.blocks.add(block);

				entries.add(whereToInsert, entry);
				return;
			}else{
				if(entries.get(whereToInsert).key != key){
					Entry entry = new Entry();
					ArrayList<BlockCount> list = new ArrayList<>();
					BlockCount block = new BlockCount();
					entry.key = key;
					block.blockNo = blockNum;
					block.count++;
					list.add(block);
					entry.blocks = list;
					entries.add(whereToInsert, entry);
				}else{
					List blocks = lookup(key);
					int findBlockNum = deleteBinarySearch(blocks, 0, blocks.size()-1, blockNum);
					if(findBlockNum != -1){
						entries.get(whereToInsert).blocks.get(findBlockNum).count++;
						return;
					}
					BlockCount block = new BlockCount();
					block.blockNo = blockNum;
					block.count++;
					entries.get(whereToInsert).blocks.add(block);
					return;
				}
			}
		}else {
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

		int indexExist = binarySearch(entries, 0, entries.size()-1, key);

		if(indexExist != -1){
			List<Integer> blockNums = lookup(key);
			int findBlockNum = deleteBinarySearch(blockNums, 0, blockNums.size()-1, blockNum);
			if(findBlockNum != -1){
				entries.get(indexExist).blocks.get(findBlockNum).count--;
				if(entries.get(indexExist).blocks.get(findBlockNum).count == 0){
					size--;
					entries.get(indexExist).blocks.remove(findBlockNum);
				}
			}
			if(entries.get(indexExist).blocks.size() == 0){
				entries.remove(indexExist);
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

	private static int insertSearch(ArrayList<Entry> arr, int lo, int hi, int key){
		int mid = lo + (hi - lo) / 2;

		while (hi >= lo)
		{
			if (arr.get(mid).key == key)
			{
				return mid;
			}

			if (arr.get(mid).key > key)
			{
				hi = mid - 1;
			}

			lo = mid + 1;
			mid = lo + (hi - lo) / 2;
		}
		return mid;
	}


	public static int binarySearch(ArrayList<Entry> arr, int lo, int hi, int key) {

		if (lo <= hi) {
			int mid = lo + (hi - lo)/2;
			if (arr.get(mid).key == key) {
				return mid;
			}
			if (arr.get(mid).key < key) {
				return binarySearch(arr, mid+1, hi, key);
			} else {
				return binarySearch(arr, lo, mid-1, key);
			}
		}
		return -1;
	}

	private static int deleteBinarySearch(List<Integer> blocks, int lo, int hi, int key){
		int mid;
		if(lo <= hi){
			mid = lo + (hi - lo)/2;
			if(blocks.get(mid) == key){
				return mid;
			}
			if(blocks.get(mid) > key){
				return deleteBinarySearch(blocks, lo, mid-1, key);
			}else{
				return deleteBinarySearch(blocks, mid+1, hi, key);
			}
		}
		return -1;
	}


	public static List<Integer> binarySearchForBlock(ArrayList<Entry> arr, int lo, int hi, int key) {

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