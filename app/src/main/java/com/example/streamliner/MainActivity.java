package com.example.streamliner;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.streamliner.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        bottomNav = binding.navView;

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_time,
                R.id.chatsFragment,
                R.id.quizListFragment,
                R.id.mePage
        ).build();

        NavigationUI.setupWithNavController(bottomNav, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (isAuthenticationScreen(destination.getId()) || isOnboardingScreen(destination.getId())) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
                updateTitle(destination.getId());
            }
        });
    }

    private boolean isAuthenticationScreen(int destinationId) {
        return destinationId == R.id.loginPage ||
                destinationId == R.id.registerPage ||
                destinationId == R.id.forgetPassword ||
                destinationId == R.id.verifyEmailPage ||
                destinationId == R.id.resetPassword;
    }

    private boolean isOnboardingScreen(int destinationId) {
        return destinationId == R.id.splashScreen1 ||
                destinationId == R.id.splashScreen2 ||
                destinationId == R.id.splashScreen3 ||
                destinationId == R.id.splashScreen4;
    }

    private void updateTitle(int destinationId) {
        if (destinationId == R.id.navigation_time) {
            setTitle("Home");
        } else if (destinationId == R.id.chatsFragment) {
            setTitle("Chat");
        } else if (destinationId == R.id.quizListFragment) {
            setTitle("Quiz");
        } else if (destinationId == R.id.mePage) {
            setTitle("Me");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}