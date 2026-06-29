package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.databinding.FragmentLoginBinding;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> {
            String email    = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString();

            User user = null;

            if (email.equals("student@uni.edu") && password.equals("student123")) {
                user = new User("u_student", "Alice Student", email, UserRole.STUDENT, "");
            } else if (email.equals("teacher@uni.edu") && password.equals("teacher123")) {
                user = new User("u_teacher", "Dr. Sarah Hassan", email, UserRole.TEACHER, "Dr. Sarah Hassan");
            } else if (email.equals("admin@uni.edu") && password.equals("admin123")) {
                user = new User("u_admin", "Admin User", email, UserRole.ADMIN, "");
            }

            if (user != null) {
                SessionManager.getInstance().login(user);
                Snackbar.make(binding.getRoot(),
                        "Welcome " + user.getName() + " (" + user.getRole() + ")",
                        Snackbar.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Snackbar.make(binding.getRoot(),
                        "Invalid credentials", Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.btnBack.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}