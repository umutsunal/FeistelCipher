import java.util.*;  

import java.util.Base64;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors


public class BBMcrypt {

	//FIXED VARIABLES FOR FEISTEL CIPHER
	static final int totalRounds = 10;
	static final int blockSize = 96;
	static final int keySize = 96;
	static String initializationVector = "";
	
	
	//SUBSTITION BOX. IT IS STORED AS A 2-DIMENSIONAL ARRAY
	static final String[][] substitution = {
			{"0010","1100","0100","0001","0111","1010","1011","0110","1000","0101","0011","1111","1101","0000","1110","1001"},
			{"1110","1011","0010","1100","0100","0111","1101","0001","0101","0000","1111","1010","0011","1001","1000","0110"},
			{"0100","0010","0001","1011","1010","1101","0111","1000","1111","1001","1100","0101","0110","0011","0000","1110"},
			{"1011","1000","1100","0111","0001","1110","0010","1101","0110","1111","0000","1001","1010","0100","0101","0011"},
	};
	
	
	//METHODS TO READ FILES***********************************
	public static String readKeyFile(String filename) {
	    String keyFileString = "";
		try {
			File file = new File(filename); 
		    Scanner sc = new Scanner(file); 
		  
		    
		    sc.useDelimiter("\\Z"); 
		    keyFileString = sc.next();
	      } 
	    catch (FileNotFoundException e) {
	        System.out.println("An error occurred.");
	        e.printStackTrace();
	      }
	    
		byte[] decoded = Base64.getDecoder().decode(keyFileString);
	    String decodedString = new String(decoded);
		
		return decodedString;
	}	//end of readKeyFile method
	
