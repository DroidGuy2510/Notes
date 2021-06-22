package com.perapps.notes.other

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchedData: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline onFetchFailed: (Throwable) -> Unit = { }
) = flow {
    emit(Resource.loading(null))

    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.loading(data))

        try {
            val fetchedResult = fetch()
            saveFetchedData(fetchedResult)
            query().map { Resource.success(it) }
        } catch (t: Throwable) {
            onFetchFailed(t)
            query().map {
                Resource.error("Could`nt reach Server", it)
            }
        }
    } else {
        query().map { Resource.success(it) }
    }
    emitAll(flow)
}