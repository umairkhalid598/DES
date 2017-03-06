/**
 * 
 */
package DEs_algorithm;



import java.math.BigInteger;
import java.util.Scanner;

/**
 * @author Rao Umair Khalid
 *
 */

class key_generation{
	private String data;
	private byte[] bin64 = new byte[64];				//64bit binary data
	private byte[] bin56 = new byte[56];				//56bit binary data
	private byte[] bin48 = new byte[48];
	private String[] key = new String[16];
	private int[] parity = {
			57, 49, 41, 33, 25, 17, 9,
	        1,  58, 50, 42, 34, 26, 18,
	        10, 2,  59, 51, 43, 35, 27,
	        19, 11, 3,  60, 52, 44, 36,
	        63, 55, 47, 39, 31, 23, 15,
	        7,  62, 54, 46, 38, 30, 22,
	        14, 6,  61, 53, 45, 37, 29,
	        21, 13, 5,  28, 20, 12, 4
	        };
	private int[] com_p_box = { 
			14, 17, 11, 24, 1,  5,
	        3,  28, 15, 6,  21, 10,
	        23, 19, 12, 4,  26, 8,
	        16, 7,  27, 20, 13, 2,
	        41, 52, 31, 37, 47, 55,
	        30, 40, 51, 45, 33, 48,
	        44, 49, 39, 56, 34, 53,
	        46, 42, 50, 36, 29, 32
	        };
	
	//getting key from the user
	public void set_key(){
		Scanner in = new Scanner(System.in);
		System.out.print("Enter key in hexadecimal form: ");
		data = in.nextLine();
	}
	
	public String addingzeros(String bin, int req_len){
		String d = "0";
		for (int i = 1; i < (req_len - bin.length()); i++){
			d = "0".concat(d);
		}
		bin = d.concat(bin);
		return bin;
	}
	
	//Converting to binary
	public byte[] deci_binary(String data, byte[] bin64, int len){
		try{
			
			String binary;
			BigInteger bigint = new BigInteger(data, 16);
			binary = bigint.toString(2);
			
			//making 64 bit if less than 64
			if (binary.length() < len){
				binary = addingzeros(binary, len);
			}
			
			
	        bin64 = binary.getBytes();
	        for(int i1 =0; i1 < len; i1++){
	        	bin64[i1] = (byte) (bin64[i1]-48);
	        }
		}
		catch (NumberFormatException e) {
			// TODO: handle exception
			System.out.println("Error: Data must be in hexa and of 16 in length. "+e.getMessage());
			System.exit(1);
		}
		return bin64;
	}
	
	//droping paritybits
	public void parity_drop(){
		for(int i = 0; i < 56; i++){
			bin56[i] = bin64[parity[i]-1];
		}
	}
	
	
	//Rotating one bits to left
	public void rotate_left(int start, int end){
		byte temp;
		temp = bin56[start];
		for (int i = start; i < end; i++){
			bin56[i] = bin56[i+1];
		}
		bin56[end] = temp;
	}
	
	//Compression P Box
	public void p_box(){
		for(int i = 0; i < 48; i++){
			bin48[i] = bin56[com_p_box[i]-1];
		}
	}
	
	public String binary_to_hex(byte[] bin48,String key, int len_data, int len_key){
		//Converting binary to hex
		for(int i =0; i < len_data; i++){
        	bin48[i] = (byte) (bin48[i]+48);
        }
		key = new String(bin48);
		BigInteger bigint = new BigInteger(key, 2);
		key = bigint.toString(16);
		
		//adding zero in the start of hex key
		if(key.length() < len_key){
			key = addingzeros(key, len_key);
		}
		return key;
	}
	
	//One Complete round
	void round(int round, int end){
		
		//calling rotate_left function for left rotation
		for(int i = 0; i< end; i++){
			rotate_left(0, 27);
			rotate_left(28, 55);
		}
		p_box();						//compression p box takes 56 bit key and return 48 bit key
		
		key[round] = binary_to_hex(bin48, key[round], 48, 12);
	}
	
	//generating key
	public String[] key_generator(){
		set_key();							//taking key from user in the form of String
		bin64 = deci_binary(data, bin64, 64);						//Converting hex to binary
		parity_drop();						//Take 64 bit key and return 56 bit key by dropping parity bits
		
		//Generating all 16 key round by roud
		for(int i = 0; i < 16; i++){
			if(i == 0 || i == 1 || i == 8 || i == 15){
				round(i,1);
			}
			else{
				round(i,2);
			}
		}
		//Showing all 16 keys generated
		/*for(int i =0; i < 16; i++){
			System.out.println(key[i]);
		}*/
		return key;
	}
}

public class DES {
	
	private String[] key = new String[16];
	private String plain_text, cipher;
	private byte[] bin64 = new byte[64];
	private byte[] bin48 = new byte[48];
	private byte[] bin32 = new byte[32];
	private byte[] left_bin32 = new byte[32];
	private byte[] right_bin32 = new byte[32];
	private key_generation key_obj = new key_generation();
	private int[] in_permut_array = {
			58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
	};
	private int[] f_permut_array = {
			40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
	};
	private int[] expansion_p_array = {
			32, 1, 2, 3, 4, 5,
            4, 5, 6, 7, 8, 9,
            8, 9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32, 1
	};
	private int[][] s_array = {{
			14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
    }, {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
    }, {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
    }, {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
    }, {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
    }, {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
    }, {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
    }, {
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
    }};
	
	private int[] straight_p_array = {
			16, 7, 20, 21,
            29, 12, 28, 17,
            1, 15, 23, 26,
            5, 18, 31, 10,
            2, 8, 24, 14,
            32, 27, 3, 9,
            19, 13, 30, 6,
            22, 11, 4, 25
	};
	
