
package com.example.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CourseDetailDialog {

    public static void show(Context context, Course course) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_course_detail);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.88),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        );

        TextView tvName    = dialog.findViewById(R.id.dcName);
        TextView tvDoctor  = dialog.findViewById(R.id.dcDoctor);
        TextView tvCredits = dialog.findViewById(R.id.dcCredits);
        TextView tvDays    = dialog.findViewById(R.id.dcDays);
        TextView tvTime    = dialog.findViewById(R.id.dcTime);
        Button   btnOk     = dialog.findViewById(R.id.dcBtnOk);

        tvName.setText(course.getId());
        tvDoctor.setText("Dr. " + course.getDoctor());
        tvCredits.setText(course.getCredits() + " Credits");
        tvDays.setText(course.getDays());
        tvTime.setText(course.getTime());

        btnOk.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}