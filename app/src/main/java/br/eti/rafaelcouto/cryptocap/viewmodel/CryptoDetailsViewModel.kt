package br.eti.rafaelcouto.cryptocap.viewmodel

import androidx.lifecycle.*
import br.eti.rafaelcouto.cryptocap.R
import br.eti.rafaelcouto.cryptocap.application.network.model.Result
import br.eti.rafaelcouto.cryptocap.domain.model.CryptoDetailsUI
import br.eti.rafaelcouto.cryptocap.domain.usecase.abs.CryptoDetailsUseCaseAbs
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class CryptoDetailsViewModel(
    private val useCase: CryptoDetailsUseCaseAbs,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private var id: Long = 0
    private val data = MutableLiveData<Result<CryptoDetailsUI>>()
    private val currentSelection = MutableLiveData(CryptoDetailsUI.Variation.DAY)
    private val favoriteStatus = MutableLiveData<Boolean>()

    val isFavorite = favoriteStatus as LiveData<Boolean>
    val status = Transformations.map(data) { it.status }
    val errorMessage = Transformations.map(data) { it.error }
    val content = Transformations.map(data) { it.data }

    val variation = MediatorLiveData<String>().apply {
        fun updateValue(data: CryptoDetailsUI?, selection: CryptoDetailsUI.Variation?) = data?.let {
            value = when (selection) {
                CryptoDetailsUI.Variation.WEEK -> it.weekVariation
                CryptoDetailsUI.Variation.MONTH -> it.monthVariation
                else -> it.dayVariation
            }
        }

        addSource(data) { updateValue(it.data, currentSelection.value) }
        addSource(currentSelection) { updateValue(data.value?.data, it) }
    }

    val variationColor = Transformations.map(variation) {
        if (it.startsWith("-")) R.color.red500 else R.color.green500
    }

    fun loadData(id: Long, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (data.value != null && !isRefresh) return@launch

            this@CryptoDetailsViewModel.id = id

            data.value = Result.loading()

            useCase.fetchDetails(id).flowOn(dispatcher).collect { data.value = it }
        }

        viewModelScope.launch(dispatcher) {
            favoriteStatus.postValue(useCase.isFavorite(id))
        }
    }

    fun updateSelection(selectionId: Int) {
        CryptoDetailsUI.Variation.values().firstOrNull { it.id == selectionId }?.let { selection ->
            currentSelection.value = selection
        }
    }

    fun handleFavorite() = viewModelScope.launch(dispatcher) {
        favoriteStatus.value?.let {
            if (it) useCase.removeFromFavorites(id) else useCase.saveToFavorites(id)

            favoriteStatus.postValue(!it)
        }
    }
}
