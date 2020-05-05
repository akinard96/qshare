package com.fourdudes.qshare.list

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item
import java.util.*

private const val LOG_TAG = "4dudes.ItemListFragment"
private const val KEY_QR_CODE = "qr_code"
private const val KEY_NEW_FILE = "new_file"
private const val KEY_FILE_NAME = "file_name"
private const val KEY_FILE_DESC = "file_desc"

class ItemListFragment : Fragment() {


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

        // Check for new QR code or new file
        val qrLink: String? = arguments?.getString(KEY_QR_CODE)
        val newFileLink: String? = arguments?.getString(KEY_NEW_FILE)

        if (qrLink != null) {
            Log.d("448.ScanActivity", "QR code link received (ItemListFrag): $qrLink")
            // Create and send to detail view
            var item = Item()
            item.link = qrLink
            item.name = "Received File"
            item.description = "Scanned from QR code"
            item.isSent = true
            itemListViewModel.addItem(item)
            callbacks?.onItemSelected(item.id)
        }
        else if (newFileLink != null) {
            Log.d("448.ScanActivity", "New File Upload link received (ItemListFrag): $newFileLink")
            // Get name/desc
            val name: String = arguments?.getString(KEY_FILE_NAME) ?: "Unknown File Name"
            val desc: String = arguments?.getString(KEY_FILE_DESC) ?: "Unknown file type"
            // Create and send to detail view
            var item = Item()
            item.link = newFileLink
            item.name = name
            item.description = "File Type: $desc"
            item.isSent = false
            itemListViewModel.addItem(item)
            callbacks?.onItemSelected(item.id)
        }
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

                    val itemTouchHelperCallback = GestureControlHelper(itemListAdapter)
                    val touchHelper = ItemTouchHelper(itemTouchHelperCallback)
                    touchHelper.attachToRecyclerView(itemListRecyclerView)
                }
            }
        )
    }

    private fun updateUI(items: List<Item>) {
        // If item selected, callback
        itemListAdapter = ItemListAdapter(items, itemListViewModel) { item: Item -> Unit
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