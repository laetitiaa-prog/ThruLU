package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;          // ✅ single import — android.widget.RatingBar only
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity3 extends AppCompatActivity {

    // ── UI refs ──────────────────────────────────────────────────────────────
    private RecyclerView recyclerReviews;
    private View layoutNotLoggedIn;
    private ExtendedFloatingActionButton btnSubmitReview;

    // ── Data & adapter ───────────────────────────────────────────────────────
    private InlineReviewAdapter adapter;
    private List<Review> allReviews = new ArrayList<>();
    private String activeFilter = "All";


    // ─────────────────────────────────────────────────────────────────────────
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reviews_main);

        bindViews();
        setupRecycler();
        setupChipFilter();
        setupSubmitSection();
        setupBottomNav();
    }

    // ── Bind views ────────────────────────────────────────────────────────────
    private void bindViews() {
        recyclerReviews   = findViewById(R.id.recyclerReviews);
        layoutNotLoggedIn = findViewById(R.id.layoutNotLoggedIn);
        btnSubmitReview   = findViewById(R.id.btnSubmitReview);
        recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
    }


    // ── RecyclerView ──────────────────────────────────────────────────────────
        private void setupRecycler() {
            allReviews = AppRepository.getInstance().getReviews();
            adapter = new InlineReviewAdapter(getFilteredReviews(), true, false, "");
            recyclerReviews.setLayoutManager(new LinearLayoutManager(this));
            recyclerReviews.setAdapter(adapter);
        }


    // ── Chip filter ───────────────────────────────────────────────────────────
    private void setupChipFilter() {
        int[]    chipIds = {
                R.id.chipAll, R.id.chipGeneral, R.id.chipAcademic,
                R.id.chipCampus, R.id.chipFaculty
        };
        String[] labels  = { "All", "General", "Academic", "Campus", "Faculty" };

        for (int i = 0; i < chipIds.length; i++) {
            final String label = labels[i];
            Chip chip = findViewById(chipIds[i]);
            if (chip != null) {
                chip.setOnClickListener(v -> {
                    activeFilter = label;
                    refreshList();
                });
            }
        }
    }

    private List<Review> getFilteredReviews() {
        if ("All".equals(activeFilter)) return new ArrayList<>(allReviews);
        return allReviews.stream()
                .filter(r -> activeFilter.equalsIgnoreCase(r.getCategory()))
                .collect(Collectors.toList());
    }

    private void refreshList() {
        allReviews = AppRepository.getInstance().getReviews();
        adapter.updateList(getFilteredReviews());
    }

    // ── Submit section ────────────────────────────────────────────────────────
    private void setupSubmitSection() {
        if (SessionManager.getInstance().isLoggedIn()) {
            layoutNotLoggedIn.setVisibility(View.GONE);
            btnSubmitReview.setVisibility(View.VISIBLE);
            btnSubmitReview.setOnClickListener(v -> showSubmitDialog());
            // Show bottom nav only for logged-in users
            findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
        } else {
            layoutNotLoggedIn.setVisibility(View.VISIBLE);
            btnSubmitReview.setVisibility(View.GONE);
            // Hide bottom nav and chips for guests
            findViewById(R.id.bottomNav).setVisibility(View.GONE);
            findViewById(R.id.hsvTabs).setVisibility(View.GONE);
            findViewById(R.id.btnLogin).setOnClickListener(v ->
                    startActivity(new Intent(this, MainActiviy8.class)));
        }
    }

    private void showSubmitDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_submit_review, null);

        android.widget.Spinner    spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        android.widget.Spinner    spinnerTeacher  = dialogView.findViewById(R.id.spinnerTeacher);
        android.widget.Spinner    spinnerCourse   = dialogView.findViewById(R.id.spinnerCourse);
        RatingBar                 ratingBar       = dialogView.findViewById(R.id.ratingBar); // ✅ android.widget.RatingBar
        TextInputEditText         etComment       = dialogView.findViewById(R.id.etReviewComment);

        // ── Categories ────────────────────────────────────────────────────────
        // ✅ Use a mutable list so ArrayAdapter works on all API levels
        List<String> categories = new ArrayList<>();
        categories.add("Academic");
        categories.add("General");
        categories.add("Campus");
        categories.add("Faculty");

        spinnerCategory.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, categories));

        // ── Teachers & courses from repository ────────────────────────────────
        LinkedHashSet<String> teacherSet = new LinkedHashSet<>();
        List<String>          courseIds  = new ArrayList<>();
        for (Course c : AppRepository.getInstance().getCourses()) {
            teacherSet.add(c.getDoctor());
            courseIds.add(c.getId());
        }

        spinnerTeacher.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item,
                new ArrayList<>(teacherSet)));

        spinnerCourse.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_dropdown_item, courseIds));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Submit a Review")
                .setView(dialogView)
                .setPositiveButton("Submit", (d, w) -> {

                    // ── Validate comment ──────────────────────────────────────
                    String comment = (etComment.getText() != null)
                            ? etComment.getText().toString().trim() : "";
                    if (comment.isEmpty()) {
                        Snackbar.make(recyclerReviews,
                                "Please write a comment.", Snackbar.LENGTH_SHORT).show();
                        return;
                    }

                    // ✅ Safe category read — null-checked, never crashes
                    String category = (spinnerCategory.getSelectedItem() != null)
                            ? spinnerCategory.getSelectedItem().toString() : "General";

                    // ✅ getRating() on android.widget.RatingBar — correct type
                    float rating = ratingBar.getRating();

                    // ✅ Safe teacher / course reads
                    String teacherName = (spinnerTeacher.getSelectedItem() != null)
                            ? spinnerTeacher.getSelectedItem().toString() : "";
                    String courseName  = (spinnerCourse.getSelectedItem() != null)
                            ? spinnerCourse.getSelectedItem().toString() : "";

                    Review review = new Review(
                            AppRepository.getInstance().newId(),
                            SessionManager.getInstance().getCurrentUser().getId(),
                            SessionManager.getInstance().getCurrentUser().getName(),
                            teacherName,
                            courseName,
                            rating,
                            comment,
                            false,
                            category
                    );

                    AppRepository.getInstance().addReview(review);
                    refreshList();
                    Snackbar.make(recyclerReviews,
                            "Review submitted!", Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ── Bottom nav ────────────────────────────────────────────────────────────
    private void setupBottomNav() {
        findViewById(R.id.events).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity.class)));
        findViewById(R.id.courses).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity3.class)));
        findViewById(R.id.campusmap).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity6.class)));
        findViewById(R.id.infoBot).setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity5.class)));
        // navReview is current screen — no action needed
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Inner Adapter
    // ─────────────────────────────────────────────────────────────────────────
    private class InlineReviewAdapter
            extends RecyclerView.Adapter<InlineReviewAdapter.ReviewViewHolder> {

        private List<Review> reviews;
        private final boolean isAdmin;
        private final boolean isTeacher;
        private final String  myTeacherName;

        InlineReviewAdapter(List<Review> reviews,
                            boolean isAdmin,
                            boolean isTeacher,
                            String  myTeacherName) {
            this.reviews       = reviews;
            this.isAdmin       = isAdmin;
            this.isTeacher     = isTeacher;
            this.myTeacherName = myTeacherName;
        }

        void updateList(List<Review> newList) {
            this.reviews = newList;
            notifyDataSetChanged();
        }

        @Override
        public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.reviews_item, parent, false);
            return new ReviewViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewViewHolder h, int position) {
            Review review = reviews.get(position);

            h.tvDoctorName.setText(review.getTeacherName());
            h.tvCourseName.setText(review.getCourseName());

            // ✅ tvAuthorName — guard against null author name
            String author = review.getAuthorName();
            h.tvAuthorName.setText("by " + (author != null ? author : "Anonymous"));

            // ✅ tvCategory — guard against null category
            String cat = review.getCategory();
            h.tvCategory.setText(cat != null ? cat : "");

            // ✅ ratingBar — android.widget.RatingBar, setRating() is correct
            h.ratingBar.setRating(review.getRating());

            h.txtReview.setText(review.getComment());

            // Hidden badge
            h.tvHiddenBadge.setVisibility(review.isHidden() ? View.VISIBLE : View.GONE);
            h.itemView.setAlpha(review.isHidden() ? 0.5f : 1.0f);

            // Moderation
            boolean canModerate = isAdmin
                    || (isTeacher && myTeacherName.equalsIgnoreCase(review.getTeacherName()));

            if (canModerate) {
                h.btnHideReview.setVisibility(View.VISIBLE);
                h.btnHideReview.setText(review.isHidden() ? "Unhide" : "Hide");
                h.btnHideReview.setOnClickListener(v -> {
                    AppRepository.getInstance()
                            .setReviewHidden(review.getId(), !review.isHidden());
                    refreshList();
                });

                h.btnDeleteReview.setVisibility(View.VISIBLE);
                h.btnDeleteReview.setOnClickListener(v ->
                        new MaterialAlertDialogBuilder(MainActivity3.this)
                                .setTitle("Delete Review")
                                .setMessage("Permanently delete this review by "
                                        + review.getAuthorName() + "?")
                                .setPositiveButton("Delete", (d, w) -> {
                                    AppRepository.getInstance().deleteReview(review.getId());
                                    allReviews = AppRepository.getInstance().getReviews();
                                    refreshList();
                                })
                                .setNegativeButton("Cancel", null)
                                .show());
            } else {
                h.btnHideReview.setVisibility(View.GONE);
                h.btnDeleteReview.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() { return reviews != null ? reviews.size() : 0; }

        // ── ViewHolder ────────────────────────────────────────────────────────
        class ReviewViewHolder extends RecyclerView.ViewHolder {
            TextView  tvDoctorName, tvCourseName, tvAuthorName,
                    tvCategory, txtReview, tvHiddenBadge;
            RatingBar ratingBar;                          // ✅ android.widget.RatingBar
            android.widget.Button btnHideReview, btnDeleteReview;

            ReviewViewHolder(@NonNull View itemView) {
                super(itemView);
                tvDoctorName    = itemView.findViewById(R.id.tvDoctorName);
                tvCourseName    = itemView.findViewById(R.id.tvCourseName);
                tvAuthorName    = itemView.findViewById(R.id.tvAuthorName);   // ✅ confirmed binding
                tvCategory      = itemView.findViewById(R.id.tvCategory);     // ✅ confirmed binding
                ratingBar       = itemView.findViewById(R.id.ratingBar);      // ✅ android.widget.RatingBar
                txtReview       = itemView.findViewById(R.id.txtReview);
                tvHiddenBadge   = itemView.findViewById(R.id.tvHiddenBadge);
                btnHideReview   = itemView.findViewById(R.id.btnHideReview);
                btnDeleteReview = itemView.findViewById(R.id.btnDeleteReview);
            }
        }
    }
}