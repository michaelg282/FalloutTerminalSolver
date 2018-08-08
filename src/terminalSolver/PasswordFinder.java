package terminalSolver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * 
 */
public class PasswordFinder{
	
	
	private ArrayList<String> _passwords; //list of possible passwords
	private ArrayList<ArrayList<Integer>> _passwordLikeness; //matrix of likeness values
	private boolean _hasFoundPassword;
	
	/**
	 * Constructs a PasswordFinder using a pre-set list of potential passwords.
	 * Useful when testing and debugging to avoid inputting values.
	 * 
	 * @see #PasswordFinder(List)
	 */
	public PasswordFinder(){
		_passwords = new ArrayList<String>();
		_passwordLikeness = new ArrayList<ArrayList<Integer>>();
		_hasFoundPassword = false;
		_passwords.add("answer");
		_passwords.add("stones");
		_passwords.add("rounds");
		_passwords.add("pulled");
		_passwords.add("access");
		_passwords.add("closet");
		_passwords.add("weaken");
		_passwords.add("weaker");
		_passwords.add("defeat");
		_passwords.add("higher");
		_passwords.add("copies");
		this.setPasswordLikeness();
	}
	
	/**
	 * Constructs a PasswordFinder using the provided list of possible passwords.
	 * The elements of the given list are copied into a new list to avoid data manipulation.
	 * If an empty list is given, PasswordFinder will use an empty list.
	 * 
	 * @param passwords		list of potential passwords
	 */
	public PasswordFinder(List<String> passwords){
		_passwordLikeness = new ArrayList<ArrayList<Integer>>();
		_hasFoundPassword = false;
		if(passwords.size() == 0){
			_passwords = new ArrayList<String>();
		}
		else{
			_passwords = new ArrayList<String>(passwords);
		}
		this.setPasswordLikeness();
	}
	
	/**
	 * Fills the _passwordLikness matrix with the password likeness values.
	 * If the list of passwords is empty the matrix will be left empty as well.
	 */
	private void setPasswordLikeness(){
		ArrayList<Integer> column;  //matrix filled from top to bottom, left to right
		for(int i = 0; i < _passwords.size(); i++){ // for each password (columns)
			column = new ArrayList<Integer>(); 
			for(int j = 0; j < _passwords.size(); j++){ // for each password (rows)
				column.add(getLikeness(_passwords.get(i), _passwords.get(j))); //get likeness between two passwords and add to the column
			}
			_passwordLikeness.add(column); //add column to the matrix
		}
	}
	
	/**
	 * Gets the number of letters, by index, that two strings have in common.
	 * <p>
	 * For example: The characters at index 0 will be compared to each other, then characters at index 1.
	 * 
	 * @param 	s1 String to compare to s2
	 * @param 	s2 String to compare to s1
	 * @return 	number of letters that match by position between s1 and s2. 
	 * 			If s1 and s2 are not of equal length or one of them is empty -1 is returned.
	 */
	private int getLikeness(String s1, String s2){
		/* Uses a modified version of Hamming distance that instead of finding the number of different characters,
		 * finds the number of similar characters, by index in the string
		 */
		if(s1.length() == 0 || s1.length() != s2.length()){
			return -1;
		}
		else if(s1.length() == 1){
			return (s1.equals(s2) ? 1 : 0);
		}
		else{
			//calls getLikeness on the strings minus the first character
			return getLikeness( s1.substring(1, s1.length()), s2.substring(1, s2.length()) ) // string1 and string2 from indices 1 to n
					+ ( (s1.substring(0, 1).equals(s2.substring(0, 1))) ? 1 : 0 ); //compares the beginning characters of the strings
		}
	}
	
