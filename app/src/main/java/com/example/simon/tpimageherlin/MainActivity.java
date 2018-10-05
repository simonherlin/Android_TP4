package com.example.simon.tpimageherlin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loadBtn = (Button) findViewById(R.id.loadBtn);
        loadBtn.setOnClickListener(loadButtonClickListener);

        Button flipHBtn = (Button) findViewById(R.id.flipHBtn);
        flipHBtn.setOnClickListener(flipHButtonClickListener);

        Button flipVBtn = (Button) findViewById(R.id.flipVBtn);
        flipVBtn.setOnClickListener(flipVButtonClickListener);
    }

    View.OnClickListener loadButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent actionGetContentIntent = new Intent();
            actionGetContentIntent.setType("image/*");
            actionGetContentIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(actionGetContentIntent,
                    "Select Picture"), SELECT_PICTURE);
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                try {
                    Uri selectedImageUri = data.getData();

                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inMutable = true;
                    Bitmap bm = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(selectedImageUri), null, option);

                    ImageView img = (ImageView) findViewById(R.id.imgImageView);
                    img.setImageBitmap(bm);

                    TextView uriTextView = (TextView) findViewById(R.id.uriTextView);
                    uriTextView.setText(selectedImageUri.getPath());

                    Button flipHBtn = (Button) findViewById(R.id.flipHBtn);
                    Button flipVBtn = (Button) findViewById(R.id.flipVBtn);
                    flipHBtn.setVisibility(View.VISIBLE);
                    flipVBtn.setVisibility(View.VISIBLE);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    View.OnClickListener flipHButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ImageView img = (ImageView) findViewById(R.id.imgImageView);
            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();

            Bitmap FlippedBitmap = flipImage(bitmap, "H");
            img.setImageBitmap(FlippedBitmap);
        }
    };

    View.OnClickListener flipVButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ImageView img = (ImageView) findViewById(R.id.imgImageView);
            Bitmap bitmap = ((BitmapDrawable)img.getDrawable()).getBitmap();

            Bitmap FlippedBitmap = flipImage(bitmap, "V");
            img.setImageBitmap(FlippedBitmap);
        }
    };

    public Bitmap flipImage(Bitmap bm, String flipOrientation) {
        Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
                Bitmap.Config.ARGB_8888);

        switch(flipOrientation) {
            case "H":
                for (int x = 0; x < bm.getWidth(); x++)
                {
                    int h = bm.getHeight() - 1;
                    for (int y = 0; y < bm.getHeight(); y++)
                    {
                        int pix = bm.getPixel(x, y);
                        newBitmap.setPixel(x, h, pix);
                        h--;
                    }
                }

                return newBitmap;
            case "V":
                int w = bm.getWidth() - 1;
                for (int x = 0; x < bm.getWidth(); x++)
                {
                    for (int y = 0; y < bm.getHeight(); y++)
                    {
                        int pix = bm.getPixel(x, y);
                        newBitmap.setPixel(w, y, pix);
                    }
                    w--;
                }
                return newBitmap;
        }
        return null;
    };
}
