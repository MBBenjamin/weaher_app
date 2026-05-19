package com.weather.utils

/**
 * Resultado genérico de operações de dados no app.
 *
 * Nomeado `AppResult` — não `Result<T>` — para evitar conflito de nome com
 * `kotlin.Result` do stdlib e tornar o uso inequívoco em todos os contextos.
 */
sealed class AppResult<out T> {

    /** Operação concluída com sucesso. */
    data class Success<T>(val data: T) : AppResult<T>()

    /** Operação falhou. [exception] é opcional para facilitar testes. */
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : AppResult<Nothing>()

    /** Operação em andamento. */
    data object Loading : AppResult<Nothing>()
}

/** Retorna `true` se o resultado for [AppResult.Success]. */
val <T> AppResult<T>.isSuccess: Boolean get() = this is AppResult.Success

/** Retorna o dado ou `null` se não for [AppResult.Success]. */
fun <T> AppResult<T>.getOrNull(): T? = (this as? AppResult.Success)?.data

/** Executa [block] apenas quando o resultado for [AppResult.Success]. */
inline fun <T> AppResult<T>.onSuccess(block: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) block(data)
    return this
}

/** Executa [block] apenas quando o resultado for [AppResult.Error]. */
inline fun <T> AppResult<T>.onError(block: (AppResult.Error) -> Unit): AppResult<T> {
    if (this is AppResult.Error) block(this)
    return this
}
