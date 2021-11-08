
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

		public String toString() {
			return key + " : " + blocks;
		}
	}

	private class BlockCount {
		int blockNo;
		int count;

		public String toString() {
			return "[" + blockNo + "," + count + "]";
		}
	}

	ArrayList<Entry> entries;

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
		List<Integer> distinctBlockNums = new ArrayList<>();
		if (entries.size() == 0) {
			return distinctBlockNums;
		}
		int l = 0;
		int r = entries.size() - 1;
		// binary search
		while (l <= r) {
			int m = l + (r - l) / 2;
			if (entries.get(m).key == key) {
				if (entries.get(m).blocks.size() > 0) {
					for (BlockCount grab : entries.get(m).blocks) {
						distinctBlockNums.add(grab.blockNo);
					}
					break;
				}
			}
			if (key > entries.get(m).key) {
				l = m + 1;
			} else if (key < entries.get(m).key) {
				r = m - 1;
			}
		}
		return distinctBlockNums;
	}

	@Override
	public void insert(int key, int blockNum) {
		int left = 0;
		int right = entries.size() - 1;
		int middle = 0;
		boolean found_key = false;
		//binary search
		while (left <= right) {

			middle = left + (right - left) / 2;

			if (entries.get(middle).key == key) {
				found_key = true;
				break;
			}
			if (entries.get(middle).key < key) {
				left = middle + 1;
			} else {
				right = middle - 1;
			}
		}

		if (found_key) { //if a key was found
			boolean foundBlock = false;
			//iterate over all blocks
			for (BlockCount b : entries.get(middle).blocks) {
				if (b.blockNo == blockNum) {
					b.count++;
					foundBlock = true; // flag if block is found
					break;
				}
			}
			if (!foundBlock) {// if block was not found that means we make a new block and begin its count at 1
				BlockCount tempBlock = new BlockCount();
				tempBlock.blockNo = blockNum;
				tempBlock.count = 1;
				entries.get(middle).blocks.add(tempBlock);
			}
		} else { //if a key was not found
			Entry newEntry = new Entry(); //create new entry
			BlockCount newBlockCount = new BlockCount();
			newEntry.blocks = new ArrayList<>();
			newEntry.key = key;

			ArrayList<BlockCount> newBlockCountlist = new ArrayList<>();
			newEntry.blocks = newBlockCountlist;
			newBlockCount.blockNo = blockNum;
			newBlockCount.count = 1;
			newEntry.blocks.add(newBlockCount);
			entries.add(left, newEntry); //insert entry into the left most part of the array
		}
	}

	@Override
	public void delete(int key, int blockNum) {
		// lookup key
		//  if key not found, should not occur.  Ignore it.
		//  decrement count for blockNum.
		//  if count is now 0, remove the blockNum.
		//  if there are no block number for this key, remove the key entry.
		//throw new UnsupportedOperationException();
		int left = 0;
		int right = size() - 1;
		boolean foundBlock = false;
		// binary search for key
		while (left <= right) {
			int middle = left + (right - left) / 2;
			if (entries.get(middle).key == key) {
				boolean deleteBlock = false;
				List<BlockCount> foundBlockList = new ArrayList<>();
				for (BlockCount b : entries.get(middle).blocks) {
					if (b.blockNo == blockNum) {
						foundBlock = true;// flag decides whether block# was found
						b.count--;
					}
					if (b.count == 0) {
						deleteBlock = true;
						foundBlockList.add(b); // add block to list of blocks to be deleted
					}
				}
				if (deleteBlock) {
					entries.get(middle).blocks.removeAll(foundBlockList); //remove all blocks found
				}
				if (entries.get(middle).blocks.size() == 0) {
					entries.remove(entries.get(middle)); // if it is empty, get rid of entry.
				}
				if (foundBlock) {
					return;
				}
			}
			//binary search
			if (entries.get(middle).key < key) {
				left = middle + 1;
			} else {
				right = middle - 1;
			}
		}
	}

	/**
	 * Return the number of entries in the index
	 *
	 * @return
	 */
	public int size() { //returns the number of blocks in each entry for all entries
		int count = 0;
		for (Entry e : entries) {
			count += e.blocks.size();
		}
		return count;
	}

	@Override
	public String toString() {
		throw new UnsupportedOperationException();
	}
}