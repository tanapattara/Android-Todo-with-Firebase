package th.ac.kku.cis.todoapplcationwithfirebase

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), ItemRowListener {
    lateinit var mDatabase: DatabaseReference

    var toDoItemList: MutableList<Todo>? = null
    lateinit var adapter: TodoItemAdapter
    private var listViewItems: ListView? = null

    var itemListener: ValueEventListener = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // call function
            addDataToList(dataSnapshot)
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, display log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Enable Firebase persistence for offline access
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        //create firebase object
        mDatabase = FirebaseDatabase.getInstance().reference
        listViewItems = findViewById(R.id.list_data)

        toDoItemList = mutableListOf<Todo>()
        adapter = TodoItemAdapter(this, toDoItemList!!)
        listViewItems!!.adapter = adapter
        mDatabase.orderByKey().addListenerForSingleValueEvent(itemListener)

        var btn_new:FloatingActionButton = findViewById(R.id.btn_new)
        btn_new.setOnClickListener { view ->
            addNewItemDialog()
        }
    }

    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()
        // Check if current database contains any collection
        if (items.hasNext()) {
            val toDoListindex = items.next()
            val itemsIterator = toDoListindex.children.iterator()

            // check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                // get current item
                val currentItem = itemsIterator.next()
                val map = currentItem.getValue() as HashMap<String, Any>
                // add data to object
                val todoItem = Todo.create()
                todoItem.objectId = currentItem.key
                todoItem.done = map.get("done") as Boolean?
                todoItem.todoText = map.get("todoText") as String?
                toDoItemList!!.add(todoItem);
            }

            adapter.notifyDataSetChanged()
        }
    }

    //Add new item to DB
    private fun addNewItemDialog() {
        // Create dialog
        val alert = AlertDialog.Builder(this)
        val itemEditText = EditText(this)
        alert.setMessage("Add New Item")
        alert.setTitle("Enter To Do Item Text")
        alert.setView(itemEditText)
        // Set submit button dialog
        alert.setPositiveButton("Submit") { dialog, positiveButton ->
            // create new todoobject
            val todoItem = Todo.create()
            todoItem.todoText = itemEditText.text.toString()
            todoItem.done = false
            // create new record
            val newItem = mDatabase.child("todo_item").push()
            // add new key to todoobject
            todoItem.objectId = newItem.key
            // set todoobject to new record on firebase db
            newItem.setValue(todoItem)
            // close dialog
            dialog.dismiss()
            // display data to user
            Toast.makeText(this,
                "Item saved with ID " + todoItem.objectId, Toast.LENGTH_SHORT).show()

            toDoItemList!!.add(todoItem);
            adapter.notifyDataSetChanged()
        }
        alert.show()
    }

    override fun modifyItemState(itemObjectId: String, index: Int, isDone: Boolean) {
        //get child reference in database via the ObjectID
        val itemReference = mDatabase.child("todo_item").child(itemObjectId)
        //set new value
        itemReference.child("done").setValue(isDone);

        toDoItemList!!.get(index).done = isDone
        adapter.notifyDataSetChanged()
    }

    override fun onItemDelete(itemObjectId: String, index: Int) {
        //get child reference in database via the ObjectID
        val itemReference = mDatabase.child("todo_item").child(itemObjectId)
        //deletion can be done via removeValue() method
        itemReference.removeValue()

        toDoItemList!!.removeAt(index)
        adapter.notifyDataSetChanged()
    }

}