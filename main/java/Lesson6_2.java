package main.java;/* 2. Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку, иначе в методе
 необходимо выбросить RuntimeException. Написать набор тестов для этого метода (по 3-4 варианта входных данных).
Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
 */

import java.util.logging.Level;

public class Lesson6_2 {

    public int[] check_Array(int[] array){
        int[] check_Array;
        int find_for=-1;

        for (int i = array.length-1; i >0 ; i--) {
            if(array[i]==4){
                System.out.println("Есть 4");
                find_for=i+1;
                break;
            }
        }
        if (find_for==-1){
            throw new RuntimeException("4 is not!");
        }
        check_Array=new int[array.length-find_for];
        int dx=0;
        for (int i = find_for; i <array.length ; i++) {
            check_Array[dx]=array[i];
            dx++;
        }
        return check_Array;
    }


    public static void main(String[] args) {
        Lesson6_2 lesson6_2=new Lesson6_2();
        int[] array=new int[]{10,15,5,4,8,6};
        int [] result=lesson6_2.check_Array(array);
        System.out.print(array+"  "+result);
    }

}
