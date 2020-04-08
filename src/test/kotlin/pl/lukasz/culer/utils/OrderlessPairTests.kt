package pl.lukasz.culer.utils

import org.junit.Assert
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OrderlessPairTests {
    @Test
    fun containsTest(){
        val a = Any()
        val b = Any()
        val c = Any()

        val first = OrderlessPair(a,b)

        assertTrue(first.contains(a))
        assertTrue(first.contains(b))
        assertFalse(first.contains(c))

        val second = OrderlessPair(b,a)

        assertTrue(second.contains(a))
        assertTrue(second.contains(b))
        assertFalse(second.contains(c))
    }

    @Test
    fun equalsTest(){
        val a = Any()
        val b = Any()
        val c = Any()

        val first = OrderlessPair(a,b)
        val second = OrderlessPair(b,a)
        val third = OrderlessPair(a,c)

        assertTrue(first == second)
        assertFalse(first == third)
    }

    @Test
    fun hashCodeTest(){
        val a = Any()
        val b = Any()
        val c = Any()

        val first = OrderlessPair(a,b)
        val second = OrderlessPair(b,a)
        val third = OrderlessPair(a,c)

        assertTrue(first.hashCode() == second.hashCode())
        assertFalse(first.hashCode() == third.hashCode())
    }
}