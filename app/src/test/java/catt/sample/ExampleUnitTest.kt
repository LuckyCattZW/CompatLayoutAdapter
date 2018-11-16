package catt.sample

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        var x = 2048
//        var y = 1536
//        var x = 1920
//        var y = 1200
        var x = 1920
        var y = 1080

        val c = calculationScreenScale(x, y)
        println("${c[0]}:${c[1]}")

    }

    fun calculationScreenScale(w:Int, h:Int):IntArray{
        val o = intArrayOf(w, h)
        o.sortDescending()
        val conventions:Int = maxNumberConventions(o[0], o[1])
        println("conventions=$conventions")
        for(index in o.indices){
            o[index] /= conventions
        }
        return o
    }

    fun maxNumberConventions(m: Int, n: Int): Int {
        var m = m
        var n = n
        while (true) {
            when(m % n == 0){
                true -> return n
                else -> m %= n
            }
            when(n % m == 0){
                true -> return m
                else -> n %= m
            }
        }
    }
}
