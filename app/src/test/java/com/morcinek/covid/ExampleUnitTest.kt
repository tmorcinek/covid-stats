package com.morcinek.covid

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }


    @UseExperimental(ExperimentalStdlibApi::class)
    @Test
    fun addition_foldRight() {
        val list = listOf(1,2,3,4,6,9,17)
        val diffMap = list.mapIndexed { index, item ->
            try {
                item - list[index - 1]
            } catch (e: IndexOutOfBoundsException){
                item
            }
//            when (index){
//                0 -> item
//                else ->
//            }
        }
        assertEquals(listOf(1, 1, 1, 1, 2, 3, 8), diffMap)
    }
}
