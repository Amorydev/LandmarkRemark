package com.amory.landmarkremark.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.amory.landmarkremark.R
import com.amory.landmarkremark.model.Landmark
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomShowMark(private val inflater: LayoutInflater): GoogleMap.InfoWindowAdapter {

    private fun renderWindowsData(marker: Marker, view: View){
        val title = view.findViewById<TextView>(R.id.txtTitle)
        val description = view.findViewById<TextView>(R.id.txtDescription)
        val email = view.findViewById<TextView>(R.id.txtEmail)

        title.text = marker.title
        description.text = marker.snippet
        email.text = marker.tag.toString()
    }
    override fun getInfoContents(p0: Marker): View? {
        val view = inflater.inflate(R.layout.layout_detail_mark, null)
        renderWindowsData(p0, view)
        return view
    }

    override fun getInfoWindow(p0: Marker): View? {
        val view = inflater.inflate(R.layout.layout_detail_mark, null)
        renderWindowsData(p0, view)
        return view
    }

}