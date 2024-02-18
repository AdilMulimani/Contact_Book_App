package com.adil.roomdatabaseone.roomdb.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity //tells database that this class is an entity
   class Contact(
    @PrimaryKey(autoGenerate = true)
    var id :Int?=null,
    var profile :ByteArray?=null,
    var name :String?=null,
    var phoneNumber :String?=null,
    var email :String?=null
):Serializable
