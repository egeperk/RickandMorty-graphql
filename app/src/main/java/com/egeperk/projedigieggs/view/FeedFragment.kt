package com.egeperk.projedigieggs.view

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.egeperk.projedigieggs.*
import com.egeperk.projedigieggs.adapter.CharAdapter
import com.egeperk.projedigieggs.databinding.DialogOptionsBinding
import com.egeperk.projedigieggs.databinding.FragmentMainBinding
import kotlinx.coroutines.channels.Channel


class FeedFragment : Fragment() {

    private var dialogBinding: DialogOptionsBinding? = null
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog
    private val TAG = "MainFragment"
    private var characters = mutableListOf<CharactersQuery.Result>()
    private val adapter = CharAdapter(characters)
    private val channel = Channel<Unit>(Channel.CONFLATED)
    private var lastAppliedQuery = ""
    private var page: Int? = 0
    var currentQuery = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter


        channel.trySend(Unit)
        adapter.onEndOfListReached = {
            channel.trySend(Unit)
        }

        super.onViewCreated(view, savedInstanceState)

        getCharacters()

        binding.filterBtn.setOnClickListener(View.OnClickListener {
            createPopup()
        })
    }


    private fun getCharacters() {

        lifecycleScope.launchWhenResumed {

            for (item in channel) {

                val response = try {

                    apolloClient.query(
                        CharactersQuery(
                            Optional.Present(page), Optional.Present(currentQuery)
                        )
                    ).execute()

                } catch (e: ApolloException) {
                    Log.d(TAG, "onViewCreated: Error ", e)
                    return@launchWhenResumed
                }


                val newCharacters = response.data?.characters?.results?.filterNotNull()

                if (currentQuery != lastAppliedQuery) {
                    characters.clear()

                }

                if (newCharacters != null) {
                    characters.addAll(newCharacters)
                }


                lastAppliedQuery = currentQuery

                adapter.notifyDataSetChanged()


                page = response.data?.characters?.info?.next

            }

            adapter.onEndOfListReached = null
            channel.close()
        }
    }

    private fun createPopup() {

        dialogBuilder = AlertDialog.Builder(context)
        val layoutInflater = LayoutInflater.from(context)
        dialogBinding = DialogOptionsBinding.inflate(layoutInflater)
        dialogBuilder.setView(dialogBinding!!.root)
        dialog = dialogBuilder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()


        dialogBinding?.btnOption1?.setOnClickListener {
            dialogBinding?.btnOption2?.setImageResource(R.drawable.ellipse1)
            dialogBinding?.btnOption3?.setImageResource(R.drawable.ellipse1)
            dialogBinding?.btnOption1?.setImageResource(R.drawable.groupellipse1)

            currentQuery = "Rick"
            if (currentQuery != lastAppliedQuery) {
                page = 0
            }
            channel.trySend(Unit)

        }
        dialogBinding?.btnOption2?.setOnClickListener {
            dialogBinding?.btnOption1?.setImageResource(R.drawable.ellipse1)
            dialogBinding?.btnOption3?.setImageResource(R.drawable.ellipse1)
            dialogBinding?.btnOption2?.setImageResource(R.drawable.groupellipse1)

            currentQuery = "Morty"
            if (currentQuery != lastAppliedQuery) {
                page = 0
            }
            channel.trySend(Unit)

        }

        dialogBinding?.btnOption3?.setOnClickListener {
            dialogBinding?.btnOption1?.setImageResource(R.drawable.ellipse1)
            dialogBinding?.btnOption2?.setImageResource(R.drawable.ellipse1)
            dialogBinding?.btnOption3?.setImageResource(R.drawable.groupellipse1)


            currentQuery = ""
            if (currentQuery != lastAppliedQuery) {
                page = 0
            }
            channel.trySend(Unit)

        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}