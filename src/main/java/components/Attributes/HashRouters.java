package components.Attributes;

import java.util.*;

public class HashRouters<S,T>{

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
			ArrayList<T> n = new ArrayList<T>();
			n.add(value);
			routers.put(key, n);
		}
	}

	/**
	 *
	 * @param key
	 * @param values
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

	public void mergeToHash(HashRouters<S, T> addHash)
	{
		//HashRouters<S, ArrayList<T>> mergeHash = new HashRouters<>();

		Set<S> keysAddHash = addHash.routers.keySet();

		for(S key : keysAddHash )
		{
			addListElement(key,addHash.routers.get(key));
		}
	}
}
