package com.example.myhouse;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PdfPageViewHolder> {
    private Context context;
    private List<Bitmap> pdfPages;

    public PdfPageAdapter(Context context, List<Bitmap> pdfPages) {
        this.context = context;
        this.pdfPages = pdfPages;
    }

    @NonNull
    @Override
    public PdfPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pdf_page, parent, false);
        return new PdfPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfPageViewHolder holder, int position) {
        Bitmap pageBitmap = pdfPages.get(position);
        holder.itemView.setTag(position);

        holder.zoomableImageView.setImageBitmap(pageBitmap);
    }

    @Override
    public int getItemCount() {
        return pdfPages.size();
    }

    static class PdfPageViewHolder extends RecyclerView.ViewHolder {
        ZoomableImageView zoomableImageView;

        public PdfPageViewHolder(@NonNull  View itemView) {
            super(itemView);
            zoomableImageView = itemView.findViewById(R.id.pdfPageImageView);
        }
    }
}