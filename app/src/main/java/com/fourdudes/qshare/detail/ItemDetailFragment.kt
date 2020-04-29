package com.fourdudes.qshare.detail

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item
import java.util.*

private const val LOG_TAG = "448.ItemDetailFrag"
private const val ARG_ITEM_ID = "item_id"

class ItemDetailFragment : Fragment() {

    /**
     * Display item detail with QR code
     * Provide external link to open drive link
     */

    private lateinit var itemDetailViewModel: ItemDetailViewModel

    private lateinit var itemFileName: TextView
    private lateinit var itemDate: TextView
    private lateinit var itemLink: TextView
    // TODO: Var for QR code, formatting

    // Puts itemId on bundle to create view
    companion object {
        fun newInstance(itemId: UUID): ItemDetailFragment {
            val args = Bundle().apply {
                putSerializable(ARG_ITEM_ID, itemId)
            }
            return ItemDetailFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate() called")

        // Get ViewModel
        val factory = ItemDetailViewModelFactory(requireContext())
        itemDetailViewModel =ViewModelProvider(this, factory)
            .get(ItemDetailViewModel::class.java)

        // Load in item
        val itemId: UUID = arguments?.getSerializable(ARG_ITEM_ID) as UUID
        Log.d(LOG_TAG, "Item UUID: $itemId")
        itemDetailViewModel.loadItem(itemId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(LOG_TAG, "onCreateView() called")

        // Inflate detail view, populate later
        val view = inflater.inflate(R.layout.fragment_item_detail, container, false)

        // Link views
        itemFileName = view.findViewById(R.id.file_name) as TextView
        itemDate = view.findViewById(R.id.file_date) as TextView
        itemLink = view.findViewById(R.id.drive_link) as TextView

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Link crime
        itemDetailViewModel.itemLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { item ->
                item?.let {
                    itemFileName.text = item.name
                    itemDate.text = item.date.toString()
                    itemLink.text = item.link
                    Log.d(LOG_TAG, "Name: ${item.name}, Date: ${item.date}, Link: ${item.link}")
                }
            }
        )

        // Link text, QR

        // TODO: QR CODE
    }


}
