/* 3. Написать метод, который проверяет состав массива из чисел 1 и 4. Если в нем нет хоть одной четверки или единицы,
 то метод вернет false; Написать набор тестов для этого метода (по 3-4 варианта входных данных).
 */
package main.java;

public class Lesson6_3 {

    public boolean check_Array(int[] array){
        boolean result=true;
        int countOne=0;
        int countFor=0;

        for (int element:array) {
            if (element==1 ){
                countOne++;
            }
            else if( element==4){
                countFor++;
            }
          }
        if(countOne==0 || countFor==0 || (countFor+countOne)!=array.length){
            result=false;
        }

        return result;

    }
    public static void main(String[] args) {
        int [] a=new int[]{ 1, 1, 1, 4, 4, 1, 4, 3 };
        Lesson6_3  lesson6_3=new Lesson6_3();
        System.out.println(lesson6_3.check_Array(a));
//                [ 1 1 1 1 1 1 ] -> false
//                [ 4 4 4 4 ] -> false
//                [ 1 4 4 1 1 4 3 ] -> false
    }
}
