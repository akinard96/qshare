package com.fourdudes.qshare.detail

import android.content.Intent
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_scan.*
import java.util.*

private const val LOG_TAG = "448.ItemDetailFrag"
private const val ARG_ITEM_ID = "item_id"
private const val ARG_NEW_FILE_BOOL = "new_file"

class ItemDetailFragment : Fragment() {

    private lateinit var itemDetailViewModel: ItemDetailViewModel

    private lateinit var itemFileName: TextView
    private lateinit var itemDate: TextView
    private lateinit var itemLink: TextView
    private lateinit var shareButton: Button
    private lateinit var qrCode: ImageView
    private var link: String = ""

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
        qrCode = view.findViewById(R.id.qr_code) as ImageView
        shareButton = view.findViewById(R.id.share_button) as Button
        shareButton.setOnClickListener { shareFile() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Link item
        itemDetailViewModel.itemLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { item ->
                item?.let {
                    // Have to assign vals here, it wouldn't let me set a local variable to LiveData item
                    itemFileName.text = item.name
                    itemDate.text = item.date.toString()
                    itemLink.text = item.link
                    link = item.link
                    generateQR(item.link)
                    Log.d(LOG_TAG, "Name: ${item.name}, Date: ${item.date}, Link: ${item.link}")
                }
            }
        )
    }

    private fun generateQR(link: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(link, BarcodeFormat.QR_CODE,200,200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            qrCode.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    // TODO: Dialog box that will take user to link if new
    private fun openNewFileDialog(link: String) {

    }

    private fun shareFile() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        val message = "Click this link to view a document in Google Drive: "

        sharingIntent.apply {
            type = "text/plain"
            putExtra(EXTRA_SUBJECT, message)
            putExtra(EXTRA_TEXT, link)
        }

        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

}
