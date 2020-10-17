package nl.sajansen.kotlinsnakegame.objects

import nl.sajansen.kotlinsnakegame.mocks.BoardMock
import nl.sajansen.kotlinsnakegame.objects.game.Game
import java.awt.Dimension
import java.awt.Point
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class UtilsTest {

    @BeforeTest
    fun before() {
        Game.board = BoardMock()
    }

    @Test
    fun testAdjustPositionForWallX0() {
        val position = Point(5, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(5, 0), position)
    }

    @Test
    fun testAdjustPositionForWallX1() {
        val position = Point(0, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, 0), position)
    }

    @Test
    fun testAdjustPositionForWallX2() {
        val position = Point(-1, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(-1, 0), position)
    }

    @Test
    fun testAdjustPositionForWallX3() {
        val position = Point(-5, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(-5, 0), position)
    }

    @Test
    fun testAdjustPositionForWallX4() {
        val position = Point(-6, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(Game.board.size.width - 5, 0), position)
    }

    @Test
    fun testAdjustPositionForWallX5() {
        val position = Point(-10, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(Game.board.size.width - 5, 0), position)
    }


    @Test
    fun testAdjustPositionForWallY0() {
        val position = Point(0, 5)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, 5), position)
    }

    @Test
    fun testAdjustPositionForWallY1() {
        val position = Point(0, 0)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, 0), position)
    }

    @Test
    fun testAdjustPositionForWallY2() {
        val position = Point(0, -1)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, -1), position)
    }

    @Test
    fun testAdjustPositionForWallY3() {
        val position = Point(0, -5)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, -5), position)
    }

    @Test
    fun testAdjustPositionForWallY4() {
        val position = Point(0, -6)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, Game.board.size.height - 5), position)
    }

    @Test
    fun testAdjustPositionForWallY5() {
        val position = Point(0, -10)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, Game.board.size.height - 5), position)
    }

    @Test
    fun testAdjustPositionForWallY6() {
        val position = Point(0, Game.board.size.height - 5)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, Game.board.size.height - 5), position)
    }

    @Test
    fun testAdjustPositionForWallY7() {
        val position = Point(0, Game.board.size.height - 4)
        adjustPositionForWall(position, Dimension(10, 10))
        assertEquals(Point(0, -5), position)
    }

    @Test
    fun testAdjustPositionForWallYWithMarginFactor0() {
        val position = Point(0, -10)
        adjustPositionForWall(position, Dimension(10, 10), sizeMarginFactor = 1.0)
        assertEquals(Point(0, -10), position)
    }

    @Test
    fun testAdjustPositionForWallYWithMarginFactor1() {
        val position = Point(0, -11)
        adjustPositionForWall(position, Dimension(10, 10), sizeMarginFactor = 1.0)
        assertEquals(Point(0, Game.board.size.height), position)
    }

}