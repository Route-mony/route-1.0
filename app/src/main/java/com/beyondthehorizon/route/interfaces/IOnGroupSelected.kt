package com.beyondthehorizon.route.interfaces

import com.beyondthehorizon.route.models.SavedGroupItem

interface IOnGroupSelected {
    fun onGroupSelectedListener(group: SavedGroupItem)
}