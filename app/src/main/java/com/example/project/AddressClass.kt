package com.example.project

import com.google.gson.annotations.SerializedName

data class AddressClass(@SerializedName("id_address") val id: Int? = null,
                        @SerializedName( "receiver_name") val receiver_name: String,
                        @SerializedName( "phone") val phone: String,
                        @SerializedName( "address_detail") val address_detail: String,
                        @SerializedName("post_code") val post_code: String
)