	/**
	 * Finds the best password to try that will remove the largest amount of possible passwords.
	 * 
	 * @return	password that will eliminate the most possible passwords if it is incorrect.
	 * 			If no passwords were provided when this class was instantiated, "There are no passwords" will be returned.
	 */
	public String suggestedPassword(){
		/* to evaluate the best password guess we want to find the password that will eliminate the most passwords
		 * to do this we take a tally of the different likeness values in each column and note the largest tally for this column
		 * then we find how many different likeness values were in that column
		 * then we check this column with the global value(s)
		 * the global value is the smallest maximum tally we have found. this is because the smaller the maximum value, the other tallys in that column must be smaller
	     * example: there are 10 passwords, the global smallest maximum tally is 4,   if the likeness is the smallest maximum then only 4 passwords remain,
		 *  	   if the likeness is not the smallest maximum then 4 or less passwords remain
		 * in addition to the above, if a local maximum tally is the same as the global maximum tally, then we look at how many different likeness values are in each column,
		 * the more unique values the more passwords that can be removed
		 */
		
		String password = ""; //password to be suggested
		int[] likenessCount; //array used to tally the different likeness values in a column. the likeness value is the index, the value in this array is the tally
		int localMax; //the largest tally in the current column
		int localDiversity; //the number of different likeness values in the current column
		int globalDiversity = 0; //the number of different likeness values in the column of the best password to be guessed
		int globalMinimumMax = 100; //the smallest largest tally seen so far
		
		if(_passwords.size() == 0){
			_hasFoundPassword = true;
			return "There are no passwords";
		}
		else if(_passwords.size() == 1){ //only one password left so it must be correct
			_hasFoundPassword = true;
			password = _passwords.get(0);
		}
		else{ //more than one password left
			for(int i = 0; i < _passwords.size(); i++){ //i represents the column
				likenessCount = new int[_passwords.get(i).length()];
				localMax = 0;
				localDiversity = 0;
				
				//find the local max of a column
				for(int j = 0; j < _passwords.size(); j++){ //j represents the row
					if(i != j){ //ignore the entry that compares the password to itself
						likenessCount[_passwordLikeness.get(i).get(j)]++; //increment the tally of the likeness value
						if(likenessCount[_passwordLikeness.get(i).get(j)] > localMax){ //is this tally the new max 
							localMax = likenessCount[_passwordLikeness.get(i).get(j)];
						}
					}
				}
				//find the number of unique likeness values in a column
				for(int j = 0; j < likenessCount.length; j++){
					if(likenessCount[j] != 0){ //if there is a zero, there were not any entries of this likeness value in the column
						localDiversity++;
					}
				}
				/* check if the global values and password to be suggested need to be updated
				 * we want the smallest maximum of tallies or if the values are the same the one with the most unique values, 
				 * see the comments at the top of the method for further explanation
				 */
				if((localMax < globalMinimumMax) || ( (localMax == globalMinimumMax) && (localDiversity > globalDiversity) )){
					globalMinimumMax = localMax;
					password = _passwords.get(i);
					globalDiversity = localDiversity;
				}
			}
		}
		return password;
	}
	
