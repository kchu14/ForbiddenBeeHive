
public class algo {
  public static void main(String[] args) {
    
   int[] a = new int[]{ 1,4,5,3,2 }; 
    countsort(a);

  }
  static int[] countsort(int[] a) {
    int[] result = new int[a.length];
    boolean[] bool = new boolean[a.length * 2 + 1];
    
    for (int i = 0; i < a.length; i++) {
      bool[a[i]] = true;
    }
    int r = 0;
    for (int j = 0; j < a.length * 2 + 1; j++) {
      if (bool[j] ) {
        result[r] = j;
        r++;
      }
    }
    for (int k=0; k < result.length ; k++)
      System.out.print(result[k] + " ");
    System.out.println();
    System.out.print(result.length + "");
    System.out.println();
    System.out.print(a[0]);
    return result;
  }
}
