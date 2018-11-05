package com.example.simon.tpimageherlin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int SELECT_PICTURE = 1;
    private Bitmap originalBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Onclick listener du boutton Load
        Button loadBtn = (Button) findViewById(R.id.loadBtn);
        loadBtn.setOnClickListener(loadButtonClickListener);
        // Onclick listener du boutton Restore
        Button restoreBtn = (Button) findViewById(R.id.restoreBtn);
        restoreBtn.setOnClickListener(restoreButtonClickListener);

        // Menu contextuel
        ImageView img = (ImageView) findViewById(R.id.imgImageView);
        registerForContextMenu(img);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contextuel, menu);
    }

    View.OnClickListener loadButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Creation de l'intent
            Intent actionGetContentIntent = new Intent();
            actionGetContentIntent.setType("image/*");
            actionGetContentIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(actionGetContentIntent, "Select Picture"),
                    SELECT_PICTURE);
        }
    };

    View.OnClickListener restoreButtonClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            ImageView img = (ImageView) findViewById(R.id.imgImageView);
            img.setImageBitmap(originalBitmap);
        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        ImageView img;
        Bitmap bitmap;
        switch(id){
            case R.id.flipHOption:
                // Récupération de l'image
                img = (ImageView) findViewById(R.id.imgImageView);
                bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                // Création du miroir HORIZONTAL et affichage
                bitmap = flipImage(bitmap, "H");
                img.setImageBitmap(bitmap);

                return true;
            case R.id.flipVOption:
                // Récupération de l'image
                img = (ImageView) findViewById(R.id.imgImageView);
                bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                // Création du miroir VERTICAL et affichage
                bitmap = flipImage(bitmap, "V");
                img.setImageBitmap(bitmap);

                return true;
            case R.id.rotationRightOption:
                // Récupération de l'image
                img = (ImageView) findViewById(R.id.imgImageView);
                bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                // Rotation HORAIRE et affichage
                bitmap = rotationRight(bitmap);
                img.setImageBitmap(bitmap);

                return true;
            case R.id.rotationLeftOption:
                // Récupération de l'image
                img = (ImageView) findViewById(R.id.imgImageView);
                bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();

                // Rotation HORAIRE et affichage
                bitmap = rotationLeft(bitmap);
                img.setImageBitmap(bitmap);

                return true;
        }

        return false;
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reverseColorsOption:
                reverseImageColors();
                return true;
            case R.id.blackWhiteColorsOption:
                imageBlackWhiteColors();
                return true;
            case R.id.blackWhiteColors2Option:
                imageBlackWhiteColors2();
                return true;
            case R.id.blackWhiteColors3Option:
                imageBlackWhiteColors3();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                try {
                    // Récupération de l'uri
                    Uri selectedImageUri = data.getData();

                    // -- preparer les options de chargement de l’image
                    BitmapFactory.Options option = new BitmapFactory.Options();
                    option.inMutable = true; // l’image pourra etre modifiee
                    // -- chargement de l'image - valeur retournee null en cas d'erreur
                    Bitmap bm = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(selectedImageUri), null, option);

                    // Affiche l'image
                    ImageView img = (ImageView) findViewById(R.id.imgImageView);
                    img.setImageBitmap(bm);

                    // Affiche l'uri
                    TextView uriTextView = (TextView) findViewById(R.id.uriTextView);
                    uriTextView.setText(selectedImageUri.getPath());

                    // Sauvegarde l'image originale
                    originalBitmap = bm.copy(bm.getConfig(), bm.isMutable());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Bitmap flipImage(Bitmap bm, String flipOrientation) {
        Bitmap newBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(),
                Bitmap.Config.ARGB_8888);

        switch (flipOrientation) {
            case "H": // Horizontal flip
                for (int x = 0; x < bm.getWidth(); x++) {
                    int h = bm.getHeight() - 1;
                    for (int y = 0; y < bm.getHeight(); y++) {
                        int pix = bm.getPixel(x, y);
                        newBitmap.setPixel(x, h, pix);
                        h--;
                    }
                }

                return newBitmap;
            case "V": // Vertical flip
                int w = bm.getWidth() - 1;
                for (int x = 0; x < bm.getWidth(); x++) {
                    for (int y = 0; y < bm.getHeight(); y++) {
                        int pix = bm.getPixel(x, y);
                        newBitmap.setPixel(w, y, pix);
                    }
                    w--;
                }

                return newBitmap;
        }

        return null;
    };

    public void reverseImageColors() {
        ImageView imageView = (ImageView) findViewById(R.id.imgImageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        int length = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[length];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < length; i++) {
            int pixel = pixels[i];
            int alphaValue = Color.alpha(pixel);
            int redValue = 255 - Color.red(pixel);
            int greenValue = 255 - Color.green(pixel);
            int blueValue = 255 - Color.blue(pixel);

            pixels[i] = Color.argb(alphaValue, redValue, greenValue, blueValue);
        }

        newBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imageView.setImageBitmap(newBitmap);
    }

    public void imageBlackWhiteColors() {
        ImageView imageView = (ImageView) findViewById(R.id.imgImageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        int length = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[length];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < length; i++) {

            int pixel = pixels[i];
            int alphaValue = Color.alpha(pixel);
            int redValue = Color.red(pixel);
            int greenValue = Color.green(pixel);
            int blueValue = Color.blue(pixel);

            redValue = blueValue = greenValue = (redValue + blueValue + greenValue) / 3;
            pixels[i] = Color.argb(alphaValue, redValue, greenValue, blueValue);
        }

        newBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imageView.setImageBitmap(newBitmap);
    }

    public void imageBlackWhiteColors2() {
        ImageView imageView = (ImageView) findViewById(R.id.imgImageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        int length = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[length];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < length; i++) {

            int pixel = pixels[i];
            int alphaValue = Color.alpha(pixel);
            int redValue = Color.red(pixel);
            int greenValue = Color.green(pixel);
            int blueValue = Color.blue(pixel);

            redValue = greenValue = blueValue =
                    (Math.max(redValue, Math.max(greenValue,blueValue)) +
                            Math.min(redValue, Math.min(greenValue,blueValue))) / 2;

            pixels[i] = Color.argb(alphaValue, redValue, greenValue, blueValue);
        }

        newBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imageView.setImageBitmap(newBitmap);
    }

    public void imageBlackWhiteColors3() {
        ImageView imageView = (ImageView) findViewById(R.id.imgImageView);
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        int length = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[length];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int i = 0; i < length; i++) {

            int pixel = pixels[i];
            int alphaValue = Color.alpha(pixel);
            int redValue = Color.red(pixel);
            int greenValue = Color.green(pixel);
            int blueValue = Color.blue(pixel);

            redValue = greenValue = blueValue = (int)(0.21 * redValue + 0.72 * greenValue + 0.07 * blueValue);
            pixels[i] = Color.argb(alphaValue, redValue, greenValue, blueValue);
        }

        newBitmap.setPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imageView.setImageBitmap(newBitmap);
    }

    // Rotation horaire
    public Bitmap rotationRight(Bitmap bitmap) {
        Bitmap bm = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), bitmap.getConfig());

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                bm.setPixel(j, i, bitmap.getPixel(i, bitmap.getHeight() - j - 1));
            }
        }

        return bm;
    }

    // Rotation anti-horaire
    public Bitmap rotationLeft(Bitmap bitmap) {
        Bitmap bm = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(), bitmap.getConfig());

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                bm.setPixel(j, i, bitmap.getPixel(bitmap.getWidth() - i - 1, j));
            }
        }

        return bm;
    }
}