package catt.sample

import org.junit.Test

import org.junit.Assert.*
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        var sValue : Date? = Date()
//        println("1 sValue.hashCode = ${sValue?.hashCode()}")
//        sValue = sValue ?: Date()
//        println("2 sValue.hashCode = ${sValue?.hashCode()}")
        val c = maxNumberConventions(2048, 1536)

        println("${2048/c} : ${1536/c}")

        val intArrayOf = intArrayOf(1536, 2048)
        intArrayOf.sort()
        intArrayOf.reverse()

        for(index in intArrayOf.indices){
            println(intArrayOf[index])
        }


//        println("${2048 / c} : ${1536 / c}")
    }

    private fun maxNumberConventions(m: Int, n: Int): Int {
        val big = when(m.compareTo(n)){
            0, 1 -> m
            else -> n
        }
        val small = when (m.compareTo(n)) {
            0, 1 -> n
            else -> m
        }
        return big % small
    }

}
