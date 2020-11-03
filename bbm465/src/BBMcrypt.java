//import java.nio.charset.StandardCharsets;
import java.util.*;  

import java.util.Base64;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors


public class BBMcrypt {

	//FIXED VARIABLES FOR FEISTEL CIPHER
	static final int totalRounds = 10;
	static final int blockSize = 96;
	static final int keySize = 96;
	
	
	//SUBSTITION BOX. IT IS STORED AS A 2-DIMENSIONAL ARRAY
	static final String[][] substititionBox = {
			{"0010","1100","0100","0001","0111","1010","1011","0110","1000","0101","0011","1111","1101","0000","1110","1001"},
			{"1110","1011","0010","1100","0100","0111","1101","0001","0101","0000","1111","1010","0011","1001","1000","0110"},
			{"0100","0010","0001","1011","1010","1101","0111","1000","1111","1001","1100","0101","0110","0011","0000","1110"},
			{"1011","1000","1100","0111","0001","1110","0010","1011","0110","1111","0000","1001","1010","0100","0101","0011"},
	};	//VALUES CHECKED 2 TIMES. NO TYPO.
	
	
	//METHODS TO READ FILES
	public static String readKeyFile(String filename) {
	    String keyFileString = "";
		try {
			File file = new File(filename); 
		    Scanner sc = new Scanner(file); 
		  
		    
		    sc.useDelimiter("\\Z"); 
		    keyFileString = sc.next();
		    System.out.println(keyFileString); 
	      } 
	    catch (FileNotFoundException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }
	    
		byte[] decoded = Base64.getDecoder().decode(keyFileString);
	    String decodedString = new String(decoded);
	    System.out.println(decodedString);
		
		return decodedString;
	}	//end of readKeyFile method
	
	public static String readInputFile(String filename) {
	    String inputFileString = "";
		try {
			File file = new File(filename); 
		    Scanner sc = new Scanner(file); 
		  
		    
		    sc.useDelimiter("\\Z"); 
		    inputFileString = sc.next();
		    System.out.println(inputFileString); 
	      } 
	    catch (FileNotFoundException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }
	    
	    //if length of the message is less than 96 bits, add zeros until the length is 96 bits.
	    if(inputFileString.length()%blockSize!=0) {
	    	while(inputFileString.length()%blockSize!=0) {
	    		inputFileString = inputFileString + "0";
	    	}
	    }
		
		return inputFileString;
	}	//end of readKeyFile method	
	
	//LEFT CIRCULAR SHIFT FOR SUBKEY GENERATION
	public static String leftCircularShift(String keyString) {
		
		String shiftedKey = "";
		char[] charArray = keyString.toCharArray();
		char firstChar = charArray[0];
		
		for(int i=0; i<charArray.length-1; i++) {
			charArray[i] = charArray[i+1];
		}
		charArray[blockSize-1] = firstChar;
		
		shiftedKey = String.valueOf(charArray);
		return shiftedKey;
	}
		
	//SUBKEY GENERATION FUNCTION
	public static String[] subkeyGeneration(String keyString, int totalRounds) {
		String[] finalKeyArray = new String[10];
		String tempString = "";
		String previousKey = keyString;
		
		String finalTempString = "";
		
		for(int i=0; i<totalRounds; i++) {
			tempString = previousKey;	//96 bitlik
			tempString = leftCircularShift(tempString);	//96 bit
			previousKey = tempString;	//96 bit, shiftlenmiþ
			System.out.println(tempString);
			//permuted choice
			char[] tempCharArray = tempString.toCharArray();
			
			if(i%2==0) {		
				System.out.println("%2==0");
				
				for(int j=0; j<blockSize;j=j+2) {
					finalTempString = finalTempString.concat(String.valueOf(tempCharArray[j]));
				}
			}
			else {
				System.out.println("%2==1");
				for(int j=1; j<blockSize;j=j+2) {
					finalTempString = finalTempString.concat(String.valueOf(tempCharArray[j]));
				}				
			}
		finalKeyArray[i] = finalTempString;
		finalTempString = "";
		}
		
		
		return finalKeyArray;
	}
	
	//SCRAMBLE FUNCTION (NOT FINISHED YET)
	public static String scrambleFunction(String rightSubText, String subKey) {
		String result = "";
		
		return result;
	}
	
	
	//METHOD FOR FEISTEL CIPHER
	public static String feistelCipherEncryption(String originalText, String keyString) {
		
		String encryptedBlock = "";
		
		
		
		
		return encryptedBlock;
	}
	
