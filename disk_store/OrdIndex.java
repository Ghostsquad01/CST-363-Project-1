package disk_store;
import java.util.ArrayList;
import java.util.List;

/**
 * An ordered index.  Duplicate search key values are allowed,
 * but not duplicate index table entries.  In DB terminology, a
 * search key is not a superkey.
 * <p>
 * A limitation of this class is that only single integer search
 * keys are supported.
 */


public class OrdIndex implements DBIndex {

	private class Entry {
		int key;
		ArrayList<BlockCount> blocks;
	}

	private class BlockCount {
		int blockNo;
		int count = 0;
	}

	ArrayList<Entry> entries;
	private int size;

	/**
	 * Create an new ordered index.
	 */
	public OrdIndex() {
		entries = new ArrayList<>();
	}


	@Override
	public List<Integer> lookup(int key) {
		// binary search of entries arraylist
		// return list of block numbers (no duplicates).
		// if key not found, return empty list
		List<Integer> BlockNums = new ArrayList<>();
		if (entries.size() == 0) {
			return BlockNums;
		}
		int lo = 0;
		int hi = entries.size() - 1;
		// perform binary search
		// this binary search will either return some filled list of integers or return an empty list.
		// either way this will give us the right output
		BlockNums = lookupBinarySearch(entries, lo, hi, key);

		return BlockNums;
	}

	@Override
	public void insert(int key, int blockNum) {
		int lo = 0;
		int hi = entries.size() - 1;
		int mid;

		// perform a binary search to find out whether we have a key already in our entries
		List<Integer> loMidHi = insertBinarySearch(entries, lo, hi, key);

		// we need to use the updated variables of lo and mid
		// hi is just used for a flag check basically
		lo = loMidHi.get(0);
		mid = loMidHi.get(1);
		hi = loMidHi.get(2);

		// check if a key was found
			// if hi is equal to -1 then we found a key that has already been inserted
				// then we want to loop through each block for the key found and
				// determine whether to increment count or add a new block for that key
					// if the blockNo exists then we just want to increment that blockNo's count
					// else create a new block for this blockNo
		// else create a new entry and add the new entry to our entries
		if (hi != -1) {
			boolean foundBlock = false; // flag variable to determine what to do later in this function
			// check each block for a similar blockNo
			for (BlockCount b : entries.get(mid).blocks) {
				if (b.blockNo == blockNum) {
					b.count++;
					// check off that we have found a block, see below why we have a flag variable
					foundBlock = true;
					break;
				}
			}
			// if a block was not found then we want to make a new block
			if (!foundBlock) {
				BlockCount block = new BlockCount();
				block.blockNo = blockNum;
				block.count++;
				entries.get(mid).blocks.add(block);
			}
			// if a key was not found then we want to create a new entry and add it to our list of entries
		} else {
			Entry entry = new Entry(); //create new entry
			BlockCount block2 = new BlockCount();
			entry.key = key;

			ArrayList<BlockCount> list = new ArrayList<>();
			entry.blocks = list;
			block2.blockNo = blockNum;
			block2.count++;
			entry.blocks.add(block2);
			entries.add(lo, entry); // add our new entry into the lowest part of our entries array
		}
	}

	@Override
	public void delete(int key, int blockNum) {
		// lookup key
		//  if key not found, should not occur.  Ignore it.
		//  decrement count for blockNum.
		//  if count is now 0, remove the blockNum.
		//  if there are no block number for this key, remove the key entry.

		int lo = 0;
		int hi = entries.size() - 1;
		// perform a binary search to find what entry or block to delete from entries
		deleteBinarySearch(entries, lo, hi, key, blockNum);
	}

	// binary search for our lookup method
	private static List<Integer> lookupBinarySearch(ArrayList<Entry> list,int lo, int hi, int key){
		// create our list to fill up with however many blockNo are found
		List<Integer> BlockNums = new ArrayList<>();

		// binary search logic
		int mid;
		while (lo <= hi) {
			mid = lo + (hi - lo) / 2;
			if (list.get(mid).key == key) {
				if (list.get(mid).blocks.size() > 0) {
					 // call our helper function - doesn't make this binary search method so cluttered
					 BlockNums = lookupHelperFunction(list, mid);
					break;
				}
			}
			if (key > list.get(mid).key) {
				lo = mid + 1;
			} else if (key < list.get(mid).key) {
				hi = mid - 1;
			}
		}
		return BlockNums;
	}

	// helper function for lookup - cleans/organizes our code
	private static List<Integer> lookupHelperFunction(ArrayList<Entry> list, int mid){
		List<Integer> BlockNums = new ArrayList<>();
		for (BlockCount grab : list.get(mid).blocks) {
			BlockNums.add(grab.blockNo);
		}
		return BlockNums;
	}


	// binary search for our delete method
	private static void deleteBinarySearch(ArrayList<Entry> list, int lo, int hi, int key, int blockNum){
		// flag variable to use in our helper method
		boolean toFind = false;

		// binary search logic
		int mid;
		while (lo <= hi) {
			mid = lo + (hi - lo) / 2;
			if (list.get(mid).key == key) {
				// call our helper function - doesn't make this binary search method so cluttered
				deleteHelperFunction(list, mid, blockNum, toFind);

				// call return because we are done with changing the entries/blocks
				// anything trying to be done after this will cause errors
				return;
			}
			if (list.get(mid).key < key) {
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}
	}

	// helper function for delete binary search - clean/organizes our code
	private static void deleteHelperFunction(ArrayList<Entry> list, int mid, int blockNum, boolean toFind){
		boolean toDelete = false;
		List<BlockCount> blocksToDelete = new ArrayList<>();
		for (BlockCount block : list.get(mid).blocks) {
			if (block.blockNo == blockNum) {
				block.count--;
				toFind = true;
			}
			if (block.count == 0) {
				blocksToDelete.add(block);
				toDelete = true;
			}
		}
		if (toDelete) {
			list.get(mid).blocks.removeAll(blocksToDelete);
		}
		if (list.get(mid).blocks.size() == 0) {
			list.remove(list.get(mid));
		}
		if (toFind) {
			return;
		}
	}

	// binary search for our insert
	private static List<Integer> insertBinarySearch(ArrayList<Entry> list, int lo, int hi, int key){
		List<Integer> loMidHi = new ArrayList<>();
		loMidHi.add(0); // lo
		loMidHi.add(0); // mid
		loMidHi.add(0); // hi

		// binary search logic
		int mid;
		while (lo <= hi) {
			mid = lo + (hi - lo) / 2;
			if (list.get(mid).key == key) {
				loMidHi.set(0, lo);
				loMidHi.set(1, mid);
				// set hi
				loMidHi.set(2, 1);
				return loMidHi;
			}
			if (list.get(mid).key < key) {
				lo = mid + 1;
			} else {
				hi = mid - 1;
			}
		}
		loMidHi.set(0, lo);
		loMidHi.set(1, -1);
		loMidHi.set(2, -1);
		return loMidHi;
	}

	/**
	 * Return the number of entries in the index
	 *
	 * @return
	 */

	// returns how many blocks exist in our entries
	public int size() {
		size = 0;
		for (Entry entry : entries) {
			size += entry.blocks.size();
		}
		return size;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}
}