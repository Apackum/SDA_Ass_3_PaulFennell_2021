package com.example.sdaassign32021;

import java.util.Date;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

/*
 * simple Order T_shirt fragment
 * @author Paul Fennell 2021
 */
public class OrderTshirt extends Fragment {



    public OrderTshirt() {
        // Required empty public constructor
    }

    //class wide variables
    String currentPhotoPath;
    private Spinner mSpinner;
    private EditText mCustomerName;
    private EditText meditDelivery;
    private ImageView mCameraImage;
    private TextView mCol;
    private EditText mDelivery1;
    private Uri imageDisplay;
    //static keys
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final String TAG = "OrderTshirt";



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment get the root view.
        final View root = inflater.inflate(R.layout.fragment_order_tshirt, container, false);
        mCustomerName = root.findViewById(R.id.editCustomer);
        meditDelivery = root.findViewById(R.id.editDeliver);
        meditDelivery.setImeOptions(EditorInfo.IME_ACTION_DONE);
        meditDelivery.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mCameraImage = root.findViewById(R.id.imageView);
        mCol = root.findViewById(R.id.editCollect);
        mDelivery1 = root.findViewById(R.id.editDeliver);
        Button mSendButton = root.findViewById(R.id.sendButton);
        Button mCollection = root.findViewById(R.id.button_set_collection);
        Button mDelivery = root.findViewById(R.id.button_set_delivery_address);


        //set a listener on the the camera image
        mCameraImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPermissions();
                Log.d("onClickCam", "onClick: for camera button ");

            }
        });

        //set a listener to start the email intent.
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail(v);
                Log.d("onClick_email", "onClick: for sendemail ");
            }
        });

        mCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClickCollection", "onClick: for choosing collection ");
                mCollection.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.VISIBLE);
                mCol.setVisibility(View.VISIBLE);
                mDelivery1.setVisibility(View.INVISIBLE);
                mCameraImage.setImageURI(imageDisplay);
            }
        });

        mDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onClickDelivery", "onClick: for choosing delivery");
                mSpinner.setVisibility(View.INVISIBLE);
                mCol.setVisibility(View.INVISIBLE);
                mDelivery1.setVisibility(View.VISIBLE);
                mCameraImage.setImageURI(imageDisplay);

            }
        });

        //initialise spinner using the integer array
        mSpinner = root.findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(root.getContext(), R.array.ui_time_entries, R.layout.spinner_days);
        mSpinner.setAdapter(adapter);
        mSpinner.setEnabled(true);
        return root;
    }

    //This is my take picture intent which checks for permission
    //calls the createImageFile method and stores the camera image within uri using fileprovider
    //and starts the activity
    private void dispatchTakePictureIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File picFile = null;
            try {
                picFile = createPicFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Somthing went wrong when creating photoFile", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                //Stores image within a URI
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", picFile);
                imageDisplay =photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.d("dispatchCameraIntent", "This is my camera intent");
            }
        }
    }

    //    This is my create image and directory methods
    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createPicFile() throws IOException {
        // I am creating the images file name and declaring its location using a timestamp and imageName
        String timeFor = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "Camera_Image " + timeFor + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName,".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("CreatePicMethod", "This is creating a file");
        return image;
    }


    //This Creates My order summary in my sendEmail method
    //I have added an if/else statement to decide what is shown within the email when called
    private String createOrderSummary(View v) {
        String orderMessage = "";
        String deliveryInstruction = meditDelivery.getText().toString();
        String customerName = getString(R.string.customer_name) + " " + mCustomerName.getText().toString();
        orderMessage += customerName + "\n" + "\n" + getString(R.string.order_message_1);
        // if the spinner is visible do this if not show the delivery
        if(mSpinner.isShown()){
            orderMessage += "\n" + getString(R.string.order_message_collect) + mSpinner.getSelectedItem().toString() + " days.\n";
        }else{
            orderMessage += "\n" + "Please Deliver My order to this address ";
            orderMessage += "\n" + deliveryInstruction;
        }
        orderMessage += "\n" + getString(R.string.order_message_end) + "\n" + mCustomerName.getText().toString();
        Log.d("orderSum", "Creating an order summary");
        return orderMessage;
    }

    //This is the sendEmail method which allows the user to choose which email app they use
    //it also request a mandatory name and adds in the required info and attaches the camera image as attachment
    private void sendEmail(View v) {
        Intent email = new Intent(Intent.ACTION_SEND);
        String customerName = mCustomerName.getText().toString();
        if (mCustomerName == null || customerName.equals("")) {
            Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
        } else {
            email.setType("image/jpg");
            String orderMessage = createOrderSummary(v);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sdaass3@mail.ie"});
            email.putExtra(Intent.EXTRA_SUBJECT, new String("Order Summary"));
            email.putExtra(Intent.EXTRA_TEXT, orderMessage);
            email.putExtra(Intent.EXTRA_STREAM, imageDisplay);
            email.setType("image/jpg");
            //need this to prompts email client only
            email.setType("message/rfc822");
            //This starts the activity
            startActivity(Intent.createChooser(email, "Please Choose your main email application:"));
            Log.d(TAG, "sendEmail: should be sending an email with " + createOrderSummary(v));
        }
    }

    //This is my verifyPermissions method which requests the users permission if needed to read,write and access camera permissions
    private void verifyPermissions(){
        Log.d("perm", "Verify: Ask the user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(this.getContext(),permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getContext(),permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getContext(),permissions[2]) == PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();


        }else {
            ActivityCompat.requestPermissions(getActivity(),permissions,REQUEST_TAKE_PHOTO);
        }
    }


    //Overide onRequestPermissionResult method which uses the verifyPermission method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}