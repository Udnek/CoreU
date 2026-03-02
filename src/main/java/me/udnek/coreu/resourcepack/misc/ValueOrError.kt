package me.udnek.coreu.resourcepack.misc

class ValueOrError<out T>{

    val value: T?
    val error: Error?

    private constructor(value: T?, error: Error?){
        this.value = value
        this.error = error
    }

    companion object{
        fun <T> failure(error: Error): ValueOrError<T>{
            return ValueOrError(null, error)
        }

        fun <T> failure(throwable: Throwable): ValueOrError<T>{
            return failure("${throwable.javaClass}(${throwable.message}): ${throwable.stackTraceToString()}")
        }

        fun <T> failure(error: String): ValueOrError<T>{
            return failure(Error(error))
        }

        fun <T> failure(parent: String, sub: Error): ValueOrError<T>{
            return failure(Error(parent, sub))
        }

        fun <T> success(value: T): ValueOrError<T> {
            return ValueOrError(value, null)
        }
    }

    operator fun component1(): T? = value

    operator fun component2(): Error? = error
}