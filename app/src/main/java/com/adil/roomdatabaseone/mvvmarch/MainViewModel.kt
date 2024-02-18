package com.adil.roomdatabaseone.mvvmarch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.adil.roomdatabaseone.roomdb.entity.Contact

class MainViewModel(application: Application) : AndroidViewModel(application)
{
    //contains data which gets changed again and again
    var contactListLiveData : LiveData<List<Contact>>
    //creating an object of repo
    private var repo:Repo
    //initializing
    init {
        repo = Repo(application)
       contactListLiveData = repo.getContactData()!!
    }
    //function to delete contact
    fun deleteContact(contact: Contact)
    {
        repo.deleteContactData(contact)
    }
}