	public DES() {
		// TODO Auto-generated constructor stub
		key = key_obj.key_generator();
		/*for(int i=0; i<16; i++){
			System.out.println(key[i]);
		}*/
	}

	public void set_plain_text(){
		Scanner in = new Scanner(System.in);
		System.out.print("Enter the plain_text: ");
		plain_text = in.nextLine();
	}
	
	public byte[] copy(byte[] temp, byte[] temp2, int len){
		for (int i = 0; i < len; i++){
			temp[i] = temp2[i];
		}
		return temp;
	}
	
	public void permutation(int[] p){
		byte[] temp = new byte[64];
		temp = copy(temp, bin64,64);
		for(int i = 0; i < 64; i++){
			bin64[i] = (byte) temp[p[i]-1];
		}
	}
	
	public void expansion_p_box(){
		
		for(int i = 0; i < 48; i++){
			bin48[i] = (byte) right_bin32[expansion_p_array[i]-1];
		}
	}
	
	public void xor(int round){
		byte[] temp = new byte[48];
		temp = key_obj.deci_binary(key[round], temp, 48);
		for(int i = 0; i<48; i++){
			if(temp[i] == bin48[i]){
				bin48[i] = 0;
			}
			else{
				bin48[i] = 1;
			}
		}
		
	}
	
	public void replace(int row,int col,int start, int end, int s){
		byte[] b = new byte[4];
		int i = Integer.parseInt(String.valueOf(s_array[s][col+(row*16)]));
	    String bin = Integer.toBinaryString(i);
	    if(bin.length()<4){
	    	bin = key_obj.addingzeros(bin, 4);
	    }
	    b = bin.getBytes();
	    for(int l = 0; l<4; l++){
	    	b[l] = (byte) (b[l]-48);
	    }
		for (int j = start; j < end; j++){
			bin32[j] = b[j-start];
		}
	}
	
	public void s_box(){
		int row = 0,col = 0;
		int start = 0, end = 4;
		byte[] b = new byte[2];
		byte[] c = new byte[4];
		int counter = 0, s = 0;
		for(int i = 0; i< 48; i++){
			if((i+1) % 6 == 0){
				b[1] = bin48[i];
				row = Integer.valueOf(key_obj.binary_to_hex(b, "", 2, 4), 16);
				col = Integer.valueOf(key_obj.binary_to_hex(c, "", 4, 4), 16);
				replace(row,col, start, end,s);
				counter = 0;
				start += 4;
				end += 4;
				s++;
				if(i!=47){
					b[0] = bin48[i+1];
					i++;
				}
			}
			else if(i == 0){
				b[0] = bin48[i];	
			}
			else{
				c[counter] = bin48[i];
				counter++;
			}
			
		}
	}
	
	public void straight_p_box(){
		byte[] temp = new byte[32];
		temp = copy(temp, bin32, 32);
		for(int i =0; i < 32; i++){
			bin32[i] = temp[straight_p_array[i]-1];
		}
	}
	
	public void swaper(){
		for (int i = 0; i < 32; i++){
			bin64[i] = right_bin32[i];
		}
		for (int i = 32; i < 64; i++){
			bin64[i] = left_bin32[i-32];
		}
	}
	
	public void combine(){
		for (int i = 0; i < 32; i++){
			bin64[i] = left_bin32[i];
			bin64[i+32] = right_bin32[i];
		}
	}
	
	public void show(int len, byte[] bin64){
		for(int i = 0; i<len; i++){
			System.out.print(bin64[i]);
		}
	}
	
	public void xor_64(){
		
		
		byte[] temp = new byte[32];
		temp = copy(temp, bin32, 32);;
		
		for(int i = 0; i<32; i++){
			if(temp[i] == left_bin32[i]){
				left_bin32[i] = 0;
			}
			else{
				left_bin32[i] = 1;
			}
		}
	}
	
	public byte[] right_copy(byte[] temp, byte[] temp2, int len){
		for (int i = 0; i < len; i++){
			temp[i] = temp2[i+32];
		}
		return temp;
	}
	
	public void encrypt(){
		set_plain_text();
		bin64 = key_obj.deci_binary(plain_text, bin64, 64);
		permutation(in_permut_array);
		for(int i = 0; i < 16; i++){
			left_bin32 = copy(left_bin32, bin64, 32);
			right_bin32 = right_copy(right_bin32, bin64, 32);
			expansion_p_box();
			xor(i);
			s_box();
			straight_p_box();
			xor_64();
			if(i!=15){
				swaper();
			}
			else{
				combine();
			}
		}
		permutation(f_permut_array);
		cipher = key_obj.binary_to_hex(bin64, cipher, 64, 16);
		System.out.println("Encrypted: "+cipher);
	}
	
	public void decrypt(){
		bin64 = key_obj.deci_binary(cipher, bin64, 64);
		permutation(in_permut_array);
		for(int i = 15; i >= 0; i--){
			left_bin32 = copy(left_bin32, bin64, 32);
			right_bin32 = right_copy(right_bin32, bin64, 32);
			expansion_p_box();
			xor(i);
			s_box();
			straight_p_box();
			xor_64();
			if(i!=0){
				swaper();
			}
			else{
				combine();
			}
		}
		permutation(f_permut_array);
		cipher = key_obj.binary_to_hex(bin64, cipher, 64, 16);
		System.out.println("Decrypted: "+cipher);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		DES des_obj = new DES();
		des_obj.encrypt();
		des_obj.decrypt();
		
	}

}
