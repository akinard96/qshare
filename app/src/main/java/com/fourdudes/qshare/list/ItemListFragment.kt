package com.fourdudes.qshare.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R
import com.fourdudes.qshare.ViewCode.ViewCode
import com.fourdudes.qshare.data.Item
import java.util.*

class ItemListFragment : Fragment() {
//    interface Callbacks {
//        fun onItemSelected(itemId: UUID){
//
//        }
//    }

//    private var callbacks: Callbacks? = null
    private val LOG_TAG = "4dudes.ItemListFragment"

    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var itemRecyclerView: RecyclerView
    private lateinit var adapter: ItemListAdapter

    override fun onAttach(context: Context){
        super.onAttach(context)
        Log.d(LOG_TAG, "onAttach() called")
//        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        val factory = ItemListViewModelFactory()
        itemListViewModel = ViewModelProvider(this, factory)
            .get(ItemListViewModel::class.java)
    }


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        Log.d(LOG_TAG, "onCreateOptionsMenu called")
//        inflater.inflate(R.menu.fragment_crime_list, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Log.d(LOG_TAG, "onOptionsItemSelected called")
//        return when(item.itemId) {
//            R.id.new_crime_menu_item -> {
//                val crime = Crime()
//                crimeListViewModel.addCrime(crime)
//                callbacks?.onCrimeSelected(crime.id)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")
//        return super.onCreateView(inflater, container, savedInstanceState)
        //TODO switch view
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        itemRecyclerView = view.findViewById(R.id.item_recycler_view) as RecyclerView
        itemRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(emptyList())

        return view
    }

    private fun updateUI(crimes: List<Item>) {
    val items = itemListViewModel.items
        adapter = ItemListAdapter(items) {item: Item -> Unit
            val intent = Intent(context, ViewCode::class.java)
            startActivity(intent)
        }
        itemRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onActivityCreated() called")
        super.onActivityCreated(savedInstanceState)
    }

    override fun onStart() {
        Log.d(LOG_TAG, "onStart() called")
        super.onStart()
    }

    override fun onResume() {
        Log.d(LOG_TAG, "onResume() called")
        super.onResume()
    }

    override fun onPause() {
        Log.d(LOG_TAG, "onPause() called")
        super.onPause()
    }

    override fun onStop() {
        Log.d(LOG_TAG, "onStop() called")
        super.onStop()
    }

    override fun onDestroyView() {
        Log.d(LOG_TAG, "onDestroyView() called")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy() called")
        super.onDestroy()
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(LOG_TAG, "onDetach() called")
//        callbacks = null
    }

}