package ci284.ass2.htable;


//When it comes to complexity within the put function we are able to break it up into 3 separate parts.
//These parts consist of comparing the maxLoad to LoadFactor, seeing if the key already exists and lastly inserting the item.
// With the hash table an item must be inserted and the insert time of majority of hash tables are consistently the same.
// Therefore the complexity of insert is represented as O1. This also applies to the get method as the time to search for data is the same
// making the complexity also O1.
//
// When it comes to terms of best case it is when there is only one step required so we can insert the date and therefore applies to
// the get method as there will only be one bit of data to sort through. Worst case scenario is when it will take O(n) time where n is
// the size of the hash. This means it will take double the time for a which is double t he size. This is also the same for get
// as it will also take O(n) due to that if n is large then it has more to sort through and this also applies to put method as
// looking for space within a larger table takes far longer. When the LoadFactor increases the amount of overhead space decreases
// thus increasing the cost to look up the put and get methods. For instance if the LoadFactor was to increase to 1.0 then the
// capacity would increase and because of this the amount of collisions would also increase which happens when multiple keys both have
// the same hash. This means that this would increase the amount of look up time since each key will have to be checked individually.

/**
 * A HashTable with no deletions allowed. Duplicates overwrite the existing value. Values are of
 * type V and keys are strings -- one extension is to adapt this class to use other types as keys.
 * 
 * The underlying data is stored in the array `arr', and the actual values stored are pairs of 
 * (key, value). This is so that we can detect collisions in the hash function and look for the next 
 * location when necessary.
 */

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class Hashtable<V> {

	private Object[] arr; //an array of Pair objects, where each pair contains the key and value stored in the hashtable
	private int max; //the size of arr. This should be a prime number
	private int itemCount; //the number of items stored in arr
	private final double maxLoad = 0.6; //the maximum load factor

	public static enum PROBE_TYPE {
		LINEAR_PROBE, QUADRATIC_PROBE, DOUBLE_HASH;
	}

	PROBE_TYPE probeType; //the type of probe to use when dealing with collisions
	private final BigInteger DBL_HASH_K = BigInteger.valueOf(8);

	/**
	 * Create a new Hashtable with a given initial capacity and using a given probe type
	 * @param initialCapacity
	 * @param pt
	 */
	public Hashtable(int initialCapacity, PROBE_TYPE pt) {
		max = nextPrime(initialCapacity); //sets the size
        arr = new Object[max]; //creates the new Hashtable set to the max size
        this.probeType = pt; //uses the given probe type 
	}
	
	/**
	 * Create a new Hashtable with a given initial capacity and using the default probe type
	 * @param initialCapacity
	 */
	public Hashtable(int initialCapacity) {
		max = nextPrime(initialCapacity);   //sets the size
        arr = new Object[max]; //creates the new Hashtable set to the max size
        probeType = PROBE_TYPE.LINEAR_PROBE; //uses the default probe type 
	}

	/**
	 * Store the value against the given key. If the loadFactor exceeds maxLoad, call the resize 
	 * method to resize the array. the If key already exists then its value should be overwritten.
	 * Create a new Pair item containing the key and value, then use the findEmpty method to find an unoccupied 
	 * position in the array to store the pair. Call findEmmpty with the hashed value of the key as the starting
	 * position for the search, stepNum of zero and the original key.
	 * containing   
	 * @param key
	 * @param value
	 */
	public void put(String key, V value) { 
		if(getLoadFactor() > maxLoad) { // checks if LoadFactor is greater than maxLoad
            resize();                   // resizes table
       }
       int loc = indexGrabber(hash(key),0,key); // calls indexGrabber which returns the index
       if(loc != -1) {							// checks is loc is not -1
           ((Pair)arr[loc]).value = value;      // sets the value
           return;
       } else {
            arr[findEmpty(hash(key),0,key)] = new Pair(key,value); 
            itemCount++;
       }
     }
	
	private int indexGrabber(int startPos,int stepNum,String key) {
        if((arr[startPos]) != null && ((Pair)arr[startPos]).key.equals(key)) { // checks if the position is not null and it matches
        	return startPos;
        } else {
            if((arr[startPos]) == null) return -1;							   // If its null it will return -1 if not then it will recur with an increase stepnum
            stepNum++;
            return indexGrabber(getNextLocation(startPos,stepNum,key),stepNum,key);
        }
       }
	
	

	/**
	 * Get the value associated with key, or return null if key does not exists. Use the find method to search the
	 * array, starting at the hashed value of the key, stepNum of zero and the original key.
	 * @param key
	 * @return
	 */
	public V get(String key) {

		return find(hash(key), key, 0); //returns the key and if there is no key it returns 0 (null)
	}
	
	/**
	 * Return true if the Hashtable contains this key, false otherwise 
	 * @param key
	 * @return
	 */
	public boolean hasKey(String key) {
        if (find(hash(key), key, 0)== null) {
        return false; //if the hash table contains no key it returns false
        } else {
        return true; // Will return true if it contains a key
        }
       
    }

	/**
	 * Return all the keys in this Hashtable as a collection
	 * @return
	 */
	public Collection<String> getKeys() { 
        Collection<String> collec = new ArrayList<String>(); //collec is the collection of keys 
        for (int i = 0; i <this.max; i++){  //counts to the max which is the size of the hash table
        	if ( arr[i] != null){ //counts down 
        		Hashtable<V>.Pair pair = (Hashtable<V>.Pair)arr[i];
        		String key = pair.key;
        		collec.add(key); //adds the key to the collection
        	}
        }
        return collec; //returns the collection of keys 
       
       
    }

	/**
	 * Return the load factor, which is the ratio of itemCount to max
	 * @return
	 */
	public double getLoadFactor() { //divides the overall amount of items by the max size which calculates the ratio
		return (double)itemCount/max;
    }

	/**
	 * return the maximum capacity of the Hashtable
	 * @return
	 */
	public int getCapacity() {
        return max; //returns the max which is the maximum capacity 
    }
	
	/**
	 * Find the value stored for this key, starting the search at position startPos in the array. If
	 * the item at position startPos is null, the Hashtable does not contain the value, so return null. 
	 * If the key stored in the pair at position startPos matches the key we're looking for, return the associated 
	 * value. If the key stored in the pair at position startPos does not match the key we're looking for, this
	 * is a hash collision so use the getNextLocation method with an incremented value of stepNum to find 
	 * the next location to search (the way that this is calculated will differ depending on the probe type 
	 * being used). Then use the value of the next location in a recursive call to find.
	 * @param startPos
	 * @param key
	 * @param stepNum
	 * @return
	 */
	private V find(int startPos, String key, int stepNum) { //to find where the key matches 

        if (arr[startPos] == null) { //if the start pos in null then it will return nothing 
            return null;
        } else if (((Pair)arr[startPos]).key.equals(key)) { //if the startpos is equal to the key then it will return the pair
        
        return (V) ((Pair) arr[startPos]).value;
        } else { //otherwise it will move to the next location 
            return find(getNextLocation(startPos, stepNum , key), key, stepNum++);            
        }
      
    }

	/**
	 * Find the first unoccupied location where a value associated with key can be stored, starting the
	 * search at position startPos. If startPos is unoccupied, return startPos. Otherwise use the getNextLocation
	 * method with an incremented value of stepNum to find the appropriate next position to check 
	 * (which will differ depending on the probe type being used) and use this in a recursive call to findEmpty.
	 * @param startPos
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int findEmpty(int startPos, int stepNum, String key) { //to find an empty space
        if (arr[startPos] == null) { // if the startpos is empty then it will return the startpos   
        return startPos; 
        } else { //otherwise it wil continue down the list to the next free space
        return findEmpty(getNextLocation(startPos, stepNum , key), stepNum, key);
        }
       
    }

	/**
	 * Finds the next position in the Hashtable array starting at position startPos. If the linear
	 * probe is being used, we just increment startPos. If the double hash probe type is being used, 
	 * add the double hashed value of the key to startPos. If the quadratic probe is being used, add
	 * the square of the step number to startPos.
	 * @param i
	 * @param stepNum
	 * @param key
	 * @return
	 */
	private int getNextLocation(int startPos, int stepNum, String key) {
        int step = startPos;
        switch (probeType) {
        case LINEAR_PROBE:
            step++;
            break;
        case DOUBLE_HASH:
            step += doubleHash(key);
            break;
        case QUADRATIC_PROBE:
            step += stepNum * stepNum;
            break;
        default:
            break;
        }
        return step % max;
    }

	/**
	 * A secondary hash function which returns a small value (less than or equal to DBL_HASH_K)
	 * to probe the next location if the double hash probe type is being used
	 * @param key
	 * @return
	 */
	private int doubleHash(String key) {
        BigInteger hashVal = BigInteger.valueOf(key.charAt(0) - 96);
        for (int i = 1; i < key.length(); i++) {
            BigInteger c = BigInteger.valueOf(key.charAt(i) - 96);
            hashVal = hashVal.multiply(BigInteger.valueOf(27)).add(c);
        }
        return DBL_HASH_K.subtract(hashVal.mod(DBL_HASH_K)).intValue();
    }

	/**
	 * Return an int value calculated by hashing the key. See the lecture slides for information
	 * on creating hash functions. The return value should be less than max, the maximum capacity 
	 * of the array
	 * @param key
	 * @return
	 */
	 private int hash(String key) {
	        int value = key.charAt(0) - 31;
	        for (int i = 0; i<key.length(); i++) { //Calculates the new int by hashing the key and in doing this creates a new unique key
	            int c = key.charAt(i) - 31;
	            value = (value * 94 + c) % this.max;
	        }
	        return value; //returns the new int
	    }

	/**
	 * Return true if n is prime
	 * @param n
	 * @return
	 */
	 private boolean isPrime(int n) {
         int b = (int) Math.sqrt(n);
         for (int a = 2; a < b; a++) { 
                 if (n % a == 0) {        //  if its 0 it is not prime
                         return false;
                 }
         }
         return true;
 }

	/**
	 * Get the smallest prime number which is larger than n
	 * @param n
	 * @return
	 */
	 private int nextPrime(int n) {
		 
		 if(isPrime(n) == true) return n;
	        else return nextPrime(n + 1); // keeps recurring until it is prime
 }

	/**
	 * Resize the hashtable, to be used when the load factor exceeds maxLoad. The new size of
	 * the underlying array should be the smallest prime number which is at least twice the size
	 * of the old array.
	 */
	 private void resize() {
		 max = nextPrime(max * 2);//sets the new max which is the next smallest prime number 
	        Object[] temp = arr;
	        arr = new Object[max];
	        for (int j = 0; j < temp.length; j++){
	        	if (temp[j] != null){//checks to make sure j is not null 
	        		Hashtable<V>.Pair pair = (Hashtable<V>.Pair) temp[j]; //adds j to the hash table 
	                String str = pair.key;
	                V value = (V) pair.value;
	                put(str, value); //inserts the pair
	            }
	        }
	    }
	 
	 
	 
	
	/**
	 * Instances of Pair are stored in the underlying array. We can't just store
	 * the value because we need to check the original key in the case of collisions.
	 * @author jb259
	 *
	 */
	 private class Pair {
	        private String key;
	        private Object value;
	 
	        public Pair(String key, Object value) {
	            this.key = key;
	            this.value = value;
	        }
	    }

}
