package io.joaa.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import java.awt.image.BufferedImage;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BufferedImage image;

        File imgFile = new File("C:\\Users\\Asus\\IdeaProjects\\untitled1\\src\\nid_cr.jpg");
        image = new BufferedImage();

        //String str = IDScanner.processImage(image);
        //System.out.println(str);

    }
}
