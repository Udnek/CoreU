package me.udnek.coreu.resourcepack.misc

import me.udnek.coreu.util.LogUtils


class Error(val message: String) {

    infix fun at(next: Error): Error{
        return at(next.message)
    }

    infix fun at(next: String): Error{
        return Error("${message}: $next")
    }

    override fun toString(): String {
        return "error: $message"
    }

    fun logError(){
        LogUtils.coreuError(this.toString())
    }
}

infix fun String.at(other: Error): Error {
    return Error(this) at other
}