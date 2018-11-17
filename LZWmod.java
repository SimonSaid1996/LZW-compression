/*************************************************************************
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/
import java.lang.*;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//Math.pow(x, y)
public class LZWmod {
    private static final int R = 256;        // number of input chars   alphabet, stay the same
    private static  int L = 512;       // number of codewords = 2^W    //number of the code words, start w at 9 and add up l
    private static  int W = 9;         // codeword width    //change the codeword width from 9-16 here and the upper one need to be changed also, if code=L we basically w++ and change the value of L also and redo it. 
    private static int MaxSize=65536;    //default setting as 9
	private static int Maxbit=16;//the maximum bits size available
	//private static char prevC;
   public static void compress() { //compress
        String input = BinaryStdIn.readString();
		//int length =input.length();
		//int i=0;
		encode(input,1);//do nothing mode will be using 1 as its indicator
    } 

	public static void modifiedMode(){
		
		//char myInput;
		TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++){
			StringBuilder temp=new StringBuilder("" + (char) i);
			st.put(/*"" + (char) i*/temp, i);//trying to store the symbol table	
		}
		int code = R+1;
		BinaryStdOut.write(1,8);
		StringBuilder temp = new StringBuilder();
		char prevC=BinaryStdIn.readChar();
		while (!BinaryStdIn.isEmpty()) {
			
            char c = BinaryStdIn.readChar();
				if(c!=prevC){
					temp.setLength(0);//reset
				}
				if(W==Maxbit){
					break;
				}
			code=encode1(prevC,c,1,st,code,temp);
			prevC=c;//set back again
        }
		//
		BinaryStdOut.write(R, W);//write data
        BinaryStdOut.close();
	}
	//need to change the put method into stringbuilders
	
	public static int encode1(char prevC,char input,int modeIndicator,TST<Integer> st,int code,StringBuilder temp){//i take the compress function into two parts so that i can reuse the code
		
		temp=st.anotherLongestPrefixOf(prevC,temp);//you should return a stringbuilder here
		BinaryStdOut.write(st.get(temp)/*.toString())*/, W);      // Print s's encoding.
				
            if ( code < L){
				temp.append(input);
				st.put(temp/*.toString()*/, code++);//put codeword into the codebook
				temp.deleteCharAt(temp.length()-1);//reset the temp back
				
			}    // Add s to symbol table
			else if(code>=L){//update the size of the codewords
					if(W<Maxbit){
						incrementW();
						updateL();
					}
			}
			return code;
	}
	
	public static void encode(String input,int modeIndicator){//i take the compress function into two parts so that i can reuse the code
		TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++){
			StringBuilder temp=new StringBuilder("" + (char) i);
			st.put(temp/*"" + (char) i*/, i);//trying to store the symbol table
		}
			//StringBuilder temp1=new StringBuilder((char) i);
       int code = R+1;  // R is codeword for EOF      so the new code book starts with the code part
		BinaryStdOut.write(modeIndicator,8);
        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
			StringBuilder temp=new StringBuilder(s);
            BinaryStdOut.write(st.get(temp), W);      // Print s's encoding.
            int t = temp.length();
			
			if(code==L&&W<Maxbit){//update the size of the codewords
				incrementW();
				updateL();
			}
							
            if (t < input.length() && code < L)    // Add s to symbol table
			{
				StringBuilder temp1=new StringBuilder(input.substring(0, t + 1));
				st.put(temp1, code++);//put codeword into the codebook
			}
            input = input.substring(t);            
        }
		
		
        BinaryStdOut.write(R, W);//write data
        BinaryStdOut.close();
	}
	public static void incrementW(){//to increase the bits size
		W++;
	}
	
	public static void resetWandL(){//reset back to default bitsize
		W=9;
		updateL();
	}
	
	public static void setWandL(int x){//pass the max codeword size to the expansion, used to increase the bits size
		W=x;
		updateL();
	}
	public static void updateL(){//to update L 
		L=(int)Math.pow(2,W);
	}
	
	public static void doNothingMod(){// the mode for doing nothing
			compress();
	}
	
	public static String addCodeBook(String input){//to iterate through every codewords in the code book, and break if reaching the Maxbit
        TST<Integer> st1 = new TST<Integer>();
        for (int i = 0; i < R; i++){
			StringBuilder temp=new StringBuilder("" + (char) i);
			st1.put(temp/*"" + (char) i*/, i);//trying to store the symbol table
		}
        int code = R+1;  // R is codeword for EOF      so the new code book starts with the code part

        while (input.length() > 0) {
            String s = st1.longestPrefixOf(input);  // Find max prefix match s.
            int t = s.length();
			if(W==Maxbit){//need to reset, which means clear out the code starting fromm 9 bits, the 13 here is just for testing
			  break;
			}
			if(code==L&&W<Maxbit){//update the size of the codewords
				incrementW();
				updateL();
			}
			
            if (t < input.length() && code < L)    // Add s to symbol table
			  //code++;
			  {
				StringBuilder temp=new StringBuilder(input.substring(0, t + 1));  
				st1.put(temp/*input.substring(0, t + 1)*/, code++);//put codeword into the codebook
			  }

            input = input.substring(t);            // Scan past s in input.
        }
		return input;//return the updated size of the codebook
	}
	
    public static void resetMod(){//reset mode, once bit size reaches 16 it will reset the bitsize back into 9
		String input = BinaryStdIn.readString();
		String prevInput=null;
		String newInput=addCodeBook(input);
		while (input.length() > 0) {
			prevInput=input;
		  if(W==Maxbit){//reaching the Maxbit, reset
			resetWandL();
			newInput=addCodeBook(newInput);//update the input string
			input=newInput;
		  }
		  
		}
		resetWandL();//reset W and endcode from now on
		encode(prevInput,2);//reset mode defined as 2 
		
	}
	
	public static String addCodeBookMon(String input,int totalFileSize){//to iterate through every codewords in the code book, and break if reaching the Maxbit
	TST<Integer> st1 = new TST<Integer>();
		int hasReseted=0;//to check how many times the code has been reseted
		int compressedCode=0;//to check the compressed bits
		int uncompressedCode=0;//uncompressed bits
		double oldRatio=0;//old ratio for the uncompressed file/compressedfile
		double newRatio=0;//new ratio for the uncompressed file/compressedfile
        for (int i = 0; i < R; i++){
			StringBuilder temp=new StringBuilder("" + (char) i); 
			st1.put(/*"" + (char) i*/temp, i);//trying to store the symbol table
		}
        int code = R+1;  // R is codeword for EOF      so the new code book starts with the code part

        while (input.length() > 0) {
            String s = st1.longestPrefixOf(input);  // Find max prefix match s.
            int t = s.length();
			if(code==L&&W<Maxbit){//update the size of the codewords
				incrementW();
				updateL();
			}
			
			uncompressedCode=input.length();//get the uncompressed size
			compressedCode=totalFileSize-uncompressedCode;
			 newRatio=(double)uncompressedCode/compressedCode;//get the new ratio
			if(oldRatio==0){//special case 
			oldRatio=newRatio;
			}
			double oldVSnewRatio=oldRatio/newRatio;//get the comparision ratio
			//System.out.println("oldVSnewRatio is "+oldVSnewRatio);
			if(oldVSnewRatio>1.1){//reset, if the ratio is appropriate
				resetWandL();
				hasReseted++;
			}
			if(hasReseted==1){//return the new codebook and return it to the monitor mode
			    //System.out.println("\n\n\nratio is "+oldVSnewRatio);
				resetWandL();
				break;
			}
            input = input.substring(t);            // Scan past s in input.
			oldRatio=newRatio;//update 
        }
		return input;//return the updated size of the codebook
	}
	
	
	
	public static void MonitorMod(){//the monitor mode
		String input = BinaryStdIn.readString();
		int totalFileSize=input.length();//keep track of the full size
		String prevInput=null;
		String newInput=addCodeBook(input);//to check if the codebook bit will reach 12 bits for the first time 
		input=newInput;
		while (input.length() > 0) {
			prevInput=input;
		  if(W==Maxbit){//reaching the Maxbit, monitor and reset
			newInput=addCodeBookMon(newInput,totalFileSize);//update the input string
			
			//resetWandL();
			newInput=addCodeBook(newInput);
			
			//System.out.println("the updated new length is "+newInput.length());
			input=newInput;	
		  }		  
		}
		resetWandL();//reset W and endcode from now on
		encode(prevInput,3);//reset mode indicator is 3
	}
    public static void expand() {//now i need to learn how to pass the integers
	  int modeSpecifier=0;
	  modeSpecifier=BinaryStdIn.readInt(8);//read the indicator
	  
	  String modeSpecifierST=Integer.toString(modeSpecifier);
	  if(modeSpecifierST.equals("1")){//if indicator is 1, then it is doNothing mode
			System.out.println("using doNothing mode");
		}
		else if(modeSpecifierST.equals("2")){//if indicator is 2, then it using reset mode
			System.out.println("using reset mode");
		}
		else{//else it is using the monitor mode 
			System.out.println("using monitor mode "+modeSpecifier);
		}
	  
		int temp=W;
        String[] st = new String[MaxSize];//largest bits size possibly contains
        int i=0; // next available codeword value
            
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++){
			 st[i] = "" + (char) i;
		}
           
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];
        StringBuilder valBuilder=new StringBuilder(val);    
        while (true) {
			if(i==L){//reaches the limit, reset the bits number
				temp++;
				setWandL(temp);//reset eveything
			}
            BinaryStdOut.write(valBuilder.toString());//print information
			//System.err.println("w is "+W+"codeword is "+BinaryStdIn.readInt(W));
			
            codeword = BinaryStdIn.readInt(W);//have no idea why this is reading from null string
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = valBuilder.append(val.charAt(0)).toString();//val + val.charAt(0);   // special case hack  //this is the part where you add y= x+c
			if(s==null){//if find null exceptions, break
				break;
			}
				if (i < L) st[i++] = valBuilder.append(s.charAt(0)).toString();//val + s.charAt(0); 
             
            valBuilder = new StringBuilder(s);//s;   // update x=y
        }
        BinaryStdOut.close();

    }


    public static void main(String[] args) {//need to learn how to accept n,m and r differently so that we can fit in different mode
        if      (args[0].equals("-"))//a really weird part here, if type print arraytostring args, it will not show me the correct result.....
		{
			if(args[1].equals("r")){//1 represent r in the output file
				resetMod();
			}
			else if(args[1].equals("m")){//2 means m in the output file
				MonitorMod();
			}
			else if(args[1].equals("n")){//3 means n in the output file
				doNothingMod();
			}
			else if(args[1].equals("c")){
				modifiedMode();
			}
			else throw new IllegalArgumentException("Illegal command line argument");
		}
        else if (args[0].equals("+")){ expand();}//for some reason, need to handle the exception like > override the previous LZWmod file, think..... and how to indicate which mode is which
        else throw new IllegalArgumentException("Illegal command line argument");
		
    }
}