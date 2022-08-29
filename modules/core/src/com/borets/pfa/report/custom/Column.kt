package com.borets.pfa.report.custom

class Column(var type: String,
             var name: String,
             var id: String,
             var order: Int,
             val headerStyleName: String = "activityType",
             val valueStyleName: String = "textValue"
             ) : Comparable<Column> {

    constructor(type: String, name: String, id: String) : this(type, name, id, 1000)

    override fun compareTo(other: Column): Int {
        return compare(this.order, other.order)
    }

    fun compare(x: Int, y: Int): Int {
        return if (x < y) -1 else if (x == y) 0 else 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Column

        if (type != other.type) return false
        if (name != other.name) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    override fun toString(): String {
        return "Column(type='$type', name='$name', id='$id', order=$order)"
    }

}