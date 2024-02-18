package com.adil.roomdatabaseone

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adil.roomdatabaseone.adapters.ContactAdapter
import com.adil.roomdatabaseone.databinding.ActivityMainBinding
import com.adil.roomdatabaseone.mvvmarch.MainViewModel
import com.adil.roomdatabaseone.roomdb.entity.Contact
import es.dmoral.toasty.Toasty
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


class MainActivity : AppCompatActivity() {
    //view binding declaration
    private lateinit var binding: ActivityMainBinding
    //declaring view model for main
    private lateinit var viewModel:MainViewModel
    //creating a contact List Variable
    private lateinit var contactList:ArrayList<Contact>
    //declaring a variable of Contact Adapter
    private lateinit var contactAdapter:ContactAdapter
    //icons
    private val deleteIcon = R.drawable.userdelete
    private val callIcon = R.drawable.userphone

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //enabling view binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //delete and call color


        //if permission is already granted then
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)==PackageManager.PERMISSION_GRANTED)
        {
            //if permission is granted then authorization takes place
            allowUIAccess()
        }
        // if permission is not granted then request for permission
        else
        {
            // Permission is not granted, request it
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                showPermissionRationaleDialog()
            } else {
                requestCallPermission()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with making the call
                allowUIAccess()
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
                showPermissionDeniedDialog()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun allowUIAccess() {
        binding.searchBar.visibility = View.VISIBLE
        binding.contactsTextView.visibility = View.VISIBLE
        binding.addButton.visibility = View.VISIBLE
        //initialing view model
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        //initialing contactList
        contactList = ArrayList()
        //observing live data
        viewModel.contactListLiveData.observeForever {
            //clearing to avoid repetition
            contactList.clear()
            //adding all the contacts
            it.map {
                contactList.add(it)
            }

            if(contactList.size==0)
            {
                binding.noSearchResults.visibility=View.VISIBLE
            }
            else if(contactList.size!=0)
            {
                binding.noSearchResults.visibility=View.GONE
            }
            //notifying adapter that data has been changed
            contactAdapter.notifyDataSetChanged()
        }

        //helps for moving,swiping etc.
        ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(1,ItemTouchHelper.LEFT
                or ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
            {
                if(ItemTouchHelper.LEFT == direction)
                {
                    //deleting the contact located at the items position
                    viewModel.deleteContact(contactList[viewHolder.adapterPosition])

                    Toasty.success(this@MainActivity,
                        "${contactList[viewHolder.adapterPosition].name} Contact Deleted.",
                        Toast.LENGTH_SHORT).show()

                    contactAdapter.notifyDataSetChanged()
                }
                else if(ItemTouchHelper.RIGHT == direction)
                {
                    //if you swipe right then you get to call
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:${contactList[viewHolder.adapterPosition].phoneNumber}")
                    startActivity(intent)
                    contactAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.generalBackground)
                    )
                    .addSwipeLeftActionIcon(deleteIcon)
                    .addSwipeLeftLabel("Delete")
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.generalBackground))
                    .addSwipeRightActionIcon(callIcon)
                    .addSwipeRightLabel("Call")
                    .create()
                    .decorate()
            }



        }).attachToRecyclerView(binding.recyclerView) //attaching to recycler view

        //connecting database to recycler view
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        contactAdapter = ContactAdapter(this,contactList)
        binding.recyclerView.adapter = contactAdapter

        //if we click on add button then we are directed to add contact activity
        binding.addButton.setOnClickListener {
            val i = Intent(this, AddEditActivity::class.java)
            startActivity(i)
        }

        //when we search for a contact
        binding.searchBar.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
              //do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s == null)
                {
                    contactAdapter.contactList = contactList
                    binding.noSearchResults.visibility = View.GONE
                    contactAdapter.notifyDataSetChanged()
                }
                else
                {
                    if (s.isEmpty())
                    {
                        binding.noSearchResults.visibility = View.GONE
                        contactAdapter.contactList = contactList
                        contactAdapter.notifyDataSetChanged()
                    }
                    else
                    {
                        val tempList = arrayListOf<Contact>()
                        contactList.forEach {
                            if (it.name?.contains(s, true) == true) { // Guard against null `it.name`
                                tempList.add(it)
                            }
                        }
                        if(tempList.size == 0)
                        {
                            binding.noSearchResults.visibility = View.VISIBLE
                            Toasty.error(this@MainActivity,
                                "No results for your Search.",
                                Toast.LENGTH_SHORT).show()
                        }
                        else
                        {
                            binding.noSearchResults.visibility = View.GONE
                        }
                        contactAdapter.contactList = tempList
                        contactAdapter.notifyDataSetChanged()
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {
               //do nothing
            }
        })
    }
    //requesting call permission
    private fun requestCallPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PERMISSION)
    }
    //dialog which mandates to grant permission
    private fun showPermissionRationaleDialog() {

       val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setView(R.layout.alertdialogcustom)

        val alertDialog = alertDialogBuilder.create()
            alertDialog.setOnShowListener {
                val message = alertDialog.findViewById<TextView>(R.id.text_message)
                val negativeButton = alertDialog.findViewById<Button>(R.id.button_negative)
                val positiveButton = alertDialog.findViewById<Button>(R.id.button_positive)

                positiveButton.text = "Grant"
                negativeButton.text="Cancel"

                message.setText(R.string.alert_message_one)
                positiveButton.setOnClickListener {
                    requestCallPermission()
                    alertDialog.dismiss()
                }
                negativeButton.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
        alertDialog.show()
    }
//dialog which appears when you deny permission
    private fun showPermissionDeniedDialog() {

    val alertDialogBuilder = AlertDialog.Builder(this)
    alertDialogBuilder.setView(R.layout.alertdialogcustom)

    val alertDialog = alertDialogBuilder.create()

    alertDialog.setOnShowListener {
        val message = alertDialog.findViewById<TextView>(R.id.text_message)
        val negativeButton = alertDialog.findViewById<Button>(R.id.button_negative)
        val positiveButton = alertDialog.findViewById<Button>(R.id.button_positive)

        positiveButton.text = "Settings"
        negativeButton.text="Cancel"

        message.setText(R.string.alert_message_two)
        positiveButton.setOnClickListener {
            // Open app settings so the user can grant the permission
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.fromParts("package", packageName, null)
            startActivity(intent)
            alertDialog.dismiss()
            this.recreate()
        }
        negativeButton.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    alertDialog.show()
    }
}

