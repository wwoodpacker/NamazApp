package com.aznstudio.namaz.data

data class ResultsItem(
	val reference: String? = null,
	val types: List<String?>? = null,
	val scope: String? = null,
	val icon: String? = null,
	val name: String? = null,
	val rating: Double? = null,
	val geometry: Geometry? = null,
	val vicinity: String? = null,
	val id: String? = null,
	val photos: List<PhotosItem?>? = null,
	val placeId: String? = null
)