	public static String feistelCipherDecryption(String originalText, String keyString) {
		
		String finalText = "";
		
		
		return finalText;
	}	
	
	
	
	//METHODS TO ENCRYPT-DECRYPT TEXTS
	public static String ECBEncryption(String originalText, String keyString) {
		
		String finalText = "";
		
		if(originalText.length()==96) {	//if string length<=96, there are only 1 block
			finalText = feistelCipherEncryption(originalText, keyString);
		}
		else {	//if string length>96, there are more than 1 block 
			int numberOfBlocks = originalText.length()/96;
			System.out.println("numofblocks: " + numberOfBlocks);
		
			String tempStringBlock = "";
			
			for(int i=0;i<numberOfBlocks;i++) {
				tempStringBlock = feistelCipherEncryption(originalText.substring(i*blockSize, (i+1)*blockSize), keyString);
				System.out.println(tempStringBlock.length());
				finalText = finalText.concat(tempStringBlock);				
			}
		}
		return finalText;
	}
	
	
	
	public static void main(String[] args) {
		
		//commandLine: given command line argument
		//keyTxt: key filename
		//inputTxt: input filename
		//outputTxt: output filename
		String commandLine;
		String keyTxt, inputTxt, outputTxt;
		keyTxt = inputTxt = outputTxt = "none";
		
		
		//actionType: enc or dec
		//modeType: ECB, CBC, and OFB
		String actionType="none";
		String modeType="none";
		
		Scanner sc=new Scanner(System.in);  
		
		System.out.println("Enter the command line arguments:");
		commandLine= sc.nextLine();
		
		System.out.println(commandLine);
		
		
		String[] commandLineArray = commandLine.split(" ", 11);
        
		for (int i = 0; i < commandLineArray.length; i++) {	// for loop to read the command line arguments
			
            if(commandLineArray[i].equals("enc") || commandLineArray[i].equals("dec")) {
            	actionType = commandLineArray[i];
            }
            else if(commandLineArray[i].equals("-K")) {
            	keyTxt = commandLineArray[i+1];
            }
            else if(commandLineArray[i].equals("-I")) {
            	inputTxt = commandLineArray[i+1];
            }
            else if(commandLineArray[i].equals("-O")) {
            	outputTxt = commandLineArray[i+1];
            }            
            else if(commandLineArray[i].equals("-M")) {
            	modeType = commandLineArray[i+1];
            }             
                  
        }	//end for loop to read the command line arguments
		
		System.out.println(actionType);
		System.out.println(keyTxt);
		System.out.println(inputTxt);
		System.out.println(outputTxt);
		System.out.println(modeType);
		
		System.out.println(keySize);
		
		//ARGUMENT LINE COMMAND READING IS FINISHED.
		//STARTING TO READ THE KEY AND INPUT FILES
		
		
		
		//READING THE KEY FILE
	    String decodedKeyString = readKeyFile(keyTxt);
	    
	    System.out.println("decoded string: " + decodedKeyString);
	    //KEY FILE IS READ AND STORED INTO decodedString VARIABLE
	    
	    
	    //READING THE BINARY INPUT FILE	    
	    String inputText = readInputFile(inputTxt);
	    System.out.println("input text: " + inputText);
	    
	    
	    //**************************************************************************//
		//KEY AND INPUT FILES ARE READ. NOW GENERATING THE SUBKEYS FOR EACH ROUND	//
	    //**************************************************************************//
	    
	    
	    String[] subkeyArray = subkeyGeneration(decodedKeyString, totalRounds);

	    
	    //**************************************************************************//
		//SUBKEYS ARE GENERATED. NOW STARTING TO IMPLEMENT THE FEISTEL CIPHER		//
	    //**************************************************************************//	    
	    
	    
	    String finalString = "";
	    
	    /*if(actionType.equals("enc") && modeType.equals("ECB")) {
	    	System.out.println("*********");	
	    	finalString = ECBEncryption(inputText, decodedKeyString);
	    }*/
	    
	    
	    System.out.println(substititionBox[0][2]);
	    
	    
	    
	    
	    
	    
	    
		
		
	}	//end of main method
}	//end of BBMcrypt class
