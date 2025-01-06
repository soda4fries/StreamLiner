package com.example.streamliner;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.streamliner.databinding.ActivityMainBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController navController;
    private BottomNavigationView bottomNav;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase if needed
        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation(savedInstanceState);
    }

    private void setupNavigation(Bundle savedInstanceState) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment == null) {
            Log.e(TAG, "NavHostFragment is null");
            return;
        }

        navController = navHostFragment.getNavController();
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);
        int startDestination = determineStartDestination();
        navGraph.setStartDestination(startDestination);
        navController.setGraph(navGraph);

        bottomNav = binding.navView;

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.forgetPassword,
                R.id.chatsFragment,
                R.id.navigation_time,
                R.id.learnHolder,
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


    private int determineStartDestination() {
        // Check if user is logged in
        if (auth.getCurrentUser() != null) {
            return R.id.learnHolder; // Or whatever your main screen is
        } else {
            return R.id.splashScreen1; // Or your first onboarding/login screen
        }
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
        if (destinationId == R.id.forgetPassword) {
            setTitle("Home");
        } else if (destinationId == R.id.chatsFragment) {
            setTitle("Chat");
        } else if (destinationId == R.id.navigation_time) {
            setTitle("Time");
        } else if (destinationId == R.id.mePage) {
            setTitle("Me");
        } else if (destinationId == R.id.learnHolder) {
            setTitle("Learn");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}