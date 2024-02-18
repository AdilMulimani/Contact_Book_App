package com.adil.roomdatabaseone.roomdb.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.adil.roomdatabaseone.roomdb.entity.Contact

@Dao//tells room db that this is dao
interface ContactDao
{
    //create
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun createContact(contact: Contact)
    //update
    @Update
    fun updateContact(contact: Contact)
    //read
    @Query("SELECT * FROM CONTACT")
    fun readContacts():LiveData<List<Contact>>
    //read only id
    @Query("SELECT * FROM CONTACT WHERE id = :id1")
    fun readContactId(id1:Int):Contact
    //delete
    @Delete
    fun deleteContact(contact: Contact)
}