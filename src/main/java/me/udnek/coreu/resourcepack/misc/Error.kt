package me.udnek.coreu.resourcepack.misc

import me.udnek.coreu.util.LogUtils


class Error(val message: String) {

//    constructor(parent: String, sub: Error) : this("${parent}: ${sub.message}")
//    fun joinErr(next: Error): Error{
//        return joinErr(next.message)
//    }
//    fun joinErr(next: String): Error{
//        return Error("${message}: $next")
//    }

    override fun toString(): String {
        return "error: $message"
    }
    fun logError(){
        LogUtils.coreuError(this.toString())
    }
}

infix fun Error.at(other: Error): Error {
    return Error("${this.message}: ${other.message}")
}
infix fun String.at(other: Error): Error {
    return Error(this) at other
}