	public static String readInputFile(String filename) {
	    String inputFileString = "";
		try {
			File file = new File(filename); 
		    Scanner sc = new Scanner(file); 
		  
		    
		    sc.useDelimiter("\\Z"); 
		    inputFileString = sc.next();
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
	//********************************************************
	
	
	//LEFT CIRCULAR SHIFT FOR SUBKEY GENERATION***************
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
	//********************************************************
	
	
	//SUBKEY GENERATION FUNCTION******************************
	public static String[] subkeyGeneration(String keyString, int totalRounds) {
		String[] finalKeyArray = new String[10];
		String tempString = "";
		String previousKey = keyString;
		
		String finalTempString = "";
		
		for(int i=0; i<totalRounds; i++) {
			tempString = previousKey;	//96 bitlik
			tempString = leftCircularShift(tempString);	//96 bit
			previousKey = tempString;	//96 bit, shiftlenmiş
			//System.out.println(tempString);
			//permuted choice
			char[] tempCharArray = tempString.toCharArray();
			
			if(i%2==0) {		
				for(int j=0; j<blockSize;j=j+2) {
					finalTempString = finalTempString.concat(String.valueOf(tempCharArray[j]));
				}
			}
			else {
				for(int j=1; j<blockSize;j=j+2) {
					finalTempString = finalTempString.concat(String.valueOf(tempCharArray[j]));
				}				
			}
		finalKeyArray[i] = finalTempString;
		finalTempString = "";
		}
		
		
		return finalKeyArray;
	}
	//********************************************************
	
	//XOR FUNCTION********************************************
    static String xorFunction(String firstString, String secondString, int stringSize){ 
    String resultString = ""; 
          
        // Loop to iterate over the 
        // Binary Strings 
        for (int i = 0; i < stringSize; i++) 
        { 
            // If the Character matches 
            if (firstString.charAt(i) == secondString.charAt(i)) 
            	resultString += "0"; 
            else
            	resultString += "1"; 
        } 
        return resultString; 
    } 
	//********************************************************
    
    //SUBSTITUTION BOX FUNCTION*******************************
    public static String[] substitutionBoxFunction(String[] xORedArray, String[][] substitutionBox) {
    	String[] resultArray = new String[12];
    	String tempString, outerBits, innerBits;
    	tempString = outerBits = innerBits = "";
    	
    	for(int i=0;i<12;i++) {
    		tempString = xORedArray[i];					
    		
    		outerBits = outerBits.concat(String.valueOf(tempString.charAt(0))).concat(String.valueOf(tempString.charAt(5)));
    		innerBits = innerBits.concat(String.valueOf(tempString.charAt(1))).concat(String.valueOf(tempString.charAt(2))).concat(String.valueOf(tempString.charAt(3))).concat(String.valueOf(tempString.charAt(4)));
    		
    		resultArray[i] = substitutionBox[Integer.parseInt(outerBits, 2)][Integer.parseInt(innerBits, 2)];
    		outerBits = innerBits = "";
    	}
    	return resultArray;
    }
    //********************************************************
    
    //PERMUTATION FUNCTION FOR SCRAMBLE FUNCTION**************
    public static String permutationFunction(String substitutedString) {
    	char tempChar;
    	
    	char[] charArray = substitutedString.toCharArray();
    	
    	for(int i = 0; i < 24; i++) {
    		tempChar = charArray[2*i];
    		charArray[2*i] = charArray[2*i+1];
    		charArray[2*i+1] = tempChar;
    	}
    	
    	String resultString = String.valueOf(charArray);
    	return resultString;
    }
    //********************************************************
    
	//SCRAMBLE FUNCTION (FINISHED BUT NOT TESTED)*************
	public static String scrambleFunction(String rightSubText, String subKey, String[][] substitution) {
		
		String xorResult = xorFunction(rightSubText, subKey, blockSize/2);
		
		String[] xORedArray = new String[12];
		
		for(int i=0;i<8;i++) {
			xORedArray[i] = xorResult.substring(i*6,(i+1)*6);
		}
		
		for(int j = 0; j<4; j++) {
			xORedArray[j+8] = xorFunction(xORedArray[j*2], xORedArray[j*2+1], xORedArray[j*2+1].length());
		}
		
		//NOW USE SUBSTITUTION BOX ON XORED ARRAY(12 ELEMENTS OF 6 BITS)
		String[] substitutedArray = substitutionBoxFunction(xORedArray,substitution);
		String substitutedString = "";
		for(int k = 0; k<12;k++) {
			substitutedString = substitutedString.concat(substitutedArray[k]);
		}
		//NOW USE PERMUTATION FUNCTION ON SUBSTITUTED STRING(48 BITS)
		String resultString = permutationFunction(substitutedString);	
		return resultString;
	}
	//********************************************************
	
	//METHOD FOR FEISTEL CIPHER
	public static String feistelCipherEncryption(String originalText, String[] subKeyArray) {
		
		String encryptedBlock, leftZero, rightZero;
		encryptedBlock = leftZero = rightZero = "";
		
		leftZero = originalText.substring(0, blockSize/2);
		rightZero = originalText.substring(blockSize/2, blockSize);
		
		String[] leftBlocksArray = new String[11];
		String[] rightBlocksArray = new String[11];
		
		leftBlocksArray[0] = leftZero;
		rightBlocksArray[0] = rightZero;
		
		for (int i=1; i<11; i++) {
			leftBlocksArray[i] = rightBlocksArray[i-1];
			rightBlocksArray[i] = xorFunction(leftBlocksArray[i-1], scrambleFunction(rightBlocksArray[i-1], subKeyArray[i-1], substitution), 48); 	
		
		}
		
		encryptedBlock = encryptedBlock.concat(leftBlocksArray[10]).concat(rightBlocksArray[10]);
		
		
		return encryptedBlock;
	}
	
	public static String feistelCipherDecryption(String originalText, String[] subKeyArray) {
		
		String decryptedBlock, leftTen, rightTen;
		decryptedBlock = leftTen = rightTen = "";
		
		leftTen = originalText.substring(0, blockSize/2);
		rightTen = originalText.substring(blockSize/2, blockSize);
		
		String[] leftBlocksArray = new String[11];
		String[] rightBlocksArray = new String[11];		
		
		leftBlocksArray[10] = leftTen;
		rightBlocksArray[10] = rightTen;
		
		for (int i=9; i>-1; i--) {
			rightBlocksArray[i] = leftBlocksArray[i+1];
			
			leftBlocksArray[i] = xorFunction(rightBlocksArray[i+1], scrambleFunction(leftBlocksArray[i+1], subKeyArray[i], substitution), 48); 	
		}
		
		decryptedBlock = decryptedBlock.concat(leftBlocksArray[0]).concat(rightBlocksArray[0]);
		
		return decryptedBlock;
	}	

	//METHODS TO ENCRYPT-DECRYPT TEXTS
	public static String ECBEncryption(String originalText, String[] subKeyArray) {
		
		String finalText = "";
		
		if(originalText.length()==96) {	//if string length<=96, there are only 1 block
			finalText = feistelCipherEncryption(originalText, subKeyArray);
		}
		else {	//if string length>96, there are more than 1 block 
			int numberOfBlocks = originalText.length()/96;
			System.out.println("numofblocks: " + numberOfBlocks);
		
			String tempStringBlock = "";
			
			for(int i=0;i<numberOfBlocks;i++) {
				tempStringBlock = feistelCipherEncryption(originalText.substring(i*blockSize, (i+1)*blockSize), subKeyArray);
				System.out.println(tempStringBlock.length());
				finalText = finalText.concat(tempStringBlock);				
			}
		}
		return finalText;
	}
	
	public static String ECBDecryption(String cipherText, String[] subKeyArray) {
		
		String finalText = "";
		
		if(cipherText.length()==96) {	//if string length<=96, there are only 1 block
			finalText = feistelCipherDecryption(cipherText, subKeyArray);
		}
		else {	//if string length>96, there are more than 1 block 
			int numberOfBlocks = cipherText.length()/96;
			System.out.println("numofblocks: " + numberOfBlocks);
		
			String tempStringBlock = "";
			
			for(int i=0;i<numberOfBlocks;i++) {
				tempStringBlock = feistelCipherDecryption(cipherText.substring(i*blockSize, (i+1)*blockSize), subKeyArray);
				System.out.println(tempStringBlock.length());
				finalText = finalText.concat(tempStringBlock);				
			}
		}
		return finalText;
	}
	
	public static String CBCEncryption(String originalText, String[] subKeyArray) {
		String finalText = "";
		
		if(originalText.length()==96) {	//if string length<=96, there are only 1 block
			String xORedString = xorFunction(originalText, initializationVector, blockSize);			
			finalText = feistelCipherEncryption(xORedString, subKeyArray);
		}
		else {	//if string length>96, there are more than 1 block 
			int numberOfBlocks = originalText.length()/96;
			System.out.println("numofblocks: " + numberOfBlocks);
		
			String[] ciphertextArray = new String[numberOfBlocks+1];
			ciphertextArray[0] = initializationVector;
			
			for(int i = 1; i < numberOfBlocks+1; i++) {
				ciphertextArray[i] = feistelCipherEncryption(xorFunction(originalText.substring((i-1)*blockSize, (i)*blockSize), ciphertextArray[i-1], blockSize), subKeyArray);
				finalText = finalText.concat(ciphertextArray[i]);
			}
		}
		return finalText;
	}
	
	public static String CBCDecryption(String cipherText, String[] subKeyArray) {
		String finalText = "";
		
		if(cipherText.length()==96) {	//if string length<=96, there are only 1 block
			finalText = xorFunction(initializationVector, feistelCipherDecryption(cipherText, subKeyArray), blockSize);
		}
		else {	//if string length>96, there are more than 1 block 
			int numberOfBlocks = cipherText.length()/96;
			System.out.println("numofblocks: " + numberOfBlocks);
		
			String[] ciphertextArray = new String[numberOfBlocks+1];
			ciphertextArray[0] = initializationVector;
			String tempCiphertext = "";
			
			//String[] originaltextArray = new String[numberofBlocks];
			
			for (int x = 1; x<numberOfBlocks+1;x++) {
				tempCiphertext = cipherText.substring((x-1)*blockSize, x*blockSize);
				ciphertextArray[x]=tempCiphertext;
				System.out.println("tempciphertext: " + tempCiphertext);
			}
			
			
			for(int i = 1; i < numberOfBlocks+1; i++) {
				//ciphertextArray[i] = feistelCipherEncryption(xorFunction(cipherText.substring((i-1)*blockSize, (i)*blockSize), ciphertextArray[i-1], blockSize), subKeyArray);
				finalText = finalText.concat(xorFunction(ciphertextArray[i-1], feistelCipherDecryption(ciphertextArray[i], subKeyArray), blockSize));
			}
		}				
		return finalText;
	}
	
	public static String OFCEncryption(String originalText, String[] subKeyArray) {
		String finalText = "";
		
		
		return finalText;
	}
	
	public static String OFCDecryption(String cipherText, String[] subKeyArray) {
		String finalText = "";
		
		
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
	    
	    
	    String[] subKeyArray = subkeyGeneration(decodedKeyString, totalRounds);

	    
	    //**************************************************************************//
		//SUBKEYS ARE GENERATED. NOW INITIALIZING THE INITIALIZATION VECTOR 		//
	    //**************************************************************************//	    
		
	    for(int i=0;i<96;i++) {
			initializationVector = initializationVector.concat("1");
		}
	    
	    //**************************************************************************//
		//VECTOR IS INITIALIZED. NOW STARTING TO IMPLEMENT THE FEISTEL CIPHER		//
	    //**************************************************************************//	    	    
	    
	    String finalString = "";
	    
	    if(actionType.equals("enc") && modeType.equals("ECB")) {
	    	System.out.println("*********");	
	    	finalString = ECBEncryption(inputText, subKeyArray);
	    	System.out.println("FINAL STRING FOR ECB ENCRYPTION:");
	    	System.out.println(finalString);
	    }
	    else if(actionType.equals("dec") && modeType.equals("ECB")) {
	    	System.out.println("*********");	
	    	finalString = ECBDecryption(inputText, subKeyArray);
	    	System.out.println("FINAL STRING FOR ECB DECRYPTION:");
	    	System.out.println(finalString);
	    }
	    else if(actionType.equals("enc") && modeType.equals("CBC")) {
	    	System.out.println("*********");	
	    	finalString = CBCEncryption(inputText, subKeyArray);
	    	System.out.println("FINAL STRING FOR CBC ENCRYPTION:");
	    	System.out.println(finalString);
	    }	    
	    else if(actionType.equals("dec") && modeType.equals("CBC")) {
	    	System.out.println("*********");	
	    	finalString = CBCDecryption(inputText, subKeyArray);
	    	System.out.println("FINAL STRING FOR CBC DECRYPTION:");
	    	System.out.println(finalString);
	    }		    
	    //scrambleFunction("100101111001010000100101011001101011111101000010","011001001100010010110001011100100010111000000000",substitution);		 
	    
		
		
	}	//end of main method
}	//end of BBMcrypt class
