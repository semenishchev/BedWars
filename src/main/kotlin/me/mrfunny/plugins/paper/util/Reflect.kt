package me.mrfunny.plugins.paper.util

import java.lang.reflect.Field

object Reflect {
    fun resetField(target: Any, fieldName: String) {
        val field = target.javaClass.getDeclaredField(fieldName)

        with (field) {
            isAccessible = true
            set(target, null)
        }
    }

    fun resetField(target: Any, field: Field) {

        with (field) {
            isAccessible = true
            set(target, null)
        }
    }
}