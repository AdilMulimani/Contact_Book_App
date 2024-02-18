package com.adil.roomdatabaseone.roomdb

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.adil.roomdatabaseone.dbName

object DbBuilderObject
{
    //creating a single ton object of contact database
    private var contactDatabaseSingletonObject:ContactDatabase?=null

    //  a function which is called when we have to perform actions on database like CRUD
    fun getDb(context:Context):ContactDatabase?
    {
        //if singleton object is not yet create,create it only onc
        if(contactDatabaseSingletonObject == null)
        {
            contactDatabaseSingletonObject = Room.databaseBuilder(
                context,
                ContactDatabase::class.java,
                dbName)
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE).fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build()
        }
        return contactDatabaseSingletonObject
    }
}