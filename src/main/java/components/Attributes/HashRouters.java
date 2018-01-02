package components.Attributes;

import java.util.*;

/**
 * @author Moshe
 */

public class HashRouters<S,T>{
	private int countOfRouters = 0;
	private Hashtable<S, ArrayList<T>> routers;

	public static void main(String[] args) {

	}

	public HashRouters() {
		this.routers = new Hashtable<S, ArrayList<T>>();
	}

	/**
	 * This function add ArrayList of elements with one key into the hashTable
	 * @param key - type of S. the key of the element in the hashTable
	 * @param value - type of T. the value which has the key above.
	 */
	public void addElement(S key, T value)
	{
		if (routers.containsKey(key)) {
			ArrayList<T> n = routers.get(key);
			n.add(value);
			routers.put(key, n);
		}
		else
		{
			countOfRouters++;
			ArrayList<T> n = new ArrayList<T>();
			n.add(value);
			routers.put(key, n);
		}
	}

	/**
	 *The funcion gets a key and ArrayList of objects for this key, and inserts the objects to the key in the exist hash Table.
	 * @param key - The key of the list. Type of key must be as a type of keys of the HashTable
	 * @param values - The objects that will be inserted in the list that belongs to the key
	 */
	public void addListElement(S key, ArrayList<T> values)
	{
		if (routers.containsKey(key)) {
			ArrayList<T> n = routers.get(key);
			for (T value : values)
				n.add(value);
			routers.put(key, n);
		}
		else
		{
			countOfRouters++;
			routers.put(key, values);
		}
	}

	/**
	 *
	 * @return all the hashTable.
	 */
	public Hashtable<S, ArrayList<T>> getRoutersHashTable()
	{
		return routers;
	}

	/**
	 * The function get key and integer k. The function returns maximum the k best elements of this key.
	 * @param key - the key of the k elements.
	 * @param k - maximal number of the best element.
	 * @param comp - The comparator for sort the elements.
	 * @return maximum of K best elements from the specified key. If this key is not foun return null.
	 */
	public ArrayList<T> getKBest(S key, int k, Comparator<T> comp)
	{
		ArrayList<T> allAppearance;
		ArrayList<T> kBest = new ArrayList<T>();

		if(!routers.containsKey(key))
			kBest = null;
		else
		{
			allAppearance = routers.get(key);
			Collections.sort(allAppearance, comp);

			for(int i = 0; i < allAppearance.size() && i < k; i++)
			{
				kBest.add(allAppearance.get(i));
			}
		}

		return kBest;
	}

	/**
	 * The function merge between the hash Table of this class to the hash Table that the function gets.
	 * @param addHash - Hash Table the will merge. The type of keys and type of values of the hash Table must be similar
	 * 			to the type of keys and type of values of the hash Table in this class.
	 */
	public void mergeToHash(HashRouters<S, T> addHash)
	{
		//HashRouters<S, ArrayList<T>> mergeHash = new HashRouters<>();

		Set<S> keysAddHash = addHash.routers.keySet();

		for(S key : keysAddHash )
		{
			addListElement(key,addHash.routers.get(key));
		}
	}
	/**
	 * @return the number of different MAC addresses in the hashTable
	 */
	public int getCountOfRouters(){ return countOfRouters; }
}
