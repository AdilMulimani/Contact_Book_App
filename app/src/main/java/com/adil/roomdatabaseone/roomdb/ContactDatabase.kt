package com.adil.roomdatabaseone.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adil.roomdatabaseone.roomdb.dao.ContactDao
import com.adil.roomdatabaseone.roomdb.entity.Contact

//tells room db that this class in database
@Database(entities = [Contact::class], version = 4, exportSchema = false)
//room-db tells us to only declare not define
abstract class ContactDatabase: RoomDatabase()
{
    abstract fun ContactDao():ContactDao
}