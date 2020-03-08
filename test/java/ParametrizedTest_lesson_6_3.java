package test.java;

import main.java.Lesson6_2;
import main.java.Lesson6_3;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


    @RunWith(value = Parameterized.class)
    public class ParametrizedTest_lesson_6_3 {

        private static Lesson6_3 lesson6_3 = null;
        private int[] x1;


        private boolean result_lesson6_3;

        public ParametrizedTest_lesson_6_3(int[] x1, boolean result) {
            this.x1 = x1;
            this.result_lesson6_3 = result;
        }


       @Parameterized.Parameters
        public static Collection test() {
            return Arrays.asList(new Object[][]{
                            {new int[]{ 1, 1, 1, 4, 4, 1, 4, 4}, true},
                            {new int[]{1, 1, 1, 1, 1, 1 }, false},
                            {new int[]{4, 4, 4, 4}, false},
                            {new int[]{1, 4, 4, 1, 1, 4, 3 }, false}
                    }
            );
        }


        @Test
        public void arrayTest_lesson6_3() {
            Assert.assertEquals(lesson6_3.check_Array(x1), result_lesson6_3);
        }
        @Before
        public void init() {
            System.out.println("init lesson6_3");
            lesson6_3 = new Lesson6_3();
        }

        @After
        public void tearDown() throws Exception {
            lesson6_3 = null;
        }
    }



