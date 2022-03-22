package th.ac.kku.cis.todoapplcationwithfirebase

class Todo {
    companion object Factory {
        fun create(): Todo = Todo()
    }

    var objectId: String? = null
    var todoText: String? = null
    var done: Boolean? = false
}