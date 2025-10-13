package com.github.purofle.sandauschool.screen

import com.github.purofle.sandauschool.model.RemoteCourse
import org.junit.Assert.assertEquals
import org.junit.Test

class MainScreenSortingTest {

    @Test
    fun `test course sorting by non-zero padded time`() {
        // Create test courses with non-zero padded times
        val courses = listOf(
            createCourse("Course 1", "13:30"),
            createCourse("Course 2", "8:00"),
            createCourse("Course 3", "10:15"),
            createCourse("Course 4", "9:00"),
            createCourse("Course 5", "14:45")
        )

        // Sort using the same logic as MainScreen
        val sorted = courses.sortedBy { course ->
            val parts = course.startTime.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                hours * 60 + minutes
            } else {
                0
            }
        }

        // Verify the sorted order
        assertEquals("8:00", sorted[0].startTime)
        assertEquals("9:00", sorted[1].startTime)
        assertEquals("10:15", sorted[2].startTime)
        assertEquals("13:30", sorted[3].startTime)
        assertEquals("14:45", sorted[4].startTime)
    }

    @Test
    fun `test course sorting with zero-padded times`() {
        // Test with zero-padded times as well
        val courses = listOf(
            createCourse("Course 1", "13:30"),
            createCourse("Course 2", "08:00"),
            createCourse("Course 3", "10:15"),
            createCourse("Course 4", "09:00")
        )

        val sorted = courses.sortedBy { course ->
            val parts = course.startTime.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                hours * 60 + minutes
            } else {
                0
            }
        }

        assertEquals("08:00", sorted[0].startTime)
        assertEquals("09:00", sorted[1].startTime)
        assertEquals("10:15", sorted[2].startTime)
        assertEquals("13:30", sorted[3].startTime)
    }

    @Test
    fun `test course sorting with edge cases`() {
        val courses = listOf(
            createCourse("Course 1", "23:59"),
            createCourse("Course 2", "0:00"),
            createCourse("Course 3", "12:00"),
            createCourse("Course 4", "1:30")
        )

        val sorted = courses.sortedBy { course ->
            val parts = course.startTime.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                hours * 60 + minutes
            } else {
                0
            }
        }

        assertEquals("0:00", sorted[0].startTime)
        assertEquals("1:30", sorted[1].startTime)
        assertEquals("12:00", sorted[2].startTime)
        assertEquals("23:59", sorted[3].startTime)
    }

    @Test
    fun `test empty list`() {
        val courses = emptyList<RemoteCourse>()
        val sorted = courses.sortedBy { course ->
            val parts = course.startTime.split(":")
            if (parts.size == 2) {
                val hours = parts[0].toIntOrNull() ?: 0
                val minutes = parts[1].toIntOrNull() ?: 0
                hours * 60 + minutes
            } else {
                0
            }
        }
        assertEquals(0, sorted.size)
    }

    private fun createCourse(name: String, startTime: String): RemoteCourse {
        return RemoteCourse(
            name = name,
            weekIndexes = listOf(1, 2, 3),
            room = 101,
            teachers = listOf("Teacher"),
            startTime = startTime,
            endTime = "15:00",
            startUnit = 1,
            endUnit = 2,
            weekday = 1
        )
    }
}
