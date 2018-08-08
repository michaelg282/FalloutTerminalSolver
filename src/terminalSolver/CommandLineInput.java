package terminalSolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInput {

	private PasswordFinder _passwordFinder;
	private Scanner _scan;
	
	/**
	 * Constructs a CommandLineInput class. Uses simpleDisplay
	 * 
	 * simpleDisplay:
	   Remaining Passwords
		----
		bird
		fish
		lion
		seal
		bull
		----
	 */
	public CommandLineInput(){
		_scan = new Scanner(System.in);
		_passwordFinder = new PasswordFinder(this.passwordEntry());
		this.run("simpleDisplay");
	}
	
	/**
	 * Constructs a CommandLineInput class that uses the given display type
	 * 
	 * @param displayType "simpleDisplay" for list of remaining possible passwords or "likenessDisplay" for matrix of likeness values of remaining possible passwords
	 * 
	 * simpleDisplay:
		    Remaining Passwords
			----
			bird
			fish
			lion
			seal
			bull
			----
	 * 
	 * likenessDisplay<br>
	  		Remaining Passwords<br>
			-------------------------<br>
			bird fish lion seal bull <br>
			-------------------------<br>
			bird   1    1    0    1  <br>
			-------------------------<br>
				 fish   1    0    0  <br>
     			 --------------------<br>
          			  lion   0    0  <br>
          			  ---------------<br>
               			   seal   1  <br>
               			   ----------<br>
                    			bull <br>
                    			-----<br>
	 */
	public CommandLineInput(String displayType){
		_scan = new Scanner(System.in);
		_passwordFinder = new PasswordFinder(this.passwordEntry());
		if(displayType.equals("simpleDisplay") || displayType.equals("likenessDisplay")){
			this.run(displayType);
		}
		else{
			this.run("simpleDisplay");
		}
	}
	
	/**
	 * Handles the input of possible passwords
	 * 
	 * @return list of passwords entered
	 */
	private List<String> passwordEntry(){
		LinkedList<String> passwordList = new LinkedList<String>();
		String entry = "";
		
		System.out.println("Enter passwords separated by a return keystroke, enter 0 when finished.");
		entry = _scan.nextLine();
		loop: while(!entry.equals("0")){
			if(passwordList.size() != 0){ //gets input for all passwords after the first
				// while the entered password is not the same length 
				while(entry.length() != passwordList.getFirst().length()){ 
					entry = _scan.nextLine();
					if(entry.equals("0")){ //user wants to stop input
						break loop;
					}
					else if(entry.length() != passwordList.getFirst().length()){ //password not the correct length
						System.out.println("Length does not match other passwords. Try again.");
					}
				}
			}
			//add password to the list
			passwordList.add(entry);
			entry = "";
		}
		return passwordList;
	}
	
	/**
	 * Runs the program
	 * 
	 * @param displayType "simpleDisplay" to print out remaining passwords, "likenessDisplay" to print out matrix with likeness values
	 * 		defaults to simpleDisplay
	 */
	public void run(String displayType){
		String entry = "";
		String suggestedPassword = "";
		boolean repeat = true;
		boolean quit;
		int likeness = 0;
				
		while(repeat){ //for multiple uses
			quit = false;
			
			while(!_passwordFinder.hasFoundPassword() && quit == false){ //core loop for one set of passwords
				if(displayType.equals("likenessDisplay")){
					System.out.println(_passwordFinder.toLikenessString());
				}
				else{
					System.out.println(_passwordFinder.toString());
				}
				
				suggestedPassword = _passwordFinder.suggestedPassword();
				if(suggestedPassword.equals("There are no passwords")){
					quit = true;
				}
				else{
					System.out.println("Suggested Password: " + suggestedPassword);
					System.out.println("Enter 0 to quit program.");
					System.out.print("Enter password attempted or 'remove dud': ");
					entry = _scan.nextLine();
					switch(entry){
						case "remove dud":
							System.out.print("Enter the dud password to remove: ");
							entry = _scan.nextLine();
							if(entry.equals("0")){
								quit = true;
								break;
							}
							else{
								_passwordFinder.removeDud(entry);
								break;
							}
						case "0":
							quit = true;
							break;
						default:
							System.out.print("Enter likeness: ");
							while(!_scan.hasNextInt()){
								_scan.next();
							}
							likeness = _scan.nextInt();
							_scan.nextLine();
							if(likeness == entry.length()){ //the password chosen was correct
								quit = true;
							}
							_passwordFinder.removePasswords(entry, likeness);
							break;
					}
				}
			}
			
			//check if the user wants to try another set of passwords
			System.out.print("Enter new set of passwords (yes/no):");
			if(_scan.nextLine().equalsIgnoreCase("no")){
				repeat = false;
			}
			else{
				System.out.println();
				_passwordFinder = new PasswordFinder(this.passwordEntry());
			}
		}
		_scan.close();
	}
	
	public static void main(String[] args) {
		CommandLineInput program;
		if(args.length > 0){
			program = new CommandLineInput(args[0]);
		}
		else{
			program = new CommandLineInput();
		}
	}

}
