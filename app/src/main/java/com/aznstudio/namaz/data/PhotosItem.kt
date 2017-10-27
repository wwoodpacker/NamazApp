package com.aznstudio.namaz.data

import com.google.gson.annotations.SerializedName

data class PhotosItem(
		@SerializedName("photo_reference")
		val photoReference: String? = null,
		@SerializedName("width")
		val width: Int? = null,
		@SerializedName("html_attributions")
		val htmlAttributions: List<String?>? = null,
		@SerializedName("height")
		val height: Int? = null
)
