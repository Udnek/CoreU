package me.udnek.coreu.resourcepack.misc

import me.udnek.coreu.util.LogUtils


class Error(val message: String) {

    constructor(parent: String, sub: Error) : this("${parent}: ${sub.message}")

    fun join(next: Error): Error{
        return join(next.message)
    }
    fun join(next: String): Error{
        return Error("${message}: $next")
    }

    override fun toString(): String {
        return "error: $message"
    }
    fun logError(){
        LogUtils.pluginWarning(this.toString())
    }
    
    operator fun plus(other: Error): Error{
        return this.join(other)
    }
    operator fun plus(other: String): Error{
        return this.join(other)
    }
}