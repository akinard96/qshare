package com.fourdudes.qshare.list

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.TextPaint
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R

class GestureControlHelper(private val adapter: ItemListAdapter) : ItemTouchHelper.Callback() {

    interface ItemTouchHelperAdapter {
        fun onItemDismiss(position: Int)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int, isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val context = recyclerView.context

        // make background red
        val background = ColorDrawable(Color.RED)
        background.setBounds(0, viewHolder.itemView.top, (viewHolder.itemView.left + dX).toInt(), viewHolder.itemView.bottom)
        background.draw(c)

        // add delete icon
        val horizontalMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16.0f, context.resources.displayMetrics).toInt()
        val icon = ContextCompat.getDrawable(context, R.drawable.ic_menu_delete)
        val iconSize = icon?.intrinsicHeight ?: viewHolder.itemView.height / 2
        val left = viewHolder.itemView.left + horizontalMargin
        if( dX > horizontalMargin ) {
            val top = viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - (iconSize / 2)
            icon?.setBounds(
                left,
                top,
                left + icon.intrinsicWidth,
                top + icon.intrinsicHeight
            )
            icon?.draw(c)
        }

        if( dX > horizontalMargin + iconSize ) {
            val textPaint = TextPaint()
            textPaint.isAntiAlias = true
            textPaint.color = Color.WHITE
            textPaint.typeface = Typeface.SANS_SERIF
            textPaint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14.0f, context.resources.displayMetrics)
            c.drawText(context.resources.getString(R.string.delete_item_label),
                (left + (icon?.intrinsicWidth ?: horizontalMargin) + horizontalMargin).toFloat(),
                (viewHolder.itemView.top + (viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 + textPaint.textSize / 2),
                textPaint
            )
        }
    }
}