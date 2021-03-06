package br.eti.rafaelcouto.cryptocap.testhelper.dummy

import androidx.recyclerview.widget.DiffUtil

class DummyDiffCallback<T> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
    override fun areContentsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
