package catt.sample;

import org.junit.Test;

public class ExampleUnitTestJava {
    @Test
    public void main() {
//        int x = 2048;
//        int y = 1536;
//        int x = 1920;
//        int y = 1080;
//        int x = 1920;
//        int y = 1200;

        int x = 1920;
        int y = 1080;

        System.out.println("ZZZZ  " + y % x);
        System.out.println("ZZZZ  " + x % y);

        int c = gcd(x, y);
        System.out.println(c);
        System.out.println((x / c) + ":" + y / c);
    }

    public static int gcd(int m, int n) {
        while (true) {
            if (m % n == 0) {
                System.out.println(m);
                return n;
            } else {
                m %= n;
                System.out.println("A " + m);
            }
            if (n % m == 0) {
                System.out.println(n);
                return m;
            } else {
                n %= m;
                System.out.println("B " + n);
            }
        }
    }
}
