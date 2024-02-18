package com.adil.roomdatabaseone.mvvmarch

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.adil.roomdatabaseone.roomdb.entity.Contact

class AddEditViewModel(application: Application) : AndroidViewModel(application)
{
    private var repo:Repo

    init {
        //initialing db
      repo = Repo(application)
    }

    fun storeData(contact: Contact,function:(i:Unit?)->Unit)
    {
       val i =  repo.insertContactData(contact)
        function(i)
    }

    fun updateData(contact: Contact,function: (i: Unit?) -> Unit)
    {
        val i = repo.updateContactData(contact)
        function(i)

    }

}