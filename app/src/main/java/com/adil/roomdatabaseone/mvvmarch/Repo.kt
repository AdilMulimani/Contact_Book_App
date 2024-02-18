package com.adil.roomdatabaseone.mvvmarch

import android.content.Context
import androidx.lifecycle.LiveData
import com.adil.roomdatabaseone.roomdb.ContactDatabase
import com.adil.roomdatabaseone.roomdb.DbBuilderObject
import com.adil.roomdatabaseone.roomdb.dao.ContactDao
import com.adil.roomdatabaseone.roomdb.entity.Contact

//repo part of MVVM->interacts with DAO to get data
//has paths for CRUD operation
class Repo(private val context: Context)
{
    //creating a contact database object
    private var database: ContactDatabase?=null

    //init block only works in initialization of properties
    //runs just after constructor creation
    init {
        database = DbBuilderObject.getDb(context)
    }
    //functions for CRUD operations
    fun getContactData(): LiveData<List<Contact>>? {
       return database?.ContactDao()?.readContacts()
    }

    fun insertContactData(contact: Contact)
    {
        database?.ContactDao()?.createContact(contact)
    }

    fun updateContactData(contact: Contact)
    {
        database?.ContactDao()?.updateContact(contact)
    }
    fun deleteContactData(contact:Contact)
    {
        database?.ContactDao()?.deleteContact(contact)

    }
}