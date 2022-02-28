package com.egeperk.projedigieggs.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.egeperk.projedigieggs.CharactersQuery
import com.egeperk.projedigieggs.databinding.CharRowBinding
import com.egeperk.projedigieggs.adapter.CharAdapter.ViewHolder
import com.squareup.picasso.Picasso

class CharAdapter(private val characters: List<CharactersQuery.Result>) :
    RecyclerView.Adapter<ViewHolder>() {

    var onEndOfListReached: (() -> Unit)? = null

    class ViewHolder(val binding: CharRowBinding) : RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = CharRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val character = characters[position]
        holder.binding.itemRowIdTv.text = character.id ?: ""
        holder.binding.itemRowName.text = character.name ?: ""
        holder.binding.itemRowLocation.text = character.location?.name ?: ""
        Picasso.get().load(character.image).into(holder.binding.itemRowImageView)

     if (position == characters.size - 1) {
            onEndOfListReached?.invoke()
        }


    }
    override fun getItemCount(): Int {
        return characters.size
    }


}