	/**
	 * Removes a dud password from the password list.
	 * 
	 * @param password 	dud password to be removed
	 * @return			true if the password was removed
	 * 					false if the password was not in the list
	 */
	public boolean removeDud(String password){
		int indexToBeRemoved;
		if(_passwords.contains(password)){ 
			indexToBeRemoved = _passwords.indexOf(password);
			_passwordLikeness.remove(indexToBeRemoved);
			_passwords.remove(indexToBeRemoved);
			//remove horizontal entry in _passwordLikeness for the incorrect password
			for(int j = 0; j < _passwordLikeness.size(); j++){
				_passwordLikeness.get(j).remove(indexToBeRemoved);
			}
			
			if(_passwords.size() == 1){
				_hasFoundPassword = true;
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Removes the incorrect password from the password list along with other passwords that don't share the same likeness value
	 * 
	 * @param password	incorrect password to be removed
	 * @param likeness	the likeness given for the incorrect password
	 */
	public void removePasswords(String password, int likeness){
		LinkedList<String> removeList = new LinkedList<String>();
		int index;
		if(_passwords.contains(password)){
			index = _passwords.indexOf(password);
				
			//get list of passwords to remove
			for(int i = 0; i < _passwords.size(); i++){ //only searches the column that the password corresponds to
				//the incorrect password chosen will be added to the list because the likeness given does not equal the likeness of the password compared to itself
				if(_passwordLikeness.get(index).get(i) != likeness){
					removeList.add(_passwords.get(i));
				}
			}
			
			//remove the passwords from the password list and the passwordLikeness matrix
			for(int i = 0; i < removeList.size(); i++){ //for each password to be removed
				int indexToBeRemoved = _passwords.indexOf(removeList.get(i));
				_passwordLikeness.remove(indexToBeRemoved);
				_passwords.remove(indexToBeRemoved);
				//remove horizontal entry in _passwordLikeness for the incorrect password
				for(int j = 0; j < _passwordLikeness.size(); j++){
					_passwordLikeness.get(j).remove(indexToBeRemoved);
				}
			}
			
			if(_passwords.size() == 1){
				_hasFoundPassword = true;
			}
		}
	}
	
	/**
	 * Provides a String of remaining potential passwords that is formatted to be displayed.
	 * <p>
	 * Example: <br>
	 * <pre>
	 * Remaining Passwords
	 * ----
	 * bird
	 * fish
	 * lion
	 * seal
	 * bull
	 * ----
	 * </pre>
	 * 
	 * @return String of potential passwords remaining
	 */
	public String toString(){
		/*String format looks like the following
		 * 
		 *	Remaining Passwords
		 *	----
		 *	bird
		 *	fish
		 *	lion
		 *	seal
		 *	bull
		 *	----
		 */
		String output = "";
		if(_passwords.size() == 0){
			output = "There are no passwords to display";
		}
		else{
			output += "\nRemaining Passwords\n";
			//add dash line
			for(int i = 0; i < _passwords.get(0).length(); i++){
				output += "-";
			}
			output += "\n";
			//print passwords
			for(String password: _passwords)
				output += password + "\n";
			//add dash line
			for(int i = 0; i < _passwords.get(0).length(); i++){
				output += "-";
			}
			output += "\n";
		}
		return output;
	}
	
	/**
	 * Provides a String of remaining potential passwords and the likeness value table that is formatted to be displayed.
	 * <p>
	 * Example:<br>
	 * <pre>
	 * Remaining Passwords
	 * -------------------------
	 * bird fish lion seal bull 
	 * -------------------------
	 * bird   1    1    0    1  
	 * -------------------------
	 *      fish   1    0    0  
     *      --------------------
     *           lion   0    0  
     *           ---------------
     *                seal   1  
     *                ----------
     *                     bull 
     *                     -----
     * </pre>
     * 
	 * @return String of remaining passwords and their likeness values
	 */
	public String toLikenessString(){
		/*String format looks like the following
		 *
		 *	Remaining Passwords
		 *	-------------------------
		 *	bird fish lion seal bull 
		 *	-------------------------
		 *	bird   1    1    0    1  
		 *	-------------------------
		 *		 fish   1    0    0  
     	 *		 --------------------
         *  		  lion   0    0  
         *  		  ---------------
         *      		   seal   1  
         *      			----------
         *           		    bull 
         *          			-----
		 */
		
		String spaces = "";
		String halfOfSpaces = "";
		String dashes = "";
		String output = "";
		if(_passwords.size() == 0){
			output = "There are no passwords to display";
		}
		else{
			//spaces and dashes are used for formatting
			for(int i = 0; i < _passwords.get(0).length(); i++){
				spaces += " ";
				dashes += "-";
			}
			//halfOfSpaces is used for formatting
			for(int i = 0; i < Math.floor(_passwords.get(0).length()/2); i++){
				halfOfSpaces += " ";
			}
			
			output += "\nRemaining Passwords\n";
			//line of dashes
			for(int i = 0; i < _passwords.size(); i++){
				output += dashes+"-";
			}
			output += "\n";
			//print password list horizontally
			for(int i = 0; i < _passwords.size(); i++){
				output += _passwords.get(i)+" ";
			}
			output += "\n";
			//print dashes under horizontal password list
			for(int i = 0; i < _passwords.size(); i++){
				output += dashes+"-";
			}
			output += "\n";
			//print passwords vertically and likeness values
			for(int i = 0; i < _passwords.size(); i++){
				//indent starting position
				for(int k = 0; k < i; k++){
					output += spaces+" ";
				}
				//print password and likeness values
				for(int j = i; j < _passwords.size(); j++){
					if(j == i){ //print password 
						output += _passwords.get(j)+" ";
						if(_passwords.get(0).length() % 2 != 0){ //formating for odd length passwords
							output += halfOfSpaces;
						}
					}
					else{ //print likeness value
						if(_passwords.get(0).length() % 2 == 0){  //formatting for even length passwords
							output += halfOfSpaces + _passwordLikeness.get(i).get(j) + halfOfSpaces;
						}
						else{ //formatting for odd length passwords
							output += _passwordLikeness.get(i).get(j) + spaces;
						}
					}
				}
				output += "\n";
				//indent dash line underneath previous row
				for(int k = 0; k < i; k++){
					output += spaces+" ";
				}
				//add dashes on line below the password and likeness values
				for(int j = i; j < _passwords.size(); j++){
					output += dashes+"-";
				}
				output += "\n";
			}
		}
		return output;
	}
	
	/**
	 * Returns whether or not the password has been found.
	 * 
	 * @return 	true if the password has been found,
	 * 			false if it has not been found
	 */
	public boolean hasFoundPassword(){
		return _hasFoundPassword;
	}
	
}