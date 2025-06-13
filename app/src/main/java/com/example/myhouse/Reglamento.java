package com.example.myhouse;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;  // Add this import

public class Reglamento extends AppCompatActivity {
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton zoomResetButton;
    private String PDF_URL = "";
    private Executor executor = Executors.newSingleThreadExecutor();
    private ViewPager2 viewPager;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor parcelFileDescriptor;
    private List<Bitmap> pdfPages;
    private TextView pageCounterTextView;
    private ImageButton prevPageButton;
    private ImageButton nextPageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reglamento);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Reglamento").child("Link").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue(String.class);
                Log.d("PDF_URL", "URL obtenida: " + url);
                PDF_URL = url;
                loadPdf();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Reglamento", "Error fetching PDF URL from Firebase", databaseError.toException());
                Toast.makeText(Reglamento.this, "Error fetching PDF URL", Toast.LENGTH_SHORT).show();
            }
        });



        View navBar = findViewById(R.id.toolbar);
        navBar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue_primary));


        pageCounterTextView = findViewById(R.id.pageCounterTextView);
        prevPageButton = findViewById(R.id.prevPageButton);
        nextPageButton = findViewById(R.id.nextPageButton);

        viewPager = findViewById(R.id.viewPager);


        viewPager.setUserInputEnabled(false);


        viewPager.setOffscreenPageLimit(3);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updatePageCounter(position);
            }
        });


        prevPageButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() > 0) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        });

        nextPageButton.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < pdfPages.size() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });


        loadPdf();
    }


    private void updatePageCounter(int currentPage) {
        if (pdfPages != null) {
            pageCounterTextView.setText(getString(R.string.page_counter,
                    currentPage + 1, pdfPages.size()));


            prevPageButton.setEnabled(currentPage > 0);
            nextPageButton.setEnabled(currentPage < pdfPages.size() - 1);
        }
    }

    private Bitmap scaleBitmapToScreen(Bitmap originalBitmap) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        float scaleWidth = ((float) screenWidth) / originalBitmap.getWidth();
        float scaleHeight = ((float) screenHeight) / originalBitmap.getHeight();
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        return Bitmap.createBitmap(
                originalBitmap,
                0, 0,
                originalBitmap.getWidth(),
                originalBitmap.getHeight(),
                matrix,
                true
        );
    }


    private void loadPdf() {

        executor.execute(() -> {
            try {
                File localFile = new File(getCacheDir(), "reglamento.pdf");
                try (InputStream inputStream = new URL(PDF_URL).openStream();
                     FileOutputStream outputStream = new FileOutputStream(localFile)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }

                runOnUiThread(() -> {
                    try {
                        parcelFileDescriptor = ParcelFileDescriptor.open(localFile, ParcelFileDescriptor.MODE_READ_ONLY);
                        pdfRenderer = new PdfRenderer(parcelFileDescriptor);

                        pdfPages = new ArrayList<>();
                        for (int i = 0; i < pdfRenderer.getPageCount(); i++) {
                            PdfRenderer.Page page = pdfRenderer.openPage(i);

                            Bitmap bitmap = Bitmap.createBitmap(
                                    page.getWidth(),
                                    page.getHeight(),
                                    Bitmap.Config.ARGB_8888
                            );

                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                            Bitmap scaledBitmap = scaleBitmapToScreen(bitmap);
                            pdfPages.add(scaledBitmap);

                            page.close();
                        }

                        PdfPageAdapter adapter = new PdfPageAdapter(this, pdfPages);
                        viewPager.setAdapter(adapter);

                        updatePageCounter(0);

                    } catch (Exception e) {
                        Log.e("Reglamento", "Error rendering PDF", e);
                        Toast.makeText(this, "Error loading PDF", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Log.e("Reglamento", "Error loading PDF", e);
                runOnUiThread(() ->
                        Toast.makeText(this, "Cargando PDF...", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    @Override
    protected void onDestroy() {
        try {
            if (pdfRenderer != null) {
                pdfRenderer.close();
            }
            if (parcelFileDescriptor != null) {
                parcelFileDescriptor.close();
            }
        } catch (Exception e) {
            Log.e("Reglamento", "Error closing PDF resources", e);
        }
        super.onDestroy();
    }

    private void zoomCurrentPage(float zoomFactor) {
        View currentView = viewPager.findViewWithTag(viewPager.getCurrentItem());
        if (currentView != null) {
            ZoomableImageView zoomableImageView = currentView.findViewById(R.id.pdfPageImageView);
            Matrix matrix = zoomableImageView.getImageMatrix();
            matrix.postScale(zoomFactor, zoomFactor,
                    zoomableImageView.getWidth() / 2f,
                    zoomableImageView.getHeight() / 2f);
            zoomableImageView.setImageMatrix(matrix);
        }
    }

    private void resetCurrentPageZoom() {
        View currentView = viewPager.findViewWithTag(viewPager.getCurrentItem());
        if (currentView != null) {
            ZoomableImageView zoomableImageView = currentView.findViewById(R.id.pdfPageImageView);
            zoomableImageView.resetZoom();
        }
    }
}