package com.github.callmephil1.parsecs.ext

object SystemExt {
    fun Long.nanoToMilli(): Float {
        return this / 1_000_000f
    }
    fun Long.nanoToSeconds(): Float {
        return this / 1_000_000_000f
    }
    fun Float.nanoToSeconds(): Float {
        return this / 1_000_000_000f
    }
    fun Float.secondsToMilli(): Float {
        return this * 1_000
    }
}