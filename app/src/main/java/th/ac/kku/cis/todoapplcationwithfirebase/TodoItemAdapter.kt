package th.ac.kku.cis.todoapplcationwithfirebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class TodoItemAdapter (context: Context, todoItemList: MutableList<Todo>) : BaseAdapter() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var itemList = todoItemList

    private var rowListener: ItemRowListener = context as ItemRowListener
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(index: Int): Any {
        return itemList.get(index)
    }

    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getView(index: Int, convertView: View?, viewGroup: ViewGroup?): View {
        // create object from view
        val objectId: String = itemList.get(index).objectId as String
        val itemText: String = itemList.get(index).todoText as String
        val done: Boolean = itemList.get(index).done as Boolean
        val view: View
        val vh: ListRowHolder

        // get list view
        if (convertView == null) {
            view = mInflater.inflate(R.layout.list_item, viewGroup, false)
            vh = ListRowHolder(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as ListRowHolder
        }

        // add text to view
        vh.label.text = itemText
        vh.isDone.isChecked = done

        //add button listenner
        vh.isDone.setOnClickListener {
            rowListener.modifyItemState(objectId, index, !done)
        }

        vh.ibDeleteObject.setOnClickListener {
            rowListener.onItemDelete(objectId, index)
        }
        return view
    }

    private class ListRowHolder(row: View?) {
        val label: TextView = row!!.findViewById<TextView>(R.id.tv_text) as TextView
        val isDone: CheckBox = row!!.findViewById<CheckBox>(R.id.cb_item) as CheckBox
        val ibDeleteObject: Button = row!!.findViewById<ImageButton>(R.id.button_delete) as Button
    }
}