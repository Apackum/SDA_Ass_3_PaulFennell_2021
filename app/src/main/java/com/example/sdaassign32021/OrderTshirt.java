package com.example.sdaassign32021;

import android.Manifest;
import android.annotation.SuppressLint;
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
import java.util.Date;
import java.util.Objects;
/*      Copyright [2021] [Paul fennell]

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
        */
/**
 * A fragment OrderTshirt.java which lets the user take a picture, add name, add delivery or collection and opens
 * a pre made order summary within the chosen email client and attaches the image
 * @author Paul Fennell 2021
 * @version 1.0
 */
public class OrderTshirt extends Fragment {

    public OrderTshirt() {
        // Required empty public constructor
    }

    //class wide variables
    /**
     * These are all my editTexts, Imageview and textview objects
     */
    private EditText mCustomerName;
    private EditText meditDelivery;
    private ImageView mCameraImage;
    private TextView mCol;
    /**
     * This is my uri which i use to access the camera image for attaching to email and adding to the ImageView
     */
    private Uri imageDisplay;
    /**
     * This String is used for the current photo path
     */
    String currentPhotoPath;
    /**
     * This names my spinner
     */
    private Spinner mSpinner;
    //static keys
    /**
     * These are mt static keys
     */
    private static final int REQUEST_TAKE_PHOTO = 2;
    private static final String TAG = "OrderTshirt";


    /**
     * @param inflater inflates the layout
     * @param container contains
     * @param savedInstanceState saves instancestate
     * @return root
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment get the root view.
        final View root = inflater.inflate(R.layout.fragment_order_tshirt, container, false);
        mCustomerName = root.findViewById(R.id.editCustomer);
        meditDelivery = root.findViewById(R.id.editDeliver);
        meditDelivery.setImeOptions(EditorInfo.IME_ACTION_DONE);
        meditDelivery.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mCameraImage = root.findViewById(R.id.imageView);
        mCol = root.findViewById(R.id.editCollect);
        Button mSendButton = root.findViewById(R.id.sendButton);
        Button mCollection = root.findViewById(R.id.button_set_collection);
        Button mDelivery = root.findViewById(R.id.button_set_delivery_address);


        mCameraImage.setOnClickListener(new View.OnClickListener() {
            /**
             * This the listener for my mCameraimage which when clicked runs the verifyPermission method
             * @param v mcameraImage listener
             */
            @Override
            public void onClick(View v) {

                verifyPermissions();
                Log.d("onClickCam", "onClick: for camera button ");

            }
        });

        //This is for the collection button
        mCollection.setOnClickListener(new View.OnClickListener() {
            /**
             *This the listener for my mCollection button which when clicked sets  mCollection, mSpinner
             *mCol to visible and mDelivery1 to invisible, also when clicked it sets mCameraImage to the
             *imageView within the fragment_order_tshirt.xml
             * @param v mCollection listener
             */
            @Override
            public void onClick(View v) {
                Log.d("onClickCollection", "onClick: for choosing collection ");
                mCollection.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.VISIBLE);
                mCol.setVisibility(View.VISIBLE);
                meditDelivery.setVisibility(View.INVISIBLE);
                mCameraImage.setImageURI(imageDisplay);
            }
        });

        //This is for the delivery button to make
        mDelivery.setOnClickListener(new View.OnClickListener() {
            /**
             * This the listener for my mCollection button which when clicked sets mSpinner
             * mCol to invisible and mDelivery1 to visible, also when clicked it sets mCameraImage to the
             * imageView within the fragment_order_tshirt.xml
             * @param v listener for mdelivery
             */
            @Override
            public void onClick(View v) {
                Log.d("onClickDelivery", "onClick: for choosing delivery");
                mSpinner.setVisibility(View.INVISIBLE);
                mCol.setVisibility(View.INVISIBLE);
                meditDelivery.setVisibility(View.VISIBLE);
                mCameraImage.setImageURI(imageDisplay);

            }
        });

        //set a listener to start the email intent.
        mSendButton.setOnClickListener(new View.OnClickListener() {
            /**
             * This mSendButton when clicked starts the sendEmail method and checks if the customer name is blank
             * and requests user to fill it in
             * @param v listener for the send button
             */
            @Override
            public void onClick(View v) {
                sendEmail();
                Log.d("onClick_email", "onClick: for sendemail ");
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

    /**
     *This is my take picture intent which checks for permission
     *calls the createImageFile method and stores the camera image within uri using fileprovider
     *and starts the activity
     */
    @SuppressLint("NewApi")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(Objects.requireNonNull(getActivity()).getPackageManager()) != null) {
            File picFile = null;
            try {
                picFile = createPicFile();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong when creating photoFile", Toast.LENGTH_SHORT).show();
            }
            if (picFile != null) {
                //Stores image within a URI
                Uri photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "com.example.android.fileprovider", picFile);
                imageDisplay =photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.d("dispatchCameraIntent", "This is my camera intent");
            }
        }
    }


    /**
     * This is my create image and directory methods
     * @return image
     * @throws IOException exception
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createPicFile() throws IOException {
        // I am creating the images file name and declaring its location using a timestamp and imageName
        @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageName = "Camera_Image " + time + "_";
        File storageDir = Objects.requireNonNull(getActivity()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName,".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("CreatePicMethod", "This is creating a file");
        return image;
    }

    /**
     *This Creates My order summary in my sendEmail method
     *I have added an if/else statement to decide what is shown within the email when called
     * @return the order message
     */
    private String createOrderSummary() {
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

    /**
     *This is the sendEmail method which allows the user to choose which email app they use
     * it also request a mandatory name and adds in the required info and attaches the camera image as attachment
     */
    private void sendEmail() {
        Intent email = new Intent(Intent.ACTION_SEND);
        String customerName = mCustomerName.getText().toString();
        if (mCustomerName == null || customerName.equals("")) {
            Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
        } else {
            String orderMessage = createOrderSummary();
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sdaass3@mail.ie"});
            email.putExtra(Intent.EXTRA_SUBJECT, "Order Summary");
            email.putExtra(Intent.EXTRA_TEXT, orderMessage);
            email.putExtra(Intent.EXTRA_STREAM, imageDisplay);
            email.setType("image/jpg");
            //need this to prompts email client only
            email.setType("message/rfc822");
            //This starts the activity
            startActivity(Intent.createChooser(email, "Please Choose your main email application:"));
            Log.d(TAG, "sendEmail: should be sending an email with " + createOrderSummary());
        }
    }

    /**
     *This is my verifyPermissions method which requests the users permission if needed to read,write and access camera permissions
     * and if they are all verified already run the dispatchTakePictureIntent
     */
    private void verifyPermissions(){
        Log.d("perm", "Verify: Ask the user for permissions");
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(this.getContext()),permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getContext(),permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.getContext(),permissions[2]) == PackageManager.PERMISSION_GRANTED){
            dispatchTakePictureIntent();


        }else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),permissions,REQUEST_TAKE_PHOTO);
        }
    }

    /**
     * Override onRequestPermissionResult method which uses the verifyPermission method
     * @param requestCode request code
     * @param permissions permissions
     * @param grantResults grants the result
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}