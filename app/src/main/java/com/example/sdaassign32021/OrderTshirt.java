package com.example.sdaassign32021;

import static android.app.Activity.RESULT_OK;

import java.io.FileOutputStream;
import java.util.Date;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
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
            }
        });

        mCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCollection.setVisibility(View.VISIBLE);
                mSpinner.setVisibility(View.VISIBLE);
                mCol.setVisibility(View.VISIBLE);
                mDelivery1.setVisibility(View.INVISIBLE);
            }
        });

        mDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpinner.setVisibility(View.INVISIBLE);
                mCol.setVisibility(View.INVISIBLE);
                mDelivery1.setVisibility(View.VISIBLE);
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


    //    This is my create image and directory methods
    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // I am creating the images file name and declaing its location
        String timeFor = new SimpleDateFormat("yyyy MM dd HH mm ss").format(new Date());
        String imageName = "Camera_Image " + timeFor + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageName,".jpg", storageDir);
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), "com.example.android.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }



    //This Creates My order summary in my Send email method
    private String createOrderSummary(View v) {
        String orderMessage = "";

        String deliveryInstruction = meditDelivery.getText().toString();
        String customerName = getString(R.string.customer_name) + " " + mCustomerName.getText().toString();
        orderMessage += customerName + "\n" + "\n" + getString(R.string.order_message_1);
        if(mSpinner.isShown()){
            orderMessage += "\n" + getString(R.string.order_message_collect) + mSpinner.getSelectedItem().toString() + " days.\n";
        }else{
            orderMessage += "\n" + "Please Deliver My order to this address ";
            orderMessage += "\n" + deliveryInstruction;
        }
        orderMessage += "\n" + getString(R.string.order_message_end) + "\n" + mCustomerName.getText().toString();

        return orderMessage;
    }

    private void sendEmail(View v) {
        Intent email = new Intent(Intent.ACTION_SEND);
        String customerName = mCustomerName.getText().toString();
        if (mCustomerName == null || customerName.equals(""))
        {
            Toast.makeText(getContext(), "Please enter your name", Toast.LENGTH_SHORT).show();
            /* we can also use a dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Notification!").setMessage("Customer Name not set.").setPositiveButton("OK", null).show();*/

        } else {
            email.setType("image/jpg");
            String orderMessage = createOrderSummary(v);
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"sdaass3@mail.ie"});
            email.putExtra(Intent.EXTRA_SUBJECT, new String("Order Summary"));
            email.putExtra(Intent.EXTRA_TEXT, orderMessage);
            Uri uri = Uri.fromFile(phot);
            email.putExtra(Intent.EXTRA_STREAM, currentPhotoPath);
            email.setType("image/jpg");
            //need this to prompts email client only
            email.setType("message/rfc822");
            //This starts the activity
            startActivity(Intent.createChooser(email, "Please Choose your main email application:"));
            Log.d(TAG, "sendEmail: should be sending an email with " + createOrderSummary(v));
        }
    }

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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }
}