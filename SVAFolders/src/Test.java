import java.util.*;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> ls = new ArrayList();
		
		int a = 0;
		String[] words = new String[]{"1","2","3","4","5","6"};
        String word = words[5].toLowerCase();
        System.out.print(word);
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter number:");
		int b = sc.nextInt();
		for(int i=2; i<=b-1; i++){
			
			if(i%5==0 || i%7==0){
			}else{
				System.out.println("i:" + i);
				System.out.println("a:" + a);
				a += i;
				System.out.println("aa:" + a);
			}
			
		}
		a = a + 1 + b;
		System.out.println("Sum:" + a);
	}

}
