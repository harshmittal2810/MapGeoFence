package com.harsh.mapgeofence.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Results(
        val bounds: Bounds,
        @SerializedName("geometry")
    @Expose
    val geometry: List<List<GeometryLatLng>>,
        val id: Int,
        val tags: Tags,
        val type: String
)