package com.fourdudes.qshare.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item
import java.util.*

class ItemListFragment : Fragment() {
    private val LOG_TAG = "4dudes.ItemListFragment"

    interface Callbacks {
        fun onItemSelected(itemId: UUID)
    }

    private var callbacks: Callbacks? = null

    private lateinit var itemListViewModel: ItemListViewModel
    private lateinit var itemListRecyclerView: RecyclerView
    private lateinit var itemListAdapter: ItemListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.i(LOG_TAG, "onAttach() called")

        // Attach callback for item selection
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        // Get ViewModel
        val factory = ItemListViewModelFactory(requireContext())
        itemListViewModel = ViewModelProvider(this, factory)
            .get(ItemListViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(LOG_TAG, "onCreateView() called")

        // Inflate list view
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Hook up recycler, layout manager
        itemListRecyclerView = view.findViewById(R.id.item_recycler_view) as RecyclerView
        itemListRecyclerView.layoutManager = LinearLayoutManager(context)

        updateUI(emptyList())

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "onViewCreated() called")
        super.onViewCreated(view, savedInstanceState)

        // Observe LiveData and display
        itemListViewModel.itemLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { items ->
                items?.let {
                    updateUI(items)
                }
            }
        )

        // TODO: Remove testing item
        val item = Item()
        item.description = "new file"
        item.name = "File"
        item.link = "www.google.com/p3ni5L0l"
        itemListViewModel.addItem(item)
    }

    private fun updateUI(items: List<Item>) {
        // If item selected, callback
        itemListAdapter = ItemListAdapter(items) { item: Item -> Unit
            callbacks?.onItemSelected(item.id)
        }
        itemListRecyclerView.adapter = itemListAdapter
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

        // Detach callback
        callbacks = null
    }

}