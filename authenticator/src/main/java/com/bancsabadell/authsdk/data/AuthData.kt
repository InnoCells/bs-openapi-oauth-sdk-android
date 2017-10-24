package com.bancsabadell.authsdk.data

import android.os.Parcel
import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONObject

/**
 * Created by Kame on 23/10/2017.
 */
data class AuthData(@JsonProperty("access_token") val accessToken: String,
                    @JsonProperty("token_type") val tokenType: String,
                    @JsonProperty("refresh_token") val refreshToken: String,
                    @JsonProperty("expires_in") val expiresIn: Int,
                    @JsonProperty("scope") val scope: String,
                    @JsonProperty("client_id") val clientId: String) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(accessToken)
        parcel.writeString(tokenType)
        parcel.writeString(refreshToken)
        parcel.writeInt(expiresIn)
        parcel.writeString(scope)
        parcel.writeString(clientId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AuthData> {
        override fun createFromParcel(parcel: Parcel): AuthData {
            return AuthData(parcel)
        }

        override fun newArray(size: Int): Array<AuthData?> {
            return arrayOfNulls(size)
        }
    }
}

fun AuthData.asJson(): JSONObject {
    val jsonString = ObjectMapper().writeValueAsString(this)
    return JSONObject(jsonString)
}
