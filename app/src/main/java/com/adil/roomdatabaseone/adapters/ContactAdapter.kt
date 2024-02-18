package com.adil.roomdatabaseone.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.adil.roomdatabaseone.AddEditActivity
import com.adil.roomdatabaseone.databinding.ContactItemBinding
import com.adil.roomdatabaseone.roomdb.entity.Contact

class ContactAdapter(private val context: Context, var contactList:List<Contact>)
    : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>()
{
    inner class ContactViewHolder(var binding: ContactItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder
    {
        //creating skeleton of each view
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context))
        return ContactViewHolder(binding)
    }

    override fun getItemCount(): Int
    {
        //returning no of view in our recycler view
        return contactList.size
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

        //getting position of view
        val contact= contactList[position]

        //if our contact profile is not null then
        if (contact.profile  != null){

            //create a byte array which will store profile
            val imageByte= contact.profile

            //if the image byte array is not null then
            if (imageByte!=null){
                //decode byte array
                val image= BitmapFactory.decodeByteArray(imageByte,0,imageByte.size)
                //set decoded byte array  to profile image
                holder.binding.contactItemProfileImage.setImageBitmap(image)
                //enable profile image
                holder.binding.contactItemProfileImage.visibility= View.VISIBLE
                //disable the text profile
                holder.binding.contactItemProfileText.visibility= View.GONE
            }else
            {    //if the byte array is null due to some error  then you just add a text profile image
                val splitName= contact.name?.split(" ")
                var sign=""
                splitName?.forEach {
                    if (it.isNotEmpty()){
                        sign += it[0]
                    }
                }
                //profile image will be a tact based on contact name
                holder.binding.contactItemProfileText.text=sign
                //visibility of the image profile is gone
                holder.binding.contactItemProfileImage.visibility= View.GONE
                //visibility of the text profile is visible
                holder.binding.contactItemProfileText.visibility= View.VISIBLE
            }
        }
        //if the profile image is null  then you just add a text profile image
        else
        {
            val splitName= contact.name?.split(" ")
            var sign=""
            splitName?.forEach {
                if (it.isNotEmpty()){
                    sign += it[0]

                }
            }
            context

            //profile image will be a tact based on contact name
            holder.binding.contactItemProfileText.text=sign
            //visibility of the image profile is gone
            holder.binding.contactItemProfileImage.visibility= View.GONE
            //visibility of the text profile is visible
            holder.binding.contactItemProfileText.visibility= View.VISIBLE
        }
        //setting up contact name,phone,email
        holder.binding.contactItemName.text=contact.name
        holder.binding.contactItemPhoneNumber.text=contact.phoneNumber
        holder.binding.contactItemEmail.text=contact.email

        //when you click on each item then
        holder.itemView.setOnClickListener{
            //we go from recycler view to added-editActivity + adding flag
            context.startActivity(Intent(context,AddEditActivity::class.java)
                .putExtra("flag",1)//sending flag
                .putExtra("data",contact)//sending complete contact at a given position
            )
        }

        setAnimation(holder.itemView,position)
    //end of onBind
    }

  private fun setAnimation(viewToAnimate:View,position:Int)
    {
        val slideIn = AnimationUtils.loadAnimation(context,android.R.anim.slide_in_left)
        viewToAnimate.startAnimation(slideIn)

    }
}