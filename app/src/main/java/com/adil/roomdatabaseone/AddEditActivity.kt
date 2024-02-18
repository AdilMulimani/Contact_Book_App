package com.adil.roomdatabaseone
import android.app.Activity
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.adil.roomdatabaseone.databinding.ActivityAddEditBinding
import com.adil.roomdatabaseone.mvvmarch.AddEditViewModel
import com.adil.roomdatabaseone.roomdb.entity.Contact
import com.github.dhaval2404.imagepicker.ImagePicker
import es.dmoral.toasty.Toasty

class AddEditActivity : AppCompatActivity() {
    //view binding declaration
    private lateinit var binding :ActivityAddEditBinding
    //declaration and initialization of our contact entity
    private var contact:Contact = Contact()
    //declaring object of view model
    private lateinit var viewModel:AddEditViewModel
    //declaration of flag
    private var flag:Int = -1
    //github library to get image from gallery
    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: androidx.activity.result.ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    //Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!
                    binding.profileImage.setImageURI(fileUri)
                    val imageBytes = contentResolver.openInputStream(fileUri)?.readBytes()
                    contact.profile = imageBytes
                }
                ImagePicker.RESULT_ERROR -> {
                    Toasty.error(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toasty.error(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enabling view binding
        binding = ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialising AddEditViewModel
        viewModel = ViewModelProvider(this)[AddEditViewModel::class.java]


        //when you long click on profile image it will be displayed
        binding.profileImage.setOnLongClickListener {
            //object of dialog
            val dialog = Dialog(this)
            //setting its layout
            dialog.setContentView(R.layout.image_dialog)
            //creating reference of Image
            val profileImage = dialog.findViewById<ImageView>(R.id.dialog_image)
            //image object from activity
            val imageObject = binding.profileImage.drawable
            //setting activity profile to dialog profile
            profileImage.setImageDrawable(imageObject)
            ///making background transparent to remove extra background
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            //dimensions of dialog
            val lp = WindowManager.LayoutParams()
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.blurBehindRadius = 100
            lp.flags = WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            dialog.window?.attributes=lp
            //showing dialog
            dialog.show()
            return@setOnLongClickListener true
        }

        //when you short click on profile Image it will let you set your choice of image from gallery
        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .crop()
                .createIntent { intent -> startForProfileImageResult.launch(intent)
                }
        }

            //when yuo click on save contact button
            binding.saveContactButton.setOnClickListener {

                //if flag == 1 we update
                if(flag == 1)
                {
                    //if we don't fill required fields
                    if(binding.nameEditText.text.toString()==""
                        ||binding.phoneNumberEditText.text.toString() =="")
                    {
                        Toasty.error(this, "Please Enter required fields", Toast.LENGTH_SHORT).show()
                    }
                    else //if all the required fields are filled
                    {
                        //setting the values that you add in edit text
                        contact.name = binding.nameEditText.text.toString()
                        contact.phoneNumber  = binding.phoneNumberEditText.text.toString()
                        contact.email= binding.emailEditText.text.toString()

                        viewModel.updateData(contact)
                        {
                            Toasty.success(this, "Updated Contact!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
                else //else we create a new contact
                {
                    if(binding.nameEditText.text.toString()==""
                        ||binding.phoneNumberEditText.text.toString() =="")
                    {
                        Toasty.error(this, "Please enter required fields.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //setting the values that you add in edit text
                        contact.name = binding.nameEditText.text.toString()
                        contact.phoneNumber  = binding.phoneNumberEditText.text.toString()
                        contact.email= binding.emailEditText.text.toString()

                        //creating this contact in our database
                        viewModel.storeData(contact)
                        {
                            //clearing all the edit texts so that previous values should disappear
                            binding.emailEditText.text?.clear()
                            binding.nameEditText.text?.clear()
                            binding.phoneNumberEditText.text?.clear()

                            //closing the activity right after all the actions are performed
                            Toasty.success(this, "Created Contact!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
        }

        //intent handling from contact Adapter
        if(intent.hasExtra("flag"))
        {
         flag = intent.getIntExtra("flag",-1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                contact = intent.getSerializableExtra("data",Contact::class.java)!!
            }
            else
            {
                contact = intent.getSerializableExtra("data") as Contact
            }

        }
        //if we have received the flag from contact adapter then we can update recycler view item view
        if(flag == 1)
        {
            //changing Save Contact to Update Contact text
            binding.saveContactButton.setText(R.string.update_contact)

            //handling profile image
            val imageByte= contact.profile

            //handling image
            //if the image byte array is not null then
            if (imageByte!=null){
                //decode byte array
                val image= BitmapFactory.decodeByteArray(imageByte,0,imageByte.size)
                //set decoded byte array  to profile image
                binding.profileImage.setImageBitmap(image)
                //enable profile image
                binding.profileImage.visibility= View.VISIBLE
            }

            //setting data from intent data to our contact to display item details
            binding.emailEditText.setText(contact.email)
            binding.nameEditText.setText(contact.name)
            binding.phoneNumberEditText.setText(contact.phoneNumber)

        }
    }
}