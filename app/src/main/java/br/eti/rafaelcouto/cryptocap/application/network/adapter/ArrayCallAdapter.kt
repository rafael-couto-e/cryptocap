package br.eti.rafaelcouto.cryptocap.application.network.adapter

import br.eti.rafaelcouto.cryptocap.application.network.call.ArrayCall
import br.eti.rafaelcouto.cryptocap.application.network.model.Body
import br.eti.rafaelcouto.cryptocap.application.network.model.Result
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ArrayCallAdapter<T>(
    private val responseType: Type
) : CallAdapter<Body<T>, Call<Result<T>>> {

    override fun responseType(): Type = responseType
    override fun adapt(call: Call<Body<T>>) = ArrayCall(call, responseType)
}
