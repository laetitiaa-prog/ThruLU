package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    public interface OnHideToggle { void onToggle(Review review); }
    public interface OnDelete     { void onDelete(Review review); }

    private List<Review>       reviewList;
    private final boolean      isAdmin;
    private final boolean      isTeacher;
    private final String       currentTeacherName;
    private final OnHideToggle onHideToggle;
    private final OnDelete     onDelete;

    public ReviewAdapter(List<Review> reviewList,
                         boolean isAdmin,
                         boolean isTeacher,
                         String currentTeacherName,
                         OnHideToggle onHideToggle,
                         OnDelete onDelete) {
        this.reviewList         = reviewList;
        this.isAdmin            = isAdmin;
        this.isTeacher          = isTeacher;
        this.currentTeacherName = currentTeacherName;
        this.onHideToggle       = onHideToggle;
        this.onDelete           = onDelete;
    }

    public void updateList(List<Review> newList) {
        this.reviewList = newList;
        notifyDataSetChanged();
    }

    private List<Review> getVisibleReviews() {
        List<Review> visible = new ArrayList<>();
        for (Review r : reviewList) {
            String teacherName = r.getTeacherName();
            boolean isOwner = teacherName != null &&
                    teacherName.equalsIgnoreCase(currentTeacherName);

            if ((isAdmin || isTeacher) && isOwner) {
                visible.add(r);  // sees hidden reviews for their own name
            } else if (!r.isHidden()) {
                visible.add(r);  // everyone else sees only visible reviews
            }
        }
        return visible;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review r = getVisibleReviews().get(position);

        // ── Doctor name ───────────────────────────────────────────────────────
        String teacherName = r.getTeacherName();
        if (teacherName == null || teacherName.isEmpty()) {
            holder.tvDoctorName.setVisibility(View.GONE);
        } else {
            holder.tvDoctorName.setVisibility(View.VISIBLE);
            holder.tvDoctorName.setText(teacherName);
        }

        // ── Course name ───────────────────────────────────────────────────────
        String courseName = r.getCourseName();
        if (courseName == null || courseName.isEmpty()) {
            holder.tvCourseName.setVisibility(View.GONE);
        } else {
            holder.tvCourseName.setVisibility(View.VISIBLE);
            holder.tvCourseName.setText(courseName);
        }

        // ── Author name ✅ ────────────────────────────────────────────────────
        String authorName = r.getAuthorName();
        if (authorName == null || authorName.isEmpty()) {
            holder.tvAuthorName.setVisibility(View.GONE);
        } else {
            holder.tvAuthorName.setVisibility(View.VISIBLE);
            holder.tvAuthorName.setText("by " + authorName);
        }

        // ── Category ✅ ───────────────────────────────────────────────────────
        String category = r.getCategory();
        if (category == null || category.isEmpty()) {
            holder.tvCategory.setVisibility(View.GONE);
        } else {
            holder.tvCategory.setVisibility(View.VISIBLE);
            holder.tvCategory.setText(category);
        }

        // ── Rating & comment ──────────────────────────────────────────────────
        holder.ratingBar.setRating(r.getRating());
        holder.txtReview.setText(r.getComment());

        // ── Hidden badge ──────────────────────────────────────────────────────
        if (holder.tvHiddenBadge != null) {
            holder.tvHiddenBadge.setVisibility(r.isHidden() ? View.VISIBLE : View.GONE);
        }

        // ── Dim hidden items for non-moderators ───────────────────────────────
        holder.itemView.setAlpha(r.isHidden() ? 0.5f : 1.0f);

        // ── Moderation buttons ────────────────────────────────────────────────
        String reviewteacher = r.getTeacherName();
        boolean isOwner = teacherName != null &&
                teacherName.equalsIgnoreCase(currentTeacherName);

        boolean canModerate = isAdmin || (isTeacher && isOwner);
        if (holder.btnHideReview != null && holder.btnDeleteReview != null) {
            if (canModerate) {
                holder.btnHideReview.setVisibility(View.VISIBLE);
                holder.btnDeleteReview.setVisibility(View.VISIBLE);
                holder.btnHideReview.setText(r.isHidden() ? "Unhide" : "Hide");
                holder.btnHideReview.setOnClickListener(v -> onHideToggle.onToggle(r));
                holder.btnDeleteReview.setOnClickListener(v -> onDelete.onDelete(r));
            } else {
                holder.btnHideReview.setVisibility(View.GONE);
                holder.btnDeleteReview.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getVisibleReviews().size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────
    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView  tvDoctorName, tvCourseName, tvAuthorName,  // ✅ tvAuthorName
                tvCategory,                                 // ✅ tvCategory
                txtReview, tvHiddenBadge;
        RatingBar ratingBar;
        android.widget.Button btnHideReview, btnDeleteReview;

        ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName    = itemView.findViewById(R.id.tvDoctorName);
            tvCourseName    = itemView.findViewById(R.id.tvCourseName);
            tvAuthorName    = itemView.findViewById(R.id.tvAuthorName);    // ✅
            tvCategory      = itemView.findViewById(R.id.tvCategory);      // ✅
            ratingBar       = itemView.findViewById(R.id.ratingBar);
            txtReview       = itemView.findViewById(R.id.txtReview);
            tvHiddenBadge   = itemView.findViewById(R.id.tvHiddenBadge);
            btnHideReview   = itemView.findViewById(R.id.btnHideReview);
            btnDeleteReview = itemView.findViewById(R.id.btnDeleteReview);
        }
    }
}